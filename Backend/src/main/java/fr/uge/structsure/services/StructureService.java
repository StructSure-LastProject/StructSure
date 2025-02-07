package fr.uge.structsure.services;

import fr.uge.structsure.dto.structure.*;
import fr.uge.structsure.entities.Structure;
import fr.uge.structsure.exceptions.ErrorIdentifier;
import fr.uge.structsure.exceptions.TraitementException;
import fr.uge.structsure.repositories.PlanRepository;
import fr.uge.structsure.repositories.SensorRepository;
import fr.uge.structsure.repositories.StructureRepository;
import fr.uge.structsure.utils.OrderEnum;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.*;

@Service
public class StructureService {
    private final StructureRepository structureRepository;
    private final PlanRepository planRepository;
    private final SensorRepository sensorRepository;
    private final ModelMapper modelMapper;

    @Autowired
    public StructureService(StructureRepository structureRepository, SensorRepository sensorRepository, PlanRepository planRepository,
                            ModelMapper modelMapper) {
        this.sensorRepository = Objects.requireNonNull(sensorRepository);
        this.structureRepository = Objects.requireNonNull(structureRepository);
        this.planRepository = Objects.requireNonNull(planRepository);
        this.modelMapper = Objects.requireNonNull(modelMapper);
    }

    /**
     * Creates a new structure in the system.
     * This method validates the input data, checks for the existence of a structure
     * with the same name, and if valid, saves a new structure in the repository.
     * It returns details of the newly created structure.
     *
     * @param addStructureRequestDTO the DTO containing the details required to create the structure,
     *                               including its name and note. The name must not be null, empty,
     *                               exceed 64 characters, or conflict with an existing structure's name.
     *
     * @return an {@link AddStructureAnswerDTO} containing the ID of the newly created structure
     *         and the timestamp of creation.
     *
     * @throws NullPointerException if {@code addStructureRequestDTO} is null.
     * @throws TraitementException  if:
     *         <ul>
     *           <li>The name of the structure is null or empty
     *               ({@code ErrorIdentifier.STRUCTURE_NAME_IS_EMPTY}).</li>
     *           <li>The name of the structure exceeds 64 characters
     *               ({@code ErrorIdentifier.STRUCTURE_NAME_EXCEED_LIMIT}).</li>
     *           <li>The note of the structure exceeds 1000 characters
     *               ({@code ErrorIdentifier.STRUCTURE_NOTE_EXCEED_LIMIT}).</li>
     *           <li>A structure with the same name already exists
     *               ({@code ErrorIdentifier.STRUCTURE_NAME_ALREADY_EXISTS}).</li>
     *         </ul>
     */
    public AddStructureAnswerDTO createStructure(AddStructureRequestDTO addStructureRequestDTO) throws TraitementException {
        structurePrecondition(addStructureRequestDTO);
        var structure = new Structure(addStructureRequestDTO.name(), addStructureRequestDTO.note(), false);
        var result = structureRepository.save(structure);
        return new AddStructureAnswerDTO(result.getId(), new Timestamp(System.currentTimeMillis()).toString());
    }

    /**
     * Edits an existing structure in the system.
     * This method validates the input data, ensures that the structure with the specified ID exists,
     * and updates its name and note. It then returns the details of the updated structure.
     *
     * @param id                     the ID of the structure to be edited. Must not be null.
     * @param editStructureRequestDTO the DTO containing the updated name and note of the structure.
     *                                The name must not be null, empty, or exceed 64 characters.
     *                                The note must not exceed 1000 characters.
     *
     * @return an {@link EditStructureResponseDTO} containing the ID of the updated structure
     *         and the timestamp of the modification.
     *
     * @throws NullPointerException if {@code editStructureRequestDTO} or {@code id} is null.
     * @throws TraitementException  if:
     *         <ul>
     *           <li>The name of the structure is null or empty
     *               ({@code ErrorIdentifier.STRUCTURE_NAME_IS_EMPTY}).</li>
     *           <li>The name of the structure exceeds 64 characters
     *               ({@code ErrorIdentifier.STRUCTURE_NAME_EXCEED_LIMIT}).</li>
     *           <li>The note of the structure exceeds 1000 characters
     *               ({@code ErrorIdentifier.STRUCTURE_NOTE_EXCEED_LIMIT}).</li>
     *           <li>A structure with the specified ID does not exist
     *               ({@code ErrorIdentifier.STRUCTURE_ID_NOT_FOUND}).</li>
     *         </ul>
     */
    public EditStructureResponseDTO editStructure(Long id, AddStructureRequestDTO editStructureRequestDTO) throws TraitementException {
        structurePrecondition(editStructureRequestDTO);
        Objects.requireNonNull(id);
        var exists = structureRepository.findById(id);
        if (exists.isEmpty()) {
            throw new TraitementException(ErrorIdentifier.STRUCTURE_ID_NOT_FOUND);
        }
        exists.get().setNote(editStructureRequestDTO.note());
        exists.get().setName(editStructureRequestDTO.name());
        var result = structureRepository.save(exists.get());
        return new EditStructureResponseDTO(result.getId(), new Timestamp(System.currentTimeMillis()).toString());
    }

