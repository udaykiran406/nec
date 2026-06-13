package com.nec.middleware.hr.service.impl;

import com.nec.middleware.Lookups.repository.LookupGenderRepository;
import com.nec.middleware.Lookups.repository.ThirdPartyStatusRepository;
import com.nec.middleware.exception.ResourceNotFoundException;
import com.nec.middleware.exception.ValidationException;
import com.nec.middleware.hr.constant.AaqilConstants;
import com.nec.middleware.hr.dto.request.AaqilFilterRequestDto;
import com.nec.middleware.hr.dto.request.AaqilRequestDto;
import com.nec.middleware.hr.dto.response.AaqilResponseDto;
import com.nec.middleware.hr.entity.Aaqil;
import com.nec.middleware.hr.mapper.AaqilMapper;
import com.nec.middleware.hr.repository.AaqilRepository;
import com.nec.middleware.hr.service.AaqilService;
import com.nec.middleware.hr.specification.AaqilSearchSpecification;
import com.nec.middleware.masterdata.entity.MasterDataAaqilType;
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
public class AaqilServiceImpl implements AaqilService {

    private final AaqilRepository aaqilRepository;
    private final AaqilMapper aaqilMapper;

    private final AaqilTypeRepository aaqilTypeRepository;
    private final LookupGenderRepository genderRepository;
    private final ThirdPartyStatusRepository statusRepository;
    private final MasterDataRepository regionRepository;
    private final DistrictRepository districtRepository;
    private final CityRepository cityRepository;

    // ------------------------------------------------------------------ SAVE / UPDATE

    @Override
    @Transactional
    public AaqilResponseDto saveAaqil(AaqilRequestDto aaqilRequestDto) {

        validateAaqil(aaqilRequestDto);

        log.info("Creating Aaqil. Name: {}", aaqilRequestDto.getFullName());

        Aaqil aaqilEntity = aaqilMapper.toEntity(aaqilRequestDto);

        aaqilEntity.setAaqilId(
                generateCode(
                        AaqilConstants.CODE_PREFIX,
                        AaqilConstants.CODE_PAD
                )
        );

        aaqilEntity.setAaqilType(
                aaqilTypeRepository.findById(aaqilRequestDto.getAaqilTypeId())
                        .orElseThrow(() ->
                                new ResourceNotFoundException("Aaqil Type Not Found"))
        );

        aaqilEntity.setGender(
                genderRepository.findById(aaqilRequestDto.getGenderId())
                        .orElseThrow(() ->
                                new ResourceNotFoundException("Gender Not Found"))
        );

        aaqilEntity.setStatus(
                statusRepository.findById(aaqilRequestDto.getStatusId())
                        .orElseThrow(() ->
                                new ResourceNotFoundException("Status Not Found"))
        );

        aaqilEntity.setRegion(
                regionRepository.findById(aaqilRequestDto.getRegionId())
                        .orElseThrow(() ->
                                new ResourceNotFoundException("Region Not Found"))
        );

        aaqilEntity.setDistrict(
                districtRepository.findById(aaqilRequestDto.getDistrictId())
                        .orElseThrow(() ->
                                new ResourceNotFoundException("District Not Found"))
        );

        aaqilEntity.setCity(
                cityRepository.findById(aaqilRequestDto.getCityId())
                        .orElseThrow(() ->
                                new ResourceNotFoundException("City Not Found"))
        );

        Aaqil aaqil = aaqilRepository.save(aaqilEntity);

        return aaqilMapper.toResponseDto(aaqil);
    }

    // ------------------------------------------------------------------ READ

