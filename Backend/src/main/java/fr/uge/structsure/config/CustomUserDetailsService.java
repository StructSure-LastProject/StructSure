package fr.uge.structsure.config;

import fr.uge.structsure.entities.Account;
import fr.uge.structsure.repositories.AccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.Objects;

@Component
public class CustomUserDetailsService implements UserDetailsService {

    private final AccountRepository accountRepository;

    @Autowired
    public CustomUserDetailsService(AccountRepository accountRepository) {
        this.accountRepository = Objects.requireNonNull(accountRepository);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        var accountOptional = accountRepository.findByLogin(username);
        if (accountOptional.isEmpty())
            throw new UsernameNotFoundException("User not found : " + username);
        var account = accountOptional.get();
        return new User(account.getLogin(), account.getPasswordCrypted(),
                Collections.singletonList(new SimpleGrantedAuthority(account.getRole().toString())));
    }
}
