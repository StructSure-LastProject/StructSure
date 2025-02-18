package fr.uge.structsure.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import org.hibernate.validator.internal.util.stereotypes.Lazy;

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

    private String note;

    @JsonBackReference
    @ManyToOne
    @JoinColumn(name = "structure_id", nullable = true)
    private Structure structure;

    @Temporal(TemporalType.DATE)
    Date installationDate;



     /*
    @Column(columnDefinition = "TEXT")
    private String installationDate;

      */

    @Column(columnDefinition = "REAL")
    private Double x;

    @Column(columnDefinition = "REAL")
    private Double y;

    @Column(columnDefinition = "INTEGER")
    private Boolean archived;

    @OneToMany(mappedBy="sensor")
    @JsonIgnore
    @Lazy
    private Set<Result> results;


    //constructeurs nécéssaire
    public Sensor(){

    }

    public Sensor(String controlChip, String measureChip, String name, String note, Date installationDate, Double x, Double y, Boolean archived, Structure structure) {
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

    public Date getInstallationDate() {
        return installationDate;
    }

    public void setInstallationDate(Date installationDate) {
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

    public Set<Result> getResults() {
        return results;
    }

    public void setResults(Set<Result> results) {
        this.results = new HashSet<>(results);
    }
}
    