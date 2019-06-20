package it.polito.thermostat.controllermd.services.server;

import it.polito.thermostat.controllermd.configuration.exception.ProgramNotExistException;
import it.polito.thermostat.controllermd.entity.Room;
import it.polito.thermostat.controllermd.entity.program.Program;
import it.polito.thermostat.controllermd.repository.ProgramRepository;
import it.polito.thermostat.controllermd.repository.RoomRepository;
import it.polito.thermostat.controllermd.resources.RoomResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class SettingService {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    RoomRepository roomRepository;

    @Autowired
    ProgramRepository programRepository;

    public void saveRoom(RoomResource roomResource)
    {
        roomRepository.save(new Room(roomResource));

        programRepository.save(roomResource.getProgram());
    }

    public void deleteRoom(String idRoom)
    {
        roomRepository.deleteById(idRoom);
    }

    public Program getProgramRoom(String idRoom) {
        Optional<Program> check = programRepository.findById(idRoom);
        if (!check.isPresent())
            throw new ProgramNotExistException("getProgramRoom");
        return check.get();
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

}
