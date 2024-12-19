package fr.uge.structsure.dto.sensors;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import fr.uge.structsure.entities.SensorId;

@JsonSerialize
public record AddSensorDTO(long structureId, SensorId sensorId, String name, String installationDate, String note) {

    public boolean hasNullMembers(){
        return sensorId == null || name == null || installationDate == null;
    }
}
