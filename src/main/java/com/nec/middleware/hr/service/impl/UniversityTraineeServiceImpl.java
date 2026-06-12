package com.nec.middleware.hr.service.impl;

import com.nec.middleware.Lookups.repository.LookupGenderRepository;
import com.nec.middleware.Lookups.repository.LookupPaymentMethodsRepository;
import com.nec.middleware.exception.ResourceNotFoundException;
import com.nec.middleware.exception.ValidationException;
import com.nec.middleware.hr.constant.UniversityTraineeConstants;
import com.nec.middleware.hr.dto.request.UniversityTraineeListRequestDto;
import com.nec.middleware.hr.dto.request.UniversityTraineeRequestDto;
import com.nec.middleware.hr.dto.response.UniversityTraineeResponseDto;
import com.nec.middleware.hr.entity.UniversityTrainee;
import com.nec.middleware.hr.mapper.UniversityTraineeMapper;
import com.nec.middleware.hr.repository.UniversityTraineeRepository;
import com.nec.middleware.hr.service.UniversityTraineeService;
import com.nec.middleware.hr.specification.UniversityTraineeSearchSpecification;
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
public class UniversityTraineeServiceImpl implements UniversityTraineeService {

    private final UniversityTraineeRepository repository;
    private final UniversityTraineeMapper mapper;

    // ---- Lookup repositories (mirrors PortalUserServiceImpl) ----
    private final LookupGenderRepository genderRepository;
    private final LookupPaymentMethodsRepository paymentMethodRepository;

    // ---- Master data repositories ----
    private final UniversityRepository universityRepository;
    private final MasterDataRepository regionRepository;
    private final DistrictRepository districtRepository;
    private final CityRepository cityRepository;

    // ------------------------------------------------------------------ CREATE
    @Override
    @Transactional
    public UniversityTraineeResponseDto createTrainee(
            UniversityTraineeRequestDto requestDto) {

        log.info("Creating university trainee");

        validateTrainee(requestDto);

        UniversityTrainee entity = mapper.toEntity(requestDto);
        entity.setUniversityTraineeId(generateTraineeId(UniversityTraineeConstants.CODE_PREFIX, UniversityTraineeConstants.CODE_PAD));

        // LOOKUPS
        entity.setGender(
                genderRepository.findById(requestDto.getGenderId())
                        .orElseThrow(() ->
                                new ResourceNotFoundException("Gender not found"))
        );

        entity.setPaymentMethod(
                paymentMethodRepository.findById(requestDto.getPaymentMethodId())
                        .orElseThrow(() ->
                                new ResourceNotFoundException("Payment method not found"))
        );

        // MASTER DATA
        entity.setUniversity(
                universityRepository.findById(requestDto.getUniversityId())
                        .orElseThrow(() ->
                                new ResourceNotFoundException("University not found"))
        );

        entity.setRegion(
                regionRepository.findById(requestDto.getRegionId())
                        .orElseThrow(() ->
                                new ResourceNotFoundException("Region not found"))
        );

        entity.setDistrict(
                districtRepository.findById(requestDto.getDistrictId())
                        .orElseThrow(() ->
                                new ResourceNotFoundException("District not found"))
        );

        entity.setCity(
                cityRepository.findById(requestDto.getCityId())
                        .orElseThrow(() ->
                                new ResourceNotFoundException("City not found"))
        );

        UniversityTrainee savedEntity = repository.save(entity);

        return mapper.toResponseDto(savedEntity);
    }


    @Override
    @Transactional
    public UniversityTraineeResponseDto updateTrainee(
            String universityTraineeId,
            UniversityTraineeRequestDto requestDto) {

        log.info("Updating university trainee id: {}", universityTraineeId);

        UniversityTrainee entity =
                findByUniversityTraineeIdAndIsActive(universityTraineeId);

        mapper.updateEntity(entity, requestDto);
        validateTraineeForUpdate(requestDto, entity.getId());
        if (requestDto.getGenderId() != null) {
            entity.setGender(
                    genderRepository.findById(requestDto.getGenderId())
                            .orElseThrow(() ->
                                    new ResourceNotFoundException("Gender not found"))
            );
        }

        if (requestDto.getPaymentMethodId() != null) {
            entity.setPaymentMethod(
                    paymentMethodRepository.findById(requestDto.getPaymentMethodId())
                            .orElseThrow(() ->
                                    new ResourceNotFoundException("Payment method not found"))
            );
        }

        if (requestDto.getUniversityId() != null) {
            entity.setUniversity(
                    universityRepository.findById(requestDto.getUniversityId())
                            .orElseThrow(() ->
                                    new ResourceNotFoundException("University not found"))
            );
        }

        if (requestDto.getRegionId() != null) {
            entity.setRegion(
                    regionRepository.findById(requestDto.getRegionId())
                            .orElseThrow(() ->
                                    new ResourceNotFoundException("Region not found"))
            );
        }

        if (requestDto.getDistrictId() != null) {
            entity.setDistrict(
                    districtRepository.findById(requestDto.getDistrictId())
                            .orElseThrow(() ->
                                    new ResourceNotFoundException("District not found"))
            );
        }

        if (requestDto.getCityId() != null) {
            entity.setCity(
                    cityRepository.findById(requestDto.getCityId())
                            .orElseThrow(() ->
                                    new ResourceNotFoundException("City not found"))
            );
        }

        UniversityTrainee updatedEntity = repository.save(entity);

        return mapper.toResponseDto(updatedEntity);
    }


