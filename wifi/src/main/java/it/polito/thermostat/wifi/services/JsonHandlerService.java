package it.polito.thermostat.wifi.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import it.polito.thermostat.wifi.Object.Mode;
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
    private ConcurrentHashMap<String, Mode> modesMap;

    public void readMode() {
        Mode mode;

        int countMode = 0;
        try {
            countMode = Objects.requireNonNull(ResourceUtils.getFile("classpath:modes//").list()).length;
            countMode++;
        } catch (IOException e) {
            e.printStackTrace();
            logger.error("Directory mode inesistente");
            System.exit(-1);
        }
        for (int i = 1; i < countMode; i++) {
            try {
                mode = objectMapper.readValue(ResourceUtils.getFile("classpath:modes/inverno.json"), Mode.class);
                modesMap.put(mode.getNome(), mode);
                logger.info(mode.toString());
            } catch (IOException e) {
                e.printStackTrace();
                logger.error("JsonHandlerService/readMode -> File not present");
                System.exit(-1);
            }
        }
    }
}
