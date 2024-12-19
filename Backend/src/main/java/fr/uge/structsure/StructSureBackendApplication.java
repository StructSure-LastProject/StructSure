package fr.uge.structsure;

import org.hibernate.annotations.processing.Exclude;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;


@SpringBootApplication(exclude = {SecurityAutoConfiguration.class})
public class StructSureBackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(StructSureBackendApplication.class, args);
	}

}
