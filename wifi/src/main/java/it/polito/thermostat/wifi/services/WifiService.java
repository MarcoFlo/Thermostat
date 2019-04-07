package it.polito.thermostat.wifi.services;

import it.polito.thermostat.wifi.dataTransferObject.WifiDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
        StringBuilder result = new StringBuilder("");
        int pos;
        if (isWindows) {
            result.append(execService.executeCommand("ipconfig"));
            pos = result.indexOf("LAN wireless Wi-Fi");
            logger.info(result.toString());
            return result.subSequence(result.indexOf("Indirizzo IPv4", pos) + 40, result.indexOf("Subnet mask", pos)).toString();

        } else {
            result.append(execService.executeCommand("ifconfig"));
            pos = result.indexOf("wlan0");
            return result.subSequence(result.indexOf("inet", pos) + 5, result.indexOf("netmask", pos) - 2).toString();
        }
    }

    //ESSID:"Alice-35965732"
    public LinkedList<String> getAvailableNet() {
        StringBuilder result = new StringBuilder("");
        LinkedList<String> wifiList = new LinkedList<String>();
        int pos = 0;
        if (isWindows) {

            return null;

        } else {
            result.append(execService.executeCommand("sudo iwlist wlan0 scan"));
            while (pos != -1) {
                pos = result.indexOf("ESSID", pos);
                wifiList.add(result.subSequence(result.indexOf("ESSID:", pos) + 7, result.indexOf("Bit Rates", pos) - 2).toString());
            }
            return wifiList;
        }

    }
}
