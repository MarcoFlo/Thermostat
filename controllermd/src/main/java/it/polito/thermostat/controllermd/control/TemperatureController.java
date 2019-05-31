package it.polito.thermostat.controllermd.control;

import it.polito.thermostat.controllermd.repository.RoomRepository;
import it.polito.thermostat.controllermd.resources.LeaveResource;
import it.polito.thermostat.controllermd.resources.RoomManualResource;
import it.polito.thermostat.controllermd.services.server.TemperatureService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TemperatureController {
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    TemperatureService temperatureService;

    @Autowired
    RoomRepository roomRepository;

    /**
     * Endpoint to change the current WSA(L), sending the string "winter", "summer", "antifreeze"
     *
     * @param wsa
     */
    @PostMapping("/temperature/wsa")
    public void postWSA(@RequestBody String wsa) {
        logger.info("I'm gonna save this WSA config: " + wsa);
        temperatureService.setWSA(wsa);
    }

    /**
     * Endpoint to change the current (WSA)L
     *
     * @param leaveResource
     */
    @PostMapping("/temperature/leave")
    public void postL(@RequestBody LeaveResource leaveResource) {
        logger.info("I'm gonna save this Leave config: " + leaveResource.toString());
        temperatureService.setL(leaveResource);
    }

    /**
     * Endpoint to set a room manual
     *
     * @param roomManualResource
     */
    @PostMapping("/temperature/manual")
    public void postManual(@RequestBody RoomManualResource roomManualResource) {
        logger.info("I'm gonna set " + roomManualResource.getIdRoom() + " manual to " + roomManualResource.getDesiredTemperature() + "°");
        temperatureService.setManualRoom(roomManualResource.getIdRoom(), roomManualResource.getDesiredTemperature());
    }

    /**
     * Endpoint to set a room programmed
     *
     * @param idRoom
     */
    @PostMapping("/temperature/programmed")
    public void postProgram(@RequestBody String idRoom) {
        logger.info("I'm gonna set " + idRoom + " to programmed");
        temperatureService.setIsProgrammedRoom(idRoom);
    }
}
