package it.polito.thermostat.controllermd;


import com.fasterxml.jackson.databind.ObjectMapper;


import it.polito.thermostat.controllermd.object.MongoZonedDateTime;
import it.polito.thermostat.controllermd.object.SensorData;
import it.polito.thermostat.controllermd.repository.ProgramRepository;
import it.polito.thermostat.controllermd.repository.RoomRepository;
import it.polito.thermostat.controllermd.repository.WSALRepository;
import it.polito.thermostat.controllermd.services.JsonHandlerService;
import it.polito.thermostat.controllermd.services.MQTTservice;
import it.polito.thermostat.controllermd.services.TemperatureService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.core.task.TaskExecutor;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;


import javax.annotation.PostConstruct;


import java.time.LocalDateTime;
import java.util.Date;
import java.util.concurrent.ConcurrentHashMap;


@SpringBootApplication
@EnableScheduling
public class ControllermdApplication implements CommandLineRunner {
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Bean
    public LettuceConnectionFactory redisConnectionFactory() {
        return new LettuceConnectionFactory();
    }

    @Bean
    public RedisTemplate<?, ?> redisTemplate() {
        RedisTemplate<byte[], byte[]> template = new RedisTemplate<>();
        template.setConnectionFactory(redisConnectionFactory());
        return template;
    }

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

    @Autowired
    MQTTservice mqtTservice;


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


//        Program program = programRepository.findByIdProgram("winter").get();



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


}
