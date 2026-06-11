package com.nec.middleware.masterdata.validation;


import com.nec.middleware.masterdata.dto.request.MasterDataRequest;
import org.springframework.stereotype.Component;

/**
 * Validation utility for MasterData operations.
 */
@Component
public class MasterDataValidationUtil {

    /**
     * Validates a request for CREATE operation.
     * Ensures that createdBy is provided and id is null.
     *
     * @param request the request to validate
     * @throws IllegalArgumentException if validation fails
     */
    public void validateCreateRequest(MasterDataRequest request) {
        if (request.getId() != null) {
            throw new IllegalArgumentException("ID must be null for CREATE operations");
        }
        if (request.getCreatedBy() == null || request.getCreatedBy() <= 0) {
            throw new IllegalArgumentException("createdBy must be provided and positive");
        }
    }

    /**
     * Validates a request for UPDATE operation.
     * Ensures that id and updatedBy are provided.
     *
     * @param request the request to validate
     * @throws IllegalArgumentException if validation fails
     */
    public void validateUpdateRequest(MasterDataRequest request) {
        if (request.getId() == null || request.getId() <= 0) {
            throw new IllegalArgumentException("ID must be provided and positive for UPDATE operations");
        }
        if (request.getUpdatedBy() == null || request.getUpdatedBy() <= 0) {
            throw new IllegalArgumentException("updatedBy must be provided and positive");
        }
    }
}

