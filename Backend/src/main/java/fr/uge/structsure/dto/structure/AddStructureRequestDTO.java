package fr.uge.structsure.dto.structure;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import fr.uge.structsure.entities.Structure;
import fr.uge.structsure.utils.DiffMaker;

import java.util.Objects;

/**
 * DTO to map a new structure creation request fields
 * @param name the name of the structure
 * @param note the note of the structure
 */
@JsonSerialize
public record AddStructureRequestDTO(String name, String note) {
    public AddStructureRequestDTO {
        Objects.requireNonNull(name);
        Objects.requireNonNull(note);
    }

    /**
     * Creates a list of updated fields ready to be logged.
     * @param structure the initial data
     * @return the changed values
     */
    public String logDiff(Structure structure) {
        return new DiffMaker()
            .add("Nom", structure.getName(), name)
            .add("Note", structure.getNote(), note)
            .toString();
    }
}
