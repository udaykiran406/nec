package com.nec.middleware.bulkUpload.excel;

import com.nec.middleware.bulkUpload.dto.RowErrorDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * CORE bulk-upload component. Reads an {@code .xlsx} file and turns it into
 * plain {@code Map<String, String>} rows, keyed by header label, in column
 * order.
 *
 * <p>This class is the single Excel-reading entry point for every module in
 * the bulk upload framework (UniversityTrainee, Employee, Student, Vendor,
 * Contractor, ...). It is intentionally dumb:
 *
 * <ul>
 *   <li>NO knowledge of any DTO class.</li>
 *   <li>NO validation beyond "is this a readable .xlsx with a header row".</li>
 *   <li>NO database access.</li>
 *   <li>NO business rules of any kind.</li>
 * </ul>
 *
 * <p>Everything module-specific (which headers are expected, how a row maps
 * to a DTO, what counts as valid) lives in that module's
 * {@link com.nec.middleware.bulkUpload.handler.BulkUploadHandler} — never
 * here. This is what lets a new module be added with zero changes to this
 * class.
 *
 * <h3>Header normalization</h3>
 * Uploaded files routinely have header text that differs from the expected
 * header only in casing or whitespace — {@code "full name"} vs
 * {@code "Full Name"} vs {@code "Full  Name "}. Treating these as different
 * columns would silently drop the column's data (it just wouldn't match any
 * key the handler looks up), so headers are normalized for <b>matching</b>
 * purposes (trim, collapse internal whitespace, lowercase) against the
 * handler's {@code expectedHeaders()} list. When a normalized match is
 * found, the row map is keyed by the handler's canonical header text — not
 * by whatever casing/spacing happened to be in the uploaded file — so
 * {@code map()} implementations can always do {@code row.get("Full Name")}
 * regardless of how the uploader typed their header row. Unrecognized
 * columns (no normalized match in {@code expectedHeaders()}) are kept under
 * their original (trimmed) label, so handlers that don't pre-declare every
 * possible column still see that data if they look for it.
 *
 * <p>Reuses the same low-level cell-reading conventions as the legacy
 * {@code ExcelParserUtil} (numeric whole numbers rendered without a
 * trailing ".0", dates as {@code yyyy-MM-dd}, blank cells as {@code null}).
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class GenericExcelParser {

    /**
     * Parse {@code file} into one {@link Map} per data row, keyed by header
     * label. Each uploaded header is matched against {@code expectedHeaders}
     * case-/whitespace-insensitively; on a match, the row is keyed by the
     * canonical text from {@code expectedHeaders} rather than the file's own
     * casing — see class javadoc.
     *
     * <p>Row 1 is always treated as the header row. Rows 2..N are data
     * rows. Fully blank rows are skipped silently (not reported as errors).
     *
     * <p>Structural failures (no sheet, no header row, unreadable file) are
     * reported as {@link RowErrorDto} entries with {@code rowNumber 0} or
     * {@code 1} rather than thrown, so the caller can still return a
     * well-formed {@code BulkUploadResultDto} instead of a raw 500.
     *
     * @param file             the uploaded {@code .xlsx} file
     * @param expectedHeaders  canonical header labels from the handler
     *                         (e.g. {@code handler.expectedHeaders()}), used
     *                         to normalize the uploaded file's header casing/
     *                         whitespace back to the handler's expected text
     * @param errors           mutable list that structural parse errors are appended to
     * @return ordered list of {@link ParsedExcelRow}, one per non-blank data row
     */
    public List<ParsedExcelRow> parse(
            MultipartFile file, List<String> expectedHeaders, List<RowErrorDto> errors) {

        rejectNonExcel(file, errors);
        if (!errors.isEmpty()) {
            return List.of();
        }

        List<ParsedExcelRow> result = new ArrayList<>();
        Map<String, String> normalizedToCanonical = buildNormalizedLookup(expectedHeaders);

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

            List<String> headers = readHeaderRow(headerRow, normalizedToCanonical);
            if (headers.isEmpty()) {
                errors.add(RowErrorDto.builder().rowNumber(1).message("Header row has no columns").build());
                return result;
            }

            for (int r = 1; r <= sheet.getLastRowNum(); r++) {
                Row row = sheet.getRow(r);
                if (row == null || isRowEmpty(row)) {
                    continue;
                }

                int rowNumber = r + 1; // 1-based; header occupies row 1
                Map<String, String> rawData = readDataRow(row, headers);
                result.add(new ParsedExcelRow(rowNumber, rawData));
            }

        } catch (IOException e) {
            log.error("Failed to read uploaded Excel file: {}", e.getMessage(), e);
            errors.add(RowErrorDto.builder()
                    .rowNumber(0)
                    .message("Could not read Excel file: " + e.getMessage())
                    .build());
        }

        return result;
    }

    /**
     * Convenience overload for callers with no canonical header list to
     * normalize against (e.g. ad-hoc inspection tooling) — headers are kept
     * exactly as read, trimmed only. Bulk upload handlers should use the
     * 3-arg overload so header matching is resilient to casing/whitespace.
     */
    public List<ParsedExcelRow> parse(MultipartFile file, List<RowErrorDto> errors) {
        return parse(file, List.of(), errors);
    }

    // ------------------------------------------------------------------
    // Excel-only enforcement
    // ------------------------------------------------------------------

    private void rejectNonExcel(MultipartFile file, List<RowErrorDto> errors) {
        if (file == null || file.isEmpty()) {
            errors.add(RowErrorDto.builder().rowNumber(0).message("No file was uploaded").build());
            return;
        }

        String filename = file.getOriginalFilename() == null ? "" : file.getOriginalFilename().toLowerCase();
        if (!filename.endsWith(".xlsx")) {
            errors.add(RowErrorDto.builder()
                    .rowNumber(0)
                    .message("Only .xlsx Excel files are supported")
                    .build());
        }
    }

    // ------------------------------------------------------------------
    // Header normalization
    // ------------------------------------------------------------------

    /**
     * Builds a lookup from normalized header text (trimmed, internal
     * whitespace collapsed, lowercased) to the canonical text as declared
     * by the handler. E.g. {@code "full  name"} and {@code "FULL NAME"}
     * both map to whatever exact string the handler put in
     * {@code expectedHeaders()}, e.g. {@code "Full Name"}.
     */
    private Map<String, String> buildNormalizedLookup(List<String> expectedHeaders) {
        Map<String, String> lookup = new LinkedHashMap<>();
        for (String canonical : expectedHeaders) {
            lookup.put(normalize(canonical), canonical);
        }
        return lookup;
    }

    /** Trim, strip a trailing "required" marker (e.g. " *"), collapse internal whitespace, lowercase. */
    private String normalize(String header) {
        if (header == null) return "";
       return header.trim()
                .replaceAll("\\s*\\*\\s*$", "")   // drop a trailing " *" required-marker
                .replaceAll("\\s+", " ")
                .toLowerCase();
    }

    // ------------------------------------------------------------------
    // Header / row reading
    // ------------------------------------------------------------------

    /**
     * Reads the header row and resolves each cell's label to its canonical
     * form via {@code normalizedToCanonical}. A header that doesn't match
     * any expected header (e.g. an extra column the handler doesn't declare)
     * is kept under its own trimmed text rather than dropped, so it still
     * shows up in {@code rawData} for handlers that want to read it anyway.
     */
    private List<String> readHeaderRow(Row headerRow, Map<String, String> normalizedToCanonical) {
        List<String> headers = new ArrayList<>();
        for (int c = 0; c < headerRow.getLastCellNum(); c++) {
            Cell cell = headerRow.getCell(c);
            String rawLabel = cellString(cell);

            if (rawLabel == null) {
                // Preserve column position even for an unlabeled column so
                // data rows still line up positionally; nothing to key it
                // by, so it's dropped from rawData in readDataRow.
                headers.add("");
                continue;
            }

            String canonical = normalizedToCanonical.get(normalize(rawLabel));
           headers.add(canonical != null ? canonical : rawLabel.trim());
        }
        return headers;
    }

    private Map<String, String> readDataRow(Row row, List<String> headers) {
        Map<String, String> raw = new LinkedHashMap<>();
        for (int c = 0; c < headers.size(); c++) {
            String header = headers.get(c);
            if (header.isEmpty()) continue; // unlabeled column, nothing to key it by
            raw.put(header, cellString(row.getCell(c)));
        }
        return raw;
    }

    private boolean isRowEmpty(Row row) {
        for (int c = row.getFirstCellNum(); c < row.getLastCellNum(); c++) {
            Cell cell = row.getCell(c);
            String v = cellString(cell);
            if (v != null && !v.isBlank()) {
                return false;
            }
        }
        return true;
    }

    /**
     * Read any cell type as a trimmed String. Whole-number numeric cells
     * render without a trailing ".0" (so an ID column reads "5" not "5.0").
     * Date-formatted numeric cells render as {@code yyyy-MM-dd}. Blank
     * cells return {@code null}.
     */
    private String cellString(Cell cell) {
        if (cell == null) return null;
        switch (cell.getCellType()) {
            case STRING -> {
                String s = cell.getStringCellValue().trim();
                return s.isEmpty() ? null : s;
            }
            case NUMERIC -> {
                if (org.apache.poi.ss.usermodel.DateUtil.isCellDateFormatted(cell)) {
                    return cell.getLocalDateTimeCellValue().toLocalDate().toString();
                }
                double d = cell.getNumericCellValue();
                return d == Math.floor(d) ? String.valueOf((long) d) : String.valueOf(d);
            }
            case BOOLEAN -> {
                return String.valueOf(cell.getBooleanCellValue());
            }
            case FORMULA -> {
                try {
                    return cell.getStringCellValue().trim();
                } catch (Exception e) {
                    double fd = cell.getNumericCellValue();
                    return fd == Math.floor(fd) ? String.valueOf((long) fd) : String.valueOf(fd);
                }
            }
            default -> {
                return null;
            }
        }
    }
}