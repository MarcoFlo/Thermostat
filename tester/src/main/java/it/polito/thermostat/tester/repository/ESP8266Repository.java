package it.polito.thermostat.tester.repository;

import it.polito.thermostat.tester.entity.ESP8266;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface ESP8266Repository extends MongoRepository<ESP8266, Integer> {
    Optional<ESP8266> findByIdEsp(String idEsp);
}
