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
	private static final String error_if_not_found = "Image file for item not found";

	public Resource download(String filename) {
		try {
			Path uploadDir = Paths.get(UPLOAD_DIR);
			return new ByteArrayResource(Files
					.readAllBytes(
							FileUtils.findPath(uploadDir, filename)
									.orElseThrow(() -> new ResourceNotFoundException(error_if_not_found))
					)
			);
		} catch (IOException e) {
			throw new ResourceNotFoundException(error_if_not_found);
		}
	}
}
