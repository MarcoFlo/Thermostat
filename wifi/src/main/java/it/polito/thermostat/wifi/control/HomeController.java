package it.polito.thermostat.wifi.control;

import it.polito.thermostat.wifi.services.WifiService;
import it.polito.thermostat.wifi.viewModel.WifiVM;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Controller
public class HomeController {
    private Logger logger = LoggerFactory.getLogger(this.getClass());
    @Autowired
    WifiService wifiService;

    /**
     * Mapping verso la home dell'applicazione
     *
     * @param wifiVM wifidata
     * @return String
     */
    @GetMapping("/")
    public String home(@ModelAttribute("wifiVM") WifiVM wifiVM) throws InterruptedException {
        logger.info("myIP ->" + wifiService.getIP() + "<");


        wifiService.switchToAP();

        wifiVM.setWifiList(wifiService.getAvailableNet());

        TimeUnit.SECONDS.sleep(5);


//      logger.info("New connection to hotspot -> " +wifiService.connectToNet("TISCALI-Moschettieri", "Ciao33trentini!"));

       logger.info("New connection to hotspot -> " +wifiService.connectToNet("AndroidMA2", "montagna"));
        return "home";
    }
}
