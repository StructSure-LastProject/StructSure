package fr.uge.structsure.controllers;

import com.fasterxml.jackson.core.JsonParseException;
import fr.uge.structsure.config.RequiresRole;
import fr.uge.structsure.dto.auth.RegisterRequestDTO;
import fr.uge.structsure.dto.logs.LogsRequestDTO;
import fr.uge.structsure.dto.userAccount.UserStructureAccessRequestDTO;
import fr.uge.structsure.dto.userAccount.UserUpdateRequestDTO;
import fr.uge.structsure.entities.Role;
import fr.uge.structsure.exceptions.Error;
import fr.uge.structsure.exceptions.TraitementException;
import fr.uge.structsure.services.AccountService;
import fr.uge.structsure.services.AppLogService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Objects;

/**
 * Controller handles the user details
 */
@RestController
@RequestMapping("/api")
public class UserAccountController {
    private final AccountService accountService;
    private final AppLogService appLogService;
    
    /**
     * Constructor
     * @param accountService Service class
     * @param appLogService Logs service
     */
    @Autowired
    public UserAccountController(AccountService accountService, AppLogService appLogService) {
      this.accountService = accountService;
      this.appLogService = appLogService;
    }

    /**
     * Returns the user list
     * @param request The HTTP Request
     * @return UserAccountResponseDTO The list of user details
     */
    @RequiresRole(Role.ADMIN)
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
     * Create a new user account
     * @param request the full request data to get the current user account
     * @param registerRequestDTO The request DTO
     * @return RegisterResponseDTO The login of the user account
     */
    @RequiresRole(Role.ADMIN)
    @PostMapping("/accounts")
    public ResponseEntity<?> createNewUserAccount(
        HttpServletRequest request,
        @RequestBody RegisterRequestDTO registerRequestDTO
    ){
        Objects.requireNonNull(registerRequestDTO);
        try {
            return ResponseEntity.status(201).body(accountService.register(request, registerRequestDTO));
        } catch (TraitementException e) {
            return e.toResponseEntity("Account creation failed: {}");
        }
    }


    /**
     * Update the role information
     * @param userUpdateRequestDTO The new information of user
     * @return RegisterResponseDTO The login of the user account
     */
    @RequiresRole(Role.ADMIN)
    @PutMapping("/accounts/reset")
    public ResponseEntity<?> updateUserAccount(@RequestBody UserUpdateRequestDTO userUpdateRequestDTO, HttpServletRequest request) {
        Objects.requireNonNull(userUpdateRequestDTO);
        try {
            return ResponseEntity.ok().body(accountService.updateUserAccount(userUpdateRequestDTO, request));
        } catch (TraitementException e) {
            return e.toResponseEntity("Account update failed: {}");
        }
    }

    /**
     * Handles exceptions when JSON parsing fails.
     * <p>
     * This method is invoked when a {@link JsonParseException} is thrown, typically when the
     * incoming JSON is malformed or contains an unexpected character. The exception message is
     * parsed to remove extra details such as the source location, and the core error message
     * is returned as part of the response.
     * </p>
     *
     * @param ex The {@link JsonParseException} thrown during JSON parsing.
     * @return A {@link ResponseEntity} containing the error message in a JSON format and
     *         a {@link HttpStatus#BAD_REQUEST} status.
     */
    @ExceptionHandler(JsonParseException.class)
    public ResponseEntity<?> handleJsonParseException(JsonParseException ex){
        var fullMessage = ex.getMessage();
        return new ResponseEntity<>(Map.of("error", fullMessage.split(" at ")[0].trim()), HttpStatus.BAD_REQUEST);
    }

    /**
     * Handles exceptions when the HTTP message (e.g., JSON, XML) is not readable.
     * <p>
     * This method is invoked when a {@link HttpMessageNotReadableException} is thrown,
     * which occurs when the incoming HTTP message cannot be deserialized (e.g., malformed JSON).
     * A generic error message is returned to inform the client that the input was invalid.
     * </p>
     *
     * @return A {@link ResponseEntity} containing a fixed error message in a JSON format and
     *         a {@link HttpStatus#BAD_REQUEST} status.
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<?> handleHttpMessageNotReadableException(){
        return new TraitementException(Error.INVALID_FIELDS).toResponseEntity();
    }

    /**
     * Handles {@link NullPointerException} exceptions.
     * <p>
     * This method is invoked when a {@link NullPointerException} is thrown, which usually occurs
     * when an object is accessed while it is null. A generic error message is returned to inform
     * the client that some fields are invalid or not provided.
     * </p>
     *
     * @return A {@link ResponseEntity} containing a fixed error message in a JSON format and
     *         a {@link HttpStatus#BAD_REQUEST} status.
     */
    @ExceptionHandler(NullPointerException.class)
    public ResponseEntity<?> handleNullPointerException(){
        return new TraitementException(Error.INVALID_FIELDS).toResponseEntity();
    }

    /**
     * Update the user's structure access
     * @param userStructureAccessRequestDTO The DTO that represent the update request
     * @return The response DTO
     */
    @RequiresRole(Role.ADMIN)
    @PostMapping("/accounts/{login}/access")
    public ResponseEntity<?> updateUserStructureAccess(
        @PathVariable String login,
        @RequestBody UserStructureAccessRequestDTO userStructureAccessRequestDTO
    ) {
        Objects.requireNonNull(userStructureAccessRequestDTO);
        try {
            return ResponseEntity.ok().body(accountService.updateUserStructureAccess(
                login, userStructureAccessRequestDTO));
        } catch (TraitementException e){
            return e.toResponseEntity("Account authorizations update failed: {}");
        }
    }

    /**
     * Get allowed structure list for the given user account
     * @param login The login of the user
     * @return GetStructureListForUserAccountsResponseDTO The response DTO
     */
    @RequiresRole(Role.ADMIN)
    @GetMapping("/accounts/{login}/structures")
    public ResponseEntity<?> getStructureListForUserAccounts(@PathVariable String login) {
        try {
            return ResponseEntity.ok().body(accountService.getStructureListForUserAccounts(login));
        } catch (TraitementException e){
            return e.toResponseEntity();
        }
    }


    /** 
     * Anonymize the user account
     * @param request the full request data to get current user account
     * @param login The login of the user
     * @return The response DTO
     */
    @RequiresRole(Role.ADMIN)
    @PutMapping("/api/accounts/{login}/anonymize")
    public ResponseEntity<?> anonymizeTheUserAccount(
        HttpServletRequest request,
        @PathVariable String login
    ) {
        try {
            return ResponseEntity.ok().body(accountService.anonymizeTheUserAccount(request, login));
        } catch (TraitementException e){
            return e.toResponseEntity("Account anonymization failed: {}");
        }
    }

    /**
     * Loads the logs matching the search string and the requested
     * pagination.
     * @param logsDto the parameters of the research
     * @return the logs matching the query
     */
    @RequiresRole(Role.ADMIN)
    @PostMapping("/logs")
    public ResponseEntity<?> getLogs(@RequestBody LogsRequestDTO logsDto) {
        try {
            return ResponseEntity.ok(appLogService.loadLogs(logsDto));
        } catch (TraitementException e) {
            return e.toResponseEntity();
        }
    }
}
