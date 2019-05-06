package it.polito.thermostat.controllermd.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import it.polito.thermostat.controllermd.object.Programm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ResourceUtils;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;


@Service
public class JsonHandlerService {
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    ObjectMapper objectMapper;

    @PostConstruct
    public void init() {
        objectMapper.registerModule(new JavaTimeModule());
    }

    /**
     * Metodo che legge i JSON delle fermate e li salva sul DB
     */
    @Autowired
    private ConcurrentHashMap<String, Programm> programsMap;

    public void readProgramms() {

        String[] deafultProgramsName = {"winter", "summer"};
        Programm programm;

        int countProgram = 0;
        try {
            countProgram = Objects.requireNonNull(ResourceUtils.getFile("classpath:defaultPrograms//").list()).length;
            countProgram++;
        } catch (IOException e) {
            e.printStackTrace();
            logger.error("Directory defaultPrograms inesistente");
            System.exit(-1);
        }
        for (int i = 0; i < countProgram - 1; i++) {
            try {
                programm = objectMapper.readValue(ResourceUtils.getFile("classpath:defaultPrograms/" + deafultProgramsName[i] + "Default.json"), Programm.class);
                programsMap.put(programm.getIdProgramm(), programm);
                logger.info(programm.toString());
            } catch (IOException e) {
                e.printStackTrace();
                logger.error("JsonHandlerService/readProgramms -> File not present");
                System.exit(-1);
            }
        }
    }
}
