package it.polito.thermostat.wifi.services;

import it.polito.thermostat.wifi.configuration.MongoZonedDateTime;
import it.polito.thermostat.wifi.entity.Room;
import it.polito.thermostat.wifi.entity.WSAL;
import it.polito.thermostat.wifi.entity.program.Program;
import it.polito.thermostat.wifi.repository.ESP8266Repository;
import it.polito.thermostat.wifi.repository.ProgramRepository;
import it.polito.thermostat.wifi.repository.RoomRepository;
import it.polito.thermostat.wifi.repository.WSALRepository;
import it.polito.thermostat.wifi.resources.LeaveResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalTime;
import java.util.Date;
import java.util.List;

@Service
public class TemperatureService {
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    ESP8266Repository esp8266Repository;

    @Autowired
    RoomRepository roomRepository;

    @Autowired
    ProgramRepository programRepository;

    @Autowired
    WSALRepository wsalRepository;

    @Autowired
    MQTTservice mqttService;


    public void setManualRoom(String idRoom, Double desiredTemperature) {
        Room room = roomRepository.findByIdRoom(idRoom).get();
        room.setIsManual(true);
        room.setDesiredTemperature(desiredTemperature);
        roomRepository.save(room);
    }


    /**
     * Memorize in the db if we are in WinterSummerAntifreeze
     * if antifreeze activate the actuator accordingly
     *
     * @param wsa
     */
    public void setWSA(String wsa) {
        WSAL wsal = wsalRepository.findAll().get(0);
        switch (wsa) {
            case "winter":
                wsal.setIsSummer(false);
                break;
            case "summer":
                wsal.setIsSummer(true);
                wsal.setIsAntiFreeze(false);
                break;
            case "antifreeze":
                wsal.setIsAntiFreeze(true);
                wsal.setIsSummer(false);
                break;
            default:
                logger.error("setWsa string not recognised");
        }
        wsalRepository.save(wsal);
    }

    /**
     * Set the actuator accordingly to the leave time
     * @param leaveResource
     */
    public void setL(LeaveResource leaveResource) {
        WSAL wsal = wsalRepository.findAll().get(0);
        wsal.setIsAntiFreeze(false);
        wsal.setIsLeave(true);
        wsal.setLeaveTemperature(leaveResource.getLeaveTemperature());
        wsal.setLeaveBackTemperature(leaveResource.getLeaveBackTemperature());
        wsal.setLeaveEnd(MongoZonedDateTime.getMongoZonedDateTimeFromDate(leaveResource.getLeaveEnd()));
        wsalRepository.save(wsal);
    }


    /**
     * Set the esp related to the room to program/manual mode
     *
     * @param idRoom
     */
    public void setIsProgrammedRoom(String idRoom) {
        Room room = roomRepository.findByIdRoom(idRoom).get();
        room.setIsManual(false);
        roomRepository.save(room);
    }


    /**
     * @param programList
     */
    public void saveProgramList(List<Program> programList) {
        programRepository.saveAll(programList);
    }
}
