package fr.uge.structsure.dto.sensors;

import fr.uge.structsure.entities.AppLog;

import java.util.List;

/**
 * Dto for the response to send the all logs with pagination
 * @param total the total number of entries (without pagination)
 * @param logs the results that match the search and pagination
 */
public record LogsResponseDTO(long total, List<AppLog> logs) {}
