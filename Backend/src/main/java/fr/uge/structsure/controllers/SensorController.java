package fr.uge.structsure.controllers;

import fr.uge.structsure.services.ResultService;
import fr.uge.structsure.services.SensorService;
import fr.uge.structsure.services.StructureService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Objects;

@RestController
@RequestMapping("/api/sensor")
public class SensorController {
    private final ResultService resultService;

    @Autowired
    public SensorController(ResultService resultService) {
        this.resultService = Objects.requireNonNull(resultService);
    }
    @GetMapping("/{controlChip}/{measureChip}/state")
    public ResponseEntity<?> getSensorState(
            @PathVariable String controlChip,
            @PathVariable String measureChip
    ){
        return ResponseEntity.status(HttpStatus.OK).body(resultService.getLatestStateByChip(controlChip,measureChip));
    }
}
