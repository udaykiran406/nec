package com.nec.middleware.masterdata.response;

import lombok.*;

/**
 * Generic API response wrapper used by all endpoints in the MasterData module.
 *
 * <p>Sample success response:
 * <pre>
 * {
 *   "success": true,
 *   "message": "Record created successfully",
 *   "data": { ... }
 * }
 * </pre>
 *
 * <p>Sample error response:
 * <pre>
 * {
 *   "success": false,
 *   "message": "Record not found with id: 99",
 *   "data": null
 * }
 * </pre>
 *
 * @param <T> the type of the payload carried in {@code data}
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ApiResponse<T> {

    /** {@code true} when the operation succeeded; {@code false} otherwise. */
    private int statusCode;

    /** Human-readable description of the operation result. */
    private String message;

    /** The response payload; may be {@code null} for delete or error responses. */
    private T data;

    // -------------------------------------------------------------------------
    // Static factory helpers
    // -------------------------------------------------------------------------

    /**
     * Creates a success response with a message and data payload.
     *
     * @param message the success message
     * @param data    the response payload
     * @param <T>     the payload type
     * @return a populated success {@link ApiResponse}
     */
    public static <T> ApiResponse<T> success(int statusCode,String message, T data) {
        return ApiResponse.<T>builder()
                .statusCode(statusCode)
                .message(message)
                .data(data)
                .build();
    }

    /**
     * Creates a success response with only a message (no data payload).
     *
     * @param message the success message
     * @param <T>     the payload type (typically {@link Void})
     * @return a success {@link ApiResponse} with {@code data = null}
     */
    public static <T> ApiResponse<T> success(int statusCode,String message) {
        return ApiResponse.<T>builder()
                .statusCode(statusCode)
                .message(message)
                .data(null)
                .build();
    }

    /**
     * Creates an error response with only a message (no data payload).
     *
     * @param message the error message
     * @param <T>     the payload type
     * @return an error {@link ApiResponse} with {@code data = null}
     */
    public static <T> ApiResponse<T> error(int statusCode,String message) {
        return ApiResponse.<T>builder()
                .statusCode(statusCode)
                .message(message)
                .data(null)
                .build();
    }
}

