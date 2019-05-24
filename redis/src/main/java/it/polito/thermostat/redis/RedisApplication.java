package it.polito.thermostat.redis;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories;

import java.time.LocalDateTime;

@SpringBootApplication
@EnableRedisRepositories
public class RedisApplication implements CommandLineRunner {
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
    UserRepository userRepository;

    public static void main(String[] args) {
        SpringApplication.run(RedisApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {

        UserEntity n = new UserEntity();
        n.setName("marco");
        n.setEsame(new Esame("Filosofia"));
        n.setLocalDateTime(LocalDateTime.now());
        userRepository.save(n);

        logger.info(userRepository.findByName("marco").getLocalDateTime().toString());
        logger.info(userRepository.findByName("marco").getEsame().toString());

    }


}