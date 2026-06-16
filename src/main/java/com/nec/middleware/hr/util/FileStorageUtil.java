package com.nec.middleware.hr.util;

import com.nec.middleware.exception.BadRequestException;
import com.nec.middleware.exception.ValidationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Set;
import java.util.UUID;

/**
 * Shared util for saving uploaded photos to local disk and returning the
 * stored path to persist on the entity.
 *
 * Used across all modules that accept a profile photo:
 * Portal User, University Trainee, Political Party Agent, Ministry of
 * Interior. Each module passes its own sub-folder name so files don't mix.
 *
 * Storage root is externalized via application.yaml:
 *   file.upload.base-dir=C:/nec-uploads
 *
 * Final layout on disk:
 *   C:/nec-uploads/portal-users/<uuid>.jpg
 *
 * Value persisted in DB (photoPath / photoUrl column):
 *   portal-users/<uuid>.jpg
 *
 * Keeping only the relative path in the DB (not the full "C:/nec-uploads/..."
 * prefix) means the base dir can move/change per-environment without a data
 * migration, and a "serve photo" endpoint can resolve relativePath -> file.
 */
@Slf4j
@Component
public class FileStorageUtil {

    @Value("${file.upload.base-dir:C:/nec-uploads}")
    private String baseDir;

    private static final Set<String> ALLOWED_CONTENT_TYPES = Set.of(
            "image/jpeg", "image/jpg", "image/png", "image/webp"
    );

    private static final long MAX_FILE_SIZE_BYTES = 5L * 1024 * 1024; // 5 MB

    /**
     * Validates and stores an uploaded photo under {baseDir}/{subFolder}/.
     *
     * @param file      the multipart file from the form-data request
     * @param subFolder logical bucket, e.g. "portal-users"
     * @return relative path (subFolder/filename) to persist in the DB
     */
    public String storePhoto(MultipartFile file, String subFolder,String entityId) {

        validate(file);

        String extension = extractExtension(file.getOriginalFilename());
        String fileName = UUID.randomUUID() + extension;

        String datedSubFolder = subFolder + "/" + LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy/MM"))+"/"+entityId;

        try {
            Path targetDir = Paths.get(baseDir, datedSubFolder).normalize();
            Files.createDirectories(targetDir);

            Path targetFile = targetDir.resolve(fileName).normalize();


            if (!targetFile.startsWith(Paths.get(baseDir).normalize())) {
                throw new BadRequestException("Invalid file path");
            }

            try (InputStream in = file.getInputStream()) {
                Files.copy(in, targetFile, StandardCopyOption.REPLACE_EXISTING);
            }

            String relativePath = datedSubFolder + "/" + fileName;
            log.info("Stored uploaded photo at {}", targetFile);
            return relativePath;

        } catch (IOException e) {
            log.error("Failed to store uploaded photo", e);
            throw new BadRequestException("Failed to store uploaded photo: " + e.getMessage());
        }
    }

    /**
     * Deletes a previously stored photo (relative path as saved in DB).
     * Safe to call with null/blank — becomes a no-op. Failures are logged,
     * not thrown, so a delete issue never blocks the main DB transaction
     * (e.g. replacing a photo on update).
     */
    public void deleteIfExists(String relativePath) {
        if (!StringUtils.hasText(relativePath)) {
            return;
        }
        try {
            Path target = Paths.get(baseDir, relativePath).normalize();
            Files.deleteIfExists(target);
        } catch (IOException e) {
            log.warn("Could not delete old photo at {}: {}", relativePath, e.getMessage());
        }
    }

    // ------------------------------------------------------------------
    // Validation helpers
    // ------------------------------------------------------------------

    private void validate(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new BadRequestException("Photo file is required");
        }

        if (file.getSize() > MAX_FILE_SIZE_BYTES) {
            throw new BadRequestException("Photo must not exceed 5MB");
        }

        String contentType = file.getContentType();
        if (contentType == null || !ALLOWED_CONTENT_TYPES.contains(contentType.toLowerCase())) {
            throw new BadRequestException("Only JPG, JPEG, PNG, or WEBP images are allowed");
        }

        String extension = extractExtension(file.getOriginalFilename());
        if (!List.of(".jpg", ".jpeg", ".png", ".webp").contains(extension.toLowerCase())) {
            throw new BadRequestException("Invalid file extension");
        }
    }

    private String extractExtension(String originalFilename) {
        if (!StringUtils.hasText(originalFilename) || !originalFilename.contains(".")) {
            throw new BadRequestException("Uploaded file has no extension");
        }
        return originalFilename.substring(originalFilename.lastIndexOf('.'));
    }
}
