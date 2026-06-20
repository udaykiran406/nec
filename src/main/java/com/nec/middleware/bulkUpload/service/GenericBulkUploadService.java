package com.nec.middleware.bulkUpload.service;

import com.nec.middleware.bulkUpload.dto.BulkUploadResultDto;
import com.nec.middleware.bulkUpload.dto.RowErrorDto;
import com.nec.middleware.bulkUpload.excel.GenericExcelParser;
import com.nec.middleware.bulkUpload.excel.ParsedExcelRow;
import com.nec.middleware.bulkUpload.handler.BulkRowValidationException;
import com.nec.middleware.bulkUpload.handler.BulkUploadHandler;
import com.nec.middleware.exception.DuplicateResourceException;
import com.nec.middleware.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
public class GenericBulkUploadService {

    private final GenericExcelParser excelParser;

    public <T, R> BulkUploadResultDto<R> process(MultipartFile file, BulkUploadHandler<T, R> handler) {

        log.info("Starting bulk upload for module '{}', file='{}'",
                handler.moduleName(), file != null ? file.getOriginalFilename() : "null");

        List<RowErrorDto> errors = new ArrayList<>();
        List<ParsedExcelRow> rows = excelParser.parse(file, handler.expectedHeaders(), errors);

        // Structural failure (no file, wrong extension, unreadable workbook,
        // missing header row) — nothing to process, return immediately.
        if (rows.isEmpty() && !errors.isEmpty()) {
            return BulkUploadResultDto.<R>builder()
                    .totalRows(0)
                    .successCount(0)
                    .failureCount(errors.size())
                    .errors(errors)
                    .build();
        }

        validateHeaderRow(rows, handler, errors);

        int sucessCount=0;

        for (ParsedExcelRow parsedRow : rows) {
            int rowNumber = parsedRow.getRowNumber();
            try {
                T dto = handler.map(parsedRow.getRawData());
                handler.validate(dto);
                R response = handler.persist(dto);
                sucessCount++;
                log.debug("Module '{}' row {}: saved", handler.moduleName(), rowNumber);

            } catch (BulkRowValidationException e) {
                log.warn("Module '{}' row {}: validation failed – {}",
                        handler.moduleName(), rowNumber, e.getMessage());
                errors.add(RowErrorDto.builder()
                        .rowNumber(rowNumber)
                        .field(e.getField())
                        .message(e.getMessage())
                        .rawData(parsedRow.getRawData())
                        .build());

            } catch (DuplicateResourceException | ResourceNotFoundException e) {
                log.warn("Module '{}' row {}: {} – {}",
                        handler.moduleName(), rowNumber, e.getClass().getSimpleName(), e.getMessage());
                errors.add(RowErrorDto.builder()
                        .rowNumber(rowNumber)
                        .message(e.getMessage())
                        .rawData(parsedRow.getRawData())
                        .build());

            } catch (Exception e) {
                log.error("Module '{}' row {}: unexpected error – {}",
                        handler.moduleName(), rowNumber, e.getMessage(), e);
                errors.add(RowErrorDto.builder()
                        .rowNumber(rowNumber)
                        .message(resolveErrorMessage(e))
                        .rawData(parsedRow.getRawData())
                        .build());
            }
        }

        int total = rows.size() + countRowlessStructuralErrors(errors);

        log.info("Bulk upload complete for module '{}' — total={}, success={}, failures={}",
                handler.moduleName(), total, sucessCount, errors.size());

        return BulkUploadResultDto.<R>builder()
                .totalRows(total)
                .successCount(sucessCount)
                .failureCount(errors.size())
                .errors(errors)
                .build();
    }

    // ------------------------------------------------------------------
    // Header validation
    // ------------------------------------------------------------------

    /**
     * Fail fast with one clear error if the uploaded file's header row is
     * missing columns the handler expects — rather than letting every
     <<<<<<< HEAD
     =======
     * single data row fail individually with a confusing "field is
     * required" message. Does not block processing; rows are still
     * attempted, since {@code map()} degrades missing columns to
     >>>>>>> rbac
     * {@code null} and {@code validate()} will report them per-row too if
     * this check is skipped or partially wrong.
     */
    private <T, R> void validateHeaderRow(
            List<ParsedExcelRow> rows, BulkUploadHandler<T, R> handler, List<RowErrorDto> errors) {

        if (rows.isEmpty()) return;

        Set<String> actualHeaders = new HashSet<>(rows.get(0).getRawData().keySet());
        List<String> missing = handler.expectedHeaders().stream()
                .filter(expected -> !actualHeaders.contains(expected))
                .toList();

        if (!missing.isEmpty()) {
            errors.add(RowErrorDto.builder()
                    .rowNumber(1)
                    .message("Uploaded file is missing required column(s): " + String.join(", ", missing))
                    .build());
        }
    }

    private int countRowlessStructuralErrors(List<RowErrorDto> errors) {
        return (int) errors.stream()
                .filter(e -> e.getRowNumber() <= 1)
                .filter(e -> e.getRawData() == null || e.getRawData().isEmpty())
                .count();
    }

    private String resolveErrorMessage(Exception e) {

        if (e.getCause() != null) {
            return e.getCause().getMessage();
        }

        return e.getMessage() != null
                ? e.getMessage()
                : "Unexpected error occurred";
    }
}
