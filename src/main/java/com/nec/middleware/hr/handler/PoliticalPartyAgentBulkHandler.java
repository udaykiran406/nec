package com.nec.middleware.hr.handler;


import com.nec.middleware.bulkUpload.excel.ExcelColumnHeaders;
import com.nec.middleware.bulkUpload.handler.BulkRowValidationException;
import com.nec.middleware.bulkUpload.handler.BulkUploadHandler;
import com.nec.middleware.hr.dto.request.PoliticalPartyAgentRequestDto;
import com.nec.middleware.hr.dto.response.PoliticalPartyAgentResponseDto;
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
 * Bulk upload strategy for Political Party Agent. This is the ONLY class
 * that knows Political Party Agent's Excel headers, how a row maps to its
 * DTO, and how it's validated and persisted — none of that leaks into the
 * generic engine, parser, registry, or controller.
 *
 * <p>Headers below match {@code @ExcelColumn} names declared on
 * {@link PoliticalPartyAgentRequestDto}, so this handler's
 * {@link #expectedHeaders()} stays consistent with the auto-generated
 * Excel import template without duplicating the literal strings by hand —
 * see {@link #expectedHeaders()} for how that's derived via reflection.
 *
 * <p>Mirrors {@code UniversityTraineeBulkHandler}, split cleanly along the
 * {@code map / validate / persist} seams.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class PoliticalPartyAgentBulkHandler implements BulkUploadHandler<PoliticalPartyAgentRequestDto, PoliticalPartyAgentResponseDto> {

    public static final String MODULE_NAME = "POLITICAL_PARTY_AGENT";

    private final Validator validator;
    private final PoliticalPartyAgentRowPersister rowPersister;

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
        return ExcelColumnHeaders.of(PoliticalPartyAgentRequestDto.class);
    }

    /**
     * Map raw columns to the DTO. Deliberately permissive about blank/
     * unparseable values — they become {@code null} on the DTO rather than
     * throwing, so {@link #validate} is the single place that reports every
     * problem with a row (missing required field, bad email format, etc.)
     * in one pass instead of failing on the first unparseable cell.
     */
    @Override
    public PoliticalPartyAgentRequestDto map(Map<String, String> row) {
        log.debug("Mapping row to PoliticalPartyAgentRequestDto: rawName='{}'", row.get("Agent Name"));

        return PoliticalPartyAgentRequestDto.builder()
                .politicalPartyNameId(parseLong(row.get("Political Party ID")))
                .agentName(trim(row.get("Agent Name")))
                .genderId(parseLong(row.get("Gender ID")))
                .phone(trim(row.get("Phone")))
                .email(trim(row.get("Email")))
                .photoPath(trim(row.get("Photo Path")))
                .pollingStationId(parseLong(row.get("Polling Station ID")))
                .regionId(parseLong(row.get("Region ID")))
                .districtId(parseLong(row.get("District ID")))
                .cityId(parseLong(row.get("City ID")))
                .build();
    }

    /**
     * Bean validation first (covers every {@code @NotNull}/{@code @NotBlank}/
     * {@code @Email}/{@code @Size}/{@code @Pattern} on the DTO — including
     * unparseable numeric columns, since those already came through as
     * {@code null} from {@link #map} and trip {@code @NotNull}). Business
     * validation (duplicate email/phone) runs after, since there's no point
     * hitting the database for a row that's already structurally invalid.
     *
     * <p>FK existence is intentionally NOT checked here — it's checked
     * inside {@link PoliticalPartyAgentRowPersister#persistSingleRow} as
     * part of the same {@code REQUIRES_NEW} transaction that resolves and
     * sets those FKs, so there's exactly one round-trip per FK instead of
     * two (one to check, one to resolve).
     */
    @Override
    public void validate(PoliticalPartyAgentRequestDto dto) {
        Set<ConstraintViolation<PoliticalPartyAgentRequestDto>> violations = validator.validate(dto);
        if (!violations.isEmpty()) {
            String message = violations.stream()
                    .map(ConstraintViolation::getMessage)
                    .distinct()
                    .collect(Collectors.joining(", "));
            log.warn("Validation failed for political party agent row: {}", message);

            throw new BulkRowValidationException(message);
        }
    }

    /**
     * Delegates to {@link PoliticalPartyAgentRowPersister}, which owns the
     * {@code REQUIRES_NEW} transaction, FK resolution, duplicate check, and
     * ID generation. {@code DuplicateResourceException} and
     * {@code ResourceNotFoundException} thrown from there propagate up to
     * the generic engine, which already knows how to turn any exception
     * into a {@code RowErrorDto} without leaking internals — there's no
     * need to catch and rewrap them as {@code BulkRowValidationException}
     * here too.
     */
    @Override
    public PoliticalPartyAgentResponseDto persist(PoliticalPartyAgentRequestDto dto) {
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
}