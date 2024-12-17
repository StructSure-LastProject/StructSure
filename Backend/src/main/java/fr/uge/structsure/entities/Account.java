package fr.uge.structsure.entities;

import jakarta.persistence.*;

import java.util.Objects;

@Entity
public class Account {
    @Id
    @Column(unique = true, nullable = false, length = 128)
    private String login;

    @Column(nullable = false, length = 64)
    private String passwordCrypted; // pensez à utiliser Spring Security !

    @Column(nullable = false, length = 64)
    private String firstname;

    @Column(nullable = false, length = 64)
    private String lastname;

    @Enumerated(EnumType.STRING)
    private Role role;

    private Boolean enabled;

    public Account() {}

    public Account(String login, String passwordCrypted, String firstname, String lastname, Role role, boolean enabled) {
        this.login = Objects.requireNonNull(login);
        this.passwordCrypted = Objects.requireNonNull(passwordCrypted); // a modifier (spring security)
        this.firstname = Objects.requireNonNull(firstname);
        this.lastname = Objects.requireNonNull(lastname);
        this.role = Objects.requireNonNull(role);
        this.enabled = enabled;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public void setPasswordCrypted(String passwordCrypted) {
        this.passwordCrypted = passwordCrypted;
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

    public String getPasswordCrypted() {
        return passwordCrypted;
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
}