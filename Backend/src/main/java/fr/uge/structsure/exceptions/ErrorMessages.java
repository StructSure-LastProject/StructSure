package fr.uge.structsure.exceptions;

import java.util.HashMap;

/**
 * This class will centralize and define all the errors that will be used
 */
public class ErrorMessages {

    private static final HashMap<ErrorIdentifier, Error> messages = new HashMap<>();

    static {
        messages.put(ErrorIdentifier.USER_ALREADY_EXISTS, new Error(422, "Nom utilisateur déjà existant"));
        messages.put(ErrorIdentifier.ROLE_NOT_EXISTS, new Error(422, "Role inconnue"));
        messages.put(ErrorIdentifier.LOGIN_PASSWORD_NOT_CORRECT, new Error(404, "Login ou mot de passe incorrect"));
        messages.put(ErrorIdentifier.STRUCTURE_NAME_ALREADY_EXISTS, new Error(422, "Nom d'ouvrage déjà existant"));
        messages.put(ErrorIdentifier.STRUCTURE_NAME_IS_EMPTY, new Error(422, "Le nom d'un ouvrage ne peut pas être vide"));
        messages.put(ErrorIdentifier.STRUCTURE_ID_NOT_FOUND, new Error(404, "Id de l'ouvrage est introuvable"));
        messages.put(ErrorIdentifier.STRUCTURE_NAME_EXCEED_LIMIT, new Error(422, "Le nom d'un ouvrage ne peut pas dépasser 64 caractères"));
        messages.put(ErrorIdentifier.STRUCTURE_NOTE_EXCEED_LIMIT, new Error(422, "La note d'un ouvrage ne peut pas dépasser 1000 caractères"));
        messages.put(ErrorIdentifier.PLAN_FILE_INVALID_FORMAT, new Error(422, "Format du fichier non valide"));
        messages.put(ErrorIdentifier.PLAN_FILE_IS_EMPTY, new Error(422, "Le fichier ne peut pas être vide"));
        messages.put(ErrorIdentifier.PLAN_NAME_IS_EMPTY, new Error(422, "Le nom du fichier ne peut pas être vide"));
        messages.put(ErrorIdentifier.SERVER_ERROR, new Error(500, "Une erreur est survenue"));
    }

    /**
     * It will return the error object corresponding to the error code.
     * @param errorIdentifier the error identifier
     * @return Error the error corresponding to the identifier
     */
    public static Error getErrorMessage(ErrorIdentifier errorIdentifier) {
        return messages.get(errorIdentifier);
    }
}
