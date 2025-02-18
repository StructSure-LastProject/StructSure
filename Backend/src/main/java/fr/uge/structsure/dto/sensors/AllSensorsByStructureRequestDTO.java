package fr.uge.structsure.dto.sensors;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import fr.uge.structsure.utils.OrderEnum;
import fr.uge.structsure.utils.StateEnum;

import java.util.Date;
import java.util.Objects;

@JsonSerialize
public record AllSensorsByStructureRequestDTO(String orderByColumn, OrderEnum orderType, StateEnum stateFilter,
                                              String minInstallationDate, String maxInstallationDate) {
    public AllSensorsByStructureRequestDTO {
        Objects.requireNonNull(orderByColumn);
        Objects.requireNonNull(orderType);
        Objects.requireNonNull(stateFilter);
    }
}
