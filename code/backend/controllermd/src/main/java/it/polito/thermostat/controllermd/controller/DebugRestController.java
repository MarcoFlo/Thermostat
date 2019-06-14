package it.polito.thermostat.controllermd.controller;

import com.google.zxing.WriterException;
import it.polito.thermostat.controllermd.repository.WSALRepository;
import it.polito.thermostat.controllermd.services.server.QRService;
import it.polito.thermostat.controllermd.services.server.WifiService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@Controller
public class DebugRestController {
    private Logger logger = LoggerFactory.getLogger(this.getClass());
    @Autowired
    WifiService wifiService;

    @Autowired
    QRService qrService;

    @Autowired
    WSALRepository wsalRepository;


    @GetMapping(value = "/debug", produces = MediaType.IMAGE_PNG_VALUE)
    public @ResponseBody byte[] qr() throws IOException, WriterException {
        logger.info("debug contacted");
        return qrService.getQRCodeImage();
    }


    @PostMapping("/debug/wsal")
    public void deleteWSAL()
    {
        wsalRepository.deleteAll();
    }
}
