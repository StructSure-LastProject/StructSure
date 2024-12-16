package fr.uge.structsure.entities;

import jakarta.persistence.*;
import java.util.Objects;

@Entity
public class Structure {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(nullable = false, length = 64, unique = true)
    private String name;
    private String note;
    private Boolean archived;

    public Structure() {}
    public Structure(String name, String note, Boolean archived) {
        this.name = Objects.requireNonNull(name);
        this.note = Objects.requireNonNull(note);
        this.archived = archived;

    }

    public void setName(String name) {
        this.name = name;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public void setArchived(Boolean archived) {
        this.archived = archived;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getNote() {
        return note;
    }

    public Boolean getArchived() {
        return archived;
    }
}