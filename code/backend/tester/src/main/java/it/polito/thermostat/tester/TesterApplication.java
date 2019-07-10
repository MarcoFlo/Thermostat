package it.polito.thermostat.tester;

import com.fasterxml.jackson.databind.ObjectMapper;
import it.polito.thermostat.tester.serviceTest.TestLauncher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;
import org.springframework.data.redis.connection.RedisPassword;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;

import java.util.concurrent.Executor;


@SpringBootApplication
@EnableScheduling
public class TesterApplication implements CommandLineRunner {
    private Logger logger = LoggerFactory.getLogger(this.getClass());
    private boolean isWindows = System.getProperty("os.name").toLowerCase().startsWith("windows");


    @Value("${redis.online}")
    Boolean isRedisOnline;

    @Value("${spring.redis.host}")
    String redisHost;

    @Value("${spring.redis.port}")
    Integer redisPort;

    @Value("${spring.redis.password}")
    String redisPassword;

    @Bean
    public ObjectMapper mapper() {
        return new ObjectMapper();
    }

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



    @Autowired
    TestLauncher testLauncher;

    public static void main(String[] args) {
        SpringApplication.run(TesterApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        testLauncher.launchAll();
    }
}
