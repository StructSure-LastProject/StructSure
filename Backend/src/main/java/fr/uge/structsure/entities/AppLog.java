package fr.uge.structsure.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

@Entity
public class AppLog {
    /** Formatter to export time */
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy - HH:mm:ss");

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

    void setId(long id) {
        this.id = id;
    }

    @JsonIgnore
    public long getId() {
        return id;
    }

    public String getTime() {
        return time.format(FORMATTER);
    }

    public String getAuthor() {
        return author == null ? "?" : author.getLogin();
    }

    public String getMessage() {
        return message;
    }
}