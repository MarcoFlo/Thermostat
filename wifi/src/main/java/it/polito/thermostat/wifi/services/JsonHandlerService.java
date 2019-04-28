package it.polito.thermostat.wifi.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import it.polito.thermostat.wifi.Object.Mode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.ResourceUtils;

import java.io.IOException;

public class JsonHandlerService {
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    ObjectMapper objectMapper;
    Mode mode;
//TODO add method

        try {
            mode = objectMapper.readValue(ResourceUtils.getFile("classpath:lines/line.json"), Mode.class);
        } catch (IOException e) {
            e.printStackTrace();
        }


}
