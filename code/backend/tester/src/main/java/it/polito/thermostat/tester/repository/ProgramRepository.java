package it.polito.thermostat.tester.repository;

import it.polito.thermostat.tester.entity.Program;
import org.springframework.data.repository.CrudRepository;

public interface ProgramRepository extends CrudRepository<Program, String> {
}
