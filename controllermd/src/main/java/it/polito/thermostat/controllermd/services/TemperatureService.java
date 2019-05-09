package it.polito.thermostat.controllermd.services;

import it.polito.thermostat.controllermd.repository.ProgrammRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.Timer;

@Service
public class TemperatureService {
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    MQTTservice mqttService;

    @Autowired
    ProgrammRepository programmRepository;

    @Scheduled(fixedRate = 10000)
    public void scheduleFixedRateTask() {
        logger.info("Fixed rate task - " + System.currentTimeMillis() / 10000);

        //list programm
        programmRepository.findAll().stream().filter(programm -> !programm.getIdProgramm().equals("winter") || !programm.getIdProgramm().equals("summer"));


    }

}
