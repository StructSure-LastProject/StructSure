package fr.uge.structsure.dto.structure;


import fr.uge.structsure.entities.Plan;
import fr.uge.structsure.entities.Sensor;

import java.util.List;
import java.util.Objects;

public record StructureDetailsResponseDTO(long id, String name, String note,
                                          List<Plan> plans, List<Sensor> sensors) {

    public StructureDetailsResponseDTO {
        if (id < 0) {
            throw new IllegalArgumentException("id < 0");
        }
        Objects.requireNonNull(name);
        Objects.requireNonNull(note);
        Objects.requireNonNull(plans);
        Objects.requireNonNull(sensors);
    }

    public record Plan(long id, String name, String imageUrl) {
        public Plan {
            if (id < 0) {
                throw new IllegalArgumentException("sensorId < 0");
            }
            Objects.requireNonNull(name);
        }

        public static Plan fromPlanEntity(fr.uge.structsure.entities.Plan plan) {
            return new Plan(plan.getId(), plan.getName(), plan.getImageUrl());
        }
    }

    public record Sensor(String controlChip, String measureChip, String name) {
        public Sensor {
            Objects.requireNonNull(controlChip);
            Objects.requireNonNull(measureChip);
            Objects.requireNonNull(name);
        }

        public static Sensor fromSensorEntity(fr.uge.structsure.entities.Sensor sensor) {
            return new Sensor(sensor.getSensorId().getControlChip(), sensor.getSensorId().getMeasureChip(), sensor.getName());
        }
    }

}
