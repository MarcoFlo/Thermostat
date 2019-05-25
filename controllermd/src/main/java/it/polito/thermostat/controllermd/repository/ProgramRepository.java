package it.polito.thermostat.controllermd.repository;

import it.polito.thermostat.controllermd.entity.program.Program;
import org.springframework.data.repository.CrudRepository;

public interface ProgramRepository extends CrudRepository<Program, String> {
}
