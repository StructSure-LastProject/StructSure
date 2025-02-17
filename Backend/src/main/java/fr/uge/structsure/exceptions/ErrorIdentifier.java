package fr.uge.structsure.exceptions;

/**
 * Represents the identifiers for the errors
 */
public enum ErrorIdentifier {
    INVALID_USER_ACCOUNT_FIELDS,
    MISSING_USER_ACCOUNT_FIELDS,
    USER_ALREADY_EXISTS,
    ROLE_NOT_EXISTS,
    LOGIN_PASSWORD_NOT_CORRECT,
    STRUCTURE_NAME_ALREADY_EXISTS,
    STRUCTURE_NAME_IS_EMPTY,
    STRUCTURE_NOTE_EXCEED_LIMIT,
    STRUCTURE_NAME_EXCEED_LIMIT,
    STRUCTURE_ID_NOT_FOUND,
    PLAN_STRUCTURE_ID_IS_EMPTY,
    PLAN_STRUCTURE_NOT_FOUND,
    PLAN_ID_IS_EMPTY,
    PLAN_NAME_IS_EMPTY,
    PLAN_FILE_IS_EMPTY,
    PLAN_SECTION_IS_EMPTY,
    PLAN_NOT_FOUND,
    PLAN_FILE_INVALID_FORMAT,
    PLAN_NAME_EXCEED_LIMIT,
    PLAN_SECTION_EXCEED_LIMIT,
    PLAN_ALREADY_EXISTS,
    PLAN_SECTION_INVALID,
    PLAN_IS_ARCHIVED,
    SERVER_ERROR,
    SENSOR_STRUCTURE_NOT_FOUND,
    SENSOR_CHIP_TAGS_IS_EMPTY,
    SENSOR_NAME_IS_EMPTY,
    SENSOR_INSTALLATION_DATE_IS_EMPTY,
    SENSOR_POSITION_IS_EMPTY,
    SENSOR_NAME_ALREADY_EXISTS,
    SENSOR_CHIP_TAGS_ALREADY_EXISTS,
    SENSOR_CHIP_TAGS_EXCEED_LIMIT,
    SENSOR_NAME_EXCEED_LIMIT,
    SENSOR_NOTE_EXCEED_LIMIT,
    SENSOR_INSTALLATION_DATE_INVALID_FORMAT,
    SENSOR_CHIP_TAGS_ARE_IDENTICAL,
    LIST_STRUCTURES_EMPTY
}
