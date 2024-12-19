package fr.uge.structsure.services;

import fr.uge.structsure.entities.Account;
import fr.uge.structsure.repositories.AccountRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AccountService {

  private final AccountRepository accountRepository;
  private final PasswordEncoder passwordEncoder; // Nécessaire pour comparer les mots de passe cryptés

  public AccountService(AccountRepository accountRepository, PasswordEncoder passwordEncoder) {
    this.accountRepository = accountRepository;
    this.passwordEncoder = passwordEncoder;
  }

  // Connexion utilisateur
  public Optional<Account> login(String login, String password) {
    Optional<Account> account = accountRepository.findByLoginAndEnabledTrue(login);
    if (account.isPresent() && passwordEncoder.matches(password, account.get().getPasswordCrypted())) {
      return account;
    }
    return Optional.empty();
  }

  // Auteur d’un scan/interrogation
  public Optional<Account> getAuthorByScan(Long scanId) {
    return accountRepository.findAuthorByScan(scanId);
  }
}
