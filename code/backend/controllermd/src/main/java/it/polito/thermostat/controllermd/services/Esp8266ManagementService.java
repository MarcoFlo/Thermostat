package it.polito.thermostat.controllermd.services;

import it.polito.thermostat.controllermd.entity.ESP8266;
import it.polito.thermostat.controllermd.entity.Room;
import it.polito.thermostat.controllermd.repository.ESP8266Repository;
import it.polito.thermostat.controllermd.repository.RoomRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


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
     * @param idRoom
     * @param espList
     */
    public void setAssociation(List<String> espList, String idRoom) {
        List<ESP8266> esp8266List = espList.stream().map(idEsp -> {
            Optional<ESP8266> checkEsp = esp8266Repository.findById(idEsp);
            if (!checkEsp.isPresent()) {
                logger.info("setAssociation esp not present " + idEsp);
                throw new IllegalArgumentException();
            }
            return checkEsp.get();
        }).collect(Collectors.toList());



        //The esp was already associated to some room
        esp8266List.forEach(esp8266 -> {
            Room room;
            if (esp8266.getIdRoom() != null && !esp8266.getIdRoom().equals(idRoom)) {
                logger.info("remove from old room");
                //remove esp from the old room
                room = roomRepository.findById(esp8266.getIdRoom()).get();
                List<String> idList = room.getEsp8266List();
                room.setEsp8266List(idList.stream().filter(id -> !id.equals(esp8266.getIdEsp())).collect(Collectors.toList()));
                roomRepository.save(room);
            }
        });

        logger.info("add to new room");
        //add esp to the new room
        Room room = roomRepository.findById(idRoom).get();
        List<String> idList = room.getEsp8266List();
        esp8266List.forEach(esp8266 ->
        {
            esp8266.setIdRoom(idRoom);
            if (!idList.contains(esp8266.getIdEsp()))
                idList.add(esp8266.getIdEsp());
        });
        esp8266Repository.saveAll(esp8266List);
        room.setEsp8266List(idList);
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
        return ((List<ESP8266>) esp8266Repository.findAll()).stream().filter(esp8266 -> esp8266.getIdRoom() == null).map(ESP8266::getIdEsp).collect(Collectors.toList());
    }
}
