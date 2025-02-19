package fr.uge.structsure.dto.structure;


import fr.uge.structsure.entities.Plan;
import fr.uge.structsure.entities.Scan;
import fr.uge.structsure.entities.Sensor;

import java.util.List;
import java.util.Objects;

public record StructureDetailsResponseDTO(long id, String name, String note,
                                         List<Scan> scans, List<Plan> plans, List<Sensor> sensors) {

    public StructureDetailsResponseDTO {
        if (id < 0) {
            throw new IllegalArgumentException("id < 0");
        }
        Objects.requireNonNull(name);
        Objects.requireNonNull(note);
        Objects.requireNonNull(plans);
        Objects.requireNonNull(sensors);
        Objects.requireNonNull(scans);
    }

    public record Scan(long id, String name) {
        public Scan {
            if (id < 0) {
                throw new IllegalArgumentException("id < 0");
            }
            Objects.requireNonNull(name);
        }

        public static Scan fromScanEntity(fr.uge.structsure.entities.Scan scan) {
            return new Scan(scan.getId(), scan.getAuthor().getFirstname() + " " + scan.getAuthor().getLastname()
                + " - " + scan.getDate() + " - #"  + scan.getId());
        }
    }

    public record Plan(long id, String name, String section) {
        public Plan {
            if (id < 0) {
                throw new IllegalArgumentException("sensorId < 0");
            }
            Objects.requireNonNull(name);
        }

        public static Plan fromPlanEntity(fr.uge.structsure.entities.Plan plan) {
            return new Plan(plan.getId(), plan.getName(), plan.getSection());
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
