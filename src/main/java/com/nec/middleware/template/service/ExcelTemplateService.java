package com.nec.middleware.template.service;


import com.nec.middleware.template.ExcelColumn;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddressList;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.lang.reflect.Field;
import java.time.LocalDate;

/**
 * Builds a downloadable Excel (.xlsx) import template for any DTO class
 * annotated with {@link ExcelColumn}.
 *
 * Generic and module-agnostic: hr, rbac, or any other module can pass
 * its own DTO class here without this service depending on that module.
 */
@Service
public class ExcelTemplateService {

    private static final String SHEET_NAME = "Template";

    public byte[] createTemplate(Class<?> dtoClass) throws Exception {

        try (Workbook workbook = new XSSFWorkbook();
             ByteArrayOutputStream out = new ByteArrayOutputStream()) {

            Sheet sheet = workbook.createSheet(SHEET_NAME);

            CellStyle mandatoryStyle = createStyle(workbook, IndexedColors.LIGHT_ORANGE);
            CellStyle optionalStyle = createStyle(workbook, IndexedColors.LIGHT_GREEN);

            Row headerRow = sheet.createRow(0);
            Row sampleRow = sheet.createRow(1);

            Field[] fields = dtoClass.getDeclaredFields();

            int col = 0;

            for (Field field : fields) {
                ExcelColumn excelColumn = field.getAnnotation(ExcelColumn.class);

                if (excelColumn == null) {
                    continue;
                }

                boolean mandatory = excelColumn.mandatory()
                        || field.isAnnotationPresent(NotNull.class)
                        || field.isAnnotationPresent(NotBlank.class)
                        || field.isAnnotationPresent(NotEmpty.class);

                String columnName = excelColumn.name() + (mandatory ? " *" : "");

                Cell headerCell = headerRow.createCell(col);
                headerCell.setCellValue(columnName);
                headerCell.setCellStyle(mandatory ? mandatoryStyle : optionalStyle);

                sampleRow.createCell(col).setCellValue(excelColumn.sample());

                String description = buildDescription(field, excelColumn, mandatory);
                addComment(sheet, 0, col, description);

                if (excelColumn.allowedValues().length > 0) {
                    addDropdown(sheet, col, excelColumn.allowedValues());
                }

                sheet.autoSizeColumn(col);
                col++;
            }

            workbook.write(out);
            return out.toByteArray();
        }
    }

    private String buildDescription(Field field, ExcelColumn excelColumn, boolean mandatory) {
        StringBuilder sb = new StringBuilder();

        sb.append(mandatory ? "Mandatory" : "Optional");

        if (!excelColumn.description().isBlank()) {
            sb.append(". ").append(excelColumn.description());
        }

        if (field.isAnnotationPresent(Email.class)) {
            sb.append(". Must be a valid email");
        }

        if (field.getType().equals(LocalDate.class)) {
            sb.append(". Date format: yyyy-MM-dd");
        }

        if (Number.class.isAssignableFrom(field.getType())) {
            sb.append(". Numeric value expected");
        }

        return sb.toString();
    }

    private void addDropdown(Sheet sheet, int colIndex, String[] values) {
        DataValidationHelper helper = sheet.getDataValidationHelper();

        DataValidationConstraint constraint =
                helper.createExplicitListConstraint(values);

        CellRangeAddressList range =
                new CellRangeAddressList(1, 1000, colIndex, colIndex);

        DataValidation validation =
                helper.createValidation(constraint, range);

        validation.setShowErrorBox(true);
        sheet.addValidationData(validation);
    }

    private void addComment(Sheet sheet, int row, int col, String text) {
        CreationHelper factory = sheet.getWorkbook().getCreationHelper();
        Drawing<?> drawing = sheet.createDrawingPatriarch();

        ClientAnchor anchor = factory.createClientAnchor();
        anchor.setCol1(col);
        anchor.setCol2(col + 4);
        anchor.setRow1(row);
        anchor.setRow2(row + 3);

        Comment comment = drawing.createCellComment(anchor);
        comment.setString(factory.createRichTextString(text));

        sheet.getRow(row).getCell(col).setCellComment(comment);
    }

    private CellStyle createStyle(Workbook workbook, IndexedColors color) {
        CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setBold(true);

        style.setFillForegroundColor(color.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        style.setFont(font);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);

        return style;
    }
}