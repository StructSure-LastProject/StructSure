package fr.uge.structsure.entities;

import jakarta.persistence.*;

import java.sql.Time;
import java.util.Objects;

@Entity
public class Scan {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne
    private Structure structure;

    @Column(columnDefinition = "TEXT")
    @Temporal(TemporalType.TIME)
    private Time date;

    private String note;

    @ManyToOne
    private Account author;

    public Scan() {}

    public Scan(Structure structure, Time date, String note, Account author) {
        this.structure = Objects.requireNonNull(structure);
        this.date = Objects.requireNonNull(date);
        this.note = Objects.requireNonNull(note);
        this.author = Objects.requireNonNull(author);
    }
}