package fr.uge.structsure.services;

import fr.uge.structsure.config.JwtUtils;
import fr.uge.structsure.dto.*;
import fr.uge.structsure.entities.Account;
import fr.uge.structsure.entities.Role;
import fr.uge.structsure.exceptions.TraitementException;
import fr.uge.structsure.repositories.AccountRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
public class AccountService {

    private final AccountRepository accountRepository;

    private final AuthenticationManager authenticationManager;

    private final JwtUtils jwtUtils;

    public AccountService(AccountRepository accountRepository, AuthenticationManager authenticationManager,
                          JwtUtils jwtUtils) {
        this.accountRepository = Objects.requireNonNull(accountRepository);
        this.authenticationManager = Objects.requireNonNull(authenticationManager);
        this.jwtUtils = Objects.requireNonNull(jwtUtils);
    }

    public RegisterResponseDTO register(RegisterRequestDTO registerRequestDTO) throws TraitementException {
        if (accountRepository.findByLogin(registerRequestDTO.login()).isPresent()) {
            throw new TraitementException(1);
        }
        Role role;
        try {
            role = Role.valueOf(registerRequestDTO.role());
        } catch (IllegalArgumentException e) {
            throw new TraitementException(2);
        }
        var account = new Account(registerRequestDTO.login(), new BCryptPasswordEncoder().encode(registerRequestDTO.password()),
                registerRequestDTO.firstname(), registerRequestDTO.lastname(),
                role, true);
        accountRepository.save(account);
        return new RegisterResponseDTO(account.getLogin());
    }

    public LoginResponseDTO login(LoginRequestDTO loginRequestDTO) throws TraitementException {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginRequestDTO.login(),
                            loginRequestDTO.password())
            );
            if (authentication.isAuthenticated()) {
                return new LoginResponseDTO(jwtUtils.generateToken(loginRequestDTO.login()), "Bearer");
            }
            throw new TraitementException(3);
        } catch (AuthenticationException e) {
            throw new TraitementException(3);
        }
    }
}
