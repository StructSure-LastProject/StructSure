package fr.uge.structsure.controllers;

import fr.uge.structsure.dto.sensors.SensorFilterDTO;
import fr.uge.structsure.dto.structure.GetAllStructureRequest;
import fr.uge.structsure.entities.Plan;
import fr.uge.structsure.entities.State;
import fr.uge.structsure.repositories.PlanRepository;
import fr.uge.structsure.repositories.SensorRepository;
import fr.uge.structsure.services.ResultService;
import fr.uge.structsure.services.SensorService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

import fr.uge.structsure.dto.structure.AllStructureResponseDTO;
import fr.uge.structsure.services.StructureService;
import org.springframework.http.MediaType;

import java.util.NoSuchElementException;
import java.util.Objects;

@RestController
@RequestMapping("/api/structure")
public class StructureController {

    private final StructureService structureService;
    private final SensorService sensorService;
    private final SensorRepository sensorRepository;
    private final PlanRepository planRepository;
    private final ResultService resultService;

    public StructureController(StructureService structureService, SensorService sensorService, SensorRepository sensorRepository, PlanRepository planRepository, ResultService resultService) {
        this.structureService = structureService;
        this.sensorService = sensorService;
        this.sensorRepository = sensorRepository;
        this.planRepository = planRepository;
        this.resultService = resultService;
    }

    @GetMapping("/{id}/sensors")
    public ResponseEntity<?> getSensorsByStructureId(
            @PathVariable Long id,
            @RequestParam(required = false) String tri,
            @RequestParam(required = false) String ordre,
            @RequestParam(required = false) String filtreEtat,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateInstallationMin,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateInstallationMax
    ) {
        SensorFilterDTO filter = new SensorFilterDTO();

        filter.setTri(tri);
        filter.setOrdre(ordre);
        filter.setFiltreEtat(filtreEtat);
        filter.setDateInstallationMin(dateInstallationMin);
        filter.setDateInstallationMax(dateInstallationMax);

        return ResponseEntity.ok(sensorService.getSensorsByStructureId(id, filter));
    }

    @PostMapping(value = "", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public List<AllStructureResponseDTO> getAllStructure(@RequestBody GetAllStructureRequest getAllStructureRequest){
        Objects.requireNonNull(getAllStructureRequest);
        return structureService.getAllStructure(getAllStructureRequest);
    }

    @GetMapping("/test")
    public void getLatest(){
        var sensor1 = sensorRepository.findSensorBySensorId_ControlChipAndSensorId_MeasureChip("chip control 0.0","chip measure 0.0");
        var sensor2 = sensorRepository.findSensorBySensorId_ControlChipAndSensorId_MeasureChip("chip control 7000.0","chip measure 7000.0");
        var sensor3 = sensorRepository.findSensorBySensorId_ControlChipAndSensorId_MeasureChip("chip control 14000.0","chip measure 14000.0");
        System.out.println("state of the chip 1" + resultService.getLatestStateByChip(sensor1));
        System.out.println("state of the chip 2" + resultService.getLatestStateByChip(sensor2));
        System.out.println("state of the chip 3" + resultService.getLatestStateByChip(sensor3));
    }
}
