package fr.uge.structsure.controllers;

import fr.uge.structsure.dto.plan.PlanMetadataDTO;
import fr.uge.structsure.dto.structure.AddStructureRequestDTO;
import fr.uge.structsure.dto.structure.AllStructureRequestDTO;
import fr.uge.structsure.exceptions.TraitementException;
import fr.uge.structsure.services.PlanService;
import fr.uge.structsure.services.StructureService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Objects;
import java.util.Optional;

/**
 * Controller for structure endpoints
 */
@RestController
@RequestMapping("/api/structures")
public class StructureController {

    private final StructureService structureService;
    private final PlanService planService;


    /**
     * The constructor of the Structure controller
     * @param structureService the structure service
     * @param planService the plan service
     */
    @Autowired
    public StructureController(StructureService structureService, PlanService planService) {
        this.structureService = Objects.requireNonNull(structureService);
        this.planService = planService;
    }

    /**
     * Handles the addition of a new structure.
     * This method processes a request to create a new structure by invoking the appropriate service method.
     * If the operation is successful, it returns a response with the details of the created structure.
     * In case of a business exception, it returns an appropriate error response.
     *
     * @param request the {@link AddStructureRequestDTO} containing the details of the structure to be added.
     * @return a {@link ResponseEntity} containing:
     * <ul>
     *   <li>The details of the created structure with an HTTP status of {@code 201 Created}, if successful.</li>
     *   <li>An error response with the appropriate HTTP status and error details in case of a {@link TraitementException}.</li>
     * </ul>
     */
    @PostMapping
    public ResponseEntity<?> addStructure(@RequestBody AddStructureRequestDTO request) {
        try {
            var structure = structureService.createStructure(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(structure);
        } catch (TraitementException e) {
            return e.toResponseEntity("Structure creation rejected: {}");
        }
    }

    /**
     * Updates a plan within a structure.
     *
     * @param id          The ID of the structure containing the plan
     * @param planId      The ID of the plan to edit
     * @param metadataDTO The DTO containing updated plan metadata
     * @param file        The new file for the plan
     * @return ResponseEntity containing either the updated plan details or an error message
     */
    @PutMapping("/{id}/plans/{planId}")
    public ResponseEntity<?> editPlan(@PathVariable("id") Long id, @PathVariable("planId") Long planId, @RequestPart("metadata") PlanMetadataDTO metadataDTO, @RequestPart("file") Optional<MultipartFile> file) {
        try {
            var structure = planService.editPlan(id, planId, metadataDTO, file);
            return ResponseEntity.status(HttpStatus.OK).body(structure);
        } catch (TraitementException e) {
            return e.toResponseEntity("Plan update rejected: {}");
        }
    }

    /**
     * Updates an existing structure in the system.
     * This method handles HTTP PUT requests to update the name and/or note of a structure
     * identified by its ID. It delegates the update operation to the service layer.
     * If the update is successful, it returns the details of the updated structure.
     * If a business exception occurs, it returns an appropriate error response.
     *
     * @param id      the ID of the structure to be updated, provided as a path variable.
     * @param request the {@link AddStructureRequestDTO} containing the updated details
     *                for the structure. The name must not be null, empty, or exceed 64 characters.
     *                The note must not exceed 1000 characters.
     * @return a {@link ResponseEntity} containing:
     * <ul>
     *   <li>The details of the updated structure with an HTTP status of {@code 201 Created}, if successful.</li>
     *   <li>An error response with the appropriate HTTP status and error details in case of a {@link TraitementException}.</li>
     * </ul>
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> editStructure(@PathVariable("id") Long id, @RequestBody AddStructureRequestDTO request) {
        try {
            var structure = structureService.editStructure(id, request);
            return ResponseEntity.status(HttpStatus.CREATED).body(structure);
        } catch (TraitementException e) {
            return e.toResponseEntity("Structure edition rejected: {}");
        }
    }


    /**
     * Handles the addition of a new plan to an existing structure.
     * Processes a multipart request containing both the plan metadata and the plan file.
     *
     * @param id          The ID of the structure to which the plan will be added
     * @param metadataDTO The DTO containing plan metadata (name and section)
     * @param file        The multipart file containing the plan image
     * @return ResponseEntity containing either the created plan details or an error message
     */
    @PostMapping("/{id}/plans")
    public ResponseEntity<?> addPlan(@PathVariable("id") Long id, @RequestPart("metadata") PlanMetadataDTO metadataDTO, @RequestPart("file") MultipartFile file) {
        try {
            var structure = planService.createPlan(id, metadataDTO, file);
            return ResponseEntity.status(HttpStatus.CREATED).body(structure);
        } catch (TraitementException e) {
            return e.toResponseEntity("Plan creation rejected: {}");
        }
    }


    /**
     * Retrieves the image of a plan within a structure.
     *
     * @param planId The ID of the plan whose image is to be retrieved
     * @return ResponseEntity containing the plan image if successful, or an error message if the operation fails
     */
    @GetMapping("/plans/{planId}/image")
    public ResponseEntity<?> downloadPlanImage(@PathVariable("planId") Long planId) {
        try {
            var imageResponse = planService.downloadPlanImage(planId);
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION,
                            "inline; filename=\"" + imageResponse.filename() + "\"")
                    .contentType(imageResponse.mediaType())
                    .body(imageResponse.resource());
        } catch (TraitementException e) {
            return e.toResponseEntity("Failed to get image for plan " + planId + ": {}");
        }
    }

