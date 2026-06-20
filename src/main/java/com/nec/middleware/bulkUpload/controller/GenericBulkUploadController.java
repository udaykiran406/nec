package com.nec.middleware.bulkUpload.controller;

import com.nec.middleware.bulkUpload.dto.BulkUploadResultDto;
import com.nec.middleware.bulkUpload.excel.BulkErrorExcelWriter;
import com.nec.middleware.bulkUpload.handler.BulkUploadHandler;
import com.nec.middleware.bulkUpload.registry.BulkUploadHandlerRegistry;
import com.nec.middleware.bulkUpload.service.GenericBulkUploadService;
import com.nec.middleware.hr.dto.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
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

    @Operation(
            summary = "Bulk upload records for a given module",
            description = """
                    Upload an .xlsx file to create records in bulk.

                    Response behavior:
                    - If all rows succeed → returns JSON success summary.
                    - If any rows fail → returns downloadable Excel file
                      containing failed rows with an appended 'Error Reason' column.
                    """
    )
    @PostMapping(value = "/{module}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> bulkUpload(
            @PathVariable String module,
            @RequestParam("file") MultipartFile file) {

        log.info("Bulk upload request received for module='{}', file='{}'",
                module,
                file != null ? file.getOriginalFilename() : "null");

        BulkUploadHandler<?, ?> handler = handlerRegistry.getHandler(module);

        BulkUploadResultDto<?> result = bulkUploadService.process(file, handler);

        // Success → return summary JSON
        if (result.getFailureCount() == 0) {
            String message = String.format(
                    "Bulk upload complete: all %d rows saved successfully",
                    result.getSuccessCount());

            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ApiResponse.created(message, null));
        }

        // Failure → stream Excel file immediately
        return streamErrorReport(handler, result);
    }

    private ResponseEntity<?> streamErrorReport(
            BulkUploadHandler<?, ?> handler,
            BulkUploadResultDto<?> result) {

        String[] columnLabels = handler.expectedHeaders().toArray(new String[0]);
        String baseFileName = handler.moduleName()
                .toLowerCase()
                .replace('_', '-') + "-bulk-errors";

        File errorFile = bulkErrorExcelWriter.write(
                columnLabels,
                result.getErrors(),
                baseFileName
        );

        log.info(
                "Module '{}' bulk upload had {} failures out of {} rows — returning error report file",
                handler.moduleName(),
                result.getFailureCount(),
                result.getTotalRows()
        );

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
            log.error("Could not open generated error report for streaming: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to stream bulk error report", e);
        }

        return ResponseEntity.ok()
                .header(
                        HttpHeaders.CONTENT_DISPOSITION,
                        ContentDisposition.attachment()
                                .filename(errorFile.getName())
                                .build()
                                .toString()
                )
                .header("X-Bulk-Success-Count", String.valueOf(result.getSuccessCount()))
                .header("X-Bulk-Failure-Count", String.valueOf(result.getFailureCount()))
                .header("X-Bulk-Total-Rows", String.valueOf(result.getTotalRows()))
                .contentType(MediaType.parseMediaType(
                        "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .contentLength(errorFile.length())
                .body(resource);
    }
}