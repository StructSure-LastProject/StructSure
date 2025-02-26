package fr.uge.structsure.services;

import fr.uge.structsure.dto.plan.AddPlanResponseDTO;
import fr.uge.structsure.dto.plan.EditPlanResponseDTO;
import fr.uge.structsure.dto.plan.PlanImageResponseDTO;
import fr.uge.structsure.dto.plan.PlanMetadataDTO;
import fr.uge.structsure.entities.Plan;
import fr.uge.structsure.entities.SensorId;
import fr.uge.structsure.exceptions.TraitementException;
import fr.uge.structsure.entities.Structure;
import fr.uge.structsure.exceptions.Error;
import fr.uge.structsure.repositories.PlanRepository;
import fr.uge.structsure.repositories.SensorRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.MediaType;
import org.springframework.http.MediaTypeFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.sql.Timestamp;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * Plan service class
 */
@Service
public class PlanService {
    private static final Logger LOGGER = LoggerFactory.getLogger(PlanService.class);
    private final PlanRepository planRepository;
    private final SensorRepository sensorRepository;
    private final StructureService structureService;
    private final SensorService sensorService;

    @Value("${file.upload-dir}")
    private String uploadDir;

    private static final List<MediaType> ALLOWED_MIME_TYPES = Arrays.asList(
        MediaType.IMAGE_JPEG,
        MediaType.IMAGE_PNG
    );

    /**
     * Constructor
     * @param planRepository The plan repo
     * @param sensorRepository The sensor repo
     * @param structureService The structure service
     * @param sensorService The sensor service
     */
    @Autowired
    public PlanService(PlanRepository planRepository, SensorRepository sensorRepository,
           StructureService structureService, SensorService sensorService) {
        this.planRepository = planRepository;
        this.sensorRepository = sensorRepository;
        this.structureService = structureService;
        this.sensorService = sensorService;
    }

    /**
     * Creates a new plan for a given structure with the provided details and file.
     *
     * @param structureId The ID of the structure to which the plan will be attached
     * @param metadata The DTO containing plan details (name and section)
     * @param file The multipart file containing the plan image
     * @return AddPlanResponseDTO containing the created plan's ID and creation timestamp
     * @throws TraitementException if validation fails or if there are issues during plan creation
     */
    public AddPlanResponseDTO createPlan(Long structureId, PlanMetadataDTO metadata, MultipartFile file) throws TraitementException {
        Objects.requireNonNull(metadata);
        addPlanAsserts(structureId, metadata, file);
        var noSection = metadata.section() == null || metadata.section().isEmpty();
        var structure = structureService.existStructure(structureId).orElseThrow(() -> new TraitementException(Error.PLAN_STRUCTURE_NOT_FOUND));
        checkState(structure);
        var directory = computeDirectory(noSection, structureId, metadata.section());
        var filePath = directory + File.separator + file.getOriginalFilename();
        if (planRepository.planFileAlreadyExists(filePath)) {
            throw new TraitementException(Error.PLAN_ALREADY_EXISTS);
        }
        managedFilesDirectory(directory);
        var savedPlan = handleAddPlan(directory, file, new Plan(filePath, metadata.name(), metadata.section(), structure));
        return new AddPlanResponseDTO(savedPlan.getId(), new Timestamp(System.currentTimeMillis()).toString());
    }

    /**
     * Performs all the checks on the arguments of the function (add plan)
     *
     * @param structureId The ID of the structure to which the plan will be attached
     * @param metadata The DTO containing plan details (name and section)
     * @param file The multipart file containing the plan image
     * @throws TraitementException if validation fails
     */
    private void addPlanAsserts(Long structureId, PlanMetadataDTO metadata, MultipartFile file) throws TraitementException {
        planEmptyPrecondition(structureId, metadata, file);
        planMalformedPrecondition(metadata);
        planConsistencyPrecondition(file);
    }

