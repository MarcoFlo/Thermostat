package it.polito.thermostat.wifi.control;

import it.polito.thermostat.wifi.entity.Room;
import it.polito.thermostat.wifi.object.ESP8266;
import it.polito.thermostat.wifi.object.Programm;
import it.polito.thermostat.wifi.repository.RoomRepository;
import it.polito.thermostat.wifi.resources.LeaveResource;
import it.polito.thermostat.wifi.resources.RoomSettingResource;
import it.polito.thermostat.wifi.services.TemperatureService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalTime;
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

        temperatureService.setL(leaveResource.getLeaveTime(), leaveResource.getDesiredTemperature());
    }

    @PostMapping("/manual")
    public void postManual(@RequestBody RoomSettingResource roomSettingResource) {
        temperatureService.setManualRoom(roomSettingResource.getIdRoom(), roomSettingResource.getDesiredTemperature());
    }

    @PostMapping("/programm")
    public void postProgramm(@RequestBody String idRoom) {
        temperatureService.setIsProgrammedRoom(idRoom);
    }

    @PostMapping("/room_setting")
    public void postPrograms(@RequestBody List<Programm> programmList) {
        temperatureService.saveProgrammList(programmList);
    }


    @PostMapping("/roomtest")
    public String roomTest() {

        Room room = new Room();
        room.setIdRoom(String.valueOf(Math.random()));
        room.setDesiredTemperature(15.0);
        List<ESP8266> esp8266List = new ArrayList<>();
        ESP8266 esp8266 = new ESP8266();
        esp8266.setIdEsp("esp1");
        esp8266.setIdRoom("room1");
        esp8266.setIsHeater(true);
        esp8266.setIsSensor(false);
        esp8266.setHumidity(20.0);
        esp8266.setTemperature(22.0);
        esp8266List.add(esp8266);
        room.setEsp8266List(esp8266List);
        room.setIsManual(true);
        roomRepository.save(room);

        Room room1 = roomRepository.findByIdRoom("room1").get();
        room1.setDesiredTemperature(50.0);
        roomRepository.save(room1);
        return "roomtest";

    }
}
