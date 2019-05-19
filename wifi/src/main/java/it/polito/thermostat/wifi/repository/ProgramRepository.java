package it.polito.thermostat.wifi.repository;

import it.polito.thermostat.wifi.entity.program.Program;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface ProgramRepository extends MongoRepository<Program, Integer> {
    Optional<Program> findByIdProgram(String idProgram);
}
