package it.polito.thermostat.wifi.control;

import it.polito.thermostat.wifi.view.FormUserLogin;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;

@Controller
public class HomeController {


    /**
     * Mapping verso la home dell'applicazione
     *
     * @param uvm wifidata
     * @return String
     */
    @GetMapping("/")
    public String home(@ModelAttribute("wifidata") FormUserLogin uvm) {

        return "home";
    }

}
