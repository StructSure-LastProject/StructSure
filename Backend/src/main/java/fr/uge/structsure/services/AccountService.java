package fr.uge.structsure.services;

import fr.uge.structsure.config.JwtUtils;
import fr.uge.structsure.dto.auth.LoginRequestDTO;
import fr.uge.structsure.dto.auth.LoginResponseDTO;
import fr.uge.structsure.dto.auth.RegisterRequestDTO;
import fr.uge.structsure.dto.auth.RegisterResponseDTO;
import fr.uge.structsure.dto.userAccount.RoleRequest;
import fr.uge.structsure.dto.userAccount.UserAccountResponseDTO;
import fr.uge.structsure.entities.Account;
import fr.uge.structsure.entities.Role;
import fr.uge.structsure.exceptions.Error;
import fr.uge.structsure.exceptions.TraitementException;
import fr.uge.structsure.repositories.AccountRepository;
import fr.uge.structsure.utils.userAccountRequestValidation.UserAccountRequestValidation;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.io.DecodingException;
import io.jsonwebtoken.security.SignatureException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * Account service class
 */
@Service
public class AccountService {
    private static final String SUPER_ADMIN_LOGIN = "StructSureAdmin";
    private final AccountRepository accountRepository;
    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtils;

    /**
     * Constructor
     * @param accountRepository Account repository to perform operations with the database
     * @param authenticationManager Authentication manager for the authentication
     * @param jwtUtils Jwt utils to perform operations with JWT token
     */
    @Autowired
    public AccountService(AccountRepository accountRepository, AuthenticationManager authenticationManager,
                          JwtUtils jwtUtils) {
        this.accountRepository = Objects.requireNonNull(accountRepository);
        this.authenticationManager = Objects.requireNonNull(authenticationManager);
        this.jwtUtils = Objects.requireNonNull(jwtUtils);
    }

    /**
     * Service that will do the register of new client
     * @param registerRequestDTO The request dto
     * @return RegisterResponseDTO the response dto
     * @throws TraitementException Thrown if no user is found or the role does not exist
     */
    public RegisterResponseDTO register(RegisterRequestDTO registerRequestDTO) throws TraitementException {
        Objects.requireNonNull(registerRequestDTO);
        if (checkIfNullInCreateNewUserAccountRequest(registerRequestDTO)){
            throw new TraitementException(Error.MISSING_USER_ACCOUNT_FIELDS);
        }
        if(!validateCreateNewUserAccountRequest(registerRequestDTO)){
            throw new TraitementException(Error.INVALID_USER_ACCOUNT_FIELDS);
        }

        if (accountRepository.findByLogin(registerRequestDTO.login()).isPresent()) {
            throw new TraitementException(Error.USER_ALREADY_EXISTS);
        }
        Role role;
        try {
            role = Role.fromValue(registerRequestDTO.role());
        } catch (IllegalArgumentException e) {
            throw new TraitementException(Error.ROLE_NOT_EXISTS);
        }
        var account = new Account(registerRequestDTO.login(), new BCryptPasswordEncoder().encode(registerRequestDTO.password()),
                registerRequestDTO.firstname(), registerRequestDTO.lastname(),
                role, true);
        accountRepository.save(account);
        return new RegisterResponseDTO(account.getLogin());
    }

