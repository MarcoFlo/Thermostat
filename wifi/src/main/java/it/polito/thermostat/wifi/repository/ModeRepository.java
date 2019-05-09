package it.polito.thermostat.wifi.repository;

import it.polito.thermostat.wifi.entity.Mode;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ModeRepository extends MongoRepository<Mode, Integer> {

}
