package it.polito.thermostat.controllermd.repository;

import it.polito.thermostat.controllermd.object.Room;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface RoomRepository extends MongoRepository<Room, Integer> {
    Optional<Room> findByIdRoom(String idRoom);
}
