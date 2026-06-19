package com.nec.middleware.hr.handler;

import com.nec.middleware.exception.DuplicateResourceException;
import com.nec.middleware.hr.dto.request.MinistryofInteriorRequestDto;
import com.nec.middleware.hr.dto.response.MinistryofInteriorResponseDto;
import com.nec.middleware.hr.entity.MinistryofInterior;
import com.nec.middleware.hr.mapper.MinistryofInteriorMapper;
import com.nec.middleware.hr.repository.MinistryofInteriorRepository;
import com.nec.middleware.hr.service.impl.MinistryofInteriorServiceImpl;
import com.nec.middleware.hr.util.FileStorageUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

/**
 * Persists a single Ministry of Interior row in its own independent
 * {@code REQUIRES_NEW} transaction.
 *
 * <h3>Why this still needs its own bean</h3>
 * Spring's {@code @Transactional} works via an AOP proxy that only
 * intercepts calls arriving from outside the bean. {@code persistSingleRow}
 * is called from {@code MinistryofInteriorBulkHandler.persist(dto)} —
 * already an external call — so {@code REQUIRES_NEW} here is honored
 * correctly. Each row commits or rolls back independently of every other
 * row in the same upload; one bad row never undoes rows already saved
 * earlier in the batch.
 *
 * <p>Mirrors {@link com.nec.middleware.hr.handler.UniversityTraineeRowPersister}:
 * validate → map to entity (scalars only) → resolve FKs via the shared
 * {@code MinistryofInteriorServiceImpl.resolveAndSetForeignKeys} → generate
 * the business id → save.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class MinistryofInteriorRowPersister {

    private final MinistryofInteriorRepository ministryofInteriorRepository;
    private final MinistryofInteriorMapper ministryofInteriorMapper;
    private final FileStorageUtil fileStorageUtil;
    private final MinistryofInteriorServiceImpl service;

    /**
     * Validate, build entity, resolve FKs (via the shared service method),
     * and save a single row in its own independent transaction. No photo
     * is stored — bulk-created records always have a null
     * {@code photoPath} unless a path string was supplied directly in the row.
     *
     * @throws DuplicateResourceException                            if email/phone already exists
     * @throws com.nec.middleware.exception.ResourceNotFoundException if any referenced FK id does not exist
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public MinistryofInteriorResponseDto persistSingleRow(MinistryofInteriorRequestDto dto) {
        log.debug("Persisting ministry of interior row");

        service.validateMinistryOfInterior(dto);

        MinistryofInterior entity = ministryofInteriorMapper.toEntity(dto);
        service.resolveAndSetForeignKeys(entity, dto);
        entity.setMinistryofInteriorId(service.generateMinistryofInteriorNumber());
        log.info("Ministry of interior row persisted: ministryofInteriorId='{}'", entity.getMinistryofInteriorId());

        // Bulk mode: no file bytes are uploaded — the row supplies a path
        // string directly. Validate extension only (no disk I/O) and store verbatim.
        if (StringUtils.hasText(dto.getPhotoPath())) {
            entity.setPhotoPath(fileStorageUtil.validatePhotoPath(dto.getPhotoPath()));
        }

        return ministryofInteriorMapper.toResponseDto(ministryofInteriorRepository.save(entity));
    }
}