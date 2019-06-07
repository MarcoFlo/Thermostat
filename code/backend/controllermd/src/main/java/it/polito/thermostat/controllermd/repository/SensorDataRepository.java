package it.polito.thermostat.controllermd.repository;

import it.polito.thermostat.controllermd.entity.SensorData;
import org.springframework.data.repository.CrudRepository;


public interface SensorDataRepository extends CrudRepository<SensorData, String> {
}