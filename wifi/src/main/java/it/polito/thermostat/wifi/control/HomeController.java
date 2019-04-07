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
    public String home(@ModelAttribute("wifiVM") WifiVM wifiVM) {
        logger.info(">" + wifiService.getIP() + "<");
        LinkedList<String> wifiList = wifiService.getAvailableNet();
        wifiVM.setWifiList(wifiList);
        StringBuilder output = new StringBuilder();
        for (String s : wifiList) {
            output.append(s);
        }
        logger.info(output.toString());

        return "home";
    }


    @GetMapping("/new")
    public String newConnection(@ModelAttribute("wifiVM") WifiVM wifiVM) {

        wifiService.connectNewNet();
        return "newConnection";
    }
}
