package com.nec.middleware.rbacAuth.rbac.handler;

import com.nec.middleware.bulkUpload.excel.ExcelColumnHeaders;
import com.nec.middleware.bulkUpload.handler.BulkRowValidationException;
import com.nec.middleware.bulkUpload.handler.BulkUploadHandler;
import com.nec.middleware.rbacAuth.rbac.dto.request.RbacUserBulkUploadDto;
import com.nec.middleware.rbacAuth.rbac.dto.response.RbacUserResponse;
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
 * Bulk upload strategy for RbacUser. This is the ONLY class
 * that knows RbacUser's Excel headers, how a row maps to its
 * DTO, and how it's validated and persisted — none of that leaks
 * into the generic engine, parser, registry, or controller.
 *
 * <p>Headers below match {@code @ExcelColumn} names declared on
 * {@link RbacUserBulkUploadDto}, so this handler's
 * {@link #expectedHeaders()} stays consistent with the auto-generated
 * Excel import template without duplicating literal strings by hand.
 *
 * <p>Unlike regular user creation (which requires password), bulk-uploaded
 * users get temporary auto-generated passwords via Keycloak's admin API.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class RbacUserBulkHandler implements BulkUploadHandler<RbacUserBulkUploadDto, RbacUserResponse> {

    public static final String MODULE_NAME = "RBAC_USER";

    private final Validator validator;
    private final RbacUserBulkRowPersister rowPersister;

    @Override
    public String moduleName() {
        return MODULE_NAME;
    }

    @Override
    public List<String> expectedHeaders() {
        // Derived from @ExcelColumn on RbacUserBulkUploadDto so the handler
        // can never drift out of sync with the auto-generated import template
        return ExcelColumnHeaders.of(RbacUserBulkUploadDto.class);
    }

    /**
     * Map raw columns to the DTO. Deliberately permissive about blank/
     * unparseable values — they become {@code null} on the DTO rather than
     * throwing, so {@link #validate} is the single place that reports every
     * problem with a row (missing required field, bad email format, etc.)
     * in one pass instead of failing on the first unparseable cell.
     *
     * <p>IMPORTANT: Optional fields with defaults (isActive, passwordToBeChanged)
     * are handled specially to ensure they never become null, since Lombok's
     * @Builder.Default doesn't apply when the builder is explicitly passed null.
     */
    @Override
    public RbacUserBulkUploadDto map(Map<String, String> row) {
        log.debug("Mapping row to RbacUserBulkUploadDto: userName='{}'", row.get("User Name"));

        // For optional fields with defaults, use null-safe parsing
        Integer isActive = parseInteger(row.get("Is Active"));
        if (isActive == null) {
            isActive = 1;  // Default: active
        }

        Boolean passwordToBeChanged = parseBoolean(row.get("Password To Be Changed"));
        if (passwordToBeChanged == null) {
            passwordToBeChanged = false;  // Default: don't force password change (unless set to true)
        }

        return RbacUserBulkUploadDto.builder()
                .userName(trim(row.get("User Name")))
                .genderId(parseLong(row.get("Gender ID")))
                .roleId(parseLong(row.get("Role ID")))
                .phone(trim(row.get("Phone")))
                .email(trim(row.get("Email")))
                .photoPath(trim(row.get("Photo Path")))
                .departmentId(parseLong(row.get("Department ID")))
                .regionId(parseLong(row.get("Region ID")))
                .districtId(parseLong(row.get("District ID")))
                .cityId(parseLong(row.get("City ID")))
                .passwordToBeChanged(passwordToBeChanged)
                .isActive(isActive)
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
    public void validate(RbacUserBulkUploadDto dto) {
        Set<ConstraintViolation<RbacUserBulkUploadDto>> violations = validator.validate(dto);
        if (!violations.isEmpty()) {
            String message = violations.stream()
                    .map(ConstraintViolation::getMessage)
                    .distinct()
                    .collect(Collectors.joining(", "));
            log.warn("Validation failed for RbacUser bulk row: {}", message);
            throw new BulkRowValidationException(message);
        }
    }

    /**
     * Delegates to {@link RbacUserBulkRowPersister}, which owns the
     * {@code REQUIRES_NEW} transaction, Keycloak integration, FK resolution,
     * duplicate check, and password generation.
     */
    @Override
    public RbacUserResponse persist(RbacUserBulkUploadDto dto) {
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

    private Integer parseInteger(String v) {
        String t = trim(v);
        if (t == null) return null;
        try {
            return Integer.parseInt(t);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private Boolean parseBoolean(String v) {
        String t = trim(v);
        if (t == null) return null;
        return "true".equalsIgnoreCase(t) || "1".equals(t) || "yes".equalsIgnoreCase(t);
    }
}

