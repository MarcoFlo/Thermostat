package it.polito.thermostat.controllermd;


import it.polito.thermostat.controllermd.repository.ProgrammRepository;
import it.polito.thermostat.controllermd.services.JsonHandlerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
import javax.annotation.PostConstruct;
import java.util.*;


@SpringBootApplication
@EnableScheduling
public class ControllermdApplication implements CommandLineRunner {
    private Logger logger = LoggerFactory.getLogger(this.getClass());


    @Autowired
    ProgrammRepository programmRepository;

    @Autowired
    JsonHandlerService jsonHandlerService;


    @PostConstruct
    public void init() {
        logger.info("Loading default programs....");
        jsonHandlerService.readProgramms();
        logger.info("Loading default programs done");

    }

    public static void main(String[] args) {
        SpringApplication.run(ControllermdApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        logger.info(programmRepository.findByIdProgramm("winter").get().getIdProgramm());

        Timer timer = new Timer();
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.MINUTE,cal.getTime().getMinutes()+1);

        Date timeoRun = cal.getTime();
        logger.info(timeoRun.toString());
        timer.schedule(new  MyTimeTask(), timeoRun);

        List<Integer> integerList = new ArrayList<>();
        integerList.add(1);
        integerList.add(2);
        integerList.add(3);
        integerList.add(4);
        integerList.add(5);
integerList.stream().filter(n -> n != 1).forEach(num -> logger.info(num + " "));
    }



    private static class MyTimeTask extends TimerTask {
        public void run() {
            System.out.println("Timer Task");
        }
    }
}
