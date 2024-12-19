package fr.uge.structsure.services;

import fr.uge.structsure.dto.StructureDTO;
import fr.uge.structsure.dto.structure.*;
import fr.uge.structsure.entities.Structure;
import fr.uge.structsure.mapper.StructureMapper;
import fr.uge.structsure.repositories.PlanRepository;
import fr.uge.structsure.repositories.SensorRepository;
import fr.uge.structsure.repositories.StructureRepository;
import fr.uge.structsure.utils.OrderEnum;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;

@Service
public class StructureService {
    private final StructureRepository structureRepository;
    private final PlanRepository planRepository;
    private final SensorRepository sensorRepository;
    private final StructureMapper structureMapper;

    public StructureService(StructureRepository structureRepository, PlanRepository planRepository, SensorRepository sensorRepository, StructureMapper structureMapper) {
        this.structureRepository = structureRepository;
        this.planRepository = planRepository;
        this.sensorRepository = sensorRepository;
        this.structureMapper = structureMapper;
    }

    public StructureResponseDTO createStructure(StructureDTO structureDTO) {
        Structure structure = new Structure(structureDTO.name(), structureDTO.note());
        return structureMapper.toResponseDTO(structureRepository.save(structure));
    }

    public StructureResponseDTO editStructure(long id, StructureDTO structureDTO) {
        var structureEntityOptional = structureRepository.findById(id);
        if (structureEntityOptional.isEmpty()) {
            return new StructureResponseDTO(null, "", "", false);
        }
        var structureEntity = structureEntityOptional.get();
        structureEntity.setName(structureDTO.name());
        structureEntity.setNote(structureDTO.note());
        var savedStructure = structureRepository.save(structureEntity);
        return structureMapper.toResponseDTO(savedStructure);
    }

    public StructureResponseDTO getStructureById(long id) {
        var structureEntityOptional = structureRepository.findById(id);
        if (structureEntityOptional.isEmpty()) {
            return new StructureResponseDTO(null, "", "", false);
        }
        var structureEntity = structureEntityOptional.get();
        return structureMapper.toResponseDTO(structureEntity);
    }

    public StructureResponseDTO getStructureByName(String name) {
        var structureEntityOptional = structureRepository.findByName(name);
        if (structureEntityOptional.isEmpty()) {
            return new StructureResponseDTO(null, "", "", false);
        }
        var structureEntity = structureEntityOptional.get();
        return structureMapper.toResponseDTO(structureEntity);
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