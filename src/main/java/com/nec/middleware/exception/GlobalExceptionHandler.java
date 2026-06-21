package com.nec.middleware.exception;

import com.nec.middleware.rbacAuth.auth.exception.AuthAspectException;
import com.nec.middleware.rbacAuth.auth.exception.ForbiddenException;
import com.nec.middleware.rbacAuth.auth.exception.InvalidCredentialsException;
import com.nec.middleware.rbacAuth.auth.exception.InvalidTokenException;
import com.nec.middleware.rbacAuth.auth.exception.KeycloakException;
import com.nec.middleware.rbacAuth.auth.exception.SomethingWentWrongException;
import com.nec.middleware.rbacAuth.auth.exception.UnauthorizedException;
import com.nec.middleware.rbacAuth.auth.exception.UserNotFoundException;
import com.nec.middleware.rbacAuth.rbac.constant.ErrorCodeConstants;
import com.nec.middleware.rbacAuth.rbac.dto.response.ApiResponse;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.multipart.support.MissingServletRequestPartException;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Global exception handler for the entire NEC Middleware application.
 *
 * <p>Handles exceptions from all module packages (masterdata, rbac, workflows, etc.)
 * and converts them into consistent {@link ApiResponse} JSON responses.
 *
 * <p>This handler applies to all classes under {@code com.nec.middleware}
 * and its subpackages.
 *
 * <p>Supported Exception Types:
 * <ul>
 *   <li>{@link ResourceNotFoundException} → HTTP 404</li>
 *   <li>{@link DuplicateException} → HTTP 409</li>
 *   <li>{@link ValidationException} → HTTP 400</li>
 *   <li>{@link ApplicationException} → HTTP 400 (base class)</li>
 *   <li>{@link MethodArgumentNotValidException} → HTTP 400 (Jakarta Bean Validation)</li>
 *   <li>{@link DataIntegrityViolationException} → HTTP 409 (Database constraint violations)</li>
 *   <li>{@link Exception} → HTTP 500 (catch-all for unexpected errors)</li>
 * </ul>
 */
@Slf4j
@RestControllerAdvice(basePackages = "com.nec.middleware")
public class GlobalExceptionHandler {

    private ResponseEntity<ApiResponse<Object>> errorResponse(
            HttpStatus status, String errorCode, String message) {
        return ResponseEntity
                .status(status)
                .body(ApiResponse.error(status.value(), errorCode, message));
    }

    private ResponseEntity<ApiResponse<Object>> errorResponse(int status, String errorCode, String message) {
        return ResponseEntity
                .status(status)
                .body(ApiResponse.error(status, errorCode, message));
    }

    /**
     * Handles {@link ResourceNotFoundException} – returns HTTP 404.
     *
     * <p>This applies to any resource type (roles, users, regions, etc.)
     * that cannot be found.
     *
     * @param ex the exception
     * @return a 404 ApiResponse with the error message
     */
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiResponse<Object>> handleResourceNotFoundException(
            ResourceNotFoundException ex) {
        return errorResponse(HttpStatus.NOT_FOUND, ex.getErrorCode(), ex.getMessage());
    }

    @ExceptionHandler(DuplicateRecordException.class)
    public ResponseEntity<ApiResponse<Object>> handleDuplicateRecordException(
            DuplicateRecordException ex) {
        return errorResponse(HttpStatus.CONFLICT, ex.getErrorCode(), ex.getMessage());
    }

    /**
     * Handles {@link DuplicateException} – returns HTTP 409 Conflict.
     *
     * @deprecated Use {@link DuplicateRecordException}; kept for backward compatibility.
     */
    @ExceptionHandler(DuplicateException.class)
    public ResponseEntity<ApiResponse<Object>> handleDuplicateException(
            DuplicateException ex) {
        return handleDuplicateRecordException(ex);
    }

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ApiResponse<Object>> handleBusinessException(BusinessException ex) {
        return errorResponse(HttpStatus.BAD_REQUEST, ex.getErrorCode(), ex.getMessage());
    }

