package fr.uge.structsure.services;

import fr.uge.structsure.config.JwtUtils;
import fr.uge.structsure.entities.Account;
import fr.uge.structsure.entities.Role;
import fr.uge.structsure.exceptions.Error;
import fr.uge.structsure.exceptions.TraitementException;
import fr.uge.structsure.repositories.AccountRepository;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.io.DecodingException;
import io.jsonwebtoken.security.SignatureException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.Optional;


/**
 * Auth validation service
 */
@Service
public class AuthValidationService {

    private final JwtUtils jwtUtils;

    /**
     * Constructor
     * @param jwtUtils The jwt utils object
     */
    @Autowired
    public AuthValidationService(JwtUtils jwtUtils) {
        this.jwtUtils = Objects.requireNonNull(jwtUtils);
    }

    /**
     * Check the validity of the JWT TOKEN
     * @param request The HTTP Request
     * @return Account Return the account if exist
     * @throws TraitementException thrown JWT token extract exception into custom exception
     */
    public Account checkTokenValidityAndUserAccessVerifier(HttpServletRequest request, AccountRepository accountRepository) throws TraitementException {
        Objects.requireNonNull(request);
        Optional<Account> account;
        try {
            var token = request.getHeader("authorization");
            if (token.startsWith("Bearer ")) token = token.substring(7);
            account = accountRepository.findByLogin(jwtUtils.extractUsername(token));
        } catch (UnsupportedJwtException | MalformedJwtException | DecodingException | SignatureException |
                 ExpiredJwtException | IllegalArgumentException | NullPointerException e){
            throw new TraitementException(fr.uge.structsure.exceptions.Error.INVALID_TOKEN);
        }

        if (account.isEmpty()){
            throw new TraitementException(Error.INVALID_TOKEN);
        }
        return account.get();
    }

    /**
     * Convert role in string format to role enum
     * @param roleString The role in string format
     * @return The Role enum
     * @throws TraitementException Thrown if role doesn't exist
     */
    public Role convertRoleStringToRole(String roleString) throws TraitementException {
        Objects.requireNonNull(roleString);
        Role role;
        try {
            role = Role.fromValue(roleString);
        } catch (IllegalArgumentException e) {
            throw new TraitementException(Error.ROLE_NOT_EXISTS);
        }
        return role;
    }


}
