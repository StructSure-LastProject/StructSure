package fr.uge.structsure.services;

import fr.uge.structsure.dto.StructureDTO;
import fr.uge.structsure.entities.Structure;
import fr.uge.structsure.repositories.StructureRepository;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
public class StructureService {
    private final StructureRepository structureRepository;

    private final SensorService sensorService;

    public StructureService(StructureRepository structureRepository, SensorService sensorService) {
        this.sensorService = sensorService;
        this.structureRepository = structureRepository;
    }

    public Structure createStructure(StructureDTO structureDTO, boolean isArchived) {
        Objects.requireNonNull(structureDTO);

        Structure structure = new Structure(structureDTO.name(), structureDTO.note(), isArchived);
        return structureRepository.save(structure);
    }

    public Structure editStructure(long id, StructureDTO structureDTO) {
        Objects.requireNonNull(structureDTO);

        var structureEntityOptional = structureRepository.findById(id);
        if (structureEntityOptional.isEmpty()) {
            return new Structure("", "", false);
        }
        var structureEntity = structureEntityOptional.get();
        structureEntity.setName(structureDTO.name());
        structureEntity.setNote(structureDTO.note());
        structureEntity.setArchived(structureDTO.isArchived());
        return structureRepository.save(structureEntity);
    }
}