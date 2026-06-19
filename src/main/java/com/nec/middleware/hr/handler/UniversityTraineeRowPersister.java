package com.nec.middleware.hr.handler;

import com.nec.middleware.exception.DuplicateResourceException;
import com.nec.middleware.hr.constant.UniversityTraineeConstants;
import com.nec.middleware.hr.dto.request.UniversityTraineeRequestDto;
import com.nec.middleware.hr.dto.response.UniversityTraineeResponseDto;
import com.nec.middleware.hr.entity.UniversityTrainee;
import com.nec.middleware.hr.mapper.UniversityTraineeMapper;
import com.nec.middleware.hr.repository.UniversityTraineeRepository;
import com.nec.middleware.hr.service.impl.UniversityTraineeServiceImpl;
import com.nec.middleware.hr.util.FileStorageUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

/**

 *
 * <h3>Why this still needs its own bean</h3>
 * Spring's {@code @Transactional} works via an AOP proxy that only
 * intercepts calls arriving from outside the bean. {@code persistSingleRow}
 * is called from {@code UniversityTraineeBulkHandler.persist(dto)} —
 * already an external call — so {@code REQUIRES_NEW} here is honored
 * correctly. Each row commits or rolls back independently of every other
 * row in the same upload; one bad row never undoes rows already saved
 * earlier in the batch.
 *
 * <p>The duplicate check stays here rather than moving to
 * {@code UniversityTraineeEnrichmentService} because it's a write-path
 * pre-condition specific to create (both single and bulk), not a
 * reference-data lookup like FK resolution — it doesn't need its own
 * shared component, just a method called from both
 * {@code persistSingleRow} and {@code ServiceImpl.createTrainee}'s
 * existing {@code validateTrainee} call. If you'd rather have that one
 * rule shared too, it can move into
 * {@code UniversityTraineeEnrichmentService} alongside the rest — happy to
 * do that if you want a single source of truth for every create-time rule,
 * not just FK + ID.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class UniversityTraineeRowPersister {

    private final UniversityTraineeRepository universityTraineeRepository;
    private final UniversityTraineeMapper universityTraineeMapper;
    private final FileStorageUtil fileStorageUtil;
    private final UniversityTraineeServiceImpl service;

    /**
     * Validate, build entity, resolve FKs (via the shared enrichment
     * service), and save a single row in its own independent transaction.
     * No photo is stored — bulk-created trainees always have a null
     * {@code photoPath} unless a path string was supplied directly in the row.
     *
     * @throws DuplicateResourceException                            if email/phone already exists
     * @throws com.nec.middleware.exception.ResourceNotFoundException if any referenced FK id does not exist
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public UniversityTraineeResponseDto persistSingleRow(UniversityTraineeRequestDto dto) {
        log.debug("Persisting university trainee row");

       service.validateTrainee(dto);

        UniversityTrainee entity = universityTraineeMapper.toEntity(dto);
        service.resolveAndSetForeignKeys(entity, dto);
        entity.setUniversityTraineeId(service.generateUniversityTraineeNumber());
        log.info("University trainee row persisted: universityTraineeId='{}'", entity.getUniversityTraineeId());

        // Bulk mode: no file bytes are uploaded — the row supplies a path
        // string directly. Validate extension only (no disk I/O) and store verbatim.
        if (StringUtils.hasText(dto.getPhotoPath())) {
            entity.setPhotoPath(fileStorageUtil.validatePhotoPath(dto.getPhotoPath()));
        }

        return universityTraineeMapper.toResponseDto(universityTraineeRepository.save(entity));
    }

}