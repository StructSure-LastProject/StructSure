package fr.uge.structsure.controllers;

import fr.uge.structsure.dto.sensors.SensorDTO;
import fr.uge.structsure.dto.sensors.SensorLastStateDTO;
import fr.uge.structsure.dto.structure.GetAllStructureRequest;
import fr.uge.structsure.entities.SensorId;
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
@RequestMapping("/api/sensors")
public class SensorController {
    private final SensorService sensorService;

    public SensorController(SensorService sensorService) {
        this.sensorService = sensorService;
    }
}
