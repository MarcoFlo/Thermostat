package it.polito.thermostat.controllermd;


import com.fasterxml.jackson.databind.ObjectMapper;


import it.polito.thermostat.controllermd.entity.ESP8266;
import it.polito.thermostat.controllermd.entity.Program;
import it.polito.thermostat.controllermd.entity.Room;
import it.polito.thermostat.controllermd.repository.ESP8266Repository;
import it.polito.thermostat.controllermd.repository.ProgramRepository;
import it.polito.thermostat.controllermd.repository.RoomRepository;
import it.polito.thermostat.controllermd.repository.WSALRepository;
import it.polito.thermostat.controllermd.services.logic.JsonHandlerService;
import it.polito.thermostat.controllermd.services.logic.MQTTservice;
import it.polito.thermostat.controllermd.services.logic.ManagerService;
import it.polito.thermostat.controllermd.services.logic.StatService;
import it.polito.thermostat.controllermd.services.server.SettingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.connection.RedisPassword;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.EnableScheduling;


import javax.annotation.PostConstruct;
import java.util.Arrays;


@SpringBootApplication
@EnableScheduling
public class ControllermdApplication implements CommandLineRunner {
    private Logger logger = LoggerFactory.getLogger(this.getClass());


    @Value("${main.room.name}")
    String mainRoomName;
    @Value("${main.room.sensor}")
    String mainRoomSensor;
    @Value("${main.room.actuator.cooler}")
    String mainRoomCooler;
    @Value("${main.room.actuator.heater}")
    String mainRoomHeater;

    @Value("${redis.online}")
    Boolean isRedisOnline;

    @Value("${spring.redis.host}")
    String redisHost;

    @Value("${spring.redis.port}")
    Integer redisPort;

    @Value("${spring.redis.pass}")
    String redisPassword;


    @Autowired
    JsonHandlerService jsonHandlerService;

    @Autowired
    ESP8266Repository esp8266Repository;

    @Autowired
    ProgramRepository programRepository;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    WSALRepository wsalRepository;

    @Autowired
    ManagerService managerService;

    @Autowired
    SettingService settingService;

    @Autowired
    MQTTservice mqtTservice;

    @Autowired
    StatService statService;

    @Autowired
    RoomRepository roomRepository;

    @Bean
    public LettuceConnectionFactory redisConnectionFactory() {
        if (isRedisOnline) {
            logger.info("Redis online database");
            RedisStandaloneConfiguration redisStandaloneConfiguration = new RedisStandaloneConfiguration(redisHost, redisPort);
            redisStandaloneConfiguration.setPassword(RedisPassword.of(redisPassword));
            return new LettuceConnectionFactory(redisStandaloneConfiguration);

        } else {
            logger.info("Redis local database");
            RedisStandaloneConfiguration redisStandaloneConfiguration = new RedisStandaloneConfiguration("192.168.1.127");

            return new LettuceConnectionFactory(redisStandaloneConfiguration);
        }
    }


    @Bean
    public RedisTemplate<?, ?> redisTemplate() {
        RedisTemplate<byte[], byte[]> template = new RedisTemplate<>();
        template.setConnectionFactory(redisConnectionFactory());
        return template;
    }


    @PostConstruct
    public void init() {
        logger.info("Loading default programs....");
        jsonHandlerService.readPrograms();
        logger.info("Loading default programs done");
    }

    public static void main(String[] args) {
        SpringApplication.run(ControllermdApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
//        roomRepository.deleteAll();
//        programRepository.deleteAll();
//        esp8266Repository.deleteAll();
        //Main room creation
        roomRepository.save(new Room(mainRoomName, Arrays.asList(mainRoomSensor, mainRoomCooler, mainRoomHeater), false, -1.0));

        //Main room program creation
        Program program = settingService.getDefaultProgram();
        program.setIdProgram(mainRoomName);
        programRepository.save(program);

        //Main Room esp creation
        esp8266Repository.save(new ESP8266(mainRoomSensor, mainRoomName, true, false));
        esp8266Repository.save(new ESP8266(mainRoomCooler, mainRoomName, false, true));
        esp8266Repository.save(new ESP8266(mainRoomHeater, mainRoomName, false, false));
        mqtTservice.subscribeSensor(mainRoomSensor);
        logger.info("Main room saved");


//        statService.buildNewDataSet();
        logger.info("new data set built");

    }


}
