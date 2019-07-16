package it.polito.thermostat.controllermd.resources.MQTTaws;

import it.polito.thermostat.controllermd.configuration.HostAddressGetter;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PingAWS {
    private Integer event_id;

    //    @JsonFormat(shape = JsonFormat.Shape.STRING)
    //    @JsonDeserialize(using = AWSDateDeserializer.class)
    //    @JsonSerialize(using = AWSDateSerializer.class)
    private String timestamp;
    private String device_mac;
    private Event event;

    public PingAWS(int sequence) {
        this.event_id = 1;
        this.timestamp = LocalDateTime.now().withNano(1000).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS"));
        this.device_mac = HostAddressGetter.getMAC();
        event = new Event("Ping response", sequence + 1);
    }

    @Data
    @AllArgsConstructor
    public static class Event {
        private String message;
        private Integer sequence;

    }
}
