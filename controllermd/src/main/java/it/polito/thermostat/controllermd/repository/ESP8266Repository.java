package it.polito.thermostat.controllermd.repository;

import it.polito.thermostat.controllermd.entity.ESP8266;
import org.springframework.data.repository.CrudRepository;

public interface ESP8266Repository extends CrudRepository<ESP8266, String> {
}
