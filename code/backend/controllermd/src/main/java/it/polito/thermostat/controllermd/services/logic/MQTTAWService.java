package it.polito.thermostat.controllermd.services.logic;

import com.amazonaws.services.iot.client.*;
import com.amazonaws.services.iot.client.sample.sampleUtil.SampleUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import it.polito.thermostat.controllermd.configuration.HostAddressGetter;
import it.polito.thermostat.controllermd.entity.program.Program;
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
import org.springframework.util.ResourceUtils;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;


@Service
@PropertySource("classpath:aws-iot-sdk-samples.properties")
public class MQTTAWService {
    boolean isWindows = System.getProperty("os.name").toLowerCase().startsWith("windows");
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    ObjectMapper objectMapper;

    @Value("${clientEndpoint}")
    String clientEndpoint;

    @Value("${clientId}")
    String clientId;

    @Value("${certificateFile}")
    String certificateFile;

    @Value("${privateKeyFile}")
    String privateKeyFile;


    private AWSIotMqttClient mqttClient;

    @PostConstruct
    public void init() throws AWSIotException {
        KeyStorePasswordPair pair = SampleUtil.getKeyStorePasswordPair(certificateFile, privateKeyFile);
        mqttClient = new AWSIotMqttClient(clientEndpoint, clientId, pair.keyStore, pair.keyPassword);
        mqttClient.connect();
        mqttClient.subscribe(new NotificationTopic());
    }

    public void sendEvent(Object event, Integer event_id) {
        String eventTopic = "pl19/event";
        AWSIotQos qos = AWSIotQos.QOS1;
        AWSIotMessage awsIotMessage = new AWSIotMessage(eventTopic, qos);
        try {
            awsIotMessage.setStringPayload(objectMapper.writeValueAsString(new EventAWS(event, event_id)));
            logger.info("This event will be published" + awsIotMessage.getStringPayload());
            mqttClient.publish(awsIotMessage);
        } catch (AWSIotException | JsonProcessingException e) {
            logger.error("Error eventTopic" + e.toString());
        }
    }


    public class NotificationTopic extends AWSIotTopic {
        private Logger logger = LoggerFactory.getLogger(this.getClass());
        String notificationTopic = "pl19/notification";
        AWSIotQos qos = AWSIotQos.QOS1;

        public NotificationTopic() {
            super("pl19/notification", AWSIotQos.QOS1);
        }

        @Override
        public void onMessage(AWSIotMessage message) {
            logger.info(message.getStringPayload());
            try {
                PingAWS request = objectMapper.readValue(message.getStringPayload(), PingAWS.class);
                PingAWS response = new PingAWS(request);
                AWSIotMessage awsIotMessage = new AWSIotMessage(notificationTopic, qos);
                awsIotMessage.setStringPayload(objectMapper.writeValueAsString(response));
                mqttClient.publish(awsIotMessage);

            } catch (IOException | AWSIotException e) {
                logger.error("Error NotificationTopic" + e.toString());
            }
        }
    }
}


