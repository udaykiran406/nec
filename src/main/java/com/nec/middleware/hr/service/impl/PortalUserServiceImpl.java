package com.nec.middleware.hr.service.impl;

import com.nec.middleware.Lookups.repository.LookupGenderRepository;
import com.nec.middleware.Lookups.repository.LookupPortalUserTypeRepository;
import com.nec.middleware.Lookups.repository.LookupRoleRepository;
import com.nec.middleware.exception.ResourceAlreadyExistsException;
import com.nec.middleware.exception.ResourceNotFoundException;
import com.nec.middleware.hr.Enum.MasterData;
import com.nec.middleware.hr.constant.PortalUserConstants;
import com.nec.middleware.hr.dto.request.PortalUserFilterRequestDto;
import com.nec.middleware.hr.dto.request.PortalUserRequestDto;
import com.nec.middleware.dto.IdValueDto;
import com.nec.middleware.hr.dto.response.PortalUserResponseDto;
import com.nec.middleware.hr.entity.PortalUser;
import com.nec.middleware.hr.mapper.PortalUserMapper;
import com.nec.middleware.hr.repository.PortalUserRepository;
import com.nec.middleware.hr.service.PortalUserService;


import com.nec.middleware.hr.specification.PortalUserSearchSpecification;
import com.nec.middleware.masterdata.entity.MasterDataAaqilType;
import com.nec.middleware.masterdata.entity.MasterDataPoliticalParty;
import com.nec.middleware.masterdata.entity.MasterDataUniversity;
import com.nec.middleware.masterdata.repository.*;
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
    private final PortalUserMapper portalUserMapper;
    private final MasterDataRepository regionRepository;
    private final DistrictRepository districtRepository;
    private final CityRepository cityRepository;
    private final UniversityRepository universityRepository;
    private final LookupGenderRepository genderRepository;
    private final LookupPortalUserTypeRepository portalUserTypeRepository;
    private final LookupRoleRepository roleRepository;
    private final AaqilTypeRepository aaqilRepository;
    private final PoliticalPartyRepository politicalPartyRepository;


    // ------------------------------------------------------------------ SAVE

    @Override
    public PortalUserResponseDto createPortalUser(PortalUserRequestDto portalUserRequest) {


        log.info("Creating portal user {}", portalUserRequest.getUserName());
//        validateDuplicateUser(portalUserRequest);
        PortalUser portalUserEntity = portalUserMapper.portalUserEntity(portalUserRequest);
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


        MasterData masterData = resolveMasterData(portalUserRequest.getPortalUserTypeId());

        if (!isMasterDataExists(masterData, portalUserRequest.getMasterdataId())) {
            throw new ResourceNotFoundException(
                    masterData + " not found with id: " + portalUserRequest.getMasterdataId()
            );
        }

        portalUserEntity.setMasterData(masterData);
        portalUserEntity.setMasterdataId(portalUserRequest.getMasterdataId());

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

        PortalUserResponseDto response =
                portalUserMapper.portalUserResponseDto(savedEntity);

        // Enrich masterData as IdValueDto: { id: masterdataId, value: "University Name / Party Name / Aaqil Value" }
        response.setMasterData(
                buildMasterDataIdValueDto(savedEntity.getMasterData(), savedEntity.getMasterdataId()));

        return response;
    }


    // ------------------------------------------------------------------ READ

    @Override
    @Transactional(readOnly = true)
    public PortalUserResponseDto getUserByPortalUserId(String portalUserId) {
        PortalUser portalUser =findByPortalUserId(portalUserId);
        PortalUserResponseDto portalUserResponse =
                portalUserMapper.portalUserResponseDto(portalUser);

        portalUserResponse.setMasterData(
                buildMasterDataIdValueDto(
                        portalUser.getMasterData(),
                        portalUser.getMasterdataId()
                )
        );

        return portalUserResponse;
    }

    @Override
    @Transactional(readOnly = true)
    public Page<PortalUserResponseDto> getAllPortalUsers(PortalUserFilterRequestDto request, int page, int size) {


        if (request == null) {
            request = new PortalUserFilterRequestDto();
        }

        Pageable pageable = PageRequest.of(
                page,
                size,
                Sort.by(Sort.Direction.DESC, "createdAt"));
        return portalUserrepository.findAll(
                        PortalUserSearchSpecification.buildSpecification(request),
                        pageable)
                .map(entity -> {
                    PortalUserResponseDto portalUserResponseDto =
                            portalUserMapper.portalUserResponseDto(entity);

                    portalUserResponseDto.setMasterData(
                            buildMasterDataIdValueDto(
                                    entity.getMasterData(),
                                    entity.getMasterdataId()
                            )
                    );
                    return portalUserResponseDto;
                });
    }

