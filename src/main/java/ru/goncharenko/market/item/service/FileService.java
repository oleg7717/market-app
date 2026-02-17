package ru.goncharenko.market.item.service;

import org.springframework.stereotype.Component;
import ru.goncharenko.market.core.exception.ResourceNotFoundException;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Component
public class FileService {
	public static final String UPLOAD_DIR = "uploads"  + File.separator;
	private static final String error_if_not_found = "Image file for item not found";

	public byte[] download(String filename) {
		try {
			Path uploadDir = Paths.get(UPLOAD_DIR).normalize();

			if (filename == null || filename.isEmpty()) {
				throw new ResourceNotFoundException(error_if_not_found);
			}

			if (filename.contains("..") ||
					filename.contains("/") ||
					filename.contains("\\") ||
					filename.contains(":")) {
				throw new ResourceNotFoundException(error_if_not_found);
			}

			Path resolvedPath = uploadDir.resolve(filename).normalize();
			if (!resolvedPath.startsWith(uploadDir)) {
				throw new ResourceNotFoundException(error_if_not_found);
			}

			return Files.readAllBytes(resolvedPath);
		} catch (IOException e) {
			throw new ResourceNotFoundException(error_if_not_found);
		}
	}
}
