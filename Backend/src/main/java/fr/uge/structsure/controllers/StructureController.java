package fr.uge.structsure.controllers;

import fr.uge.structsure.repositories.StructureRepository;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/structure")
public class StructureController {
    private final StructureRepository structureRepository;

    public StructureController(StructureRepository structureRepository) {
        this.structureRepository = structureRepository;
    }

    @RequestMapping("/test")
    public String getTest(){
        return "test";
    }


}
