package it.polito.thermostat.controllermd.repository;

import it.polito.thermostat.controllermd.entity.WSAL;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface WSALRepository extends MongoRepository<WSAL,Integer> {

}