    /**
     * Service that will do the login of a user
     * @param loginRequestDTO the request dto
     * @return LoginResponseDTO the response dto
     * @throws TraitementException thrown if login or password not correct
     */
    public LoginResponseDTO login(LoginRequestDTO loginRequestDTO) throws TraitementException {
        try {
            var authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginRequestDTO.login(),
                            loginRequestDTO.password())
            );
            if (authentication.isAuthenticated()) {
                var account = accountRepository.findByLogin(loginRequestDTO.login());
                var accountDetails = account.orElseThrow(() -> new IllegalStateException("Account authenticated but not present"));
                return new LoginResponseDTO(jwtUtils.generateToken(loginRequestDTO.login()), "Bearer",
                        accountDetails.getLogin(), accountDetails.getFirstname(), accountDetails.getLastname(),
                        accountDetails.getRole().toString());
            }
            throw new TraitementException(Error.LOGIN_PASSWORD_NOT_CORRECT);
        } catch (AuthenticationException e) {
            throw new TraitementException(Error.LOGIN_PASSWORD_NOT_CORRECT);
        }
    }

    /**
     * Service that will get all users
     * @return List Send the list of users
     */
    public List<UserAccountResponseDTO> getUserAccounts(){
        return accountRepository
            .findAll()
            .stream()
            .map(account ->
                new UserAccountResponseDTO(
                    account.getFirstname(),
                    account.getLastname(),
                    account.getLogin(),
                    account.getRole().value,
                    account.getEnabled()
                )
            )
            .toList();
    }

    /**
     * Check if the request contains some null fields
     * @param registerRequestDTO The request DTO
     * @return true if a null found or false
     */
    private boolean checkIfNullInCreateNewUserAccountRequest(RegisterRequestDTO registerRequestDTO){
        Objects.requireNonNull(registerRequestDTO);
        return registerRequestDTO.firstname() == null
                && registerRequestDTO.lastname() == null
                && registerRequestDTO.login() == null
                && registerRequestDTO.role() == null
                && registerRequestDTO.password() == null;
    }

    /**
     * Validate the request fields
     * @param registerRequestDTO The request DTO
     * @return true for valid or false for invalid
     */
    private boolean validateCreateNewUserAccountRequest(RegisterRequestDTO registerRequestDTO){
        Objects.requireNonNull(registerRequestDTO);
        return UserAccountRequestValidation.containsNonLetters(registerRequestDTO.firstname())
                || UserAccountRequestValidation.containsNonLetters(registerRequestDTO.lastname())
                || UserAccountRequestValidation.loginValidator(registerRequestDTO.login())
                || Arrays.stream(Role.values()).anyMatch(role -> role.value.equals(registerRequestDTO.role())
                && (registerRequestDTO.password().length() >= 12 && registerRequestDTO.password().length() <= 64));
    }

    /**
     * Service that will update the user role
     * @param login Login of the user
     * @param roleRequest The new role of the user
     * @return RegisterResponseDTO The login of the user
     * @throws TraitementException thrown if login or role not exist and also thrown if super-admin role was requested to change
     */
    public RegisterResponseDTO updateRole(String login, RoleRequest roleRequest, HttpServletRequest request) throws TraitementException {
        Objects.requireNonNull(roleRequest);
        var userSessionAccount = checkTokenValidity(request);
        var userAccount = userAccountOperationChecker(login, userSessionAccount);
        Role role;
        try {
            role = Role.fromValue(roleRequest.role());
        } catch (IllegalArgumentException e) {
            throw new TraitementException(Error.ROLE_NOT_EXISTS);
        }

        if (!userSessionAccount.getLogin().equals(SUPER_ADMIN_LOGIN) &&
                userSessionAccount.getRole() == Role.ADMIN &&
                userAccount.getRole() != Role.ADMIN &&
                Role.ADMIN == role
        ){
            throw new TraitementException(Error.UNAUTHORIZED_OPERATION);
        }
        userAccount.setRole(role);
        accountRepository.save(userAccount);
        return new RegisterResponseDTO(userAccount.getLogin());
    }

    /**
     * Check if is possible to change role of the given user
     * @param login The login
     * @param userSessionAccount User that requested the operation
     * @return Optional<Account> Return the account if exist
     * @throws TraitementException thrown custom exceptions
     */
    private Account userAccountOperationChecker(String login, Account userSessionAccount) throws TraitementException {
        Objects.requireNonNull(login);
        Objects.requireNonNull(userSessionAccount);
        var userAccount = accountRepository.findByLogin(login);
        if (userAccount.isEmpty()){
            throw new TraitementException(Error.USER_ACCOUNT_NOT_FOUND);
        }
        if (userAccount.get().getLogin().equals(SUPER_ADMIN_LOGIN) && userAccount.get().getRole() == Role.ADMIN){
            throw new TraitementException(Error.SUPER_ADMIN_ACCOUNT_CANT_BE_MODIFIED);
        }

        if (!userSessionAccount.getLogin().equals(SUPER_ADMIN_LOGIN) && userAccount.get().getRole() == Role.ADMIN){
            throw new TraitementException(Error.ADMIN_ACCOUNT_CANT_BE_MODIFIED_BY_AN_ADMIN_ACCOUNT);
        }
        return userAccount.get();
    }

    /**
     * Check the validity of the JWT TOKEN
     * @param request The HTTP Request
     * @return Optional<Account> Return the account if exist
     * @throws TraitementException thrown JWT token extract exception into custom exception
     */
    private Account checkTokenValidity(HttpServletRequest request) throws TraitementException {
        Objects.requireNonNull(request);
        Optional<Account> account;
        try {
            var token = request.getHeader("authorization");
            if (token.startsWith("Bearer ")) token = token.substring(7);
            account = accountRepository.findByLogin(jwtUtils.extractUsername(token));
        } catch (UnsupportedJwtException | MalformedJwtException | DecodingException | SignatureException | ExpiredJwtException | IllegalArgumentException | NullPointerException e){
            throw new TraitementException(Error.INVALID_TOKEN);
        }

        if (account.isEmpty()){
            throw new TraitementException(Error.INVALID_TOKEN);
        }
        return account.get();
    }
}