    /**
     * Handles enhanced validation exceptions ({@link EnhancedValidationException}).
     * MUST be BEFORE ApplicationException since it's a subclass.
     *
     * <p>Includes the actual exception message which contains field error details.
     *
     * @param ex the EnhancedValidationException
     * @return a 400 ApiResponse with validation error details
     */
    @ExceptionHandler(EnhancedValidationException.class)
    public ResponseEntity<ApiResponse<Object>> handleEnhancedValidationException(
            EnhancedValidationException ex) {
        return errorResponse(HttpStatus.BAD_REQUEST, ErrorCodeConstants.VALIDATION_ERROR, ex.getMessage());
    }

    /**
     * Handles business rule violation exceptions ({@link BusinessRuleViolationException}).
     * MUST be BEFORE ApplicationException since it's a subclass.
     *
     * @param ex the BusinessRuleViolationException
     * @return a 422 ApiResponse with business rule violation message
     */
    @ExceptionHandler(BusinessRuleViolationException.class)
    public ResponseEntity<ApiResponse<Object>> handleBusinessRuleViolation(
            BusinessRuleViolationException ex) {
        String message = (ex.getMessage() != null && !ex.getMessage().isBlank())
                ? ex.getMessage()
                : ErrorCodeConstants.BUSINESS_RULE_VIOLATION_MSG;
        return errorResponse(HttpStatus.UNPROCESSABLE_ENTITY, ErrorCodeConstants.BUSINESS_RULE_VIOLATION, message);
    }

    /**
     * Handles concurrent update exceptions ({@link ConcurrentUpdateException}).
     * MUST be BEFORE ApplicationException since it's a subclass.
     *
     * @param ex the ConcurrentUpdateException
     * @return a 409 ApiResponse with concurrent update message
     */
    @ExceptionHandler(ConcurrentUpdateException.class)
    public ResponseEntity<ApiResponse<Object>> handleConcurrentUpdateException(
            ConcurrentUpdateException ex) {
        return errorResponse(
                HttpStatus.CONFLICT,
                ErrorCodeConstants.CONCURRENT_UPDATE,
                ErrorCodeConstants.CONCURRENT_UPDATE_MSG);
    }

    /**
     * Handles {@link ValidationException} – returns HTTP 400 Bad Request.
     *
     * <p>This applies to any business-level validation failure
     * across all modules.
     *
     * @param ex the exception
     * @return a 400 ApiResponse with the error message
     */
    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<ApiResponse<Object>> handleValidationException(
            ValidationException ex) {
        return errorResponse(HttpStatus.BAD_REQUEST, ex.getErrorCode(), ex.getMessage());
    }

    /**
     * Handles {@link ApplicationException} (base class) – returns HTTP 400 Bad Request.
     *
     * <p>This is a fallback handler for any application exception that doesn't match
     * the more specific subclass handlers above. MUST be last since it's the base class.
     *
     * @param ex the exception
     * @return a 400 ApiResponse with the error message
     */
    @ExceptionHandler(ApplicationException.class)
    public ResponseEntity<ApiResponse<Object>> handleApplicationException(
            ApplicationException ex) {
        return errorResponse(HttpStatus.BAD_REQUEST, ErrorCodeConstants.BUSINESS_RULE_VIOLATION, ex.getMessage());
    }

