package fr.uge.structsure.entities;

import jakarta.persistence.*;
import org.apache.juli.logging.Log;

import java.util.Objects;

/**
 * Account structure entity
 */
@Entity
public class AccountStructure {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Account Entity
     */
    @ManyToOne
    @JoinColumn(name = "account_login", nullable = false)
    private Account account;

    /**
     * Structure Entity
     */
    @ManyToOne
    @JoinColumn(name = "structure_id", nullable = false)
    private Structure structure;

    /**
     * Constructor
     */
    public AccountStructure(){}

    /**
     * Constructor
     * @param account The account object
     * @param structure The structure object
     */
    public AccountStructure(Account account, Structure structure){
        this.account = Objects.requireNonNull(account);
        this.structure = Objects.requireNonNull(structure);
    }

    public Long getId(){
        return id;
    }

    public void setId(Long id){
        this.id = Objects.requireNonNull(id);
    }

    public Account getAccount() {
        return account;
    }

    public void setAccount(Account account) {
        this.account = Objects.requireNonNull(account);
    }

    public Structure getStructure() {
        return structure;
    }

    public void setStructure(Structure structure) {
        this.structure = Objects.requireNonNull(structure);
    }
}
