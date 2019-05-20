package it.polito.thermostat.wifi.control;

import it.polito.thermostat.wifi.services.*;
import it.polito.thermostat.wifi.viewModel.WifiVM;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Controller
public class HomeController {
    private Logger logger = LoggerFactory.getLogger(this.getClass());
    @Autowired
    WifiService wifiService;


    @GetMapping("/wifi")
    public String wifiDebug(@ModelAttribute("wifiVM") WifiVM wifiVM) throws InterruptedException {
        logger.info("myIP ->" + wifiService.getIP() + "<");


        wifiService.switchToAP();

        TimeUnit.SECONDS.sleep(5);

        wifiVM.setWifiList(wifiService.getAvailableNet().stream().map(wifiNetDTO -> wifiNetDTO.getEssid()).collect(Collectors.toList()));
        TimeUnit.SECONDS.sleep(1);
        wifiVM.setWifiListIterator(wifiService.getAvailableNetIterator().stream().map(wifiNetDTO -> wifiNetDTO.getEssid()).collect(Collectors.toList()));

        TimeUnit.SECONDS.sleep(5);

        logger.info("New connection to hotspot -> " + wifiService.connectToNet("TISCALI-Moschettieri", "Ciao33trentini!"));

        logger.info("New connection to hotspot -> " + wifiService.connectToNet("AndroidMA2", "montagna"));
        return "home";
    }




}
