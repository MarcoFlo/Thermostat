package it.polito.thermostat.wifi.services;

import it.polito.thermostat.wifi.dataTransferObject.WifiDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class WifiService {

    boolean isWindows = System.getProperty("os.name").toLowerCase().startsWith("windows");
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    ExecuteShellComandService execService;


    public String getIP() {
        StringBuilder result = new StringBuilder("");
        int pos;
        if (isWindows) {
            result.append(execService.executeCommand("ipconfig"));
            pos = result.indexOf("LAN wireless Wi-Fi");
            logger.info(result.toString());
            return result.subSequence(result.indexOf("Indirizzo IPv4",pos) + 40,result.indexOf("Subnet mask",pos)).toString();

        } else {
            pos = result.indexOf("wlan0");
            logger.info(result.toString());
            return result.subSequence(result.indexOf("inet",pos) + 40,result.indexOf("netmask",pos) - 1).toString();
        }




    }
}
