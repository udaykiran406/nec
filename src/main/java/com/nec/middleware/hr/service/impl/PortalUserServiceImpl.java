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


import com.nec.middleware.hr.specification.PortalUserSearchSpecification;
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

    private final PortalUserRepository portalUserrepository;
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
        PortalUser portalUserEntity = mapper.portalUserEntity(portalUserRequest);
        portalUserEntity.setPortalUserId(generateUserId(PortalUserConstants.CODE_PREFIX, PortalUserConstants.CODE_PAD));
        // ---------------- LOOKUPS ----------------
        portalUserEntity.setGender(
                genderRepository.findById(portalUserRequest.getGenderId())
                        .orElseThrow(() ->
                                new ResourceNotFoundException("Gender not found"))
        );
        portalUserEntity.setRole(
                roleRepository.findById(portalUserRequest.getRoleId())
                        .orElseThrow(() ->
                                new ResourceNotFoundException("Role not found"))
        );
        portalUserEntity.setPortalUserType(
                portalUserTypeRepository.findById(
                                portalUserRequest.getPortalUserTypeId())
                        .orElseThrow(() ->
                                new ResourceNotFoundException("Portal User Type not found"))
        );
// ---------------- MASTER DATA ----------------

        portalUserEntity.setUniversity(
                universityRepository.findById(
                                portalUserRequest.getUniversityId())
                        .orElseThrow(() ->
                                new ResourceNotFoundException("University not found"))
        );
        portalUserEntity.setRegion(
                regionRepository.findById(
                                portalUserRequest.getRegionId())
                        .orElseThrow(() ->
                                new ResourceNotFoundException("Region not found"))
        );
        portalUserEntity.setDistrict(
                districtRepository.findById(
                                portalUserRequest.getDistrictId())
                        .orElseThrow(() ->
                                new ResourceNotFoundException("District not found"))
        );
        portalUserEntity.setCity(
                cityRepository.findById(
                                portalUserRequest.getCityId())
                        .orElseThrow(() ->
                                new ResourceNotFoundException("City not found"))
        );
        PortalUser savedEntity = portalUserrepository.save(portalUserEntity);
        return mapper.portalUserResponseDto(savedEntity);
    }


    // ------------------------------------------------------------------ READ

    @Override
    @Transactional(readOnly = true)
    public PortalUserResponseDto getUserByPortalUserId(String portalUserId) {
        return mapper.portalUserResponseDto(findByPortalUserIdAndIsActive(portalUserId));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<PortalUserResponseDto> getAllPortalUsers(PortalUserListRequestDto request, int page, int size) {
        log.info("userName = {}", request.getUserName());
        log.info("portalUserId = {}", request.getPortalUserId());

        if (request == null) {
            request = new PortalUserListRequestDto();
        }

        Pageable pageable = PageRequest.of(
                page,
                size,
                Sort.by(Sort.Direction.DESC, "createdAt"));
        return portalUserrepository.findAll(
                        PortalUserSearchSpecification.build(request),
                        pageable)
                .map(mapper::portalUserResponseDto);
    }

// ------------------------------------------------------------------ UPDATE


    @Override
    @Transactional
    public PortalUserResponseDto updatePortalUser(
            String portalUserId,
            PortalUserRequestDto portalUserRequestDto) {

        PortalUser portalUser = findByPortalUserIdAndIsActive(portalUserId);
        if (portalUserRequestDto.getGenderId() != null) {
            portalUser.setGender(
                    genderRepository.findById(
                                    portalUserRequestDto.getGenderId())
                            .orElseThrow(() ->
                                    new ResourceNotFoundException("Gender not found"))
            );
        }

        if (portalUserRequestDto.getRoleId() != null) {
            portalUser.setRole(
                    roleRepository.findById(
                                    portalUserRequestDto.getRoleId())
                            .orElseThrow(() ->
                                    new ResourceNotFoundException("Role not found"))
            );
        }
        if (portalUserRequestDto.getPortalUserTypeId() != null) {
            portalUser.setPortalUserType(
                    portalUserTypeRepository.findById(
                                    portalUserRequestDto.getPortalUserTypeId())
                            .orElseThrow(() ->
                                    new ResourceNotFoundException("Portal User Type not found"))
            );
        }

// ---------------- MASTER DATA ----------------

        if (portalUserRequestDto.getUniversityId() != null) {
            portalUser.setUniversity(
                    universityRepository.findById(
                                    portalUserRequestDto.getUniversityId())
                            .orElseThrow(() ->
                                    new ResourceNotFoundException("University not found"))
            );
        }

        if (portalUserRequestDto.getRegionId() != null) {
            portalUser.setRegion(
                    regionRepository.findById(
                                    portalUserRequestDto.getRegionId())
                            .orElseThrow(() ->
                                    new ResourceNotFoundException("Region not found"))
            );
        }

        if (portalUserRequestDto.getDistrictId() != null) {
            portalUser.setDistrict(
                    districtRepository.findById(
                                    portalUserRequestDto.getDistrictId())
                            .orElseThrow(() ->
                                    new ResourceNotFoundException("District not found"))
            );
        }

        if (portalUserRequestDto.getCityId() != null) {
            portalUser.setCity(
                    cityRepository.findById(
                                    portalUserRequestDto.getCityId())
                            .orElseThrow(() ->
                                    new ResourceNotFoundException("City not found"))
            );
        }
        mapper.updatePortalUserEntity(portalUser, portalUserRequestDto);

        return mapper.portalUserResponseDto(portalUserrepository.save(portalUser));
    }

    // ------------------------------------------------------------------ SOFT DELETE

    @Override
    @Transactional
    public PortalUserResponseDto softDelete(String portalUserId) {

        PortalUser entity = findByPortalUserIdAndIsActive(portalUserId);
        entity.setIsActive(false);

        log.info("Changing status for portal user id: {} → isActive=false","isDeleted=true",portalUserId);

        return mapper.portalUserResponseDto(portalUserrepository.save(entity));
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
        int next = portalUserrepository.findMaxCodeSequence() + 1;
        return codePrefix + String.format("%0" + codePad + "d", next);
    }
    // ------------------------------------------------------------------
    // Validation
    // ------------------------------------------------------------------

    private void validateDuplicateUser(PortalUserRequestDto request) {

        if (portalUserrepository.existsByEmailAndIsActiveTrue(request.getEmail())) {
            throw new ResourceAlreadyExistsException(
                    PortalUserConstants.USER_ALREADY_EXISTS +request.getEmail());
        }
    }

    // ------------------------------------------------------------------
    // Private Helpers
    // ------------------------------------------------------------------

    /** Used by GET — excludes inactive records. */
    private PortalUser findByPortalUserIdAndIsActive(String portalUserId) {
        return portalUserrepository.findByPortalUserIdAndIsActiveTrue(portalUserId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(PortalUserConstants.USER_NOT_FOUND + portalUserId));
    }
}
