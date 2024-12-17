package fr.uge.structsure.controllers;

import fr.uge.User;
import fr.uge.structsure.config.JwtUtils;
import fr.uge.structsure.dto.ErrorDTO;
import fr.uge.structsure.dto.LoginRequestDTO;
import fr.uge.structsure.dto.LoginResponseDTO;
import fr.uge.structsure.dto.RegisterRequestDTO;
import fr.uge.structsure.exceptions.ErrorMessages;
import fr.uge.structsure.exceptions.TraitementException;
import fr.uge.structsure.repositories.AccountRepository;
import fr.uge.structsure.services.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Objects;

@RestController
@RequestMapping("/api/auth")
public class AuthenticationController {

    private final PasswordEncoder passwordEncoder;

    private final AccountRepository accountRepository;

    private final AccountService accountService;


    @Autowired
    public AuthenticationController(PasswordEncoder passwordEncoder,
                                    AccountRepository accountRepository,
                                    AccountService accountService) {
        this.passwordEncoder = Objects.requireNonNull(passwordEncoder);
        this.accountRepository = Objects.requireNonNull(accountRepository);
        this.accountService = Objects.requireNonNull(accountService);
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequestDTO registerRequestDTO) {
        try {
            return ResponseEntity.status(200).body(accountService.register(registerRequestDTO));
        } catch (TraitementException e) {
            var error = ErrorMessages.getErrorMessage(e.getCode());
            return ResponseEntity.status(error.code()).body(new ErrorDTO(error.message()));
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequestDTO loginRequestDTO) {
        try {
            return ResponseEntity.status(200).body(accountService.login(loginRequestDTO));
        } catch (TraitementException e) {
            var error = ErrorMessages.getErrorMessage(e.getCode());
            return ResponseEntity.status(error.code()).body(new ErrorDTO(error.message()));
        }
    }

    @GetMapping("/tests")
    public ResponseEntity<?> testLogin() {
        return ResponseEntity.ok().body("Login");
    }
}

