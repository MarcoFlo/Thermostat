package it.polito.thermostat.controllermd.services;

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
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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
        handleManualProgrammed();
    }

    /**
     * Set the the room to programmed mode
     *
     * @param idRoom room to program
     */
    public void setIsProgrammedRoom(String idRoom) {
        Room room = checkRoom(idRoom);
        room.setIsManual(false);
        roomRepository.save(room);
        handleManualProgrammed();
    }

    /**
     * Memorize in the db if we are in WinterSummerAntifreeze
     *
     * @param wsa string "winter", ecc
     */
    public void setWSA(String wsa) {
        WSAL wsal = getWSAL();

        switch (wsa) {
            case "winter":
                wsal.setIsSummer(false);
                wsal.setIsWinter(true);
                break;
            case "summer":
                wsal.setIsSummer(true);
                wsal.setIsWinter(false);
                wsal.setIsAntiFreeze(false);
                break;
            case "antifreeze":
                wsal.setIsAntiFreeze(!wsal.getIsAntiFreeze());
                List<Room> roomList = ((List<Room>) roomRepository.findAll()).stream().map(room -> {
                    room.setIsManual(false);
                    return room;
                }).collect(Collectors.toList());
                roomRepository.saveAll(roomList);
                wsal.setIsSummer(false);
                wsal.setIsWinter(true);
                break;
            default:
                logger.error("setWsa string not recognised");
        }
        logger.info(wsal.toString());
        wsalRepository.save(wsal);
    }

    /**
     * Memorize into the db the leave details
     *
     * @param leaveResource leave details
     */
    public void setL(LeaveResource leaveResource) {
        WSAL wsal = getWSAL();
        wsal.setIsAntiFreeze(false);
        wsal.setIsLeave(true);
        wsal.setLeaveTemperature(leaveResource.getLeaveTemperature());
        wsal.setLeaveEnd(LocalDateTime.now().plus(leaveResource.getHourAmount(), ChronoUnit.HOURS));
        wsalRepository.save(wsal);
    }

    private Room checkRoom(String idRoom) {
        Optional<Room> check = roomRepository.findById(idRoom);
        if (!check.isPresent())
            throw new RoomNotExistException("checkRoom");
        return check.get();
    }

    /**
     * @param idRoom
     * @return the current state for the specified room
     */
    public CurrentRoomStateResource getCurrentRoomStateResource(String idRoom) {
        Optional<WSAL> wsalCheck = wsalRepository.findById("mainwsal");
        Optional<Room> checkRoom = roomRepository.findById(idRoom);

        if (wsalCheck.isPresent() && checkRoom.isPresent()) {
            logger.info("old current room stare tesource");

            return new CurrentRoomStateResource(wsalCheck.get(), checkRoom.get());
        } else {
            logger.info("new current room stare tesource");
            return new CurrentRoomStateResource();
        }
    }


    private WSAL getWSAL() {
        WSAL wsal;
        Optional<WSAL> checkWSAL = wsalRepository.findById("mainwsal");
        if (checkWSAL.isPresent()) {
            logger.info("old wsal");
            wsal = checkWSAL.get();
        } else {
            logger.info("new wsal");

            wsal = new WSAL();
            wsal.setIsAntiFreeze(false);
            wsal.setAntiFreezeTemperature(10.0);
            wsal.setIsSummer(false);
            wsal.setIsLeave(false);
            wsal.setIsWinter(false);
        }
        wsal.setId("mainwsal");
        return wsal;
    }

    private void handleManualProgrammed() {
        logger.info("handle manual programmed");
        WSAL wsal = getWSAL();
        wsal.setIsAntiFreeze(false);
        wsal.setIsLeave(false);
        wsalRepository.save(wsal);
    }
}
