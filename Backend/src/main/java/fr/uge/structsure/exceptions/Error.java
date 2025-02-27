package fr.uge.structsure.exceptions;

import java.util.Objects;

/**
 * This class will centralize and define all the errors that will be used
 */
public enum Error {
    INVALID_FIELDS(422, "Les champs sont invalides"),
    INCORRECT_FIELD_VALUE(422, "Une ou plusieurs valeur(s) invalide(s)"),
    MISSING_FIELDS(422, "Un ou plusieurs champs manquant"),
    UNAUTHORIZED_OPERATION(422, "Permission insuffisante"),
    INVALID_TOKEN(401, "Session expirée"),
    MISSING_USER_ACCOUNT_FIELDS(422, "Des champs sont manquants"),
    INVALID_USER_ACCOUNT_FIELDS(422, "Les champs sont invalides"),
    USER_ALREADY_EXISTS(422, "Identifiant déjà utilisé"),
    USER_ACCOUNT_NOT_FOUND(404, "Compte utilisateur non trouvé"),
    SUPER_ADMIN_ACCOUNT_CANT_BE_MODIFIED(422, "Les informations d’un compte super-administrateur ne peut pas être modifié"),
    ADMIN_ACCOUNT_CANT_BE_MODIFIED_BY_AN_ADMIN_ACCOUNT(422, "Les informations d’un compte administrateur ne peut pas être modifié"),
    ROLE_NOT_EXISTS(422, "Rôle inconnu"),
    ORDER_NOT_EXISTS(422, "L'ordre n'existe pas"),
    ORDER_BY_COLUMN_NAME_NOT_EXISTS(422, "L'ordre par le nom de la colonne n'existe pas"),
    LOGIN_PASSWORD_NOT_CORRECT(404, "Login ou mot de passe incorrect"),
    PASSWORD_NOT_VALID(422, "Le mot de passe doit comporter entre 12 et 64 caractères"),
    STRUCTURE_NAME_ALREADY_EXISTS(422, "Nom d'ouvrage déjà existant"),
    STRUCTURE_NAME_IS_EMPTY(422, "Le nom d'un ouvrage ne peut pas être vide"),
    STRUCTURE_ID_NOT_FOUND(404, "Id de l'ouvrage est introuvable"),
    STRUCTURE_NAME_EXCEED_LIMIT(422, "Le nom d'un ouvrage ne peut pas dépasser 64 caractères"),
        STRUCTURE_NOTE_EXCEED_LIMIT(422, "La note d'un ouvrage ne peut pas dépasser 1000 caractères"),

    PLAN_NOT_BELONG_TO_STRUCTURE(422, "Plan n’appartient pas à l’ouvrage"),
    PLAN_STRUCTURE_ID_IS_EMPTY(422, "Le champ structureId est requis"),
    PLAN_STRUCTURE_NOT_FOUND(404, "Ouvrage introuvable"),
    PLAN_FILE_INVALID_FORMAT(422, "Format du fichier non valide"),
    PLAN_FILE_IS_EMPTY(422, "Le champ file est requis"),
    PLAN_NAME_IS_EMPTY(422, "Le champ name (metadata) est requis"),
    PLAN_NAME_EXCEED_LIMIT(422, "Le champ name (metadata) dépasse le nombre de caractères (1-32 caractères)"),
    PLAN_SECTION_EXCEED_LIMIT(422, "Le champ section (metadata) dépasse le nombre de caractères (0-128 caractères)"),
    PLAN_ALREADY_EXISTS(422, "Plan déjà existant"),
    PLAN_SECTION_INVALID(422, "Le champ section (metadata) est invalide"),
    SERVER_ERROR(500, "Une erreur est survenue"),
    PLAN_STRUCTURE_MISMATCH(400, "Ce plan n'appartient pas à cet ouvrage"),
    PLAN_FILE_NOT_FOUND(404, "L'image du plan est introuvable"),

    PLAN_ID_IS_EMPTY(422, "Le champ planId est requis"),
    PLAN_NOT_FOUND(404, "Plan introuvable"),
    PLAN_SECTION_IS_EMPTY(422, "Le champ section (metadata) est requis"),
    PLAN_IS_ARCHIVED(409, "Le plan à été archivé"),
    STRUCTURE_IS_ARCHIVED(409, "L'ouvrage à été archivé"),

    SENSOR_NOT_FOUND(404, "Capteur introuvable"),
    SENSOR_CHIP_TAGS_IS_EMPTY(422, "Les champs [controlChip | measureChip] sont obligatoires"),
    SENSOR_NAME_IS_EMPTY(422, "Le champ name est obligatoire"),
    SENSOR_COMMENT_IS_EMPTY(422, "Le champ note est obligatoire"),
    SENSOR_INSTALLATION_DATE_IS_EMPTY(422, "Le champ installationDate est obligatoire"),
    SENSOR_POSITION_IS_EMPTY(422, "Les champs [x | y] sont obligatoires"),
    SENSOR_CHIP_TAGS_EXCEED_LIMIT(422, "L'un des tags dépasse le nombre de caractères (1-32 caractères)"),
    SENSOR_NAME_EXCEED_LIMIT(422, "Le nom dépasse le nombre de caractères (1-32 caractères)"),
    SENSOR_COMMENT_EXCEED_LIMIT(422, "La note dépasse le nombre de caractères (0-1000 caractères)"),
    SENSOR_NAME_ALREADY_EXISTS(422, "Le nom est déjà utilisé"),
    SENSOR_CHIP_TAGS_ALREADY_EXISTS(422, "L'un des deux tags ou les deux sont déjà utilisés"),
    SENSOR_STRUCTURE_NOT_FOUND(404, "Ouvrage introuvable"),
    SENSOR_INSTALLATION_DATE_INVALID_FORMAT(422, "La date d’installation doit être au format AAAA-MM-JJ"),
    SENSOR_CHIP_TAGS_ARE_IDENTICAL(422, "Les tags sont identiques"),
    SENSOR_STRUCTURE_ID_IS_EMPTY(422, "Le champ structureId est obligatoire"),

    LIST_STRUCTURES_EMPTY(404, "Aucun ouvrage enregistré dans le système"),
    DATE_FORMAT_ERROR(422, "Le format de la date n'est pas correct JJ-MM-AAAA"),
    DATE_TIME_ISO_FORMAT_ERROR(422, "Le format de la date n'est pas correct AAAA-MM-JJTHH:MM:SS"),
    DATE_TIME_FORMAT_ERROR(422, "Le format de la date n'est pas correct AAAA-MM-JJ HH:MM:SS.SSS");


    public final int code;
    public final String message;

    /**
     * The constructor for the Error
     * @param code the code of the error
     * @param message the message of the error
     */
    private Error(int code, String message) {
        if (code < 0) throw new IllegalArgumentException("code < 0");
        this.code = code;
        this.message = Objects.requireNonNull(message);
    }
}
