package fr.uge.structsure.controllers;

import fr.uge.structsure.dto.scan.AndroidScanResultDTO;
import fr.uge.structsure.exceptions.TraitementException;
import fr.uge.structsure.services.ScanService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Objects;


@RestController
@RequestMapping("/api/scans")
public class AndroidScanController {
    private final ScanService scanService;

    @Autowired
    public AndroidScanController(ScanService scanService) {
        this.scanService = Objects.requireNonNull(scanService);
    }

    @PostMapping
    public ResponseEntity<?> submitScanResults(HttpServletRequest request, @RequestBody AndroidScanResultDTO scanData) {
        try {
            scanService.saveScanResults(request, scanData);
            return ResponseEntity.ok().build();
        } catch (TraitementException e) {
            return e.toResponseEntity("Android scan rejected: {}");
        }
    }
}