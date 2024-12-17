package fr.uge.structsure.services;

import fr.uge.structsure.dto.ErrorDTO;
import fr.uge.structsure.dto.structure.*;
import fr.uge.structsure.repositories.PlanRepository;
import fr.uge.structsure.repositories.SensorRepository;
import fr.uge.structsure.repositories.StructureRepository;
import fr.uge.structsure.utils.OrderEnum;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

@Service
public class StructureService {
    private final StructureRepository structureRepository;
    private final PlanRepository planRepository;
    private final SensorRepository sensorRepository;

    public StructureService(StructureRepository structureRepository, SensorRepository sensorRepository ,PlanRepository planRepository) {
        this.sensorRepository = sensorRepository;
        this.structureRepository = structureRepository;
        this.planRepository = planRepository;
    }

    public ApiResponseWrapper<?> editStructure(long id, StructureRequestDTO structureRequestDTO) {
        Objects.requireNonNull(structureRequestDTO);

        if (structureRequestDTO.name() == null || structureRequestDTO.name().isEmpty()) {
            return new ApiResponseWrapper<>(new ErrorDTO("Le nom d’un ouvrage ne peut pas être vide"), 422);
        }
        var structureEntityOptional = structureRepository.findById(id);
        if (structureEntityOptional.isEmpty()) {
            return new ApiResponseWrapper<>(new ErrorDTO("Ouvrage introuvable"), 422);
        }
        var structureEntity = structureEntityOptional.get();
        structureEntity.setName(structureRequestDTO.name());
        structureEntity.setNote(structureRequestDTO.note());
        var toto = structureRepository.save(structureEntity);
        return new ApiResponseWrapper<>(new StructureAnswerDTO(toto.getId(), new Timestamp(System.currentTimeMillis()).toString()), 200);
    }

    public List<AllStructureResponseDTO> getAllStructure(GetAllStructureRequest getAllStructureRequest){
        Objects.requireNonNull(getAllStructureRequest);
        var result = structureRepository
                .findAll()
                .stream()
                .map(structure -> {
                        var numberOfPlans = planRepository.countByStructureId(structure.getId());
                        var numberOfSensors = sensorRepository.findByStructureId(structure.getId()).size();
                        return new AllStructureResponseDTO(
                                structure.getId(),
                                structure.getName(),
                                numberOfSensors,
                                numberOfPlans,
                                String.format("/api/structure/%d", structure.getId())
                                );
                    }
                );

        var resultList = switch (getAllStructureRequest.sort()){
            case NUMBEROFSENSORS -> result.sorted(Comparator.comparing(AllStructureResponseDTO::numberOfSensors)).toList();
            case NAME -> result.sorted(Comparator.comparing(AllStructureResponseDTO::name)).toList();
            case WORSTSTATE -> result.sorted(Comparator.comparing(AllStructureResponseDTO::numberOfSensors)).toList();
        };

        if (getAllStructureRequest.order() == OrderEnum.DESC) {
            resultList = resultList.reversed();
        }
        return resultList;

    }

}