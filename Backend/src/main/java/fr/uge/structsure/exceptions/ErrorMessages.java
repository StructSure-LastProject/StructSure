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

        messages.put(ErrorIdentifier.PLAN_STRUCTURE_ID_IS_EMPTY, new Error(422, "Le champ id est requis"));
        messages.put(ErrorIdentifier.PLAN_FILE_INVALID_FORMAT, new Error(422, "Format du fichier non valide"));
        messages.put(ErrorIdentifier.PLAN_FILE_IS_EMPTY, new Error(422, "Le champ file est requis"));
        messages.put(ErrorIdentifier.PLAN_NAME_IS_EMPTY, new Error(422, "Le champ name (metadata) est requis"));
        messages.put(ErrorIdentifier.PLAN_NAME_EXCEED_LIMIT, new Error(422, "Le champ name (metadata) dépasse le nombre de caractères (1-32 caractères)"));
        messages.put(ErrorIdentifier.PLAN_SESSION_EXCEED_LIMIT, new Error(422, "Le champ session (metadata) dépasse le nombre de caractères (0-128 caractères)"));
        messages.put(ErrorIdentifier.PLAN_ALREADY_EXISTS, new Error(422, "Plan déjà existant"));
        messages.put(ErrorIdentifier.SERVER_ERROR, new Error(500, "Une erreur est survenue"));

        messages.put(ErrorIdentifier.SENSOR_CHIP_TAGS_IS_EMPTY, new Error(422, "Les champs [controlChip | measureChip] sont obligatoires"));
        messages.put(ErrorIdentifier.SENSOR_NAME_IS_EMPTY, new Error(422, "Le champ name est obligatoire"));
        messages.put(ErrorIdentifier.SENSOR_INSTALLATION_DATE_IS_EMPTY, new Error(422, "Le champ installationDate est obligatoire"));
        messages.put(ErrorIdentifier.SENSOR_POSITION_IS_EMPTY, new Error(422, "Les champs [x | y] sont obligatoires"));
        messages.put(ErrorIdentifier.SENSOR_CHIP_TAGS_EXCEED_LIMIT, new Error(422, "L'un des tags dépasse le nombre de caractères (1-32 caractères)"));
        messages.put(ErrorIdentifier.SENSOR_NAME_EXCEED_LIMIT, new Error(422, "Le nom dépasse le nombre de caractères (1-32 caractères)"));
        messages.put(ErrorIdentifier.SENSOR_NOTE_EXCEED_LIMIT, new Error(422, "La note dépasse le nombre de caractères (0-1000 caractères)"));
        messages.put(ErrorIdentifier.SENSOR_NAME_ALREADY_EXISTS, new Error(422, "Le nom est déjà utilisé"));
        messages.put(ErrorIdentifier.SENSOR_CHIP_TAGS_ALREADY_EXISTS, new Error(422, "L'un des deux tags ou les deux sont déjà utilisés"));
        messages.put(ErrorIdentifier.SENSOR_STRUCTURE_NOT_FOUND, new Error(404, "Ouvrage introuvable"));
        messages.put(ErrorIdentifier.SENSOR_INSTALLATION_DATE_INVALID_FORMAT, new Error(422, "La date d’installation doit être au format AAAA-MM-JJ"));
        messages.put(ErrorIdentifier.SENSOR_CHIP_TAGS_ARE_IDENTICAL, new Error(422, "Les tags sont identiques"));
        messages.put(ErrorIdentifier.LIST_STRUCTURES_EMPTY, new Error(404, "Aucun ouvrage enregistré dans le système"));
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
