package it.polito.thermostat.wifi.repository;

import it.polito.thermostat.wifi.entity.Room;
import org.springframework.data.repository.CrudRepository;

public interface RoomRepository extends CrudRepository<Room, String> {
}