    @Override
    @Transactional(readOnly = true)
    public AaqilResponseDto getAaqilById(String aaqilId) {
        return aaqilMapper.toResponseDto(findByAaqilId(aaqilId));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<AaqilResponseDto> getAllAaqils(
            AaqilFilterRequestDto filterDto,
            int page,
            int size) {

        if (filterDto == null) {
            filterDto = new AaqilFilterRequestDto();
        }

        Pageable pageable = PageRequest.of(
                page,
                size,
                Sort.by(Sort.Direction.DESC, "createdAt")
        );

        return aaqilRepository.findAll(
                        AaqilSearchSpecification.buildSpecification(filterDto),
                        pageable)
                .map(aaqilMapper::toResponseDto);
    }

    // ------------------------------------------------------------------ SOFT DELETE

    @Override
    @Transactional
    public AaqilResponseDto changeStatus(
            String aaqilId,
            Boolean isActiveFlag) {

        Aaqil aaqil = findByAaqilId(aaqilId);

        aaqil.setIsActive(isActiveFlag);

        log.info("Aaqil status changed with id: {}", aaqil);

        return aaqilMapper.toResponseDto(
                aaqilRepository.save(aaqilRepository.save(aaqil)));
    }

    //-------------------------------------------------------------Update

    @Override
    @Transactional
    public AaqilResponseDto updateAaqil(
            String aaqilId,
            AaqilRequestDto aaqilRequestDto) {

        Aaqil aaqil = findByAaqilId(aaqilId);

        validateAaqilForUpdate(
                aaqilRequestDto,
                aaqil.getId()
        );

        updateAaqils(
                aaqil,
                aaqilRequestDto
        );

        aaqilRepository.save(aaqil);

        return aaqilMapper.toResponseDto(
                aaqil
        );
    }

    // ------------------------------------------------------------------ Code generation

    private String generateCode(String codePrefix, int codePad) {
        int next = aaqilRepository.findMaxCodeSequence() + 1;
        return codePrefix + String.format("%0" + codePad + "d", next);
    }

    // ------------------------------------------------------------------ Validation

    private void validateAaqil(AaqilRequestDto aaqilRequestDto) {

        if (aaqilRepository.existsByEmail(aaqilRequestDto.getEmail()) ||
                aaqilRepository.existsByPhone(aaqilRequestDto.getPhone())) {

            throw new ValidationException(
                    AaqilConstants.AAQIL_ALREADY_EXISTS
            );
        }
    }

    private void validateAaqilForUpdate(
            AaqilRequestDto request,
            Long id) {

        if (aaqilRepository.existsByEmailAndIdNot(
                request.getEmail(), id)
                ||
                aaqilRepository.existsByPhoneAndIdNot(
                        request.getPhone(), id)) {

            throw new ValidationException(
                    AaqilConstants.AAQIL_ALREADY_EXISTS
            );
        }
    }

    private Aaqil findByAaqilId(String aaqilId) {
        return aaqilRepository.findByAaqilId(aaqilId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                AaqilConstants.AAQIL_NOT_FOUND + aaqilId));
    }

    public void updateAaqils(
            Aaqil aaqil,
            AaqilRequestDto aaqilRequestDto) {

        if (aaqilRequestDto.getFullName() != null) {
            aaqil.setFullName(aaqilRequestDto.getFullName());
        }

        if (aaqilRequestDto.getAge() != null) {
            aaqil.setAge(aaqilRequestDto.getAge());
        }

        if (aaqilRequestDto.getPhone() != null) {
            aaqil.setPhone(aaqilRequestDto.getPhone());
        }

        if (aaqilRequestDto.getEmail() != null) {
            aaqil.setEmail(aaqilRequestDto.getEmail());
        }

        if (aaqilRequestDto.getAaqilTypeId() != null) {
            aaqil.setAaqilType(
                    aaqilTypeRepository.findById(aaqilRequestDto.getAaqilTypeId())
                            .orElseThrow(() ->
                                    new ResourceNotFoundException("Aaqil Type not found"))
            );
        }

        if (aaqilRequestDto.getGenderId() != null) {
            aaqil.setGender(
                    genderRepository.findById(aaqilRequestDto.getGenderId())
                            .orElseThrow(() ->
                                    new ResourceNotFoundException("Gender not found"))
            );
        }

        if (aaqilRequestDto.getStatusId() != null) {
            aaqil.setStatus(
                    statusRepository.findById(aaqilRequestDto.getStatusId())
                            .orElseThrow(() ->
                                    new ResourceNotFoundException("Status not found"))
            );
        }

        if (aaqilRequestDto.getRegionId() != null) {
            aaqil.setRegion(
                    regionRepository.findById(aaqilRequestDto.getRegionId())
                            .orElseThrow(() ->
                                    new ResourceNotFoundException("Region not found"))
            );
        }

        if (aaqilRequestDto.getDistrictId() != null) {
            aaqil.setDistrict(
                    districtRepository.findById(aaqilRequestDto.getDistrictId())
                            .orElseThrow(() ->
                                    new ResourceNotFoundException("District not found"))
            );
        }

        if (aaqilRequestDto.getCityId() != null) {
            aaqil.setCity(
                    cityRepository.findById(aaqilRequestDto.getCityId())
                            .orElseThrow(() ->
                                    new ResourceNotFoundException("City not found"))
            );
        }
    }
}