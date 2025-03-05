package fr.uge.structsure;

import org.junit.jupiter.api.BeforeAll;

import java.io.IOException;
import java.nio.file.FileSystemException;
import java.nio.file.Files;
import java.nio.file.Path;

public class DataBaseTests {
	@BeforeAll
	public static void cleanDatabase() throws IOException {
		try {
			Files.deleteIfExists(Path.of("structsure-test.db"));
		} catch (FileSystemException e) {
			// Database in use (already cleaned)
		}
	}
}
