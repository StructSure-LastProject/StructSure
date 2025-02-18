package fr.uge.structsure.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.Objects;

@Entity
public class Scan {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @JsonBackReference
    @ManyToOne
    private Structure structure;

    private LocalDateTime date;

    private String note;

    @ManyToOne
    private Account author;

    public Scan() {}

    public Scan(Structure structure, LocalDateTime date, String note, Account author) {
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

    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
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