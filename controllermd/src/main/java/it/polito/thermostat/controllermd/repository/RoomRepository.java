package it.polito.thermostat.controllermd.repository;

import it.polito.thermostat.controllermd.entity.Room;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

public interface RoomRepository extends MongoRepository<Room, Integer> {
    Optional<Room> findByIdRoom(String idRoom);
}