package fr.uge.structsure.entities;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.Objects;

@Entity
public class AppLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private LocalDateTime time;

    @ManyToOne
    private Account author;

    private String message;

    /**
     * Initialise the AppLog entity
     */
    public AppLog() {}

    /**
     * Initialise the AppLog entity
     * @param author the user that initiates the action
     * @param message the description of the action
     */
    public AppLog(Account author, String message) {
        this.author = author;
        this.message = Objects.requireNonNull(message);
        this.time = LocalDateTime.now();
    }

    public long getId() {
        return id;
    }
}