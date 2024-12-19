package fr.uge.structsure.services;

import fr.uge.structsure.dto.structure.AllStructureResponseDTO;
import fr.uge.structsure.dto.StructureDTO;
import fr.uge.structsure.dto.structure.GetAllStructureRequest;
import fr.uge.structsure.entities.Structure;
import fr.uge.structsure.repositories.PlanRepository;
import fr.uge.structsure.repositories.SensorRepository;
import fr.uge.structsure.repositories.StructureRepository;
import fr.uge.structsure.utils.OrderEnum;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

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

    public List<AllStructureResponseDTO> getAllStructure(GetAllStructureRequest getAllStructureRequest){
        Objects.requireNonNull(getAllStructureRequest);
        var result = structureRepository
                .findAll()
                .stream()
                .map(structure -> {
                        var numberOfPlans = planRepository.countByStructureId(structure.getId());
                        var numberOfSensors = sensorRepository.findByStructureId(structure.getId()).size(); // NUMBER OF SENSORS TODO
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

        if (getAllStructureRequest.order().equals(OrderEnum.DESC)){
            return resultList.reversed();
        }
        return resultList;
    }

    public List<Structure> getAllActiveStructures() {
        return structureRepository.findByArchivedFalse();
    }

    public Optional<Structure> getStructureById(long id) {
        return structureRepository.findById(id);
    }

}