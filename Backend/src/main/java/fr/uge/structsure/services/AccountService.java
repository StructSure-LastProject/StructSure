package fr.uge.structsure.services;

import fr.uge.structsure.config.JwtUtils;
import fr.uge.structsure.dto.auth.*;
import fr.uge.structsure.dto.userAccount.*;
import fr.uge.structsure.dto.userAccount.accountStructure.GetStructureListForUserAccountsResponseDTO;
import fr.uge.structsure.dto.userAccount.accountStructure.StructureAccessDetails;
import fr.uge.structsure.dto.userAccount.accountStructure.StructurePermission;
import fr.uge.structsure.dto.userAccount.accountStructure.UpdateUserStructureAccessResponseDTO;
import fr.uge.structsure.entities.Account;
import fr.uge.structsure.entities.Role;
import fr.uge.structsure.entities.Structure;
import fr.uge.structsure.exceptions.Error;
import fr.uge.structsure.exceptions.TraitementException;
import fr.uge.structsure.repositories.AccountRepository;
import fr.uge.structsure.repositories.StructureRepository;
import fr.uge.structsure.utils.AuthenticationType;
import fr.uge.structsure.utils.userAccountRequestValidation.UserAccountRequestValidation;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.function.Consumer;


/**
 * Account service class
 */
@Service
public class AccountService {
    private final AccountRepository accountRepository;
    private final AuthenticationManager authenticationManager;
    private final StructureRepository structureRepository;
    private final JwtUtils jwtUtils;
    private final AuthValidationService authValidationService;
    private final AppLogService appLogs;

    /**
     * Constructor
     * @param accountRepository Account repository to perform operations with the database
     * @param structureRepository The structure repository
     * @param authenticationManager Authentication manager for the authentication
     * @param jwtUtils Jwt utils to perform operations with JWT token
     * @param authValidationService The auth validation service
     */
    @Autowired
    public AccountService(
        AccountRepository accountRepository,
        StructureRepository structureRepository, AuthenticationManager authenticationManager,
        JwtUtils jwtUtils, AuthValidationService authValidationService, AppLogService appLogs
    ) {
        this.accountRepository = Objects.requireNonNull(accountRepository);
        this.structureRepository = structureRepository;
        this.authenticationManager = Objects.requireNonNull(authenticationManager);
        this.jwtUtils = Objects.requireNonNull(jwtUtils);
        this.authValidationService = authValidationService;
        this.appLogs = appLogs;
    }

    /**
     * Service that will do the register of new client
     * @param request the complete request to get the register author
     * @param registerRequestDTO The request dto
     * @return RegisterResponseDTO the response dto
     * @throws TraitementException Thrown if no user is found or the role does not exist
     */
    public RegisterResponseDTO register(HttpServletRequest request, RegisterRequestDTO registerRequestDTO) throws TraitementException {
        Objects.requireNonNull(registerRequestDTO);
        if (checkIfNullInCreateNewUserAccountRequest(registerRequestDTO)){
            throw new TraitementException(Error.MISSING_USER_ACCOUNT_FIELDS);
        }
        if(validateCreateNewUserAccountRequest(registerRequestDTO)){
            throw new TraitementException(Error.INVALID_USER_ACCOUNT_FIELDS);
        }
        if (accountRepository.findByLogin(registerRequestDTO.login()).isPresent()) {
            throw new TraitementException(Error.USER_ALREADY_EXISTS);
        }
        Role role = authValidationService.convertRoleStringToRole(registerRequestDTO.role());
        var account = new Account(registerRequestDTO.login(), new BCryptPasswordEncoder().encode(registerRequestDTO.password()),
                registerRequestDTO.firstname(), registerRequestDTO.lastname(),
                role, true);
        accountRepository.save(account);
        appLogs.addAccount(request, account);
        return new RegisterResponseDTO(account.getLogin());
    }

