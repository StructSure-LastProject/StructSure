package fr.uge.structsure.dto.plan;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import fr.uge.structsure.entities.Plan;
import org.springframework.web.multipart.MultipartFile;

import java.util.Objects;

@JsonSerialize
public record AddPlanRequestDTO(String name, String section) {
}
