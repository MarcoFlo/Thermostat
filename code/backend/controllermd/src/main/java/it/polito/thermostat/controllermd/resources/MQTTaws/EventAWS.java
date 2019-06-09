package it.polito.thermostat.controllermd.resources.MQTTaws;

import it.polito.thermostat.controllermd.configuration.HostAddressGetter;
import lombok.Data;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * event_id = 10 new esp
 * event_id = 11 new command actuator
 * event_id = 12 new sensor data
 *
 */
@Data
public class EventAWS {
    private Integer event_id;
    private String timestamp;
    private String device_mac;
    private Object event;

    public EventAWS(Object event, Integer event_id) {
        this.event_id = event_id;
        timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSSSSS"));
        device_mac = HostAddressGetter.getMAC();
        this.event = event;
    }

}
