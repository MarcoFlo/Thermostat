package it.polito.thermostat.controllermd;


import com.fasterxml.jackson.databind.ObjectMapper;


import it.polito.thermostat.controllermd.entity.ESP8266;
import it.polito.thermostat.controllermd.entity.Program;
import it.polito.thermostat.controllermd.object.DailyProgram;
import it.polito.thermostat.controllermd.object.MongoZonedDateTime;
import it.polito.thermostat.controllermd.object.SensorData;
import it.polito.thermostat.controllermd.repository.ProgramRepository;
import it.polito.thermostat.controllermd.repository.RoomRepository;
import it.polito.thermostat.controllermd.repository.WSALRepository;
import it.polito.thermostat.controllermd.services.JsonHandlerService;
import it.polito.thermostat.controllermd.services.TemperatureService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cglib.core.Local;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;


import javax.annotation.PostConstruct;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAmount;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;


@SpringBootApplication
@EnableScheduling
public class ControllermdApplication implements CommandLineRunner {
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Value("#{T(java.lang.Double).parseDouble('${scaling.factor}')}")
    Double scalingFactor;

    @Autowired
    JsonHandlerService jsonHandlerService;

    @Autowired
    RoomRepository roomRepository;

    @Autowired
    ProgramRepository programRepository;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    WSALRepository wsalRepository;

    @Autowired
    TemperatureService temperatureService;




    @Bean
    public ConcurrentHashMap<String, SensorData> sensorData() {
        return new ConcurrentHashMap<>();
    }

    @PostConstruct
    public void init() {

    }

    public static void main(String[] args) {
        SpringApplication.run(ControllermdApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {

        logger.info("Loading default programs....");
        jsonHandlerService.readPrograms();
        logger.info("Loading default programs done");


        DailyProgram dailyProgram = programRepository.findByIdProgram("winter").get().getWeeklyMap().get(1);
        LocalDateTime time = MongoZonedDateTime.getDateFromMongoZonedDateTime(dailyProgram.getDailyMap().get("wake").getTime());

        //Program program = programRepository.findByIdProgram("winter").get();
        //Date date = program.getWeeklyMap().get(1).getDailyMap().get("wake").getTime();
        //logger.info(MongoZonedDateTime.getDateFromMongoZonedDateTime(date).toString());


//                > currentWSAL.getLeaveDuration().getNano()

        //logger.info(temperatureService.findNearestTimeSlot(program).getTime().toString());
        //        logger.info(MongoZonedDateTime.getNow().toString());
//        WSAL wsal = new WSAL(true, false, false, false, MongoZonedDateTime.getNow());
//        wsalRepository.deleteAll();
//        wsalRepository.save(wsal);
//
//        logger.info(wsalRepository.findAll().get(0).getCreationDate().toString());
//        Timer timer = new Timer();
//        Calendar cal = Calendar.getInstance();
//        cal.set(Calendar.MINUTE, cal.getTime().getMinutes() + 1);
//
//        Date timeoRun = cal.getTime();
//        logger.info(timeoRun.toString());
//        timer.schedule(new MyTimeTask(), timeoRun);


//        Room room = new Room();
//        room.setIdRoom("room1");
//        room.setDesiredTemperature(15.0);
//        List<ESP8266> esp8266List = new ArrayList<>();
//        ESP8266 esp8266 = new ESP8266();
//        esp8266.setIdEsp("esp1");
//        esp8266.setIdRoom("room1");
//        esp8266.setIsHeater(true);
//        esp8266.setIsSensor(false);
//        esp8266.setHumidity(20.0);
//        esp8266.setTemperature(22.0);
//        esp8266List.add(esp8266);
//        room.setEsp8266List(esp8266List);
//        room.setIsManual(true);
//        roomRepository.save(room);


    }


    private static class MyTimeTask extends TimerTask {
        public void run() {
            System.out.println("Timer Task");
        }
    }


}
