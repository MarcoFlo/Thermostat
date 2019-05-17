package it.polito.thermostat.wifi.repository;

import it.polito.thermostat.wifi.entity.Room;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

import java.util.Optional;

public interface RoomRepository extends MongoRepository<Room, Integer> {
    Optional<Room> findByIdRoom(String idRoom);
}
