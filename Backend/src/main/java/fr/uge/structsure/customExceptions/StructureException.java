package fr.uge.structsure.customExceptions;

import java.util.Objects;

public class StructureException extends RuntimeException {

    private final int code;
    private final String error;

    @Override
    public synchronized Throwable fillInStackTrace() {
        return this;
    }

    public StructureException(int code, String error){
        super(error);
        Objects.requireNonNull(error);
        this.code = code;
        this.error = error;
    }

}
