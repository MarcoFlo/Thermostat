package it.polito.thermostat.controllermd.configuration;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

public class CustomDateDeserializer extends StdDeserializer<LocalTime> {
    private String pattern = "HH:mm";
    private DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(pattern);

    public CustomDateDeserializer() {
        this(null);
    }

    @Override
    public LocalTime deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException, JsonProcessingException {
        String time = jsonParser.getText();
        return LocalTime.parse(time, dateTimeFormatter);
    }

    public CustomDateDeserializer(Class<?> vc) {
        super(vc);
    }

}