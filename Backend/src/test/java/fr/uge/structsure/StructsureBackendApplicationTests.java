package fr.uge.structsure;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@SpringBootTest
class StructsureBackendApplicationTests {

	@BeforeAll
	public static void cleanDatabase() throws IOException {
		Files.deleteIfExists(Path.of("structsure-test.db"));
	}

	@Test
	void contextLoads() {
	}

}
