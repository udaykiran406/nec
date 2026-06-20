package com.nec.middleware.hr.util;

import com.nec.middleware.bulkUpload.handler.BulkRowValidationException;
import com.nec.middleware.exception.BadRequestException;
import com.nec.middleware.exception.ValidationException;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
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

    private static final Set<String> ALLOWED_EXTENSIONS = Set.of(
            ".jpg", ".jpeg", ".png", ".webp"
    );

    private static final Set<String> ALLOWED_CONTENT_TYPES = Set.of(
            "image/jpeg", "image/jpg", "image/png", "image/webp"
    );

    private static final long MAX_FILE_SIZE_BYTES = 5L * 1024 * 1024; // 5 MB

    private static final int MAX_PHOTO_PATH_LENGTH = 1024;

    /** Cap on the sanitized base-name portion, to keep final filenames reasonable on disk. */
    private static final int MAX_BASE_NAME_LENGTH = 80;
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
        String baseName=sanitizeBaseName(file.getOriginalFilename(), extension);
        String fileName = UUID.randomUUID() + "_" + baseName + extension;

        String datedSubFolder = subFolder + "/" + LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy/MM")) + "/" + entityId;


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
        if (!ALLOWED_EXTENSIONS.contains(extension.toLowerCase())) {
            throw new BadRequestException("Invalid file extension");
        }
    }

    private String extractExtension(String originalFilename) {
        if (!StringUtils.hasText(originalFilename) || !originalFilename.contains(".")) {
            throw new BadRequestException("Uploaded file has no extension");
        }
        return originalFilename.substring(originalFilename.lastIndexOf('.'));
    }
//---------------------------------------------------------------------------PhotoPath Validation for bulk
    /**
     * Validates a photo path STRING supplied directly by the caller (bulk-upload mode).
     * Unlike {@link #storePhoto}, no bytes are read or written — the uploader's path
     * string is trusted as-is and persisted verbatim once it passes validation.
     *
     * @param photoPath relative or absolute path string from the bulk row
     * @return the same {@code photoPath}, unchanged, for chaining into the entity setter
     * @throws BulkRowValidationException if blank or extension is not in the allowed image set
     */
    public String validatePhotoPath(String photoPath) {
        if (!StringUtils.hasText(photoPath)) {
            throw new BulkRowValidationException("Photo path is required");
        }
        String trimmed = photoPath.trim();

        if (trimmed.length() > MAX_PHOTO_PATH_LENGTH) {
            throw new BulkRowValidationException("Photo path exceeds maximum length of " + MAX_PHOTO_PATH_LENGTH);
        }

        if (trimmed.contains("..")) {
            throw new BulkRowValidationException("Photo path must not contain '..' segments");
        }

        String extension = extractExtension(photoPath);
        if (!ALLOWED_EXTENSIONS.contains(extension.toLowerCase())) {
            throw new BulkRowValidationException("Only JPG, JPEG, PNG, or WEBP image paths are allowed");
        }

        return photoPath;
    }
//---------------------------------------------------------File name helper
    private String sanitizeBaseName(String originalFilename, String extension) {
        String withoutExt = originalFilename.substring(0, originalFilename.length() - extension.length());

        String sanitized = withoutExt
                .replaceAll("[^a-zA-Z0-9._-]", "-")
                .replaceAll("-{2,}", "-")
                .replaceAll("^[.-]+|[.-]+$", "");

        if (sanitized.isBlank()) {
            sanitized = "photo";
        }

        if (sanitized.length() > MAX_BASE_NAME_LENGTH) {
            sanitized = sanitized.substring(0, MAX_BASE_NAME_LENGTH);
        }

        return sanitized;
    }
}
