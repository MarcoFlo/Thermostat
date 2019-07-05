package it.polito.thermostat.controllermd.controller;

import it.polito.thermostat.controllermd.entity.Program;
import it.polito.thermostat.controllermd.entity.Room;
import it.polito.thermostat.controllermd.repository.ESP8266Repository;
import it.polito.thermostat.controllermd.resources.AssociationResource;
import it.polito.thermostat.controllermd.resources.RoomResource;
import it.polito.thermostat.controllermd.resources.StatsResource;
import it.polito.thermostat.controllermd.resources.WifiNetResource;
import it.polito.thermostat.controllermd.services.logic.StatService;
import it.polito.thermostat.controllermd.services.server.Esp8266ManagementService;
import it.polito.thermostat.controllermd.services.server.SettingService;
import it.polito.thermostat.controllermd.services.server.TemperatureService;
import it.polito.thermostat.controllermd.services.server.WifiService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;


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

    @Autowired
    SettingService settingService;

    @Autowired
    StatService statService;

    /**
     * @return a list of free esp
     */
    @GetMapping("/setting/esp/free")
    public List<String> getEspFree() {
        logger.info("I'm gonna return the list of free esp");
        return esp8266ManagementService.getEspFree();
    }


    /**
     * Endpoint to save a room
     *
     * @param roomResource
     */
    @PostMapping("/setting/room/resource")
    public void postRoom(@RequestBody RoomResource roomResource) {
        logger.info("Room " + roomResource.getIdRoom() + " will be saved");
//        logger.info(roomResource.toString());
        settingService.saveRoomResource(roomResource);
    }

    @GetMapping("/setting/room/resource/{idRoom}")
    public RoomResource getRoomResource(@PathVariable("idRoom") String idRoom) {
        logger.info("I'm gonna retrive the roomResource for " + idRoom);
        RoomResource roomResource = settingService.getRoomResource(idRoom);
//        logger.info(roomResource.toString());
        return roomResource;
    }

    /**
     * Endpoint to delete a room
     *
     * @param idRoom
     */
    @DeleteMapping("/setting/room")
    public void postRoom(@RequestBody String idRoom) {
        settingService.deleteRoom(idRoom);
    }

    /**
     * List of saved room
     *
     * @return
     */
    @GetMapping("/setting/room/list")
    public List<String> getListRoom() {
        return settingService.getListRoom();
    }


    /**
     * @return the default room setting
     */
    @GetMapping("/setting/default_program")
    public Program getDefaultProgram() {
        logger.info("/setting/default_program contacted");
        return settingService.getDefaultProgram();
    }

    @GetMapping("/setting/program/{idRoom}")
    public Program getProgram(@PathVariable("idRoom") String idRoom) {
        logger.info("I'm trying to retrive a program for " + idRoom);
        Program result = settingService.getProgramRoom(idRoom);
        return result;
    }

    /**
     * @return list of available net
     */
    @GetMapping("/setting/wifi/list")
    public List<WifiNetResource> wifiList() {
        if (isWindows)
            return Arrays.asList(new WifiNetResource("NewIpNetworkName", false), new WifiNetResource("KnownIpNetworkName", true));
        else
            return wifiService.getAvailableNet();
    }

    /**
     * Endpoint that allow us to connect to a net
     * Send the netPassword == null to connect to a known net
     *
     * @param wifiNetResource net credentials
     */
    @PostMapping("/setting/wifi/credentials")
    public void postWifi(@RequestBody WifiNetResource wifiNetResource) {
        logger.info("I'm gonna connect to this net -> " + wifiNetResource.toString());
        if (!isWindows)
            wifiService.connectToNet(wifiNetResource.getEssid(), wifiNetResource.getNetPassword());
        else
            logger.info("This operation is not available on windows");
    }

    /**
     * Endpoint for the device discovery
     *
     * @return "iamrpi" when contacted
     */
    @GetMapping("/setting/device_discovery")
    public String ping() {
        logger.info("Just received a ping, I'm gonna respond >iamrpi<");
        return "iamrpi";
    }


    @GetMapping("/setting/stats/{idRoom}")
    public StatsResource getStatsResourceRoom(@PathVariable("idRoom") String idRoom) {
        logger.info("get /setting/stats/" + idRoom + " contacted");
        return statService.getweeklyStats(idRoom);
    }
}
