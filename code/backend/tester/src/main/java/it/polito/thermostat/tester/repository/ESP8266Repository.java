package it.polito.thermostat.tester.repository;

import it.polito.thermostat.tester.entity.ESP8266;
import org.springframework.data.repository.CrudRepository;


public interface ESP8266Repository extends CrudRepository<ESP8266, String> {
}
