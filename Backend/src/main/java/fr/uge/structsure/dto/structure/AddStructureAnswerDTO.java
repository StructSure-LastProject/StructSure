package fr.uge.structsure.dto.structure;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@JsonSerialize
public record AddStructureAnswerDTO(int id, String createdAt) {
}
