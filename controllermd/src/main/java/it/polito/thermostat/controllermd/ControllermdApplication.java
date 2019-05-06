package it.polito.thermostat.controllermd;

import it.polito.thermostat.controllermd.object.ESP8266;
import it.polito.thermostat.controllermd.object.Programm;
import it.polito.thermostat.controllermd.repository.ProgrammRepository;
import it.polito.thermostat.controllermd.services.JsonHandlerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import javax.annotation.PostConstruct;
import java.util.concurrent.ConcurrentHashMap;

@SpringBootApplication
public class ControllermdApplication implements CommandLineRunner {
    private Logger logger = LoggerFactory.getLogger(this.getClass());


    @Autowired
    ProgrammRepository programmRepository;

    @Autowired
    JsonHandlerService jsonHandlerService;

    @Autowired
    private ConcurrentHashMap<String, Programm> programsMap;

    /**
     * Contains boot default and roomPrograms
     * @return
     */
    @Bean
    public ConcurrentHashMap<String, Programm> programs() {
        return new ConcurrentHashMap<>();
    }

    @Bean
    public ConcurrentHashMap<String, ESP8266> esps() {
        return new ConcurrentHashMap<>();
    }



    @PostConstruct
    public void init() {

        jsonHandlerService.readProgramms();

    }

    public static void main(String[] args) {
        SpringApplication.run(ControllermdApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {

        programmRepository.deleteAll();
        programmRepository.save(programsMap.get("winter"));
        logger.info(programmRepository.findByIdProgramm("winter").get().getIdProgramm());


    }

}
