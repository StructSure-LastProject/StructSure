package fr.uge.structsure.dto.userAccount;

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
}
