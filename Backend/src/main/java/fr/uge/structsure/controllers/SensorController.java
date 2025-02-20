package fr.uge.structsure.controllers;

import fr.uge.structsure.dto.sensors.AddSensorRequestDTO;
import fr.uge.structsure.dto.sensors.AllSensorsByStructureRequestDTO;
import fr.uge.structsure.dto.sensors.SensorDTO;
import fr.uge.structsure.exceptions.TraitementException;
import fr.uge.structsure.services.SensorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;

/**
 * This class regroups all the endpoints controllers for the sensor
 */
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
            return e.toResponseEntity();
        }
    }

    /**
     * Endpoint to get the list of sensors present in a structure
     * @return List of sensors
     */
    @PostMapping("/structures/{id}/sensors")
    public ResponseEntity<?> getSensorsByStructure(@PathVariable("id") long id, @RequestBody AllSensorsByStructureRequestDTO request) {
        try {
            List<SensorDTO> sensorDTOs = sensorService.getSensors(id, request);
            return ResponseEntity.ok(sensorDTOs);
        } catch (TraitementException e) {
            return e.toResponseEntity();
        }
    }

    /**
     * Endpoint to get the lis of sensors prensent in a plan
     * @return List of the sensors
     */
    @GetMapping("/structures/{structureId}/plan/{planId}/sensors")
    public ResponseEntity<?> getSensorsByPlan(@PathVariable("structureId") long structureId,
                                              @PathVariable("planId") long planId) {
        try {
            List<SensorDTO> sensorDTOs = sensorService.getSensorsByPlanId(structureId, planId);
            return ResponseEntity.ok(sensorDTOs);
        } catch (TraitementException e) {
            return e.toResponseEntity();
        }
    }
}
