package com.nec.middleware.hr.service.impl;

import com.nec.middleware.Lookups.repository.LookupGenderRepository;
import com.nec.middleware.Lookups.repository.LookupPaymentMethodsRepository;
import com.nec.middleware.exception.DuplicateResourceException;
import com.nec.middleware.exception.ResourceNotFoundException;
import com.nec.middleware.exception.ValidationException;
import com.nec.middleware.hr.constant.UniversityTraineeConstants;
import com.nec.middleware.hr.dto.request.UniversityTraineeFilterRequestDto;
import com.nec.middleware.hr.dto.request.UniversityTraineeRequestDto;
import com.nec.middleware.hr.dto.response.UniversityTraineeResponseDto;
import com.nec.middleware.hr.entity.UniversityTrainee;
import com.nec.middleware.hr.mapper.UniversityTraineeMapper;
import com.nec.middleware.hr.repository.UniversityTraineeRepository;
import com.nec.middleware.hr.service.UniversityTraineeService;
import com.nec.middleware.hr.specification.UniversityTraineeSearchSpecification;
import com.nec.middleware.idGenerator.Enum.ModuleCode;
import com.nec.middleware.idGenerator.service.UniqueIdGeneratorService;
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

import java.time.Year;

@Slf4j
@Service
@RequiredArgsConstructor
public class UniversityTraineeServiceImpl implements UniversityTraineeService {

    private final UniversityTraineeRepository universityTraineerepository;
    private final UniversityTraineeMapper universityTraineeMapper;
    private final UniqueIdGeneratorService uniqueIdGeneratorService;
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

        UniversityTrainee universityTraineeEntity = universityTraineeMapper.toEntity(requestDto);

        // LOOKUPS
        universityTraineeEntity.setGender(
                genderRepository.findById(requestDto.getGenderId())
                        .orElseThrow(() ->
                                new ResourceNotFoundException("Gender not found"))
        );

        universityTraineeEntity.setPaymentMethod(
                paymentMethodRepository.findById(requestDto.getPaymentMethodId())
                        .orElseThrow(() ->
                                new ResourceNotFoundException("Payment method not found"))
        );

        // MASTER DATA
        universityTraineeEntity.setUniversity(
                universityRepository.findById(requestDto.getUniversityId())
                        .orElseThrow(() ->
                                new ResourceNotFoundException("University not found"))
        );

        universityTraineeEntity.setRegion(
                regionRepository.findById(requestDto.getRegionId())
                        .orElseThrow(() ->
                                new ResourceNotFoundException("Region not found"))
        );

        universityTraineeEntity.setDistrict(
                districtRepository.findById(requestDto.getDistrictId())
                        .orElseThrow(() ->
                                new ResourceNotFoundException("District not found"))
        );

        universityTraineeEntity.setCity(
                cityRepository.findById(requestDto.getCityId())
                        .orElseThrow(() ->
                                new ResourceNotFoundException("City not found"))
        );

        // 4. Generate code — only after all lookups succeeded
        universityTraineeEntity.setUniversityTraineeId(
                generateUniversityTraineeNumber()
        );

        UniversityTrainee savedEntity = universityTraineerepository.save(universityTraineeEntity);

