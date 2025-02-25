package fr.uge.structsure.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;

import java.util.Objects;

@Entity
public class Result {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne
    @JoinColumns({
            @JoinColumn(name = "control_chip"),
            @JoinColumn(name = "measure_chip")
    })
    private Sensor sensor;

    @Enumerated(EnumType.ORDINAL)
    private State state;

    @JsonBackReference
    @ManyToOne
    private Scan scan;

    public Result() {}

    public Result(State state, Sensor sensor, Scan scan) {
        this.state = Objects.requireNonNull(state);
        this.sensor = Objects.requireNonNull(sensor);
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Sensor getSensor() {
        return sensor;
    }

    public void setSensor(Sensor sensor) {
        this.sensor = sensor;
    }

    public State getState() {
        return state;
    }

    public void setState(State state) {
        this.state = state;
    }

    public Scan getScan() {
        return scan;
    }

    public void setScan(Scan scan) {
        this.scan = scan;
    }
}