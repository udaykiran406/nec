package com.nec.middleware.hr.handler;

import com.nec.middleware.bulkUpload.excel.ExcelColumnHeaders;
import com.nec.middleware.bulkUpload.handler.BulkRowValidationException;
import com.nec.middleware.bulkUpload.handler.BulkUploadHandler;
import com.nec.middleware.hr.dto.request.PortalUserRequestDto;
import com.nec.middleware.hr.dto.response.PortalUserResponseDto;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class PortalUserBulkHandler implements BulkUploadHandler<PortalUserRequestDto, PortalUserResponseDto> {

    public static final String MODULE_NAME = "PORTAL_USER";

    private final Validator validator;
    private final PortalUserRowPersister rowPersister;

    @Override
    public String moduleName() {
        return MODULE_NAME;
    }

    @Override
    public List<String> expectedHeaders() {
        return ExcelColumnHeaders.of(PortalUserRequestDto.class);
    }

    @Override
    public PortalUserRequestDto map(Map<String, String> row) {
        log.debug("Mapping row to PortalUserRequestDto: rawName='{}'", row.get("User Name"));

        return PortalUserRequestDto.builder()
                .userName(trim(row.get("User Name")))
                .genderId(parseLong(row.get("Gender ID")))
                .roleId(parseLong(row.get("Role ID")))
                .phone(trim(row.get("Phone")))
                .email(trim(row.get("Email")))
                .photoPath(trim(row.get("Photo Path")))
                .faculty(trim(row.get("Faculty")))
                .regionId(parseLong(row.get("Region ID")))
                .districtId(parseLong(row.get("District ID")))
                .cityId(parseLong(row.get("City ID")))
                .portalUserTypeId(parseLong(row.get("Portal User Type ID")))
                .masterdataId(parseLong(row.get("Master Data ID")))
                .build();
    }

    @Override
    public void validate(PortalUserRequestDto dto) {
        Set<ConstraintViolation<PortalUserRequestDto>> violations = validator.validate(dto);
        if (!violations.isEmpty()) {
            String message = violations.stream()
                    .map(ConstraintViolation::getMessage)
                    .distinct()
                    .collect(Collectors.joining(", "));
            log.warn("Validation failed for portal user row: {}", message);
            throw new BulkRowValidationException(message);
        }
    }

    @Override
    public PortalUserResponseDto persist(PortalUserRequestDto dto) {
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