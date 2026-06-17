package com.nec.middleware.hr.service.impl;

import com.nec.middleware.Lookups.repository.LookupGenderRepository;
import com.nec.middleware.Lookups.repository.LookupPaymentMethodsRepository;
import com.nec.middleware.exception.DuplicateResourceException;
import com.nec.middleware.exception.ResourceNotFoundException;
import com.nec.middleware.exception.ValidationException;
import com.nec.middleware.hr.constant.UniversityTraineeConstants;
import com.nec.middleware.hr.dto.request.UniversityTraineeFilterRequestDto;
import com.nec.middleware.hr.dto.request.UniversityTraineeRequestDto;
import com.nec.middleware.hr.dto.response.BulkUploadResultDto;
import com.nec.middleware.hr.dto.response.RowErrorDto;
import com.nec.middleware.hr.dto.response.UniversityTraineeResponseDto;
import com.nec.middleware.hr.entity.UniversityTrainee;
import com.nec.middleware.hr.mapper.UniversityTraineeMapper;
import com.nec.middleware.hr.repository.UniversityTraineeRepository;
import com.nec.middleware.hr.service.UniversityTraineeService;
import com.nec.middleware.hr.specification.UniversityTraineeSearchSpecification;
import com.nec.middleware.hr.util.FileStorageUtil;
import com.nec.middleware.hr.util.ParsedRow;
import com.nec.middleware.hr.util.UniversityTraineeCsvImportService;
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
import org.springframework.web.multipart.MultipartFile;

