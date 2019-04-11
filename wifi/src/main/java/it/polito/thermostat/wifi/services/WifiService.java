package it.polito.thermostat.wifi.services;

import it.polito.thermostat.wifi.dataTransferObject.WifiDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

@Service
@Transactional
public class WifiService {

    boolean isWindows = System.getProperty("os.name").toLowerCase().startsWith("windows");
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    ExecuteShellComandService execService;

    /*
    Restituisce l'ipv4 dell'interfaccia wifi
     */
    public String getIP() {
        StringBuilder result;
        int pos;
        if (isWindows) {
            result = new StringBuilder();
            result.append(execService.executeCommand("ipconfig"));
            pos = result.indexOf("LAN wireless Wi-Fi");
            logger.info(result.toString());
            return result.subSequence(result.indexOf("Indirizzo IPv4", pos) + 40, result.indexOf("Subnet mask", pos - 1)).toString();

        } else {
            result = execService.execute("ifconfig");
            pos = result.indexOf("wlan0");
            return result.subSequence(result.indexOf("inet", pos) + 5, result.indexOf("netmask", pos) - 2).toString();
        }
    }


    public List<String> getAvailableNet() {
        StringBuilder result;
        if (isWindows) {
            result = new StringBuilder();
            List<String> wifiList = new LinkedList<>();
            int pos = -1;
            result.append("ESSID:\"Alice-35965732\"\n" +
                    "                    Bit Rates:" +
                    "\n\nESSID:\"FASTWEB-YPBJG9\"\n" +
                    "                    Bit Rates:");

            while ((pos = result.indexOf("ESSID", pos + 1)) != -1) {
                wifiList.add(result.subSequence(result.indexOf("ESSID:", pos) + 7, (result.indexOf("\n", pos) - 1)).toString());
            }
            return wifiList;

        } else {
            result = execService.execute("iwlist wlan0 scan | grep ESSID");
            return Arrays.asList(result.toString().split("\n"));
        }
    }

    /*
        wpa_cli -iwlan0 disconnect
        wpa_cli -iwlan0 add_network -> result

        wpa_cli -iwlan0 set_network 3 auth_alg OPEN | wpa_cli -iwlan0 set_network 3 key_mgmt WPA-PSK | wpa_cli -iwlan0 set_network 3 psk '"12345678"' | wpa_cli -iwlan0 set_network 3 proto RSN | wpa_cli -iwlan0 set_network 3 mode 0 | wpa_cli -iwlan0 set_network 3 ssid '"Matterhorn"' | wpa_cli -iwlan0 select_network 3 | wpa_cli -iwlan0 enable_network 3 | wpa_cli -iwlan0 reassociate | wpa_cli -iwlan0 status
                SCANNING -> ci sta provando
                INACTIVE -> credenziali sbagliate
                id = result ok (id=3)
        wpa_cli -iwlan0 save_config

     */

    public boolean connectNewNet(String essid, String pw) {
        if (!isWindows) {
            StringBuilder result = new StringBuilder();
            Integer netNumber;
            result.append(execService.execute("wpa_cli -iwlan0 disconnect | wpa_cli -iwlan0 add_network"));
            netNumber = Integer.valueOf(result.toString());
            result.setLength(0);
            result.append(execService.execute("wpa_cli -iwlan0 set_network "+ netNumber +" auth_alg OPEN | wpa_cli -iwlan0 set_network "+ netNumber +" key_mgmt WPA-PSK | wpa_cli -iwlan0 set_network "+ netNumber +" psk '\""+pw+"\"' | wpa_cli -iwlan0 set_network "+ netNumber +" proto RSN | wpa_cli -iwlan0 set_network "+ netNumber +" mode 0 | wpa_cli -iwlan0 set_network "+ netNumber +" ssid '\""+essid+"\"' | wpa_cli -iwlan0 select_network "+ netNumber +" | wpa_cli -iwlan0 enable_network "+ netNumber +" | wpa_cli -iwlan0 reassociate | wpa_cli -iwlan0 status"));

            return handleConnectResult(result,netNumber);
        }
        return false;
    }

    /*
    wpa_cli -iwlan0 disconnect | wpa_cli -iwlan0 select_network 3 | wpa_cli -iwlan0 enable_network 3 | wpa_cli -iwlan0 reassociate
     */
    public boolean connectKnownNet(Integer netNumber) {
        if (!isWindows) {
            StringBuilder result = new StringBuilder();
            execService.execute("wpa_cli -iwlan0 disconnect | wpa_cli -iwlan0 select_network "+netNumber+" | wpa_cli -iwlan0 enable_network "+netNumber+" | wpa_cli -iwlan0 reassociate");
            return handleConnectResult(result,netNumber);
        }
        return false;
    }

    public Integer isKnownNet(String essid) {
        if (!isWindows) {
            StringBuilder result = new StringBuilder();
            result.append(execService.execute("wpa_cli -iwlan0 list_networks | grep " + essid));
            if (result.length() == 0) {
                return -1;
            } else {
                return Integer.valueOf(result.substring(0,result.indexOf("\t")));
            }
        }
        return -1;
    }


    private boolean handleConnectResult(StringBuilder result, Integer netNumber)
    {
        while (result.indexOf("SCANNING") != -1)
        {
            result.setLength(0);
            result.append(execService.execute("sleep 0.5s | wpa_cli -iwlan0 status"));
        }
        if (result.indexOf("INACTIVE") != -1)
        {
            execService.execute("wpa_cli -iwlan0 remove_network " + netNumber + " | wpa_cli -i wlan0 reconfigure" );
            return false;

        }
        if (result.indexOf("id") != -1)
        {
            execService.execute(" wpa_cli -iwlan0 save_config" );
            return true;
        }
        return false;
    }
}
