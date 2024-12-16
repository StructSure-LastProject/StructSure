package fr.uge.structsure.controllers;

import fr.uge.structsure.dto.sensors.SensorFilterDTO;
import fr.uge.structsure.dto.sensors.SensorResponseDTO;
import fr.uge.structsure.services.SensorService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/structure")
public class StructureController {
    private final SensorService sensorService;

    public StructureController(SensorService sensorService) {
        this.sensorService = sensorService;
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

}
