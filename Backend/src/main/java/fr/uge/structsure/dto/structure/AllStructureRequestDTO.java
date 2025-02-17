package fr.uge.structsure.dto.structure;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import fr.uge.structsure.utils.OrderEnum;

import java.util.Objects;

@JsonSerialize
public record AllStructureRequestDTO(String searchByName, OrderByColumn orderByColumnName, OrderEnum orderType) {
    public AllStructureRequestDTO {
        Objects.requireNonNull(searchByName);
        Objects.requireNonNull(orderByColumnName);
        Objects.requireNonNull(orderType);
    }

    public enum OrderByColumn {
        STATE("state"),
        NUMBER_OF_SENSORS("numberOfSensors"),
        NAME("name");

        private final String value;

        OrderByColumn(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }
    }
}
