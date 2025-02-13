package fr.uge.structsure.services;

import fr.uge.structsure.dto.plan.AddPlanRequestDTO;
import fr.uge.structsure.dto.plan.AddPlanResponseDTO;
import fr.uge.structsure.entities.Plan;
import fr.uge.structsure.entities.Structure;
import fr.uge.structsure.exceptions.ErrorIdentifier;
import fr.uge.structsure.exceptions.TraitementException;
import fr.uge.structsure.repositories.PlanRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.logging.Logger;

@Service
public class PlanService {
    private final Logger log = Logger.getLogger(this.getClass().getName());
    private final PlanRepository planRepository;
    private final StructureService structureService;
    private final String workingDir = System.getProperty("user.dir");

    @Value("${file.upload-dir}")
    private String uploadDir;

    private static final List<String> ALLOWED_MIME_TYPES = Arrays.asList(
            MediaType.IMAGE_JPEG_VALUE,
            MediaType.IMAGE_PNG_VALUE
    );

    @Autowired
    public PlanService(PlanRepository planRepository, StructureService structureService) {
        this.planRepository = planRepository;
        this.structureService = structureService;
    }

    /**
     * Creates a new plan for a given structure with the provided details and file.
     *
     * @param structureId The ID of the structure to which the plan will be attached
     * @param addPlanRequestDTO The DTO containing plan details (name and section)
     * @param file The multipart file containing the plan image
     * @return AddPlanResponseDTO containing the created plan's ID and creation timestamp
     * @throws TraitementException if validation fails or if there are issues during plan creation
     */
    public AddPlanResponseDTO createPlan(Long structureId, AddPlanRequestDTO addPlanRequestDTO, MultipartFile file) throws TraitementException {
        planEmptyPrecondition(structureId, addPlanRequestDTO, file);
        planMalformedPrecondition(addPlanRequestDTO);
        planConsistencyPrecondition(file);
        var noSection = addPlanRequestDTO.section() == null || addPlanRequestDTO.section().isEmpty();
        var structure = structureService.existStructure(structureId);
        if (structure.isEmpty()) {
            throw new TraitementException(ErrorIdentifier.STRUCTURE_ID_NOT_FOUND);
        }
        var directory = workingDir + File.separator + uploadDir + File.separator + "Ouvrages" + File.separator + structureId;
        if (!noSection) {
            directory += File.separator + addPlanRequestDTO.section();
        }
        var filePath = directory + File.separator + file.getOriginalFilename();

        var planAlreadyExists = planRepository.planFileAlreadyExists(filePath);
        if (planAlreadyExists) {
            throw new TraitementException(ErrorIdentifier.PLAN_ALREADY_EXISTS);
        }
        managedFilesDirectory(directory);
        var savedPlan = addPlanServerTraitment(filePath, file, structure.get(), addPlanRequestDTO.section(), addPlanRequestDTO.name());
        return new AddPlanResponseDTO(savedPlan.getId(), new Timestamp(System.currentTimeMillis()).toString());
    }

    /**
     * Validates the consistency of the file before creation.
     * Checks file format.
     *
     * @param file              The multipart file to be validated
     * @throws TraitementException if the file format is invalid
     */
    private void planConsistencyPrecondition(MultipartFile file) throws TraitementException {
        var mimeType = file.getContentType();
        if (mimeType == null || !ALLOWED_MIME_TYPES.contains(mimeType)) {
            throw new TraitementException(ErrorIdentifier.PLAN_FILE_INVALID_FORMAT);
        }
    }

