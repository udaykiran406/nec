package com.nec.middleware.bulkUpload.excel;

import lombok.Getter;

import java.util.Map;

/**
 * One data row out of {@link GenericExcelParser}: a 1-based file row number
 * paired with its raw column values, keyed by header label exactly as read
 * from row 1 of the sheet.
 *
 * <p>This is as far as the parser's knowledge goes — no DTO, no types
 * beyond {@code String}, no validation. Everything downstream (mapping,
 * validation, persistence) is the handler's responsibility.
 */
@Getter
public class ParsedExcelRow {

    private final int rowNumber;
    private final Map<String, String> rawData;

    public ParsedExcelRow(int rowNumber, Map<String, String> rawData) {
        this.rowNumber = rowNumber;
        this.rawData = rawData;
    }
}
