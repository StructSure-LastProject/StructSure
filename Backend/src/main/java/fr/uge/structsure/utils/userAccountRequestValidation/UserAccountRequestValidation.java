package fr.uge.structsure.utils.userAccountRequestValidation;

import java.util.Objects;
import java.util.regex.Pattern;

/**
 * User account request validation class to verify the string values
 */
public class UserAccountRequestValidation {

    /**
     * Method to verify the string value to make sure the value contains only letters and spaces
     * @param str The string value
     * @return true if it contains only letters and spaces or false
     */
    public static boolean containsNonLetters(String str){
        Objects.requireNonNull(str);
        if (str.isEmpty()){
            return false;
        }
        var regex = "^[A-Za-zÀ-ÿ -]*$";
        var pattern = Pattern.compile(regex);
        return pattern.matcher(str).matches();
    }

    /**
     * Method to verify the string value to make sure the value contains only authorized characters
     * @param str The string value
     * @return true if it contains only letters/numbers/_/@/./- or false
     */
    public static boolean loginValidator(String str){
        Objects.requireNonNull(str);
        if (str.isEmpty()){
            return false;
        }
        var regex = "^[A-Za-z0-9_@.-]*$";
        var pattern = Pattern.compile(regex);
        return pattern.matcher(str).matches();
    }
}
