package it.polito.thermostat.wifi.services;

import it.polito.thermostat.wifi.dataTransferObject.WifiDTO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service @Transactional
public class WifiService
{
public WifiDTO getWifi()
{
    ExecuteShellComandService c = new ExecuteShellComandService();
    return new WifiDTO();

}

}