    /**
     * Handles Jakarta Bean Validation failures ({@link MethodArgumentNotValidException}).
     *
     * <p>Extracts field-specific validation errors and returns them in a user-friendly format.
     * Converts technical field paths to readable names:
     * - "moduleCode" → "Module Code is required."
     * - "modules[0].groupCode" → "Module (1): Group Code is required."
     *
     * <p>This handler applies to @Valid and @Validated annotations on request parameters
     * across all modules.
     *
     * @param ex the exception containing field-level errors
     * @return a 400 ApiResponse with field-specific error messages
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Object>> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex) {

        // Extract field-specific errors
        Map<String, String> fieldErrors = new LinkedHashMap<>();
        List<String> errorMessages = new ArrayList<>();

        ex.getBindingResult().getFieldErrors().forEach(error -> {
            String fieldName = ValidationErrorResponseBuilder.extractFieldName(error.getField());
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

        // If only one error, return it directly
        if (errorMessages.size() == 1) {
            return errorResponse(HttpStatus.BAD_REQUEST, ErrorCodeConstants.VALIDATION_ERROR, errorMessages.get(0));
        }

        String mainMessage = errorMessages.isEmpty()
                ? ErrorCodeConstants.VALIDATION_ERROR_MSG
                : String.join(", ", errorMessages);

        return errorResponse(HttpStatus.BAD_REQUEST, ErrorCodeConstants.VALIDATION_ERROR, mainMessage);
    }

    /**
     * Handles database constraint violations ({@link DataIntegrityViolationException}).
     *
     * <p>This handler intercepts SQL constraint violations (UNIQUE, FOREIGN KEY, CHECK, etc.)
     * and converts them into user-friendly error messages.
     *
     * <p>Supported constraint patterns:
     * <ul>
     *   <li>Email constraint → "A user with this email already exists."</li>
     *   <li>Phone constraint → "A user with this phone number already exists."</li>
     *   <li>Role code constraint → "A role with this code already exists."</li>
     *   <li>Module code constraint → "A module with this code already exists."</li>
     *   <li>Group code constraint → "A permission group with this code already exists."</li>
     *   <li>Permission code constraint → "A permission with this code already exists."</li>
     *   <li>Foreign key constraint → "The selected reference is not valid."</li>
     *   <li>Other constraints → Generic conflict message</li>
     * </ul>
     *
     * @param ex the DataIntegrityViolationException
     * @return ApiResponse with appropriate HTTP status and user-friendly error message
     */
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ApiResponse<Object>> handleDataIntegrityViolation(
            DataIntegrityViolationException ex) {
        log.warn("Data integrity violation", ex);
        ErrorMessageMapper.ErrorMessageMapping mapping = ErrorMessageMapper.mapDataIntegrityViolation(ex);
        HttpStatus status = mapping.getHttpStatusEnum();
        return errorResponse(status, mapping.getErrorCode(), mapping.getMessage());
    }

    /**
     * Handles optimistic locking failures ({@link ObjectOptimisticLockingFailureException}).
     *
     * <p>Thrown when a record is updated by another user (version mismatch).
     *
     * @param ex the ObjectOptimisticLockingFailureException
     * @return a 409 ApiResponse with concurrent update message
     */
    @ExceptionHandler(ObjectOptimisticLockingFailureException.class)
    public ResponseEntity<ApiResponse<Object>> handleOptimisticLockingFailure(
            ObjectOptimisticLockingFailureException ex) {
        log.warn("Optimistic locking failure: {}", ex.getMessage());
        return errorResponse(
                HttpStatus.CONFLICT,
                ErrorCodeConstants.CONCURRENT_UPDATE,
                ErrorCodeConstants.CONCURRENT_UPDATE_MSG);
    }

    /**
     * Handles Jakarta validation constraint violations (from @Validated on method parameters).
     *
     * <p>Extracts field-specific validation errors and returns them in a user-friendly format.
     *
     * @param ex the ConstraintViolationException
     * @return a 400 ApiResponse with field-specific error messages
     */
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ApiResponse<Object>> handleConstraintViolationException(
            ConstraintViolationException ex) {

        List<String> errorMessages = new ArrayList<>();

        for (ConstraintViolation<?> violation : ex.getConstraintViolations()) {
            String message = violation.getMessage();
            if (!errorMessages.contains(message)) {
                errorMessages.add(message);
            }
        }

        // If only one error, return it directly
        if (errorMessages.size() == 1) {
            return errorResponse(HttpStatus.BAD_REQUEST, ErrorCodeConstants.VALIDATION_ERROR, errorMessages.get(0));
        }

        String mainMessage = errorMessages.isEmpty()
                ? ErrorCodeConstants.VALIDATION_ERROR_MSG
                : String.join(", ", errorMessages);

        return errorResponse(HttpStatus.BAD_REQUEST, ErrorCodeConstants.VALIDATION_ERROR, mainMessage);
    }

