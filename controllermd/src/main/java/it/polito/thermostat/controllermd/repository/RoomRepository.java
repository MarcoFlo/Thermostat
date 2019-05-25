package it.polito.thermostat.controllermd.repository;

import it.polito.thermostat.controllermd.entity.Room;
import org.springframework.data.repository.CrudRepository;

public interface RoomRepository extends CrudRepository<Room, String> {
}