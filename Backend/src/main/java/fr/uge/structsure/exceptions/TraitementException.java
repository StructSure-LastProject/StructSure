package fr.uge.structsure.exceptions;

import java.util.Objects;

/**
 * This is the centralized exception that will be used
 */
public class TraitementException extends Exception {
    private final ErrorIdentifier errorIdentifier;

    public TraitementException(ErrorIdentifier errorIdentifier) {
        this.errorIdentifier = Objects.requireNonNull(errorIdentifier);
    }

    public ErrorIdentifier getErrorIdentifier() {
        return errorIdentifier;
    }

    @Override
    public synchronized Throwable fillInStackTrace() {
        return this;
    }

    @Override
    public synchronized Throwable fillInStackTrace() {
        return this;
    }
}
