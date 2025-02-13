package fr.uge.structsure.services;

import fr.uge.structsure.config.JwtUtils;
import fr.uge.structsure.dto.auth.LoginRequestDTO;
import fr.uge.structsure.dto.auth.LoginResponseDTO;
import fr.uge.structsure.dto.auth.RegisterRequestDTO;
import fr.uge.structsure.dto.auth.RegisterResponseDTO;
import fr.uge.structsure.dto.userAccount.UserAccountResponseDTO;
import fr.uge.structsure.entities.Account;
import fr.uge.structsure.entities.Role;
import fr.uge.structsure.exceptions.ErrorIdentifier;
import fr.uge.structsure.exceptions.TraitementException;
import fr.uge.structsure.repositories.AccountRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AccountServiceTest {

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtUtils jwtUtils;

    @InjectMocks
    private AccountService accountService;

    private Account account;
    private RegisterRequestDTO registerRequestDTO;
    private LoginRequestDTO loginRequestDTO;

    @BeforeEach
    void setUp() {
        account = new Account("testuser", "encodedPassword", "John", "Doe", Role.OPERATEUR, true);
        registerRequestDTO = new RegisterRequestDTO("testuser", "password", "John", "Doe", "USER");
        loginRequestDTO = new LoginRequestDTO("testuser", "password");
    }

    @Test
    void testRegisterUserAlreadyExists() {
        when(accountRepository.findByLogin(registerRequestDTO.login())).thenReturn(Optional.of(account));

        TraitementException exception = assertThrows(TraitementException.class, () -> accountService.register(registerRequestDTO));

        assertEquals(ErrorIdentifier.USER_ALREADY_EXISTS, exception.getErrorIdentifier());
        verify(accountRepository, never()).save(any());
    }

    @Test
    void testRegisterInvalidRole() {
        RegisterRequestDTO invalidRoleRequest = new RegisterRequestDTO("testuser", "password", "John", "Doe", "INVALID_ROLE");

        TraitementException exception = assertThrows(TraitementException.class, () -> accountService.register(invalidRoleRequest));

        assertEquals(ErrorIdentifier.ROLE_NOT_EXISTS, exception.getErrorIdentifier());
        verify(accountRepository, never()).save(any());
    }

    @Test
    void testLoginInvalidCredentials() {
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenThrow(new AuthenticationException("Invalid credentials") {});

        TraitementException exception = assertThrows(TraitementException.class, () -> accountService.login(loginRequestDTO));

        assertEquals(ErrorIdentifier.LOGIN_PASSWORD_NOT_CORRECT, exception.getErrorIdentifier());
    }

    @Test
    void testGetUserAccounts() {
        // Fix the role to match the expected value (USER) in the test
        Account accountWithCorrectRole = new Account("testuser", "encodedPassword", "John", "Doe", Role.OPERATEUR, true);

        // Mock the repository call to return the correctly initialized account
        when(accountRepository.findAll()).thenReturn(List.of(accountWithCorrectRole));

        // Call the method to test
        List<UserAccountResponseDTO> response = accountService.getUserAccounts();

        // Verify that the response has the correct role and other expected properties
        assertNotNull(response);
        assertEquals(1, response.size());

        UserAccountResponseDTO userAccountResponse = response.get(0);
        assertEquals("John", userAccountResponse.firstName());
        assertEquals("Doe", userAccountResponse.lastName());
        assertEquals("testuser", userAccountResponse.login());
        assertEquals(Role.OPERATEUR.value, userAccountResponse.role());
        assertTrue(userAccountResponse.enabled());
    }
}
