package it.polito.thermostat.wifi.control;

import it.polito.thermostat.wifi.entity.Room;
import it.polito.thermostat.wifi.entity.ESP8266;
import it.polito.thermostat.wifi.entity.program.Program;
import it.polito.thermostat.wifi.repository.RoomRepository;
import it.polito.thermostat.wifi.resources.LeaveResource;
import it.polito.thermostat.wifi.resources.RoomSettingResource;
import it.polito.thermostat.wifi.services.TemperatureService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
public class TemperatureController {
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    TemperatureService temperatureService;

    @Autowired
    RoomRepository roomRepository;

    @PostMapping("/wsa")
    public void postWSA(@RequestBody String wsa) {
        temperatureService.setWSA(wsa);
    }

    @PostMapping("/leave")
    public void postL(@RequestBody LeaveResource leaveResource) {

        temperatureService.setL(leaveResource);
    }

    @PostMapping("/manual")
    public void postManual(@RequestBody RoomSettingResource roomSettingResource) {
        temperatureService.setManualRoom(roomSettingResource.getIdRoom(), roomSettingResource.getDesiredTemperature());
    }

    @PostMapping("/programmed")
    public void postProgram(@RequestBody String idRoom) {
        temperatureService.setIsProgrammedRoom(idRoom);
    }

    @PostMapping("/room_setting")
    public void postPrograms(@RequestBody List<Program> programList) {
        temperatureService.saveProgramList(programList);
    }

}
