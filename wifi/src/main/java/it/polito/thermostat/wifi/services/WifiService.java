package it.polito.thermostat.wifi.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

@Service
@Transactional
public class WifiService {

    boolean isWindows = System.getProperty("os.name").toLowerCase().startsWith("windows");
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    ExecuteShellComandService execService;

    /**
     * Restituisce l'ipv4 dell'interfaccia wifi
     */
    public String getIP() {
        if (!isWindows) {
            StringBuilder result = new StringBuilder();

            result.append(execService.execute("wpa_cli -iwlan0 status | grep ip_address"));
            if (result.length() != 0) {
                String[] arr = result.toString().split("=");
                return arr[1];
            } else {
                logger.error("Errore in getIP, comando non andato a buon fine");
                return "Errore in getIP, comando non andato a buon fine";
            }
        }
        return "windows no good";
    }


    /**
     * Restituisce le reti visibili
     *
     * @return
     */
    public List<String> getAvailableNet() {
        if (!isWindows) {
            StringBuilder result = new StringBuilder();
            result.append(execService.execute("iwlist wlan0 scan | grep ESSID"));
            if (result.length() != 0) {
                logger.info(result.toString());
                return Arrays.asList(result.toString().split("\n"));
            }
            else
            {
                logger.error("Errore in getAvailableNet, comando non andato a buon fine");
                return null;
            }
        }
        return null;
    }


    /**
     * Ci permette di connetterci a una rete
     */
    public String connectToNet(String essid, String pw) {
        Integer knownNet;

        switchToStation();

        if ((knownNet = isKnownNet(essid)) != -1) {
            if (!connectKnownNet(knownNet)) {
                logger.error("errore nella connectToNet/known");
                return "err connectToNet/known";
            }
        } else {
            if (!connectNewNet(essid, pw)) {
                logger.error("errore nella connectToNet/new");
                return "err connectToNet/known";
            }
        }
        if (!isWindows) {
            execService.execute("echo albertengopi | sudo - S ip link set dev wlan0 up");
        }
        return "connectToNet okay";

    }

    /**
     * Connette alla rete selezionata
     *
     * @param essid
     * @param pw
     * @return
     */
    private boolean connectNewNet(String essid, String pw) {
        if (!isWindows) {
            StringBuilder result = new StringBuilder();
            Integer netNumber;
            result.append(execService.execute("wpa_cli -iwlan0 disconnect | wpa_cli -iwlan0 add_network"));
            netNumber = Integer.valueOf(result.toString());
            result.setLength(0);
            result.append(execService.execute("wpa_cli -iwlan0 set_network " + netNumber + " auth_alg OPEN | wpa_cli -iwlan0 set_network " + netNumber + " key_mgmt WPA-PSK | wpa_cli -iwlan0 set_network " + netNumber + " psk '\"" + pw + "\"' | wpa_cli -iwlan0 set_network " + netNumber + " proto RSN | wpa_cli -iwlan0 set_network " + netNumber + " mode 0 | wpa_cli -iwlan0 set_network " + netNumber + " ssid '\"" + essid + "\"' | wpa_cli -iwlan0 select_network " + netNumber + " | wpa_cli -iwlan0 enable_network " + netNumber + " | wpa_cli -iwlan0 reassociate | wpa_cli -iwlan0 status"));

            return handleConnectResult(result, netNumber);
        }
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
            StringBuilder result = new StringBuilder();
            execService.execute("wpa_cli -iwlan0 disconnect | wpa_cli -iwlan0 select_network " + netNumber + " | wpa_cli -iwlan0 enable_network " + netNumber + " | wpa_cli -iwlan0 reassociate");
            return handleConnectResult(result, netNumber);
        }
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
                return -1;
            } else {
                return Integer.valueOf(result.substring(0, result.indexOf("\t")));
            }
        }
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
                return Integer.valueOf(split[0]);
            }
        }
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
     *
     * @return
     */
    public Boolean switchToAP() {
        if (!isWindows) {
            StringBuilder result = new StringBuilder();
            result.append(execService.execute("wpa_cli -iwlan0 disable_network " + getCurrentNetNumber() + " | echo albertengopi | sudo -S ip link set dev wlan0 down | echo albertengopi | sudo -S ip addr add 192.168.4.1/24 dev wlan0 | echo albertengopi | sudo -S systemctl restart dnsmasq.service | echo albertengopi | sudo -S systemctl restart hostapd.service"));
        }
        return !isStationMode();
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
                StringBuilder result = new StringBuilder();
                result.append(execService.execute("echo albertengopi | sudo -S systemctl stop dnsmasq.service | echo albertengopi | sudo -S systemctl stop hostapd.service"));
            }
        }
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
        return false;
    }

    /**
     * Meotodo privato che gestisce la riuscita o meno della connessione
     * SCANNING significa che ci sta ancora provando
     * INACTIVE vuol dire che le credenziali sono sbaglaite
     * è presente "id=netNumber" andato tutto bene
     *
     * @param result
     * @param netNumber
     * @return
     */
    private Boolean handleConnectResult(StringBuilder result, Integer netNumber) {
        while (result.indexOf("SCANNING") != -1) {
            result.setLength(0);
            result.append(execService.execute("sleep 0.5s | wpa_cli -iwlan0 status"));
        }
        if (result.indexOf("INACTIVE") != -1) {
            execService.execute("wpa_cli -iwlan0 remove_network " + netNumber + " | wpa_cli -i wlan0 reconfigure");
            return false;

        }
        if (result.indexOf("id") != -1) {
            execService.execute(" wpa_cli -iwlan0 save_config");
            return true;
        }
        return false;
    }
}
