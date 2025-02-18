package fr.uge.structsure.dto.structure;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import fr.uge.structsure.utils.OrderEnum;

import java.util.Objects;

/**
 * The dto for the all structure request
 * @param searchByName the name to search with
 * @param orderByColumnName the column to order with
 * @param orderType the order type (ASC or DESC)
 */
@JsonSerialize
public record AllStructureRequestDTO(String searchByName, OrderByColumn orderByColumnName, OrderEnum orderType) {
    public AllStructureRequestDTO {
        Objects.requireNonNull(searchByName);
        Objects.requireNonNull(orderByColumnName);
        Objects.requireNonNull(orderType);
    }

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
}
