package fr.uge.structsure.entities;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import org.hibernate.validator.internal.util.stereotypes.Lazy;

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
    private Boolean archived;

    @OneToMany(mappedBy="structure")
    private Set<Sensor> sensors;

    @OneToMany(mappedBy="structure")
    private Set<Plan> plans;

    @JsonManagedReference
    @OneToMany(mappedBy="structure")
    private Set<Sensor> sensors;


    @JsonManagedReference
    @OneToMany(mappedBy="structure")
    private Set<Plan> plans;

    @Lazy
    @JoinTable(
            name = "structure_account",
            joinColumns = @JoinColumn(name = "id"),
            inverseJoinColumns = @JoinColumn(name = "login")
    )
    @ManyToMany(cascade = {CascadeType.DETACH})
    private Set<Account> accounts = new HashSet<>();

    /**
     * Initialise the structure entity
     */
    public Structure() {}

    /**
     * Initialise the structure entity
     * @param name the name
     * @param note the note
     * @param archived if its archived or not
     */
    public Structure(String name, String note, Boolean archived) {
        this.name = Objects.requireNonNull(name);
        this.note = Objects.requireNonNull(note);
        this.archived = archived;
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

    public Set<Account> getAccountStructures() {
        return accounts;
    }

    public void add(Account account){
        this.accounts.add(Objects.requireNonNull(account));
    }

    public void remove(Account account){
        this.accounts.remove(Objects.requireNonNull(account));
    }
}