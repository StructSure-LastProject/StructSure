package fr.uge.structsure.services;

import fr.uge.structsure.dto.scan.AndroidSensorEditDTO;
import fr.uge.structsure.dto.structure.AddStructureRequestDTO;
import fr.uge.structsure.dto.userAccount.UserUpdateRequestDTO;
import fr.uge.structsure.entities.*;
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

    // TODO flush too old entries

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
        save(author, String.format("Compte créé: %s - %s (#%d)",
            created.getLogin(), created.getRole(), created.getId()
        ));
    }

    /**
     * Adds a log entry for an existing account edition.
     * @param request to extract the author of this action
     * @param account the account that will be edited
     * @param edits the new values to log
     */
    public void editAccount(HttpServletRequest request, Account account, UserUpdateRequestDTO edits) {
        var diff = edits.logDiff(account);
        if (diff.isEmpty()) return;
        var author = currentAccount(request);
        save(author, String.format("Compte édité: %s (#%d)%s",
            account.getLogin(), account.getId(), diff
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
        save(author, String.format("Compte supprimé: %s (#%d)", login, id));
    }

    /**
     * Adds a log entry for a new structure creation.
     * @param request to extract the author of this action
     * @param structure the freshly created structure
     */
    public void addStructure(HttpServletRequest request, Structure structure) {
        var author = currentAccount(request);
        save(author, String.format("Ouvrage créé: %s (#%d)",
            structure.getName(), structure.getId()
        ));
    }

    /**
     * Adds a log entry for an existing structure edition.
     * @param request to extract the author of this action
     * @param structure the structure to be edited
     * @param edits the new values to log
     */
    public void editStructure(HttpServletRequest request, Structure structure, AddStructureRequestDTO edits) {
        var diff = edits.logDiff(structure);
        if (diff.isEmpty()) return;
        var author = currentAccount(request);
        save(author, String.format("Ouvrage édité: %s (#%d)%s",
            structure.getName(), structure.getId(), diff
        ));
    }

    /**
     * Adds a log entry for an existing structure archivage.
     * @param request to extract the author of this action
     * @param structure the structure to be archived
     */
    public void archiveStructure(HttpServletRequest request, Structure structure) {
        var author = currentAccount(request);
        save(author, String.format("Ouvrage archivé: %s (#%d)",
            structure.getName(), structure.getId()
        ));
    }

    /**
     * Adds a log entry for an existing structure restoration.
     * @param request to extract the author of this action
     * @param structure the structure to be restored
     */
    public void restoreStructure(HttpServletRequest request, Structure structure) {
        var author = currentAccount(request);
        save(author, String.format("Ouvrage restauré: %s (#%d)",
            structure.getName(), structure.getId()
        ));
    }

    /**
     * Adds a log entry for a new plan creation.
     * @param request to extract the author of this action
     * @param plan the freshly created plan
     */
    public void addPlan(HttpServletRequest request, Plan plan) {
        var author = currentAccount(request);
        save(author, String.format("Plan créé: %s", plan));
    }

    /**
     * Adds a log entry for an existing plan edition.
     * @param request to extract the author of this action
     * @param plan the initial data of the plan
     * @param diff the updated fields
     */
    public void editPlan(HttpServletRequest request, Plan plan, String diff) {
        if (diff.isEmpty()) return;
        var author = currentAccount(request);
        save(author, String.format("Plan édité: %s%s", plan, diff));
    }

    /**
     * Adds a log entry for an existing plan archivage.
     * @param request to extract the author of this action
     * @param plan the plan that got archived
     */
    public void archivePlan(HttpServletRequest request, Plan plan) {
        // TODO connect to the service method once implemented
        var author = currentAccount(request);
        save(author, String.format("Plan archivé: %s", plan));
    }

    /**
     * Adds a log entry for an existing plan restoration.
     * @param request to extract the author of this action
     * @param plan the plan that got restored
     */
    public void restorePlan(HttpServletRequest request, Plan plan) {
        // TODO connect to the service method once implemented
        var author = currentAccount(request);
        save(author, String.format("Plan restoré: %s", plan));
    }

    /**
     * Adds a log entry for a new sensor creation.
     * @param request to extract the author of this action
     * @param sensor the freshly created sensor
     */
    public void addSensor(HttpServletRequest request, Sensor sensor) {
        var author = currentAccount(request);
        save(author, String.format("Capteur créé: %s (%s), Note: %s",
            sensor.getName(), sensor.getSensorId(), sensor.getNote()
        ));
    }

    /**
     * Adds a log entry for an existing sensor edition.
     * @param request to extract the author of this action
     * @param sensor the updated sensor
     * @param diff the updated fields
     */
    public void editSensor(HttpServletRequest request, Sensor sensor, String diff) {
        if (diff.isEmpty()) return;
        var author = currentAccount(request);
        save(author, String.format("Capteur édité: %s (%s)%s",
            sensor.getName(), sensor.getSensorId(), diff
        ));
    }

    /**
     * Adds a log entry for an existing sensor archivage.
     * @param request to extract the author of this action
     * @param sensor the archived sensor
     */
    public void archiveSensor(HttpServletRequest request, Sensor sensor) {
        var author = currentAccount(request);
        save(author, String.format("Capteur archivé: %s (%s)",
            sensor.getName(), sensor.getSensorId()
        ));
    }

    /**
     * Adds a log entry for an existing sensor restoration.
     * @param request to extract the author of this action
     * @param sensor the restored sensor
     */
    public void restoreSensor(HttpServletRequest request, Sensor sensor) {
        var author = currentAccount(request);
        save(author, String.format("Capteur restauré: %s (%s)",
            sensor.getName(), sensor.getSensorId()
        ));
    }

    /**
     * Adds a log entry for a new scan creation.
     * @param request to extract the author of this action
     * @param scan the received scan
     * @param results the number of results saved
     */
    public void addScan(HttpServletRequest request, Scan scan, int results) {
        var author = currentAccount(request);
        save(author, String.format(
            "Scan ajouté: Scan: #%d, Ouvrage: %s (#%d), Auteur: %s, Résultats: %d, Note: %s",
            scan.getId(), scan.getStructure().getName(), scan.getStructure().getId(),
            scan.getAuthor().getLogin(), results, scan.getNote()
        ));
    }

    /**
     * Adds a log entry for a sensor creation/edition within a scan.
     * @param request to extract the author of this action
     * @param scan the scan where the edition occurs
     * @param sensor the edited sensor
     * @param edit the new data of the sensor
     * @param newSensor true if this sensor just got created
     */
    public void addScanEdit(HttpServletRequest request, Scan scan, Sensor sensor, AndroidSensorEditDTO edit, boolean newSensor) {
        var author = currentAccount(request);
        if (newSensor) {
            save(author, String.format("Capteur créé lors du scan #%d: %s (%s), Note: %s",
                scan.getId(), sensor.getName(), sensor.getSensorId(), sensor.getNote()
            ));
        } else {
            save(author, String.format("Capteur édité lors du scan #%d: %s (%s)%s",
                scan.getId(), sensor.getName(), sensor.getSensorId(), edit.logDiff(sensor)
            ));
        }
    }

    /**
     * Adds a log entry for a sensor creation/edition within a scan.
     * @param request to extract the author of this action
     * @param scan the scan where the edition occurs
     * @param structure structure to be edited
     * @param note the new structure note
     */
    public void addScanNote(HttpServletRequest request, Scan scan, Structure structure, String note) {
        var author = currentAccount(request);
        save(author, String.format("Note d'ouvrage éditée lors du scan #%d: %s (%s), Note: %s -> %s",
            scan.getId(), structure.getName(), structure.getId(), scan.getNote(), note
        ));
    }
}
