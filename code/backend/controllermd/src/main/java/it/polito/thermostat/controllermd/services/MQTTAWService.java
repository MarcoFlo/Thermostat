package it.polito.thermostat.controllermd.services;

import com.amazonaws.services.iot.client.*;
import com.amazonaws.services.iot.client.core.AwsIotConnection;
import com.amazonaws.services.iot.client.sample.sampleUtil.SampleUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import it.polito.thermostat.controllermd.configuration.HostAddressGetter;
import it.polito.thermostat.controllermd.resources.MQTTaws.EventAWS;
import it.polito.thermostat.controllermd.resources.MQTTaws.PingAWS;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

import com.amazonaws.services.iot.client.sample.sampleUtil.SampleUtil.KeyStorePasswordPair;

import java.io.IOException;
import java.util.Random;

@Service
@PropertySource("classpath:aws-iot-sdk-samples.properties")
public class MQTTAWService {
    boolean isWindows = System.getProperty("os.name").toLowerCase().startsWith("windows");
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    WifiService wifiService;

    @Value("${clientEndpoint}")
    String clientEndpoint;

    @Value("#{'${clientId}'}")
    String clientId;

    @Value("${certificateFile}")
    String certificateFile;

    @Value("${privateKeyFile}")
    String privateKeyFile;


    private AWSIotMqttClient mqttClient;

    @PostConstruct
    public void init() throws AWSIotException {
        clientId += HostAddressGetter.getMAC().replace(":", "") + "-" + new Random().nextInt(1000);
        KeyStorePasswordPair pair = SampleUtil.getKeyStorePasswordPair(certificateFile, privateKeyFile);
        mqttClient = new AWSIotMqttClient(clientEndpoint, clientId, pair.keyStore, pair.keyPassword);
        if (wifiService.isInternet()) {
            mqttClient.setCleanSession(true);
            mqttClient.connect();
            mqttClient.subscribe(new NotificationTopic());
        }
    }

    /**
     * Event id:
     * - new esp = 10
     * - command to actuator = 11
     * - sensorData = 12
     *
     * @param event
     * @param event_id
     */
    public void sendEvent(Object event, Integer event_id) {
        String eventTopic = "pl19/event";
        AWSIotQos qos = AWSIotQos.QOS1;
        AWSIotMessage awsIotMessage = new AWSIotMessage(eventTopic, qos);
        if (wifiService.isInternet()) {
            if (!mqttClient.getConnectionStatus().equals(AWSIotConnectionStatus.CONNECTED))
            {
                try {
                    mqttClient.setCleanSession(true);
                    mqttClient.connect();
                    mqttClient.subscribe(new NotificationTopic());
                } catch (AWSIotException e) {
                    e.printStackTrace();
                }
            }
            try {
                awsIotMessage.setStringPayload(objectMapper.writeValueAsString(new EventAWS(event, event_id)));
//          logger.info("This event will be published" + awsIotMessage.getStringPayload());
                mqttClient.publish(awsIotMessage);
            } catch (AWSIotException | JsonProcessingException e) {
                logger.error("Error eventTopic" + e.toString());
            }
        }
    }


/**
 * To handle the notification topic
 */
public class NotificationTopic extends AWSIotTopic {
    private Logger logger = LoggerFactory.getLogger(this.getClass());
    String notificationTopic = "pl19/notification";
    AWSIotQos qos = AWSIotQos.QOS1;

    public NotificationTopic() {
        super("pl19/notification", AWSIotQos.QOS1);
    }

    /**
     * To react when ping event occurs
     *
     * @param message
     */
    @Override
    public void onMessage(AWSIotMessage message) {
        try {
            PingAWS request = objectMapper.readValue(message.getStringPayload(), PingAWS.class);
            PingAWS response = new PingAWS(request.getEvent().getSequence());
            AWSIotMessage awsIotMessage = new AWSIotMessage(notificationTopic, qos);
            awsIotMessage.setStringPayload(objectMapper.writeValueAsString(response));
            mqttClient.publish(awsIotMessage);
            logger.info("This has been published" + awsIotMessage.getStringPayload());
        } catch (IOException | AWSIotException e) {
            logger.error("Error NotificationTopic" + e.toString());
        }
    }
}
}


