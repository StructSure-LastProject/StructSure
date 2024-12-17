package fr.uge.structsure.services;

import fr.uge.structsure.dto.StructureDTO;
import fr.uge.structsure.dto.structure.AddStructureAnswerDTO;
import fr.uge.structsure.dto.structure.AddStructureRequestDTO;
import fr.uge.structsure.entities.Structure;
import fr.uge.structsure.exceptions.TraitementException;
import fr.uge.structsure.repositories.StructureRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.Objects;

@Service
public class StructureService {
    private final StructureRepository structureRepository;

    @Autowired
    public StructureService(StructureRepository structureRepository) {
        this.structureRepository = structureRepository;
    }

    public AddStructureAnswerDTO createStructure(AddStructureRequestDTO addStructureRequestDTO) throws TraitementException {
        Objects.requireNonNull(addStructureRequestDTO);
        if (addStructureRequestDTO.name() == null || addStructureRequestDTO.name().isEmpty()) {
            throw new TraitementException(5);
        }
        var exists = structureRepository.findByName(addStructureRequestDTO.name());
        if (exists.isPresent()) {
            throw new TraitementException(4);
        }
        var structure = new Structure(addStructureRequestDTO.name(), addStructureRequestDTO.note(), false);
        var result = structureRepository.save(structure);
        return new AddStructureAnswerDTO(result.getId(), new Timestamp(System.currentTimeMillis()).toString());
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