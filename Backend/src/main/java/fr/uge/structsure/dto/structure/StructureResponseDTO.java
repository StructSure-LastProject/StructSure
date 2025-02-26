package fr.uge.structsure.dto.structure;

import fr.uge.structsure.dto.plan.PlanDTO;
import fr.uge.structsure.dto.sensors.SensorDTO;
import fr.uge.structsure.entities.Structure;

import java.util.List;

public record StructureResponseDTO(
    Long id,
    String name,
    String note,
    List<PlanDTO> plans,
    List<SensorDTO> sensors
) {
    /**
     * Alternative constructor that directly takes a structure and
     * fill internal values automatically.
     * @param structure the structure to get id, name and note from
     * @param plans the plans to attach in the response
     * @param sensors the sensors to attach in the response
     */
    public StructureResponseDTO(Structure structure, List<PlanDTO> plans, List<SensorDTO> sensors) {
        this(structure.getId(), structure.getName(), structure.getNote(), plans, sensors);
    }
}
