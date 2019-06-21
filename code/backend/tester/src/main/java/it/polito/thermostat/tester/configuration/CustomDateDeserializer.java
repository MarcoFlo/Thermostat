package it.polito.thermostat.tester.configuration;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

import java.io.IOException;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class CustomDateDeserializer extends StdDeserializer<LocalTime> {
    private String pattern = "HH:mm";
    private DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(pattern);

    public CustomDateDeserializer() {
        this(null);
    }

    @Override
    public LocalTime deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException, JsonProcessingException {
        String time = jsonParser.getText();
        if (time.length() > 5)
            time = time.substring(0,time.length() - 3);
        return LocalTime.parse(time, dateTimeFormatter);
    }

    public CustomDateDeserializer(Class<?> vc) {
        super(vc);
    }

}