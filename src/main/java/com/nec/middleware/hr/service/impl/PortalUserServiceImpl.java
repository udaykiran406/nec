package com.nec.middleware.hr.service.impl;

import com.nec.middleware.exception.ResourceNotFoundException;
import com.nec.middleware.exception.ValidationException;
import com.nec.middleware.hr.constant.PortalUserConstants;
import com.nec.middleware.hr.dto.request.PortalUserListRequestDto;
import com.nec.middleware.hr.mapper.PortalUserMapper;
import com.nec.middleware.hr.repository.PortalUserRepository;
import com.nec.middleware.hr.service.PortalUserService;
import com.nec.middleware.portal.dto.request.PortalUserRequestDto;
import com.nec.middleware.portal.dto.response.PortalUserResponseDto;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class PortalUserServiceImpl implements PortalUserService {

    private static final String CODE_PREFIX = "PU";
    private static final int    CODE_PAD    = 3;

    private final PortalUserRepository repository;
    private final PortalUserMapper mapper;

    // ------------------------------------------------------------------ SAVE / UPDATE

    @Override
    @Transactional
    public PortalUserResponseDto saveOrUpdate(PortalUserRequestDto requestDto) {

        validatePortalUser(requestDto);
        com.nec.middleware.portal.entity.PortalUser entity;

        if (requestDto.getId() == null) {

            log.info("Creating portal user");
            entity = mapper.toEntity(requestDto);
            entity.setCode(generateCode());


        } else {

            log.info("Updating portal user id: {}", requestDto.getId());
            entity = findByIdAndNotDeleted(requestDto.getId());
            mapper.updateEntity(entity, requestDto);
        }

        com.nec.middleware.portal.entity.PortalUser saved = repository.save(entity);
        return mapper.toResponseDto(saved);
    }

    // ------------------------------------------------------------------ READ

    @Override
    @Transactional(readOnly = true)
    public PortalUserResponseDto getById(Long id) {
        return mapper.toResponseDto(findByIdAndNotDeleted(id));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<PortalUserResponseDto> getAll(PortalUserListRequestDto filterDto) {

        Pageable pageable = PageRequest.of(
                filterDto.getPage(),
                filterDto.getSize(),
                Sort.by(Sort.Direction.DESC, "createdAt")
        );

        return repository.findAllWithFilters(
                filterDto.getRoleId(),
                filterDto.getGenderId(),
                filterDto.getDepartmentId(),
                filterDto.getRegionId(),
                filterDto.getDistrictId(),
                filterDto.getCityId(),
                filterDto.getPortalUserTypeId(),
                filterDto.getReferenceId(),
                filterDto.getIsActive(),
                pageable
        ).map(mapper::toResponseDto);
    }

    // ------------------------------------------------------------------ STATUS CHANGE

    @Override
    @Transactional
    public PortalUserResponseDto changeStatus(Long id) {

        com.nec.middleware.portal.entity.PortalUser entity = findById(id);
        boolean newStatus = !entity.getIsActive();
        entity.setIsActive(newStatus);

        // Reactivating a deleted record clears the deleted flag
        if (newStatus) {
            entity.setIsDeleted(false);
        }

        log.info("Changing status for portal user id: {} → isActive={}", id, newStatus);

        com.nec.middleware.portal.entity.PortalUser saved = repository.save(entity);
        return mapper.toResponseDto(saved);
    }

    // ------------------------------------------------------------------ SOFT DELETE

    @Override
    @Transactional
    public void softDelete(Long id) {

        com.nec.middleware.portal.entity.PortalUser entity = findById(id);
        entity.setIsDeleted(true);
        entity.setIsActive(false);

        repository.save(entity);
        log.info("Portal user soft deleted with id: {}", id);
    }

// ------------------------------------------------------------------
    // Code generation
    // ------------------------------------------------------------------

    /**
     * Reads the current max numeric suffix stored in the DB and returns the
     * next code, e.g. if the highest is PU007 this returns PU008.
     * The call is made inside a @Transactional method, so it is safe under
     * concurrent load (the subsequent save will fail on the unique constraint
     * in the extreme race-condition case, which can be retried at the API level).
     */
    private String generateCode() {
        int next = repository.findMaxCodeSequence() + 1;
        return CODE_PREFIX + String.format("%0" + CODE_PAD + "d", next);
    }
    // ------------------------------------------------------------------
    // Validation
    // ------------------------------------------------------------------

    private void validatePortalUser(PortalUserRequestDto dto) {

        if (dto.getId() == null) {

            if (repository.existsByEmailAndIsDeletedFalse(dto.getEmail()) ||
                    repository.existsByPhoneAndIsDeletedFalse(dto.getPhone())) {
                throw new ValidationException(PortalUserConstants.USER_ALREADY_EXISTS);
            }

        } else {

            if (repository.existsByEmailAndIsDeletedFalseAndIdNot(dto.getEmail(), dto.getId()) ||
                    repository.existsByPhoneAndIsDeletedFalseAndIdNot(dto.getPhone(), dto.getId())) {
                throw new ValidationException(PortalUserConstants.USER_ALREADY_EXISTS);
            }
        }
    }

    // ------------------------------------------------------------------
    // Private Helpers
    // ------------------------------------------------------------------

    private com.nec.middleware.portal.entity.PortalUser findByIdAndNotDeleted(Long id) {
        return repository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(PortalUserConstants.USER_NOT_FOUND + id));
    }

    private com.nec.middleware.portal.entity.PortalUser findById(Long id) {
        return repository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(PortalUserConstants.USER_NOT_FOUND + id));
    }
}
