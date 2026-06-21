package com.nec.middleware.rbacAuth.rbac.dto.response;

import lombok.*;

/**
 * Common response DTO for all RBAC API endpoints.
 * Used for consistent API response format.
 *
 * <p>Sample JSON:
 * <pre>
 * {
 *   "status": "SUCCESS",
 *   "message": "Module hierarchy saved successfully",
 *   "data": null
 * }
 * </pre>
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CommonResponse<T> {

    /**
     * Response status: SUCCESS, ERROR, VALIDATION_ERROR.
     */
    private String status;

    /**
     * Response message describing the result or error.
     */
    private String message;

    /**
     * Optional response data (generic type).
     */
    private T data;

    /**
     * Static factory method for success response.
     *
     * @param message the success message
     * @param <T> the data type
     * @return CommonResponse with SUCCESS status
     */
    public static <T> CommonResponse<T> success(String message) {
        return CommonResponse.<T>builder()
                .status("SUCCESS")
                .message(message)
                .data(null)
                .build();
    }

    /**
     * Static factory method for success response with data.
     *
     * @param message the success message
     * @param data the optional data to include
     * @param <T> the data type
     * @return CommonResponse with SUCCESS status and data
     */
    public static <T> CommonResponse<T> success(String message, T data) {
        return CommonResponse.<T>builder()
                .status("SUCCESS")
                .message(message)
                .data(data)
                .build();
    }

    /**
     * Static factory method for error response.
     *
     * @param message the error message
     * @param <T> the data type
     * @return CommonResponse with ERROR status
     */
    public static <T> CommonResponse<T> error(String message) {
        return CommonResponse.<T>builder()
                .status("ERROR")
                .message(message)
                .data(null)
                .build();
    }

    /**
     * Static factory method for validation error response.
     *
     * @param message the validation error message
     * @param <T> the data type
     * @return CommonResponse with VALIDATION_ERROR status
     */
    public static <T> CommonResponse<T> validationError(String message) {
        return CommonResponse.<T>builder()
                .status("VALIDATION_ERROR")
                .message(message)
                .data(null)
                .build();
    }
}


