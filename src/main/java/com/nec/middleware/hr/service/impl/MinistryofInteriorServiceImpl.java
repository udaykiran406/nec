package com.nec.middleware.hr.service.impl;

import com.nec.middleware.Lookups.entity.ThirdPartyStatus;
import com.nec.middleware.Lookups.repository.LookupGenderRepository;
import com.nec.middleware.Lookups.repository.LookupMOITitlesRepository;
import com.nec.middleware.Lookups.repository.ThirdPartyStatusRepository;
import com.nec.middleware.exception.ResourceNotFoundException;
import com.nec.middleware.exception.ValidationException;
import com.nec.middleware.hr.constant.MinistryofInteriorConstants;
import com.nec.middleware.hr.dto.request.MinistryofInteriorFilterRequestDto;
import com.nec.middleware.hr.dto.request.MinistryofInteriorRequestDto;
import com.nec.middleware.hr.dto.response.MinistryofInteriorResponseDto;
import com.nec.middleware.hr.entity.MinistryofInterior;
import com.nec.middleware.hr.mapper.MinistryofInteriorMapper;
import com.nec.middleware.hr.repository.MinistryofInteriorRepository;
import com.nec.middleware.hr.service.MinistryofInteriorService;
import com.nec.middleware.hr.specification.MinistryofInteriorSearchSpecification;
import com.nec.middleware.hr.util.FileStorageUtil;
import com.nec.middleware.idGenerator.Enum.ModuleCode;
import com.nec.middleware.idGenerator.service.UniqueIdGeneratorService;
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
public class MinistryofInteriorServiceImpl implements MinistryofInteriorService {

    private final MinistryofInteriorRepository ministryofInteriorRepository;
    private final MinistryofInteriorMapper ministryMapper;
    private final LookupMOITitlesRepository moiTitlesRepository;
    private final LookupGenderRepository genderRepository;
    private final ThirdPartyStatusRepository statusRepository;
    private final MasterDataRepository regionRepository;
    private final DistrictRepository districtRepository;
    private final CityRepository cityRepository;
    private final VoterRegistrationCenterRepository vrcRepository;
    private final UniqueIdGeneratorService uniqueIdGeneratorService;

    private final FileStorageUtil fileStorageUtil;

    private static final String PHOTO_SUB_FOLDER = "ministry_of_interior";
    // ------------------------------------------------------------------ SAVE / UPDATE

    @Override
    @Transactional
    public MinistryofInteriorResponseDto saveMinistryofInterior(MinistryofInteriorRequestDto ministryofInteriorRequestDto , MultipartFile photo) {

        validateMinistryOfInterior(ministryofInteriorRequestDto);

        MinistryofInterior ministryofInteriorEntity = ministryMapper.toEntity(ministryofInteriorRequestDto);

        resolveAndSetForeignKeys(ministryofInteriorEntity, ministryofInteriorRequestDto);

        // 4. Generate code — only after all lookups succeeded
        ministryofInteriorEntity.setMinistryofInteriorId(generateMinistryofInteriorNumber());

        // store photo AFTER portalUserId is generated
        String storedPhotoPath = fileStorageUtil.storePhoto(
                photo,
                PHOTO_SUB_FOLDER,
                ministryofInteriorEntity.getMinistryofInteriorId()
        );
        ministryofInteriorEntity.setPhotoPath(storedPhotoPath);

        MinistryofInterior moi = ministryofInteriorRepository.save(ministryofInteriorEntity);
        log.info("Ministry of interior created: ministryofInteriorId='{}'", moi.getMinistryofInteriorId());
        return ministryMapper.toResponseDto(moi);
    }

    // ------------------------------------------------------------------ READ

