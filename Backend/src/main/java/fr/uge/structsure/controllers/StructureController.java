package fr.uge.structsure.controllers;

import fr.uge.structsure.dto.ErrorDTO;
import fr.uge.structsure.dto.structure.AddStructureRequestDTO;
import fr.uge.structsure.exceptions.ErrorMessages;
import fr.uge.structsure.exceptions.TraitementException;
import fr.uge.structsure.repositories.StructureRepository;
import fr.uge.structsure.services.StructureService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class StructureController {
    private final StructureRepository structureRepository;
    private final StructureService structureService;

    @Autowired
    public StructureController(StructureRepository structureRepository, StructureService structureService) {
        this.structureRepository = structureRepository;
        this.structureService = structureService;
    }

    @PostMapping("/api/structure")
    public ResponseEntity<?> addStructure(@RequestBody AddStructureRequestDTO request) {
        try {
            var structure = structureService.createStructure(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(structure);
        } catch (TraitementException e) {
            var error = ErrorMessages.getErrorMessage(e.getCode());
            return ResponseEntity.status(error.code()).body(new ErrorDTO(error.message()));
        }
    }

    @RequestMapping("/test")
    public String getTest(){
        return "test";
    }


}