import java.time.Year;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class UniversityTraineeServiceImpl implements UniversityTraineeService {

    private final UniversityTraineeRepository universityTraineerepository;
    private final UniversityTraineeMapper     universityTraineeMapper;
    private final UniqueIdGeneratorService    uniqueIdGeneratorService;

    // Lookup repositories
    private final LookupGenderRepository         genderRepository;
    private final LookupPaymentMethodsRepository paymentMethodRepository;

    // Master data repositories
    private final UniversityRepository  universityRepository;
    private final MasterDataRepository  regionRepository;
    private final DistrictRepository    districtRepository;
    private final CityRepository        cityRepository;

    private final FileStorageUtil                    fileStorageUtil;
    private final UniversityTraineeCsvImportService  csvImportService;
    private final UniversityTraineeRowPersister      rowPersister;

    private static final String PHOTO_SUB_FOLDER = "university-trainees";

    // ------------------------------------------------------------------ SINGLE CREATE

    @Override
    @Transactional
    public UniversityTraineeResponseDto createTrainee(
            UniversityTraineeRequestDto requestDto, MultipartFile photo) {

        log.info("Creating university trainee");

        validateTrainee(requestDto);

        UniversityTrainee entity = universityTraineeMapper.toEntity(requestDto);

        resolveAndSetForeignKeys(entity, requestDto);

        entity.setStatus("PENDING");
        entity.setUniversityTraineeId(generateUniversityTraineeNumber());

        String storedPhotoPath = fileStorageUtil.storePhoto(
                photo, PHOTO_SUB_FOLDER, entity.getUniversityTraineeId());
        entity.setPhotoPath(storedPhotoPath);

        return universityTraineeMapper.toResponseDto(universityTraineerepository.save(entity));
    }

    // ------------------------------------------------------------------ BULK CREATE

    /**
     * Parse the uploaded file and persist every valid row independently.
     *
     * <p><b>Partial-success design:</b> each row is processed inside its own
     * try/catch so a single bad row never rolls back the rows before it.
     * Rows that fail are described in {@link BulkUploadResultDto#getErrors()}.
     *
     * <p>Photos are not supported in bulk mode — {@code photoPath} stays null.
     */
    @Override
    public BulkUploadResultDto<UniversityTraineeResponseDto> bulkCreate(MultipartFile file) {

        log.info("Starting bulk create for University Trainees, file='{}'",
                file.getOriginalFilename());

        // ── Step 1: parse file → list of parsed rows (DTO + rawData) ───
        List<RowErrorDto> parseErrors = new ArrayList<>();
        List<ParsedRow> rows = csvImportService.parse(file, parseErrors);

        log.info("Parsed {} rows from file, {} parse errors", rows.size(), parseErrors.size());

        // ── Step 2: persist each row independently ──────────────────────
        List<UniversityTraineeResponseDto> saved   = new ArrayList<>();
        List<RowErrorDto>                  errors  = new ArrayList<>(parseErrors);

        for (ParsedRow parsedRow : rows) {
            int rowNum = parsedRow.getRowNumber();
            try {
                UniversityTraineeResponseDto response = rowPersister.persistSingleRow(parsedRow.getDto());
                saved.add(response);
                log.info("Row {}: saved as {}", rowNum, response.getUniversityTraineeId());
            } catch (DuplicateResourceException e) {
                log.warn("Row {}: duplicate – {}", rowNum, e.getMessage());
                errors.add(RowErrorDto.builder()
                        .rowNumber(rowNum)
                        .field("email/phone")
                        .message(e.getMessage())
                        .rawData(parsedRow.getRawData())
                        .build());
            } catch (ResourceNotFoundException e) {
                log.warn("Row {}: FK not found – {}", rowNum, e.getMessage());
                errors.add(RowErrorDto.builder()
                        .rowNumber(rowNum)
                        .message(e.getMessage())
                        .rawData(parsedRow.getRawData())
                        .build());
            } catch (Exception e) {
                log.error("Row {}: unexpected error – {}", rowNum, e.getMessage(), e);
                errors.add(RowErrorDto.builder()
                        .rowNumber(rowNum)
                        .message("Unexpected error: " + e.getMessage())
                        .rawData(parsedRow.getRawData())
                        .build());
            }
        }

        int total = rows.size() + parseErrors.size(); // total attempted rows
        log.info("Bulk create complete — total={}, success={}, failures={}",
                total, saved.size(), errors.size());

        return BulkUploadResultDto.<UniversityTraineeResponseDto>builder()
                .totalRows(total)
                .successCount(saved.size())
                .failureCount(errors.size())
                .successRecords(saved)
                .errors(errors)
                .build();
    }

    // ------------------------------------------------------------------ UPDATE

    @Override
    @Transactional
    public UniversityTraineeResponseDto updateTrainee(
            String universityTraineeId,
            UniversityTraineeRequestDto requestDto,
            MultipartFile photo) {

        log.info("Updating university trainee id: {}", universityTraineeId);

        UniversityTrainee entity = findByUniversityTraineeId(universityTraineeId);

        universityTraineeMapper.updateUniversityTraineeEntity(entity, requestDto);
        validateTraineeForUpdate(requestDto, entity.getId());

        if (photo != null && !photo.isEmpty()) {
            fileStorageUtil.deleteIfExists(entity.getPhotoPath());
            entity.setPhotoPath(fileStorageUtil.storePhoto(
                    photo, PHOTO_SUB_FOLDER, entity.getUniversityTraineeId()));
        }

//        if (requestDto.getGenderId() != null)
            entity.setGender(genderRepository.findById(requestDto.getGenderId())
                    .orElseThrow(() -> new ResourceNotFoundException("Gender not found")));

//        if (requestDto.getPaymentMethodId() != null)
            entity.setPaymentMethod(paymentMethodRepository.findById(requestDto.getPaymentMethodId())
                    .orElseThrow(() -> new ResourceNotFoundException("Payment method not found")));

//        if (requestDto.getUniversityId() != null)
            entity.setUniversity(universityRepository.findById(requestDto.getUniversityId())
                    .orElseThrow(() -> new ResourceNotFoundException("University not found")));

//        if (requestDto.getRegionId() != null)
            entity.setRegion(regionRepository.findById(requestDto.getRegionId())
                    .orElseThrow(() -> new ResourceNotFoundException("Region not found")));

//        if (requestDto.getDistrictId() != null)
            entity.setDistrict(districtRepository.findById(requestDto.getDistrictId())
                    .orElseThrow(() -> new ResourceNotFoundException("District not found")));

//        if (requestDto.getCityId() != null)
            entity.setCity(cityRepository.findById(requestDto.getCityId())
                    .orElseThrow(() -> new ResourceNotFoundException("City not found")));

        // save
        UniversityTrainee updated = universityTraineerepository.save(entity);

        log.info("University trainee updated successfully: {}", updated.getUniversityTraineeId());

        return universityTraineeMapper.toResponseDto(updated);
    }

    // ------------------------------------------------------------------ GET BY ID

    @Override
    @Transactional(readOnly = true)
    public UniversityTraineeResponseDto getTraineeByUniversityTraineeId(String universityTraineeId) {
        return universityTraineeMapper.toResponseDto(findByUniversityTraineeId(universityTraineeId));
    }

    // ------------------------------------------------------------------ GET ALL

    @Override
    @Transactional(readOnly = true)
    public Page<UniversityTraineeResponseDto> getAllUniversityTrainees(
            UniversityTraineeFilterRequestDto request,
            int page,
            int size) {

        if (request == null) request = new UniversityTraineeFilterRequestDto();

        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));

        return universityTraineerepository.findAll(
                        UniversityTraineeSearchSpecification.buildSpecification(request), pageable)
                .map(universityTraineeMapper::toResponseDto);
    }

    // ------------------------------------------------------------------ CHANGE STATUS

    @Override
    @Transactional
    public UniversityTraineeResponseDto changeStatus(String universityTraineeId, Boolean isActive) {

        UniversityTrainee entity = findByUniversityTraineeId(universityTraineeId);
        entity.setIsActive(isActive);

        log.info("Changing trainee status: {} -> isActive={}", universityTraineeId, isActive);

        return universityTraineeMapper.toResponseDto(universityTraineerepository.save(entity));
    }

    // ------------------------------------------------------------------ PRIVATE HELPERS

    /**
     * Shared FK resolution used by both {@code createTrainee} and {@code persistSingleRow}.
     * Throws {@link ResourceNotFoundException} on any missing FK — the caller decides
     * whether to propagate (single create) or catch and log (bulk create).
     */
     void resolveAndSetForeignKeys(
            UniversityTrainee entity,
            UniversityTraineeRequestDto dto) {

        entity.setGender(
                genderRepository.findById(dto.getGenderId())
                        .orElseThrow(() -> new ResourceNotFoundException("Gender not found with id: " + dto.getGenderId())));

        entity.setPaymentMethod(
                paymentMethodRepository.findById(dto.getPaymentMethodId())
                        .orElseThrow(() -> new ResourceNotFoundException("Payment method not found with id: " + dto.getPaymentMethodId())));

        entity.setUniversity(
                universityRepository.findById(dto.getUniversityId())
                        .orElseThrow(() -> new ResourceNotFoundException("University not found with id: " + dto.getUniversityId())));

        entity.setRegion(
                regionRepository.findById(dto.getRegionId())
                        .orElseThrow(() -> new ResourceNotFoundException("Region not found with id: " + dto.getRegionId())));

        entity.setDistrict(
                districtRepository.findById(dto.getDistrictId())
                        .orElseThrow(() -> new ResourceNotFoundException("District not found with id: " + dto.getDistrictId())));

        entity.setCity(
                cityRepository.findById(dto.getCityId())
                        .orElseThrow(() -> new ResourceNotFoundException("City not found with id: " + dto.getCityId())));
    }


    // ------------------------------------------------------------------ ID GENERATION

     String generateUniversityTraineeNumber() {
        String prefix = UniversityTraineeConstants.CODE_PREFIX + "-" + Year.now().getValue() + "-";
        return uniqueIdGeneratorService.generateId(ModuleCode.UNIVERSITY_TRAINEE, prefix);
    }

    // ------------------------------------------------------------------ VALIDATION

    void validateTrainee(UniversityTraineeRequestDto dto) {
        if (universityTraineerepository.existsByEmail(dto.getEmail())
                || universityTraineerepository.existsByPhone(dto.getPhone())) {
            throw new DuplicateResourceException(UniversityTraineeConstants.TRAINEE_ALREADY_EXISTS);
        }
    }

    private void validateTraineeForUpdate(UniversityTraineeRequestDto dto, Long id) {
        if (universityTraineerepository.existsByEmailAndIsActiveTrueAndIdNot(dto.getEmail(), id))
            throw new ValidationException(UniversityTraineeConstants.TRAINEE_ALREADY_EXISTS);

        if (universityTraineerepository.existsByPhoneAndIsActiveTrueAndIdNot(dto.getPhone(), id))
            throw new ValidationException(UniversityTraineeConstants.TRAINEE_ALREADY_EXISTS);
    }

    private UniversityTrainee findByUniversityTraineeId(String universityTraineeId) {
        return universityTraineerepository.findByUniversityTraineeId(universityTraineeId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        UniversityTraineeConstants.TRAINEE_NOT_FOUND + universityTraineeId));
    }
}