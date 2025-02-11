package fr.uge.structsure.dto.structure;



import fr.uge.structsure.utils.OrderEnum;
import fr.uge.structsure.utils.SortStructuresByEnum;

import java.util.Objects;

/**
 * This record represents the request format of get all structure
 * @param searchByName Search by name filter
 * @param sort Sort options
 * @param order Order options
 */
public record GetAllStructureRequest(String searchByName, SortStructuresByEnum sort, OrderEnum order) {
    public  GetAllStructureRequest {
        Objects.requireNonNull(searchByName);
        Objects.requireNonNull(sort);
        Objects.requireNonNull(order);
    }
}
