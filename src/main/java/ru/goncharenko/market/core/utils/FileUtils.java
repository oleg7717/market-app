package ru.goncharenko.market.core.utils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;
import java.util.stream.Stream;

public class FileUtils {
	public static Optional<Path> findPath(Path uploadDir, String filename) throws IOException {
		try (Stream<Path> files = Files.walk(uploadDir)) {
			return files
					.filter(Files::isRegularFile)
					.filter(path -> path.getFileName().toString()
							.equalsIgnoreCase(filename))
					.findFirst();
		}
	}
}
