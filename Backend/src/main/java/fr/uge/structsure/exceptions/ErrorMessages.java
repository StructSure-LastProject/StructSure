package fr.uge.structsure.exceptions;

import java.util.HashMap;

public class ErrorMessages {

    private static final HashMap<Integer, ErrorException> messages = new HashMap<>();

    static {
        messages.put(1, new ErrorException(422, "Nom utilisateur déjà existant"));
        messages.put(2, new ErrorException(422, "Role inconnue"));
        messages.put(3, new ErrorException(404, "Login ou mot de passe incorrect"));
    }

    public static ErrorException getErrorMessage(int errorCode) {
        return messages.get(errorCode);
    }
}
