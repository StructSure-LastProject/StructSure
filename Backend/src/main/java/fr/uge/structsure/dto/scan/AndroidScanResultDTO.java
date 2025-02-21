package fr.uge.structsure.dto.scan;

import java.util.List;

public record AndroidScanResultDTO(
        Long scanId,
        String launchDate,
        String note,
        List<AndroidSensorResultDTO> results
) {}
