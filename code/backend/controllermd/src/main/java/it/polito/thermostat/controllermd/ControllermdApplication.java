package it.polito.thermostat.controllermd;


import com.fasterxml.jackson.databind.ObjectMapper;


import it.polito.thermostat.controllermd.configuration.HostAddressGetter;
import it.polito.thermostat.controllermd.entity.ESP8266;
import it.polito.thermostat.controllermd.entity.Program;
import it.polito.thermostat.controllermd.entity.Room;
import it.polito.thermostat.controllermd.repository.ProgramRepository;
import it.polito.thermostat.controllermd.repository.ESP8266Repository;
import it.polito.thermostat.controllermd.repository.RoomRepository;
import it.polito.thermostat.controllermd.repository.WSALRepository;
import it.polito.thermostat.controllermd.services.JsonHandlerService;
import it.polito.thermostat.controllermd.services.MQTTservice;
import it.polito.thermostat.controllermd.services.ManagerService;
import it.polito.thermostat.controllermd.services.StatService;
import it.polito.thermostat.controllermd.services.SettingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.core.task.TaskExecutor;
import org.springframework.data.redis.connection.RedisPassword;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;


import javax.annotation.PostConstruct;
import java.util.Arrays;


@SpringBootApplication
@EnableScheduling
@EnableAsync
public class ControllermdApplication implements CommandLineRunner {
    private Logger logger = LoggerFactory.getLogger(this.getClass());
    private boolean isWindows = System.getProperty("os.name").toLowerCase().startsWith("windows");


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


    @Autowired
    ProgramRepository programRepository;
    @Bean
    public LettuceConnectionFactory redisConnectionFactory() {
        if (isWindows) {
            logger.info("Redis online database");
            RedisStandaloneConfiguration redisStandaloneConfiguration = new RedisStandaloneConfiguration(redisHost, redisPort);
            redisStandaloneConfiguration.setPassword(RedisPassword.of(redisPassword));
            return new LettuceConnectionFactory(redisStandaloneConfiguration);

        } else {
            logger.info("Redis local database");
            RedisStandaloneConfiguration redisStandaloneConfiguration = new RedisStandaloneConfiguration("localhost");

            return new LettuceConnectionFactory(redisStandaloneConfiguration);
        }
    }


    @Bean
    public RedisTemplate<?, ?> redisTemplate() {
        RedisTemplate<byte[], byte[]> template = new RedisTemplate<>();
        template.setConnectionFactory(redisConnectionFactory());
        return template;
    }

    @Bean("threadPoolTaskExecutor")
    public TaskExecutor getAsyncExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(20);
        executor.setMaxPoolSize(1000);
        executor.setWaitForTasksToCompleteOnShutdown(true);
        executor.setThreadNamePrefix("Async-");
        return executor;
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
        //Main room creation
        if (!roomRepository.findById(mainRoomName).isPresent()) {
            roomRepository.save(new Room(mainRoomName, Arrays.asList(mainRoomSensor, mainRoomCooler, mainRoomHeater), false, 25.0));

            //Main room program creation
            Program program = settingService.getDefaultProgram();
            program.setIdProgram(mainRoomName);
            programRepository.save(program);
        }
        //Main Room esp creation
        esp8266Repository.save(new ESP8266(mainRoomSensor, mainRoomName, true, false));
        esp8266Repository.save(new ESP8266(mainRoomCooler, mainRoomName, false, true));
        esp8266Repository.save(new ESP8266(mainRoomHeater, mainRoomName, false, false));
        mqtTservice.subscribeSensor(mainRoomSensor);
        logger.info("Main room saved");

        mqtTservice.subscribeToPresentEsp();

        logger.info(System.getProperty("os.name"));
    }


}
