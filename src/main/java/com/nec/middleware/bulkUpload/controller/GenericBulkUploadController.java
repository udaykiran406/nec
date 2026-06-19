package com.nec.middleware.bulkUpload.controller;

import com.nec.middleware.bulkUpload.dto.BulkUploadResultDto;
import com.nec.middleware.bulkUpload.excel.BulkErrorExcelWriter;
import com.nec.middleware.bulkUpload.handler.BulkUploadHandler;
import com.nec.middleware.bulkUpload.registry.BulkUploadHandlerRegistry;
import com.nec.middleware.bulkUpload.service.BulkErrorReportStore;
import com.nec.middleware.bulkUpload.service.GenericBulkUploadService;
import com.nec.middleware.hr.dto.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

@Slf4j
@RestController
@RequestMapping("/api/v1/bulk-upload")
@RequiredArgsConstructor
@Tag(name = "Bulk Upload", description = "Generic Excel bulk upload endpoint for all modules")
public class GenericBulkUploadController {

    private final BulkUploadHandlerRegistry handlerRegistry;
    private final GenericBulkUploadService bulkUploadService;
    private final BulkErrorExcelWriter bulkErrorExcelWriter;
    private final BulkErrorReportStore errorReportStore;

    // ─────────────────────────────────────────────────────────────────────────
    // POST /api/v1/bulk-upload/{module}
    // ─────────────────────────────────────────────────────────────────────────

    @Operation(
            summary = "Bulk upload records for a given module",
            description = """
                    Upload an .xlsx file to create records in bulk.

                    • All rows succeed  → 201 JSON: success message.
                    • Any row fails     → 207 JSON: summary + errorDownloadUrl.
                      Open that URL directly in the browser to download the .xlsx error report.
                      The URL is valid for 30 minutes.
                    """
    )
    @PostMapping(value = "/{module}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> bulkUpload(
            @PathVariable String module,
            @RequestParam("file") MultipartFile file,
            HttpServletRequest request) {

        log.info("Bulk upload request: module='{}', file='{}'",
                module, file != null ? file.getOriginalFilename() : "null");

        BulkUploadHandler<?, ?> handler = handlerRegistry.getHandler(module);
        BulkUploadResultDto<?> result = bulkUploadService.process(file, handler);

        // ── All rows succeeded ────────────────────────────────────────────────
        if (result.getFailureCount() == 0) {
            String message = String.format(
                    "Bulk upload complete: all %d rows saved successfully", result.getSuccessCount());
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ApiResponse.created(message, null));
        }

        // ── One or more rows failed → build error Excel, store it, return URL ─
        String[] columnLabels = handler.expectedHeaders().toArray(new String[0]);
        String baseFileName = handler.moduleName().toLowerCase().replace('_', '-') + "-bulk-errors";
        File errorFile = bulkErrorExcelWriter.write(columnLabels, result.getErrors(), baseFileName);
        String token = errorReportStore.store(errorFile);

        // Build an absolute download URL the user can open directly in a browser
        String downloadUrl = buildDownloadUrl(request, token);
        ((BulkUploadResultDto) result).setErrorDownloadUrl(downloadUrl);

        log.info("Module '{}' — {}/{} rows failed. Error report token={}",
                handler.moduleName(), result.getFailureCount(), result.getTotalRows(), token);

        String message = String.format(
                "%d row(s) failed out of %d. %d row(s) saved successfully. "
                        + "Click the errorDownloadUrl to download the error report.",
                result.getFailureCount(), result.getTotalRows(), result.getSuccessCount());

        return ResponseEntity.status(HttpStatus.MULTI_STATUS)
                .body(ApiResponse.success(message, result));
    }

    // ─────────────────────────────────────────────────────────────────────────
    // GET /api/v1/bulk-upload/errors/download/{token}
    // ─────────────────────────────────────────────────────────────────────────

    @Operation(
            summary = "Download bulk-upload error report",
            description = """
                    GET endpoint — open this URL directly in a browser to download the .xlsx error file.
                    The token comes from the errorDownloadUrl field in the POST response.
                    Single-use, expires after 30 minutes.
                    Each failed row is present with an appended "Error Reason" column.
                    """
    )
    @GetMapping("/errors/download/{token}")
    public ResponseEntity<?> downloadErrorReport(@PathVariable String token) {

        log.info("Error report download requested, token='{}'", token);

        File errorFile = errorReportStore.claim(token);

        if (errorFile == null || !errorFile.exists()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.success(
                            "Error report not found or has expired (valid for 30 minutes). Please re-upload.",
                            null));
        }

        InputStreamResource resource;
        try {
            resource = new InputStreamResource(new FileInputStream(errorFile) {
                @Override
                public void close() throws IOException {
                    super.close();
                    bulkErrorExcelWriter.cleanup(errorFile);
                }
            });
        } catch (FileNotFoundException e) {
            log.error("Could not open error report for streaming: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to stream bulk error report", e);
        }

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        ContentDisposition.attachment().filename(errorFile.getName()).build().toString())
                .contentType(MediaType.parseMediaType(
                        "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .contentLength(errorFile.length())
                .body(resource);
    }

    // ─────────────────────────────────────────────────────────────────────────

    private String buildDownloadUrl(HttpServletRequest request, String token) {
        String scheme = request.getScheme();             // http / https
        String host   = request.getServerName();         // localhost
        int    port   = request.getServerPort();         // 8080
        String ctx    = request.getContextPath();        // "" or /app

        // Omit default ports (80 for http, 443 for https) to keep the URL clean
        String portPart = (scheme.equals("http")  && port == 80)
                || (scheme.equals("https") && port == 443)
                ? "" : ":" + port;

        return scheme + "://" + host + portPart + ctx
                + "/api/v1/bulk-upload/errors/download/" + token;
    }
}