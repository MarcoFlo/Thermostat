package it.polito.thermostat.tester.repository;

import it.polito.thermostat.tester.entity.Room;
import org.springframework.data.repository.CrudRepository;

public interface RoomRepository extends CrudRepository<Room, String> {
}