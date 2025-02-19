package fr.uge.structsure;

import fr.uge.structsure.entities.Account;
import fr.uge.structsure.entities.Role;
import fr.uge.structsure.repositories.AccountRepository;
import org.apache.commons.lang3.RandomStringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@SpringBootApplication(exclude = {SecurityAutoConfiguration.class})
public class StructSureBackendApplication {

	/** The login of the super-admin account */
	public static final String SUPER_ADMIN_LOGIN = "StructSureAdmin";

	/** The official logger of this class */
	private static final Logger LOGGER = LoggerFactory.getLogger(StructSureBackendApplication.class);

	/** Access to the accounts table to set up the super-admin */
	private final AccountRepository accountRepository;

	public static void main(String[] args) {
		SpringApplication.run(StructSureBackendApplication.class, args);
	}

	/**
	 * Constructor of the main application to inject autowired values.
	 * This constructor should only be called automatically by spring.
	 * @param accountRepository access to the accounts table
	 */
	protected StructSureBackendApplication(AccountRepository accountRepository) {
		this.accountRepository = accountRepository;
	}

	@EventListener(ApplicationReadyEvent.class)
	public void onReady() {
		setupSuperAdmin();
	}

	/**
	 * Adds the SuperAdmin account to the application if not already
	 * present (with a randomly generated password)
	 */
	private void setupSuperAdmin() {
		var admin = accountRepository.findByLogin(SUPER_ADMIN_LOGIN);
		if (admin.isPresent()) return;

		var password = RandomStringUtils.secure().nextAlphanumeric(16, 32);
		var account = new Account(
			SUPER_ADMIN_LOGIN,
			new BCryptPasswordEncoder().encode(password),
			"StructSure", "Admin",
			Role.ADMIN,
			true
		);
		accountRepository.save(account);
		LOGGER.error("\033[0;91m\n\n!!! IMPORTANT !!!\nSuper admin account created with password {}\nThis password MUST be changed immediately!\n\033[0m", password);
	}
}