    /**
     * Edits an existing plan with new metadata and optionally a new file.
     *
     * @param structureId The ID of the structure containing the plan
     * @param planId The ID of the plan to edit
     * @param metadata The new metadata for the plan
     * @param multipartFile The new file for the plan (optional)
     * @return EditPlanResponseDTO containing the edited plan's ID
     * @throws TraitementException if validation fails or if there are issues during plan editing
     */
    public EditPlanResponseDTO editPlan(Long structureId, Long planId, PlanMetadataDTO metadata, Optional<MultipartFile> multipartFile) throws TraitementException {
        Objects.requireNonNull(metadata);
        editPlanAsserts(structureId, planId, metadata, multipartFile);

        var noSection = metadata.section() == null || metadata.section().isEmpty();
        var plan = planRepository.findById(planId).orElseThrow(() -> new TraitementException(Error.PLAN_NOT_FOUND));
        var structure = plan.getStructure();
        checkState(plan, structure, structureId);
        var planFile = Path.of(plan.getImageUrl());

        var name = plan.getName().equals(metadata.name()) ? plan.getName() : metadata.name();
        var section = plan.getSection().equals(metadata.section()) ? plan.getSection() : metadata.section();
        var directory = computeDirectory(noSection, structure.getId(), metadata.section());
        var fileName = multipartFile.map(MultipartFile::getOriginalFilename).orElse(planFile.getFileName().toString());
        var filePath = Path.of(directory.toString(), fileName);

        plan.setName(name);
        plan.setSection(section);
        plan.setImageUrl(filePath.toString());

        if (!noSection) {
            managedFilesDirectory(directory);
        }
        Optional<MultipartFile> file = planFile.equals(filePath) ? Optional.empty() : multipartFile;

        var savedPlan = handleEditPlan(planFile, filePath, file, plan);
        return new EditPlanResponseDTO(savedPlan.getId());
    }

    /**
     * Checks if a structure is in an archived state.
     *
     * @param structure The structure to check
     * @throws TraitementException if the structure is archived
     */
    private void checkState(Structure structure) throws TraitementException {
        if (structure.getArchived()) {
            throw new TraitementException(Error.STRUCTURE_IS_ARCHIVED);
        }
    }

    /**
     * Checks if a structure and plan are in an archived state.
     *
     * @param plan The plan to check
     * @param structure The structure to check
     * @throws TraitementException if either the plan or structure is archived
     */
    private void checkState(Plan plan, Structure structure) throws TraitementException {
        checkState(structure);
        if (plan.isArchived()) {
            throw new TraitementException(Error.PLAN_IS_ARCHIVED);
        }
    }

    /**
     * Checks the state of a plan, its structure, and verifies structure ID matching.
     *
     * @param plan The plan to check
     * @param structure The structure to check
     * @param structureId The expected structure ID
     * @throws TraitementException if any check fails (archived state or ID mismatch)
     */
    private void checkState(Plan plan, Structure structure, long structureId) throws TraitementException {
        checkState(plan, structure);
        if (structure.getId() != structureId) {
            throw new TraitementException(Error.PLAN_STRUCTURE_MISMATCH);
        }
    }

    /**
     * Performs all the checks on the arguments of the function (edit plan)
     *
     * @param structureId The ID of the structure containing the plan
     * @param planId The ID of the plan to edit
     * @param metadata The new metadata for the plan
     * @param multipartFile The new file for the plan (optional)
     * @throws TraitementException if validation fails
     */
    private void editPlanAsserts(Long structureId, Long planId, PlanMetadataDTO metadata, Optional<MultipartFile> multipartFile) throws TraitementException {
        planEmptyPrecondition(structureId, planId, metadata);
        planMalformedPrecondition(metadata);
        if (multipartFile.isPresent()) {
            planConsistencyPrecondition(multipartFile.get());
        }
    }

