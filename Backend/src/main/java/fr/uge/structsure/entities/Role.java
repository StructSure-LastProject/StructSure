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

    /**
     * Method to get the Role enum constant based on the string value.
     *
     * @param value the string value to match.
     * @return the corresponding Role enum constant.
     * @throws IllegalArgumentException if no match is found.
     */
    public static Role fromValue(String value) {
        for (Role role : Role.values()) {
            if (role.value.equals(value)) {
                return role;
            }
        }
        throw new IllegalArgumentException("This role does not exist : " + value);
    }
}