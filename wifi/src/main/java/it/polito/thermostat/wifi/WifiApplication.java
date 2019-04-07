package it.polito.thermostat.wifi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.concurrent.ConcurrentHashMap;

@SpringBootApplication
public class WifiApplication {

    /*
    @Bean
    public ConcurrentHashMap<String, User> users() {
        return new ConcurrentHashMap<>();
    }
*/

    public static void main(String[] args) {
        SpringApplication.run(WifiApplication.class, args);
    }

}
