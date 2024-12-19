package fr.uge.structsure.controllers;

import fr.uge.structsure.dto.sensors.SensorDTO;
import fr.uge.structsure.dto.structure.GetAllStructureRequest;
import fr.uge.structsure.services.SensorService;
import fr.uge.structsure.utils.OrderEnum;
import fr.uge.structsure.utils.SortEnum;
import org.springframework.context.annotation.Bean;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalTime;
import java.util.List;

import fr.uge.structsure.dto.structure.AllStructureResponseDTO;
import fr.uge.structsure.services.StructureService;
import org.springframework.http.MediaType;

import java.util.Objects;

@RestController
@RequestMapping("/api/structure")
public class StructureController {

    private final StructureService structureService;
    private final SensorService sensorService;

    public StructureController(StructureService structureService, SensorService sensorService) {
        this.structureService = structureService;
        this.sensorService = sensorService;
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



}
