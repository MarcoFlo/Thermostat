package it.polito.thermostat.controllermd.object;

import lombok.Data;
import org.springframework.cglib.core.Local;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Date;

//TODO sono tutte sfasate

@Data
public class MongoZonedDateTime extends Date {

    private static Date parseData(String pattern, String completeData) {
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(pattern);
        ZonedDateTime londonTime = ZonedDateTime.parse(completeData, dateTimeFormatter);
        return Date.from(londonTime.toInstant());
    }


    public static Date getNow() {
        // String pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS z";
        // DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(pattern);
        // String completeData = LocalDateTime.now().toString() + " GMT+00:00";
        // ZonedDateTime londonTime = ZonedDateTime.parse(completeData, dateTimeFormatter);
        // return Date.from(londonTime.toInstant());
        return parseData("yyyy-MM-dd'T'HH:mm:ss.SSS z", LocalDateTime.now().toString() + " GMT+00:00");
    }

    public static Date getMongoZonedDateTimeFromDate(String date) {
        // String completeData = date + " 12:00 GMT+00:00"; //data nel formato AAAA-MM-DD
        // String pattern = "yyyy-MM-dd HH:mm z";
        // DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(pattern);
        // ZonedDateTime londonTime = ZonedDateTime.parse(completeData, dateTimeFormatter);
        return parseData("yyyy-MM-dd HH:mm z", date + " 12:00 GMT+00:00");
    }

    public static LocalTime getTimeFromMongoZonedDateTime(Date date) {
        return LocalDateTime.ofInstant(date.toInstant(), ZoneId.of("Europe/London")).toLocalTime();
    }

    public static LocalDateTime getDateFromMongoZonedDateTime(Date date) {

        return LocalDateTime.ofInstant(date.toInstant(), ZoneId.of("Europe/London"));
    }
}
