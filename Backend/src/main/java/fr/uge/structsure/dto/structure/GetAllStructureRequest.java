package fr.uge.structsure.dto.structure;



import fr.uge.structsure.utils.OrderEnum;
import fr.uge.structsure.utils.SortEnum;

public record GetAllStructureRequest(String searchByName, SortEnum sort, OrderEnum order) {
}
