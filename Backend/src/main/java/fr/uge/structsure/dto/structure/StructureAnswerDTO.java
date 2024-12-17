package fr.uge.structsure.dto.structure;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@JsonSerialize
public record StructureAnswerDTO(long id, String createdAt) {
}