package fr.uge.structsure.controllers;

import fr.uge.structsure.dto.sensors.*;
import fr.uge.structsure.exceptions.TraitementException;
import fr.uge.structsure.services.SensorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * This class regroups all the endpoints controllers for the sensor
 */
@RestController
@RequestMapping("/api")
public class SensorController {
    private final SensorService sensorService;

    /**
     * The constructor for the Sensor controller
     * @param sensorService the sensor service
     */
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
    public ResponseEntity<?> addSensor(@RequestBody BaseSensorDTO request) {
        try {
            var sensor = sensorService.createSensor(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(sensor);
        } catch (TraitementException e) {
            return e.toResponseEntity("Sensor creation rejected: {}");
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
            var sizeOfResult = sensorService.countSensors(id, request);
            return ResponseEntity.ok(new SensorByStructureResponseDTO(sizeOfResult, sensorDTOs));
        } catch (TraitementException e) {
            return e.toResponseEntity();
        }
    }

    /**
     * Endpoint to get the list of sensors prensent in a plan
     * @return List of the sensors
     */
    @GetMapping("/structures/{structureId}/plan/{planId}/sensors")
    public ResponseEntity<?> getSensorsByPlan(@PathVariable("structureId") long structureId,
                                              @PathVariable("planId") long planId,
                                              @RequestParam("scanId") Optional<Long> scanId) {
        try {
            List<SensorDTO> sensorDTOs = sensorService.getSensorsByPlanId(structureId, planId, scanId);
            return ResponseEntity.ok(sensorDTOs);
        } catch (TraitementException e) {
            return e.toResponseEntity();
        }
    }

    /**
     * Endpoint to delete the position of a sensor in a plan
     */
    @DeleteMapping("/sensors/{controlChip}/{measureChip}/position/delete")
    public ResponseEntity<?> deletePositionOfSensor(@PathVariable("controlChip") String controlChip,
                                                    @PathVariable("measureChip") String measureChip) {
        try {
            DeletePositionSensorResponseDTO deteletedSensor = sensorService.deletePositionOfSensor(controlChip, measureChip);
            return ResponseEntity.ok(deteletedSensor);
        } catch (TraitementException e) {
            return e.toResponseEntity();
        }
    }




    /**
     * Edit a sensor
     * @return The edit sensor response DTO
     */
    @PutMapping("/sensors/edit")
    public ResponseEntity<?> editSensor(@RequestBody EditSensorRequestDTO editSensorRequestDTO){
        try {
            return ResponseEntity.ok(sensorService.editSensor(editSensorRequestDTO));
        } catch (TraitementException e){
            return e.toResponseEntity("Sensor update rejected: {}");
        }
    }

    /**
     * Endpoint to give a position to a sensor in a plan
     * @return ResponseEntity<?> the response containing the sensor id if success, and error if not
     */
    @PostMapping("sensors/position")
    public ResponseEntity<?> getSensorsByStructure(@RequestBody SensorPositionRequestDTO request) {
        try {
            SensorPositionResponseDTO sensorDTOs = sensorService.positionSensor(request);
            return ResponseEntity.ok(sensorDTOs);
        } catch (TraitementException e) {
            return e.toResponseEntity("Sensor placement rejected: {}");
        }
    }


}
