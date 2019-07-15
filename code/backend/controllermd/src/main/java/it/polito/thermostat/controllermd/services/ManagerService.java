package it.polito.thermostat.controllermd.services;

import it.polito.thermostat.controllermd.entity.*;
import it.polito.thermostat.controllermd.entity.Program;
import it.polito.thermostat.controllermd.repository.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ManagerService {
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Value("#{T(java.lang.Double).parseDouble('${scaling.factor}')}")
    Double scalingFactor;

    @Value("#{T(java.lang.Double).parseDouble('${temperature.buffer}')}")
    Double temperatureBuffer;

    @Autowired
    StatService statService;


    @Autowired
    MQTTservice mqttService;

    @Autowired
    TemperatureService temperatureService;

    @Autowired
    ProgramRepository programRepository;

    @Autowired
    RoomRepository roomRepository;

    @Autowired
    WSALRepository wsalRepository;

    @Autowired
    SensorDataRepository sensorDataRepository;

    @Autowired
    ESP8266Repository esp8266Repository;

    @Autowired
    SettingService settingService;

    /**
     * Scheduled task that manage the temperature for each room
     * we check that there is at the first wsal to start working
     */
    @Scheduled(fixedRate = 3000, initialDelay = 4000)
    public void scheduleFixedRateTask() {
        Optional<WSAL> checkWSAL = wsalRepository.findById("mainwsal");
        if (checkWSAL.isPresent()) {
            WSAL currentWSAL = checkWSAL.get();
            List<Room> roomList = ((List<Room>) roomRepository.findAll());
            if (currentWSAL.getIsLeave()) {
                logger.info("Leave mode");
                roomList.forEach(room -> manageLeave(room, currentWSAL));
                return;
            } else if (currentWSAL.getIsAntiFreeze()) {
                logger.info("AntiFreeze mode");
                roomList.forEach(room -> manageESP(room, currentWSAL.getAntiFreezeTemperature(), false, true));

                return;
            }
            roomList.stream().forEach(room -> manageRoom(room, currentWSAL.getIsSummer()));
        }
    }

    private void manageLeave(Room room, WSAL currentWSAL) {
        LocalDateTime leaveEnd = currentWSAL.getLeaveEnd();
        LocalDateTime now = LocalDateTime.now();
        Double leaveBackTemperature = findNearestTimeSlot(currentWSAL.getLeaveEnd(), findProgramById(room.getIdRoom())).getTemperature();
        Double temperatureDiff = Math.abs(currentWSAL.getLeaveTemperature() - leaveBackTemperature);


        //It's time to turn on the system
        if (now.isAfter(leaveEnd.plus((long) (60 * temperatureDiff * scalingFactor), ChronoUnit.MINUTES))) {
            manageESP(room, leaveBackTemperature, currentWSAL.getIsSummer(), true);
        } else {
            //waiting at leave temperature
            manageESP(room, currentWSAL.getLeaveTemperature(), currentWSAL.getIsSummer(), true);
        }
    }

    private void manageRoom(Room room, Boolean isSummer) {
        if (room.getIsManual()) {
            logger.info(room.getIdRoom() + "isManual");
            manageESP(room, room.getDesiredTemperature(), isSummer, false);
        } else {
            logger.info(room.getIdRoom() + "isProgramed");
            Program programRoom = findProgramById(room.getIdRoom());

            Program.HourlyProgram hourlyProgram = findNearestTimeSlot(LocalDateTime.now(), programRoom);
            room.setDesiredTemperature(hourlyProgram.getTemperature());
            roomRepository.save(room);
            manageESP(room, room.getDesiredTemperature(), isSummer, true);
        }
    }


    /**
     * @param programRoom
     * @return the nearest time slot to @param when
     */
    public Program.HourlyProgram findNearestTimeSlot(LocalDateTime when, Program programRoom) {
        Program.DailyProgram dailyProgram;
        if (when.getDayOfWeek().getValue() <= 5)//lunedi - venerdì
            dailyProgram = programRoom.getWeeklyList().get(0);
        else
            dailyProgram = programRoom.getWeeklyList().get(1);

        Optional<Program.HourlyProgram> checkHourlyProgram = dailyProgram.getDailyMap().values().stream()
                .filter(hourlyProgram -> hourlyProgram.getTime().isAfter(when.toLocalTime()))
                .min(Comparator.comparing(Program.HourlyProgram::getTime));

        return checkHourlyProgram.orElseGet(() -> dailyProgram.getDailyMap().get("wake"));

    }

    private void manageESP(Room room, Double desiredTemperature, Boolean isSummer, Boolean isProgrammed) {
        if (!room.getEsp8266List().isEmpty()) {
            int deleteBuffer = isProgrammed ? 1 : 0; //if manual delete buffer

            List<ESP8266> esp8266List = room.getEsp8266List().stream().map(idEsp -> esp8266Repository.findById(idEsp).orElse(null)).collect(Collectors.toList());//.filter(esp8266 -> esp8266.getIsCooler().equals(isSummer) && !esp8266.getIsSensor()).forEach(esp8266 -> logger.info(esp8266.toString()));

            List<ESP8266> esp8266ListActuator = esp8266List.stream().filter(esp8266 -> !esp8266.getIsSensor()).collect(Collectors.toList());
            Optional<String> checkString = esp8266List.stream().filter(ESP8266::getIsSensor).map(ESP8266::getIdEsp).findFirst();
            String idSensor;
            if (checkString.isPresent())
                idSensor = checkString.get();
            else
                throw new IllegalArgumentException("Room must have a sensor");

            esp8266ListActuator.stream().forEach(esp8266 -> {
                Optional<SensorData> sensorDataCheck = sensorDataRepository.findById(idSensor);
                if (sensorDataCheck.isPresent()) {
                    SensorData sensorData = sensorDataCheck.get();
                    CommandActuator commandActuator;
                    if (esp8266.getIsCooler().equals(isSummer)) {
                        if (sensorData.getTemperature() < (desiredTemperature - (temperatureBuffer * deleteBuffer)))
                            commandActuator = new CommandActuator(esp8266.getIdEsp(), !isSummer);
                        else
                            commandActuator = new CommandActuator(esp8266.getIdEsp(), isSummer);
                    } else {
                        // turn off the heater in summer&viceversa
                        commandActuator = new CommandActuator(esp8266.getIdEsp(), false);
                    }
                    mqttService.manageActuator(commandActuator);
                    statService.handleNewCommand(room.getIdRoom(), commandActuator);
                }
            });
        }
    }


    private Program findProgramById(String idRoom) {
        Optional<Program> check = programRepository.findById(idRoom);

        if (check.isPresent())
            return check.get();
        else
            return settingService.getDefaultProgram();

    }

}
