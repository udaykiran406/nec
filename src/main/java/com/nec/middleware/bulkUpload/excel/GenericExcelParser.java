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

@Slf4j
@Component
@RequiredArgsConstructor
public class GenericExcelParser {

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