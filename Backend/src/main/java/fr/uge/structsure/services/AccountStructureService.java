package fr.uge.structsure.services;

import fr.uge.structsure.entities.Account;
import fr.uge.structsure.exceptions.Error;
import fr.uge.structsure.exceptions.TraitementException;
import fr.uge.structsure.repositories.AccountRepository;
import fr.uge.structsure.repositories.StructureRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Objects;

/**
 * Account structure service
 */
@Service
public class AccountStructureService {

    private final AccountRepository accountRepository;
    private final StructureRepository structureRepository;

    /**
     * Constructor
     * @param accountRepository The account repository
     * @param structureRepository The structure repository
     */
    @Autowired
    public AccountStructureService(AccountRepository accountRepository, StructureRepository structureRepository){
        this.accountRepository = accountRepository;
        this.structureRepository = structureRepository;
    }


    /**
     * Helper method to fetch the account, throwing an exception if not found.
     *
     * @param login The login of the user
     * @return Account
     * @throws TraitementException If the account is not found
     */
    private Account getAccountAndStructure(String login) throws TraitementException {
        Objects.requireNonNull(login);
        return accountRepository.findByLogin(login).orElseThrow(() -> new TraitementException(Error.USER_ACCOUNT_NOT_FOUND));

    }


    /**
     * Assign account to structure
     * @param login The login of the user
     * @param structureId The structure id
     * @throws TraitementException throw if user account does not exist or structure not found
     */
    @Transactional
    public void assignAccountToStructure(String login, Long structureId) throws TraitementException {
        var account = getAccountAndStructure(login);
        var allowedStructures = account.getAllowedStructures();
        var structure = structureRepository.findById(structureId).orElseThrow(() -> new TraitementException(Error.STRUCTURE_ID_NOT_FOUND));

        if (allowedStructures.stream().noneMatch(s -> s.getId() == structureId)) {
            account.add(structure);
            structure.add(account);
            accountRepository.save(account);
        }
    }


}
