package fr.uge.structsure.dto.structure;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import fr.uge.structsure.entities.State;
import fr.uge.structsure.exceptions.Error;
import fr.uge.structsure.exceptions.TraitementException;
import fr.uge.structsure.utils.OrderEnum;

import java.util.Objects;
import java.util.Optional;

/**
 * The dto for the all structure request
 * @param searchByName the name to search with
 * @param orderByColumnName the column to order with
 * @param searchByState the state to filter
 * @param orderType the order type (ASC or DESC)
 */
@JsonSerialize
public record AllStructureRequestDTO(String searchByName, Optional<State> searchByState, String orderByColumnName, String orderType) {
    /**
     * The orderByColumn enum
     */
    public enum OrderByColumn {
        STATE("state"),
        NUMBER_OF_SENSORS("numberOfSensors"),
        NAME("name");

        private final String value;

        /**
         * Creates the orderByColumn enum with value
         * @param value the value the column
         */
        OrderByColumn(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }
    }

    public void checkFields() throws TraitementException {
        if (Objects.isNull(orderType) || Objects.isNull(orderByColumnName) || Objects.isNull(searchByName)) {
            throw new TraitementException(Error.MISSING_USER_ACCOUNT_FIELDS);
        }
        if (!orderType.equalsIgnoreCase("asc") && !orderType.equalsIgnoreCase("desc")) {
            throw new TraitementException(Error.ORDER_NOT_EXISTS);
        }
        if (!orderByColumnName.equals("STATE") && !orderByColumnName.equals("NUMBER_OF_SENSORS") && !orderByColumnName.equals("NAME")) {
            throw new TraitementException(Error.ORDER_BY_COLUMN_NAME_NOT_EXISTS);
        }
    }
}
