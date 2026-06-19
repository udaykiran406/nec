package com.nec.middleware.hr.handler;

import com.nec.middleware.exception.DuplicateResourceException;
import com.nec.middleware.hr.constant.PortalUserConstants;
import com.nec.middleware.hr.dto.request.PortalUserRequestDto;
import com.nec.middleware.hr.dto.response.PortalUserResponseDto;
import com.nec.middleware.hr.entity.PortalUser;
import com.nec.middleware.hr.mapper.PortalUserMapper;
import com.nec.middleware.hr.repository.PortalUserRepository;
import com.nec.middleware.hr.service.impl.PortalUserServiceImpl;
import com.nec.middleware.hr.util.FileStorageUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

/**
 * Persists a single Portal User row in its own REQUIRES_NEW transaction
 * so one bad row never rolls back rows already committed earlier in the batch.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class PortalUserRowPersister {

    private final PortalUserRepository portalUserRepository;
    private final PortalUserMapper portalUserMapper;
    private final FileStorageUtil fileStorageUtil;
    private final PortalUserServiceImpl service;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public PortalUserResponseDto persistSingleRow(PortalUserRequestDto dto) {
        log.debug("Persisting portal user row");

       service.validateDuplicateUser(dto);

        PortalUser entity = portalUserMapper.portalUserEntity(dto);


        service.resolveAndSetForeignKeys(entity, dto);

        entity.setPortalUserId(service.generatePortalUserNumber());
        log.info("Portal user row persisted: portalUserId='{}'", entity.getPortalUserId());

        // Bulk mode: no file bytes — path string supplied directly in the row
        if (StringUtils.hasText(dto.getPhotoPath())) {
            entity.setPhotoPath(fileStorageUtil.validatePhotoPath(dto.getPhotoPath()));
        }

        PortalUserResponseDto response = portalUserMapper.portalUserResponseDto(
                portalUserRepository.save(entity));

        // Enrich masterData IdValueDto on the response
        service.enrichMasterData(response, entity.getMasterData(), entity.getMasterdataId());

        return response;
    }
}