package fr.uge.structsure.exceptions;

import java.util.Objects;

public record Error(int code, String message) {
    public Error {
        if (code < 0) throw new IllegalArgumentException("code < 0");
        Objects.requireNonNull(message);
    }
}