    @Override
    @org.springframework.transaction.annotation.Transactional(readOnly = true)
    public UniversityTraineeResponseDto getTraineeByUniversityTraineeId(
            String universityTraineeId) {

        return mapper.toResponseDto(
                findByUniversityTraineeIdAndIsActive(universityTraineeId)
        );
    }



    // ------------------------------------------------------------------ GET ALL

    @Override
    @Transactional(readOnly = true)
    public Page<UniversityTraineeResponseDto> getAllUniversityTrainees(
            UniversityTraineeListRequestDto request,
            int page,
            int size) {

        if (request == null) {
            request = new UniversityTraineeListRequestDto();
        }

        Pageable pageable = PageRequest.of(
                page,
                size,
                Sort.by(Sort.Direction.DESC, "createdAt")
        );

        return repository.findAll(
                        UniversityTraineeSearchSpecification.build(request),
                        pageable
                )
                .map(mapper::toResponseDto);
    }

    // ------------------------------------------------------------------ CHANGE STATUS

    @Override
    @Transactional
    public UniversityTraineeResponseDto changeStatus(
            String universityTraineeId,
            Boolean isActive) {

        UniversityTrainee entity =
                findByUniversityTraineeId(universityTraineeId);

        entity.setIsActive(isActive);
        entity.setIsDeleted(!isActive);

        log.info("Changing trainee status: {} -> isActive={}",
                universityTraineeId,
                isActive);

        return mapper.toResponseDto(repository.save(entity));
    }

    // ------------------------------------------------------------------ CODE GENERATION

    private String generateTraineeId(String codePrefix, int codePad) {
        int next = repository.findMaxCodeSequence() + 1;
        return codePrefix + String.format("%0" + codePad + "d", next);
    }

    // ------------------------------------------------------------------ VALIDATION

    private void validateTrainee(UniversityTraineeRequestDto dto) {

        if (repository.existsByEmailAndIsActiveTrue(dto.getEmail())) {
            throw new ValidationException(
                    UniversityTraineeConstants.TRAINEE_ALREADY_EXISTS
            );
        }

        if (repository.existsByPhoneAndIsActiveTrue(dto.getPhone())) {
            throw new ValidationException(
                    UniversityTraineeConstants.TRAINEE_ALREADY_EXISTS
            );
        }
    }

    private void validateTraineeForUpdate(
            UniversityTraineeRequestDto dto,
            Long id) {

        if (repository.existsByEmailAndIsActiveTrueAndIdNot(
                dto.getEmail(), id)) {
            throw new ValidationException(
                    UniversityTraineeConstants.TRAINEE_ALREADY_EXISTS
            );
        }

        if (repository.existsByPhoneAndIsActiveTrueAndIdNot(
                dto.getPhone(), id)) {
            throw new ValidationException(
                    UniversityTraineeConstants.TRAINEE_ALREADY_EXISTS
            );
        }
    }

    // ------------------------------------------------------------------ HELPERS

    private UniversityTrainee findByUniversityTraineeIdAndIsActive(
            String universityTraineeId) {

        return repository.findByUniversityTraineeIdAndIsActiveTrue(
                universityTraineeId
        ).orElseThrow(() ->
                new ResourceNotFoundException(
                        UniversityTraineeConstants.TRAINEE_NOT_FOUND
                                + universityTraineeId
                ));
    }

    private UniversityTrainee findByUniversityTraineeId(
            String universityTraineeId) {

        return repository.findByUniversityTraineeId(
                universityTraineeId
        ).orElseThrow(() ->
                new ResourceNotFoundException(
                        UniversityTraineeConstants.TRAINEE_NOT_FOUND
                                + universityTraineeId
                ));
    }
}