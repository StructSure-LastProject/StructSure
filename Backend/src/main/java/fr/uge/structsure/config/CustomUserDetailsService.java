package fr.uge.structsure.config;

import fr.uge.structsure.repositories.AccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.Objects;


/**
 * This class will implement UserDetailsService that will be used by spring security
 * in order to load the user by username
 */
@Component
public class CustomUserDetailsService implements UserDetailsService {

    private final AccountRepository accountRepository;

    @Autowired
    public CustomUserDetailsService(AccountRepository accountRepository) {
        this.accountRepository = Objects.requireNonNull(accountRepository);
    }

    /**
     * This will be called by Spring Security to load the user by its username.
     * If the user is not found, we throw an exception and no token will be generated
     * by Spring Security. Otherwise, we return a User object that contains the login,
     * password, and role of the user.
     * @param username the username of the user
     * @return UserDetails the user found
     * @throws UsernameNotFoundException If no user found with the given username
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        var accountOptional = accountRepository.findByLogin(username);
        if (accountOptional.isEmpty())
            throw new UsernameNotFoundException("User not found : " + username);
        var account = accountOptional.get();
        return new User(account.getLogin(), account.getPasswordEncrypted(),
                Collections.singletonList(new SimpleGrantedAuthority(account.getRole().toString())));
    }
}
