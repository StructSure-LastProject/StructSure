package fr.uge.structsure.controllers;

import fr.uge.structsure.dto.auth.ChangePasswordRequestDTO;
import fr.uge.structsure.dto.auth.LoginRequestDTO;
import fr.uge.structsure.dto.auth.RegisterRequestDTO;
import fr.uge.structsure.exceptions.TraitementException;
import fr.uge.structsure.services.AccountService;
import fr.uge.structsure.utils.AuthenticationType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Objects;

/**
 * Controller for Authentication endpoints
 */
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
            return ResponseEntity.status(200).body(accountService.login(loginRequestDTO, AuthenticationType.WEB));
        } catch (TraitementException e) {
            return e.toResponseEntity("User login rejected: {}");
        }
    }

    /**
     * Controller that will generate token for android users
     * @param loginRequestDTO dto containing login and password
     * @return the response entity
     */
    @PostMapping("/android/login")
    public ResponseEntity<?> androidLogin(@RequestBody LoginRequestDTO loginRequestDTO) {
        try {
            return ResponseEntity.status(200).body(accountService.login(loginRequestDTO, AuthenticationType.ANDROID));
        } catch (TraitementException e) {
            return e.toResponseEntity("User login rejected: {}");
        }
    }

    /**
     * Handles the request to change a user's password.
     *
     * This endpoint processes a password change request by validating the provided credentials
     * and updating the user's password if all conditions are met.
     *
     * @param changePasswordRequestDTO The DTO containing user ID, current password, new password, and confirmation.
     * @return A {@link ResponseEntity} containing the response DTO if successful,
     *         or an error response in case of failure.
     */
    @PostMapping("/change-password")
    public ResponseEntity<?> changePassword(@RequestBody ChangePasswordRequestDTO changePasswordRequestDTO) {
        try {
            return ResponseEntity.status(200).body(accountService.changePassword(changePasswordRequestDTO));
        } catch (TraitementException e) {
            return e.toResponseEntity("User login rejected: {}");
        }
    }

    @GetMapping("/test")
    public ResponseEntity<?> login() {
        return ResponseEntity.status(200).body("test");
    }
}

