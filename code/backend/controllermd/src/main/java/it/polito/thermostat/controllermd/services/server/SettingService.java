package it.polito.thermostat.controllermd.services.server;

import it.polito.thermostat.controllermd.configuration.SeasonGetter;
import it.polito.thermostat.controllermd.configuration.exception.ProgramNotExistException;
import it.polito.thermostat.controllermd.configuration.exception.RoomNotExistException;
import it.polito.thermostat.controllermd.entity.Room;
import it.polito.thermostat.controllermd.entity.Program;
import it.polito.thermostat.controllermd.repository.ProgramRepository;
import it.polito.thermostat.controllermd.repository.RoomRepository;
import it.polito.thermostat.controllermd.resources.RoomResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class SettingService {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    RoomRepository roomRepository;

    @Autowired
    ProgramRepository programRepository;

    @Autowired
    Esp8266ManagementService esp8266ManagementService;

    @PostConstruct
    public void init() {

    }

    public void saveRoomResource(RoomResource roomResource) {
        Room room = roomRepository.save(new Room(roomResource));
        logger.info(roomResource.getEsp8266List().toString() + room.getIdRoom());
        esp8266ManagementService.setAssociation(roomResource.getEsp8266List(), room.getIdRoom());
        programRepository.save(roomResource.getProgram());

    }

    public RoomResource getRoomResource(String idRoom) {
        Optional<Room> checkRoom = roomRepository.findById(idRoom);
        Optional<Program> checkProgram = programRepository.findById(idRoom);

        if (checkRoom.isPresent() && checkProgram.isPresent())
            return new RoomResource(checkRoom.get(), checkProgram.get());
        else
            throw new IllegalArgumentException();


    }

    public void deleteRoom(String idRoom) {

        Optional<Room> checkRoom = roomRepository.findById(idRoom);
        if (!checkRoom.isPresent())
            throw new RoomNotExistException();

        Room room = checkRoom.get();
        room.getEsp8266List().forEach(esp -> esp8266ManagementService.deleteAssociation(esp));
        roomRepository.deleteById(idRoom);
    }

    public List<String> getListRoom() {
        return ((List<Room>) roomRepository.findAll()).stream().map(room -> {
            logger.info((room.getIdRoom()));
            return room.getIdRoom();}).collect(Collectors.toList());
    }

    public Program getProgramRoom(String idRoom) {
        Optional<Program> check = programRepository.findById(idRoom);
        if (!check.isPresent())
            throw new ProgramNotExistException("getProgramRoom");
        return check.get();
    }

    public Program getDefaultProgram() {
        Optional<Program> check;

        if (SeasonGetter.isSummer())
            check = programRepository.findById("summer");
        else
            check = programRepository.findById("winter");

        if (check.isPresent())
            return check.get();
        else
            logger.error("get default program error");
        return null;

    }


}
