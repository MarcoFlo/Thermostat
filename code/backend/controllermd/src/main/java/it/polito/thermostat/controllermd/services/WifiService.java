package it.polito.thermostat.controllermd.services;

import it.polito.thermostat.controllermd.resources.WifiNetResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
public class WifiService {
    private boolean isWindows = System.getProperty("os.name").toLowerCase().startsWith("windows");
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    ExecuteShellComandService execService;
    Boolean wasAP = false;

    /**
     * @return the available net, iterating @count times to return the result with more entry
     */
    public List<WifiNetResource> getAvailableNet() {
        if (!isWindows) {
            Map<Integer, List<String>> mapAvailableNet = new HashMap<>();
            StringBuilder result = new StringBuilder();
            List<String> listNet;

            int count = 0;
            while (count < 3 || mapAvailableNet.keySet().size() == 0) {
                try {
                    result.append(execService.execute("iwlist wlan0 scan | grep ESSID"));
                    listNet = Arrays.asList(result.toString().split("\n"));
                    if (listNet.get(0).length() != 0) {
                        mapAvailableNet.put(listNet.size(), listNet.stream().map(s -> s.substring(s.indexOf("\"") + 1, s.length() - 1)).filter(s -> s.length() < 50).distinct().collect(Collectors.toList()));
                    }
                    result.setLength(0);
                    count++;
                    TimeUnit.MILLISECONDS.sleep(300);

                } catch (InterruptedException e) {
                    logger.error("wifiService/getAvailableNetIterato error\n" + e.toString());
                }
            }
            return mapAvailableNet.get(Collections.max(mapAvailableNet.keySet())).stream().map(essid -> new WifiNetResource(essid, isKnownNet(essid) > -1)).collect(Collectors.toList());
        }
        logger.info("getAvailableNet doesn't work on windows");
        return null;
    }


    /**
     * Allow us to connect to a net
     * If the credentials are wrong and the rpi was an AP, we go back to AP mode
     *
     * @param essid essid to connect
     * @param pw    pw of the ap
     * @return true/false
     */
    public Boolean connectToNet(String essid, String pw) {
        switchToStation();

        if (pw == null) {
            Integer knownNet;
            if ((knownNet = isKnownNet(essid)) != -1) {
                logger.info("Gonna connect to knownNet");
                return connectKnownNet(knownNet);
            } else {
                logger.error("WifiService/conncetToNet it was not a known net");
                return false;
            }
        } else {
            logger.info("Gonna connect to newNet");
            return connectNewNet(essid, pw);
        }
    }

    /**
     * Connect to a net that was not memorized inside wpa_supplicant.conf
     *
     * @param essid
     * @param pw
     * @return true/false
     */
    private boolean connectNewNet(String essid, String pw) {
        if (!isWindows) {
            StringBuilder result = new StringBuilder();
            Integer netNumber;
            result.append(execService.execute("wpa_cli -iwlan0 disconnect | wpa_cli -iwlan0 add_network"));
            netNumber = Integer.valueOf(result.substring(0, result.indexOf("\n")));
            logger.info("new net number in connectNewNet is " + netNumber);
            execService.execute("wpa_cli -iwlan0 set_network " + netNumber + " auth_alg OPEN | wpa_cli -iwlan0 set_network " + netNumber + " key_mgmt WPA-PSK | wpa_cli -iwlan0 set_network " + netNumber + " psk '\"" + pw + "\"' | wpa_cli -iwlan0 set_network " + netNumber + " proto RSN | wpa_cli -iwlan0 set_network " + netNumber + " mode 0 | wpa_cli -iwlan0 set_network " + netNumber + " ssid '\"" + essid + "\"' | wpa_cli -iwlan0 select_network " + netNumber + " | wpa_cli -iwlan0 enable_network " + netNumber + " | wpa_cli -iwlan0 reassociate ");

            return handleConnectResult(netNumber);
        }
        logger.info("connectNewNet doesn't work on windows");
        return false;
    }

    /**
     * Connect to a net that was memorized inside wpa_supplicant.conf
     *
     * @param netNumber
     * @return true/false
     */
    private boolean connectKnownNet(Integer netNumber) {
        if (!isWindows) {
            execService.execute("wpa_cli -iwlan0 disconnect | wpa_cli -iwlan0 select_network " + netNumber + " | wpa_cli -iwlan0 enable_network " + netNumber + " | wpa_cli -iwlan0 reassociate");
            return handleConnectResult(netNumber);
        }
        logger.info("connectKnownNet doesn't work on windows");
        return false;
    }

    /**
     * @param essid
     * @return the netNumber if the net is contained inside wpa_supplicant.conf
     */
    private Integer isKnownNet(String essid) {

        if (!isWindows) {
            StringBuilder result = new StringBuilder();
            result.append(execService.execute("wpa_cli -iwlan0 list_networks | grep " + essid));
            if (result.length() == 0) {
                logger.info("isKnownNet, " + essid + " sconosciuta");
                return -1;
            } else {
                logger.info("isKnownNet, " + essid + " nota");
                return Integer.valueOf(result.substring(0, result.indexOf("\t")));
            }
        }
        logger.info("isKnownNet doesn't work on windows");
        return -1;
    }


    /**
     * @return -1 if we are not connected to any net or the net number
     */
    private Integer getCurrentNetNumber() {
        if (!isWindows) {
            StringBuilder result = new StringBuilder();
            result.append(execService.execute("wpa_cli -iwlan0 list_networks | grep CURRENT"));
            if (result.length() != 0) {
                String[] split = result.toString().split("\t");
                logger.info("getCurrentNetNumber okay");
                return Integer.valueOf(split[0]);
            } else {
                logger.info("getCurrentNetNumber no current net");
                return -1;
            }
        }
        logger.info("getCurrentNetNumber doesn't work on windows");
        return -1;
    }

