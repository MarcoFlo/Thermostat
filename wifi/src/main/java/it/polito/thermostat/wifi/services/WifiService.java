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


    public String getWifi() {
        StringBuilder result = new StringBuilder("");
        if (isWindows) {
            result.append(execService.executeCommand("ipconfig"));
        } else {
            result.append(execService.executeCommand("ifconfig"));
        }

        return result + "\n\n " + result.subSequence(result.indexOf("Indirizzo IPv4") + 40,result.indexOf("Subnet mask")).toString();


    }
}
