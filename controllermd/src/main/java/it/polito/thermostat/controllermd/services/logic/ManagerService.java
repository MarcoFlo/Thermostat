package it.polito.thermostat.controllermd.services.logic;

import it.polito.thermostat.controllermd.entity.ESP8266;
import it.polito.thermostat.controllermd.entity.program.Program;
import it.polito.thermostat.controllermd.entity.Room;
import it.polito.thermostat.controllermd.entity.WSAL;
import it.polito.thermostat.controllermd.entity.program.DailyProgram;
import it.polito.thermostat.controllermd.entity.program.HourlyProgram;
import it.polito.thermostat.controllermd.object.SensorData;
import it.polito.thermostat.controllermd.repository.ProgramRepository;
import it.polito.thermostat.controllermd.repository.RoomRepository;
import it.polito.thermostat.controllermd.repository.WSALRepository;
import it.polito.thermostat.controllermd.services.server.TemperatureService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
public class ManagerService {
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Value("#{T(java.lang.Double).parseDouble('${scaling.factor}')}")
    Double scalingFactor;

    @Value("#{T(java.lang.Double).parseDouble('${temperature.buffer}')}")
    Double temperatureBuffer;


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

    //The key is idESP
    @Autowired
    ConcurrentHashMap<String, SensorData> mapSensorData;


    @Scheduled(fixedRate = 1000)
    public void scheduleFixedRateTask() {
        List<WSAL> checkWSAL = StreamSupport.stream(wsalRepository.findAll().spliterator(), false).collect(Collectors.toList());
        if (!checkWSAL.isEmpty()) //controlliamo che ci sia almneo una config
        {
            WSAL currentWSAL = checkWSAL.get(0);
            List<Room> roomList = StreamSupport.stream(roomRepository.findAll().spliterator(), false).collect(Collectors.toList());
            if (currentWSAL.getIsLeave()) {
                logger.info("Leave mode");
                manageLeave(roomList.stream().flatMap(room -> room.getEsp8266List().stream()).collect(Collectors.toList()), currentWSAL);
                return;
            } else if (currentWSAL.getIsAntiFreeze()) {
                logger.info("AntiFreeze mode");
                manageESP(roomList.stream().flatMap(room -> room.getEsp8266List().stream()).collect(Collectors.toList())
                        , currentWSAL.getAntiFreezeTemperature(), false, true);
                return;
            }
            roomList.stream().forEach(room -> manageRoom(room, currentWSAL.getIsSummer()));
        }
    }

    private void manageLeave(List<ESP8266> esp8266List, WSAL currentWSAL) {
        LocalDateTime leaveEnd = currentWSAL.getLeaveEnd();
        LocalDateTime now = LocalDateTime.now();
        Double temperatureDiff = Math.abs(currentWSAL.getLeaveTemperature() - currentWSAL.getLeaveBackTemperature());

        //It's time to turn on the system
        if (now.isAfter(leaveEnd.plus((long) (60 * temperatureDiff * scalingFactor), ChronoUnit.MINUTES))) {
            manageESP(esp8266List, currentWSAL.getLeaveBackTemperature(), currentWSAL.getIsSummer(), true);
        } else //waiting at leaveTemperature
        {
            manageESP(esp8266List, currentWSAL.getLeaveTemperature(), currentWSAL.getIsSummer(), true);

        }
    }

    private void manageRoom(Room room, Boolean isSummer) {
        if (room.getIsManual()) {
            logger.info(room.getIdRoom() + "isManual");
            manageESP(room.getEsp8266List(), room.getDesiredTemperature(), isSummer, false);
        } else {
            logger.info(room.getIdRoom() + "isProgramed");
            Optional<Program> check = programRepository.findById(room.getIdRoom());

            //Shouldn't be possible, but if we try to set a room without a program, programmed the system uses the default one
            Program programRoom;
            if (check.isPresent())
                programRoom = check.get();
            else
                programRoom = temperatureService.getDefaultProgram();

            manageProgram(room.getEsp8266List(), programRoom, isSummer);
        }
    }


    private void manageProgram(List<ESP8266> esp8266List, Program programRoom, Boolean isSummer) {
        HourlyProgram hourlyProgram = findNearestTimeSlot(programRoom);
        manageESP(esp8266List, hourlyProgram.getTemperature(), isSummer, true);
    }

    private HourlyProgram findNearestTimeSlot(Program programRoom) {
        DailyProgram dailyProgram;
        if (LocalDate.now().getDayOfWeek().getValue() <= 5)//lunedi - venerdì
            dailyProgram = programRoom.getWeeklyMap().get(1);
        else
            dailyProgram = programRoom.getWeeklyMap().get(2);

        HourlyProgram programResult = dailyProgram.getDailyMap().values().stream()
                .filter(hourlyProgram -> hourlyProgram.getTime().isAfter(LocalTime.now()))
                .min(Comparator.comparing(o -> o.getTime()))
                .get();
        return programResult;
    }

    private void manageESP(List<ESP8266> esp8266List, Double desiredTemperature, Boolean isSummer, Boolean isProgrammed) {
        int deleteBuffer = isProgrammed ? 1 : 0; //if manual delete buffer
        List<ESP8266> esp8266ListSeason = esp8266List.stream().filter(esp8266 -> esp8266.getIsCooler().equals(isSummer)).collect(Collectors.toList());

        esp8266ListSeason.stream().forEach(esp8266 -> {
            SensorData sensorData = mapSensorData.get(esp8266.getIdEsp());
            if (sensorData.getTemperature() < (desiredTemperature - (temperatureBuffer * deleteBuffer)))
                mqttService.manageActuator(esp8266.getIdEsp(), !isSummer);
            else
                mqttService.manageActuator(esp8266.getIdEsp(), isSummer);
        });
    }
}
