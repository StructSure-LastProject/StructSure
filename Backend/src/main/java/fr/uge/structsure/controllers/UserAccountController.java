package fr.uge.structsure.controllers;

import fr.uge.structsure.dto.auth.RegisterRequestDTO;
import fr.uge.structsure.dto.userAccount.RoleRequest;
import fr.uge.structsure.exceptions.TraitementException;
import fr.uge.structsure.services.AccountService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Objects;

/**
 * Controller handles the user details
 */
@RestController
@RequestMapping("/api")
public class UserAccountController {
    private final AccountService accountService;
    
    /**
     * Constructor
     * @param accountService Service class
     */
    @Autowired
    public UserAccountController(AccountService accountService) {
      this.accountService = accountService;
    }

    /**
     * Returns the user list
     * @return UserAccountResponseDTO The list of user details
     */
    @GetMapping("/accounts")
    public ResponseEntity<?> getUserAccounts(){
        return ResponseEntity
            .status(HttpStatus.OK)
            .body(accountService.getUserAccounts());
    }

    /**
     * Create new user account
     * @param registerRequestDTO The request DTO
     * @return RegisterResponseDTO The login of the user account
     */
    @PostMapping("/accounts")
    public ResponseEntity<?> createNewUserAccount(@RequestBody RegisterRequestDTO registerRequestDTO){
        Objects.requireNonNull(registerRequestDTO);
        try {
            return ResponseEntity.status(201).body(accountService.register(registerRequestDTO));
        } catch (TraitementException e) {
            return e.toResponseEntity();
        }
    }


    /**
     * Update the role for a user
     * @param login User login
     * @param roleRequest The new role
     * @return RegisterResponseDTO The login of the user account
     */
    @PutMapping("/accounts/{login}/role")
    public ResponseEntity<?> updateRole(@PathVariable("login") String login, @RequestBody RoleRequest roleRequest, HttpServletRequest request) {
        Objects.requireNonNull(login);
        Objects.requireNonNull(roleRequest);
        try {
            return ResponseEntity.status(200).body(accountService.updateRole(login, roleRequest, request));
        } catch (TraitementException e) {
            return e.toResponseEntity();
        }
    }

}
