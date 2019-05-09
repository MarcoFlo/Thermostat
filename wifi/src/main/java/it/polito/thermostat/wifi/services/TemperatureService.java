package it.polito.thermostat.wifi.services;

import it.polito.thermostat.wifi.entity.Mode;
import it.polito.thermostat.wifi.entity.Room;
import it.polito.thermostat.wifi.object.Programm;
import it.polito.thermostat.wifi.repository.ModeRepository;
import it.polito.thermostat.wifi.repository.ESP8266Repository;
import it.polito.thermostat.wifi.repository.ProgrammRepository;
import it.polito.thermostat.wifi.repository.RoomRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalTime;
import java.util.List;

@Service
public class TemperatureService {
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    ESP8266Repository esp8266Repository;

    @Autowired
    RoomRepository roomRepository;

    @Autowired
    ProgrammRepository programmRepository;

    @Autowired
    ModeRepository modeRepository;

    @Autowired
    MQTTservice mqttService;


    public void setManualRoom(String idRoom, Double desiredTemperature) {
        Room room = roomRepository.findByIdRoom(idRoom).get();
        room.setIsManual(true);
        room.setDesiredTemperature(desiredTemperature);
        roomRepository.save(room);
    }


    /**
     * Memorize in the db if we are in WinterSummerAntifreeze
     * if antifreeze activate the actuator accordingly
     *
     * @param wsa
     */
    public void setWSA(String wsa) {
        Mode mode = modeRepository.findAll().get(0);
        switch (wsa) {
            case "winter":
                mode.setIsSummer(false);
                break;
            case "summer":
                mode.setIsSummer(true);
                mode.setIsAntifreeze(false);
                break;
            case "antifreeze":
                mode.setIsAntifreeze(true);
                mode.setIsSummer(false);
                break;
            default:
                logger.error("setWsa string not recognised");
        }
        modeRepository.save(mode);
    }

    /**
     * Set the actuator accordingly to the leave time
     *
     * @param leaveTime
     */
    public void setL(LocalTime leaveTime, Double desiredTemperature) {
        Mode mode = modeRepository.findAll().get(0);
        mode.setLeaveTime(leaveTime);
        mode.setDesiredTemperature(desiredTemperature);
        modeRepository.save(mode);
    }


    /**
     * Set the esp related to the room to programm/manual mode
     *
     * @param idRoom
     */
    public void setIsProgrammedRoom(String idRoom) {
        Room room = roomRepository.findByIdRoom(idRoom).get();
        room.setIsManual(false);
        roomRepository.save(room);
    }


    /**
     * @param programmList
     */
    public void saveProgrammList(List<Programm> programmList) {
        programmRepository.saveAll(programmList);
    }
}