    /**
     * Service that will do the login of a user
     * @param loginRequestDTO the request dto
     * @return LoginResponseDTO the response dto
     * @throws TraitementException thrown if login or password not correct
     */
    public LoginResponseDTO login(LoginRequestDTO loginRequestDTO, AuthenticationType authenticationType) throws TraitementException {
        try {
            var authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginRequestDTO.login(),
                            loginRequestDTO.password())
            );
            if (authentication.isAuthenticated()) {
                var accountDetails = accountRepository.findByLogin(loginRequestDTO.login()).orElseThrow(() -> new TraitementException(Error.AUTHENTICATION_ERROR));
                var createToken = (authenticationType.equals(AuthenticationType.WEB))? jwtUtils.generateToken(loginRequestDTO.login()):
                        jwtUtils.generateAndroidToken(loginRequestDTO.login());
                return new LoginResponseDTO( createToken
                        , "Bearer",
                        accountDetails.getLogin(), accountDetails.getFirstname(), accountDetails.getLastname(),
                        accountDetails.getRole().toString(), accountDetails.getId());
            }
            throw new TraitementException(Error.LOGIN_PASSWORD_NOT_CORRECT);
        } catch (AuthenticationException e) {
            throw new TraitementException(Error.LOGIN_PASSWORD_NOT_CORRECT);
        }
    }

    /**
     * Service that will get all users
     * @param request The HTTP request
     * @return List Send the list of users
     */
    public List<UserAccountResponseDTO> getUserAccounts(HttpServletRequest request) throws TraitementException {
        Objects.requireNonNull(request);
        return accountRepository
            .findAll()
            .stream()
            .filter(account -> account.getPasswordEncrypted() != null)
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
     * Validate common fields
     * @param firstname Firstname
     * @param lastname Lastname
     * @param login login
     * @param role role
     * @return true for valid or false for invalid
     */
    private boolean validateCommonFields(String firstname, String lastname, String login, String role){
        return (firstname != null && UserAccountRequestValidation.containsNonLetters(firstname)) &&
                (lastname != null && UserAccountRequestValidation.containsNonLetters(lastname)) &&
                (login != null && UserAccountRequestValidation.loginValidator(login)) &&
                role != null;
    }


    /**
     * Validate the update account request
     * @param userUpdateRequest The update request DTO
     * @return true for valid or false for invalid
     */
    private boolean validateUpdateAccountRequest(UserUpdateRequestDTO userUpdateRequest) {
        Objects.requireNonNull(userUpdateRequest);
        return validateCommonFields(userUpdateRequest.firstname(),
                userUpdateRequest.lastname(),
                userUpdateRequest.login(),
                userUpdateRequest.role());
    }

    /**
     * Validate the request fields
     * @param registerRequestDTO The request DTO
     * @return true for valid or false for invalid
     */
    private boolean validateCreateNewUserAccountRequest(RegisterRequestDTO registerRequestDTO){
        Objects.requireNonNull(registerRequestDTO);
        var isCommonValid = validateCommonFields(registerRequestDTO.firstname(),
                registerRequestDTO.lastname(),
                registerRequestDTO.login(),
                registerRequestDTO.role());
        return !isCommonValid || registerRequestDTO.password() == null ||
                registerRequestDTO.password().length() < 12 ||
                registerRequestDTO.password().length() > 64;
    }


    /**
     * Update the user information firstname, lastname, login if different
     * @param newValue New value
     * @param currentValue Current value
     * @param setter The set method
     */
    private void updateIfDifferent(String newValue, String currentValue, Consumer<String> setter){
        Objects.requireNonNull(newValue);
        Objects.requireNonNull(currentValue);
        Objects.requireNonNull(setter);
        if (!newValue.equals(currentValue)) {
            setter.accept(newValue);
        }
    }




    /**
     * Service that will update the user information
     * @param userUpdateRequestDTO The new information of the user
     * @return RegisterResponseDTO The login of the user
     * @throws TraitementException thrown if login or role not exist and also thrown if super-admin role was requested to change
     */
    public RegisterResponseDTO updateUserAccount(UserUpdateRequestDTO userUpdateRequestDTO, HttpServletRequest request) throws TraitementException {
        Objects.requireNonNull(userUpdateRequestDTO);
        Objects.requireNonNull(request);

        var userSessionAccount = authValidationService.checkTokenValidityAndUserAccessVerifier(request, accountRepository);
        var userAccount = userAccountOperationChecker(userUpdateRequestDTO, userSessionAccount);
        if (!userUpdateRequestDTO.password().isEmpty() && (userUpdateRequestDTO.password().length() < 12 || userUpdateRequestDTO.password().length() > 64)){
            throw new TraitementException(Error.PASSWORD_NOT_VALID);
        }
        if (!validateUpdateAccountRequest(userUpdateRequestDTO)){
            throw new TraitementException(Error.MISSING_USER_ACCOUNT_FIELDS);
        }

        appLogs.editAccount(request, userAccount, userUpdateRequestDTO);
        updateIfDifferent(userUpdateRequestDTO.firstname(), userAccount.getFirstname(), userAccount::setFirstname);
        updateIfDifferent(userUpdateRequestDTO.lastname(), userAccount.getLastname(), userAccount::setLastname);

        if (!userUpdateRequestDTO.password().isEmpty()){
            var passwordHash = new BCryptPasswordEncoder().encode(userUpdateRequestDTO.password());
            if (!userAccount.getPasswordEncrypted().equals(passwordHash)){
                userAccount.setPasswordEncrypted(passwordHash);
            }
        }

        Role role = authValidationService.convertRoleStringToRole(userUpdateRequestDTO.role());
        if (userAccount.getRole() != role){
            userAccount.setRole(role);
        }
        if (userAccount.getEnabled() != userUpdateRequestDTO.accountState()){
            userAccount.setEnabled(userUpdateRequestDTO.accountState());
        }
        accountRepository.save(userAccount);
        return new RegisterResponseDTO(userAccount.getLogin());
    }

    /**
     * Method to check if the given login is valid
     * @param login The login
     * @return The Account object
     * @throws TraitementException thrown custom exception if the login is not related to a valid user account
     */
    private Account checkIfAccountExist(String login) throws TraitementException {
        return accountRepository.findByLogin(Objects.requireNonNull(login)).orElseThrow(() -> new TraitementException(Error.USER_ACCOUNT_NOT_FOUND));
    }

    /**
     * Check if is possible to change role of the given user
     * @param userUpdateRequestDTO The user update request with new user information
     * @param userSessionAccount User that requested the operation
     * @return Optional<Account> Return the account if exist
     * @throws TraitementException thrown custom exceptions
     */
    private Account userAccountOperationChecker(UserUpdateRequestDTO userUpdateRequestDTO, Account userSessionAccount) throws TraitementException {
        Objects.requireNonNull(userUpdateRequestDTO);
        Objects.requireNonNull(userSessionAccount);

        var userAccount = checkIfAccountExist(userUpdateRequestDTO.login());

        if (userAccount.isSuperAdmin()) {
            throw new TraitementException(Error.SUPER_ADMIN_ACCOUNT_CANT_BE_MODIFIED);
        }

        if (!userSessionAccount.isSuperAdmin() && userAccount.getRole() == Role.ADMIN){
            throw new TraitementException(Error.ADMIN_ACCOUNT_CANT_BE_MODIFIED_BY_AN_ADMIN_ACCOUNT);
        }

        return userAccount;
    }



    /**
     * Service that update the user's structure access
     * @param login The user login
     * @param userStructureAccessRequestDTO The DTO that represent the request
     * @return The Response DTO
     * @throws TraitementException thrown custom exception
     */
    public UpdateUserStructureAccessResponseDTO updateUserStructureAccess(
        String login,
        UserStructureAccessRequestDTO userStructureAccessRequestDTO
    ) throws TraitementException{
        Objects.requireNonNull(login);
        Objects.requireNonNull(userStructureAccessRequestDTO);
        var userAccount = checkIfAccountExist(login);

        if (userStructureAccessRequestDTO.access().isEmpty()){
            return UpdateUserStructureAccessResponseDTO.success(login, List.of());
        }

        var allowedSet = userAccount.getAllowedStructures();
        var unChangedAccess = new ArrayList<Long>();
        var changedAccess = new ArrayList<Long>();

        for (var structurePermission : userStructureAccessRequestDTO.access()){
            updateProcessStructureAccess(structurePermission, allowedSet, unChangedAccess, userAccount, changedAccess);

        }
        accountRepository.save(userAccount);
        if (!unChangedAccess.isEmpty()){
            return UpdateUserStructureAccessResponseDTO.error("Ouvrage introuvable", Collections.unmodifiableList(changedAccess), Collections.unmodifiableList(unChangedAccess));
        }
        return UpdateUserStructureAccessResponseDTO.success(login, Collections.unmodifiableList(changedAccess));
    }

    /**
     * Update process handler method
     * @param structurePermission The structure permission
     * @param allowedSet The allowed structure set
     * @param unChangedAccess The unchanged structure access list of unkown structure
     * @param userAccount The user account
     * @param changedAccess The changed access list for the given user
     */
    private void updateProcessStructureAccess(StructurePermission structurePermission, Set<Structure> allowedSet, ArrayList<Long> unChangedAccess, Account userAccount, ArrayList<Long> changedAccess) {
        var removedStructure = allowedSet.stream().filter(structure -> structure.getId() == structurePermission.structureId()).findFirst();
        Structure structure;
        if (removedStructure.isPresent()){
            structure = removedStructure.get();
        } else {
            var structureOp = structureRepository.findById(structurePermission.structureId());
            if (structureOp.isEmpty()) {
                unChangedAccess.add(structurePermission.structureId());
                return;
            }
            structure = structureOp.get();
        }
        if (structurePermission.hasAccess()){
            structure.add(userAccount);
            userAccount.add(structure);
            changedAccess.add(structure.getId());
        }
        else {
            structure.remove(userAccount);
            userAccount.remove(structure);
            changedAccess.add(structure.getId());
        }
    }


    /**
     * Service that will get structure list of the given login (user account)
     * @param login The login
     * @return The response DTO
     * @throws TraitementException Thrown custom exceptions
     */
    public GetStructureListForUserAccountsResponseDTO getStructureListForUserAccounts(String login) throws TraitementException {
        Objects.requireNonNull(login);
        var userAccount = checkIfAccountExist(login);
        var structures = structureRepository.findAll();
        var allowedStructures = userAccount.getAllowedStructures();
        var resultList = new ArrayList<StructureAccessDetails>();
        structures.forEach(structure -> resultList.add(new StructureAccessDetails(
                structure.getId(),
                structure.getName(),
                allowedStructures.contains(structure)
        )));
        return new GetStructureListForUserAccountsResponseDTO(resultList);
    }

    /**
     * Service that update the user's structure access
     * @param request access to the deletion author
     * @param login The login of the user
     * @return The Response DTO
     * @throws TraitementException thrown custom exception
     */
    public RegisterResponseDTO anonymizeTheUserAccount(HttpServletRequest request,
               String login) throws TraitementException {
        Objects.requireNonNull(login);
        var userAccount = accountRepository.findByLogin(login).orElseThrow(() -> new TraitementException(Error.USER_ACCOUNT_NOT_FOUND));
        var random = new Random();
        int leftLimit = 48; // numeral '0'
        int rightLimit = 122; // letter 'z'
        int targetStringLength = 16;
        var generatedString = random.ints(leftLimit, rightLimit + 1)
                .filter(i -> (i <= 57 || i >= 65) && (i <= 90 || i >= 97))
                .limit(targetStringLength)
                .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                .toString();
        userAccount.setLogin("user_%s&=)}(|[".formatted(generatedString));
        userAccount.setFirstname("Prénom anonymisé");
        userAccount.setLastname("Nom anonymisé");
        userAccount.setPasswordEncrypted(null);
        accountRepository.save(userAccount);
        appLogs.deleteAccount(request, login, userAccount.getId());
        return new RegisterResponseDTO(login);
    }

    /**
     * Changes the password of a user.
     * This method validates the user's current password, ensures the new password is different,
     * and updates the stored password securely. It also performs validation on the new password.
     *
     * @param changePasswordRequestDTO The DTO containing user ID, current password, and new password.
     * @return A DTO containing the user ID after the password update.
     * @throws TraitementException If the user is not found, the current password is incorrect,
     *                             the new password is the same as the old one,
     *                             or the new password fails validation.
     */
    public ChangePasswordResponseDTO changePassword(ChangePasswordRequestDTO changePasswordRequestDTO) throws TraitementException {
        changePasswordRequestDTO.checkFields();
        var user = accountRepository.findById(changePasswordRequestDTO.userId())
                .orElseThrow(() -> new TraitementException(Error.USER_ACCOUNT_NOT_FOUND));
        var passwordEncoder = new BCryptPasswordEncoder();
        if (!passwordEncoder.matches(changePasswordRequestDTO.currentPassword(), user.getPasswordEncrypted())) {
            throw new TraitementException(Error.OLD_PASSWORD_NOT_CORRECT);
        }
        if (passwordEncoder.matches(changePasswordRequestDTO.newPassword(), user.getPasswordEncrypted())) {
            throw new TraitementException(Error.NEW_PASSWORD_SHOULD_BE_DIFFERENT_THAN_THE_OLD_ONE);
        }
        if (changePasswordRequestDTO.newPassword().length() < 12 || changePasswordRequestDTO.newPassword().length() > 64){
            throw new TraitementException(Error.PASSWORD_NOT_VALID);
        }
        user.setPasswordEncrypted(passwordEncoder.encode(changePasswordRequestDTO.newPassword()));
        accountRepository.save(user);
        return new ChangePasswordResponseDTO(changePasswordRequestDTO.userId());
    }
}
