package fr.uge.structsure.entities;


import jakarta.persistence.*;
import jakarta.validation.constraints.*;

import java.io.File;

@Entity
public class Plan {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private long id;
    @Column(unique = true)
    @NotBlank(message = "Name is required")
    @Size(max = 32, message = "Name must be less than 32 characters")
    private String name;
    private boolean archived = false;
    private String imageUrl;
    @Size(max = 128, message = "Section must be less than 128 characters")
    private String section = "";
    @ManyToOne
    @JoinColumn(name = "structure_id", nullable = false)
    private Structure structure;

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

    public @NotBlank(message = "Name is required") @Size(max = 32, message = "Name must be less than 32 characters") String getName() {
        return name;
    }

    public void setName(@NotBlank(message = "Name is required") @Size(max = 32, message = "Name must be less than 32 characters") String name) {
        this.name = name;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public @Size(max = 128, message = "Section must be less than 128 characters") String getSection() {
        return section;
    }

    public void setSection(@Size(max = 128, message = "Section must be less than 128 characters") String section) {
        this.section = section;
    }
    public long getId() {
        return id;
    }

    public Boolean getArchived() {
        return archived;
    }

    public void setArchived(Boolean archived) {
        this.archived = archived;
    }
}