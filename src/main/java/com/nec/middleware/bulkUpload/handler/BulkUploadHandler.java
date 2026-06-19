package com.nec.middleware.bulkUpload.handler;



import java.util.List;
import java.util.Map;

/**
 * Strategy contract every bulk-upload module implements exactly once.
 *
 * <p>A handler owns everything specific to its module: which Excel headers
 * it expects, how a raw row maps to its DTO, what "valid" means for that
 * DTO (bean validation + business rules + FK checks), and how a validated
 * DTO gets persisted. The {@code GenericBulkUploadService} never branches
 * on module type — it only ever calls these five methods.
 *
 * <p>To add a new module (Employee, Student, Vendor, Contractor, ...):
 * implement this interface, annotate it {@code @Component}, and register
 * its {@link #moduleName()} in
 * {@link com.nec.middleware.bulkUpload.registry.BulkUploadHandlerRegistry},
 * Nothing in the core engine, parser, or controller changes.
 *
 * @param <T> the module's request DTO type (e.g. {@code UniversityTraineeRequestDto})
 * @param <R> the module's response DTO type (e.g. {@code UniversityTraineeResponseDto}),
 *            returned for each successfully persisted row
 */
public interface BulkUploadHandler<T, R> {

    /**
     * Unique key identifying this module, matched against the
     * {@code {module}} path variable on {@code POST /bulk-upload/{module}}
     * (e.g. {@code "UNIVERSITY_TRAINEE"}). Convention: upper snake case.
     */
    String moduleName();

    /**
     * Column headers this module's Excel template requires, in the order
     * they should appear in the generated error report. Used by the engine
     * to fail fast with a single clear error when the uploaded file's
     * header row doesn't match, instead of letting every data row fail
     * individually with confusing "field is required" messages.
     */
    List<String> expectedHeaders();

    /**
     * Convert one raw row (header label → raw string value) into this
     * module's request DTO. Implementations should be defensive about
     * missing/blank values — a missing required field should map to
     * {@code null} on the DTO rather than throw, so {@link #validate}
     * is the single place that reports "field is required" consistently
     * via Bean Validation.
     *
     * <p>Mapping-level parse problems that aren't expressible as a bean
     * validation constraint (e.g. "Age column contains non-numeric text")
     * should be thrown as {@link BulkRowValidationException} so the engine
     * can attribute the failure to this row without aborting the batch.
     */
    T map(Map<String, String> row);

    /**
     * Validate a mapped DTO. Implementations should run Jakarta Bean
     * Validation constraints first, then any custom business rules
     * (duplicate checks, FK existence, cross-field rules). On failure,
     * throw {@link BulkRowValidationException} with a single human-readable
     * message describing everything wrong with the row.
     *
     * <p>Must NOT throw raw persistence/database exceptions — those are
     * handled separately by the engine around {@link #persist}.
     */
    void validate(T dto);

    /**
     * Persist a validated DTO and return its response representation.
     * Implementations are responsible for their own per-row transaction
     * boundary (typically {@code Propagation.REQUIRES_NEW} on a dedicated
     * bean) so one bad row never rolls back rows already committed earlier
     * in the same batch. See module README / migration notes for why this
     * can't simply be a plain {@code @Transactional} method on the handler
     * itself.
     */
    R persist(T dto);
}
