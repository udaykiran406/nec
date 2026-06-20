package com.nec.middleware.hr.handler;

import com.nec.middleware.exception.DuplicateResourceException;
import com.nec.middleware.hr.dto.request.PoliticalPartyAgentRequestDto;
import com.nec.middleware.hr.dto.response.PoliticalPartyAgentResponseDto;
import com.nec.middleware.hr.entity.PoliticalPartyAgent;
import com.nec.middleware.hr.mapper.PoliticalPartyAgentMapper;
import com.nec.middleware.hr.repository.PoliticalPartyAgentRepository;
import com.nec.middleware.hr.service.impl.PoliticalPartyAgentServiceImpl;
import com.nec.middleware.hr.util.FileStorageUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

/**
 * Persists a single Political Party Agent row in its own independent
 * {@code REQUIRES_NEW} transaction.
 *
 * <h3>Why this still needs its own bean</h3>
 * Spring's {@code @Transactional} works via an AOP proxy that only
 * intercepts calls arriving from outside the bean. {@code persistSingleRow}
 * is called from {@code PoliticalPartyAgentBulkHandler.persist(dto)} —
 * already an external call — so {@code REQUIRES_NEW} here is honored
 * correctly. Each row commits or rolls back independently of every other
 * row in the same upload; one bad row never undoes rows already saved
 * earlier in the batch.
 *
 * <p>Mirrors {@link com.nec.middleware.hr.handler.UniversityTraineeRowPersister}:
 * validate → map to entity (scalars only) → resolve FKs via the shared
 * {@code PoliticalPartyAgentServiceImpl.resolveAndSetForeignKeys} → generate
 * the business id → save.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class PoliticalPartyAgentRowPersister {

    private final PoliticalPartyAgentRepository politicalPartyAgentRepository;
    private final PoliticalPartyAgentMapper politicalPartyAgentMapper;
    private final FileStorageUtil fileStorageUtil;
    private final PoliticalPartyAgentServiceImpl service;

    /**
     * Validate, build entity, resolve FKs (via the shared service method),
     * and save a single row in its own independent transaction. No photo
     * is stored — bulk-created agents always have a null {@code photoPath}
     * unless a path string was supplied directly in the row.
     *
     * @throws DuplicateResourceException                            if email/phone already exists
     * @throws com.nec.middleware.exception.ResourceNotFoundException if any referenced FK id does not exist
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public PoliticalPartyAgentResponseDto persistSingleRow(PoliticalPartyAgentRequestDto dto) {
        log.debug("Persisting political party agent row");

        service.validateAgent(dto);

        PoliticalPartyAgent entity = politicalPartyAgentMapper.toPoliticalPartyAgentEntity(dto);
        service.resolveAndSetForeignKeys(entity, dto);
        entity.setPoliticalPartyAgentUserId(service.generatePoliticalPartyAgentNumber());
        log.info("Political party agent row persisted: partyAgentUserId='{}'", entity.getPoliticalPartyAgentUserId());

        // Bulk mode: no file bytes are uploaded — the row supplies a path
        // string directly. Validate extension only (no disk I/O) and store verbatim.
        if (StringUtils.hasText(dto.getPhotoPath())) {
            entity.setPhotoPath(fileStorageUtil.validatePhotoPath(dto.getPhotoPath()));
        }

        return politicalPartyAgentMapper.politicalPartyResponseDto(politicalPartyAgentRepository.save(entity));
    }
}