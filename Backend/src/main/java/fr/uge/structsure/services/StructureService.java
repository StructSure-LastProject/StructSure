package fr.uge.structsure.services;

import fr.uge.structsure.dto.structure.AddStructureAnswerDTO;
import fr.uge.structsure.dto.structure.AddStructureRequestDTO;
import fr.uge.structsure.dto.structure.AllStructureResponseDTO;
import fr.uge.structsure.dto.structure.GetAllStructureRequest;
import fr.uge.structsure.entities.Structure;
import fr.uge.structsure.exceptions.ErrorIdentifier;
import fr.uge.structsure.exceptions.TraitementException;
import fr.uge.structsure.repositories.PlanRepository;
import fr.uge.structsure.repositories.SensorRepository;
import fr.uge.structsure.repositories.StructureRepository;
import fr.uge.structsure.utils.OrderEnum;
import org.springframework.beans.factory.annotation.Autowired;
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

    @Autowired
    public StructureService(StructureRepository structureRepository, SensorRepository sensorRepository ,PlanRepository planRepository) {
        this.sensorRepository = Objects.requireNonNull(sensorRepository);
        this.structureRepository = Objects.requireNonNull(structureRepository);
        this.planRepository = Objects.requireNonNull(planRepository);
    }

    /**
     * Creates a new structure in the system.
     * This method validates the input data, checks for the existence of a structure
     * with the same name, and if valid, saves a new structure in the repository.
     * It returns details of the newly created structure.
     *
     * @param addStructureRequestDTO the DTO containing the details required to create the structure,
     *                               including its name and note. The name must not be null or empty.
     *
     * @return an {@link AddStructureAnswerDTO} containing the ID of the newly created structure
     *         and the timestamp of creation.
     *
     * @throws NullPointerException if {@code addStructureRequestDTO} is null.
     * @throws TraitementException  if:
     *         <ul>
     *           <li>The name of the structure is null or empty ({@code ErrorIdentifier.ARCHITECTURE_NAME_IS_EMPTY}).</li>
     *           <li>A structure with the same name already exists
     *               ({@code ErrorIdentifier.ARCHITECTURE_NAME_ALREADY_EXISTS}).</li>
     *         </ul>
     */
    public AddStructureAnswerDTO createStructure(AddStructureRequestDTO addStructureRequestDTO) throws TraitementException {
        Objects.requireNonNull(addStructureRequestDTO);
        if (addStructureRequestDTO.name() == null || addStructureRequestDTO.name().isEmpty()) {
            throw new TraitementException(ErrorIdentifier.STRUCTURE_NAME_IS_EMPTY);
        }
        var exists = structureRepository.findByName(addStructureRequestDTO.name());
        if (exists.isPresent()) {
            throw new TraitementException(ErrorIdentifier.STRUCTURE_NAME_ALREADY_EXISTS);
        }
        var structure = new Structure(addStructureRequestDTO.name(), addStructureRequestDTO.note(), false);
        var result = structureRepository.save(structure);
        return new AddStructureAnswerDTO(result.getId(), new Timestamp(System.currentTimeMillis()).toString());
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

        // TODO Filters
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