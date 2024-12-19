package fr.uge.structsure.entities;

import jakarta.persistence.*;

import java.util.Objects;

@Entity
public class Result {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne
    private Sensor sensor;

    @Enumerated(EnumType.STRING)
    private State state;

    @ManyToOne
    private Scan scan;

    public Result() {}

    public Result(State state, Sensor sensor, Scan scan) {
        this.state = Objects.requireNonNull(state);
        this.sensor = Objects.requireNonNull(sensor);
        this.scan = Objects.requireNonNull(scan);
    }
}