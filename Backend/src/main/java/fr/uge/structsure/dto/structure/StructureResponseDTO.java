package fr.uge.structsure.dto.structure;

import fr.uge.structsure.entities.Plan;
import fr.uge.structsure.entities.Sensor;

import java.util.List;

public record StructureResponseDTO(
    Long id,
    String name,
    String note,
    List<Plan> plans,
    List<Sensor> sensors
) { }
