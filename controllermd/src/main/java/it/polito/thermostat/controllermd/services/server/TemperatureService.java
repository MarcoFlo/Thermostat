package it.polito.thermostat.controllermd.services.server;

import it.polito.thermostat.controllermd.configuration.exception.ProgramNotExistException;
import it.polito.thermostat.controllermd.configuration.exception.RoomNotExistException;
import it.polito.thermostat.controllermd.entity.Room;
import it.polito.thermostat.controllermd.entity.WSAL;
import it.polito.thermostat.controllermd.entity.program.Program;
import it.polito.thermostat.controllermd.repository.ESP8266Repository;
import it.polito.thermostat.controllermd.repository.ProgramRepository;
import it.polito.thermostat.controllermd.repository.RoomRepository;
import it.polito.thermostat.controllermd.repository.WSALRepository;
import it.polito.thermostat.controllermd.resources.LeaveResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

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


    /**
     * Set the room in manual mode
     *
     * @param idRoom             room to set
     * @param desiredTemperature temperature to set
     */
    public void setManualRoom(String idRoom, Double desiredTemperature) {
        Room room = checkRoom(idRoom);
        room.setIsManual(true);
        room.setDesiredTemperature(desiredTemperature);
        roomRepository.save(room);
    }

    /**
     * Set the the room to program mode
     *
     * @param idRoom room to program
     */
    public void setIsProgrammedRoom(String idRoom) {
        Room room = checkRoom(idRoom);
        room.setIsManual(false);
        roomRepository.save(room);
    }

    /**
     * Memorize in the db if we are in WinterSummerAntifreeze
     *
     * @param wsa string "winter", ecc
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
     * @param leaveResource leave details
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
     * @param program program to save
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
                return programRepository.findById("summer").get();

            case NOVEMBER:
            case DECEMBER:
            case JANUARY:
            case FEBRUARY:
            case MARCH:
                return programRepository.findById("winter").get();

            default:
                return null;
        }
    }

    public Program getProgramRoom(String idRoom) {
        Optional<Program> check = programRepository.findById(idRoom);
        if (!check.isPresent())
            throw new ProgramNotExistException("getProgramRoom");
        return check.get();
    }

    private Room checkRoom(String idRoom) {
        Optional<Room> check = roomRepository.findById(idRoom);
        if (!check.isPresent())
            throw new RoomNotExistException("checkRoom");
        return check.get();
    }
}
