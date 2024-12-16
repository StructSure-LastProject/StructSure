package fr.uge.structsure.controllers;

import fr.uge.structsure.dto.StructureRequestDTO;
import fr.uge.structsure.repositories.StructureRepository;
import fr.uge.structsure.services.StructureService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/structure")
public class StructureController {
    private final StructureRepository structureRepository;
    private final StructureService structureService;

    public StructureController(StructureRepository structureRepository, StructureService structureService) {
        this.structureRepository = structureRepository;
        this.structureService = structureService;
    }

    @PostMapping("/api/ouvrages")
    public ResponseEntity<Object> createOuvrage(@RequestBody StructureRequestDTO request) {
        var ouvrage = structureService.createStructure(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ouvrage.data());
    }

    @RequestMapping("/test")
    public String getTest(){
        return "test";
    }


}
