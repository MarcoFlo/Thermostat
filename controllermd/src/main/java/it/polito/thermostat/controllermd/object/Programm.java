package it.polito.thermostat.controllermd.object;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalTime;
import java.util.Map;

@Data
@Document(collection = "programs")
public class Programm {

    @Id
    private String idProgramm;
    private Map<Integer, DailyProgramm> programs;

}
