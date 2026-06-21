package com.nec.middleware.exception;

import com.nec.middleware.rbacAuth.rbac.constant.ErrorCodeConstants;
import com.nec.middleware.rbacAuth.rbac.dto.response.ApiResponse;
import jakarta.validation.ConstraintViolation;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Utility class for building field-specific validation error responses.
 *
 * <p>Extracts validation errors from MethodArgumentNotValidException and converts them
 * into user-friendly error messages grouped by field name.
 *
 * <p>Supports nested field validation errors and formats them in a consistent manner.
 */
public class ValidationErrorResponseBuilder {

    private static final int HTTP_BAD_REQUEST = HttpStatus.BAD_REQUEST.value();

    /**
     * Prevent instantiation
     */
    private ValidationErrorResponseBuilder() {
        throw new AssertionError("Cannot instantiate ValidationErrorResponseBuilder");
    }

    /**
     * Builds an ApiResponse with field-specific validation errors from MethodArgumentNotValidException.
     *
     * @param ex the MethodArgumentNotValidException
     * @return ApiResponse with field-specific error messages
     */
    public static ApiResponse<Object> buildValidationErrorResponse(MethodArgumentNotValidException ex) {
        Map<String, String> fieldErrors = new LinkedHashMap<>();
        List<String> errorMessages = new ArrayList<>();

        ex.getBindingResult().getFieldErrors().forEach(error -> {
            String fieldName = extractFieldName(error.getField());
            String message = error.getDefaultMessage();
            fieldErrors.put(fieldName, message);
            if (!errorMessages.contains(message)) {
                errorMessages.add(message);
            }
        });

        ex.getBindingResult().getGlobalErrors().forEach(error -> {
            String message = error.getDefaultMessage();
            if (!errorMessages.contains(message)) {
                errorMessages.add(message);
            }
        });

        if (errorMessages.size() == 1) {
            return ApiResponse.error(HTTP_BAD_REQUEST, ErrorCodeConstants.VALIDATION_ERROR, errorMessages.get(0));
        }
        return ApiResponse.error(
                HTTP_BAD_REQUEST,
                ErrorCodeConstants.VALIDATION_ERROR,
                "Please review the information you entered.");
    }

    /**
     * Builds an ApiResponse with field-specific validation errors from a constraint violation.
     *
     * @param violations the set of constraint violations
     * @return ApiResponse with field-specific error messages
     */
    public static ApiResponse<Object> buildConstraintViolationResponse(
            Set<? extends ConstraintViolation<?>> violations) {
        List<String> errorMessages = violations.stream()
                .map(ConstraintViolation::getMessage)
                .collect(Collectors.toList());

        if (errorMessages.size() == 1) {
            return ApiResponse.error(HTTP_BAD_REQUEST, ErrorCodeConstants.VALIDATION_ERROR, errorMessages.get(0));
        }
        return ApiResponse.error(
                HTTP_BAD_REQUEST,
                ErrorCodeConstants.VALIDATION_ERROR,
                "Please review the information you entered.");
    }

    /**
     * Extracts the user-friendly field name from a nested property path.
     *
     * <p>Examples:
     * - "moduleCode" → "Module Code"
     * - "modules[0].groupCode" → "Module (1): Group Code"
     * - "modules[0].groups[0].permissionCode" → "Module (1), Group (1): Permission Code"
     *
     * @param fieldPath the nested field path
     * @return the user-friendly field name
     */
    public static String extractFieldName(String fieldPath) {
        if (fieldPath == null || fieldPath.isEmpty()) {
            return "Field";
        }

        StringBuilder result = new StringBuilder();
        String[] parts = fieldPath.split("\\.");
        int index = 0;

        for (String part : parts) {
            if (part.contains("[")) {
                String fieldName = part.substring(0, part.indexOf('['));
                String indexPart = part.substring(part.indexOf('[') + 1, part.indexOf(']'));
                int arrayIndex = Integer.parseInt(indexPart) + 1;

                if (index > 0) {
                    result.append(", ");
                }
                result.append(camelCaseToReadable(fieldName))
                        .append(" (").append(arrayIndex).append(")");
                index++;
            } else if (!part.isEmpty()) {
                if (index > 0) {
                    result.append(": ");
                }
                result.append(camelCaseToReadable(part));
                index++;
            }
        }

        return result.toString();
    }

    private static String camelCaseToReadable(String camelCase) {
        if (camelCase == null || camelCase.isEmpty()) {
            return "";
        }

        StringBuilder result = new StringBuilder();
        for (int i = 0; i < camelCase.length(); i++) {
            char c = camelCase.charAt(i);
            if (Character.isUpperCase(c) && i > 0) {
                result.append(" ");
            }
            result.append(c);
        }

        String converted = result.toString();
        if (!converted.isEmpty()) {
            return Character.toUpperCase(converted.charAt(0)) + converted.substring(1);
        }
        return converted;
    }
}