        return universityTraineeMapper.toResponseDto(savedEntity);
    }


    @Override
    @Transactional
    public UniversityTraineeResponseDto updateTrainee(
            String universityTraineeId,
            UniversityTraineeRequestDto requestDto) {

        log.info("Updating university trainee id: {}", universityTraineeId);

        UniversityTrainee universityTrainee =
                findByUniversityTraineeId(universityTraineeId);

        universityTraineeMapper.updateUniversityTraineeEntity(universityTrainee, requestDto);
        validateTraineeForUpdate(requestDto, universityTrainee.getId());
        if (requestDto.getGenderId() != null) {
            universityTrainee.setGender(
                    genderRepository.findById(requestDto.getGenderId())
                            .orElseThrow(() ->
                                    new ResourceNotFoundException("Gender not found"))
            );
        }

        if (requestDto.getPaymentMethodId() != null) {
            universityTrainee.setPaymentMethod(
                    paymentMethodRepository.findById(requestDto.getPaymentMethodId())
                            .orElseThrow(() ->
                                    new ResourceNotFoundException("Payment method not found"))
            );
        }

        if (requestDto.getUniversityId() != null) {
            universityTrainee.setUniversity(
                    universityRepository.findById(requestDto.getUniversityId())
                            .orElseThrow(() ->
                                    new ResourceNotFoundException("University not found"))
            );
        }

        if (requestDto.getRegionId() != null) {
            universityTrainee.setRegion(
                    regionRepository.findById(requestDto.getRegionId())
                            .orElseThrow(() ->
                                    new ResourceNotFoundException("Region not found"))
            );
        }

        if (requestDto.getDistrictId() != null) {
            universityTrainee.setDistrict(
                    districtRepository.findById(requestDto.getDistrictId())
                            .orElseThrow(() ->
                                    new ResourceNotFoundException("District not found"))
            );
        }

        if (requestDto.getCityId() != null) {
            universityTrainee.setCity(
                    cityRepository.findById(requestDto.getCityId())
                            .orElseThrow(() ->
                                    new ResourceNotFoundException("City not found"))
            );
        }

        UniversityTrainee updatedEntity = universityTraineerepository.save(universityTrainee);

        return universityTraineeMapper.toResponseDto(updatedEntity);
    }


    @Override
    @org.springframework.transaction.annotation.Transactional(readOnly = true)
    public UniversityTraineeResponseDto getTraineeByUniversityTraineeId(
            String universityTraineeId) {

        return universityTraineeMapper.toResponseDto(
                findByUniversityTraineeId(universityTraineeId)
        );
    }



    // ------------------------------------------------------------------ GET ALL

    @Override
    @Transactional(readOnly = true)
    public Page<UniversityTraineeResponseDto> getAllUniversityTrainees(
            UniversityTraineeFilterRequestDto request,
            int page,
            int size) {

        if (request == null) {
            request = new UniversityTraineeFilterRequestDto();
        }

        Pageable pageable = PageRequest.of(
                page,
                size,
                Sort.by(Sort.Direction.DESC, "createdAt")
        );

        return universityTraineerepository.findAll(
                        UniversityTraineeSearchSpecification.buildSpecification(request),
                        pageable
                )
                .map(universityTraineeMapper::toResponseDto);
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

        log.info("Changing trainee status: {} -> isActive={}",
                universityTraineeId,
                isActive);

        return universityTraineeMapper.toResponseDto(universityTraineerepository.save(entity));
    }

 //--------------------------------------------------------------------Code generation

    public String generateUniversityTraineeNumber() {

        String prefix = UniversityTraineeConstants.CODE_PREFIX+"-"
                + Year.now().getValue()+"-";
        return uniqueIdGeneratorService.generateId(
                ModuleCode.UNIVERSITY_TRAINEE,
                prefix
        );
    }

    // ------------------------------------------------------------------ VALIDATION

    private void validateTrainee(UniversityTraineeRequestDto dto) {

        if (universityTraineerepository.existsByEmail(dto.getEmail()) || universityTraineerepository.existsByPhone(dto.getEmail())) {
            throw new DuplicateResourceException(
                    UniversityTraineeConstants.TRAINEE_ALREADY_EXISTS
            );
        }
    }

    private void validateTraineeForUpdate(
            UniversityTraineeRequestDto dto,
            Long id) {

        if (universityTraineerepository.existsByEmailAndIsActiveTrueAndIdNot(
                dto.getEmail(), id)) {
            throw new ValidationException(
                    UniversityTraineeConstants.TRAINEE_ALREADY_EXISTS
            );
        }

        if (universityTraineerepository.existsByPhoneAndIsActiveTrueAndIdNot(
                dto.getPhone(), id)) {
            throw new ValidationException(
                    UniversityTraineeConstants.TRAINEE_ALREADY_EXISTS
            );
        }
    }

    // ------------------------------------------------------------------ HELPERS

    private UniversityTrainee findByUniversityTraineeId(
            String universityTraineeId) {

        return universityTraineerepository.findByUniversityTraineeId(
                universityTraineeId
        ).orElseThrow(() ->
                new ResourceNotFoundException(
                        UniversityTraineeConstants.TRAINEE_NOT_FOUND
                                + universityTraineeId
                ));
    }
}