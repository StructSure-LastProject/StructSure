package fr.uge.structsure.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import org.hibernate.validator.internal.util.stereotypes.Lazy;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

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

    private String note = "";

    @JsonBackReference
    @ManyToOne
    @JoinColumn(name = "structure_id", nullable = true)
    private Structure structure;

    private LocalDateTime installationDate;

    @Column(columnDefinition = "REAL")
    private Double x;

    @Column(columnDefinition = "REAL")
    private Double y;

    private Boolean archived=false;

    @OneToMany(mappedBy="sensor")
    @JsonIgnore
    @Lazy
    private Set<Result> results;

    @JsonBackReference
    @ManyToOne
    @JoinColumn(name="plan_id")
    private Plan plan;

    //constructeurs nécéssaire
    public Sensor(){

    }

    /**
     * The constructor for the Sensor entity
     * @param sensorId the sensor id
     * @param name the name of the sensor
     * @param note the note of the sensor
     * @param structure the structure
     * @param installationDate the installation date
     * @param x the position x
     * @param y the position y
     * @param archived if its archived
     * @param results the results
     * @param plan the plan
     */
    public Sensor(SensorId sensorId, String name, String note, Structure structure, LocalDateTime installationDate, Double x, Double y, Boolean archived, Set<Result> results, Plan plan) {
        this.sensorId = sensorId;
        this.name = name;
        this.note = note;
        this.structure = structure;
        this.installationDate = installationDate;
        this.x = x;
        this.y = y;
        this.archived = archived;
        this.results =  new HashSet<>(results);
        this.plan = plan;
    }

    /**
     * The constructor for the Sensor entity
     * @param controlChip the control chip id
     * @param measureChip the measure chip id
     * @param name the name of the sensor
     * @param note the note of the sensor
     * @param structure the structure
     * @param installationDate the installation date
     * @param x the position x
     * @param y the position y
     * @param archived if its archived
     */
    public Sensor(String controlChip, String measureChip, String name, String note, LocalDateTime installationDate, Double x, Double y, Boolean archived, Structure structure) {
        Objects.requireNonNull(controlChip);
        Objects.requireNonNull(measureChip);
        Objects.requireNonNull(name);
        Objects.requireNonNull(note);
        Objects.requireNonNull(installationDate);
        Objects.requireNonNull(structure);
        this.sensorId = new SensorId(controlChip, measureChip);
        this.name = name;
        this.note = note;
        this.installationDate = installationDate;
        this.x = x;
        this.y = y;
        this.archived = archived;
        this.structure = structure;
    }

    /**
     * The base constructor for the Sensor entity
     * @param controlChip the control chip id
     * @param measureChip the measure chip id
     * @param name the name of the sensor
     * @param note the note of the sensor
     * @param structure the structure
     */
    public Sensor(String controlChip, String measureChip, String name, String note, Structure structure) {
        this.sensorId = new SensorId(controlChip, measureChip);
        this.name = name;
        this.note = note;
        this.structure = structure;
    }

    @Override
    public String toString() {
        return sensorId.toString();
    }

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

    public Structure getStructure() {
        return structure;
    }

    public void setStructure(Structure structure) {
        this.structure = structure;
    }

    // todo default value to remove
    public LocalDateTime getInstallationDate() {
        return Objects.requireNonNullElseGet(this.installationDate, LocalDateTime::now);
    }

    public void setInstallationDate(LocalDateTime installationDate) {
        this.installationDate = installationDate;
    }

    // todo default value to remove
    public Double getX() {
        if (x == null) {
            return 0.0;
        }
        return x;
    }

    public void setX(Double x) {
        this.x = x;
    }

    // todo default value to remove
    public Double getY() {
        if (y == null) {
            return 0.0;
        }
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

    public Set<Result> getResults() {
        return results;
    }

    public void setResults(Set<Result> results) {
        this.results = new HashSet<>(results);
    }

    public Plan getPlan() {
        return plan;
    }

    public void setPlan(Plan plan) {
        this.plan = plan;
    }
}