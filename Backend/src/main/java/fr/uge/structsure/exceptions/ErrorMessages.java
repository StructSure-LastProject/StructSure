package fr.uge.structsure.exceptions;

import java.util.HashMap;

/**
 * This class will centralize and define all the errors that will be used
 */
public class ErrorMessages {

    private static final HashMap<ErrorIdentifier, Error> messages = new HashMap<>();

    static {
        messages.put(ErrorIdentifier.NO_USER, new Error(422, "Nom utilisateur déjà existant"));
        messages.put(ErrorIdentifier.ROLE_NOT_EXISTS, new Error(422, "Role inconnue"));
        messages.put(ErrorIdentifier.LOGIN_PASSWORD_NOT_CORRECT, new Error(404, "Login ou mot de passe incorrect"));
        messages.put(ErrorIdentifier.STRUCTURE_NOT_EXIST, new Error(404, "Ouvrage introuvable"));
        messages.put(ErrorIdentifier.NAME_EXISTS, new Error(422, "Le nom est déjà utilisé"));
        messages.put(ErrorIdentifier.TAGS_EXISTS, new Error(422, "L'un des deux tags ou les deux sont déjà utilisés"));
        messages.put(ErrorIdentifier.UNDEFINED_FIELD, new Error(422, "Les champs tags, nom, dateInstallation et position sont obligatoires"));
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
