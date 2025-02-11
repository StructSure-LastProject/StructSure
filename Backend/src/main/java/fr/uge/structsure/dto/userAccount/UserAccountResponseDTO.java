package fr.uge.structsure.dto.userAccount;

import java.util.Objects;

/**
 * Record to represent the response data
 * @param firstName The first name of the user
 * @param lastName The last name of the user
 * @param login The login of the user
 * @param role The role of the user
 * @param enabled The user account is enabled or not
 */
public record UserAccountResponseDTO(String firstName, String lastName, String login, String role, boolean enabled) {
  public UserAccountResponseDTO {
    Objects.requireNonNull(firstName);
    Objects.requireNonNull(lastName);
    Objects.requireNonNull(login);
    Objects.requireNonNull(role);
  }
}
