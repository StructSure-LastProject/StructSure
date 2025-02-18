package fr.uge.structsure.controllers;

import fr.uge.structsure.dto.auth.RegisterRequestDTO;
import fr.uge.structsure.dto.userAccount.UserUpdateRequestDTO;
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
     * @param request The HTTP Request
     * @return UserAccountResponseDTO The list of user details
     */
    @GetMapping("/accounts")
    public ResponseEntity<?> getUserAccounts(HttpServletRequest request){
        try{
            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(accountService.getUserAccounts(request));
        }
        catch (TraitementException e){
            return e.toResponseEntity();
        }
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
     * Update the role information
     * @param userUpdateRequestDTO The new information of user
     * @return RegisterResponseDTO The login of the user account
     */
    @PutMapping("/accounts/reset")
    public ResponseEntity<?> updateRole(@RequestBody UserUpdateRequestDTO userUpdateRequestDTO, HttpServletRequest request) {
        Objects.requireNonNull(userUpdateRequestDTO);
        try {
            return ResponseEntity.status(200).body(accountService.updateUserAccount(userUpdateRequestDTO, request));
        } catch (TraitementException e) {
            return e.toResponseEntity();
        }
    }

}
