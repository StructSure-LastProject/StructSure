package fr.uge.structsure.dto.sensors;

import fr.uge.structsure.entities.State;

public record SensorLastStateDTO(long id, String name, State state, String date) {
}
