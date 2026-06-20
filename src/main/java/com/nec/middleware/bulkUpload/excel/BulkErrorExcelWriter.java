package com.nec.middleware.bulkUpload.excel;


import com.nec.middleware.bulkUpload.dto.RowErrorDto;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

@Slf4j
@Component
public class BulkErrorExcelWriter {

    /** Sub-folder name under the system temp directory where error reports are staged. */
    private static final String TEMP_SUBFOLDER = "nec-bulk-errors";

    private static final String ERROR_REASON_HEADER = "Error Reason";

    public File write(String[] columnLabels, List<RowErrorDto> errors, String baseFileName) {

        try (Workbook workbook = new XSSFWorkbook()) {

            Sheet sheet = workbook.createSheet("Errors");

            CellStyle headerStyle = buildHeaderStyle(workbook);

            // ── Header row ───────────────────────────────────────────
            Row headerRow = sheet.createRow(0);
            int col = 0;
            for (String label : columnLabels) {
                Cell cell = headerRow.createCell(col++);
                cell.setCellValue(label);
                cell.setCellStyle(headerStyle);
            }
            Cell reasonHeaderCell = headerRow.createCell(col);
            reasonHeaderCell.setCellValue(ERROR_REASON_HEADER);
            reasonHeaderCell.setCellStyle(headerStyle);

            // ── Data rows ────────────────────────────────────────────
            int rowIdx = 1;
            for (RowErrorDto error : errors) {
                // Skip purely structural errors that have no row data at all
                // (e.g. "file has no sheets") — nothing meaningful to write back.
                if (error.getRowNumber() <= 1 && (error.getRawData() == null || error.getRawData().isEmpty())) {
                    continue;
                }

                Row dataRow = sheet.createRow(rowIdx++);
                int c = 0;
                for (String label : columnLabels) {
                    String value = error.getRawData() != null ? error.getRawData().get(label) : null;
                    dataRow.createCell(c++).setCellValue(value != null ? value : "");
                }
                dataRow.createCell(c).setCellValue(error.getMessage() != null ? error.getMessage() : "Unknown error");
            }

            // ── Auto-size columns for readability ───────────────────
            for (int i = 0; i <= columnLabels.length; i++) {
                sheet.autoSizeColumn(i);
            }

            return writeToTempFile(workbook, baseFileName);

        } catch (IOException e) {
            log.error("Failed to build bulk error report: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to build bulk error report: " + e.getMessage(), e);
        }
    }

    /**
     * Deletes a temp error-report file. Call this after the file's bytes
     * have been fully streamed in the HTTP response.
     */
    public void cleanup(File file) {
        if (file == null) return;
        try {
            Files.deleteIfExists(file.toPath());
        } catch (IOException e) {
            log.warn("Could not delete temp error report '{}': {}", file.getAbsolutePath(), e.getMessage());
        }
    }

    // ------------------------------------------------------------------
    // Private helpers
    // ------------------------------------------------------------------

    private CellStyle buildHeaderStyle(Workbook workbook) {
        Font boldFont = workbook.createFont();
        boldFont.setBold(true);
        CellStyle style = workbook.createCellStyle();
        style.setFont(boldFont);
        return style;
    }

    private File writeToTempFile(Workbook workbook, String baseFileName) throws IOException {
        Path tempDir = Files.createTempDirectory(TEMP_SUBFOLDER);
        // Unique suffix avoids collisions if multiple uploads fail concurrently.
        String fileName = baseFileName + "-" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss")) + ".xlsx";
        File outFile = new File(tempDir.toFile(), fileName);

        try (FileOutputStream fos = new FileOutputStream(outFile)) {
            workbook.write(fos);
        }

        log.info("Wrote bulk error report to '{}'", outFile.getAbsolutePath());
        return outFile;
    }
}