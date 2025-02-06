package fr.uge.structsure.controllers;

import fr.uge.structsure.dto.ErrorDTO;
import fr.uge.structsure.dto.sensors.AddSensorRequestDTO;
import fr.uge.structsure.dto.sensors.SensorDTO;
import fr.uge.structsure.dto.structure.AddStructureRequestDTO;
import fr.uge.structsure.exceptions.ErrorMessages;
import fr.uge.structsure.exceptions.TraitementException;
import fr.uge.structsure.services.PlanService;
import fr.uge.structsure.services.SensorService;
import fr.uge.structsure.services.StructureService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Objects;

@RestController
@RequestMapping("/api")
public class SensorController {
    private final SensorService sensorService;

    @Autowired
    public SensorController(SensorService sensorService) {
        this.sensorService = Objects.requireNonNull(sensorService);
    }

    /**
     * Handles HTTP POST requests to create a new sensor.
     *
     * This method receives a sensor creation request, validates it, and attempts to add the sensor.
     * If the sensor is successfully created, it returns a 201 Created response with the sensor details.
     * If an exception occurs (e.g., missing data, duplicate name or ID, or invalid structure),
     * an appropriate error response is returned.
     *
     * @param request The request body containing sensor details.
     * @return A ResponseEntity containing either the created sensor data (on success)
     *         or an error message (on failure).
     */
    @PostMapping("/sensors")
    public ResponseEntity<?> addSensor(@RequestBody AddSensorRequestDTO request) {
        try {
            var sensor = sensorService.createSensor(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(sensor);
        } catch (TraitementException e) {
            var error = ErrorMessages.getErrorMessage(e.getErrorIdentifier());
            return ResponseEntity.status(error.code()).body(new ErrorDTO(error.message()));
        }
    }
}
