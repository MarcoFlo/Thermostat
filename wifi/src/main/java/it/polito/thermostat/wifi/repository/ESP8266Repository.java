package it.polito.thermostat.wifi.repository;

import it.polito.thermostat.wifi.entity.ESP8266;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface ESP8266Repository extends MongoRepository<ESP8266, Integer> {
    Optional<ESP8266> findByIdEsp(String idEsp);
    Iterable<ESP8266> findAllByIdRoom(String idRoom);
}
