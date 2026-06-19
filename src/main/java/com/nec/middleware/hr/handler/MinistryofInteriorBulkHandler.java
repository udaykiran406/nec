package com.nec.middleware.hr.handler;


import com.nec.middleware.bulkUpload.excel.ExcelColumnHeaders;
import com.nec.middleware.bulkUpload.handler.BulkRowValidationException;
import com.nec.middleware.bulkUpload.handler.BulkUploadHandler;
import com.nec.middleware.hr.dto.request.MinistryofInteriorRequestDto;
import com.nec.middleware.hr.dto.response.MinistryofInteriorResponseDto;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Bulk upload strategy for Ministry of Interior. This is the ONLY class
 * that knows Ministry of Interior's Excel headers, how a row maps to its
 * DTO, and how it's validated and persisted — none of that leaks into the
 * generic engine, parser, registry, or controller.
 *
 * <p>Headers below match {@code @ExcelColumn} names declared on
 * {@link MinistryofInteriorRequestDto}, so this handler's
 * {@link #expectedHeaders()} stays consistent with the auto-generated
 * Excel import template without duplicating the literal strings by hand —
 * see {@link #expectedHeaders()} for how that's derived via reflection.

 */
@Slf4j
@Component
@RequiredArgsConstructor
public class MinistryofInteriorBulkHandler implements BulkUploadHandler<MinistryofInteriorRequestDto, MinistryofInteriorResponseDto> {

    public static final String MODULE_NAME = "MINISTRY_OF_INTERIOR";

    private final Validator validator;
    private final MinistryofInteriorRowPersister rowPersister;

    @Override
    public String moduleName() {
        return MODULE_NAME;
    }

    @Override
    public List<String> expectedHeaders() {
        // Derived from @ExcelColumn so the handler can never drift out of
        // sync with the auto-generated import template — one annotation,
        // two consumers (ExcelTemplateService for generation, this for
        // validation), no hand-copied header strings to keep in sync.
        return ExcelColumnHeaders.of(MinistryofInteriorRequestDto.class);
    }

    /**
     * Map raw columns to the DTO. Deliberately permissive about blank/
     * unparseable values — they become {@code null} on the DTO rather than
     * throwing, so {@link #validate} is the single place that reports every
     * problem with a row (missing required field, bad email format, etc.)
     * in one pass instead of failing on the first unparseable cell.
     */
    @Override
    public MinistryofInteriorRequestDto map(Map<String, String> row) {
        log.debug("Mapping row to MinistryofInteriorRequestDto: rawName='{}'", row.get("Full Name"));

        return MinistryofInteriorRequestDto.builder()
                .moiTitleId(parseLong(row.get("Title Type")))
                .Name(trim(row.get("Full Name")))
                .genderId(parseLong(row.get("Gender ID")))
                .age(parseShort(row.get("Age")))
                .phone(trim(row.get("Phone")))
                .photoPath(trim(row.get("Photo Path")))
                .email(trim(row.get("Email")))
                .regionId(parseLong(row.get("Region ID")))
                .districtId(parseLong(row.get("District ID")))
                .cityId(parseLong(row.get("City ID")))
                .vrcId(parseLong(row.get("VRC ID")))
                .build();
    }

    /**
     * Bean validation first (covers every {@code @NotNull}/{@code @NotBlank}/
     * {@code @Email}/{@code @Size}/{@code @Pattern} on the DTO — including
     * unparseable numeric columns, since those already came through as
     * {@code null} from {@link #map} and trip {@code @NotNull}). Business
     * validation (duplicate email/phone) runs after, since there's no point
     * hitting the database for a row that's already structurally invalid.
     */
    @Override
    public void validate(MinistryofInteriorRequestDto dto) {
        Set<ConstraintViolation<MinistryofInteriorRequestDto>> violations = validator.validate(dto);
        if (!violations.isEmpty()) {
            String message = violations.stream()
                    .map(ConstraintViolation::getMessage)
                    .distinct()
                    .collect(Collectors.joining(", "));
            log.warn("Validation failed for ministry of interior row: {}", message);
            throw new BulkRowValidationException(message);
        }
    }

    /**
     * Delegates to {@link MinistryofInteriorRowPersister}, which owns the
     * {@code REQUIRES_NEW} transaction, FK resolution, duplicate check, and
     * ID generation. {@code DuplicateResourceException} and
    **/
    @Override
    public MinistryofInteriorResponseDto persist(MinistryofInteriorRequestDto dto) {
        return rowPersister.persistSingleRow(dto);
    }

    // ------------------------------------------------------------------
    // Cell parsing helpers
    // ------------------------------------------------------------------

    private String trim(String v) {
        if (v == null) return null;
        String t = v.trim();
        return t.isEmpty() ? null : t;
    }

    private Long parseLong(String v) {
        String t = trim(v);
        if (t == null) return null;
        try {
            return Long.parseLong(t);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private Short parseShort(String v) {
        String t = trim(v);
        if (t == null) return null;
        try {
            return Short.parseShort(t);
        } catch (NumberFormatException e) {
            return null;
        }
    }
}