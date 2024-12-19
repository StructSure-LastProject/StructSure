package fr.uge.structsure.dto.structure;

public record EditStructureResponseDTO(long id, String updatedAt) {
    public EditStructureResponseDTO {
        if (id <= 0) {
            throw new IllegalArgumentException("id must be greater than 0");
        }
    }
}