    @Override
    @Transactional(readOnly = true)
    public MinistryofInteriorResponseDto getMinistryofInteriorById(String ministryofInteriorId) {
        return ministryMapper.toResponseDto(findByMinistryofInteriorId(ministryofInteriorId));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<MinistryofInteriorResponseDto> getAllMinistryofInterior(
            MinistryofInteriorFilterRequestDto filterDto,
            int page,
            int size) {

        if (filterDto == null) {
            filterDto = new MinistryofInteriorFilterRequestDto();
        }

        Pageable pageable = PageRequest.of(
                page,
                size,
                Sort.by(Sort.Direction.DESC, "createdAt")
        );

        return ministryofInteriorRepository.findAll(
                        MinistryofInteriorSearchSpecification.buildSpecification(filterDto),
                        pageable)
                .map(ministryMapper::toResponseDto);
    }

    // ------------------------------------------------------------------ SOFT DELETE

    @Override
    @Transactional
    public MinistryofInteriorResponseDto changeStatus(
            String ministryofInteriorId,
            Boolean isActiveFlag) {

        MinistryofInterior ministryofInterior = findByMinistryofInteriorId(ministryofInteriorId);

        ministryofInterior.setIsActive(isActiveFlag);

        log.info("Moi status changed with id: {}", ministryofInterior);

        return ministryMapper.toResponseDto(
                ministryofInteriorRepository.save(ministryofInteriorRepository.save(ministryofInterior)));
    }

    //-------------------------------------------------------------Update

    @Override
    @Transactional
    public MinistryofInteriorResponseDto updateMinistryofInterior(
            String ministryofInteriorId,
            MinistryofInteriorRequestDto ministryofInteriorRequestDto,MultipartFile photo) {
        log.info("Update ministry of interior request, ministryofInteriorId='{}'", ministryofInteriorId);

        MinistryofInterior ministryofInterior = findByMinistryofInteriorId(ministryofInteriorId);

        validateMinistryOfInteriorForUpdate(
                ministryofInteriorRequestDto,
                ministryofInterior.getId()
        );
        // Only touch the photo if a new file was actually sent — otherwise
        // keep whatever is already on the entity.
        if (photo != null && !photo.isEmpty()) {
            fileStorageUtil.deleteIfExists(ministryofInterior.getPhotoPath());

            String newPhotoPath = fileStorageUtil.storePhoto(
                    photo,
                    PHOTO_SUB_FOLDER,
                    ministryofInterior.getMinistryofInteriorId()
            );

            ministryofInteriorRequestDto.setPhotoPath(newPhotoPath);
        }

        updateMinistryofInteriorEntity(
                ministryofInterior,
                ministryofInteriorRequestDto
        );

        MinistryofInterior savedEntity = ministryofInteriorRepository.save(ministryofInterior);
        log.info("Ministry of interior updated: ministryofInteriorId='{}'", savedEntity.getMinistryofInteriorId());

        return ministryMapper.toResponseDto(
                savedEntity
        );
    }

//------------------------------------------------------code generation

    public String generateMinistryofInteriorNumber() {

        String prefix = MinistryofInteriorConstants.CODE_PREFIX+"-"
                + Year.now().getValue() + "-";
        return uniqueIdGeneratorService.generateId(
                ModuleCode.MINISTRY_OF_INTERIOR,
                prefix
        );
    }

    // ------------------------------------------------------------------ Validation

    public void validateMinistryOfInterior(MinistryofInteriorRequestDto ministryofInteriorRequestDto) {

        if (ministryofInteriorRepository.existsByEmail(ministryofInteriorRequestDto.getEmail()) ||
                ministryofInteriorRepository.existsByPhone(ministryofInteriorRequestDto.getPhone())) {

            throw new ValidationException(
                    MinistryofInteriorConstants.MINISTRY_OF_INTERIOR_ALREADY_EXISTS
            );
        }
    }

    private void validateMinistryOfInteriorForUpdate(
            MinistryofInteriorRequestDto request,
            Long id) {

        if (ministryofInteriorRepository.existsByEmailAndIdNot(
                request.getEmail(), id)
                ||
                ministryofInteriorRepository.existsByPhoneAndIdNot(
                        request.getPhone(), id)) {

            throw new ValidationException(
                    MinistryofInteriorConstants.MINISTRY_OF_INTERIOR_ALREADY_EXISTS
            );
        }
    }

    private MinistryofInterior findByMinistryofInteriorId(String ministryofInteriorId) {
        return ministryofInteriorRepository.findByMinistryofInteriorId(ministryofInteriorId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                MinistryofInteriorConstants.MINISTRY_OF_INTERIOR_NOT_FOUND + ministryofInteriorId));
    }

    /**
     * Shared FK resolution used by both {@code saveMinistryofInterior} and
     */
    public void resolveAndSetForeignKeys(
            MinistryofInterior entity,
            MinistryofInteriorRequestDto dto) {
        log.debug("Resolving FKs for ministryofInteriorId='{}'", entity.getMinistryofInteriorId());

        entity.setMoiTitle(
                moiTitlesRepository.findById(dto.getMoiTitleId())
                        .orElseThrow(() ->
                                new ResourceNotFoundException("Ministry of Interior Title not found with id: " + dto.getMoiTitleId()))
        );

        entity.setGender(
                genderRepository.findById(dto.getGenderId())
                        .orElseThrow(() ->
                                new ResourceNotFoundException("Gender not found with id: " + dto.getGenderId()))
        );

        ThirdPartyStatus pendingStatus =
                statusRepository
                        .findByCode("PENDING")
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "PENDING status not configured"));

        entity.setStatus(pendingStatus);

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

        entity.setVrc(
                vrcRepository.findById(dto.getVrcId())
                        .orElseThrow(() ->
                                new ResourceNotFoundException("VRC not found with id: " + dto.getVrcId()))
        );
    }

    public void updateMinistryofInteriorEntity(
            MinistryofInterior moi,
            MinistryofInteriorRequestDto moiRequestDto) {

        moi.setName(moiRequestDto.getName());

        moi.setAge(moiRequestDto.getAge());

        moi.setPhone(moiRequestDto.getPhone());

        moi.setEmail(moiRequestDto.getEmail());
        moi.setUpdatedBy(moiRequestDto.getUpdatedBy());

        moi.setMoiTitle(
                moiTitlesRepository.findById(moiRequestDto.getMoiTitleId())
                        .orElseThrow(() ->
                                new ResourceNotFoundException("Title not found"))
        );

        moi.setGender(
                genderRepository.findById(moiRequestDto.getGenderId())
                        .orElseThrow(() ->
                                new ResourceNotFoundException("Gender not found"))
        );

        moi.setRegion(
                regionRepository.findById(moiRequestDto.getRegionId())
                        .orElseThrow(() ->
                                new ResourceNotFoundException("Region not found"))
        );

        moi.setDistrict(
                districtRepository.findById(moiRequestDto.getDistrictId())
                        .orElseThrow(() ->
                                new ResourceNotFoundException("District not found"))
        );

        moi.setCity(
                cityRepository.findById(moiRequestDto.getCityId())
                        .orElseThrow(() ->
                                new ResourceNotFoundException("City not found"))
        );

        moi.setVrc(
                vrcRepository.findById(moiRequestDto.getVrcId())
                        .orElseThrow(() ->
                                new ResourceNotFoundException("VRC not found"))
        );
    }
}