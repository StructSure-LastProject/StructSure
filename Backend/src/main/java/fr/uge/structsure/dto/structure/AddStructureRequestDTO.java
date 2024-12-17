package fr.uge.structsure.dto.structure;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@JsonSerialize
public record AddStructureRequestDTO(String name, String note) {
}