package it.polito.thermostat.wifi;

import it.polito.thermostat.wifi.entity.ESP8266;
import it.polito.thermostat.wifi.repository.ESP8266Repository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.domain.Example;
import org.springframework.data.redis.connection.RedisPassword;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;


@SpringBootApplication
@EnableScheduling
public class WifiApplication implements CommandLineRunner {
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
    ESP8266Repository esp8266Repository;

    @PostConstruct
    public void init() {
        logger.info("Loading default programs....");
//        ESP8266  esp8266 = new ESP8266();
//        esp8266.setIdEsp("idTest1000");
//        esp8266.setIsCooler(true);
//        esp8266.setIsSensor(false);
//
//esp8266Repository.save(esp8266);
    logger.info("Loading default programs done");
    }

    public static void main(String[] args) {
        SpringApplication.run(WifiApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        logger.info(StreamSupport.stream(esp8266Repository.findAll().spliterator(),false).collect(Collectors.toList()).toString());


//        List<ESP8266> list = StreamSupport.stream(esp8266Repository.findAll().spliterator(),false).collect(Collectors.toList());
//        logger.info(list.toString());
//        list.stream().forEach(
//                esp -> {
//                    if (esp != null)
//                    logger.info(esp.getIdEsp());
//
//                }
//        );
    }
}