    /**
     * Calculates the directory path for storing plan files based on structure and section information.
     *
     * @param noSection true if there is no section specified, false otherwise
     * @param structureId the ID of the structure
     * @param section the section name, can be null
     * @return the normalized Path object representing the directory
     */
    private Path computeDirectory(boolean noSection, Long structureId, String section) {
        var directory = Path.of(uploadDir, "ouvrages", String.valueOf(structureId));
        if (!noSection) {
            directory = Path.of(directory.toString(), section);
        }
        return directory.normalize();
    }

    /**
     * Uploads a MultipartFile to a specified path on the server.
     *
     * @param file the MultipartFile to upload
     * @param filePath the destination path for the file
     * @throws TraitementException if an IO error occurs during file transfer
     */
    private void uploadFile(MultipartFile file, Path filePath) throws TraitementException {
        Objects.requireNonNull(file);
        try {
            file.transferTo(filePath);
        } catch (IOException e) {
            LOGGER.error("IOException when uploading file to server", e);
            throw new TraitementException(Error.SERVER_ERROR);
        }
    }

    /**
     * Moves a file from one location to another on the server.
     * If the source parent directory becomes empty after the move, it will be deleted.
     *
     * @param source the source path of the file
     * @param dest the destination path for the file
     * @throws TraitementException if an IO error occurs during file movement
     */
    private void moveFile(Path source, Path dest) throws TraitementException {
        var parent = source.getParent();
        if (source.equals(dest)) {
            return;
        }
        try {
            Files.move(source, dest, StandardCopyOption.REPLACE_EXISTING);
            if (parent != null) {
                deleteEmptyParentDirectory(parent);
            }
        } catch (IOException e) {
            LOGGER.error("IOException when moving file in the server (from '{}' to '{}')", source, dest, e);
            throw new TraitementException(Error.SERVER_ERROR);
        }
    }

    /**
     * Deletes a file from the server and its parent directory if it becomes empty.
     *
     * @param path the path of the file to delete
     * @throws TraitementException if an IO error occurs during file deletion
     */
    private void deleteFile(Path path) throws TraitementException {
        var parent = path.getParent();
        try {
            Files.deleteIfExists(path);
            if (parent != null) {
                deleteEmptyParentDirectory(parent);
            }
        } catch (IOException e) {
            LOGGER.error("IOException when deleting file in the server", e);
            throw new TraitementException(Error.SERVER_ERROR);
        }
    }

    /**
     * Deletes a directory and all its parent if they are empty. Used
     * for cleanup after file operations.
     *
     * @param path the path of the directory to check and potentially delete
     * @throws IOException if an error occurs while accessing or deleting the directory
     */
    private void deleteEmptyParentDirectory(Path path) throws IOException {
        if (path.getFileName().toString().equals(uploadDir)) return; // security to avoid removing root directory
        try (var files = Files.list(path)) {
            if (files.findAny().isEmpty()) {
                Files.delete(path);
                deleteEmptyParentDirectory(path.getParent());
            }
        }
    }

    /**
     * Handles the plan editing process, including file upload, movement, and database updates.
     * If any step fails, the operation is rolled back.
     *
     * @param sourceFilePath the original file path
     * @param targetFilePath the new file path
     * @param file optional new file to upload
     * @param plan the Plan entity to update
     * @return the saved Plan entity
     * @throws TraitementException if any step of the process fails
     */
    private Plan handleEditPlan(Path sourceFilePath, Path targetFilePath, Optional<MultipartFile> file, Plan plan) throws TraitementException {
        if (file.isPresent()) {
            if (planRepository.planFileAlreadyExists(targetFilePath.toString())) {
                throw new TraitementException(Error.PLAN_ALREADY_EXISTS);
            }
            uploadFile(file.get(), sourceFilePath);
        }
        moveFile(sourceFilePath, targetFilePath);
        Plan savedPlan = null;
        try {
            savedPlan = planRepository.save(plan);
        } catch (Exception e) {
            LOGGER.error("Exception when editing plan to db", e);
            managedFilesDirectory(sourceFilePath.getParent());
            moveFile(targetFilePath, sourceFilePath);
            if (file.isPresent()) {
                deleteFile(targetFilePath);
            }
        }
        return savedPlan;
    }

