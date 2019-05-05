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
    private ConcurrentHashMap<String, Programm> defaultProgramsMap;

    public void readProgramms() {
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
        for (int i = 1; i < countProgram; i++) {
            try {
                programm = objectMapper.readValue(ResourceUtils.getFile("classpath:defaultPrograms/invernoDefault.json"), Programm.class);
                defaultProgramsMap.put(programm.getIdProgramm(), programm);
                logger.info(programm.toString());
            } catch (IOException e) {
                e.printStackTrace();
                logger.error("JsonHandlerService/readProgramms -> File not present");
                System.exit(-1);
            }
        }
    }
}
