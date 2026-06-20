package com.nec.middleware.hr.handler;

import com.nec.middleware.bulkUpload.excel.ExcelColumnHeaders;
import com.nec.middleware.bulkUpload.handler.BulkRowValidationException;
import com.nec.middleware.bulkUpload.handler.BulkUploadHandler;
import com.nec.middleware.hr.dto.request.TemporaryContractRequestDto;
import com.nec.middleware.hr.dto.response.TemporaryContractResponseDto;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class TemporaryContractBulkHandler implements BulkUploadHandler<TemporaryContractRequestDto, TemporaryContractResponseDto> {

    public static final String MODULE_NAME = "TEMPORARY_CONTRACT";

    private final Validator validator;
    private final TemporaryContractRowPersister rowPersister;

    @Override
    public String moduleName() {
        return MODULE_NAME;
    }

    @Override
    public List<String> expectedHeaders() {
        return ExcelColumnHeaders.of(TemporaryContractRequestDto.class);
    }

    @Override
    public TemporaryContractRequestDto map(Map<String, String> row) {
        log.debug("Mapping row to TemporaryContractRequestDto: employerName='{}'", row.get("Employer Name"));

        return TemporaryContractRequestDto.builder()
                .employerName(trim(row.get("Employer Name")))
                .employeeName(trim(row.get("Employee Name")))
                .contactNo(trim(row.get("Contact No")))
                .contractTypeId(parseLong(row.get("Contract Type ID")))
                .startDate(parseDate(row.get("Start Date")))
                .endDate(parseDate(row.get("End Date")))
                .totalContractAmount(parseBigDecimal(row.get("Total Contract Amount")))
                .initialPaymentPercentage(parseBigDecimal(row.get("Initial Payment Percentage")))
                .remainingPercentage(parseBigDecimal(row.get("Remaining Percentage")))
                .jobDescription(trim(row.get("Job Description")))
                .termsAndConditions(trim(row.get("Terms and Conditions")))
                .status("DRAFT")
                .build();
    }

    @Override
    public void validate(TemporaryContractRequestDto dto) {
        Set<ConstraintViolation<TemporaryContractRequestDto>> violations = validator.validate(dto);
        if (!violations.isEmpty()) {
            String message = violations.stream()
                    .map(ConstraintViolation::getMessage)
                    .distinct()
                    .collect(Collectors.joining(", "));
            log.warn("Validation failed for temporary contract row: {}", message);
            throw new BulkRowValidationException(message);
        }
    }

    @Override
    public TemporaryContractResponseDto persist(TemporaryContractRequestDto dto) {
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

    private BigDecimal parseBigDecimal(String v) {
        String t = trim(v);
        if (t == null) return null;
        try {
            return new BigDecimal(t);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private LocalDate parseDate(String v) {
        String t = trim(v);
        if (t == null) return null;
        try {
            return LocalDate.parse(t);
        } catch (Exception e) {
            return null;
        }
    }
}