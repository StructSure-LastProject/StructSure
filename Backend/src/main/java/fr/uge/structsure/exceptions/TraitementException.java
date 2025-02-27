package fr.uge.structsure.exceptions;

import fr.uge.structsure.dto.ErrorDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;

import java.util.Objects;

/**
 * This is the centralized exception that will be used
 */
public class TraitementException extends Exception {
    private static final Logger LOGGER = LoggerFactory.getLogger(TraitementException.class);

    /** The error encapsulated by this exception */
    public final Error error;

    /**
     * Creates a new lightweight exception that can be sent to the
     * frontend using http response
     * @param error the error to signal to the user
     */
    public TraitementException(Error error) {
        this.error = Objects.requireNonNull(error);
    }

    /**
     * Creates a new Response from this exception's error code and
     * message.
     * @param log the log pattern where {} will be replaced by the
     *            TraitementException message
     * @return the corresponding response
     */
    public ResponseEntity<?> toResponseEntity(String log) {
        LOGGER.info(log, error.message);
        return ResponseEntity.status(error.code).body(new ErrorDTO(error.message));
    }

    /**
     * Creates a new Response from this exception's error code and
     * message.
     * @return the corresponding response
     */
    public ResponseEntity<?> toResponseEntity() {
        return ResponseEntity.status(error.code).body(new ErrorDTO(error.message));
    }

    @Override
    public synchronized Throwable fillInStackTrace() {
        return this;
    }
}
