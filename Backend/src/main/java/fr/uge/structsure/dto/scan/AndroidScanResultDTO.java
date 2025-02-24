package fr.uge.structsure.dto.scan;

import java.util.List;

/**
 * The DTO for the scan result for the android application
 * It contains the structure id, the scan id, the launch date, the note, the login
 */
public record AndroidScanResultDTO(
        Long scanId,
        String launchDate,
        String note,
        List<AndroidSensorResultDTO> results
) {}
