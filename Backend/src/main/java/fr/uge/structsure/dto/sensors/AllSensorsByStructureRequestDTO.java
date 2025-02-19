package fr.uge.structsure.dto.sensors;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import fr.uge.structsure.exceptions.ErrorIdentifier;
import fr.uge.structsure.exceptions.TraitementException;
import fr.uge.structsure.utils.EnumValidatorFromString;
import fr.uge.structsure.utils.OrderEnum;
import fr.uge.structsure.utils.StateEnum;

import java.util.Date;
import java.util.Objects;

@JsonSerialize
public record AllSensorsByStructureRequestDTO(String orderByColumn, String orderType, String stateFilter, String planFilter,
                                              String minInstallationDate, String maxInstallationDate, Integer limit, Integer offset) {

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

    public void checkFields() throws TraitementException {
        if (Objects.isNull(orderByColumn) || Objects.isNull(orderType) || Objects.isNull(offset) || Objects.isNull(limit)) {
            throw new TraitementException(ErrorIdentifier.MISSING_FIELDS);
        }
        if (!EnumValidatorFromString.validateEnumValue(OrderByColumn.class, orderByColumn) ||
                !EnumValidatorFromString.validateEnumValue(OrderEnum.class, orderType) ||
                (!Objects.isNull(stateFilter) && !EnumValidatorFromString.validateEnumValue(StateEnum.class, stateFilter))) {
            throw new TraitementException(ErrorIdentifier.INCORRECT_FIELD_VALUE);
        }
    }
}
