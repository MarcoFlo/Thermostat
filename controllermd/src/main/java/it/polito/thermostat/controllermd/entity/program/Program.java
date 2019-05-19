package it.polito.thermostat.controllermd.entity.program;

import it.polito.thermostat.controllermd.entity.program.DailyProgram;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Map;

@Data
@Document(collection = "weeklyPrograms")
public class Program {

    @Id
    private String idProgram;
    private Map<Integer, DailyProgram> weeklyMap;

}