    /**
     * Handles Spring Security authentication failures – returns HTTP 401.
     */
    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ApiResponse<Object>> handleAuthenticationException(
            AuthenticationException ex) {
        log.warn("Authentication failed: {}", ex.getMessage());
        // Normalize authentication failure message to avoid leaking internal details.
        String message = "Invalid username/email or password";
        return errorResponse(HttpStatus.UNAUTHORIZED, "INVALID_CREDENTIALS", message);
    }

    /**
     * Handles disabled/inactive accounts – returns HTTP 403.
     */
    @ExceptionHandler(DisabledException.class)
    public ResponseEntity<ApiResponse<Object>> handleDisabledException(DisabledException ex) {
        log.warn("Account disabled: {}", ex.getMessage());
        return errorResponse(HttpStatus.FORBIDDEN, "FORBIDDEN", ex.getMessage());
    }

    /**
     * Handles malformed JSON or unparseable HTTP message bodies.
     *
     * @param ex the HttpMessageNotReadableException
     * @return a 400 ApiResponse with invalid format message
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiResponse<Object>> handleHttpMessageNotReadable(
            HttpMessageNotReadableException ex) {
        return errorResponse(
                HttpStatus.BAD_REQUEST,
                ErrorCodeConstants.INVALID_REQUEST,
                ErrorCodeConstants.INVALID_REQUEST_MSG);
    }

    /**
     * Handles type mismatch errors (e.g., passing string when integer expected).
     *
     * <p>Returns a generic, user-friendly invalid request message without exposing
     * field names or technical type information.
     *
     * @param ex the MethodArgumentTypeMismatchException
     * @return a 400 ApiResponse with generic user-friendly error message
     */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ApiResponse<Object>> handleMethodArgumentTypeMismatch(
            MethodArgumentTypeMismatchException ex) {
        return errorResponse(
                HttpStatus.BAD_REQUEST,
                ErrorCodeConstants.INVALID_REQUEST,
                ErrorCodeConstants.INVALID_REQUEST_MSG);
    }

    /**
     * Handles missing required request parameters (e.g. when @RequestParam without required=false is omitted).
     */
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ApiResponse<Object>> handleMissingServletRequestParameter(
            MissingServletRequestParameterException ex) {
        log.warn("Missing request parameter: {}", ex.getParameterName());
        String message = String.format("Required request parameter '%s' is missing", ex.getParameterName());
        return errorResponse(HttpStatus.BAD_REQUEST, ErrorCodeConstants.INVALID_REQUEST, message);
    }

    /**
     * Handles 404 Not Found exceptions from Spring.
     *
     * @param ex the NoHandlerFoundException
     * @return a 404 ApiResponse with not found message
     */
    @ExceptionHandler(NoHandlerFoundException.class)
    public ResponseEntity<ApiResponse<Object>> handleNoHandlerFoundException(
            NoHandlerFoundException ex) {
        return errorResponse(
                HttpStatus.NOT_FOUND,
                ErrorCodeConstants.NOT_FOUND,
                ErrorCodeConstants.NOT_FOUND_MSG);
    }

    /**
     * Handles access denied exceptions (from Spring Security).
     *
     * @param ex the AccessDeniedException
     * @return a 403 ApiResponse with access denied message
     */
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiResponse<Object>> handleAccessDeniedException(
            AccessDeniedException ex) {
        return errorResponse(
                HttpStatus.FORBIDDEN,
                "ACCESS_DENIED",
                "You are not authorized to perform this action.");
    }

