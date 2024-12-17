package fr.uge.structsure.dto;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@JsonSerialize
public record StructureDTO(String name, String note, boolean isArchived) {
}
