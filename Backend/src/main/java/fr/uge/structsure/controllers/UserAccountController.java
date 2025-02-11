package fr.uge.structsure.controllers;

import fr.uge.structsure.services.AccountService;
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
}
