package fr.uge.structsure.dto.userAccount;

import java.util.Objects;

public record UserAccountResponseDTO(String firstName, String lastName, String mail, String role, boolean enabled) {
  public UserAccountResponseDTO {
    Objects.requireNonNull(firstName);
    Objects.requireNonNull(lastName);
    Objects.requireNonNull(mail);
    Objects.requireNonNull(role);
  }
}
