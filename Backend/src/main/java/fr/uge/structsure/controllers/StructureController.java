package fr.uge.structsure.controllers;

import fr.uge.structsure.dto.ErrorDTO;
import fr.uge.structsure.dto.plan.AddPlanRequestDTO;
import fr.uge.structsure.dto.sensors.SensorDTO;
import fr.uge.structsure.dto.structure.*;
import fr.uge.structsure.exceptions.ErrorMessages;
import fr.uge.structsure.exceptions.TraitementException;
import fr.uge.structsure.services.PlanService;
import fr.uge.structsure.services.SensorService;
import fr.uge.structsure.utils.OrderEnum;
import fr.uge.structsure.utils.SortEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalTime;
import java.util.List;

import fr.uge.structsure.services.StructureService;

import org.springframework.http.MediaType;
import org.springframework.web.multipart.MultipartFile;

import java.util.Objects;

@RestController
@RequestMapping("/api/structures")
public class StructureController {

    private final StructureService structureService;
    private final SensorService sensorService;
    private final PlanService planService;

    @Autowired
    public StructureController(StructureService structureService, SensorService sensorService, PlanService planService) {
        this.structureService = Objects.requireNonNull(structureService);
        this.sensorService = Objects.requireNonNull(sensorService);
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


    @PostMapping("/{id}/plans")
    public ResponseEntity<?> addPlan(@PathVariable("id") Long id, @RequestPart("metadata") AddPlanRequestDTO request, @RequestPart("file") MultipartFile file) {
        try {
            var structure = planService.createPlan(id, request, file);
            return ResponseEntity.status(HttpStatus.CREATED).body(structure);
        } catch (TraitementException e) {
            var error = ErrorMessages.getErrorMessage(e.getErrorIdentifier());
            return ResponseEntity.status(error.code()).body(new ErrorDTO(error.message()));
        }
    }

    /**
     * Endpoint pour récupérer la liste des capteurs d'un ouvrage donné avec options de tri et filtre.
     *
     * @param id               L'ID de l'ouvrage
     * @param tri              Critère de tri : "nom", "etat", "dateDerniereInterrogation", "dateInstallation"
     * @param ordre            Ordre de tri : "asc" ou "desc"
     * @param filtreEtat       Filtre par état : "actif", "archivé", "défaillant"
     * @param dateInstallationMin Date minimale pour l'installation des capteurs
     * @param dateInstallationMax Date maximale pour l'installation des capteurs
     * @return Liste des capteurs (DTO)
     */
    @GetMapping("/{id}/sensors")
    public ResponseEntity<?> getSensorsByStructure(
            @PathVariable("id") Long id,
            @RequestParam(value = "tri", required = false, defaultValue = "nom") String tri,
            @RequestParam(value = "ordre", required = false, defaultValue = "asc") String ordre,
            @RequestParam(value = "filtreEtat", required = false) String filtreEtat,
            @RequestParam(value = "dateInstallationMin", required = false)
            @DateTimeFormat(pattern = "HH:mm:ss") LocalTime dateInstallationMin,
            @RequestParam(value = "dateInstallationMax", required = false)
            @DateTimeFormat(pattern = "HH:mm:ss") LocalTime dateInstallationMax) {

        try {
            List<SensorDTO> sensorDTOs = sensorService.getSensorDTOsByStructure(
                    id, tri, ordre, filtreEtat, dateInstallationMin, dateInstallationMax);
            return ResponseEntity.ok(sensorDTOs);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.unprocessableEntity().body(new ErrorResponse("422", e.getMessage()));
        } catch (RuntimeException e) {
            return ResponseEntity.status(404).body(new ErrorResponse("404", "Ouvrage introuvable"));
        }
    }


    public record ErrorResponse(String code, String message) {
    }

    /**
     * This method handle the structure endpoint to get all structures
     * @param searchByName Object that represents the request
     * @param sort Object that represents the request
     * @param order Object that represents the request
     * @return List of structures
     */
    @GetMapping(value = "", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<AllStructureResponseDTO> getAllStructure(@RequestParam(required = false) String searchByName,
                                                         @RequestParam(required = false) SortEnum sort,
                                                         @RequestParam(required = false) OrderEnum order){
        Objects.requireNonNull(searchByName);
        Objects.requireNonNull(sort);
        Objects.requireNonNull(order);
        return structureService.getAllStructure(new GetAllStructureRequest(searchByName, sort, order));
    }

    @GetMapping(value = "/android", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<AllStructureResponseDTO> getAllStructure(){
        return structureService.getAllStructure();
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
