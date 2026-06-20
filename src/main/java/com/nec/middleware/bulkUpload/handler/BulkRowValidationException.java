package com.nec.middleware.bulkUpload.handler;

/**
 * Thrown by a {@link BulkUploadHandler}'s {@code map} or {@code validate}
 * method to report a row-level problem (bean validation failure, business
 * rule violation, FK not found, unparseable cell value, etc.) without
 * aborting the rest of the batch.
 *
 * <p>The {@code GenericBulkUploadService} catches this specifically (as
 * opposed to generic {@code Exception}) so handler authors have a clear,
 * intentional way to signal "this row failed for a known reason" versus an
 * unexpected bug. Optionally carries a {@code field} name when the failure
 * is attributable to one column (e.g. a duplicate email).
 */
public class BulkRowValidationException extends RuntimeException {

    private final String field;

    public BulkRowValidationException(String message) {
        super(message);
        this.field = null;
    }

    public BulkRowValidationException(String field, String message) {
        super(message);
        this.field = field;
    }

    public String getField() {
        return field;
    }
}
