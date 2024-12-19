package fr.uge.structsure.controllers;


import fr.uge.structsure.dto.ErrorDTO;
import fr.uge.structsure.dto.sensors.AddSensorDTO;
import fr.uge.structsure.exceptions.ErrorMessages;
import fr.uge.structsure.exceptions.TraitementException;
import fr.uge.structsure.repositories.SensorRepository;
import fr.uge.structsure.services.SensorService;
import fr.uge.structsure.services.StructureService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class SensorController {

    private final SensorService sensorService;

    public SensorController (SensorService sensorService, StructureService structureService, SensorRepository sensorRepository) {
        this.sensorService = sensorService;
    }


    @PostMapping("/api/sensors")
    public ResponseEntity<?> addSensor(@RequestBody AddSensorDTO sensorDto) {
        try {
            return ResponseEntity.status(HttpStatus.CREATED).body(sensorService.addSensor(sensorDto));
        }catch (TraitementException e) {
            var error = ErrorMessages.getErrorMessage(e.getErrorIdentifier());
            return ResponseEntity.status(error.code()).body(new ErrorDTO(error.message()));
        }
    }

}
