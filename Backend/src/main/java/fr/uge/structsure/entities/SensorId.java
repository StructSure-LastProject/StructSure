package fr.uge.structsure.entities;

import fr.uge.structsure.exceptions.Error;
import fr.uge.structsure.exceptions.TraitementException;
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
        this.controlChip = Objects.requireNonNull(controlChip.replace(" ", "").toUpperCase());
        this.measureChip = Objects.requireNonNull(measureChip.replace(" ", "").toUpperCase());
    }

    /**
     * Safely creates and returns a SensorId object from the dto
     * sensor id string, parsing the field with a '-'
     * @param stringId the sensor id object with control chip and then
     *     measure chip separated by a '-'
     * @return the SensorId corresponding to the given string
     * @throws TraitementException if the sensorId is malformed
     */
    public static SensorId from(String stringId) throws TraitementException {
        var split = stringId.split("-");
        if (split.length != 2) throw new TraitementException(Error.INVALID_FIELDS);
        return new SensorId(split[0], split[1]);
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
