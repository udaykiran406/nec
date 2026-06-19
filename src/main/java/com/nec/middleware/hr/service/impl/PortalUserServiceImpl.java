package com.nec.middleware.hr.service.impl;

import com.nec.middleware.Lookups.entity.MinistryOfInteriorTitles;
import com.nec.middleware.Lookups.repository.LookupGenderRepository;
import com.nec.middleware.Lookups.repository.LookupMOITitlesRepository;
import com.nec.middleware.Lookups.repository.LookupPortalUserTypeRepository;
import com.nec.middleware.Lookups.repository.LookupRoleRepository;
import com.nec.middleware.exception.DuplicateResourceException;
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
import com.nec.middleware.hr.util.FileStorageUtil;
import com.nec.middleware.idGenerator.Enum.ModuleCode;
import com.nec.middleware.idGenerator.service.UniqueIdGeneratorService;
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
import org.springframework.web.multipart.MultipartFile;

import java.time.Year;

@Slf4j
@Service
@RequiredArgsConstructor
public class PortalUserServiceImpl implements PortalUserService {

    private final PortalUserRepository portalUserrepository;
    private final UniqueIdGeneratorService uniqueIdGeneratorService;
    private final PortalUserMapper portalUserMapper;
    private final MasterDataRepository regionRepository;
    private final DistrictRepository districtRepository;
    private final CityRepository cityRepository;
    private final UniversityRepository universityRepository;
    private final LookupGenderRepository genderRepository;
    private final LookupPortalUserTypeRepository portalUserTypeRepository;
    private final LookupRoleRepository roleRepository;
    private final PoliticalPartyRepository politicalPartyRepository;
    private final LookupMOITitlesRepository moiTitlesRepository;
    private final FileStorageUtil fileStorageUtil;

    private static final String PHOTO_SUB_FOLDER = "portal-users";

    // ------------------------------------------------------------------ SAVE

    @Override
    public PortalUserResponseDto createPortalUser(PortalUserRequestDto portalUserRequest , MultipartFile photo) {


        log.info("Create portal user request received");
        validateDuplicateUser(portalUserRequest);


        PortalUser portalUserEntity = portalUserMapper.portalUserEntity(portalUserRequest);

        resolveAndSetForeignKeys(portalUserEntity, portalUserRequest);

        // 4. Generate code — only after all lookups succeeded
        portalUserEntity.setPortalUserId(generatePortalUserNumber());
         // store photo AFTER portalUserId is generated
        String storedPhotoPath = fileStorageUtil.storePhoto(
                photo,
                PHOTO_SUB_FOLDER,
                portalUserEntity.getPortalUserId()
        );
        portalUserEntity.setPhotoPath(storedPhotoPath);

        PortalUser savedEntity = portalUserrepository.save(portalUserEntity);

        PortalUserResponseDto response =
                portalUserMapper.portalUserResponseDto(savedEntity);

        // Enrich masterData as IdValueDto: { id: masterdataId, value: "University Name / Party Name / MOI Value" }
        enrichMasterData(response, savedEntity.getMasterData(), savedEntity.getMasterdataId());

        log.info("Portal user created: portalUserId='{}'", savedEntity.getPortalUserId());

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
            PortalUserRequestDto portalUserRequestDto, MultipartFile photo) {
        log.info("Update portal user request, portalUserId='{}'", portalUserId);

        PortalUser portalUser = findByPortalUserId(portalUserId);
        validateDuplicateUserForUpdate(
                portalUserRequestDto,
                portalUser.getId()
        );
        // Only touch the photo if a new file was actually sent — otherwise
        // keep whatever is already on the entity.
        if (photo != null && !photo.isEmpty()) {
            fileStorageUtil.deleteIfExists(portalUser.getPhotoPath());

            String newPhotoPath = fileStorageUtil.storePhoto(
                    photo,
                    PHOTO_SUB_FOLDER,
                    portalUser.getPortalUserId()
            );

            portalUserRequestDto.setPhotoPath(newPhotoPath);
        }

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
        log.info("Portal user updated: portalUserId='{}'", updatedEntity.getPortalUserId());

        return prtalUserResponse;
    }

    // ------------------------------------------------------------------ SOFT DELETE

    @Override
    @Transactional
    public PortalUserResponseDto changeStatus(String portalUserId, Boolean isActive) {

        PortalUser portalUser = findByPortalUserId(portalUserId);

        portalUser.setIsActive(isActive);

        PortalUser savedPortalUser = portalUserrepository.save(portalUser);
        log.info("Portal user status changed: portalUserId='{}', isActive={}", savedPortalUser.getPortalUserId(), isActive);
        return portalUserMapper.portalUserResponseDto(savedPortalUser);
    }
//--------------------------------------------------------------------code generation
    public String generatePortalUserNumber() {

        String prefix= PortalUserConstants.CODE_PREFIX+"-"
                + Year.now().getValue()+"-";
        return uniqueIdGeneratorService.generateId(
                ModuleCode.PORTAL_USER,
                prefix
        );
    }

