package it.polito.thermostat.controllermd.services.server;

import it.polito.thermostat.controllermd.configuration.exception.WifiCredentialsException;
import it.polito.thermostat.controllermd.resources.WifiNetResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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
    Boolean wasAP;

    /**
     * @return the available net iterating @count times to check the result with more results
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
     * If the credentials are wrong and wasAP == true we go back to AP mode, so that the esp have a neto una loro rete
     *
     * @param essid essid to connect
     * @param pw    pw of the ap
     * @return debugging string
     */
    public String connectToNet(String essid, String pw) {
        switchToStation();

        if (pw == null) {
            Integer knownNet;
            if ((knownNet = isKnownNet(essid)) != -1) {
                if (connectKnownNet(knownNet)) {
                    logger.info("ConnectKnownNet done");
                }
            } else {
                logger.error("WifiService/conncetToNet it was not a known net");
            }
        } else {
            if (connectNewNet(essid, pw)) {
                logger.info("ConnectNewNet done");
            }
        }

        return "connectToNet okay";

    }


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
     * Mi connette a una rete nota, dopo aver recuperato il netNumber con isKnownNet(essid)
     *
     * @param netNumber
     * @return
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
     * Resituisce il netNumber se il essid è noto
     *
     * @param essid
     * @return
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
     * Ritorna il numero della rete a cui si è connessi o -1, se non si è connessi
     *
     * @return
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
     * Ci permette di passare in modalità AccessPointMode
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
                if (!isStationMode())
                    logger.info("switchToAP okay");
                else
                    logger.error("switchToAP error");
            }
        }
        logger.info("switchToAp doesn't work on windows");
    }

    /**
     * Ci permette di passare in StationMode, solo se non lo siamo già
     * echo albertengopi | sudo -S systemctl stop dnsmasq.service
     * echo albertengopi | sudo -S systemctl stop hostapd.service
     * wpa_cli -iwlan0 enable_network 2
     * //separatamente
     * echo albertengopi | sudo -S ip link set dev wlan0 up
     *
     * @return
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
        }
        logger.info("switchToStation doesn't work on windows");
    }

    /**
     * True se in station mode
     * False se in AP mode
     * <p>
     * Controllo solo hostapd e on dnsmasq tanto vanno di pari passo
     *
     * @return
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
     * Meotodo privato che gestisce la riuscita o meno della connessione
     * Per prima cosa tira su l'interfaccia se non lo è già
     * SCANNING significa che ci sta ancora provando
     * INACTIVE vuol dire che le credenziali sono sbagliate e nel caso fossimo in AP mode ci ritorniamo
     * è presente "id=netNumber" andato tutto bene
     *
     * @param netNumber
     * @return
     */
    private Boolean handleConnectResult(Integer netNumber) {
        if (!isWindows) {

            StringBuilder result = new StringBuilder();
            result.append(execService.execute("echo albertengopi | sudo -S wpa_cli -iwlan0 status"));

            logger.info(result.toString());


            while (result.indexOf("SCANNING") != -1) {
                result.setLength(0);
                result.append(execService.execute("sleep 0.5s | wpa_cli -iwlan0 status"));
            }
            if (result.indexOf("INACTIVE") != -1) {
                execService.execute("wpa_cli -iwlan0 remove_network " + netNumber + " | wpa_cli -i wlan0 reconfigure");
                if (wasAP)
                    switchToAP();
                logger.info("handleConnectResult -> credenziali sbagliate");
                throw new WifiCredentialsException("Credentials not valid");
            }
            if (result.indexOf("id") != -1) {
                execService.execute(" wpa_cli -iwlan0 save_config");
                logger.info("handleConnectResult credenziali ok");
                return true;
            }
            logger.error("Unexpected result handleConnectResult" + result.toString());
            return false;
        }
        logger.info("handleConnectResult doesn't work on windows");
        return false;
    }
}
