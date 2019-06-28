package it.polito.thermostat.controllermd.services.server;

import it.polito.thermostat.controllermd.configuration.SeasonGetter;
import it.polito.thermostat.controllermd.configuration.exception.ProgramNotExistException;
import it.polito.thermostat.controllermd.entity.Room;
import it.polito.thermostat.controllermd.entity.Program;
import it.polito.thermostat.controllermd.repository.ProgramRepository;
import it.polito.thermostat.controllermd.repository.RoomRepository;
import it.polito.thermostat.controllermd.resources.RoomResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
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

    public void saveRoom(RoomResource roomResource) {
        roomRepository.save(new Room(roomResource));

        programRepository.save(roomResource.getProgram());
    }

    public void deleteRoom(String idRoom) {
        roomRepository.deleteById(idRoom);
    }

    public List<String> getListRoom() {
        return ((List<Room>) roomRepository.findAll()).stream().map(room -> room.getIdRoom()).collect(Collectors.toList());
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
