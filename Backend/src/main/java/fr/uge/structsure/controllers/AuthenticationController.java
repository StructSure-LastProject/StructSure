package fr.uge.structsure.controllers;

import fr.uge.structsure.dto.auth.LoginRequestDTO;
import fr.uge.structsure.dto.auth.RegisterRequestDTO;
import fr.uge.structsure.exceptions.TraitementException;
import fr.uge.structsure.services.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Objects;

@RestController
@RequestMapping("/api")
public class AuthenticationController {

    private final AccountService accountService;


    @Autowired
    public AuthenticationController(AccountService accountService) {
        this.accountService = Objects.requireNonNull(accountService);
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequestDTO registerRequestDTO) {
        try {
            return ResponseEntity.status(200).body(accountService.register(registerRequestDTO));
        } catch (TraitementException e) {
            return e.toResponseEntity("Account creation rejected: {}");
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequestDTO loginRequestDTO) {
        try {
            return ResponseEntity.status(200).body(accountService.login(loginRequestDTO));
        } catch (TraitementException e) {
            return e.toResponseEntity("User login rejected: {}");
        }
    }

    @GetMapping("/test")
    public ResponseEntity<?> login() {
        return ResponseEntity.status(200).body("test");
    }
}

