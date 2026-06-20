package com.nec.middleware.hr.mapper;

import com.nec.middleware.hr.dto.request.UniversityTraineeRequestDto;
import com.nec.middleware.dto.IdValueDto;
import com.nec.middleware.hr.dto.response.UniversityTraineeResponseDto;
import com.nec.middleware.hr.entity.UniversityTrainee;
import org.springframework.stereotype.Component;

/**
 * Maps between {@link UniversityTrainee} entity and its DTOs.
 * Mirrors {@code PortalUserMapper} exactly:
 *  - toEntity()       — scalar fields only; FK associations set by service
 *  - updateEntity()   — merges non-null scalar fields
 *  - politicalPartyResponseDto()  — reads lazy-loaded associations directly from the entity
 *                       and converts each to an {@link IdValueDto}
 */
@Component
public class UniversityTraineeMapper {

    // ------------------------------------------------------------------
    // CREATE
    // ------------------------------------------------------------------

    /**
     * Map RequestDto → new Entity.
     * FK associations (gender, paymentMethod, etc.) are set by the service
     * layer immediately after this call — mirrors portalUserEntity() in PortalUserMapper.
     */
    public UniversityTrainee toEntity(UniversityTraineeRequestDto universityTraineeRequestDto) {
        UniversityTrainee universityTraineeEntity = UniversityTrainee.builder()
                .fullName(universityTraineeRequestDto.getFullName())
                .age(universityTraineeRequestDto.getAge())
                .phone(universityTraineeRequestDto.getPhone())
                .email(universityTraineeRequestDto.getEmail())
                .semester(universityTraineeRequestDto.getSemester())
                .faculty(universityTraineeRequestDto.getFaculty())
                .photoPath(universityTraineeRequestDto.getPhotoPath())
                .build();

        universityTraineeEntity.setIsActive(Boolean.TRUE);
        universityTraineeEntity.setCreatedBy(universityTraineeRequestDto.getCreatedBy());
        universityTraineeEntity.setUpdatedBy(universityTraineeRequestDto.getCreatedBy());
        return universityTraineeEntity;
    }

    // ------------------------------------------------------------------
    // UPDATE
    // ------------------------------------------------------------------

    /**
     * Merge non-null scalar fields from RequestDto into an existing Entity.
     * FK associations are handled separately in the service layer.
     * {@code universityTraineeId} is immutable and deliberately excluded.
     */
    public void updateUniversityTraineeEntity(UniversityTrainee UniversityTraineEntity, UniversityTraineeRequestDto universityTraineeRequesDto) {

        if (universityTraineeRequesDto.getFullName()  != null) UniversityTraineEntity.setFullName(universityTraineeRequesDto.getFullName());
        if (universityTraineeRequesDto.getAge()       != null) UniversityTraineEntity.setAge(universityTraineeRequesDto.getAge());
        if (universityTraineeRequesDto.getPhone()     != null) UniversityTraineEntity.setPhone(universityTraineeRequesDto.getPhone());
        if (universityTraineeRequesDto.getEmail()     != null) UniversityTraineEntity.setEmail(universityTraineeRequesDto.getEmail());
        if (universityTraineeRequesDto.getSemester()  != null) UniversityTraineEntity.setSemester(universityTraineeRequesDto.getSemester());
        if (universityTraineeRequesDto.getFaculty()   != null) UniversityTraineEntity.setFaculty(universityTraineeRequesDto.getFaculty());
        if (universityTraineeRequesDto.getPhotoPath()  != null) UniversityTraineEntity.setPhotoPath(universityTraineeRequesDto.getPhotoPath());
        if (universityTraineeRequesDto.getUpdatedBy() != null) UniversityTraineEntity.setUpdatedBy(universityTraineeRequesDto.getUpdatedBy());
    }

    // ------------------------------------------------------------------
    // READ  — Entity → ResponseDto
    // ------------------------------------------------------------------

    /**
     * Reads lazy-loaded associations directly from the entity and converts
     * each FK to an {@link IdValueDto} { id, value }.
     * Mirrors portalUserResponseDto() in PortalUserMapper exactly.
     *
     * <pre>
     *   "city":          { "id": 1, "value": "Hyderabad" },
     *   "paymentMethod": { "id": 2, "value": "Cash"      }
     * </pre>
     */
    public UniversityTraineeResponseDto toResponseDto(UniversityTrainee universityTraineeEntity) {
        return UniversityTraineeResponseDto.builder()
                // Identity
                .universityTraineeId(universityTraineeEntity.getUniversityTraineeId())
                .fullName(universityTraineeEntity.getFullName())
                .age(universityTraineeEntity.getAge())
                .phone(universityTraineeEntity.getPhone())
                .email(universityTraineeEntity.getEmail())
                .semester(universityTraineeEntity.getSemester())
                .faculty(universityTraineeEntity.getFaculty())
                .photoPath(universityTraineeEntity.getPhotoPath())

                // Lookup FKs → IdValueDto (lazy-loaded via @ManyToOne)
                .gender(universityTraineeEntity.getGender() != null
                        ? IdValueDto.builder()
                          .id(universityTraineeEntity.getGender().getId())
                          .value(universityTraineeEntity.getGender().getValue())
                          .build()
                        : null)

                .paymentMethod(universityTraineeEntity.getPaymentMethod() != null
                        ? IdValueDto.builder()
                          .id(universityTraineeEntity.getPaymentMethod().getId())
                          .value(universityTraineeEntity.getPaymentMethod().getValue())
                          .build()
                        : null)

                // Master Data FKs → IdValueDto (lazy-loaded via @ManyToOne)
                .university(universityTraineeEntity.getUniversity() != null
                        ? IdValueDto.builder()
                          .id(universityTraineeEntity.getUniversity().getId())
                          .value(universityTraineeEntity.getUniversity().getUniversityName())
                          .build()
                        : null)

                .region(universityTraineeEntity.getRegion() != null
                        ? IdValueDto.builder()
                          .id(universityTraineeEntity.getRegion().getId())
                          .value(universityTraineeEntity.getRegion().getRegionName())
                          .build()
                        : null)

                .district(universityTraineeEntity.getDistrict() != null
                        ? IdValueDto.builder()
                          .id(universityTraineeEntity.getDistrict().getId())
                          .value(universityTraineeEntity.getDistrict().getDistrictName())
                          .build()
                        : null)

                .city(universityTraineeEntity.getCity() != null
                        ? IdValueDto.builder()
                          .id(universityTraineeEntity.getCity().getId())
                          .value(universityTraineeEntity.getCity().getCityName())
                          .build()
                        : null)

                .status(
                        universityTraineeEntity.getStatus() != null
                                ? IdValueDto.builder()
                                  .id(universityTraineeEntity.getStatus().getId())
                                  .value(universityTraineeEntity.getStatus().getValue())
                                  .build()
                                : null
                )

                // Audit
                .isActive(universityTraineeEntity.getIsActive())
                .createdBy(universityTraineeEntity.getCreatedBy())
                .createdAt(universityTraineeEntity.getCreatedAt())
                .updatedBy(universityTraineeEntity.getUpdatedBy())
                .updatedAt(universityTraineeEntity.getUpdatedAt())
                .build();
    }
}