package fr.uge.structsure.dto.structure;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;

/**
 * Represents the detailed response of a structure, including its scans, plans, and sensors.
 *
 * @param id      The unique identifier of the structure.
 * @param name    The name of the structure.
 * @param note    Additional notes about the structure.
 * @param scans   The list of scans associated with the structure.
 * @param plans   The list of plans related to the structure.
 * @param sensors The list of sensors within the structure.
 */
public record StructureDetailsResponseDTO(long id, String name, String note,
                                         List<Scan> scans, List<Plan> plans, List<Sensor> sensors) {

    /**
     * Validates the required fields of the structure details response.
     *
     * @throws IllegalArgumentException If {@code id} is negative.
     * @throws NullPointerException If any of the fields {@code name}, {@code note}, {@code scans}, {@code plans}, or {@code sensors} is null.
     */
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

    /**
     * Represents a scan associated with a structure.
     *
     * @param id   The unique identifier of the scan.
     * @param note The note of the scan.
     * @param name The name of the scan author (firstname, lastname).
     * @param date The date of the scan.
     * @param dataRow The data row representation of the scan
     */
    public record Scan(long id, String note, String name, String date, String dataRow) {
        /**
         * Validates the scan fields.
         *
         * @throws IllegalArgumentException If {@code id} is negative.
         * @throws NullPointerException If {@code name} is null.
         */
        public Scan {
            if (id < 0) {
                throw new IllegalArgumentException("id < 0");
            }
            Objects.requireNonNull(note);
            Objects.requireNonNull(name);
            Objects.requireNonNull(date);
            Objects.requireNonNull(dataRow);
        }

        /**
         * Converts a {@link fr.uge.structsure.entities.Scan} entity into a {@code Scan} DTO.
         *
         * @param scan The scan entity to convert.
         * @return A new {@code Scan} DTO.
         */
        public static Scan fromScanEntity(fr.uge.structsure.entities.Scan scan) {
            var name = scan.getAuthor().getFirstname() + " " + scan.getAuthor().getLastname();
            var format = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
            var date = scan.getDate().format(format);
            var dataRow = name + " - " + date + " - #"  + scan.getId();
            return new Scan(scan.getId(), scan.getNote(), name, date, dataRow);
        }
    }

    public record Plan(long id, String name, String section, boolean archived) {
        public Plan {
            if (id < 0) {
                throw new IllegalArgumentException("sensorId < 0");
            }
            Objects.requireNonNull(name);
        }

        public static Plan fromPlanEntity(fr.uge.structsure.entities.Plan plan) {
            return new Plan(plan.getId(), plan.getName(), plan.getSection(), plan.isArchived());
        }
    }

    /**
     * The sensor dto
     * @param controlChip the control chip
     * @param measureChip the measure chip
     * @param name the name of the sensor
     * @param x the position x of the sensor
     * @param y the position y of the sensor
     */
    public record Sensor(String controlChip, String measureChip, String name, Integer x, Integer y) {
        public Sensor {
            Objects.requireNonNull(controlChip);
            Objects.requireNonNull(measureChip);
            Objects.requireNonNull(name);
        }

        /**
         * Creates the dto from the sensor entity
         * @param sensor the sensor entity
         * @return the sensor dto
         */
        public static Sensor fromSensorEntity(fr.uge.structsure.entities.Sensor sensor) {
            return new Sensor(sensor.getSensorId().getControlChip(), sensor.getSensorId().getMeasureChip(), sensor.getName(),
                    sensor.getX(), sensor.getY());
        }
    }
}
