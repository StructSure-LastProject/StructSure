package fr.uge.structsure.services;

import fr.uge.structsure.entities.Account;
import fr.uge.structsure.repositories.AccountRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

class AccountServiceTest {

  @Mock
  private AccountRepository accountRepository;

  @Mock
  private PasswordEncoder passwordEncoder;

  @InjectMocks
  private AccountService accountService;

  private Account mockAccount;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
    // Créer un compte mock pour les tests
    mockAccount = new Account();
    mockAccount.setLogin("user");
    mockAccount.setPasswordCrypted("$2a$10$J9U9..."); // Exemple de mot de passe crypté
    mockAccount.setEnabled(true);
  }

  @Test
  void testLoginSuccessful() {
    // Simuler un comportement du repository
    when(accountRepository.findByLoginAndEnabledTrue("user")).thenReturn(Optional.of(mockAccount));
    when(passwordEncoder.matches("password", mockAccount.getPasswordCrypted())).thenReturn(true);

    Optional<Account> accountOptional = accountService.login("user", "password");

    assertTrue(accountOptional.isPresent());
    assertEquals("user", accountOptional.get().getLogin());
  }

  @Test
  void testLoginInvalidPassword() {
    // Simuler un comportement du repository
    when(accountRepository.findByLoginAndEnabledTrue("user")).thenReturn(Optional.of(mockAccount));
    when(passwordEncoder.matches("wrongPassword", mockAccount.getPasswordCrypted())).thenReturn(false);

    Optional<Account> accountOptional = accountService.login("user", "wrongPassword");

    assertFalse(accountOptional.isPresent());
  }

  @Test
  void testLoginAccountNotFound() {
    // Simuler un comportement du repository où aucun compte n'est trouvé
    when(accountRepository.findByLoginAndEnabledTrue("nonExistentUser")).thenReturn(Optional.empty());

    Optional<Account> accountOptional = accountService.login("nonExistentUser", "password");

    assertFalse(accountOptional.isPresent());
  }

  @Test
  void testGetAuthorByScan() {
    // Simuler un comportement du repository
    when(accountRepository.findAuthorByScan(1L)).thenReturn(Optional.of(mockAccount));

    Optional<Account> accountOptional = accountService.getAuthorByScan(1L);

    assertTrue(accountOptional.isPresent());
    assertEquals("user", accountOptional.get().getLogin());
  }

  @Test
  void testGetAuthorByScanNotFound() {
    // Simuler l'absence de l'auteur pour l'ID de scan donné
    when(accountRepository.findAuthorByScan(999L)).thenReturn(Optional.empty());

    Optional<Account> accountOptional = accountService.getAuthorByScan(999L);

    assertFalse(accountOptional.isPresent());
  }
}
