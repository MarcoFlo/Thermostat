package it.polito.thermostat.controllermd.controller;

import it.polito.thermostat.controllermd.repository.ESP8266Repository;
import it.polito.thermostat.controllermd.repository.ProgramRepository;
import it.polito.thermostat.controllermd.repository.RoomRepository;
import it.polito.thermostat.controllermd.repository.WSALRepository;
import it.polito.thermostat.controllermd.services.StatService;
import it.polito.thermostat.controllermd.services.WifiService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class DebugRestController {
    private Logger logger = LoggerFactory.getLogger(this.getClass());
    @Autowired
    WifiService wifiService;


    @Autowired
    WSALRepository wsalRepository;

    @Autowired
    RoomRepository roomRepository;

    @Autowired
    ProgramRepository programRepository;

    @Autowired
    ESP8266Repository esp8266Repository;

    @Autowired
    StatService statService;


    @PostMapping("/debug/wsal")
    public void deleteWSAL() {
        wsalRepository.deleteAll();
    }

    @PostMapping("/debug/getall")
    public void getAll() {
        logger.info(wsalRepository.findAll().toString());
        logger.info(esp8266Repository.findAll().toString());
        logger.info(programRepository.findAll().toString());
        logger.info(roomRepository.findAll().toString());
    }

    @PostMapping("/debug/stats")
    public void buildStats() {
        statService.buildNewDataSet();
        logger.info("new data set built");
    }
}