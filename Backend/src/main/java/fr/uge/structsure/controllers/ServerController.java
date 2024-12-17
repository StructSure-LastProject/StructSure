package fr.uge.structsure.controllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/serverStatus")
public class ServerController {

    @GetMapping()
    public boolean checkServerStatus(){
        return true;
    }
}