    /**
     * Handles the plan creation process, including file upload and database updates.
     * If any step fails, the operation is rolled back.
     *
     * @param targetDirPath the directory path where the file should be stored
     * @param file the MultipartFile to upload
     * @param plan the Plan entity to create
     * @return the saved Plan entity
     * @throws TraitementException if any step of the process fails
     */
    private Plan handleAddPlan(Path targetDirPath, MultipartFile file, Plan plan) throws TraitementException {
        var filePath = Path.of(targetDirPath.toString(), Objects.requireNonNull(file.getOriginalFilename()));
        uploadFile(file, filePath);
        Plan savedPlan;
        try {
            savedPlan = planRepository.save(plan);
        } catch (Exception e) {
            LOGGER.error("IOException when adding plan to db", e);
            deleteFile(filePath);
            throw new TraitementException(Error.SERVER_ERROR);
        }
        return savedPlan;
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
        if (mimeType == null || !ALLOWED_MIME_TYPES.contains(MediaType.valueOf(mimeType))) {
            throw new TraitementException(Error.PLAN_FILE_INVALID_FORMAT);
        }
    }

    /**
     * Validates that all required plan fields are present and not empty.
     *
     * @param structureId The ID of the structure to be validated
     * @param metadata The DTO containing plan details to be validated
     * @param file The multipart file to be validated
     * @throws TraitementException if any required field is null or empty
     */
    private void planEmptyPrecondition(Long structureId, PlanMetadataDTO metadata, MultipartFile file) throws TraitementException {
        Objects.requireNonNull(metadata);
        if (structureId == null) {
            throw new TraitementException(Error.PLAN_STRUCTURE_ID_IS_EMPTY);
        }
        if (metadata.name() == null || metadata.name().isEmpty()) {
            throw new TraitementException(Error.PLAN_NAME_IS_EMPTY);
        }
        if (file == null || file.isEmpty()) {
            throw new TraitementException(Error.PLAN_FILE_IS_EMPTY);
        }
    }

    /**
     * Validates that all required plan fields are present and not empty.
     *
     * @param structureId The ID of the structure to be validated
     * @param planId The ID of the plan to be validated
     * @param metadata The DTO containing plan details to be validated
     * @throws TraitementException if any required field is null or empty
     */
    private void planEmptyPrecondition(Long structureId, Long planId, PlanMetadataDTO metadata) throws TraitementException {
        Objects.requireNonNull(metadata);
        if (structureId == null) {
            throw new TraitementException(Error.PLAN_STRUCTURE_ID_IS_EMPTY);
        }
        if (planId == null) {
            throw new TraitementException(Error.PLAN_ID_IS_EMPTY);
        }
        if (metadata.name() == null || metadata.name().isEmpty()) {
            throw new TraitementException(Error.PLAN_NAME_IS_EMPTY);
        }
        if (metadata.section() == null) {
            throw new TraitementException(Error.PLAN_SECTION_IS_EMPTY);
        }
    }

    /**
     * Validates the format and length of plan fields.
     * Ensures name length doesn't exceed 32 characters and section length doesn't exceed 128 characters.
     * Ensures section is valid (only alphanumeric and '/' character allowed).
     *
     * @param metadata The DTO containing plan details to be validated
     * @throws TraitementException if field lengths exceed their limits
     */
    private void planMalformedPrecondition(PlanMetadataDTO metadata) throws TraitementException {
        Objects.requireNonNull(metadata);
        if(metadata.name().isEmpty() || metadata.name().length() > 32) {
            throw new TraitementException(Error.PLAN_NAME_EXCEED_LIMIT);
        }
        if(metadata.section() != null && metadata.section().length() > 128) {
            throw new TraitementException(Error.PLAN_SECTION_EXCEED_LIMIT);
        }
        if(metadata.section() != null && !metadata.section().matches("^(?:[a-zA-Z0-9_-]+(?:/[a-zA-Z0-9_-]+)*)?$")) {
            throw new TraitementException(Error.PLAN_SECTION_INVALID);
        }
    }

