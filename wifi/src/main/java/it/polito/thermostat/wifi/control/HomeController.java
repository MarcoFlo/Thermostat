package it.polito.thermostat.wifi.control;

import it.polito.thermostat.wifi.services.*;
import it.polito.thermostat.wifi.viewModel.WifiVM;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.annotation.PostConstruct;
import java.util.concurrent.TimeUnit;

@Controller
public class HomeController {
    private Logger logger = LoggerFactory.getLogger(this.getClass());
    @Autowired
    WifiService wifiService;

    @Autowired
    MQTTservice mqttService;

    @Autowired
    JsonHandlerService jsonHandlerService;

    @Autowired
    Esp8266ManagementService esp8266ManagementService;

    @Autowired
    TemperatureService temperatureService;

    /**
     * Metodo eseguito all'avvio della classe come init
     */
    @PostConstruct
    public void init() {
        logger.info("Programs loading....");
        jsonHandlerService.readProgramms();
        logger.info("Programs loaded!");
    }


    /**
     * Mapping verso la home dell'applicazione
     *
     * @param wifiVM wifidata
     * @return String
     */
    @GetMapping("/wifi")
    public String wifiDebug(@ModelAttribute("wifiVM") WifiVM wifiVM) throws InterruptedException {
        logger.info("myIP ->" + wifiService.getIP() + "<");


        wifiService.switchToAP();

        TimeUnit.SECONDS.sleep(5);

        wifiVM.setWifiList(wifiService.getAvailableNet());
        TimeUnit.SECONDS.sleep(1);
        wifiVM.setWifiListIterator(wifiService.getAvailableNetIterator());

        TimeUnit.SECONDS.sleep(5);

        logger.info("New connection to hotspot -> " + wifiService.connectToNet("TISCALI-Moschettieri", "Ciao33trentini!"));

        logger.info("New connection to hotspot -> " + wifiService.connectToNet("AndroidMA2", "montagna"));
        return "home";
    }



    @PostMapping("/association/{id_esp}")
    public String postReservation(@RequestBody String idRoom, @PathVariable("id_esp") String idEsp) {
        esp8266ManagementService.setAssociation(idRoom, idEsp);
        return "registered association";
    }

    @GetMapping("/appmio")
    public String propertiesDebug() {

        //temperatureService.setProgrammedMode();
        return "mqtt";
    }

}
