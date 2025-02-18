package fr.uge.structsure.dto.plan;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;

@JsonSerialize
public record PlanImageResponseDTO(
        Resource resource,
        String filename,
        MediaType mediaType
) {}