package fr.uge.structsure;

import org.junit.jupiter.api.BeforeAll;

import java.io.IOException;
import java.nio.file.FileSystemException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.atomic.AtomicBoolean;

public class DataBaseTests {

	/** Prevent from removing the database while in use */
	private static final AtomicBoolean initialized = new AtomicBoolean(false);

	@BeforeAll
	public static void cleanDatabase() throws IOException {
		if (initialized.getAndSet(true)) return;
		try {
			Files.deleteIfExists(Path.of("structsure-test.db"));
		} catch (FileSystemException e) {
			// Database in use (already cleaned)
		}
	}
}