    /**
     * Retrieves the image of a plan within a structure that associated with the given sensor.
     *
     * @param controlChip The control chip
     * @param measureChip The measure chip
     * @param structureId The structure id
     * @return ResponseEntity containing the plan image if successful, or an error message if the operation fails
     */
    @GetMapping("/plans/{structureId}/{controlChip}/{measureChip}/image")
    public ResponseEntity<?> downloadPlanImageAssociatedToTheSensor(
            @PathVariable("structureId") long structureId,
            @PathVariable("controlChip") String controlChip,
            @PathVariable("measureChip") String measureChip
        ) {
        Objects.requireNonNull(controlChip);
        Objects.requireNonNull(measureChip);
        try {
            var imageResponse = planService.downloadPlanImageAssociatedToTheSensor(structureId, controlChip, measureChip);
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION,
                            "inline; filename=\"" + imageResponse.filename() + "\"")
                    .contentType(imageResponse.mediaType())
                    .body(imageResponse.resource());
        } catch (TraitementException e) {
            return e.toResponseEntity("Failed to get plan image for sensor " + controlChip + "-" + measureChip + ": {}");
        }
    }


    /**
     * This method handle the structure endpoint to get all structures
     * @param allStructureRequestDTO The request DTO
     * @param httpRequest The http request to check the permission
     * @return List of structures
     */
    @GetMapping(value = "", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getAllStructure(AllStructureRequestDTO allStructureRequestDTO, HttpServletRequest httpRequest) {
        try {
            return ResponseEntity.status(200).body(structureService.getAllStructure(allStructureRequestDTO, httpRequest));
        } catch (TraitementException e) {
            return e.toResponseEntity("Failed to send structures list: {}");
        }
    }

    @GetMapping(value = "/android/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getStructureById(@PathVariable("id") Long id) {
        try {
            return ResponseEntity.status(200).body(structureService.downloadStructureAndroid(id));
        } catch (TraitementException e) {
            return e.toResponseEntity("Failed to send structures list to Android: {}");
        }
    }

    /**
     * Returns the structure details with the specified id
     *
     * @param id the id of the structure
     * @param httpServletRequest The http request to check the permission
     * @return StructureDetailsResponseDTO the detail of the structure,
     * or Error if structure not found
     */
    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> structureDetail(@PathVariable("id") long id, HttpServletRequest httpServletRequest) {
        try {
            var detail = structureService.structureDetail(id, httpServletRequest);
            return ResponseEntity.status(200).body(detail);
        } catch (TraitementException e) {
            return e.toResponseEntity("Failed to get structure details: {}");
        }
    }
}
