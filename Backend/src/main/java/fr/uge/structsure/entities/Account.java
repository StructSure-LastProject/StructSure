package fr.uge.structsure.entities;

import jakarta.persistence.*;
import org.hibernate.validator.internal.util.stereotypes.Lazy;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity
public class Account {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(unique = true, nullable = false, length = 128)
    private String login;

    @Column(length = 64)
    private String passwordEncrypted;

    @Column(nullable = false, length = 64)
    private String firstname;

    @Column(nullable = false, length = 64)
    private String lastname;

    private Role role;

    private Boolean enabled;

    @Lazy
    @ManyToMany(mappedBy = "accounts")
    private Set<Structure> structures = new HashSet<>();

    public Account() {}

    public Account(String login, String passwordEncrypted, String firstname, String lastname, Role role, boolean enabled) {
        this.login = Objects.requireNonNull(login);
        this.passwordEncrypted = Objects.requireNonNull(passwordEncrypted); // a modifier (spring security)
        this.firstname = Objects.requireNonNull(firstname);
        this.lastname = Objects.requireNonNull(lastname);
        this.role = Objects.requireNonNull(role);
        this.enabled = enabled;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    public String getLogin() {
        return login;
    }

    public String getPasswordEncrypted() {
        return passwordEncrypted;
    }

    public void setPasswordEncrypted(String passwordEncrypted) {
        this.passwordEncrypted = passwordEncrypted;
    }

    public String getFirstname() {
        return firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public Role getRole() {
        return role;
    }

    public Boolean getEnabled() {
        return enabled;
    }

    public Set<Structure> getAllowedStructures() {
        return structures;
    }

    public void add(Structure structure){
        structures.add(Objects.requireNonNull(structure));
    }

    public void remove(Structure structure){
        structures.remove(Objects.requireNonNull(structure));
    }


}