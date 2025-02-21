package fr.uge.structsure.utils;

/**
 * Contains helper method for enum processing
 */
public class EnumValidatorFromString {

    /**
     * Will check that the value is fied in the enum
     * @param enumClass the enum class
     * @param value the value to check
     * @return boolean true if the value is present in the enum and false if not
     * @param <T> the type parameter
     */
    public static <T extends Enum<T>> boolean validateEnumValue(Class<T> enumClass, String value) {
        for (T enumValue : enumClass.getEnumConstants()) {
            if (enumValue.name().equals(value)) {
                return true;
            }
        }
        return false;
    }
}
