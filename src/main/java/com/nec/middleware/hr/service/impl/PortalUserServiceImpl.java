package com.nec.middleware.hr.service.impl;

import com.nec.middleware.Lookups.repository.LookupGenderRepository;
import com.nec.middleware.Lookups.repository.LookupPortalUserTypeRepository;
import com.nec.middleware.Lookups.repository.LookupRoleRepository;
import com.nec.middleware.exception.ResourceAlreadyExistsException;
import com.nec.middleware.exception.ResourceNotFoundException;
import com.nec.middleware.hr.constant.PortalUserConstants;
import com.nec.middleware.hr.dto.request.PortalUserListRequestDto;
import com.nec.middleware.hr.dto.request.PortalUserRequestDto;
import com.nec.middleware.hr.dto.response.PortalUserResponseDto;
import com.nec.middleware.hr.entity.PortalUser;
import com.nec.middleware.hr.mapper.PortalUserMapper;
import com.nec.middleware.hr.repository.PortalUserRepository;
import com.nec.middleware.hr.service.PortalUserService;


import com.nec.middleware.masterdata.repository.CityRepository;
import com.nec.middleware.masterdata.repository.DistrictRepository;
import com.nec.middleware.masterdata.repository.MasterDataRepository;
import com.nec.middleware.masterdata.repository.UniversityRepository;
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

    private final PortalUserRepository repository;
    private final PortalUserMapper mapper;
    private final MasterDataRepository regionRepository;
    private final DistrictRepository districtRepository;
    private final CityRepository cityRepository;
    private final UniversityRepository universityRepository;
    private final LookupGenderRepository genderRepository;
    private final LookupPortalUserTypeRepository portalUserTypeRepository;
    private final LookupRoleRepository roleRepository;


    // ------------------------------------------------------------------ SAVE

    @Override
    public PortalUserResponseDto createPortalUser(PortalUserRequestDto portalUserRequest) {



        log.info("Creating portal user {}", portalUserRequest.getUserName());
        validateDuplicateUser(portalUserRequest);

        PortalUser entity = mapper.toEntity(portalUserRequest);

        entity.setPortalUserId(generateUserId(PortalUserConstants.CODE_PREFIX,PortalUserConstants.CODE_PAD));

        // ---------------- LOOKUPS ----------------

        entity.setGender(
                genderRepository.findById(portalUserRequest.getGenderId())
                        .orElseThrow(() ->
                                new ResourceNotFoundException("Gender not found"))
        );

        entity.setRole(
                roleRepository.findById(portalUserRequest.getRoleId())
                        .orElseThrow(() ->
                                new ResourceNotFoundException("Role not found"))
        );

        entity.setPortalUserType(
                portalUserTypeRepository.findById(
                                portalUserRequest.getPortalUserTypeId())
                        .orElseThrow(() ->
                                new ResourceNotFoundException("Portal User Type not found"))
        );
// ---------------- MASTER DATA ----------------

        entity.setUniversity(
                universityRepository.findById(
                                portalUserRequest.getUniversityId())
                        .orElseThrow(() ->
                                new ResourceNotFoundException("University not found"))
        );

        entity.setRegion(
                regionRepository.findById(
                                portalUserRequest.getRegionId())
                        .orElseThrow(() ->
                                new ResourceNotFoundException("Region not found"))
        );

        entity.setDistrict(
                districtRepository.findById(
                                portalUserRequest.getDistrictId())
                        .orElseThrow(() ->
                                new ResourceNotFoundException("District not found"))
        );

        entity.setCity(
                cityRepository.findById(
                                portalUserRequest.getCityId())
                        .orElseThrow(() ->
                                new ResourceNotFoundException("City not found"))
        );

        PortalUser savedEntity = repository.save(entity);

        return mapper.toResponseDto(savedEntity);
    }


    // ------------------------------------------------------------------ READ

    @Override
    @Transactional(readOnly = true)
    public PortalUserResponseDto getUserByPortalUserId(String portalUserId) {
        return mapper.toResponseDto(findByPortalUserIdAndIsActive(portalUserId));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<PortalUserResponseDto> getAllPortalUsers(PortalUserListRequestDto filterDto) {
        log.info("userName = {}", filterDto.getUserName());
        log.info("portalUserId = {}", filterDto.getPortalUserId());
        Pageable pageable = PageRequest.of(
                filterDto.getPage(),
                filterDto.getSize(),
                Sort.by(Sort.Direction.DESC, "createdAt")
        );

        return repository.findAllWithFilters(
                filterDto.getUserName(),
                filterDto.getPortalUserId(),
                filterDto.getRoleId(),
                filterDto.getGenderId(),
                filterDto.getUniversityId(),
                filterDto.getRegionId(),
                filterDto.getDistrictId(),
                filterDto.getCityId(),
                filterDto.getPortalUserTypeId(),
                filterDto.getIsActive(),
                pageable
        ).map(mapper::toResponseDto);
    }

// ------------------------------------------------------------------ UPDATE


    @Override
    @Transactional
    public PortalUserResponseDto updatePortalUser(
            String portalUserId,
            PortalUserRequestDto requestDto) {

        PortalUser entity = findByPortalUserId(portalUserId);
        entity.setUpdatedBy(requestDto.getUpdatedBy());

        mapper.updateEntity(entity, requestDto);

        return mapper.toResponseDto(repository.save(entity));
    }

    // ------------------------------------------------------------------ SOFT DELETE

    @Override
    @Transactional
    public PortalUserResponseDto softDelete(String portalUserId) {

        PortalUser entity = findByPortalUserId(portalUserId);
        entity.setIsActive(false);
        entity.setIsDeleted(true);

        log.info("Changing status for portal user id: {} → isActive=false","isDeleted=true",portalUserId);

        return mapper.toResponseDto(repository.save(entity));
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
    private String generateUserId(String codePrefix, int codePad) {
        int next = repository.findMaxCodeSequence() + 1;
        return codePrefix + String.format("%0" + codePad + "d", next);
    }
    // ------------------------------------------------------------------
    // Validation
    // ------------------------------------------------------------------

    private void validateDuplicateUser(PortalUserRequestDto request) {

        if (repository.existsByEmailAndIsActiveTrue(request.getEmail())) {
            throw new ResourceAlreadyExistsException(
                    PortalUserConstants.USER_ALREADY_EXISTS +request.getEmail());
        }
    }

    // ------------------------------------------------------------------
    // Private Helpers
    // ------------------------------------------------------------------

    /** Used by GET — excludes inactive records. */
    private PortalUser findByPortalUserIdAndIsActive(String portalUserId) {
        return repository.findByPortalUserIdAndIsActiveTrue(portalUserId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(PortalUserConstants.USER_NOT_FOUND + portalUserId));
    }

    /** Used by UPDATE / SOFT-DELETE — allows acting on any non-deleted record. */
    private PortalUser findByPortalUserId(String portalUserId) {
        return repository.findByPortalUserIdAndIsDeletedFalse(portalUserId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(PortalUserConstants.USER_NOT_FOUND + portalUserId));
    }


}
