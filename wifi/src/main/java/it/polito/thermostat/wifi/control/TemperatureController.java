package it.polito.thermostat.wifi.control;

import it.polito.thermostat.wifi.entity.program.Program;
import it.polito.thermostat.wifi.repository.RoomRepository;
import it.polito.thermostat.wifi.resources.LeaveResource;
import it.polito.thermostat.wifi.resources.RoomManualResource;
import it.polito.thermostat.wifi.services.TemperatureService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class TemperatureController {
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    TemperatureService temperatureService;

    @Autowired
    RoomRepository roomRepository;

    /**
     * Endpoint to change the current WSA(L), sending the string "winter", "summer", "antifreeze"
     * @param wsa
     */
    @PostMapping("/wsa")
    public void postWSA(@RequestBody String wsa) {
        temperatureService.setWSA(wsa);
    }

    /**
     * Endpoint to change the current (WSA)L
     * @param leaveResource
     */
    @PostMapping("/leave")
    public void postL(@RequestBody LeaveResource leaveResource) {

        temperatureService.setL(leaveResource);
    }

    /**
     * Endpoint to set a room manual
     * @param roomManualResource
     */
    @PostMapping("/manual")
    public void postManual(@RequestBody RoomManualResource roomManualResource) {
        temperatureService.setManualRoom(roomManualResource.getIdRoom(), roomManualResource.getDesiredTemperature());
    }

    /**
     * Endpoint to set a room programmed
     * @param idRoom
     */
    @PostMapping("/programmed")
    public void postProgram(@RequestBody String idRoom) {
        temperatureService.setIsProgrammedRoom(idRoom);
    }
}