    public Optional<Structure> existStructure(Long id) {
        return structureRepository.findById(id);
    }

    /**
     * Validates the preconditions for adding or editing a structure.
     * This method checks that the input DTO is not null, the name is not null or empty,
     * and the name and note adhere to length constraints. It also ensures that the name
     * does not conflict with an existing structure.
     *
     * @param addStructureRequestDTO the DTO containing the details of the structure.
     *
     * @throws NullPointerException if {@code addStructureRequestDTO} is null.
     * @throws TraitementException  if:
     *         <ul>
     *           <li>The name of the structure is null or empty
     *               ({@code ErrorIdentifier.STRUCTURE_NAME_IS_EMPTY}).</li>
     *           <li>The name of the structure exceeds 64 characters
     *               ({@code ErrorIdentifier.STRUCTURE_NAME_EXCEED_LIMIT}).</li>
     *           <li>The note of the structure exceeds 1000 characters
     *               ({@code ErrorIdentifier.STRUCTURE_NOTE_EXCEED_LIMIT}).</li>
     *           <li>A structure with the same name already exists
     *               ({@code ErrorIdentifier.STRUCTURE_NAME_ALREADY_EXISTS}).</li>
     *         </ul>
     */
    private void structurePrecondition(AddStructureRequestDTO addStructureRequestDTO) throws TraitementException {
        Objects.requireNonNull(addStructureRequestDTO);
        if (addStructureRequestDTO.name() == null || addStructureRequestDTO.name().isEmpty()) {
            throw new TraitementException(ErrorIdentifier.STRUCTURE_NAME_IS_EMPTY);
        }
        if (addStructureRequestDTO.name().length() > 1000) {
            throw new TraitementException(ErrorIdentifier.STRUCTURE_NOTE_EXCEED_LIMIT);
        }
        if (addStructureRequestDTO.name().length() > 64) {
            throw new TraitementException(ErrorIdentifier.STRUCTURE_NAME_EXCEED_LIMIT);
        }
        var exists = structureRepository.findByName(addStructureRequestDTO.name());
        if (exists.isPresent()) {
            throw new TraitementException(ErrorIdentifier.STRUCTURE_NAME_ALREADY_EXISTS);
        }
    }

    public List<AllStructureResponseDTO> getAllStructure(){
        return structureRepository.findAll().stream()
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
            ).toList();
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

    public StructureResponseDTO getStructureById(Long id) {
        Objects.requireNonNull(id);
        var structureOptional = structureRepository.findById(id);
        if (structureOptional.isEmpty()){
        throw new IllegalArgumentException("Structure n'existe pas");
        }
        var structure = structureOptional.get();
        var plans = planRepository.findByStructure(structure);
        var sensors = sensorRepository.findByStructureId(structure.getId());
        return new StructureResponseDTO(
          id,
          structure.getName(),
          structure.getNote(),
          plans,
          sensors
        );
    }

    /**
     * Will return a detail of the structure with the specified id
     * @param id the id of the structure
     * @return the record containing the detail
     * @throws TraitementException
     */
    public StructureDetailsResponseDTO structureDetail(long id) throws TraitementException {
        var structureOpt = structureRepository.findById(id);
        if (structureOpt.isEmpty()) {
            throw new TraitementException(ErrorIdentifier.STRUCTURE_ID_NOT_FOUND);
        }
        var structure = structureOpt.get();
        var plans = planRepository.findByStructure(structure);
        var sensors = sensorRepository.findByStructure(structure);
        System.err.println("Plans : " + plans);
        System.err.println("Sensors : " + sensors);
        var plansDto = new ArrayList<StructureDetailsResponseDTO.PLan>();
        var sensorsDto = new ArrayList<StructureDetailsResponseDTO.Sensor>();
        modelMapper.map(plans, plansDto);
        modelMapper.map(sensors, sensorsDto);
        return new StructureDetailsResponseDTO(structure.getId(), structure.getName(),
                structure.getNote(), plansDto, sensorsDto);
    }
}
