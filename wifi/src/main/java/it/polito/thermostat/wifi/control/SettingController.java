package it.polito.thermostat.wifi.control;

import it.polito.thermostat.wifi.resources.AssociationResource;
import it.polito.thermostat.wifi.resources.WifiNetResource;
import it.polito.thermostat.wifi.services.Esp8266ManagementService;
import it.polito.thermostat.wifi.services.WifiService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class SettingController {
    private Logger logger = LoggerFactory.getLogger(this.getClass());
    @Autowired
    WifiService wifiService;

    @Autowired
    Esp8266ManagementService esp8266ManagementService;


    @PostMapping("/association")
    public void postReservation(@RequestBody AssociationResource associationResource) {
        if (associationResource.getAddBool()) {
            esp8266ManagementService.setAssociation(associationResource.getIdEsp(), associationResource.getIdRoom());
        } else {
            esp8266ManagementService.deleteAssociation(associationResource.getIdEsp());
        }
    }

    @PostMapping("/wifi/credentials")
    public void postWifi(@RequestBody WifiNetResource wifiNetResource) {
        wifiService.connectToNet(wifiNetResource.getNetName(), wifiNetResource.getNetPassword());
    }

    @GetMapping("/wifi/list")
    public List<String> wifiList() {
        //TODO scegliere se questo o "wifiService.getAvailableNet()" che non itera
        return wifiService.getAvailableNetIterator();
    }
}
