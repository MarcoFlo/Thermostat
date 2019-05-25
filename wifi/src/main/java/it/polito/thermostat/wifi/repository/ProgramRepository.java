package it.polito.thermostat.wifi.repository;

import it.polito.thermostat.wifi.entity.program.Program;
import org.springframework.data.repository.CrudRepository;

public interface ProgramRepository extends CrudRepository<Program, String> {
}
