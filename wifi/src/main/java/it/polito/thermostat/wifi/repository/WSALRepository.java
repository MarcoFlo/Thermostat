package it.polito.thermostat.wifi.repository;

import it.polito.thermostat.wifi.entity.WSAL;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface WSALRepository extends MongoRepository<WSAL,Integer> {

}