    // ------------------------------------------------------------------
    // Validation
    // ------------------------------------------------------------------

    public void validateDuplicateUser(PortalUserRequestDto request) {

        if (portalUserrepository.existsByEmail(request.getEmail()) || portalUserrepository.existsByPhone(request.getPhone())) {
            throw new DuplicateResourceException(
                    PortalUserConstants.USER_ALREADY_EXISTS);
        }

    }

    private void validateDuplicateUserForUpdate(
            PortalUserRequestDto request,
            Long id) {

        if (request.getEmail() != null &&
                portalUserrepository.existsByEmailAndIdNot(
                        request.getEmail(), id)) {
            throw new DuplicateResourceException("Email already exists");
        }

        if (request.getPhone() != null &&
                portalUserrepository.existsByPhoneAndIdNot(
                        request.getPhone(), id)) {
            throw new DuplicateResourceException("Phone already exists");
        }
    }
    // ------------------------------------------------------------------
    // Private Helpers
    // ------------------------------------------------------------------


    private PortalUser findByPortalUserId(String portalUserId) {
        return portalUserrepository.findByPortalUserId(portalUserId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                PortalUserConstants.USER_NOT_FOUND + portalUserId));
    }

    public void resolveAndSetForeignKeys(
            PortalUser entity,
            PortalUserRequestDto dto) {
        log.debug("Resolving FKs for portalUserId='{}'", entity.getPortalUserId());

        entity.setGender(
                genderRepository.findById(dto.getGenderId())
                        .orElseThrow(() ->
                                new ResourceNotFoundException("Gender not found with id: " + dto.getGenderId()))
        );

        entity.setRole(
                roleRepository.findById(dto.getRoleId())
                        .orElseThrow(() ->
                                new ResourceNotFoundException("Role not found with id: " + dto.getRoleId()))
        );

        entity.setPortalUserType(
                portalUserTypeRepository.findById(dto.getPortalUserTypeId())
                        .orElseThrow(() ->
                                new ResourceNotFoundException("Portal User Type not found with id: " + dto.getPortalUserTypeId()))
        );

        MasterData masterData = resolveMasterData(dto.getPortalUserTypeId());

        if (!isMasterDataExists(masterData, dto.getMasterdataId())) {
            throw new ResourceNotFoundException(
                    masterData + " not found with id: " + dto.getMasterdataId()
            );
        }

        entity.setMasterData(masterData);
        entity.setMasterdataId(dto.getMasterdataId());

        entity.setRegion(
                regionRepository.findById(dto.getRegionId())
                        .orElseThrow(() ->
                                new ResourceNotFoundException("Region not found with id: " + dto.getRegionId()))
        );

        entity.setDistrict(
                districtRepository.findById(dto.getDistrictId())
                        .orElseThrow(() ->
                                new ResourceNotFoundException("District not found with id: " + dto.getDistrictId()))
        );

        entity.setCity(
                cityRepository.findById(dto.getCityId())
                        .orElseThrow(() ->
                                new ResourceNotFoundException("City not found with id: " + dto.getCityId()))
        );
    }

    public void enrichMasterData(
            PortalUserResponseDto response,
            MasterData masterData,
            Long masterdataId) {
        response.setMasterData(buildMasterDataIdValueDto(masterData, masterdataId));
    }

    private MasterData resolveMasterData(Long portalUserTypeId) {
        return switch (portalUserTypeId.intValue()) {
            case 1 -> MasterData.UNIVERSITY;
            case 2 -> MasterData.POLITICAL_PARTY;
            case 3 -> MasterData.MINISTRY_OF_INTERIOR;
            default ->
                    throw new ResourceNotFoundException("Invalid portal user type");
        };
    }
// checks if the master data exists for the given type and id, used in both create and update flows to validate the masterdataId before setting it on the entity
    private boolean isMasterDataExists(MasterData type, Long id) {
        return switch (type) {
            case UNIVERSITY -> universityRepository.existsById(id);
            case POLITICAL_PARTY -> politicalPartyRepository.existsById(id);
            case MINISTRY_OF_INTERIOR -> moiTitlesRepository.existsById(id);
        };
    }

    /**
     * Resolves the human-readable name/value for the given master data type and id,
     * and returns it as an {@link IdValueDto} so the response is consistent with
     * all other FK fields:
     * <pre>
     *   portalUserTypeId=1  →  "masterData": { "id": 3, "value": "Hargeisa University" }
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

            case MINISTRY_OF_INTERIOR -> moiTitlesRepository.findById(masterdataId)
                    .map(MinistryOfInteriorTitles::getValue)
                    .orElseThrow(() -> new ResourceNotFoundException("Ministry of Interior not found"));
        };

        return IdValueDto.builder()
                .id(masterdataId)
                .value(name)
                .build();
    }
}
