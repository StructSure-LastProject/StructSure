package fr.uge.structsure.services;

import fr.uge.structsure.config.JwtUtils;
import fr.uge.structsure.dto.auth.LoginRequestDTO;
import fr.uge.structsure.dto.auth.LoginResponseDTO;
import fr.uge.structsure.dto.auth.RegisterRequestDTO;
import fr.uge.structsure.dto.auth.RegisterResponseDTO;
import fr.uge.structsure.dto.userAccount.*;
import fr.uge.structsure.dto.userAccount.accountStructure.GetStructureListForUserAccountsResponseDTO;
import fr.uge.structsure.dto.userAccount.accountStructure.StructureAccessDetails;
import fr.uge.structsure.dto.userAccount.accountStructure.UpdateUserStructureAccessResponseDTO;
import fr.uge.structsure.dto.userAccount.accountStructure.StructurePermission;
import fr.uge.structsure.entities.Account;
import fr.uge.structsure.entities.Role;
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
    private static final String SUPER_ADMIN_LOGIN = "StructSureAdmin";
    private final AccountRepository accountRepository;
    private final AuthenticationManager authenticationManager;
    private final AccountStructureService accountStructureService;
    private final StructureRepository structureRepository;
    private final JwtUtils jwtUtils;
    private final AuthValidationService authValidationService;

    /**
     * Constructor
     * @param accountRepository Account repository to perform operations with the database
     * @param accountStructureService The account structure service
     * @param structureRepository The structure repository
     * @param authenticationManager Authentication manager for the authentication
     * @param jwtUtils Jwt utils to perform operations with JWT token
     * @param authValidationService The auth validation service
     */
    @Autowired
    public AccountService(AccountRepository accountRepository, AccountStructureService accountStructureService, StructureRepository structureRepository, AuthenticationManager authenticationManager,
                          JwtUtils jwtUtils, AuthValidationService authValidationService) {
        this.accountRepository = Objects.requireNonNull(accountRepository);
        this.accountStructureService = accountStructureService;
        this.structureRepository = structureRepository;
        this.authenticationManager = Objects.requireNonNull(authenticationManager);
        this.jwtUtils = Objects.requireNonNull(jwtUtils);
        this.authValidationService = authValidationService;
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
                        accountDetails.getRole().toString());
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
        var userSessionAccount = authValidationService.checkTokenValidityAndUserAccessVerifier(request, accountRepository);
        if (userSessionAccount.getRole() != Role.ADMIN && !userSessionAccount.getLogin().equals(SUPER_ADMIN_LOGIN)){
            throw new TraitementException(Error.UNAUTHORIZED_OPERATION);
        }

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
        if (userSessionAccount.getRole() != Role.ADMIN && !userSessionAccount.getLogin().equals(SUPER_ADMIN_LOGIN)){
            throw new TraitementException(Error.UNAUTHORIZED_OPERATION);
        }
        if (!userUpdateRequestDTO.password().isEmpty() && (userUpdateRequestDTO.password().length() < 12 || userUpdateRequestDTO.password().length() > 64)){
            throw new TraitementException(Error.PASSWORD_NOT_VALID);
        }
        if (!validateUpdateAccountRequest(userUpdateRequestDTO)){
            throw new TraitementException(Error.MISSING_USER_ACCOUNT_FIELDS);
        }

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

        if (userSessionAccount.getRole() != Role.ADMIN ||  (!userSessionAccount.getLogin().equals(SUPER_ADMIN_LOGIN) &&
                userSessionAccount.getRole() == Role.ADMIN &&
                authValidationService.convertRoleStringToRole(userUpdateRequestDTO.role()) == Role.ADMIN))
        {
            throw new TraitementException(Error.UNAUTHORIZED_OPERATION);
        }

        var userAccount = checkIfAccountExist(userUpdateRequestDTO.login());

        if (userAccount.getLogin().equals(SUPER_ADMIN_LOGIN) && userAccount.getRole() == Role.ADMIN){
            throw new TraitementException(Error.SUPER_ADMIN_ACCOUNT_CANT_BE_MODIFIED);
        }

        if (!userSessionAccount.getLogin().equals(SUPER_ADMIN_LOGIN) && userAccount.getRole() == Role.ADMIN){
            throw new TraitementException(Error.ADMIN_ACCOUNT_CANT_BE_MODIFIED_BY_AN_ADMIN_ACCOUNT);
        }

        return userAccount;
    }

    /**
     * Processes a structure permission and updates the access lists accordingly.
     * <p>
     * This method checks whether the structure exists in the repository. If it does not exist, the structure ID
     * is added to the list of unchanged access. If the structure exists, the method checks if the user should have access
     * or not, and then either assigns or removes the access based on that.
     * </p>
     *
     * @param structurePermission The structure permission to process.
     * @param login The login of the user whose access is being updated.
     * @param unChangedAccess The list to collect structure IDs that couldn't be found in the repository.
     * @param changedAccess The list to collect structure IDs whose access has been changed.
     * @throws TraitementException If any error occurs during processing, such as issues with access assignment or removal.
     */
    private void processStructurePermission(
            StructurePermission structurePermission,
            String login,
            List<Long> unChangedAccess,
            List<Long> changedAccess
    ) throws TraitementException {
        if (!structureRepository.existsById(structurePermission.structureId())) {
            unChangedAccess.add(structurePermission.structureId());
        } else {
            if (structurePermission.hasAccess()) {
                assignAccessIfNeeded(login, structurePermission.structureId());
            } else {
                removeAccessIfNeeded(login, structurePermission.structureId());
            }
            changedAccess.add(structurePermission.structureId());
        }
    }


    /**
     * Assigns access to the given structure for the user if they do not already have access.
     * <p>
     * This method checks if the account already has access to the specified structure. If not,
     * it assigns the account to the structure.
     * </p>
     *
     * @param login The login of the user to assign access to.
     * @param structureId The ID of the structure to assign access to.
     * @throws TraitementException If any error occurs during the assignment process, such as a database issue.
     */
    private void assignAccessIfNeeded(String login, Long structureId) throws TraitementException {
        if (!accountStructureService.isExist(login, structureId)) {
            accountStructureService.assignAccountToStructure(login, structureId);
        }
    }

    /**
     * Removes access to the given structure for the user if they have access.
     * <p>
     * This method removes the association between the user and the specified structure,
     * effectively revoking the user's access to that structure.
     * </p>
     *
     * @param login The login of the user to remove access from.
     * @param structureId The ID of the structure to remove access from.
     * @throws TraitementException If any error occurs during the removal process, such as a database issue.
     */
    private void removeAccessIfNeeded(String login, Long structureId) throws TraitementException {
        accountStructureService.removeAccountToStructure(login, structureId);
    }

    /**
     * Service that update the user's structure access
     * @param userStructureAccessRequestDTO The DTO that represent the request
     * @return The Response DTO
     * @throws TraitementException thrown custom exception
     */
    public UpdateUserStructureAccessResponseDTO updateUserStructureAccess(String login, UserStructureAccessRequestDTO userStructureAccessRequestDTO) throws TraitementException{
        Objects.requireNonNull(login);
        Objects.requireNonNull(userStructureAccessRequestDTO);
        checkIfAccountExist(login);

        if (userStructureAccessRequestDTO.access().isEmpty()){
            return UpdateUserStructureAccessResponseDTO.success(login, List.of());
        }

        var unChangedAccess = new ArrayList<Long>();
        var changedAccess = new ArrayList<Long>();
        for (var structurePermission : userStructureAccessRequestDTO.access()){
            processStructurePermission(structurePermission, login, unChangedAccess, changedAccess);
        }
        if (!unChangedAccess.isEmpty()){
            return UpdateUserStructureAccessResponseDTO.error("Ouvrage introuvable", Collections.unmodifiableList(changedAccess), Collections.unmodifiableList(unChangedAccess));
        }
        return UpdateUserStructureAccessResponseDTO.success(login, Collections.unmodifiableList(changedAccess));
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
     * @param login The login of the user
     * @return The Response DTO
     * @throws TraitementException thrown custom exception
     */
    public RegisterResponseDTO anonymizeTheUserAccount(String login) throws TraitementException {
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
        return new RegisterResponseDTO(login);
    }

}
