package fr.uge.structsure.controllers;

import fr.uge.structsure.services.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
public class UserAccountController {
    private final AccountService accountService;

    @Autowired
    public UserAccountController(AccountService accountService) {
      this.accountService = accountService;
    }

    @GetMapping("/getUserAccounts")
    public ResponseEntity<?> getUserAccounts(){
        return ResponseEntity
            .status(HttpStatus.OK)
            .body(accountService.getUserAccounts());
    }
}
