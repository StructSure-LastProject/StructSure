package fr.uge.structsure.services;

import fr.uge.structsure.dto.userAccount.UserUpdateRequestDTO;
import fr.uge.structsure.entities.Account;
import fr.uge.structsure.entities.AppLog;
import fr.uge.structsure.exceptions.TraitementException;
import fr.uge.structsure.repositories.AccountRepository;
import fr.uge.structsure.repositories.AppLogRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * Account service class
 */
@Service
public class AppLogService {
    private final AppLogRepository appLogRepository;
    private final AccountRepository accountRepository;
    private final AuthValidationService authValidation;

    /**
     * Constructor
     * @param appLogRepository Database access to the logs table
     * @param accountRepository Database access to the accounts table
     * @param authValidation Service to validate authentication
     */
    @Autowired
    public AppLogService(
        AppLogRepository appLogRepository, AccountRepository accountRepository,
        AuthValidationService authValidation
    ) {
        this.appLogRepository = Objects.requireNonNull(appLogRepository);
        this.accountRepository = Objects.requireNonNull(accountRepository);
        this.authValidation = Objects.requireNonNull(authValidation);
    }

    /**
     * Creates and saves a new log entry with the given author and
     * message.
     * @param author the user that initiated the action
     * @param message the description of the action
     */
    private void save(Account author, String message) {
        appLogRepository.save(new AppLog(author, message));
    }

    /**
     * Extracts the current user's account from the http request.
     * @param request the request to get the user from
     * @return the current user if existing, null otherwise
     */
    private Account currentAccount(HttpServletRequest request) {
        try {
            return authValidation.checkTokenValidityAndUserAccessVerifier(
                request, accountRepository);
        } catch (TraitementException e) {
            return null;
        }
    }

    /**
     * Adds a log entry for a new account creation.
     * @param request to extract the author of this action
     * @param created the account freshly created
     */
    public void addAccount(HttpServletRequest request, Account created) {
        var author = currentAccount(request);
        save(author, String.format(
            "Compte créé: %s - %s (#%d)",
            created.getLogin(), created.getRole(), created.getId()
        ));
    }

    /**
     * Adds a log entry for an existing account edition.
     * @param request to extract the author of this action
     * @param account the account that will be edited
     * @param edits the new values to set
     */
    public void editAccount(HttpServletRequest request, Account account, UserUpdateRequestDTO edits) {
        var author = currentAccount(request);
        save(author, String.format(
            "Compte édité: %s%s", account.getLogin(), edits.logDiff(account)
        ));
    }

    /**
     * Adds a log entry for an existing account deletion.
     * @param request to extract the author of this action
     * @param login the login of the account that will be deleted
     * @param id the id of the deleted account
     */
    public void deleteAccount(HttpServletRequest request, String login, long id) {
        var author = currentAccount(request);
        save(author, String.format(
            "Compte supprimé: %s (#%d)", login, id
        ));
    }
}
