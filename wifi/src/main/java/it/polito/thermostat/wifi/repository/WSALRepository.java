package it.polito.thermostat.wifi.repository;

import it.polito.thermostat.wifi.entity.WSAL;
import org.springframework.data.repository.CrudRepository;

import java.time.LocalDateTime;


public interface WSALRepository extends CrudRepository<WSAL, LocalDateTime> {

}
