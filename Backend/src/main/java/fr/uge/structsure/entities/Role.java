package fr.uge.structsure.entities;

import java.util.Objects;

/**
 * Enum representing the different roles within the system.
 * Each role is associated with a specific string value that is used to identify it.
 * The available roles are:
 * <ul>
 *     <li>{@link #OPERATEUR} - The operator role</li>
 *     <li>{@link #RESPONSABLE} - The responsible role</li>
 *     <li>{@link #ADMIN} - The admin role</li>
 * </ul>
 */
public enum Role {
    OPERATEUR("Opérateur"),
    RESPONSABLE("Responsable"),
    ADMIN("Admin");

    public final String value;

    /**
     * Constructor for creating a Role enum with the specified string value.
     *
     * @param value The string value associated with the role.
     * @throws NullPointerException if the value is {@code null}.
     */
    Role(String value){
        Objects.requireNonNull(value);
        this.value = value;
    }
}