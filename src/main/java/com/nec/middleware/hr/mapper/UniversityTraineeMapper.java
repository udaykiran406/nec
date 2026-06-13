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
                .photoUrl(universityTraineeRequestDto.getPhotoUrl())
                .statusId(universityTraineeRequestDto.getStatusId())
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
        if (universityTraineeRequesDto.getPhotoUrl()  != null) UniversityTraineEntity.setPhotoUrl(universityTraineeRequesDto.getPhotoUrl());
        if (universityTraineeRequesDto.getStatusId()  != null) UniversityTraineEntity.setStatusId(universityTraineeRequesDto.getStatusId());
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
    public UniversityTraineeResponseDto toResponseDto(UniversityTrainee universityTraineEentity) {
        return UniversityTraineeResponseDto.builder()
                // Identity
                .id(universityTraineEentity.getId())
                .universityTraineeId(universityTraineEentity.getUniversityTraineeId())
                .fullName(universityTraineEentity.getFullName())
                .age(universityTraineEentity.getAge())
                .phone(universityTraineEentity.getPhone())
                .email(universityTraineEentity.getEmail())
                .semester(universityTraineEentity.getSemester())
                .faculty(universityTraineEentity.getFaculty())
                .photoUrl(universityTraineEentity.getPhotoUrl())
                .statusId(universityTraineEentity.getStatusId())

                // Lookup FKs → IdValueDto (lazy-loaded via @ManyToOne)
                .gender(universityTraineEentity.getGender() != null
                        ? IdValueDto.builder()
                          .id(universityTraineEentity.getGender().getId())
                          .value(universityTraineEentity.getGender().getValue())
                          .build()
                        : null)

                .paymentMethod(universityTraineEentity.getPaymentMethod() != null
                        ? IdValueDto.builder()
                          .id(universityTraineEentity.getPaymentMethod().getId())
                          .value(universityTraineEentity.getPaymentMethod().getValue())
                          .build()
                        : null)

                // Master Data FKs → IdValueDto (lazy-loaded via @ManyToOne)
                .university(universityTraineEentity.getUniversity() != null
                        ? IdValueDto.builder()
                          .id(universityTraineEentity.getUniversity().getId())
                          .value(universityTraineEentity.getUniversity().getUniversityName())
                          .build()
                        : null)

                .region(universityTraineEentity.getRegion() != null
                        ? IdValueDto.builder()
                          .id(universityTraineEentity.getRegion().getId())
                          .value(universityTraineEentity.getRegion().getRegionName())
                          .build()
                        : null)

                .district(universityTraineEentity.getDistrict() != null
                        ? IdValueDto.builder()
                          .id(universityTraineEentity.getDistrict().getId())
                          .value(universityTraineEentity.getDistrict().getDistrictName())
                          .build()
                        : null)

                .city(universityTraineEentity.getCity() != null
                        ? IdValueDto.builder()
                          .id(universityTraineEentity.getCity().getId())
                          .value(universityTraineEentity.getCity().getCityName())
                          .build()
                        : null)

                // Audit
                .isActive(universityTraineEentity.getIsActive())
                .createdBy(universityTraineEentity.getCreatedBy())
                .createdAt(universityTraineEentity.getCreatedAt())
                .updatedBy(universityTraineEentity.getUpdatedBy())
                .updatedAt(universityTraineEentity.getUpdatedAt())
                .build();
    }
}