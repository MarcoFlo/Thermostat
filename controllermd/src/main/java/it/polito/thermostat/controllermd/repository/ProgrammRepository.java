package it.polito.thermostat.controllermd.repository;

import it.polito.thermostat.controllermd.object.Programm;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface ProgrammRepository extends MongoRepository<Programm, Integer> {
    Optional<Programm> findByIdProgramm(String idProgramm);
}
