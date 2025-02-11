package fr.uge.structsure.entities;

import java.util.Objects;

public enum Role {
    OPERATEUR("Opérateur"),
    RESPONSABLE("Responsable"),
    ADMIN("Admin");

    public final String value;

    Role(String value){
        Objects.requireNonNull(value);
        this.value = value;
    }
}