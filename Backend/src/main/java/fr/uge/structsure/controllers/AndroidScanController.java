package fr.uge.structsure.controllers;


import fr.uge.structsure.dto.ErrorDTO;
import fr.uge.structsure.dto.scan.AndroidScanResultDTO;
import fr.uge.structsure.exceptions.ErrorMessages;
import fr.uge.structsure.exceptions.TraitementException;
import fr.uge.structsure.services.ScanService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Objects;

@RestController
@RequestMapping("/api/android/scans")
public class AndroidScanController {
    private final ScanService scanService;

    @Autowired
    public AndroidScanController(ScanService scanService) {
        this.scanService = Objects.requireNonNull(scanService);
    }

    @GetMapping("/receive")
    public ResponseEntity<?> receiveScanData(@RequestBody AndroidScanResultDTO scanData) {
        try {
            var response = scanService.processScanFromAndroid(scanData);
            return ResponseEntity.ok(response);
        } catch (TraitementException e) {
            var error = ErrorMessages.getErrorMessage(e.getErrorIdentifier());
            return ResponseEntity.status(error.code())
                    .body(new ErrorDTO(error.message()));
        }
    }
}