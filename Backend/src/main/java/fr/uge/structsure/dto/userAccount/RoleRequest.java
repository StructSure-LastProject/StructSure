package fr.uge.structsure.dto.userAccount;

import java.util.Objects;

/**
 * The record that represents the role request
 * @param role The role
 */
public record RoleRequest(String role) {
    public RoleRequest {
        Objects.requireNonNull(role);
    }
}
