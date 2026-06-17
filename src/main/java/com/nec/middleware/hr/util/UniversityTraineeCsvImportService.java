package com.nec.middleware.hr.util;

import com.nec.middleware.hr.dto.request.UniversityTraineeRequestDto;
import com.nec.middleware.hr.dto.response.RowErrorDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Parses a bulk-upload file (Excel .xlsx or CSV) into a list of {@link ParsedRow}
 * objects, each pairing a {@link UniversityTraineeRequestDto} with its original
 * raw column values (needed later for the error report file).
 *
 * <h3>Responsibilities</h3>
 * <ul>
 *   <li>Detect file type (xlsx vs csv) from filename / content-type.</li>
 *   <li>Map header labels to column indices (order-independent).</li>
 *   <li>Read each data row into a {@link UniversityTraineeRequestDto} AND
 *       retain its raw column values for error reporting.</li>
 *   <li>Append row-level parse errors (with whatever raw data was readable)
 *       to the supplied {@code errors} list without stopping the whole import.</li>
 * </ul>
 *
 * <h3>What this class does NOT do</h3>
 * <ul>
 *   <li>FK resolution (gender, region, etc.) — done in the service layer.</li>
 *   <li>Business validation (duplicate email/phone) — done in the service layer.</li>
 *   <li>Photo handling — photos are not supported in bulk mode.</li>
 * </ul>
 *
 * <h3>Expected Excel / CSV columns (order-independent, header-matched)</h3>
 * <pre>
 * Full Name | Gender ID | Age | Phone | Email | Payment Method ID |
 * University ID | Semester | Faculty | Region ID | District ID | City ID | Status ID
 * </pre>
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UniversityTraineeCsvImportService {

    private final ExcelParserUtil excel;

    // ------------------------------------------------------------------
    // Column index slots  (logical, not positional — mapped via header)
    // ------------------------------------------------------------------

    private static final int COL_FULL_NAME         = 0;
    private static final int COL_GENDER_ID         = 1;
    private static final int COL_AGE               = 2;
    private static final int COL_PHONE             = 3;
    private static final int COL_EMAIL             = 4;
    private static final int COL_PAYMENT_METHOD_ID = 5;
    private static final int COL_UNIVERSITY_ID     = 6;
    private static final int COL_SEMESTER          = 7;
    private static final int COL_FACULTY           = 8;
    private static final int COL_REGION_ID         = 9;
    private static final int COL_DISTRICT_ID       = 10;
    private static final int COL_CITY_ID           = 11;
    private static final int COL_STATUS         = 12;
    private static final int TOTAL_COLS            = 13;

    /**
     * Display labels, in column order, used both as the canonical header
     * names for {@code rawData} maps and as the header row in the
     * regenerated error report file. Keep this in sync with the COL_* slots
     * above (index-for-index).
     */
    private static final String[] COLUMN_LABELS = {
            "Full Name", "Gender ID", "Age", "Phone", "Email",
            "Payment Method ID", "University ID", "Semester", "Faculty",
            "Region ID", "District ID", "City ID", "Status"
    };

    // ------------------------------------------------------------------
    // Public entry point
    // ------------------------------------------------------------------

    /**
     * Parse the uploaded file and return all successfully-parsed rows,
     * each paired with its raw column values.
     * Row-level parse failures are appended to {@code errors} (with
     * whatever raw data was readable) and excluded from the returned list.
     *
     * @param file   the uploaded .xlsx or .csv file
     * @param errors mutable list; parse errors are appended here
     * @return list of parsed rows ready for the service layer
     */
    public List<ParsedRow> parse(
            MultipartFile file,
            List<RowErrorDto> errors) {

        String filename    = file.getOriginalFilename() == null ? "" : file.getOriginalFilename().toLowerCase();
        String contentType = file.getContentType()     == null ? "" : file.getContentType().toLowerCase();

        boolean isExcel = filename.endsWith(".xlsx")
                || contentType.contains("spreadsheet")
                || contentType.contains("excel");

        return isExcel ? parseExcel(file, errors) : parseCsvText(file, errors);
    }

    /** Column labels in order — exposed so the error-report writer can build a matching header row. */
    public String[] columnLabels() {
        return COLUMN_LABELS;
    }

    // ------------------------------------------------------------------
    // Excel parser
    // ------------------------------------------------------------------

    private List<ParsedRow> parseExcel(
            MultipartFile file,
            List<RowErrorDto> errors) {

        List<ParsedRow> result = new ArrayList<>();

        try (Workbook workbook = new XSSFWorkbook(file.getInputStream())) {

            Sheet sheet = workbook.getSheetAt(0);
            if (sheet == null) {
                errors.add(RowErrorDto.builder().rowNumber(0).message("Excel file has no sheets").build());
                return result;
            }

            Row headerRow = sheet.getRow(0);
            if (headerRow == null) {
                errors.add(RowErrorDto.builder().rowNumber(1).message("Header row is missing").build());
                return result;
            }

            int[] colIdx = buildColIdx(headerRow);

            for (int r = 1; r <= sheet.getLastRowNum(); r++) {
                Row row = sheet.getRow(r);
                if (row == null || excel.isRowEmpty(row)) continue;

                int rowNum = r + 1; // 1-based, header = row 1

                // Read raw values FIRST, independent of DTO mapping, so that
                // even if mapping throws, we still have something to report.
                Map<String, String> rawData = readRawRow(row, colIdx);

                try {
                    UniversityTraineeRequestDto dto = mapRow(row, colIdx, rowNum);
                    if (dto != null) {
                        result.add(new ParsedRow(rowNum, dto, rawData));
                    }
                } catch (Exception e) {
                    log.warn("Excel row {} parse error: {}", rowNum, e.getMessage());
                    errors.add(RowErrorDto.builder()
                            .rowNumber(rowNum)
                            .message(e.getMessage())
                            .rawData(rawData)
                            .build());
                }
            }

        } catch (Exception e) {
            log.error("Failed to read Excel file: {}", e.getMessage(), e);
            errors.add(RowErrorDto.builder().rowNumber(0).message("Failed to read Excel: " + e.getMessage()).build());
        }

        return result;
    }

    // ------------------------------------------------------------------
    // CSV parser
    // ------------------------------------------------------------------

    private List<ParsedRow> parseCsvText(
            MultipartFile file,
            List<RowErrorDto> errors) {

        List<ParsedRow> result = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8))) {

            String headerLine = reader.readLine();
            if (headerLine == null) {
                errors.add(RowErrorDto.builder().rowNumber(0).message("CSV file is empty").build());
                return result;
            }

            String delimiter = headerLine.contains("\t") ? "\t" : ",";
            String[] headers = headerLine.split(delimiter, -1);
            int[] colIdx = defaultColIdx();
            for (int i = 0; i < headers.length; i++) {
                assignHeader(colIdx, excel.normaliseHeader(headers[i]), i);
            }

            String line;
            int rowNum = 1; // header was row 1
            while ((line = reader.readLine()) != null) {
                rowNum++;
                if (line.trim().isEmpty()) continue;

                String[] cols = line.split(delimiter, -1);
                Map<String, String> rawData = readRawCsvRow(cols, colIdx);

                try {
                    UniversityTraineeRequestDto dto = mapCsvRow(cols, colIdx, rowNum);
                    if (dto != null) {
                        result.add(new ParsedRow(rowNum, dto, rawData));
                    }
                } catch (Exception e) {
                    log.warn("CSV row {} parse error: {}", rowNum, e.getMessage());
                    errors.add(RowErrorDto.builder()
                            .rowNumber(rowNum)
                            .message(e.getMessage())
                            .rawData(rawData)
                            .build());
                }
            }

        } catch (Exception e) {
            log.error("Failed to read CSV file: {}", e.getMessage(), e);
            errors.add(RowErrorDto.builder().rowNumber(0).message("Failed to read CSV: " + e.getMessage()).build());
        }

        return result;
    }

    // ------------------------------------------------------------------
    // Raw row capture (for error report regeneration)
    // ------------------------------------------------------------------

    /** Read every column's raw string value for an Excel row, keyed by display label, in column order. */
    private Map<String, String> readRawRow(Row row, int[] colIdx) {
        Map<String, String> raw = new LinkedHashMap<>();
        raw.put(COLUMN_LABELS[COL_FULL_NAME],         excel.cellString(row.getCell(colIdx[COL_FULL_NAME])));
        raw.put(COLUMN_LABELS[COL_GENDER_ID],         excel.cellString(row.getCell(colIdx[COL_GENDER_ID])));
        raw.put(COLUMN_LABELS[COL_AGE],               excel.cellString(row.getCell(colIdx[COL_AGE])));
        raw.put(COLUMN_LABELS[COL_PHONE],             excel.cellString(row.getCell(colIdx[COL_PHONE])));
        raw.put(COLUMN_LABELS[COL_EMAIL],             excel.cellString(row.getCell(colIdx[COL_EMAIL])));
        raw.put(COLUMN_LABELS[COL_PAYMENT_METHOD_ID], excel.cellString(row.getCell(colIdx[COL_PAYMENT_METHOD_ID])));
        raw.put(COLUMN_LABELS[COL_UNIVERSITY_ID],     excel.cellString(row.getCell(colIdx[COL_UNIVERSITY_ID])));
        raw.put(COLUMN_LABELS[COL_SEMESTER],          excel.cellString(row.getCell(colIdx[COL_SEMESTER])));
        raw.put(COLUMN_LABELS[COL_FACULTY],           excel.cellString(row.getCell(colIdx[COL_FACULTY])));
        raw.put(COLUMN_LABELS[COL_REGION_ID],         excel.cellString(row.getCell(colIdx[COL_REGION_ID])));
        raw.put(COLUMN_LABELS[COL_DISTRICT_ID],       excel.cellString(row.getCell(colIdx[COL_DISTRICT_ID])));
        raw.put(COLUMN_LABELS[COL_CITY_ID],           excel.cellString(row.getCell(colIdx[COL_CITY_ID])));
        raw.put(COLUMN_LABELS[COL_STATUS],         excel.cellString(row.getCell(colIdx[COL_STATUS])));
        return raw;
    }

    /** Read every column's raw string value for a CSV row, keyed by display label, in column order. */
    private Map<String, String> readRawCsvRow(String[] cols, int[] colIdx) {
        Map<String, String> raw = new LinkedHashMap<>();
        raw.put(COLUMN_LABELS[COL_FULL_NAME],         safeGet(cols, colIdx[COL_FULL_NAME]));
        raw.put(COLUMN_LABELS[COL_GENDER_ID],         safeGet(cols, colIdx[COL_GENDER_ID]));
        raw.put(COLUMN_LABELS[COL_AGE],               safeGet(cols, colIdx[COL_AGE]));
        raw.put(COLUMN_LABELS[COL_PHONE],             safeGet(cols, colIdx[COL_PHONE]));
        raw.put(COLUMN_LABELS[COL_EMAIL],             safeGet(cols, colIdx[COL_EMAIL]));
        raw.put(COLUMN_LABELS[COL_PAYMENT_METHOD_ID], safeGet(cols, colIdx[COL_PAYMENT_METHOD_ID]));
        raw.put(COLUMN_LABELS[COL_UNIVERSITY_ID],     safeGet(cols, colIdx[COL_UNIVERSITY_ID]));
        raw.put(COLUMN_LABELS[COL_SEMESTER],          safeGet(cols, colIdx[COL_SEMESTER]));
        raw.put(COLUMN_LABELS[COL_FACULTY],           safeGet(cols, colIdx[COL_FACULTY]));
        raw.put(COLUMN_LABELS[COL_REGION_ID],         safeGet(cols, colIdx[COL_REGION_ID]));
        raw.put(COLUMN_LABELS[COL_DISTRICT_ID],       safeGet(cols, colIdx[COL_DISTRICT_ID]));
        raw.put(COLUMN_LABELS[COL_CITY_ID],           safeGet(cols, colIdx[COL_CITY_ID]));
        raw.put(COLUMN_LABELS[COL_STATUS],         safeGet(cols, colIdx[COL_STATUS]));
        return raw;
    }

    // ------------------------------------------------------------------
    // Row → DTO mapping
    // ------------------------------------------------------------------

    /** Map one Excel {@link Row} into a {@link UniversityTraineeRequestDto}. */
    private UniversityTraineeRequestDto mapRow(Row row, int[] colIdx, int rowNum) {

        String fullName = excel.cellString(row.getCell(colIdx[COL_FULL_NAME]));
        if (fullName == null || fullName.isBlank()) {
            log.warn("Row {}: fullName is empty – skipping", rowNum);
            return null;
        }

        return UniversityTraineeRequestDto.builder()
                .fullName(fullName)
                .genderId(excel.cellLong(row.getCell(colIdx[COL_GENDER_ID])))
                .age(excel.cellShort(row.getCell(colIdx[COL_AGE])))
                .phone(excel.cellString(row.getCell(colIdx[COL_PHONE])))
                .email(excel.cellString(row.getCell(colIdx[COL_EMAIL])))
                .paymentMethodId(excel.cellLong(row.getCell(colIdx[COL_PAYMENT_METHOD_ID])))
                .universityId(excel.cellLong(row.getCell(colIdx[COL_UNIVERSITY_ID])))
                .semester(excel.cellString(row.getCell(colIdx[COL_SEMESTER])))
                .faculty(excel.cellString(row.getCell(colIdx[COL_FACULTY])))
                .regionId(excel.cellLong(row.getCell(colIdx[COL_REGION_ID])))
                .districtId(excel.cellLong(row.getCell(colIdx[COL_DISTRICT_ID])))
                .cityId(excel.cellLong(row.getCell(colIdx[COL_CITY_ID])))
//                .statusId(excel.cellLong(row.getCell(colIdx[COL_STATUS_ID])))
                .build();
    }

    /** Map one CSV String[] into a {@link UniversityTraineeRequestDto}. */
    private UniversityTraineeRequestDto mapCsvRow(String[] cols, int[] colIdx, int rowNum) {

        String fullName = safeGet(cols, colIdx[COL_FULL_NAME]);
        if (fullName == null || fullName.isBlank()) {
            log.warn("Row {}: fullName is empty – skipping", rowNum);
            return null;
        }

        return UniversityTraineeRequestDto.builder()
                .fullName(fullName)
                .genderId(parseLong(safeGet(cols, colIdx[COL_GENDER_ID])))
                .age(parseShort(safeGet(cols, colIdx[COL_AGE])))
                .phone(safeGet(cols, colIdx[COL_PHONE]))
                .email(safeGet(cols, colIdx[COL_EMAIL]))
                .paymentMethodId(parseLong(safeGet(cols, colIdx[COL_PAYMENT_METHOD_ID])))
                .universityId(parseLong(safeGet(cols, colIdx[COL_UNIVERSITY_ID])))
                .semester(safeGet(cols, colIdx[COL_SEMESTER]))
                .faculty(safeGet(cols, colIdx[COL_FACULTY]))
                .regionId(parseLong(safeGet(cols, colIdx[COL_REGION_ID])))
                .districtId(parseLong(safeGet(cols, colIdx[COL_DISTRICT_ID])))
                .cityId(parseLong(safeGet(cols, colIdx[COL_CITY_ID])))
//                .status(parseLong(safeGet(cols, colIdx[COL_STATUS])))
                .build();
    }

    // ------------------------------------------------------------------
    // Header mapping
    // ------------------------------------------------------------------

    /**
     * Walk the Excel header row and build a column-index array.
     * Column order in the file does NOT matter — headers are matched by keyword.
     */
    private int[] buildColIdx(Row headerRow) {
        int[] idx = defaultColIdx();
        for (int c = 0; c < headerRow.getLastCellNum(); c++) {
            Cell cell = headerRow.getCell(c);
            if (cell == null) continue;
            assignHeader(idx, excel.normaliseHeader(excel.cellString(cell)), c);
        }
        return idx;
    }

    /**
     * Map a single normalised header label to its logical column slot.
     *
     * <p>Accepted header labels (case-insensitive):
     * <pre>
     * COL_FULL_NAME         → "full name", "fullname", "name"
     * COL_GENDER_ID         → "gender id", "gender_id", "genderid"
     * COL_AGE               → "age"
     * COL_PHONE             → "phone", "mobile", "phone number"
     * COL_EMAIL             → "email", "e-mail", "email address"
     * COL_PAYMENT_METHOD_ID → "payment method id", "payment_method_id", "paymentmethodid"
     * COL_UNIVERSITY_ID     → "university id", "university_id", "universityid"
     * COL_SEMESTER          → "semester"
     * COL_FACULTY           → "faculty", "department"
     * COL_REGION_ID         → "region id", "region_id", "regionid"
     * COL_DISTRICT_ID       → "district id", "district_id", "districtid"
     * COL_CITY_ID           → "city id", "city_id", "cityid"
     * COL_STATUS_ID         → "status id", "status_id", "statusid"
     * </pre>
     */
    private void assignHeader(int[] idx, String h, int c) {
        if (h == null) return;
        if (h.equals("full name") || h.equals("fullname") || h.equals("name"))              idx[COL_FULL_NAME]         = c;
        else if (h.equals("gender id") || h.equals("gender_id") || h.equals("genderid"))    idx[COL_GENDER_ID]         = c;
        else if (h.equals("age"))                                                             idx[COL_AGE]               = c;
        else if (h.equals("phone") || h.contains("mobile") || h.contains("phone number"))   idx[COL_PHONE]             = c;
        else if (h.equals("email") || h.equals("e-mail") || h.contains("email address"))    idx[COL_EMAIL]             = c;
        else if (h.contains("payment method"))                                               idx[COL_PAYMENT_METHOD_ID] = c;
        else if (h.contains("university"))                                                   idx[COL_UNIVERSITY_ID]     = c;
        else if (h.equals("semester"))                                                       idx[COL_SEMESTER]          = c;
        else if (h.equals("faculty") || h.equals("department"))                             idx[COL_FACULTY]           = c;
        else if (h.contains("region"))                                                       idx[COL_REGION_ID]         = c;
        else if (h.contains("district"))                                                     idx[COL_DISTRICT_ID]       = c;
        else if (h.contains("city"))                                                         idx[COL_CITY_ID]           = c;
        else if (h.contains("status"))                                                       idx[COL_STATUS]         = c;
    }

    /** Default: column slot i maps to physical column i (positional fallback). */
    private int[] defaultColIdx() {
        int[] idx = new int[TOTAL_COLS];
        for (int i = 0; i < TOTAL_COLS; i++) idx[i] = i;
        return idx;
    }

    // ------------------------------------------------------------------
    // CSV helpers
    // ------------------------------------------------------------------

    private String safeGet(String[] cols, int idx) {
        if (idx < 0 || idx >= cols.length) return null;
        String v = cols[idx].trim();
        return v.isEmpty() ? null : v;
    }

    private Long parseLong(String v) {
        if (v == null) return null;
        try { return Long.parseLong(v.trim()); }
        catch (NumberFormatException e) { return null; }
    }

    private Short parseShort(String v) {
        if (v == null) return null;
        try { return Short.parseShort(v.trim()); }
        catch (NumberFormatException e) { return null; }
    }
}
