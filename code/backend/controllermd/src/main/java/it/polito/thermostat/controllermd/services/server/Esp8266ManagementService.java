package it.polito.thermostat.controllermd.services.server;

import it.polito.thermostat.controllermd.entity.ESP8266;
import it.polito.thermostat.controllermd.entity.Room;
import it.polito.thermostat.controllermd.repository.ESP8266Repository;
import it.polito.thermostat.controllermd.repository.RoomRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;


@Service
public class Esp8266ManagementService {
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    ESP8266Repository esp8266Repository;

    @Autowired
    RoomRepository roomRepository;


    /**
     * Allow us to set an association between an esp and the choosen room
     * TODO set up control
     *
     * @param idRoom idRooom to set
     * @param idEsp  idEsp to set
     */
    public void setAssociation(String idEsp, String idRoom) {
        ESP8266 esp8266 = esp8266Repository.findById(idEsp).get();

        Room room;
        List<String> esp8266List;
        //The esp was already associated to some room
        if (!esp8266.getIdRoom().equals(idRoom) && esp8266.getIdRoom() != null) {
            //remove esp from the old room
            room = roomRepository.findById(esp8266.getIdRoom()).get();
            esp8266List = room.getEsp8266List();
            room.setEsp8266List(esp8266List.stream().filter(id -> !id.equals(idEsp)).collect(Collectors.toList()));
            roomRepository.save(room);
        }

        //add esp to the new room
        esp8266.setIdRoom(idRoom);
        esp8266Repository.save(esp8266);
        room = roomRepository.findById(idRoom).get();
        esp8266List = room.getEsp8266List();
        esp8266List.add(esp8266.getIdEsp());
        room.setEsp8266List(esp8266List);
        roomRepository.save(room);

    }

    /**
     * Allow us to delete an association between an esp and the choosen room
     * TODO set up control
     *
     * @param idEsp idEsp to delete
     */
    public void deleteAssociation(String idEsp) {
        ESP8266 esp8266 = esp8266Repository.findById(idEsp).get();

        Room room = roomRepository.findById(esp8266.getIdRoom()).get();
        List<String> esp8266List = room.getEsp8266List();
        room.setEsp8266List(esp8266List.stream().filter(id -> !id.equals(idEsp)).collect(Collectors.toList()));
        roomRepository.save(room);

        esp8266.setIdRoom(null);
        esp8266Repository.save(esp8266);
    }

    /**
     * @return list of free esp
     */
    public List<String> getEspFree() {
        return StreamSupport.stream(esp8266Repository.findAll().spliterator(), false).filter(esp8266 -> esp8266.getIdRoom() == null).map(ESP8266::getIdEsp).collect(Collectors.toList());
    }
}
