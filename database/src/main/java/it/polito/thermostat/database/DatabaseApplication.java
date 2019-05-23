package it.polito.thermostat.database;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.time.LocalDateTime;

@SpringBootApplication
public class DatabaseApplication implements CommandLineRunner {
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    UserRepository userRepository;

    public static void main(String[] args) {
        SpringApplication.run(DatabaseApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {

        UserEntity n = new UserEntity();
        n.setName("marco");
        n.setLocalDateTime(LocalDateTime.now());
        userRepository.save(n);

        logger.info(userRepository.findByName("marco").getLocalDateTime().toString());
    }


}
