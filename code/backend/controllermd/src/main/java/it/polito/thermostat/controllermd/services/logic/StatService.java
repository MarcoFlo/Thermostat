package it.polito.thermostat.controllermd.services.logic;

import it.polito.thermostat.controllermd.configuration.exception.StatNotExistException;
import it.polito.thermostat.controllermd.entity.CommandActuator;
import it.polito.thermostat.controllermd.entity.Room;
import it.polito.thermostat.controllermd.entity.Stats;
import it.polito.thermostat.controllermd.repository.RoomRepository;
import it.polito.thermostat.controllermd.repository.StatsRepository;
import it.polito.thermostat.controllermd.resources.StatsResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class StatService {
    private Logger logger = LoggerFactory.getLogger(this.getClass());


    //TODO chiamare una funzione che a mezzanotte calcola le ultime eventuali ore di on
    @Autowired
    StatsRepository statsRepository;
    @Autowired
    RoomRepository roomRepository;

    public void handleNewCommand(String idRoom, CommandActuator commandActuator) {
        LocalDate nowDate = LocalDate.now();
        Optional<Stats> checkStat = statsRepository.findById(nowDate + idRoom);
        if (checkStat.isPresent()) {
            Stats stats = checkStat.get();
            if (stats.getOn() && !commandActuator.getCommandBoolean()) {
                logger.info("Sto spegnendo");
                stats.setOn(false);
                stats.setAmount(stats.getAmount() + ChronoUnit.HOURS.between(stats.getOnTime(), LocalTime.now()));
                statsRepository.save(stats);
            } else if (!stats.getOn() && commandActuator.getCommandBoolean()) {
                logger.info("Sto accendendo");
                stats.setOn(true);
                stats.setOnTime(LocalTime.now());
                statsRepository.save(stats);
            }
        } else {
            statsRepository.save(new Stats(nowDate + idRoom, nowDate, idRoom, (long) 0, LocalTime.now(), commandActuator.getCommandBoolean()));

        }
    }

    public List<Stats> getDayStats(String date) {
        List<Room> roomList = (List<Room>) roomRepository.findAll();
        return roomList.stream().map(room -> getRoomDayStats(room.getIdRoom(), date)).collect(Collectors.toList());
    }

    private Stats getRoomDayStats(String idRoom, String date) {
        Optional<Stats> statsCheck = statsRepository.findById(idRoom + date);
        if (statsCheck.isPresent())
            return statsCheck.get();
        else
            throw new StatNotExistException();
    }
}
