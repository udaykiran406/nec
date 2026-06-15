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

    }
}

