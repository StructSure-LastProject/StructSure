package fr.uge.structsure.controllers;

import fr.uge.structsure.dto.ErrorDTO;
import fr.uge.structsure.dto.plan.PlanMetadataDTO;
import fr.uge.structsure.dto.structure.AddStructureRequestDTO;
import fr.uge.structsure.dto.structure.StructureResponseDTO;
import fr.uge.structsure.exceptions.ErrorMessages;
import fr.uge.structsure.exceptions.TraitementException;
import fr.uge.structsure.services.PlanService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import fr.uge.structsure.services.StructureService;

import org.springframework.http.MediaType;
import org.springframework.web.multipart.MultipartFile;

import java.util.Objects;

@RestController
@RequestMapping("/api/structures")
public class StructureController {

    private final StructureService structureService;
    private final PlanService planService;

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
     *
     * @return a {@link ResponseEntity} containing:
     *         <ul>
     *           <li>The details of the created structure with an HTTP status of {@code 201 Created}, if successful.</li>
     *           <li>An error response with the appropriate HTTP status and error details in case of a {@link TraitementException}.</li>
     *         </ul>
     */
    @PostMapping
    public ResponseEntity<?> addStructure(@RequestBody AddStructureRequestDTO request) {
        try {
            var structure = structureService.createStructure(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(structure);
        } catch (TraitementException e) {
            var error = ErrorMessages.getErrorMessage(e.getErrorIdentifier());
            return ResponseEntity.status(error.code()).body(new ErrorDTO(error.message()));
        }
    }

/*    @PutMapping("/{id}/plans/{planId")
    public ResponseEntity<?> addPlan(@PathVariable("id") Long id, @PathVariable("planId") Long planId, @RequestPart("metadata") PlanMetadataDTO metadataDTO, @RequestPart("file") MultipartFile file) {
        try {
            var structure = planService.editPlan(id, planId, metadataDTO, file);
            return ResponseEntity.status(HttpStatus.OK).body(structure);
        } catch (TraitementException e) {
            var error = ErrorMessages.getErrorMessage(e.getErrorIdentifier());
            return ResponseEntity.status(error.code()).body(new ErrorDTO(error.message()));
        }
    }*/

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
     *
     * @return a {@link ResponseEntity} containing:
     *         <ul>
     *           <li>The details of the updated structure with an HTTP status of {@code 201 Created}, if successful.</li>
     *           <li>An error response with the appropriate HTTP status and error details in case of a {@link TraitementException}.</li>
     *         </ul>
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> editStructure(@PathVariable("id") Long id, @RequestBody AddStructureRequestDTO request) {
        try {
            var structure = structureService.editStructure(id, request);
            return ResponseEntity.status(HttpStatus.CREATED).body(structure);
        } catch (TraitementException e) {
            var error = ErrorMessages.getErrorMessage(e.getErrorIdentifier());
            return ResponseEntity.status(error.code()).body(new ErrorDTO(error.message()));
        }
    }


    /**
     * Handles the addition of a new plan to an existing structure.
     * Processes a multipart request containing both the plan metadata and the plan file.
     *
     * @param id The ID of the structure to which the plan will be added
     * @param metadataDTO The DTO containing plan metadata (name and section)
     * @param file The multipart file containing the plan image
     * @return ResponseEntity containing either the created plan details or an error message
     */
    @PostMapping("/{id}/plans")
    public ResponseEntity<?> addPlan(@PathVariable("id") Long id, @RequestPart("metadata") PlanMetadataDTO metadataDTO, @RequestPart("file") MultipartFile file) {
        try {
            var structure = planService.createPlan(id, metadataDTO, file);
            return ResponseEntity.status(HttpStatus.CREATED).body(structure);
        } catch (TraitementException e) {
            var error = ErrorMessages.getErrorMessage(e.getErrorIdentifier());
            return ResponseEntity.status(error.code()).body(new ErrorDTO(error.message()));
        }
    }

    /**
     * This method handle the structure endpoint to get all structures
     * @return List of structures
     */
    @GetMapping(value = "", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getAllStructure(){
        try {
            return ResponseEntity.status(200).body(structureService.getAllStructure());
        } catch (TraitementException e) {
            var error = ErrorMessages.getErrorMessage(e.getErrorIdentifier());
            return ResponseEntity.status(error.code()).body(new ErrorDTO(error.message()));
        }
    }

    @GetMapping(value = "/android/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public StructureResponseDTO getStructureById(@PathVariable("id") Long id){
        return structureService.getStructureById(id);
    }


    /**
     * Returns the structure details with the specified id
     * @param id the id of the structure
     * @return StructureDetailsResponseDTO the detail of the structure,
     *  or Error if structure not found
     */
    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> structureDetail(@PathVariable("id") long id) {
        try {
            var detail = structureService.structureDetail(id);
            return ResponseEntity.status(200).body(detail);
        } catch (TraitementException e) {
            var error = ErrorMessages.getErrorMessage(e.getErrorIdentifier());
            return ResponseEntity.status(error.code()).body(new ErrorDTO(error.message()));
        }
    }
}
