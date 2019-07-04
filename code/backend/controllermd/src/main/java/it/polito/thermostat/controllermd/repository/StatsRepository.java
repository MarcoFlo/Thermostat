package it.polito.thermostat.controllermd.repository;

import it.polito.thermostat.controllermd.entity.Stats;
import org.springframework.data.repository.CrudRepository;

import java.time.LocalDate;

public interface StatsRepository extends CrudRepository<Stats, String> {
}