    /**
     * Handles illegal argument exceptions (invalid arguments passed to methods).
     *
     * @param ex the IllegalArgumentException
     * @return a 400 ApiResponse with invalid argument message
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiResponse<Object>> handleIllegalArgumentException(
            IllegalArgumentException ex) {
        // Log at WARN to avoid noisy stacktraces for expected client input errors.
        log.warn("Unexpected argument: {}", ex.getMessage());
        return errorResponse(
                HttpStatus.BAD_REQUEST,
                ErrorCodeConstants.INVALID_REQUEST,
                ex.getMessage() != null && !ex.getMessage().isBlank()
                        ? ex.getMessage()
                        : ErrorCodeConstants.INVALID_REQUEST_MSG);
    }

    /**
     * Handles illegal state exceptions (invalid object state).
     *
     * @param ex the IllegalStateException
     * @return a 400 ApiResponse with invalid state message
     */
    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<ApiResponse<Object>> handleIllegalStateException(
            IllegalStateException ex) {
        log.error("Unexpected state: {}", ex.getMessage(), ex);
        return errorResponse(
                HttpStatus.BAD_REQUEST,
                ErrorCodeConstants.INVALID_REQUEST,
                ex.getMessage() != null && !ex.getMessage().isBlank()
                        ? ex.getMessage()
                        : ErrorCodeConstants.INVALID_REQUEST_MSG);
    }

    /**
     * Handles SQL exceptions (database connectivity issues, etc.).
     *
     * @param ex the SQLException
     * @return a 500 ApiResponse with database error message
     */
    @ExceptionHandler(SQLException.class)
    public ResponseEntity<ApiResponse<Object>> handleSqlException(SQLException ex) {
        return errorResponse(
                HttpStatus.INTERNAL_SERVER_ERROR,
                ErrorCodeConstants.DATABASE_ERROR,
                ErrorCodeConstants.DATABASE_ERROR_MSG);
    }

    /**
     * Handles IO exceptions (file reading, network issues, etc.).
     *
     * @param ex the IOException
     * @return a 503 ApiResponse with service unavailable message
     */
    @ExceptionHandler(IOException.class)
    public ResponseEntity<ApiResponse<Object>> handleIOException(IOException ex) {
        return errorResponse(
                HttpStatus.SERVICE_UNAVAILABLE,
                ErrorCodeConstants.SERVICE_UNAVAILABLE,
                ErrorCodeConstants.SERVICE_UNAVAILABLE_MSG);
    }

    // =========================================================================
    // Auth / Keycloak exception handlers
    // =========================================================================

    /**
     * Handles invalid credentials (wrong username or password) – returns HTTP 401.
     */
    @ExceptionHandler(InvalidCredentialsException.class)
    public ResponseEntity<ApiResponse<Object>> handleInvalidCredentialsException(
            InvalidCredentialsException ex) {
        log.warn("Invalid credentials: {}", ex.getMessage());
        // Standardize message returned to clients for invalid credentials
        return errorResponse(HttpStatus.UNAUTHORIZED, "INVALID_CREDENTIALS", "Invalid username/email or password");
    }

    /**
     * Handles an invalid or expired Bearer token – returns HTTP 401.
     */
    @ExceptionHandler(InvalidTokenException.class)
    public ResponseEntity<ApiResponse<Object>> handleInvalidTokenException(
            InvalidTokenException ex) {
        log.warn("Invalid token: {}", ex.getMessage());
        return errorResponse(HttpStatus.UNAUTHORIZED, "INVALID_TOKEN", ex.getMessage());
    }

    /**
     * Handles unauthorized access (missing / unrecognised principal) – returns HTTP 401.
     */
    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<ApiResponse<Object>> handleUnauthorizedException(
            UnauthorizedException ex) {
        log.warn("Unauthorized: {}", ex.getMessage());
        return errorResponse(HttpStatus.UNAUTHORIZED, "UNAUTHORIZED", ex.getMessage());
    }

    /**
     * Handles forbidden access (authenticated but lacking permission) – returns HTTP 403.
     */
    @ExceptionHandler(ForbiddenException.class)
    public ResponseEntity<ApiResponse<Object>> handleForbiddenException(
            ForbiddenException ex) {
        log.warn("Forbidden: {}", ex.getMessage());
        return errorResponse(HttpStatus.FORBIDDEN, "FORBIDDEN", ex.getMessage());
    }

