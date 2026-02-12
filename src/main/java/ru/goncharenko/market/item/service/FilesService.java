package ru.goncharenko.market.item.service;

import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;
import ru.goncharenko.market.core.exception.ResourceNotFoundException;
import ru.goncharenko.market.core.utils.FileUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Component
public class FilesService {
	public static final String UPLOAD_DIR = "uploads"  + File.separator;
	private static final String error_if_not_found = "Image file for post not found";

/*	public void upload(long postId, MultipartFile file) {
		try {
			Path uploadDir = Paths.get(UPLOAD_DIR + postId);
			if (!Files.exists(uploadDir)) {
				Files.createDirectories(uploadDir);
			}

			// Сохраняем файл
			String extension = FileUtils.getExtension(file);
			Path filePath = uploadDir.resolve("post" + postId + "_image." + extension);
			file.transferTo(filePath);
		} catch (IOException ex) {
			throw new RuntimeException(ex.getMessage());
		}
	}*/

	public Resource download(long postId) {
		try {
			Path uploadDir = Paths.get(UPLOAD_DIR + postId);
			return new ByteArrayResource(Files
					.readAllBytes(
							FileUtils.findPath(uploadDir, postId)
									.orElseThrow(() -> new ResourceNotFoundException(error_if_not_found))
					)
			);
		} catch (IOException e) {
			throw new ResourceNotFoundException(error_if_not_found);
		}
	}

	public void delete(long postId) {
		try {
			Path uploadDir = Paths.get(UPLOAD_DIR + postId);
			FileUtils.findPath(uploadDir, postId)
					.filter(path -> (UPLOAD_DIR + postId).equals(path.getParent().toString()))
					.ifPresent(path -> {
						try {
							Files.deleteIfExists(path);
							Files.deleteIfExists(path.getParent());
						} catch (IOException ignored) {
						}
					});
		} catch (IOException ignored) {
		}
	}
}
