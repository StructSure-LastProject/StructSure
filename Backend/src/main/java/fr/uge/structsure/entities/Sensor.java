package fr.uge.structsure.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import jakarta.persistence.*;
import org.hibernate.validator.internal.util.stereotypes.Lazy;

import java.io.IOException;
import java.time.LocalDate;
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

    private LocalDate installationDate;

    @ManyToOne
    @JoinColumn(name="plan_id")
    @JsonSerialize(using = PlanIdSerializer.class)
    private Plan plan;

    @Column(columnDefinition = "REAL")
    private Integer x;

    @Column(columnDefinition = "REAL")
    private Integer y;

    private Boolean archived=false;

    @OneToMany(mappedBy="sensor")
    @JsonIgnore
    @Lazy
    private Set<Result> results;


    //constructeurs nécéssaire
    public Sensor(){

    }

    /**
     * The constructor for the Sensor entity
     * @param sensorId the control and measure chips Ids
     * @param name the display name of the sensor
     * @param note the note of the sensor (empty if not set)
     * @param structure the structure that contains the sensor
     * @param installationDate the installation date (optional)
     * @param plan the plan where this sensor is placed (optional)
     * @param x the x coordinate of the point in the plan (optional)
     * @param y the y coordinate of the point in the plan (optional)
     * @param archived if its archived
     * @param results the results
     */
    @SuppressWarnings("java:S107")
    public Sensor(
        SensorId sensorId, String name, String note, Structure structure,
        LocalDate installationDate, Plan plan, Integer x, Integer y,
        Boolean archived, Set<Result> results
    ) {
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
     * @param name the display name of the sensor
     * @param note the note of the sensor (empty if not set)
     * @param structure the structure that contains the sensor
     * @param installationDate the installation date (optional)
     * @param plan the plan where this sensor is placed (optional)
     * @param x the x coordinate of the point in the plan (optional)
     * @param y the y coordinate of the point in the plan (optional)
     * @param archived if its archived
     */
    @SuppressWarnings("java:S107")
    public Sensor(
        String controlChip, String measureChip, String name, String note,
        Structure structure, LocalDate installationDate, Plan plan,
        Integer x, Integer y, Boolean archived
    ) {
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
        this.plan = plan; // Nullable
        this.x = x;       // Nullable
        this.y = y;       // Nullable
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

    /**
     * The base constructor for the Sensor entity with an installation date
     * @param controlChip the control chip id
     * @param measureChip the measure chip id
     * @param name the name of the sensor
     * @param installationDate the installation date
     * @param note the note of the sensor
     * @param structure the structure
     */
    public Sensor(String controlChip, String measureChip, String name, LocalDate installationDate, String note, Structure structure) {
        this.sensorId = new SensorId(controlChip, measureChip);
        this.name = name;
        this.installationDate = installationDate;
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

    public LocalDate getInstallationDate() {
        return installationDate;
    }

    public void setInstallationDate(LocalDate installationDate) {
        this.installationDate = installationDate;
    }

    public Integer getY() {
        return y;
    }

    public Integer getX() {
        return x;
    }

    public void setX(Integer x) {
        this.x = x;
    }

    public void setY(Integer y) {
        this.y = y;
    }

    public Boolean isArchived() {
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

    /**
     * Serializer the sensor's plan in JSON only with its ID instead
     * of all its fields
     */
    public static class PlanIdSerializer extends JsonSerializer<Plan> {
        @Override
        public void serialize(Plan plan, JsonGenerator gen, SerializerProvider s) throws IOException {
            gen.writeObject(plan == null ? null : plan.getId());
        }
    }
}
    
