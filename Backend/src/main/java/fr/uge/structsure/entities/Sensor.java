package fr.uge.structsure.entities;

import fr.uge.structsure.utils.converters.LocalDateTimeConverter;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.Objects;

@Entity
public class Sensor {
    /*
    spring data ne permet pas d'avoir 2 champs Id.
    Il a donc fallu créer une classe avec les champs controlChip et measureChip.
    Ce qui permet de créer une paire de clé primaire -> une clé composite.
     */
    @Id
    private SensorId sensorId;

    private String name;
    private String note;

    /*
    @Column(columnDefinition = "TEXT")
    @Temporal(TemporalType.TIME)
    private Time installationDate;
     */

    @Column(columnDefinition = "TEXT")
    private String installationDate;

    /*
    @Convert(converter = TimeConverter.class)
    private Time installationDate;
     */

    @Column(columnDefinition = "REAL")
    private Double x;

    @Column(columnDefinition = "REAL")
    private Double y;

    @Column(columnDefinition = "INTEGER")
    private Boolean archived;

    @ManyToOne
    private Structure structure;

    @ManyToOne
    private Plan plan;

    //constructeurs nécéssaire
    public Sensor(){

    }

    public Sensor(String controlChip, String measureChip, String name, String note, String installationDate, Double x, Double y, Boolean archived, Structure structure, Plan plan) {
        Objects.requireNonNull(controlChip);
        Objects.requireNonNull(measureChip);
        Objects.requireNonNull(name);
        Objects.requireNonNull(note);
        Objects.requireNonNull(installationDate);
        Objects.requireNonNull(structure);
        Objects.requireNonNull(plan);
        this.sensorId = new SensorId(controlChip, measureChip);
        this.name = name;
        this.note = note;
        this.installationDate = installationDate;
        this.x = x;
        this.y = y;
        this.archived = archived;
        this.structure = structure;
        this.plan = plan;
    }
    @Override
    public String toString() {
        return sensorId.toString();
    }


    // Getter and Setter


    public SensorId getSensorId() {
        return sensorId;
    }

    public void setSensorId(SensorId sensorId) {
        this.sensorId = sensorId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getInstallationDate() {
        return installationDate;
    }

    public void setInstallationDate(String installationDate) {
        this.installationDate = installationDate;
    }

    public Double getX() {
        return x;
    }

    public void setX(Double x) {
        this.x = x;
    }

    public Double getY() {
        return y;
    }

    public void setY(Double y) {
        this.y = y;
    }

    public Boolean getArchived() {
        return archived;
    }

    public void setArchived(Boolean archived) {
        this.archived = archived;
    }

    public Structure getStructure() {
        return structure;
    }

    public void setStructure(Structure structure) {
        this.structure = structure;
    }

    public void setPlan(Plan plan) {this.plan = plan;}
}
    