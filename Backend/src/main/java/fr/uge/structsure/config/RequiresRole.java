package fr.uge.structsure.config;

import fr.uge.structsure.entities.Role;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Role restriction for API endpoints. Any role higher than the
 * specified role will be authorized.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface RequiresRole {
    /** The minimum required role to access the endpoint */
    Role value();
}
