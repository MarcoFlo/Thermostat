package it.polito.thermostat.controllermd.services.logic;

import com.fasterxml.jackson.databind.ObjectMapper;
import it.polito.thermostat.controllermd.entity.Program;
import it.polito.thermostat.controllermd.repository.ProgramRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ResourceUtils;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.Objects;

import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;


@Service
public class JsonHandlerService {
    private Logger logger = LoggerFactory.getLogger(this.getClass());



    @Autowired
    ProgramRepository programRepository;

    @Autowired
    ObjectMapper objectMapper;

    @PostConstruct
    public void init() {
        objectMapper.registerModule(new JavaTimeModule());
    }


    /**
     * Metodo che legge i JSON delle fermate e li salva sul DB
     */
    public void readPrograms() {

        String[] deafultProgramsName = {"winter", "summer"};
        Program program;

        int countProgram = 0;
        try {
            countProgram = Objects.requireNonNull(ResourceUtils.getFile("./defaultPrograms//").list()).length;
            countProgram++;
        } catch (IOException e) {
            e.printStackTrace();
            logger.error("Directory defaultPrograms inesistente");
            System.exit(-1);
        }
        for (int i = 0; i < countProgram - 1; i++) {
            try {
                program = objectMapper.readValue(ResourceUtils.getFile("./defaultPrograms/" + deafultProgramsName[i] + "Default.json"), Program.class);
                programRepository.save(program);
                logger.info(objectMapper.writeValueAsString(program));
            } catch (IOException e) {
                e.printStackTrace();
                logger.error("JsonHandlerService/readPrograms -> File not present");
                System.exit(-1);
            }
        }
    }


}
