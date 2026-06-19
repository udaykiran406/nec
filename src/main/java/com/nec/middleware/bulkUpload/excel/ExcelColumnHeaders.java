package com.nec.middleware.bulkUpload.excel;

import com.nec.middleware.template.ExcelColumn;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

/**
 * Reads {@code @ExcelColumn} annotations off a request DTO class and
 * returns the column header names in declaration order.
 *
 * <p>Shared by {@code BulkUploadHandler.expectedHeaders()} implementations
 * so a module's header list is derived from the same single source of
 * truth ({@code @ExcelColumn} on the DTO) that
 * {@code ExcelTemplateService} already uses to generate the downloadable
 * import template. One annotation, two consumers, zero hand-copied header
 * strings to keep in sync.
 *
 * <p>NOTE: imports {@code com.nec.middleware.template.ExcelColumn} — the
 * actual package the annotation lives in. The existing
 * {@code ExcelTemplateService} / {@code ExcelTemplateController} in the
 * uploaded {@code template.zip} import
 * {@code com.nec.middleware.template.annotation.ExcelColumn}, a package
 * that does not exist in that zip. That mismatch is a pre-existing bug,
 * independent of this refactor — see migration notes.
 */
public final class ExcelColumnHeaders {

    private ExcelColumnHeaders() {
    }

    /**
     * Returns the {@code @ExcelColumn(name = ...)} values for every
     * annotated field on {@code dtoClass}, in field declaration order.
     * Fields without {@code @ExcelColumn} are skipped — exactly matching
     * what {@code ExcelTemplateService} renders as columns in the
     * generated template.
     */
    public static List<String> of(Class<?> dtoClass) {
        List<String> headers = new ArrayList<>();
        for (Field field : dtoClass.getDeclaredFields()) {
            ExcelColumn excelColumn = field.getAnnotation(ExcelColumn.class);
            if (excelColumn != null) {
                headers.add(excelColumn.name());
            }
        }
        return headers;
    }
}
