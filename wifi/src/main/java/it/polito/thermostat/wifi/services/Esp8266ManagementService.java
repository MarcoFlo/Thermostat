package it.polito.thermostat.wifi.services;

import it.polito.thermostat.wifi.object.ESP8266;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.concurrent.ConcurrentHashMap;

@Service
public class Esp8266ManagementService {
    @Autowired
    private ConcurrentHashMap<String, ESP8266> esp8266Map;

    /**
     * Allow us to set an association between an esp and the choosen room
     * TODO set up control
     *
     * @param idRoom
     * @param idEsp
     * @return
     */
    public Boolean setAssociation(String idRoom, String idEsp) {
        esp8266Map.get(idEsp).setIdRoom(idRoom);
        return true;
    }

    /**
     * Allow us to delete an association between an esp and the choosen room
     * TODO set up control
     *
     * @param idEsp
     * @return
     */
    public Boolean deleteAssociation(String idEsp) {
        esp8266Map.get(idEsp).setIdRoom(null);
        return true;
    }
}
