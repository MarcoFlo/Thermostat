package it.polito.thermostat.wifi.services;

import it.polito.thermostat.wifi.object.ESP8266;
import it.polito.thermostat.wifi.object.Programm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalTime;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Service
public class TemperatureService {
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private ConcurrentHashMap<String, ESP8266> esp8266Map;

    public void setManualRoom(String idRoom, Double desiredTemperature) {
        List<ESP8266> espRoomActuators = esp8266Map.values().stream().filter(esp -> esp.getIdRoom() == idRoom).collect(Collectors.toList());


    }

    /**
     * Memorize in the db if we are in WinterSummerAntifreeze
     * if antifreeze activate the actuator accordingly
     * @param wsa
     */
    public void setWSA(String wsa) {
    }

    /**
     * Set the actuator accordingly to the leave time
     * @param leaveTime
     */
    public void setL(LocalTime leaveTime) {
    }

    /**
     * Set the actuator accordingly to the program related to the room
     * @param idRoom
     */
    public void setProgrammRoom(String idRoom) {
    }

    /**
     * Save into the db the programs set by the user
     * @param programmList
     */
    public void saveProgrammList(List<Programm> programmList) {
    }
}
