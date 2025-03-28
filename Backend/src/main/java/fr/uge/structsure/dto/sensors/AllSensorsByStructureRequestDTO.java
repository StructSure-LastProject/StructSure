package fr.uge.structsure.dto.sensors;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import fr.uge.structsure.entities.State;
import fr.uge.structsure.exceptions.Error;
import fr.uge.structsure.exceptions.TraitementException;
import fr.uge.structsure.utils.EnumValidatorFromString;
import fr.uge.structsure.utils.OrderEnum;

import java.util.Objects;

/**
 * Dto for the request to get the all the sensors present in a structure
 * @param orderByColumn the column to order by
 * @param orderType the order type
 * @param stateFilter the state filter
 * @param planFilter the plan filter
 * @param minInstallationDate the minimum installation date
 * @param maxInstallationDate the maximum installation date
 * @param limit the limit
 * @param offset the offset
 * @param archivedFilter the archived filter true or false
 */
@JsonSerialize
public record AllSensorsByStructureRequestDTO(String orderByColumn, String orderType, String stateFilter, Integer planFilter,
                                              String minInstallationDate, String maxInstallationDate, Integer limit, Integer offset,
                                              Boolean archivedFilter, Integer scanFilter) {

    /**
     * Represents enum for the supported order
     */
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
        if (Objects.isNull(orderByColumn) || Objects.isNull(orderType)) {
            throw new TraitementException(Error.MISSING_FIELDS);
        }
        if (!EnumValidatorFromString.validateEnumValue(OrderByColumn.class, orderByColumn) ||
                !EnumValidatorFromString.validateEnumValue(OrderEnum.class, orderType) ||
                (!Objects.isNull(stateFilter) && !EnumValidatorFromString.validateEnumValue(State.class, stateFilter))
                || (planFilter != null && planFilter < 0) || (scanFilter != null && scanFilter < 0)) {
            throw new TraitementException(Error.INCORRECT_FIELD_VALUE);
        }
    }
}