// ------------------------------------------------------------------ UPDATE


    @Override
    @Transactional
    public PortalUserResponseDto updatePortalUser(
            String portalUserId,
            PortalUserRequestDto portalUserRequestDto) {

        PortalUser portalUser = findByPortalUserId(portalUserId);
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
        if (portalUserRequestDto.getMasterdataId() != null) {

            Long portalUserTypeId =
                    portalUserRequestDto.getPortalUserTypeId() != null
                            ? portalUserRequestDto.getPortalUserTypeId()
                            : portalUser.getPortalUserType().getId();

            MasterData masterData = resolveMasterData(portalUserTypeId);

            if (!isMasterDataExists(
                    masterData,
                    portalUserRequestDto.getMasterdataId())) {

                throw new ResourceNotFoundException(
                        masterData + " not found with id: "
                                + portalUserRequestDto.getMasterdataId()
                );
            }

            portalUser.setMasterData(masterData);
            portalUser.setMasterdataId(
                    portalUserRequestDto.getMasterdataId()
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
        portalUserMapper.updatePortalUserEntity(portalUser, portalUserRequestDto);

        PortalUser updatedEntity = portalUserrepository.save(portalUser);

        PortalUserResponseDto prtalUserResponse =
                portalUserMapper.portalUserResponseDto(updatedEntity);

        // Enrich masterData as IdValueDto
        prtalUserResponse.setMasterData(
                buildMasterDataIdValueDto(updatedEntity.getMasterData(), updatedEntity.getMasterdataId())
        );

        return prtalUserResponse;
    }

    // ------------------------------------------------------------------ SOFT DELETE

    @Override
    @Transactional
    public PortalUserResponseDto changeStatus(String portalUserId, Boolean isActive) {

        PortalUser portalUser = findByPortalUserId(portalUserId);

        portalUser.setIsActive(isActive);

        log.info("Changing status for portal user id: {} -> isActive={}",
                portalUserId,
                isActive
        );

        return portalUserMapper.portalUserResponseDto(
                portalUserrepository.save(portalUser));
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

//    private void validateDuplicateUser(PortalUserRequestDto request) {
//
//        if (portalUserrepository.existsByEmail(request.getEmail()) || portalUserrepository.existsByPhone(request.getPhone())) {
//            throw new DuplicateResourceException(
//                    PortalUserConstants.USER_ALREADY_EXISTS + " with email "+request.getEmail());
//        }
//    }

    // ------------------------------------------------------------------
    // Private Helpers
    // ------------------------------------------------------------------

    /**
     * Used by GET — excludes inactive records.
     */
    private PortalUser findByPortalUserIdAndIsActive(String portalUserId) {
        return portalUserrepository.findByPortalUserIdAndIsActiveTrue(portalUserId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(PortalUserConstants.USER_NOT_FOUND + portalUserId));
    }

    private PortalUser findByPortalUserId(String portalUserId) {
        return portalUserrepository.findByPortalUserId(portalUserId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                PortalUserConstants.USER_NOT_FOUND + portalUserId));
    }
    private MasterData resolveMasterData(Long portalUserTypeId) {
        return switch (portalUserTypeId.intValue()) {
            case 1 -> MasterData.UNIVERSITY;
            case 2 -> MasterData.POLITICAL_PARTY;
            case 3 -> MasterData.AAQIL;
            default -> throw new ResourceNotFoundException("Invalid portal user type");
        };
    }
// checks if the master data exists for the given type and id, used in both create and update flows to validate the masterdataId before setting it on the entity
    private boolean isMasterDataExists(MasterData type, Long id) {
        return switch (type) {
            case UNIVERSITY -> universityRepository.existsById(id);
            case POLITICAL_PARTY -> politicalPartyRepository.existsById(id);
            case AAQIL -> aaqilRepository.existsById(id);
        };
    }

// to get the name of master data id
    private String getMasterDataName(MasterData masterData, Long masterdataId) {
        return switch (masterData) {
            case UNIVERSITY -> universityRepository.findById(masterdataId)
                    .map(MasterDataUniversity::getUniversityName)
                    .orElseThrow(() ->
                            new ResourceNotFoundException("University not found"));

            case POLITICAL_PARTY -> politicalPartyRepository.findById(masterdataId)
                    .map(MasterDataPoliticalParty::getPartyName)
                    .orElseThrow(() ->
                            new ResourceNotFoundException("Political party not found"));

            case AAQIL -> aaqilRepository.findById(masterdataId)
                    .map(MasterDataAaqilType::getValue)
                    .orElseThrow(() ->
                            new ResourceNotFoundException("Aaqil not found"));
        };
    }

    /**
     * Resolves the human-readable name/value for the given master data type and id,
     * and returns it as an {@link IdValueDto} so the response is consistent with
     * all other FK fields:
     *
     * <pre>
     *   portalUserTypeId=1  →  "masterData": { "id": 3, "value": "Hargeisa University" }
     *   portalUserTypeId=2  →  "masterData": { "id": 2, "value": "Peace Party"         }
     *   portalUserTypeId=3  →  "masterData": { "id": 1, "value": "Senior Aaqil"        }
     * </pre>
     */
    private IdValueDto buildMasterDataIdValueDto(MasterData masterData, Long masterdataId) {
        String name = switch (masterData) {
            case UNIVERSITY -> universityRepository.findById(masterdataId)
                    .map(MasterDataUniversity::getUniversityName)
                    .orElseThrow(() -> new ResourceNotFoundException("University not found"));

            case POLITICAL_PARTY -> politicalPartyRepository.findById(masterdataId)
                    .map(MasterDataPoliticalParty::getPartyName)
                    .orElseThrow(() -> new ResourceNotFoundException("Political party not found"));

            case AAQIL -> aaqilRepository.findById(masterdataId)
                    .map(MasterDataAaqilType::getValue)
                    .orElseThrow(() -> new ResourceNotFoundException("Aaqil not found"));
        };

        return IdValueDto.builder()
                .id(masterdataId)
                .value(name)
                .build();
    }
}
