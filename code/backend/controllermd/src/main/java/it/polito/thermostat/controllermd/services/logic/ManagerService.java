package it.polito.thermostat.controllermd.services.logic;

import it.polito.thermostat.controllermd.entity.*;
import it.polito.thermostat.controllermd.entity.Program;
import it.polito.thermostat.controllermd.repository.*;
import it.polito.thermostat.controllermd.services.server.SettingService;
import it.polito.thermostat.controllermd.services.server.TemperatureService;
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




    @Scheduled(fixedRate = 1000)
    public void scheduleFixedRateTask() {
        List<WSAL> checkWSAL = ((List<WSAL>) wsalRepository.findAll());
        if (!checkWSAL.isEmpty()) //controlliamo che ci sia almneo una config
        {
            WSAL currentWSAL = checkWSAL.get(0);
            List<Room> roomList = ((List<Room>)roomRepository.findAll());
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
            //waiting at leave beack temperature
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

            Program.HourlyProgram hourlyProgram = findNearestTimeSlot(LocalDateTime.now(),programRoom);
            room.setDesiredTemperature(hourlyProgram.getTemperature());
            roomRepository.save(room);
            manageESP(room, room.getDesiredTemperature(), isSummer, true);
        }
    }


    private Program.HourlyProgram findNearestTimeSlot(LocalDateTime when, Program programRoom) {
        Program.DailyProgram dailyProgram;
        if (when.getDayOfWeek().getValue() <= 5)//lunedi - venerdì
            dailyProgram = programRoom.getWeeklyList().get(1);
        else
            dailyProgram = programRoom.getWeeklyList().get(2);

        Program.HourlyProgram programResult = dailyProgram.getDailyMap().values().stream()
                .filter(hourlyProgram -> hourlyProgram.getTime().isAfter(when.toLocalTime()))
                .min(Comparator.comparing(o -> o.getTime()))
                .get();
        return programResult;
    }

    private void manageESP(Room room, Double desiredTemperature, Boolean isSummer, Boolean isProgrammed) {
        if (!room.getEsp8266List().isEmpty()) {
            int deleteBuffer = isProgrammed ? 1 : 0; //if manual delete buffer
            List<String> esp8266ListSeason = room.getEsp8266List().stream().filter(esp8266 -> esp8266Repository.findById(esp8266).get().getIsCooler().equals(isSummer)).collect(Collectors.toList());

            esp8266ListSeason.stream().forEach(idEsp -> {
                Optional<SensorData> sensorDataCheck = sensorDataRepository.findById(idEsp);
                if (sensorDataCheck.isPresent()) {
                    SensorData sensorData = sensorDataCheck.get();
                    CommandActuator commandActuator;
                    if (sensorData.getTemperature() < (desiredTemperature - (temperatureBuffer * deleteBuffer)))
                        commandActuator = new CommandActuator(idEsp, !isSummer);
                    else
                        commandActuator = new CommandActuator(idEsp, isSummer);
                    mqttService.manageActuator(commandActuator);
                    statService.handleNewCommand(room.getIdRoom(),commandActuator);
                }
            });
        }
    }


    private Program findProgramById(String idRoom)
    {
        Optional<Program> check = programRepository.findById(idRoom);

        if (check.isPresent())
            return check.get();
        else
            return settingService.getDefaultProgram();

    }

}
