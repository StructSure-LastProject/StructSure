package fr.uge.structsure.entities;

import jakarta.persistence.*;
import org.hibernate.validator.constraints.Length;

import java.util.Objects;

@Entity
public class Structure {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @Column(nullable = false, unique = true)
    @Length(min = 1, max = 64)
    private String name;
    @Length(max = 1000)
    private String note;
    private Boolean archived = false;

    public Structure() {}
    public Structure(String name, String note) {
        this.name = Objects.requireNonNull(name);
        this.note = Objects.requireNonNull(note);
    }

    public long getId() {
        return id;
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