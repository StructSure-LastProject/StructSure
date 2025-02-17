package fr.uge.structsure.exceptions;

import fr.uge.structsure.dto.ErrorDTO;
import org.springframework.http.ResponseEntity;

import java.util.Objects;

/**
 * This is the centralized exception that will be used
 */
public class TraitementException extends Exception {
    private final Error error;

    public TraitementException(Error error) {
        this.error = Objects.requireNonNull(error);
    }

    public Error getError() {
        return error;
    }

    public ResponseEntity<?> toResponseEntity() {
        return ResponseEntity.status(error.code).body(new ErrorDTO(error.message));
    }

    @Override
    public synchronized Throwable fillInStackTrace() {
        return this;
    }
}
