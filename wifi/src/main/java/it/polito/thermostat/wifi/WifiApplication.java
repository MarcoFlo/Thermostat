package it.polito.thermostat.wifi;

import it.polito.thermostat.wifi.object.ESP8266;
import it.polito.thermostat.wifi.object.Programm;
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

    /**
     * Contains boot default and roomPrograms
     * @return
     */
    @Bean
    public ConcurrentHashMap<String, Programm> programs() {
        return new ConcurrentHashMap<>();
    }

    public static void main(String[] args) {
        SpringApplication.run(WifiApplication.class, args);
    }

}
