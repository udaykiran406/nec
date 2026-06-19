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

/**
 * CORE bulk-upload pipeline. Every module's upload — UniversityTrainee,
 * Employee, Student, Vendor, Contractor, anything added later — runs
 * through this exact same class. The only thing that varies per module is
 * which {@link BulkUploadHandler} gets passed in.
 *
 * <p>Per-row flow:
 * <ol>
 *   <li>{@code handler.map(row)} — raw columns → DTO</li>
 *   <li>{@code handler.validate(dto)} — bean validation + business rules + FK checks</li>
 *   <li>{@code handler.persist(dto)} — save and return the response DTO</li>
 * </ol>
 *
 * <p>Each row is isolated: a failure at any step is caught, turned into a
 * {@link RowErrorDto} carrying that row's original raw data, and processing
 * continues with the next row. Rows already persisted earlier in the batch
 * are never rolled back because of a later row's failure — that guarantee
 * is enforced by each handler's {@code persist} method owning its own
 * {@code REQUIRES_NEW} transaction (see module README), not by this engine.
 *
 * <p>This class has NO knowledge of UniversityTrainee, Employee, or any
 * other concrete module — it only ever calls the five
 * {@link BulkUploadHandler} methods and the generic {@link GenericExcelParser}.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class GenericBulkUploadService {

    private final GenericExcelParser excelParser;

    /**
     * Parse {@code file} with the generic Excel parser, then map/validate/
     * persist every row through {@code handler}, collecting successes and
     * failures independently.
     *
     * @param file    the uploaded {@code .xlsx} file
     * @param handler the module-specific strategy implementation
     * @param <T>     the module's request DTO type
     * @param <R>     the module's response DTO type
     * @return aggregate result with success records and per-row errors
     */
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

    /**
     * Counts structural parse errors that have no associated data row at
     * all (e.g. "missing header column") so they're included in
     * {@code totalRows} alongside the rows the parser did manage to read.
     * Per-row failures (which DO have a row already counted in
     * {@code rows.size()}) are intentionally excluded here to avoid
     * double-counting.
     */
    private int countRowlessStructuralErrors(List<RowErrorDto> errors) {
        return (int) errors.stream()
                .filter(e -> e.getRowNumber() <= 1)
                .filter(e -> e.getRawData() == null || e.getRawData().isEmpty())
                .count();
    }

    /**
     * Never expose a raw exception message (SQL state, constraint name,
     * stack-trace fragments) to the end user's downloaded error report.
     */
    private String resolveErrorMessage(Exception e) {

        if (e.getCause() != null) {
            return e.getCause().getMessage();
        }

        return e.getMessage() != null
                ? e.getMessage()
                : "Unexpected error occurred";
    }
}
