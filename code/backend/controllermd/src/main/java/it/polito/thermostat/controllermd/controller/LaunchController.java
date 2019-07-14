package it.polito.thermostat.controllermd.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class LaunchController {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    /**
     * @return the index.html
     */
    @GetMapping("/")
    public String getIndex() {
        logger.info("page index.html requested");
        return "index";
    }
}
