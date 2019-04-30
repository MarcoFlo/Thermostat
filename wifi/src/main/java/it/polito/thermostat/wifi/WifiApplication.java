package it.polito.thermostat.wifi;

import it.polito.thermostat.wifi.Object.ESP8266;
import it.polito.thermostat.wifi.Object.Mode;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import java.util.concurrent.ConcurrentHashMap;


@SpringBootApplication
public class WifiApplication {


    @Bean
    public ConcurrentHashMap<String, ESP8266> esps() {
        return new ConcurrentHashMap<>();
    }

    @Bean
    public ConcurrentHashMap<String, Mode> modes() {
        return new ConcurrentHashMap<>();
    }

    public static void main(String[] args) {
        SpringApplication.run(WifiApplication.class, args);
    }

}
