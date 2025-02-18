package fr.uge.structsure.services;

import fr.uge.structsure.dto.scan.AndroidScanResultDTO;
import fr.uge.structsure.dto.scan.AndroidSensorResultDTO;
import fr.uge.structsure.entities.*;
import fr.uge.structsure.exceptions.Error;
import fr.uge.structsure.exceptions.TraitementException;
import fr.uge.structsure.repositories.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.stream.Collectors;
import java.util.Objects;

@Service
public class ScanService {
    private final ScanRepository scanRepository;
    private final ResultRepository resultRepository;

    @Autowired
    public ScanService(ScanRepository scanRepository, ResultRepository resultRepository) {
        this.scanRepository = Objects.requireNonNull(scanRepository);
        this.resultRepository = Objects.requireNonNull(resultRepository);
    }

    @Transactional(readOnly = true)
    public AndroidScanResultDTO getScanDetails(long scanId) throws TraitementException {
        Scan scan = scanRepository.findById(scanId)
                .orElseThrow(() -> new TraitementException(Error.SCAN_NOT_FOUND));

        var results = resultRepository.findByScan(scan)
                .stream()
                .map(result -> new AndroidSensorResultDTO(
                        result.getSensor().getSensorId().toString(),
                        result.getSensor().getSensorId().getControlChip(),
                        result.getSensor().getSensorId().getMeasureChip(),
                        result.getSensor().getName(),
                        result.getState().name(),
                        result.getSensor().getNote(),
                        result.getScan().getDate()
                ))
                .collect(Collectors.toList());

        return new AndroidScanResultDTO(
                scan.getId(),
                scan.getDate(),
                scan.getNote(),
                results
        );
    }
}