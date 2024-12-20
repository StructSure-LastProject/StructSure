package fr.uge.structsure.entities;


import jakarta.persistence.*;
import jakarta.validation.constraints.*;

import java.io.File;

@Entity
public class Plan {
    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    private long id;
    @Column(unique = true)
    @NotBlank(message = "Name is required")
    @Size(max = 32, message = "Name must be less than 32 characters")
    private String name;
    private String imageUrl;
    @NotBlank(message = "Section is required")
    @Size(max = 128, message = "Section must be less than 128 characters")
    private String section;
    @ManyToOne
    @JoinColumn(name = "structure_id", nullable = false)
    private Structure structure;

    public Plan() {}

    public Plan(String imageUrl, String name, String section, Structure structure) {
        this.imageUrl = imageUrl;
        this.name = name;
        this.section = section;
        this.structure = structure;
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

    public @NotBlank(message = "Section is required") @Size(max = 128, message = "Section must be less than 128 characters") String getSection() {
        return section;
    }

    public void setSection(@NotBlank(message = "Section is required") @Size(max = 128, message = "Section must be less than 128 characters") String section) {
        this.section = section;
    }
    public long getId() {
        return id;
    }
}