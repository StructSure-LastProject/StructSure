package fr.uge.structsure.dto.sensors;

import fr.uge.structsure.entities.Sensor;

public record SensorDTO(
        long id,
        String controlChip,
        String measureChip,
        String name,
        String note,
        String state,
        String installationDate,
        double x,
        double y
) {
    public static SensorDTO fromEntity(Sensor sensor) {
        return new SensorDTO(
                sensor.getSensorId().hashCode(),
                sensor.getSensorId().getControlChip(),
                sensor.getSensorId().getMeasureChip(),
                sensor.getName(),
                sensor.getNote(),
                sensor.getArchived() ? "archivé" : "actif",
                sensor.getInstallationDate().toString(),
                sensor.getX(),
                sensor.getY()
        );
    }
}
