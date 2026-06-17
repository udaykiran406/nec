package com.nec.middleware.hr.util;

import org.apache.poi.ss.usermodel.*;
import org.springframework.stereotype.Component;

/**
 * Stateless helper that knows how to read a single Apache POI {@link Cell}
 * as a plain Java type.
 *
 * <p>This class has NO knowledge of any entity or column layout — it only
 * understands POI cell types.  Each entity-specific import service depends
 * on this util for the low-level reads, keeping their own code focused on
 * column mapping and DTO construction.
 *
 * <p>All methods are null-safe: passing {@code null} returns {@code null}
 * (or 0 / false for primitives) rather than throwing NPE.
 */
@Component
public class ExcelParserUtil {

    // ------------------------------------------------------------------
    // String
    // ------------------------------------------------------------------

    /**
     * Read any cell type as a trimmed String.
     * Date-formatted numeric cells are returned as {@code "yyyy-MM-dd"}.
     * Blank / null cells return {@code null}.
     */
    public String cellString(Cell cell) {
        if (cell == null) return null;
        switch (cell.getCellType()) {
            case STRING:
                String s = cell.getStringCellValue().trim();
                return s.isEmpty() ? null : s;
            case NUMERIC:
                if (DateUtil.isCellDateFormatted(cell)) {
                    return cell.getLocalDateTimeCellValue().toLocalDate().toString();
                }
                double d = cell.getNumericCellValue();
                // Return "1" not "1.0" for whole numbers (IDs, ages, etc.)
                return d == Math.floor(d) ? String.valueOf((long) d) : String.valueOf(d);
            case BOOLEAN:
                return String.valueOf(cell.getBooleanCellValue());
            case FORMULA:
                try { return cell.getStringCellValue().trim(); }
                catch (Exception e) {
                    double fd = cell.getNumericCellValue();
                    return fd == Math.floor(fd) ? String.valueOf((long) fd) : String.valueOf(fd);
                }
            default:
                return null;
        }
    }

    // ------------------------------------------------------------------
    // Numeric helpers  (all delegate to cellString then parse)
    // ------------------------------------------------------------------

    /**
     * Read a cell as {@code Long}.  Returns {@code null} if blank or unparseable.
     * Used for FK id columns (genderId, regionId, etc.).
     */
    public Long cellLong(Cell cell) {
        String v = cellString(cell);
        if (v == null) return null;
        try { return Long.parseLong(v.trim()); }
        catch (NumberFormatException e) { return null; }
    }

    /**
     * Read a cell as {@code Short}.  Returns {@code null} if blank or unparseable.
     * Used for the {@code age} column.
     */
    public Short cellShort(Cell cell) {
        String v = cellString(cell);
        if (v == null) return null;
        try { return Short.parseShort(v.trim()); }
        catch (NumberFormatException e) { return null; }
    }

    // ------------------------------------------------------------------
    // Row utility
    // ------------------------------------------------------------------

    /**
     * Returns {@code true} if every cell in the row is blank or null.
     * Used to skip trailing empty rows without mis-reporting them as errors.
     */
    public boolean isRowEmpty(Row row) {
        if (row == null) return true;
        for (int c = row.getFirstCellNum(); c < row.getLastCellNum(); c++) {
            Cell cell = row.getCell(c);
            if (cell != null && cell.getCellType() != CellType.BLANK) {
                String v = cellString(cell);
                if (v != null && !v.isEmpty()) return false;
            }
        }
        return true;
    }

    // ------------------------------------------------------------------
    // Header normalisation
    // ------------------------------------------------------------------

    /**
     * Lower-case and strip leading/trailing whitespace from a header label.
     * Centralised here so every import service normalises headers identically.
     */
    public String normaliseHeader(String raw) {
        if (raw == null) return "";
        return raw.trim().toLowerCase();
    }
}
