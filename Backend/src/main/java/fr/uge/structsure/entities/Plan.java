package fr.uge.structsure.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;

import java.io.File;
import java.util.Set;

@Entity
public class Plan {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private long id;
    @Column(unique = true)
    @NotBlank(message = "Name is required")
    @Size(max = 32, message = "Name must be less than 32 characters")
    private String name;
    private boolean archived;
    private String imageUrl;
    @Size(max = 128, message = "Section must be less than 128 characters")
    private String section = "";

    @JsonBackReference
    @ManyToOne
    @JoinColumn(name = "structure_id", nullable = false)
    private Structure structure;

    @OneToMany(mappedBy="plan")
    private Set<Sensor> sensors;

    public Plan() {}

    public Plan(String imageUrl, String name, Structure structure) {
        this.imageUrl = imageUrl;
        this.name = name;
        this.structure = structure;
    }

    public Plan(String imageUrl, String name, String section, Structure structure) {
        this.imageUrl = imageUrl;
        this.name = name;
        this.structure = structure;
        this.section = section;
    }

    public Plan(long id, String name, boolean archived, String imageUrl, String section, Structure structure, Set<Sensor> sensors) {
        this.id = id;
        this.name = name;
        this.archived = archived;
        this.imageUrl = imageUrl;
        this.section = section;
        this.structure = structure;
        this.sensors = sensors;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isArchived() {
        return archived;
    }

    public void setArchived(boolean archived) {
        this.archived = archived;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getSection() {
        return section;
    }

    public void setSection(String section) {
        this.section = section;
    }

    public Structure getStructure() {
        return structure;
    }

    public void setStructure(Structure structure) {
        this.structure = structure;
    }

    public Set<Sensor> getSensors() {
        return sensors;
    }

    public void setSensors(Set<Sensor> sensors) {
        this.sensors = sensors;
    }

    public Structure getStructure() {
        return structure;
    }
}