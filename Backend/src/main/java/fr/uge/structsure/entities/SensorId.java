package fr.uge.structsure.entities;

import jakarta.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class SensorId implements Serializable {
    private String controlChip;
    private String measureChip;

    //constructeurs nécéssaire
    public SensorId(){

    }

    public SensorId(String controlChip, String measureChip) {
        this.controlChip = Objects.requireNonNull(controlChip);
        this.measureChip = Objects.requireNonNull(measureChip);
    }

    // Getters
    public String getControlChip() {
        return controlChip;
    }

    public String getMeasureChip() {
        return measureChip;
    }

    // Redéfinir equals() et hashCode()
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SensorId sensorId = (SensorId) o;
        return Objects.equals(controlChip, sensorId.controlChip) &&
               Objects.equals(measureChip, sensorId.measureChip);
    }

    @Override
    public int hashCode() {
        return Objects.hash(controlChip, measureChip);
    }

    @Override
    public String toString() {
        return controlChip + ", " + measureChip;
    }
}
