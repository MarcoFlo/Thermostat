package it.polito.thermostat.controllermd.services.logic;

import it.polito.thermostat.controllermd.entity.CommandActuator;
import it.polito.thermostat.controllermd.entity.Room;
import it.polito.thermostat.controllermd.entity.Stats;
import it.polito.thermostat.controllermd.repository.RoomRepository;
import it.polito.thermostat.controllermd.repository.StatsRepository;
import it.polito.thermostat.controllermd.resources.StatsResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

@Service
public class StatService {
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    StatsRepository statsRepository;
    @Autowired
    RoomRepository roomRepository;


    private Stats handleStatUpdate(Stats stats, Boolean currentCommand, LocalTime time) {
        if (stats.getOn() && !currentCommand) {
            stats.setOn(false);
            stats.setAmount(stats.getAmount() + ChronoUnit.HOURS.between(stats.getCommandTime(), time));
            return stats;
        } else if (!stats.getOn() && currentCommand) {
            stats.setOn(true);
            stats.setCommandTime(time);
            return stats;
        }
        return null;
    }

    public void handleNewCommand(String idRoom, CommandActuator commandActuator) {
        LocalDate nowDate = LocalDate.now();
        Optional<Stats> checkStat = statsRepository.findById(nowDate + idRoom);
        if (checkStat.isPresent()) {
            Stats previousStat = checkStat.get();
            Stats updatedStat = handleStatUpdate(previousStat, commandActuator.getCommandBoolean(), LocalTime.now());
            if (updatedStat != null)
                statsRepository.save(updatedStat);
        } else {
            statsRepository.save(new Stats(nowDate + idRoom, nowDate, idRoom, (long) 0, LocalTime.now(), commandActuator.getCommandBoolean()));
        }
    }


    public StatsResource getweeklyStats(String idRoom) {
        List<String> dayList = new LinkedList<>();
        List<List<Long>> dataList = new LinkedList<>();
        List<Long> onList = new LinkedList<>();
        List<Long> offList = new LinkedList<>();

        List<String> statsKey = new LinkedList<>();
        for (int i = 8; i >= 1; i--) {
            LocalDate day = LocalDate.now().minus(i, ChronoUnit.DAYS);
            statsKey.add(day + idRoom);
        }
        List<Stats> statsList = (List<Stats>) statsRepository.findAllById(statsKey);
        for (Stats stat : statsList) {
            String dayOfWeek = stat.getDay().getDayOfWeek().toString();
            dayList.add(dayOfWeek.substring(0, 1) + dayOfWeek.toLowerCase().substring(1).toLowerCase());
            onList.add(stat.getAmount());
            offList.add(24 - stat.getAmount());
        }


        dataList.add(onList);
        dataList.add(offList);
        return new StatsResource(dayList, dataList);
    }


    @Scheduled(cron = "0 0 0 * * ?")
    public void completeStatsCalculation() {
        LocalTime midnight = LocalTime.now().withHour(0).withMinute(0).withSecond(0).withNano(0);
        LocalDate today = LocalDate.now().minus(12, ChronoUnit.HOURS);

        List<String> keyDayRoomList = ((List<Room>) roomRepository.findAll()).stream().map(room -> room.getIdRoom() + today).collect(Collectors.toList());
        List<Stats> statToBeSaved = new LinkedList<>();

        for (Stats stat : statsRepository.findAllById(keyDayRoomList)) {
            if (!stat.getCommandTime().equals(midnight)) {
                Stats updatedStat = handleStatUpdate(stat, false, midnight);
                updatedStat.setCommandTime(midnight);
                updatedStat.setOn(false);
                statToBeSaved.add(updatedStat);
            }
        }
        statsRepository.saveAll(statToBeSaved);
    }

    @Profile("dev")
    public void buildNewDataSet() {
        statsRepository.deleteAll();

        List<Stats> toBeSaved = new LinkedList<>();
        String[] roomArr = {"MainRoom", "Kitchen", "Living"};

        for (String idRoom : roomArr) {
            for (int i = 1; i < 8; i++) {
                LocalDate day = LocalDate.now().minus(i, ChronoUnit.DAYS);
                Long amount = ThreadLocalRandom.current().nextLong(0, 16);
                toBeSaved.add(new Stats(day + idRoom, day, idRoom, amount, LocalTime.now(), false));
            }
        }
        statsRepository.saveAll(toBeSaved);
    }
}
