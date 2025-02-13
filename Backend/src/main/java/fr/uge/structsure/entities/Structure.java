package fr.uge.structsure.entities;

import jakarta.persistence.*;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity
public class Structure {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(nullable = false, length = 64, unique = true)
    private String name;
    private String note;
    private Boolean archived=false;

    @OneToMany(mappedBy="structure")
    private Set<Sensor> sensors;

    @OneToMany(mappedBy="structure")
    private Set<Plan> plans;

    public Structure() {}
    public Structure(String name, String note, Boolean archived) {
        this.name = Objects.requireNonNull(name);
        this.note = Objects.requireNonNull(note);
        this.archived = archived;
    }
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

    public void setId(long id) {
        this.id = id;
    }

    public Set<Sensor> getSensors() {
        return sensors;
    }

    public void setSensors(Set<Sensor> sensors) {
        this.sensors = new HashSet<>(sensors);
    }

    public Set<Plan> getPlans() {
        return plans;
    }

    public void setPlans(Set<Plan> plans) {
        this.plans =  new HashSet<>(plans);
    }
}