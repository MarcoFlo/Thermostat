package it.polito.thermostat.wifi.control;

import it.polito.thermostat.wifi.DTO.WifiNetDTO;
import it.polito.thermostat.wifi.entity.program.Program;
import it.polito.thermostat.wifi.resources.AssociationResource;
import it.polito.thermostat.wifi.resources.WifiNetResource;
import it.polito.thermostat.wifi.services.Esp8266ManagementService;
import it.polito.thermostat.wifi.services.TemperatureService;
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

    @Autowired
    TemperatureService temperatureService;

    /**
     * Endpoint that allow us to set/delete an association between room-esp
      * @param associationResource
     */
    @PostMapping("/association")
    public void postReservation(@RequestBody AssociationResource associationResource) {
        if (associationResource.getAddBool()) {
            esp8266ManagementService.setAssociation(associationResource.getIdEsp(), associationResource.getIdRoom());
        } else {
            esp8266ManagementService.deleteAssociation(associationResource.getIdEsp());
        }
    }

    /**
     * Endpoint to save the new room setting
     * @param program
     */
    @PostMapping("/room_setting")
    public void postProgram(@RequestBody Program program) {
        temperatureService.saveProgram(program);
    }

    /**
     * Endpoint to get the default room setting
     */
    @PostMapping("/room_setting")
    public Program getDefaultProgram() {
        return temperatureService.getDefaultProgram();
    }

    /**
     * Endpoint that allow us to connect to a net
     * Send the netPassword == null to connect to a known net
     * @param wifiNetResource
     */
    @PostMapping("/wifi/credentials")
    public void postWifi(@RequestBody WifiNetResource wifiNetResource) {
        wifiService.connectToNet(wifiNetResource.getEssid(), wifiNetResource.getNetPassword());
    }

    /**
     * Endpoint that allow us to get the list of available net
     * @return
     */
    @GetMapping("/wifi/list")
    public List<WifiNetDTO> wifiList() {
        //TODO scegliere se questo o "wifiService.getAvailableNet()" che non itera
        return wifiService.getAvailableNet();
    }
}
