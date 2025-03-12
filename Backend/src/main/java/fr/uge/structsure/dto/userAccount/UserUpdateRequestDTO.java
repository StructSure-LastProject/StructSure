package fr.uge.structsure.dto.userAccount;

import fr.uge.structsure.entities.Account;
import fr.uge.structsure.utils.DiffMaker;

import java.util.Objects;

/**
 * The record that represents the role request
 * @param firstname Firstname
 * @param lastname Lastname
 * @param login login
 * @param password password
 * @param role role
 * @param accountState Account state
 */
public record UserUpdateRequestDTO(String firstname, String lastname, String login, String password, String role, boolean accountState) {
    public UserUpdateRequestDTO {
        Objects.requireNonNull(login);
        Objects.requireNonNull(password);
        Objects.requireNonNull(firstname);
        Objects.requireNonNull(lastname);
        Objects.requireNonNull(role);
    }

    /**
     * Calculates a string with all the fields that got updated to add
     * them in the logs with a before/after comparison.
     * @param account the account with the previous values
     * @return the difference between the current values and the new ones
     */
    public String logDiff(Account account) {
        return new DiffMaker()
            .add("Prénom", account.getFirstname(), firstname)
            .add("Nom", account.getLastname(), lastname)
            .add("Rôle", account.getRole().value, role)
            .add("Actif", account.getEnabled().toString(), accountState + "")
            .add(!password.isEmpty(), () -> "Mot de passe mis à jour")
            .toString();
    }
}
