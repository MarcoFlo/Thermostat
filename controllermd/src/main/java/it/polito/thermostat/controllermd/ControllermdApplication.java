package it.polito.thermostat.controllermd;


import com.fasterxml.jackson.databind.ObjectMapper;


import it.polito.thermostat.controllermd.object.SensorData;
import it.polito.thermostat.controllermd.repository.ESP8266Repository;
import it.polito.thermostat.controllermd.repository.ProgramRepository;
import it.polito.thermostat.controllermd.repository.WSALRepository;
import it.polito.thermostat.controllermd.services.logic.JsonHandlerService;
import it.polito.thermostat.controllermd.services.logic.MQTTservice;
import it.polito.thermostat.controllermd.services.logic.ManagerService;
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


import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;


@SpringBootApplication
@EnableScheduling
public class ControllermdApplication implements CommandLineRunner {
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Value("${redis.online}")
    Boolean isRedisOnline;

    @Value("${spring.redis.host}")
    String redisHost;

    @Value("${spring.redis.port}")
    Integer redisPort;

    @Value("${spring.redis.password}")
    String redisPassword;

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

            return new LettuceConnectionFactory(redisStandaloneConfiguration);        }
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
    MQTTservice mqtTservice;


    @Bean
    public ConcurrentHashMap<String, SensorData> sensorData() {
        return new ConcurrentHashMap<>();
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

//        esp8266Repository.deleteAll();
        logger.info(StreamSupport.stream(esp8266Repository.findAll().spliterator(),false).collect(Collectors.toList()).toString());

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
