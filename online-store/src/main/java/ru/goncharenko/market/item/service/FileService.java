package ru.goncharenko.market.item.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;

@Slf4j
@Component
public class FileService {
	public static final String UPLOAD_DIR = "uploads" + File.separator;
	private static final String PLACEHOLDER_IMAGE_PATH = "classpath:/static/images/no_image.jpg";

	@Value(PLACEHOLDER_IMAGE_PATH)
	private Resource placeholderImage;

	public byte[] download(String filename) throws IOException {
		validateFilename(filename);

		try {
			Path uploadDir = Paths.get(UPLOAD_DIR).normalize();
			Path resolvedPath = uploadDir.resolve(filename).normalize();

			if (!resolvedPath.startsWith(uploadDir)) {
				throw new SecurityException("Path traversal attempt detected");
			}

			return Files.readAllBytes(resolvedPath);
		} catch (NoSuchFileException e) {
			return getPlaceholderImage();
		} catch (IOException e) {
			log.error("Error reading file: {}", filename, e);
			throw new IOException("Error accessing file: " + filename, e);
		}
	}

	private void validateFilename(String filename) {
		if (filename == null || filename.isEmpty()) {
			throw new IllegalArgumentException("Filename cannot be null or empty");
		}

		if (filename.contains("..") ||
				filename.contains("/") ||
				filename.contains("\\") ||
				filename.contains(":")) {
			throw new SecurityException("Invalid filename: " + filename);
		}
	}

	private byte[] getPlaceholderImage() throws IOException {
		try {
			return placeholderImage.getContentAsByteArray();
		} catch (IOException e) {
			log.error("Failed to load placeholder image", e);
			throw new IOException("Placeholder image not available", e);
		}
	}
}
