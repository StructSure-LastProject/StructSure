package fr.uge.structsure.exceptions;

import java.util.Objects;

public record ErrorException(int code, String message) {
    public ErrorException {
        if (code < 0) throw new IllegalArgumentException("code < 0");
        Objects.requireNonNull(message);
    }
}
