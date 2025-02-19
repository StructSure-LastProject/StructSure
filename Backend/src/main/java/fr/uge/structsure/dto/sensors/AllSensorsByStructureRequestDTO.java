package fr.uge.structsure.dto.sensors;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import fr.uge.structsure.exceptions.Error;
import fr.uge.structsure.exceptions.TraitementException;
import fr.uge.structsure.utils.EnumValidatorFromString;
import fr.uge.structsure.utils.OrderEnum;
import fr.uge.structsure.utils.StateEnum;

import java.util.Date;
import java.util.Objects;

@JsonSerialize
public record AllSensorsByStructureRequestDTO(String orderByColumn, String orderType, String stateFilter, String planFilter,
                                              String minInstallationDate, String maxInstallationDate, Integer limit, Integer offset,
                                              Boolean archivedFilter) {

    public enum OrderByColumn {
        NAME("name"),
        STATE("state"),
        INSTALLATION_DATE("installationDate");

        private final String value;

        /**
         * The constructor for the OrderByColumn enum
         * @param value the value of the ordre as writen in the entity because this will be used in the jpa query
         */
        OrderByColumn(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }
    }

    /**
     * Checks that the required fields are present and also checks that the fields values are correct
     * @throws TraitementException MISSING_FIELDS if there is some missing fields and INCORRECT_FIELD_VALUE
     * if there is some incorrect fields
     */
    public void checkFields() throws TraitementException {
        if (Objects.isNull(orderByColumn) || Objects.isNull(orderType) || Objects.isNull(offset) || Objects.isNull(limit)) {
            throw new TraitementException(Error.MISSING_FIELDS);
        }
        if (!EnumValidatorFromString.validateEnumValue(OrderByColumn.class, orderByColumn) ||
                !EnumValidatorFromString.validateEnumValue(OrderEnum.class, orderType) ||
                (!Objects.isNull(stateFilter) && !EnumValidatorFromString.validateEnumValue(StateEnum.class, stateFilter))) {
            throw new TraitementException(Error.INCORRECT_FIELD_VALUE);
        }
    }
}
