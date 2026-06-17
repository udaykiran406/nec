package com.nec.middleware.hr.util;

import com.nec.middleware.hr.dto.request.UniversityTraineeRequestDto;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Map;

/**
 * Internal carrier produced by {@link UniversityTraineeCsvImportService}.
 *
 * <p>Bundles a successfully-parsed {@link UniversityTraineeRequestDto} together
 * with the original raw column values (header label → raw string) and the
 * 1-based file row number. The service layer needs {@code rawData} so that,
 * if this row later fails validation or FK resolution during save, the
 * failure can be reported in the error report file with the user's original
 * input intact.
 */
@Getter
@AllArgsConstructor
public class ParsedRow {

    private final int rowNumber;
    private final UniversityTraineeRequestDto dto;
    private final Map<String, String> rawData;
}
