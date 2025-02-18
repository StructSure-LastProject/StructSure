package fr.uge.structsure.services;

import fr.uge.structsure.config.JwtUtils;
import fr.uge.structsure.dto.auth.LoginRequestDTO;
import fr.uge.structsure.dto.auth.RegisterRequestDTO;
import fr.uge.structsure.dto.userAccount.UserAccountResponseDTO;
import fr.uge.structsure.entities.Account;
import fr.uge.structsure.entities.Role;
import fr.uge.structsure.exceptions.Error;
import fr.uge.structsure.exceptions.TraitementException;
import fr.uge.structsure.repositories.AccountRepository;
import jakarta.servlet.http.HttpServletRequest;
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

        assertEquals(Error.USER_ALREADY_EXISTS, exception.error);
        verify(accountRepository, never()).save(any());
    }

    @Test
    void testRegisterInvalidRole() {
        RegisterRequestDTO invalidRoleRequest = new RegisterRequestDTO("testuser", "password", "John", "Doe", "INVALID_ROLE");

        TraitementException exception = assertThrows(TraitementException.class, () -> accountService.register(invalidRoleRequest));

        assertEquals(Error.ROLE_NOT_EXISTS, exception.error);
        verify(accountRepository, never()).save(any());
    }

    @Test
    void testLoginInvalidCredentials() {
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenThrow(new AuthenticationException("Invalid credentials") {});

        TraitementException exception = assertThrows(TraitementException.class, () -> accountService.login(loginRequestDTO));

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
        verify(accountRepository, never()).save(any());
    }
}
