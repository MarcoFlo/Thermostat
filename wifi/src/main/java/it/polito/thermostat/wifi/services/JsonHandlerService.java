package it.polito.thermostat.wifi.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import it.polito.thermostat.wifi.object.Programm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ResourceUtils;
import java.io.IOException;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class JsonHandlerService {
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    ObjectMapper objectMapper;

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
        for (int i = 0; i < countProgram-1; i++) {
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
