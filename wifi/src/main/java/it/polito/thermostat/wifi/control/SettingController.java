package it.polito.thermostat.wifi.control;

import it.polito.thermostat.wifi.DTO.WifiNetDTO;
import it.polito.thermostat.wifi.entity.ESP8266;
import it.polito.thermostat.wifi.entity.program.Program;
import it.polito.thermostat.wifi.repository.ESP8266Repository;
import it.polito.thermostat.wifi.resources.AssociationResource;
import it.polito.thermostat.wifi.resources.WifiNetResource;
import it.polito.thermostat.wifi.services.Esp8266ManagementService;
import it.polito.thermostat.wifi.services.TemperatureService;
import it.polito.thermostat.wifi.services.WifiService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;


/*
 DENIED Redis is running in protected mode because protected mode is enabled, no bind address was specified, no authentication password is requested to clients. In this mode connections are only accepted from the loopback interface.
 If you want to connect from external computers to Redis you may adopt one of the following solutions:
 1) Just disable protected mode sending the command 'CONFIG SET protected-mode no' from the loopback interface by connecting to Redis from the same host the server is running, however MAKE SURE Redis is not publicly accessible from internet if you do so. Use CONFIG REWRITE to make this change permanent.
 2) Alternatively you can just disable the protected mode by editing the Redis configuration file, and setting the protected mode option to 'no', and then restarting the server.
 3) If you started the server manually just for testing, restart it with the '--protected-mode no' option.
 4) Setup a bind address or an authentication password. NOTE: You only need to do one of the above things in order for the server to start accepting connections from the outside.

 */
@RestController
public class SettingController {
    private Logger logger = LoggerFactory.getLogger(this.getClass());
    private boolean isWindows = System.getProperty("os.name").toLowerCase().startsWith("windows");

    @Autowired
    WifiService wifiService;

    @Autowired
    Esp8266ManagementService esp8266ManagementService;

    @Autowired
    TemperatureService temperatureService;

    @Autowired
    ESP8266Repository esp8266Repository;
//
//    /**
//     * Retrive a list of free esp
//     *
//     * @return
//     */
//    @GetMapping("/setting/espfree")
//    public List<ESP8266> getEspFree() {
//        List<ESP8266> list = esp8266ManagementService.getEspFree();
//        logger.info("ciao");
//        logger.info(list.size() + "size" + list.get(0).getIdEsp());
//        list.forEach(esp -> logger.info(list.size() + "\t" + esp.getIdEsp() + "\t" + esp.getIdRoom()));
//        return list;
//    }
//
//    /**
//     * Endpoint that allow us to set/delete associations between room-esp
//     *
//     * @param associationList
//     */
//    @PostMapping("/setting/association")
//    public void postAssociation(@RequestBody List<AssociationResource> associationList) {
//        Iterator<AssociationResource> iterator = associationList.iterator();
//        while (iterator.hasNext()) {
//            AssociationResource associationResource = iterator.next();
//            if (associationResource.getAddBool()) {
//                esp8266ManagementService.setAssociation(associationResource.getIdEsp(), associationResource.getIdRoom());
//            } else {
//                esp8266ManagementService.deleteAssociation(associationResource.getIdEsp());
//            }
//        }
//    }
//
//    /**
//     * Endpoint to get the default room setting
//     */
//    @GetMapping("/setting/default_program")
//    public Program getDefaultProgram() {
//        return temperatureService.getDefaultProgram();
//    }
//
//    /**
//     * Endpoint to save the new program
//     *
//     * @param program
//     */
//    @PostMapping("/setting/program")
//    public void postProgram(@RequestBody Program program) {
//        temperatureService.saveProgram(program);
//    }
//
//
//    /**
//     * Endpoint that allow us to get the list of available net
//     *
//     * @return
//     */
//    @GetMapping("/setting/wifi/list")
//    public List<WifiNetDTO> wifiList() {
//        if (isWindows)
//            return Arrays.asList(new WifiNetDTO("NewIpNetwork", false), new WifiNetDTO("KnownIpNetwork", true));
//        else
//            return wifiService.getAvailableNet();
//    }
//
//    /**
//     * Endpoint that allow us to connect to a net
//     * Send the netPassword == null to connect to a known net
//     *
//     * @param wifiNetResource
//     */
//    @PostMapping("/setting/wifi/credentials")
//    public void postWifi(@RequestBody WifiNetResource wifiNetResource) {
//        if (!isWindows)
//            wifiService.connectToNet(wifiNetResource.getEssid(), wifiNetResource.getNetPassword());
//    }
//
//    @GetMapping("/device_discovery")
//    public String ping() {
//        return "iamrpi";
//    }


}
