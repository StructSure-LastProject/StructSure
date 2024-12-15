package fr.uge.structsure.entities;

import jakarta.persistence.*;

@Entity
public class Plan {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(nullable = false, length = 32)
    private String name;

    @Column(nullable = false, length = 128)
    private String section;

    private byte[] file;
    private Boolean archived;

    @ManyToOne
    private Structure structure;
}