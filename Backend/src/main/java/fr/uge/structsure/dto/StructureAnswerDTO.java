package fr.uge.structsure.dto;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@JsonSerialize
public record StructureAnswerDTO(int id, String createdAt) {
}
