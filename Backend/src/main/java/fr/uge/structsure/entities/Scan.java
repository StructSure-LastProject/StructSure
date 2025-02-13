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
    private String date;

    private String note;

    @ManyToOne
    private Account author;

    public Scan() {}

    public Scan(Structure structure, String date, String note, Account author) {
        this.structure = Objects.requireNonNull(structure);
        this.date = Objects.requireNonNull(date);
        this.note = Objects.requireNonNull(note);
        this.author = Objects.requireNonNull(author);
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Structure getStructure() {
        return structure;
    }

    public void setStructure(Structure structure) {
        this.structure = structure;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public Account getAuthor() {
        return author;
    }

    public void setAuthor(Account author) {
        this.author = author;
    }
}