package fr.uge.structsure.exceptions;

import java.util.HashMap;

/**
 * This class will centralize and define all the errors that will be used
 */
public class ErrorMessages {

    private static final HashMap<ErrorIdentifier, Error> messages = new HashMap<>();

    static {
        messages.put(ErrorIdentifier.UNAUTHORIZED_OPERATION, new Error(422, "Permission insuffisante"));
        messages.put(ErrorIdentifier.INVALID_TOKEN, new Error(401, "Session expirée"));
        messages.put(ErrorIdentifier.MISSING_USER_ACCOUNT_FIELDS, new Error(422, "Des champs sont manquants"));
        messages.put(ErrorIdentifier.INVALID_USER_ACCOUNT_FIELDS, new Error(422, "Les champs sont invalides"));
        messages.put(ErrorIdentifier.USER_ALREADY_EXISTS, new Error(422, "Identifiant déjà utilisé"));
        messages.put(ErrorIdentifier.USER_ACCOUNT_NOT_FOUND, new Error(404, "Compte utilisateur non trouvé"));
        messages.put(ErrorIdentifier.SUPER_ADMIN_ACCOUNT_CANT_BE_MODIFIED, new Error(422, "Le rôle d’un compte super-administrateur ne peut pas être modifié"));
        messages.put(ErrorIdentifier.ADMIN_ACCOUNT_CANT_BE_MODIFIED_BY_AN_ADMIN_ACCOUNT, new Error(422, "Le rôle d’un compte administrateur ne peut pas être modifié"));
        messages.put(ErrorIdentifier.ROLE_NOT_EXISTS, new Error(422, "Rôle inconnu"));
        messages.put(ErrorIdentifier.LOGIN_PASSWORD_NOT_CORRECT, new Error(404, "Login ou mot de passe incorrect"));
        messages.put(ErrorIdentifier.STRUCTURE_NAME_ALREADY_EXISTS, new Error(422, "Nom d'ouvrage déjà existant"));
        messages.put(ErrorIdentifier.STRUCTURE_NAME_IS_EMPTY, new Error(422, "Le nom d'un ouvrage ne peut pas être vide"));
        messages.put(ErrorIdentifier.STRUCTURE_ID_NOT_FOUND, new Error(404, "Id de l'ouvrage est introuvable"));
        messages.put(ErrorIdentifier.STRUCTURE_NAME_EXCEED_LIMIT, new Error(422, "Le nom d'un ouvrage ne peut pas dépasser 64 caractères"));
        messages.put(ErrorIdentifier.STRUCTURE_NOTE_EXCEED_LIMIT, new Error(422, "La note d'un ouvrage ne peut pas dépasser 1000 caractères"));

        messages.put(ErrorIdentifier.PLAN_STRUCTURE_ID_IS_EMPTY, new Error(422, "Le champ structureId est requis"));
        messages.put(ErrorIdentifier.PLAN_STRUCTURE_NOT_FOUND, new Error(404, "Ouvrage introuvable"));
        messages.put(ErrorIdentifier.PLAN_FILE_INVALID_FORMAT, new Error(422, "Format du fichier non valide"));
        messages.put(ErrorIdentifier.PLAN_FILE_IS_EMPTY, new Error(422, "Le champ file est requis"));
        messages.put(ErrorIdentifier.PLAN_NAME_IS_EMPTY, new Error(422, "Le champ name (metadata) est requis"));
        messages.put(ErrorIdentifier.PLAN_NAME_EXCEED_LIMIT, new Error(422, "Le champ name (metadata) dépasse le nombre de caractères (1-32 caractères)"));
        messages.put(ErrorIdentifier.PLAN_SECTION_EXCEED_LIMIT, new Error(422, "Le champ section (metadata) dépasse le nombre de caractères (0-128 caractères)"));
        messages.put(ErrorIdentifier.PLAN_ALREADY_EXISTS, new Error(422, "Plan déjà existant"));
        messages.put(ErrorIdentifier.PLAN_SECTION_INVALID, new Error(422, "Le champ section (metadata) est invalide"));
        messages.put(ErrorIdentifier.SERVER_ERROR, new Error(500, "Une erreur est survenue"));

        messages.put(ErrorIdentifier.PLAN_ID_IS_EMPTY, new Error(422, "Le champ planId est requis"));
        messages.put(ErrorIdentifier.PLAN_NOT_FOUND, new Error(404, "Plan introuvable"));
        messages.put(ErrorIdentifier.PLAN_SECTION_IS_EMPTY, new Error(422, "Le champ section (metadata) est requis"));
        messages.put(ErrorIdentifier.PLAN_IS_ARCHIVED, new Error(409, "Le plan à été archivé"));

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
