package fr.uge.structsure.controllers;

import fr.uge.structsure.dto.sensors.AddSensorRequestDTO;
import fr.uge.structsure.dto.sensors.SensorDTO;
import fr.uge.structsure.exceptions.TraitementException;
import fr.uge.structsure.services.SensorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
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
            return e.toResponseEntity();
        }
    }

    /**
     * Endpoint pour récupérer la liste des capteurs d'un ouvrage donné avec options de tri et filtre.
     * @return Liste des capteurs (DTO)
     */
    @GetMapping("/structures/{id}/sensors")
    public ResponseEntity<?> getSensorsByStructure(@PathVariable("id") long id) {
        try {
            List<SensorDTO> sensorDTOs = sensorService.getSensors(id);
            return ResponseEntity.ok(sensorDTOs);
        } catch (TraitementException e) {
            return e.toResponseEntity();
        }
    }
}
