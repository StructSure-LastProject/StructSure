package fr.uge.structsure.mapper;

import fr.uge.structsure.dto.StructureDTO;
import fr.uge.structsure.dto.structure.StructureResponseDTO;
import fr.uge.structsure.entities.Structure;
import org.springframework.stereotype.Component;

@Component
public class StructureMapper {
    public Structure toEntity(StructureDTO structureDTO) {
        Structure structure = new Structure();
        structure.setName(structureDTO.name());
        structure.setNote(structureDTO.note());
        structure.setArchived(structureDTO.isArchived());
        return structure;
    }

    public StructureResponseDTO toResponseDTO(Structure structure) {
        return new StructureResponseDTO(structure.getId(), structure.getName(), structure.getNote(), structure.getArchived());
    }
}
