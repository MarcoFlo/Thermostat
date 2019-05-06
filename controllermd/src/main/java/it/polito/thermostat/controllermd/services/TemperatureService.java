package it.polito.thermostat.controllermd.services;


import it.polito.thermostat.controllermd.object.ESP8266;
import it.polito.thermostat.controllermd.object.Programm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.util.DefaultPropertiesPersister;

import java.io.*;
import java.time.LocalTime;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Service
public class TemperatureService {
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private ConcurrentHashMap<String, ESP8266> esp8266Map;

    @Autowired
    private ConcurrentHashMap<String, Programm> programMap;

    @Autowired
    MQTTservice mqttService;


    public void setManualRoom(String idRoom, Double desiredTemperature, Boolean isSummer) {
        List<ESP8266> espRoomActuators = esp8266Map.values().stream().filter(esp -> (esp.getIdRoom() == idRoom) && !esp.getIsSensor()).collect(Collectors.toList());
        if (isSummer) {
            espRoomActuators.stream().filter(esp -> !esp.getIsHeater()).forEach(cooler -> mqttService.manageActuator(cooler, "on"));
        }


    }


    /**
     * Memorize in the db if we are in WinterSummerAntifreeze
     * if antifreeze activate the actuator accordingly
     *
     * @param wsa
     */
    public void setWSA(String wsa) {
    }

    /**
     * Set the actuator accordingly to the leave time
     *
     * @param leaveTime
     */
    public void setL(LocalTime leaveTime) {
    }



    /**
     * Set the actuator accordingly to the program related to the room
     *
     * @param idRoom
     */
    //@Scheduled(cron = "${" +  + "}")
    public void setProgrammRoom(String idRoom) {
    }

    //@Async("threadPoolTaskExecutor")
    public void manageDesiredTemperature() {

    }


    /**
     * Save into the db (TODO) the programs set by the user
     *
     * @param programmList
     */
    public void saveProgrammList(List<Programm> programmList) {
        programmList.stream().forEach(programm -> programMap.put(programm.getIdProgramm(), programm));
    }
}
