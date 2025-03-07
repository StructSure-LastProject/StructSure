package fr.uge.structsure.services;

import fr.uge.structsure.DataBaseTests;
import fr.uge.structsure.dto.auth.LoginRequestDTO;
import fr.uge.structsure.dto.auth.RegisterRequestDTO;
import fr.uge.structsure.entities.Account;
import fr.uge.structsure.entities.Role;
import fr.uge.structsure.exceptions.Error;
import fr.uge.structsure.exceptions.TraitementException;
import fr.uge.structsure.repositories.AccountRepository;
import fr.uge.structsure.utils.AuthenticationType;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
class AccountServiceTest extends DataBaseTests {

    @Autowired
    private AccountService accountService;

    @Autowired
    private AccountRepository accountRepository;

    private final Account account = new Account("testuser", "encodedPassword", "John", "Doe", Role.ADMIN, true);
    private final RegisterRequestDTO registerRequestDTO = new RegisterRequestDTO("testuser", "passwordlongenough", "John", "Doe", "Admin");
    private final LoginRequestDTO loginRequestDTO = new LoginRequestDTO("testuser", "passwordlongenough");
    private final HttpServletRequest request = Mockito.mock(HttpServletRequest.class);

    @BeforeEach
    void setUp() {
        accountRepository.save(account);
    }

    @AfterEach
    void clean() {
        accountRepository.delete(account);
    }

    @Test
    void testRegisterUserAlreadyExists() {
        TraitementException exception = assertThrows(TraitementException.class, () -> accountService.register(request, registerRequestDTO));
        assertEquals(Error.USER_ALREADY_EXISTS, exception.error);
    }

    @Test
    void testRegisterInvalidRole() {
        RegisterRequestDTO invalidRoleRequest = new RegisterRequestDTO("testuser2", "passwordlongenough", "John", "Doe", "INVALID_ROLE");

        TraitementException exception = assertThrows(TraitementException.class, () -> accountService.register(request, invalidRoleRequest));

        assertEquals(Error.ROLE_NOT_EXISTS, exception.error);
    }

    @Test
    void testLoginInvalidCredentials() {
        TraitementException exception = assertThrows(TraitementException.class, () -> accountService.login(loginRequestDTO, AuthenticationType.WEB));
        assertEquals(Error.LOGIN_PASSWORD_NOT_CORRECT, exception.error);
    }

    @Test
    void testGetUserAccounts() {
        // Set up an account with the correct role (USER) for testing purposes
        Account testAccount = new Account("testuser", "encodedPassword", "John", "Doe", Role.OPERATEUR, true);

        // Simulate the request with a valid token and role (ADMIN)
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getHeader("authorization")).thenReturn("valid_token");

        TraitementException exception = assertThrows(TraitementException.class, () -> accountService.getUserAccounts(request));
        assertEquals(Error.INVALID_TOKEN, exception.error);
    }
}