    /**
     * Handles user not found during auth flows – returns HTTP 404.
     */
    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ApiResponse<Object>> handleUserNotFoundException(
            UserNotFoundException ex) {
        log.warn("User not found: {}", ex.getMessage());
        return errorResponse(HttpStatus.NOT_FOUND, "USER_NOT_FOUND", ex.getMessage());
    }

    /**
     * Handles structured Keycloak error responses – forwards the status Keycloak returned.
     */
    @ExceptionHandler(KeycloakException.class)
    public ResponseEntity<ApiResponse<Object>> handleKeycloakException(
            KeycloakException ex) {
        log.error("Keycloak error [{}]: {}", ex.getErrorTraceMethod(),
                ex.getKeycloakErrorResponse().getErrorDescription());
        int status = ex.getKeycloakErrorResponse().getStatus() != null
                ? ex.getKeycloakErrorResponse().getStatus()
                : HttpStatus.UNAUTHORIZED.value();
        String description = ex.getKeycloakErrorResponse().getErrorDescription();
        String message = (description != null && !description.isBlank())
                ? description
                : "Authentication failed";
        return errorResponse(status, ex.getKeycloakErrorResponse().getError(), message);
    }

    /**
     * Handles unexpected internal errors from the auth/Keycloak layer – returns HTTP 500.
     */
    @ExceptionHandler(SomethingWentWrongException.class)
    public ResponseEntity<ApiResponse<Object>> handleSomethingWentWrongException(
            SomethingWentWrongException ex) {
        log.error("Internal error in [{}]: {}", ex.getErrTraceMethod(), ex.getMessage());
        return errorResponse(
                HttpStatus.INTERNAL_SERVER_ERROR,
                ErrorCodeConstants.INTERNAL_SERVER_ERROR,
                ErrorCodeConstants.INTERNAL_SERVER_ERROR_MSG);
    }

    /**
     * Handles AOP authorize-aspect failures (token validation errors not covered above) – returns HTTP 401.
     */
    @ExceptionHandler(AuthAspectException.class)
    public ResponseEntity<ApiResponse<Object>> handleAuthAspectException(
            AuthAspectException ex) {
        log.error("Auth aspect error [{}]: {}", ex.getErrTraceMethod(), ex.getMessage());
        return errorResponse(HttpStatus.UNAUTHORIZED, "AUTH_ERROR", ex.getMessage());
    }

    /**
     * Catch-all handler for any unexpected exception – returns HTTP 500.
     *
     * <p>This is a safety net for any unhandled exceptions that escape
     * the more specific handlers.
     *
     * @param ex the exception
     * @return a 500 ApiResponse with a generic error message
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Object>> handleGenericException(Exception ex) {
        log.error("Unhandled exception: {}", ex.getMessage(), ex);
        return errorResponse(
                HttpStatus.INTERNAL_SERVER_ERROR,
                ErrorCodeConstants.INTERNAL_SERVER_ERROR,
                ErrorCodeConstants.INTERNAL_SERVER_ERROR_MSG);
    }

    @ExceptionHandler(MissingServletRequestPartException.class)
    public ResponseEntity<ApiResponse<Object>> handleMissingServletRequestPart(
            MissingServletRequestPartException ex) {
        log.warn("Missing request part: {}", ex.getRequestPartName());

        String message = String.format(
                "Required request part '%s' is missing",
                ex.getRequestPartName());

        return errorResponse(
                HttpStatus.BAD_REQUEST,
                ErrorCodeConstants.INVALID_REQUEST,
                message
        );
    }

    @ExceptionHandler(DuplicateResourceException.class)
    public ResponseEntity<ApiResponse<Object>> handleDuplicateResourceException(
            DuplicateResourceException ex) {
        return errorResponse(
                HttpStatus.CONFLICT,
                "DUPLICATE_RESOURCE",
                ex.getMessage()
        );
    }
}