    /**
     * Switch from station mode to access point mode
     * <p>
     * wpa_cli -iwlan0 disable_network 2
     * echo albertengopi | sudo -S ip link set dev wlan0 down
     * echo albertengopi | sudo -S ip addr add 192.168.4.1/24 dev wlan0
     * echo albertengopi | sudo -S systemctl restart dnsmasq.service
     * echo albertengopi | sudo -S systemctl restart hostapd.service
     */
    public void switchToAP() {
        if (!isWindows) {
            if (isStationMode()) {
                int netNumber = getCurrentNetNumber();
                StringBuilder disable_network = new StringBuilder();

                if (netNumber != -1)
                    disable_network.append("wpa_cli -iwlan0 disable_network " + netNumber + " | ");

                execService.execute(disable_network + "echo albertengopi | sudo -S ip link set dev wlan0 down | echo albertengopi | sudo -S ip addr add 192.168.4.1/24 dev wlan0 | echo albertengopi | sudo -S systemctl restart dnsmasq.service | echo albertengopi | sudo -S systemctl restart hostapd.service");
//                if (!isStationMode())
//                    logger.info("switchToAP okay");
//                else
//                    logger.error("switchToAP error");
                wasAP = true;
                return;
            }
        }
        logger.info("switchToAp doesn't work on windows");
    }

    /**
     * automatic switch to ap if no net
     */
    @Scheduled(fixedRate = 20000, initialDelay = 10000)
    public void switchToApIfNone() {
        if (!isWindows) {
            StringBuilder result = new StringBuilder();
            result.append(execService.execute("wpa_cli -iwlan0 status"));

            if (result.indexOf("ssid") == -1) {
                result.setLength(0);
                result.append(execService.execute("sleep 1.5s | wpa_cli -iwlan0 status"));
                if (result.indexOf("ssid") == -1) {
                    switchToAP();
                    logger.info("switching to ap");
                }
            } else
                logger.info("not switching to ap");

        }
    }

    /**
     * Switch to station mode from access point mdoe
     * <p>
     * echo albertengopi | sudo -S systemctl stop dnsmasq.service
     * echo albertengopi | sudo -S systemctl stop hostapd.service
     * wpa_cli -iwlan0 enable_network 2
     * <p>
     * echo albertengopi | sudo -S ip link set dev wlan0 up
     */
    private void switchToStation() {
        if (!isWindows) {
            if (!isStationMode()) {
                wasAP = true;
                int i = 0;
                StringBuilder result = new StringBuilder();

                execService.execute("echo albertengopi | sudo -S systemctl stop dnsmasq.service | echo albertengopi | sudo -S systemctl stop hostapd.service");

                result.append(execService.execute("echo albertengopi | sudo -S wpa_cli -iwlan0 status"));
                while (result.indexOf("INTERFACE_DISABLED") != -1) {
                    execService.execute("echo albertengopi | sudo -S ip link set dev wlan0 up");
                    i++;
                    logger.info("abbiamo provato a fare la up in switchTOStation " + i + " volte");
                    result.setLength(0);
                    result.append(execService.execute("sleep 0.25s | echo albertengopi | sudo -S wpa_cli -iwlan0 status"));
                }
            } else {
                wasAP = false;
            }
            return;
        }
        logger.info("switchToStation doesn't work on windows");
    }

    /**
     * @return true if station mode
     */
    private Boolean isStationMode() {
        if (!isWindows) {
            StringBuilder result = new StringBuilder();
            result.append(execService.execute("echo albertengopi | sudo -S systemctl status hostapd.service | grep inactive"));
            if (result.length() != 0)
                return true;
            else
                return false;
        }
        logger.info("isStationMode doesn't work on windows");
        return false;
    }

    /**
     * Handle the connection result
     * First of all start the interface
     * <p>
     * ASSOCIATING : still trying, wait a sec
     * INACTIVE SCANNING : credentials were wrong / not connected
     * is present "id=netNumber" : success
     *
     * @param netNumber
     * @return true/false
     */
    private Boolean handleConnectResult(Integer netNumber) {
        if (!isWindows) {

            StringBuilder result = new StringBuilder();
            result.append(execService.execute("echo albertengopi | sudo -S wpa_cli -iwlan0 status"));

            logger.info(result.toString());


            while (result.indexOf("ASSOCIATING") != -1 || result.indexOf("SCANNING") != -1 || result.indexOf("DISCONNECTED") != -1) {
                result.setLength(0);
                result.append(execService.execute("sleep 0.5s | wpa_cli -iwlan0 status"));
            }
            if (result.indexOf("INACTIVE") != -1) {
                execService.execute("wpa_cli -iwlan0 remove_network " + netNumber + " | wpa_cli -i wlan0 reconfigure");
                if (wasAP)
                    switchToAP();
                logger.info("handleConnectResult -> credenziali sbagliate");
                return false;
            }
            if (result.indexOf("ssid") != -1) {
                execService.execute(" wpa_cli -iwlan0 save_config");
                logger.info("handleConnectResult credenziali ok");
                return true;
            }
            logger.info("Unexpected result handleConnectResult" + result.toString());
            return false;
        }
        logger.info("handleConnectResult doesn't work on windows");
        return false;
    }


    public Boolean isInternet() {
        StringBuilder result = new StringBuilder();
        result.append(execService.execute("wpa_cli -iwlan0 status"));
        if (result.indexOf("ssid") != -1) {
            execService.execute(" wpa_cli -iwlan0 save_config");
            return true;
        } else
            return false;

    }

}
