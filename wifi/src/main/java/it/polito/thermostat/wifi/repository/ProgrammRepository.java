package it.polito.thermostat.wifi.repository;

import it.polito.thermostat.wifi.object.Programm;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface ProgrammRepository extends MongoRepository<Programm, Integer> {
    Optional<Programm> findByIdProgramm(String idProgramm);
}
