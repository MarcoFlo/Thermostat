package it.polito.thermostat.controllermd.controller;

import it.polito.thermostat.controllermd.services.server.WifiService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class DebugRestController {
    private Logger logger = LoggerFactory.getLogger(this.getClass());
    @Autowired
    WifiService wifiService;





}
