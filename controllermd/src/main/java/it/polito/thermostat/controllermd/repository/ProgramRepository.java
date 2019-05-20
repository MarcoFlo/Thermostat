package it.polito.thermostat.controllermd.repository;

import it.polito.thermostat.controllermd.entity.program.Program;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

public interface ProgramRepository extends MongoRepository<Program, Integer> {
    Optional<Program> findByIdProgram(String idProgram);
}