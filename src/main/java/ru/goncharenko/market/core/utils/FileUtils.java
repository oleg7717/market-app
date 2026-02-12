package ru.goncharenko.market.core.utils;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;
import java.util.stream.Stream;

public class FileUtils {
	public static String getExtension(MultipartFile file) {
		String originalFilename = file.getOriginalFilename();
		if (originalFilename == null || originalFilename.isEmpty()) {
			return "";
		}
		int lastDotIndex = originalFilename.lastIndexOf('.');
		if (lastDotIndex == -1) {
			return "";
		}
		return originalFilename.substring(lastDotIndex + 1);
	}

	public static String getFileNameWithoutExtension(Path path) {
		String fileName = path.getFileName().toString();
		int dotIndex = fileName.lastIndexOf('.');
		return (dotIndex == -1) ? fileName : fileName.substring(0, dotIndex);
	}

	public static Optional<Path> findPath(Path uploadDir, long postId) throws IOException {
		try (Stream<Path> files = Files.walk(uploadDir)) {
			return files
					.filter(Files::isRegularFile)
					.filter(path -> FileUtils.getFileNameWithoutExtension(path)
							.equalsIgnoreCase("post" + postId + "_image"))
					.findFirst();
		}
	}
}
