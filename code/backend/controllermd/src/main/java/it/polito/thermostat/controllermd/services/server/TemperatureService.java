package it.polito.thermostat.controllermd.services.server;

import it.polito.thermostat.controllermd.configuration.exception.RoomNotExistException;
import it.polito.thermostat.controllermd.entity.Room;
import it.polito.thermostat.controllermd.entity.WSAL;
import it.polito.thermostat.controllermd.repository.ESP8266Repository;
import it.polito.thermostat.controllermd.repository.ProgramRepository;
import it.polito.thermostat.controllermd.repository.RoomRepository;
import it.polito.thermostat.controllermd.repository.WSALRepository;
import it.polito.thermostat.controllermd.resources.CurrentRoomStateResource;
import it.polito.thermostat.controllermd.resources.LeaveResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Iterator;
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
        Iterator<WSAL> wsalIterator = wsalRepository.findAll().iterator();
        WSAL wsal;
        if (wsalIterator.hasNext())
            wsal = wsalIterator.next();
        else {
            wsal = new WSAL();
            wsal.setIsAntiFreeze(false);
            wsal.setIsSummer(false);
            wsal.setIsLeave(false);
            wsal.setIsWinter(false);
        }
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
        wsal.setCreationDate(LocalDateTime.now().toString());
        wsalRepository.save(wsal);
    }

    /**
     * Memorize into the db the leave details
     *
     * @param leaveResource leave details
     */
    public void setL(LeaveResource leaveResource) {
        Iterator<WSAL> wsalIterator = wsalRepository.findAll().iterator();
        WSAL wsal;
        if (wsalIterator.hasNext())
            wsal = wsalIterator.next();
        else
            wsal = new WSAL();


        wsal.setIsAntiFreeze(false);
        wsal.setIsLeave(true);
        wsal.setLeaveTemperature(leaveResource.getLeaveTemperature());
        wsal.setLeaveEnd(LocalDateTime.now().plus(leaveResource.getHourAmount(), ChronoUnit.HOURS));
        wsal.setCreationDate(LocalDateTime.now().toString());
        wsalRepository.save(wsal);
    }

    private Room checkRoom(String idRoom) {
        Optional<Room> check = roomRepository.findById(idRoom);
        if (!check.isPresent())
            throw new RoomNotExistException("checkRoom");
        return check.get();
    }

    /**
     * Get the current state for the specified room
     *
     * @param idRoom
     * @return
     */
    public CurrentRoomStateResource getCurrentRoomStateResource(String idRoom) {
        Iterator<WSAL> wsalIterator = wsalRepository.findAll().iterator();
        Optional<Room> checkRoom = roomRepository.findById(idRoom);

        if (wsalIterator.hasNext() && checkRoom.isPresent())
            return new CurrentRoomStateResource(wsalIterator.next(), checkRoom.get());
        else
            return new CurrentRoomStateResource();

    }
}
