package it.polito.thermostat.controllermd.controller;

import it.polito.thermostat.controllermd.repository.RoomRepository;
import it.polito.thermostat.controllermd.resources.CurrentRoomStateResource;
import it.polito.thermostat.controllermd.resources.LeaveResource;
import it.polito.thermostat.controllermd.resources.ManualResource;
import it.polito.thermostat.controllermd.services.server.TemperatureService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
public class TemperatureController {
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    TemperatureService temperatureService;

    @Autowired
    RoomRepository roomRepository;

    /**
     * Endpoint to change the current WSA(L)
     *
     * @param wsa the string "winter", "summer", "antifreeze"
     */
    @PostMapping(value = "/temperature/wsa")
    public void postWSA(@RequestBody String wsa) {
        logger.info("I'm gonna save this WSA config: " + wsa);
        temperatureService.setWSA(wsa);
    }

    /**
     * Endpoint to change the current (WSA)L
     *
     * @param leaveResource leave details
     */
    @PostMapping("/temperature/leave")
    public void postL(@RequestBody LeaveResource leaveResource) {
        logger.info("I'm gonna save this Leave config: " + leaveResource.toString());
        temperatureService.setL(leaveResource);
    }

    /**
     * Endpoint to set a room manual
     *
     * @param manualResource manual details
     */
    @PostMapping("/temperature/manual")
    public void postManual(@RequestBody ManualResource manualResource) {
        logger.info("I'm gonna set " + manualResource.getIdRoom() + " manual to " + manualResource.getDesiredTemperature() + "°");
        temperatureService.setManualRoom(manualResource.getIdRoom(), manualResource.getDesiredTemperature());
    }

    /**
     * Endpoint to set a room programmed
     *
     * @param idRoom programemd room
     */
    @PostMapping("/temperature/programmed")
    public void postProgram(@RequestBody String idRoom) {
        logger.info("I'm gonna set " + idRoom + " to programmed");
        temperatureService.setIsProgrammedRoom(idRoom);
    }

    @GetMapping(value = "/temperature/current_room_state_resource")
    public CurrentRoomStateResource getCurrentRoomStateResource(@RequestBody String idRoom) {
       return temperatureService.getCurrentRoomStateResource(idRoom);
    }


}
