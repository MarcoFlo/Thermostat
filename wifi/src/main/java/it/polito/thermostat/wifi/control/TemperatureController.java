package it.polito.thermostat.wifi.control;

import it.polito.thermostat.wifi.object.Programm;
import it.polito.thermostat.wifi.resources.RoomSettingResource;
import it.polito.thermostat.wifi.services.TemperatureService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.time.LocalTime;
import java.util.List;

@Controller
public class TemperatureController {
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    TemperatureService temperatureService;

    @PostMapping("/wsa")
    public void postWSA(@RequestBody String wsa) {
        temperatureService.setWSA(wsa);
    }

    @PostMapping("/leave")
    public void postL(@RequestBody LocalTime leaveTime) {

        temperatureService.setL(leaveTime);
    }

    @PostMapping("/manual")
    public void postManual(@RequestBody RoomSettingResource roomSettingResource) {
        temperatureService.setManualRoom(roomSettingResource.getIdRoom(), roomSettingResource.getDesiredTemperature(), roomSettingResource.getIsSummer());
    }

    @PostMapping("/programm")
    public void postProgramm(@RequestBody String idRoom) {
        temperatureService.setProgrammRoom(idRoom);
    }

    @PostMapping("/room_setting")
    public void postPrograms(@RequestBody List<Programm> programmList) {
        temperatureService.saveProgrammList(programmList);
    }
}
