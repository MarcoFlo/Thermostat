package it.polito.thermostat.wifi.services;

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

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Date;
import java.util.List;
import java.util.Properties;

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


    /**
     * Set the room in manual mode
     *
     * @param idRoom
     * @param desiredTemperature
     */
    public void setManualRoom(String idRoom, Double desiredTemperature) {
        Room room = roomRepository.findById(idRoom).get();
        room.setIsManual(true);
        room.setDesiredTemperature(desiredTemperature);
        roomRepository.save(room);
    }

    /**
     * Set the the room to program mode
     *
     * @param idRoom
     */
    public void setIsProgrammedRoom(String idRoom) {
        Room room = roomRepository.findById(idRoom).get();
        room.setIsManual(false);
        roomRepository.save(room);
    }

    /**
     * Memorize in the db if we are in WinterSummerAntifreeze
     *
     * @param wsa
     */
    public void setWSA(String wsa) {
        WSAL wsal;
        if (wsalRepository.findAll().iterator().hasNext())
            wsal = wsalRepository.findAll().iterator().next();
        else
            wsal = new WSAL();

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
        wsal.setCreationDate(LocalDateTime.now());
        wsalRepository.save(wsal);
    }

    /**
     * Memorize into the db the leave details
     *
     * @param leaveResource
     */
    public void setL(LeaveResource leaveResource) {
        WSAL wsal;
        if (wsalRepository.findAll().iterator().hasNext())
            wsal = wsalRepository.findAll().iterator().next();
        else
            wsal = new WSAL();

        wsal.setIsAntiFreeze(false);
        wsal.setIsLeave(true);
        wsal.setLeaveTemperature(leaveResource.getLeaveTemperature());
        wsal.setLeaveBackTemperature(leaveResource.getLeaveBackTemperature());
        wsal.setLeaveEnd(leaveResource.getLeaveEnd());
        wsal.setCreationDate(LocalDateTime.now());
        wsalRepository.save(wsal);
    }


    /**
     * Save the program related to a room into the db
     *
     * @param program
     */
    public void saveProgram(Program program) {
        programRepository.save(program);
    }


    public Program getDefaultProgram() {
        switch (LocalDateTime.now().getMonth()) {
            case APRIL:
            case MAY:
            case JUNE:
            case JULY:
            case AUGUST:
            case SEPTEMBER:
            case OCTOBER:
                //summer
                return programRepository.findById("summer").get();

            case NOVEMBER:
            case DECEMBER:
            case JANUARY:
            case FEBRUARY:
            case MARCH:
                //winter
                return programRepository.findById("winter").get();
            default:
                return null;
        }
    }
}
