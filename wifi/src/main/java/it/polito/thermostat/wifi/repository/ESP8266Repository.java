package it.polito.thermostat.wifi.repository;

import it.polito.thermostat.wifi.entity.ESP8266;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface ESP8266Repository extends CrudRepository<ESP8266, String> {
    List<ESP8266> findByIdRoom(String idRoom);
}
