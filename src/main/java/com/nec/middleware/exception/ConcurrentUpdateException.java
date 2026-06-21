package com.nec.middleware.exception;

/**
 * Custom exception for concurrency and optimistic locking failures.
 *
 * <p>Thrown when:
 * - A record was modified by another user (optimistic lock failure)
 * - A concurrent update conflict is detected
 * - A record version mismatch occurs
 *
 * <p>Usage:
 * <pre>
 * try {
 *     role = roleRepository.save(updatedRole);
 * } catch (ObjectOptimisticLockingFailureException ex) {
 *     throw new ConcurrentUpdateException("The record was updated by another user", ex);
 * }
 * </pre>
 */
public class ConcurrentUpdateException extends ApplicationException {

    private static final long serialVersionUID = 1L;

    /** The entity that had the concurrent update */
    private final String entityType;

    /** The entity ID that had the concurrent update */
    private final Long entityId;

    /** The expected version that caused the conflict */
    private final Long expectedVersion;

    /** The actual version in the database */
    private final Long actualVersion;

    /**
     * Constructs a ConcurrentUpdateException with a message
     *
     * @param message the error message
     */
    public ConcurrentUpdateException(String message) {
        super(message);
        this.entityType = null;
        this.entityId = null;
        this.expectedVersion = null;
        this.actualVersion = null;
    }

    /**
     * Constructs a ConcurrentUpdateException with detailed information
     *
     * @param message the error message
     * @param entityType the type of entity
     * @param entityId the ID of the entity
     * @param expectedVersion the expected version
     * @param actualVersion the actual version in database
     */
    public ConcurrentUpdateException(String message, String entityType, Long entityId,
                                    Long expectedVersion, Long actualVersion) {
        super(message);
        this.entityType = entityType;
        this.entityId = entityId;
        this.expectedVersion = expectedVersion;
        this.actualVersion = actualVersion;
    }

    /**
     * Constructs a ConcurrentUpdateException with a message and cause
     *
     * @param message the error message
     * @param cause the cause exception
     */
    public ConcurrentUpdateException(String message, Throwable cause) {
        super(message, cause);
        this.entityType = null;
        this.entityId = null;
        this.expectedVersion = null;
        this.actualVersion = null;
    }

    /**
     * Gets the entity type that had the concurrent update
     *
     * @return the entity type
     */
    public String getEntityType() {
        return entityType;
    }

    /**
     * Gets the entity ID that had the concurrent update
     *
     * @return the entity ID
     */
    public Long getEntityId() {
        return entityId;
    }

    /**
     * Gets the expected version
     *
     * @return the expected version
     */
    public Long getExpectedVersion() {
        return expectedVersion;
    }

    /**
     * Gets the actual version in the database
     *
     * @return the actual version
     */
    public Long getActualVersion() {
        return actualVersion;
    }
}

