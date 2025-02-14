package fr.uge.structsure.services;

import ch.qos.logback.core.joran.action.PreconditionValidator;
import fr.uge.structsure.dto.plan.*;
import fr.uge.structsure.entities.Plan;
import fr.uge.structsure.entities.Structure;
import fr.uge.structsure.exceptions.ErrorIdentifier;
import fr.uge.structsure.exceptions.TraitementException;
import fr.uge.structsure.repositories.PlanRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.sql.Timestamp;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
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
     * @param metadata The DTO containing plan details (name and section)
     * @param file The multipart file containing the plan image
     * @return AddPlanResponseDTO containing the created plan's ID and creation timestamp
     * @throws TraitementException if validation fails or if there are issues during plan creation
     */
    public AddPlanResponseDTO createPlan(Long structureId, PlanMetadataDTO metadata, MultipartFile file) throws TraitementException {
        Objects.requireNonNull(metadata);
        planEmptyPrecondition(structureId, metadata, file);
        planMalformedPrecondition(metadata);
        planConsistencyPrecondition(file);
        var noSection = metadata.section() == null || metadata.section().isEmpty();
        var structure = structureService.existStructure(structureId).orElseThrow(() -> new TraitementException(ErrorIdentifier.PLAN_STRUCTURE_NOT_FOUND));
        var directory = computeDirectory(noSection, structureId, metadata.section());
        var filePath = directory + File.separator + file.getOriginalFilename();
        if (planRepository.planFileAlreadyExists(filePath)) {
            throw new TraitementException(ErrorIdentifier.PLAN_ALREADY_EXISTS);
        };
        managedFilesDirectory(directory);
        var savedPlan = handleAddPlan(directory, file, new Plan(filePath, metadata.name(), metadata.section(), structure));
        return new AddPlanResponseDTO(savedPlan.getId(), new Timestamp(System.currentTimeMillis()).toString());
    }

    private Path computeDirectory(boolean noSection, Long structureId, String section) {
        var directory = Path.of(uploadDir, "Ouvrages", String.valueOf(structureId));
        if (!noSection) {
            directory = Path.of(directory.toString(), section);
        }
        return directory.normalize();
    }

    private void uploadFile(MultipartFile file, Path filePath) throws TraitementException {
        Objects.requireNonNull(file);
        try {
            file.transferTo(filePath);
        } catch (IOException e) {
            log.severe("IOException when uploading file to server : " + e.getMessage());
            throw new TraitementException(ErrorIdentifier.SERVER_ERROR);
        }
    }

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
            log.severe("IOException when moving file in the server : " + e.getMessage());
            throw new TraitementException(ErrorIdentifier.SERVER_ERROR);
        }
    }

    private void deleteFile(Path path) throws TraitementException {
        var parent = path.getParent();
        try {
            Files.deleteIfExists(path);
            if (parent != null) {
                deleteEmptyParentDirectory(parent);
            }
        } catch (IOException e) {
            log.severe("IOException when deleting file in the server : " + e.getMessage());
            throw new TraitementException(ErrorIdentifier.SERVER_ERROR);
        }
    }

    private void deleteEmptyParentDirectory(Path path) throws IOException {
        try (var dirStream = Files.newDirectoryStream(path)) {
            if (!dirStream.iterator().hasNext()) {
                Files.delete(path);
            }
        } catch (DirectoryNotEmptyException ignored) {
        }
    }

    public AddPlanResponseDTO editPlan(Long structureId, Long planId, PlanMetadataDTO metadata, MultipartFile multipartFile) throws TraitementException {
        Objects.requireNonNull(metadata);
        planEmptyPrecondition(structureId, planId, metadata, multipartFile);
        planMalformedPrecondition(metadata);
        planConsistencyPrecondition(multipartFile);
        var noSection = metadata.section() == null || metadata.section().isEmpty();
        var plan = planRepository.findById(planId).orElseThrow(() -> new TraitementException(ErrorIdentifier.PLAN_NOT_FOUND));
        var planFile = Path.of(plan.getImageUrl());
        var structure = structureService.existStructure(structureId).orElseThrow(() -> new TraitementException(ErrorIdentifier.PLAN_STRUCTURE_NOT_FOUND));

        var directory = computeDirectory(noSection, structureId, metadata.section());
        var filePath = Path.of(directory.toString(), Objects.requireNonNull(multipartFile.getOriginalFilename()));
        var name = plan.getName().equals(metadata.name()) ? plan.getName() : metadata.name();
        var section = plan.getSection().equals(metadata.section()) ? plan.getSection() : metadata.section();

        plan.setName(name);
        plan.setSection(section);
        /*plan.setImageUrl();*/

        var file = multipartFile.getOriginalFilename().equals(planFile.getFileName().toString()) ? null : multipartFile;

        var savedPlan = handleEditPlan(Path.of(plan.getImageUrl()), filePath, Optional.empty(), new Plan(filePath.toString(), name, section, structure));
        return new AddPlanResponseDTO(savedPlan.getId(), new Timestamp(System.currentTimeMillis()).toString());
    }

    private Plan handleEditPlan(Path sourceFilePath, Path targetFilePath, Optional<MultipartFile> file, Plan plan) throws TraitementException {
        if (file.isPresent()) {
            uploadFile(file.get(), sourceFilePath.getParent());
        }
        moveFile(sourceFilePath, targetFilePath);
        Plan savedPlan = null;
        try {
            savedPlan = planRepository.save(plan);
        } catch (Exception e) {
            log.severe("Exception when editing plan to bdd : " + e.getMessage());
            managedFilesDirectory(sourceFilePath.getParent());
            moveFile(targetFilePath, sourceFilePath);
            if (file.isPresent()) {
                deleteFile(targetFilePath);
            }
        }
        return savedPlan;
    }

    private Plan handleAddPlan(Path targetDirPath, MultipartFile file, Plan plan) throws TraitementException {
        Objects.requireNonNull(file);
        Objects.requireNonNull(file.getOriginalFilename());
        var filePath = Path.of(targetDirPath.toString(), file.getOriginalFilename());
        uploadFile(file, filePath);
        Plan savedPlan;
        try {
            savedPlan = planRepository.save(plan);
        } catch (Exception e) {
            log.severe("IOException when adding plan to bdd : " + e.getMessage());
            deleteFile(filePath);
            throw new TraitementException(ErrorIdentifier.SERVER_ERROR);
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
        if (mimeType == null || !ALLOWED_MIME_TYPES.contains(mimeType)) {
            throw new TraitementException(ErrorIdentifier.PLAN_FILE_INVALID_FORMAT);
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
            throw new TraitementException(ErrorIdentifier.PLAN_STRUCTURE_ID_IS_EMPTY);
        }
        if (metadata.name() == null || metadata.name().isEmpty()) {
            throw new TraitementException(ErrorIdentifier.PLAN_NAME_IS_EMPTY);
        }
        if (file == null || file.isEmpty()) {
            throw new TraitementException(ErrorIdentifier.PLAN_FILE_IS_EMPTY);
        }
    }

    /**
     * Validates that all required plan fields are present and not empty.
     *
     * @param structureId The ID of the structure to be validated
     * @param planId The ID of the plan to be validated
     * @param metadata The DTO containing plan details to be validated
     * @param file The multipart file to be validated
     * @throws TraitementException if any required field is null or empty
     */
    private void planEmptyPrecondition(Long structureId, Long planId, PlanMetadataDTO metadata, MultipartFile file) throws TraitementException {
        Objects.requireNonNull(metadata);
        if (structureId == null) {
            throw new TraitementException(ErrorIdentifier.PLAN_STRUCTURE_ID_IS_EMPTY);
        }
        if (planId == null) {
            throw new TraitementException(ErrorIdentifier.PLAN_ID_IS_EMPTY);
        }
        if (metadata.name() == null || metadata.name().isEmpty()) {
            throw new TraitementException(ErrorIdentifier.PLAN_NAME_IS_EMPTY);
        }
        if (metadata.section() == null) {
            throw new TraitementException(ErrorIdentifier.PLAN_SECTION_IS_EMPTY);
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
     * @param metadata The DTO containing plan details to be validated
     * @throws TraitementException if field lengths exceed their limits
     */
    private void planMalformedPrecondition(PlanMetadataDTO metadata) throws TraitementException {
        Objects.requireNonNull(metadata);
        if(metadata.name().isEmpty() || metadata.name().length() > 32) {
            throw new TraitementException(ErrorIdentifier.PLAN_NAME_EXCEED_LIMIT);
        }
        if(metadata.section() != null && metadata.section().length() > 128) {
            throw new TraitementException(ErrorIdentifier.PLAN_SECTION_EXCEED_LIMIT);
        }
        if(metadata.section() !=null && !metadata.section().matches("^(?:[a-zA-Z0-9]+(?:/[a-zA-Z0-9]+)*)?$")) {
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
    private void managedFilesDirectory(Path path) throws TraitementException {
        try {
            Files.createDirectories(path);
        } catch (IOException e) {
            log.warning("IOException when looking and/or directories of the path : '" + path + "' : " + e.getMessage());
            throw new TraitementException(ErrorIdentifier.SERVER_ERROR);
        }
    }
}
