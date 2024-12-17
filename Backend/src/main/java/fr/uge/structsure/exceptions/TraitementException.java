package fr.uge.structsure.exceptions;

public class TraitementException extends Exception {
    private final int code;

    public TraitementException(int code) {
        if (code < 0) {
            throw new IllegalArgumentException("code < 0");
        }
        this.code = code;
    }

    public int getCode() {
        return code;
    }
}
