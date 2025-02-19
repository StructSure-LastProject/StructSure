package fr.uge.structsure.services;

import fr.uge.structsure.entities.Account;
import fr.uge.structsure.entities.AccountStructure;
import fr.uge.structsure.entities.Structure;
import fr.uge.structsure.exceptions.Error;
import fr.uge.structsure.exceptions.TraitementException;
import fr.uge.structsure.repositories.AccountRepository;
import fr.uge.structsure.repositories.AccountStructureRepository;
import fr.uge.structsure.repositories.StructureRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Objects;

/**
 * Account structure service
 */
@Service
public class AccountStructureService {
    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private StructureRepository structureRepository;

    @Autowired
    private AccountStructureRepository accountStructureRepository;


    /**
     * A simple record to hold a pair of Account and Structure entities.
     * <p>
     * This record ensures that both account and structure are not null by performing validation during initialization.
     * </p>
     */
    private record AccountAndStructureTuple(Account account, Structure structure){

        /**
         * Constructor that ensures both the account and structure are non-null.
         * <p>
         * If either the account or structure is null, a {@link NullPointerException} will be thrown.
         * </p>
         *
         * @param account The account associated with the structure.
         * @param structure The structure associated with the account.
         */
        public AccountAndStructureTuple {
            Objects.requireNonNull(account);
            Objects.requireNonNull(structure);
        }
    }

    /**
     * Helper method to fetch the account and structure, throwing an exception if not found.
     *
     * @param login The login of the user
     * @param structureId The structure ID
     * @return An array containing the account and structure
     * @throws TraitementException If the account or structure is not found
     */
    private AccountAndStructureTuple getAccountAndStructure(String login, Long structureId) throws TraitementException {
        Objects.requireNonNull(login);
        Objects.requireNonNull(structureId);
        var account = accountRepository.findById(login).orElseThrow(() -> new TraitementException(Error.USER_ACCOUNT_NOT_FOUND));
        var structure = structureRepository.findById(structureId).orElseThrow(() -> new TraitementException(Error.STRUCTURE_ID_NOT_FOUND));

        return new AccountAndStructureTuple(account, structure);
    }


    /**
     * Assign account to structure
     * @param login The login of the user
     * @param structureId The structure id
     * @throws TraitementException throw if user account does not exist or structure not found
     */
    public void assignAccountToStructure(String login, Long structureId) throws TraitementException {
        var accountAndStructure = getAccountAndStructure(login, structureId);
        var account = accountAndStructure.account();
        var structure = accountAndStructure.structure();

        if (accountStructureRepository.findByAccountAndStructure(account, structure).isEmpty()){
            var accountStructure = new AccountStructure(account, structure);
            accountStructureRepository.save(accountStructure);
        }
    }

    /**
     * Remove account to structure
     * @param login The login of the user
     * @param structureId The structure id
     * @throws TraitementException throw if user account does not exist or structure not found
     */
    public void removeAccountToStructure(String login, Long structureId) throws TraitementException {
        var accountAndStructure = getAccountAndStructure(login, structureId);
        var account = accountAndStructure.account();
        var structure = accountAndStructure.structure();

        var accountStructure = accountStructureRepository.findByAccountAndStructure(account, structure);
        accountStructure.ifPresent(value -> accountStructureRepository.delete(value));

    }

    /**
     * Checks if a structure is attached to the given user account
     * @param login The login
     * @param structureId The structure id
     * @return true of false
     * @throws TraitementException throw if user account does not exist or structure not found
     */
    public boolean isExist(String login, Long structureId) throws TraitementException {
        var accountAndStructure = getAccountAndStructure(login, structureId);
        var account = accountAndStructure.account();
        var structure = accountAndStructure.structure();

        return accountStructureRepository.findByAccountAndStructure(account, structure).isPresent();
    }

}
