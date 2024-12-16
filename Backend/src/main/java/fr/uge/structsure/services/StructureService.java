package fr.uge.structsure.services;

import fr.uge.structsure.dto.ApiResponseWrapper;
import fr.uge.structsure.dto.ErrorDTO;
import fr.uge.structsure.dto.StructureAnswerDTO;
import fr.uge.structsure.dto.StructureRequestDTO;
import fr.uge.structsure.entities.Structure;
import fr.uge.structsure.repositories.StructureRepository;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Optional;

@Service
public class StructureService {
    private final StructureRepository structureRepository;

    public StructureService(StructureRepository structureRepository) {
        this.structureRepository = structureRepository;
    }

    public ApiResponseWrapper<?> createStructure(StructureRequestDTO structureRequestDTO) {
        Objects.requireNonNull(structureRequestDTO);
        if (structureRequestDTO.name() == null || structureRequestDTO.name().isEmpty()) {
            return new ApiResponseWrapper<>(new ErrorDTO("Le nom d’un ouvrage ne peut pas être vide"), 422);
        }
        var exists = structureRepository.findByName(structureRequestDTO.name());
        if (exists.isPresent()) {
            return new ApiResponseWrapper<>(new ErrorDTO("Nom existe déjà"), 422);
        }
        var structure = new Structure(structureRequestDTO.name(), structureRequestDTO.note(), false);
        var toto = structureRepository.save(structure);
        return new ApiResponseWrapper<>(new StructureAnswerDTO(toto.getId(), new Timestamp(System.currentTimeMillis()).toString()), 422);
    }

    // changer la request
    public Structure editStructure(long id, StructureRequestDTO structureRequestDTO) {
        Objects.requireNonNull(structureRequestDTO);

        var structureEntityOptional = structureRepository.findById(id);
        if (structureEntityOptional.isEmpty()) {
            return new Structure("", "", false);
        }
        var structureEntity = structureEntityOptional.get();
        structureEntity.setName(structureRequestDTO.name());
        structureEntity.setNote(structureRequestDTO.note());
        /*structureEntity.setArchived(structureRequestDTO.isArchived());*/
        return structureRepository.save(structureEntity);
    }
}