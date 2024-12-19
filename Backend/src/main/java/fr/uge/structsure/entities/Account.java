package fr.uge.structsure.entities;

import jakarta.persistence.*;

import java.util.Objects;

@Entity
public class Account {
    @Id
    @Column(unique = true, nullable = false, length = 128)
    private String login;

    @Column(nullable = false, length = 64)
    private String passwordEncrypted; // pensez à utiliser Spring Security !

    @Column(nullable = false, length = 64)
    private String firstname;

    @Column(nullable = false, length = 64)
    private String lastname;

    @Enumerated(EnumType.STRING)
    private Role role;

    @Column(nullable = false, length = 255)
    private String mail;

    private Boolean enabled;

    public Account() {}

    public Account(String login, String passwordEncrypted, String firstname, String lastname, Role role, String mail, boolean enabled) {
        this.login = Objects.requireNonNull(login);
        this.passwordEncrypted = Objects.requireNonNull(passwordEncrypted); // a modifier (spring security)
        this.firstname = Objects.requireNonNull(firstname);
        this.lastname = Objects.requireNonNull(lastname);
        this.role = Objects.requireNonNull(role);
        this.mail = Objects.requireNonNull(mail);
        this.enabled = enabled;
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

    public String getMail() {
        return mail;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }

    public Boolean getEnabled() {
        return enabled;
    }
}