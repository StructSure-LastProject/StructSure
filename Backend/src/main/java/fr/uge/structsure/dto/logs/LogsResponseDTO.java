package fr.uge.structsure.dto.logs;

import fr.uge.structsure.entities.AppLog;

import java.util.List;

/**
 * Dto for the response to send the all logs with pagination
 * @param total the total number of entries (without pagination)
 * @param pageSize the maximum number of items per page
 * @param logs the results that match the search and pagination
 */
public record LogsResponseDTO(long total, long pageSize, List<AppLog> logs) {}
