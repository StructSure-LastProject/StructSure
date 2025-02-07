package fr.uge.structsure.dto.structure;


import fr.uge.structsure.entities.SensorId;

import java.util.List;
import java.util.Objects;

public record StructureDetailsResponseDTO(long id, String name, String note,
                                          List<PLan> plans, List<Sensor> sensors) {

    public StructureDetailsResponseDTO {
        if (id < 0) {
            throw new IllegalArgumentException("id < 0");
        }
        Objects.requireNonNull(name);
        Objects.requireNonNull(note);
        Objects.requireNonNull(plans);
        Objects.requireNonNull(sensors);
    }

    public record PLan(long id, String name, String imageUrl) {
        public PLan {
            if (id < 0) {
                throw new IllegalArgumentException("sensorId < 0");
            }
            Objects.requireNonNull(name);
        }
    }

    public record Sensor(SensorId sensorId, String name) {
        public Sensor {
            Objects.requireNonNull(sensorId);
            Objects.requireNonNull(name);
        }
    }

}
