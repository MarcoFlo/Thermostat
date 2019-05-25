package it.polito.thermostat.controllermd.repository;

import it.polito.thermostat.controllermd.entity.WSAL;
import org.springframework.data.repository.CrudRepository;

import java.time.LocalDateTime;


public interface WSALRepository extends CrudRepository<WSAL, LocalDateTime> {

}
