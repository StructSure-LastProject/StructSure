package fr.uge.structsure.dto.structure;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import fr.uge.structsure.utils.OrderEnum;

import java.util.Objects;

@JsonSerialize
public record AllStructureRequestDTO(String searchByName, SortTypeEnum sortTypeEnum, OrderEnum order) {
    public AllStructureRequestDTO {
        Objects.requireNonNull(searchByName);
        Objects.requireNonNull(sortTypeEnum);
        Objects.requireNonNull(order);
    }

    public enum SortTypeEnum {
        STATE("state"),
        NUMBER_OF_SENSORS("numberOfSensors"),
        NAME("name");

        private final String value;

        SortTypeEnum(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }
    }
}