    /**
     * Manages the creation of necessary directories for storing plan files.
     * Creates the directory if it doesn't exist.
     *
     * @param path The path of the directory to be created/managed
     * @throws TraitementException if the directory cannot be created
     */
    private void managedFilesDirectory(Path path) throws TraitementException {
        try {
            Files.createDirectories(path);
        } catch (IOException e) {
            LOGGER.warn("IOException when looking and/or directories of the path : '{}'", path, e);
            throw new TraitementException(Error.SERVER_ERROR);
        }
    }

    /**
     * Downloads the image of a plan.
     * The image is retrieved from the server and returned as a DTO containing the image resource and metadata.
     *
     * @param planId The ID of the plan to download
     * @return PlanImageResponseDTO containing the image resource and metadata
     * @throws TraitementException if the plan is not found, structure is not found, or if there are issues retrieving the image
     */
    public PlanImageResponseDTO downloadPlanImage(Long planId) throws TraitementException {
        if (planId == null) {
            throw new TraitementException(Error.PLAN_ID_IS_EMPTY);
        }

        var plan = planRepository.findById(planId)
                .orElseThrow(() -> new TraitementException(Error.PLAN_NOT_FOUND));

        var imageUrl = plan.getImageUrl();
        var imagePath = Paths.get(imageUrl).normalize();

        try {
            if (!Files.exists(imagePath)) {
                LOGGER.warn("Plan image not found at path: {}", imagePath);
                throw new TraitementException(Error.PLAN_FILE_NOT_FOUND);
            }

            var mediaType = MediaTypeFactory.getMediaType(imagePath.getFileName().toString()).orElseThrow(() -> new TraitementException(Error.PLAN_FILE_INVALID_FORMAT));
            if (!ALLOWED_MIME_TYPES.contains(mediaType)){
                LOGGER.warn("Image file with wrong media type: {}", imagePath);
                throw new TraitementException(Error.PLAN_FILE_INVALID_FORMAT);
            }

            var resource = new InputStreamResource(Files.newInputStream(imagePath));

            return new PlanImageResponseDTO(
                    resource,
                    imagePath.getFileName().toString(),
                    mediaType
            );
        } catch (IOException e) {
            LOGGER.error("IOException when retrieving plan image", e);
            throw new TraitementException(Error.SERVER_ERROR);
        }
    }

    /**
     * Downloads the image of a plan.
     * The image is retrieved from the server and returned as a DTO containing the image resource and metadata.
     *
     * @param structureId The ID of the structure
     * @param controlChip The control chip of the sensor
     * @param measureChip The measure chip of the sensor
     * @return PlanImageResponseDTO containing the image resource and metadata
     * @throws TraitementException if the plan is not found, structure is not found, or if there are issues retrieving the image
     */
    public PlanImageResponseDTO downloadPlanImageAssociatedToTheSensor(long structureId, String controlChip, String measureChip) throws TraitementException {
        Objects.requireNonNull(controlChip);
        Objects.requireNonNull(measureChip);
        var plan = sensorService.getPlanFromSensor(controlChip, measureChip);
        if(sensorRepository.findByStructureId(structureId).stream()
                .noneMatch(sensor -> sensor.getSensorId().equals(new SensorId(controlChip, measureChip)))){
            throw new TraitementException(Error.SENSOR_NOT_FOUND);
        }
        if (plan == null){
            throw new TraitementException(Error.PLAN_NOT_FOUND);
        }
        return downloadPlanImage(plan.getId());
    }
}
