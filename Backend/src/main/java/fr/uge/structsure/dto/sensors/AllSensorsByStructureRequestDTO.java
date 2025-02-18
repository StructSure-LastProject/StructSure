package fr.uge.structsure.dto.sensors;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import fr.uge.structsure.utils.OrderEnum;
import fr.uge.structsure.utils.StateEnum;

import java.util.Date;
import java.util.Objects;

@JsonSerialize
public record AllSensorsByStructureRequestDTO(OrderByColumn orderByColumn, OrderEnum orderType, StateEnum stateFilter, String planFilter,
                                              String minInstallationDate, String maxInstallationDate, int limit, int offset) {
    public AllSensorsByStructureRequestDTO {
        Objects.requireNonNull(orderByColumn);
        Objects.requireNonNull(orderType);
    }

    public enum OrderByColumn {
        NAME("name"),
        STATE("state"),
        INSTALLATION_DATE("installationDate");

        private final String value;

        OrderByColumn(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }
    }
}