    /**
     * Handles the server-side processing of adding a new plan.
     * Saves the file to the server and creates a corresponding database entry.
     *
     * @param filePath The complete path where the file will be stored
     * @param file The multipart file to be saved
     * @param structure The structure to which the plan belongs
     * @param section The section name or "null" if no section is specified
     * @param name The name of the plan
     * @return Plan The saved plan entity
     * @throws TraitementException if there are issues saving the file or creating the database entry
     */
    private Plan addPlanServerTraitment(String filePath, MultipartFile file, Structure structure, String section, String name) throws TraitementException {
        var dest = new File(filePath);
        try {
            file.transferTo(dest);
        } catch (IOException e) {
            log.severe("IOException when uploading file to server : " + e.getMessage());
            throw new TraitementException(ErrorIdentifier.SERVER_ERROR);
        }

        var plan = new Plan(filePath, false, name, structure);
        Plan savedPlan;
        try {
            savedPlan = planRepository.save(plan);
        } catch (Exception e) {
            log.severe("Exception when uploading plan to bdd : " + e.getMessage());
            var delete = dest.delete();
            if (!delete) {
                log.severe("Exception when removing file to server, please check it");
            }
            throw new TraitementException(ErrorIdentifier.SERVER_ERROR);
        }
        return savedPlan;
    }

    /**
     * Validates that all required plan fields are present and not empty.
     *
     * @param structureId The ID of the structure to be validated
     * @param addPlanRequestDTO The DTO containing plan details to be validated
     * @param file The multipart file to be validated
     * @throws TraitementException if any required field is null or empty
     */
    private void planEmptyPrecondition(Long structureId, AddPlanRequestDTO addPlanRequestDTO, MultipartFile file) throws TraitementException {
        Objects.requireNonNull(addPlanRequestDTO);
        if (structureId == null) {
            throw new TraitementException(ErrorIdentifier.PLAN_STRUCTURE_ID_IS_EMPTY);
        }
        if (addPlanRequestDTO.name() == null || addPlanRequestDTO.name().isEmpty()) {
            throw new TraitementException(ErrorIdentifier.PLAN_NAME_IS_EMPTY);
        }
        if (file == null || file.isEmpty()) {
            throw new TraitementException(ErrorIdentifier.PLAN_FILE_IS_EMPTY);
        }
    }

    /**
     * Validates the format and length of plan fields.
     * Ensures name length doesn't exceed 32 characters and section length doesn't exceed 128 characters.
     * Ensures section is valid (only alphanumeric and '/' character allowed).
     *
     * @param addPlanRequestDTO The DTO containing plan details to be validated
     * @throws TraitementException if field lengths exceed their limits
     */
    private void planMalformedPrecondition(AddPlanRequestDTO addPlanRequestDTO) throws TraitementException {
        Objects.requireNonNull(addPlanRequestDTO);
        if(addPlanRequestDTO.name().isEmpty() || addPlanRequestDTO.name().length() > 32) {
            throw new TraitementException(ErrorIdentifier.PLAN_NAME_EXCEED_LIMIT);
        }
        if(addPlanRequestDTO.section() != null && addPlanRequestDTO.section().length() > 128) {
            throw new TraitementException(ErrorIdentifier.PLAN_SECTION_EXCEED_LIMIT);
        }
        if(addPlanRequestDTO.section() !=null && !addPlanRequestDTO.section().matches("^(?:[a-zA-Z0-9]+(?:/[a-zA-Z0-9]+)*)?$")) {
            throw new TraitementException(ErrorIdentifier.PLAN_SECTION_INVALID);
        }
    }

    /**
     * Manages the creation of necessary directories for storing plan files.
     * Creates the directory if it doesn't exist.
     *
     * @param path The path of the directory to be created/managed
     * @throws TraitementException if the directory cannot be created
     */
    private void managedFilesDirectory(String path) throws TraitementException {
        var directory = new File(path);
        var dirCreated = directory.exists();
        if (!dirCreated) {
            log.info(path + " directory not found, creating one ...");
            dirCreated = directory.mkdirs();
        }
        if (!dirCreated) {
            log.warning(path + " directory not created");
            throw new TraitementException(ErrorIdentifier.SERVER_ERROR);
        }
    }
}
