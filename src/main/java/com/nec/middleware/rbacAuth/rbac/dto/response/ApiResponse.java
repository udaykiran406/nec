package com.nec.middleware.rbacAuth.rbac.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.*;

/**
 * Generic API response DTO for all endpoints across the application.
 * The {@code success} field mirrors the HTTP status code on every response.
 *
 * <p>Sample success JSON (HTTP 200/201):
 * <pre>
 * {
 *   "success": 200,
 *   "message": "Operation successful",
 *   "data": { ... }
 * }
 * </pre>
 *
 * <p>Sample error JSON (HTTP 4xx/5xx):
 * <pre>
 * {
 *   "success": 400,
 *   "errorCode": "INVALID_REQUEST",
 *   "message": "New password and confirm password do not match"
 * }
 * </pre>
 *
 * @param <T> the type of data being returned
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonPropertyOrder({"success", "errorCode", "message", "data"})
public class ApiResponse<T> {

    public static final int HTTP_OK = 200;
    public static final int HTTP_CREATED = 201;

    /**
     * HTTP status code mirrored in the response body (e.g. 200, 201, 400, 404).
     */
    private Integer success;

    /**
     * Error code identifier (e.g., VALIDATION_ERROR, DUPLICATE_RECORD, NOT_FOUND).
     * Only included in error responses; null for success responses.
     */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String errorCode;

    /**
     * A descriptive message about the operation result or error.
     */
    private String message;

    /**
     * Optional response data (generic type). Omitted on error responses.
     */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private T data;

    public static <T> ApiResponse<T> success(int success, String message) {
        return ApiResponse.<T>builder()
                .success(success)
                .message(message)
                .build();
    }

    public static <T> ApiResponse<T> success(int success, String message, T data) {
        return ApiResponse.<T>builder()
                .success(success)
                .message(message)
                .data(data)
                .build();
    }

    public static <T> ApiResponse<T> ok(String message) {
        return success(HTTP_OK, message);
    }

    public static <T> ApiResponse<T> ok(String message, T data) {
        return success(HTTP_OK, message, data);
    }

    public static <T> ApiResponse<T> created(String message, T data) {
        return success(HTTP_CREATED, message, data);
    }

    public static <T> ApiResponse<T> error(int success, String errorCode, String message) {
        return ApiResponse.<T>builder()
                .success(success)
                .errorCode(errorCode)
                .message(message)
                .build();
    }
}
