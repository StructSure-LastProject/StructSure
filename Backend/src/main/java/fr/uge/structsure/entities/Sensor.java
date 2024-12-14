package com.example.springbootapi.entities;

import jakarta.persistence.*;

import java.sql.Time;
import java.time.LocalDateTime;
import java.util.Date;
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

    @Column(columnDefinition = "TEXT")
    @Temporal(TemporalType.TIME)
    private Time installationDate;

    @Column(columnDefinition = "REAL")
    private Double x;

    @Column(columnDefinition = "REAL")
    private Double y;

    @Column(columnDefinition = "INTEGER")
    private Boolean archived;

    @ManyToOne
    private Structure structure;

    //constructeurs nécéssaire
    public Sensor(){

    }

    public Sensor(String controlChip, String measureChip, String name, String note, Time installationDate, Double x, Double y, Boolean archived, Structure structure) {
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
}
    