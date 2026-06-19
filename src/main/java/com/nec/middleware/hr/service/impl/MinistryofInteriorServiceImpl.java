package com.nec.middleware.hr.service.impl;

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

        log.info("Creating moi. Name: {}", ministryofInteriorRequestDto.getName());

        MinistryofInterior moiEntity = ministryMapper.toEntity(ministryofInteriorRequestDto);


        moiEntity.setMoiTitle(
                moiTitlesRepository.findById(ministryofInteriorRequestDto.getMoiTitleId())
                        .orElseThrow(() ->
                                new ResourceNotFoundException("Ministry of Interior Title Not Found"))
        );

        moiEntity.setGender(
                genderRepository.findById(ministryofInteriorRequestDto.getGenderId())
                        .orElseThrow(() ->
                                new ResourceNotFoundException("Gender Not Found"))
        );

        moiEntity.setStatus(
                statusRepository.findById(ministryofInteriorRequestDto.getStatusId())
                        .orElseThrow(() ->
                                new ResourceNotFoundException("Status Not Found"))
        );

        moiEntity.setRegion(
                regionRepository.findById(ministryofInteriorRequestDto.getRegionId())
                        .orElseThrow(() ->
                                new ResourceNotFoundException("Region Not Found"))
        );

        moiEntity.setDistrict(
                districtRepository.findById(ministryofInteriorRequestDto.getDistrictId())
                        .orElseThrow(() ->
                                new ResourceNotFoundException("District Not Found"))
        );

        moiEntity.setCity(
                cityRepository.findById(ministryofInteriorRequestDto.getCityId())
                        .orElseThrow(() ->
                                new ResourceNotFoundException("City Not Found"))
        );

        moiEntity.setVrc(
                vrcRepository.findById(ministryofInteriorRequestDto.getVrcId())
                        .orElseThrow(() ->
                                new ResourceNotFoundException("VRC Not Found"))
        );

        // 4. Generate code — only after all lookups succeeded
        moiEntity.setMinistryofInteriorId(generateMinistryofInteriorNumber());

        // store photo AFTER portalUserId is generated
        String storedPhotoPath = fileStorageUtil.storePhoto(
                photo,
                PHOTO_SUB_FOLDER,
                moiEntity.getMinistryofInteriorId()
        );
        moiEntity.setPhotoPath(storedPhotoPath);

        MinistryofInterior aaqil = ministryofInteriorRepository.save(moiEntity);

        return ministryMapper.toResponseDto(aaqil);
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

        MinistryofInterior moi = findByMinistryofInteriorId(ministryofInteriorId);

        moi.setIsActive(isActiveFlag);

        log.info("Moi status changed with id: {}", moi);

        return ministryMapper.toResponseDto(
                ministryofInteriorRepository.save(ministryofInteriorRepository.save(moi)));
    }

    //-------------------------------------------------------------Update

    @Override
    @Transactional
    public MinistryofInteriorResponseDto updateMinistryofInterior(
            String aaqilId,
            MinistryofInteriorRequestDto ministryofInteriorRequestDto,MultipartFile photo) {

        MinistryofInterior moi = findByMinistryofInteriorId(aaqilId);

        validateMinistryOfInteriorForUpdate(
                ministryofInteriorRequestDto,
                moi.getId()
        );
        // Only touch the photo if a new file was actually sent — otherwise
        // keep whatever is already on the entity.
        if (photo != null && !photo.isEmpty()) {
            fileStorageUtil.deleteIfExists(moi.getPhotoPath());

            String newPhotoPath = fileStorageUtil.storePhoto(
                    photo,
                    PHOTO_SUB_FOLDER,
                    moi.getMinistryofInteriorId()
            );

            ministryofInteriorRequestDto.setPhotoPath(newPhotoPath);
        }

        updateMinistryofInteriorEntity(
                moi,
                ministryofInteriorRequestDto
        );

        MinistryofInterior savedEntity = ministryofInteriorRepository.save(moi);

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

    private void validateMinistryOfInterior(MinistryofInteriorRequestDto ministryofInteriorRequestDto) {

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

    public void updateMinistryofInteriorEntity(
            MinistryofInterior moi,
            MinistryofInteriorRequestDto moiRequestDto) {

        if (moiRequestDto.getName() != null) {
            moi.setName(moiRequestDto.getName());
        }

        if (moiRequestDto.getAge() != null) {
            moi.setAge(moiRequestDto.getAge());
        }

        if (moiRequestDto.getPhone() != null) {
            moi.setPhone(moiRequestDto.getPhone());
        }

        if (moiRequestDto.getEmail() != null) {
            moi.setEmail(moiRequestDto.getEmail());
        }

        if (moiRequestDto.getMoiTitleId() != null) {
            moi.setMoiTitle(
                    moiTitlesRepository.findById(moiRequestDto.getMoiTitleId())
                            .orElseThrow(() ->
                                    new ResourceNotFoundException("Title not found"))
            );
        }

        if (moiRequestDto.getGenderId() != null) {
            moi.setGender(
                    genderRepository.findById(moiRequestDto.getGenderId())
                            .orElseThrow(() ->
                                    new ResourceNotFoundException("Gender not found"))
            );
        }

        if (moiRequestDto.getStatusId() != null) {
            moi.setStatus(
                    statusRepository.findById(moiRequestDto.getStatusId())
                            .orElseThrow(() ->
                                    new ResourceNotFoundException("Status not found"))
            );
        }

        if (moiRequestDto.getRegionId() != null) {
            moi.setRegion(
                    regionRepository.findById(moiRequestDto.getRegionId())
                            .orElseThrow(() ->
                                    new ResourceNotFoundException("Region not found"))
            );
        }

        if (moiRequestDto.getDistrictId() != null) {
            moi.setDistrict(
                    districtRepository.findById(moiRequestDto.getDistrictId())
                            .orElseThrow(() ->
                                    new ResourceNotFoundException("District not found"))
            );
        }

        if (moiRequestDto.getCityId() != null) {
            moi.setCity(
                    cityRepository.findById(moiRequestDto.getCityId())
                            .orElseThrow(() ->
                                    new ResourceNotFoundException("City not found"))
            );
        }

        if (moiRequestDto.getVrcId() != null) {
            moi.setVrc(
                    vrcRepository.findById(moiRequestDto.getVrcId())
                            .orElseThrow(() ->
                                    new ResourceNotFoundException("VRC not found"))
            );
        }
    }
}