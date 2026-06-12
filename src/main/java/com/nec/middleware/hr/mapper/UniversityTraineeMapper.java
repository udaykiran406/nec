package com.nec.middleware.hr.mapper;

import com.nec.middleware.hr.dto.request.UniversityTraineeRequestDto;
import com.nec.middleware.hr.dto.response.IdValueDto;
import com.nec.middleware.hr.dto.response.UniversityTraineeResponseDto;
import com.nec.middleware.hr.entity.UniversityTrainee;
import org.springframework.stereotype.Component;

/**
 * Maps between {@link UniversityTrainee} entity and its DTOs.
 * Mirrors {@code PortalUserMapper} exactly:
 *  - toEntity()       — scalar fields only; FK associations set by service
 *  - updateEntity()   — merges non-null scalar fields
 *  - toResponseDto()  — reads lazy-loaded associations directly from the entity
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
    public UniversityTrainee toEntity(UniversityTraineeRequestDto dto) {
        UniversityTrainee entity = UniversityTrainee.builder()
                .fullName(dto.getFullName())
                .age(dto.getAge())
                .phone(dto.getPhone())
                .email(dto.getEmail())
                .semester(dto.getSemester())
                .faculty(dto.getFaculty())
                .photoUrl(dto.getPhotoUrl())
                .statusId(dto.getStatusId())
                .build();

        entity.setIsActive(Boolean.TRUE);
        entity.setIsDeleted(Boolean.FALSE);
        entity.setCreatedBy(dto.getCreatedBy());
        entity.setUpdatedBy(dto.getCreatedBy());

        return entity;
    }

    // ------------------------------------------------------------------
    // UPDATE
    // ------------------------------------------------------------------

    /**
     * Merge non-null scalar fields from RequestDto into an existing Entity.
     * FK associations are handled separately in the service layer.
     * {@code universityTraineeId} is immutable and deliberately excluded.
     */
    public void updateEntity(UniversityTrainee entity, UniversityTraineeRequestDto dto) {

        if (dto.getFullName()  != null) entity.setFullName(dto.getFullName());
        if (dto.getAge()       != null) entity.setAge(dto.getAge());
        if (dto.getPhone()     != null) entity.setPhone(dto.getPhone());
        if (dto.getEmail()     != null) entity.setEmail(dto.getEmail());
        if (dto.getSemester()  != null) entity.setSemester(dto.getSemester());
        if (dto.getFaculty()   != null) entity.setFaculty(dto.getFaculty());
        if (dto.getPhotoUrl()  != null) entity.setPhotoUrl(dto.getPhotoUrl());
        if (dto.getStatusId()  != null) entity.setStatusId(dto.getStatusId());
        if (dto.getUpdatedBy() != null) entity.setUpdatedBy(dto.getUpdatedBy());
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
    public UniversityTraineeResponseDto toResponseDto(UniversityTrainee entity) {
        return UniversityTraineeResponseDto.builder()
                // Identity
                .id(entity.getId())
                .universityTraineeId(entity.getUniversityTraineeId())
                .fullName(entity.getFullName())
                .age(entity.getAge())
                .phone(entity.getPhone())
                .email(entity.getEmail())
                .semester(entity.getSemester())
                .faculty(entity.getFaculty())
                .photoUrl(entity.getPhotoUrl())
                .statusId(entity.getStatusId())

                // Lookup FKs → IdValueDto (lazy-loaded via @ManyToOne)
                .gender(entity.getGender() != null
                        ? IdValueDto.builder()
                          .id(entity.getGender().getId())
                          .value(entity.getGender().getValue())
                          .build()
                        : null)

                .paymentMethod(entity.getPaymentMethod() != null
                        ? IdValueDto.builder()
                          .id(entity.getPaymentMethod().getId())
                          .value(entity.getPaymentMethod().getValue())
                          .build()
                        : null)

                // Master Data FKs → IdValueDto (lazy-loaded via @ManyToOne)
                .university(entity.getUniversity() != null
                        ? IdValueDto.builder()
                          .id(entity.getUniversity().getId())
                          .value(entity.getUniversity().getUniversityName())
                          .build()
                        : null)

                .region(entity.getRegion() != null
                        ? IdValueDto.builder()
                          .id(entity.getRegion().getId())
                          .value(entity.getRegion().getRegionName())
                          .build()
                        : null)

                .district(entity.getDistrict() != null
                        ? IdValueDto.builder()
                          .id(entity.getDistrict().getId())
                          .value(entity.getDistrict().getDistrictName())
                          .build()
                        : null)

                .city(entity.getCity() != null
                        ? IdValueDto.builder()
                          .id(entity.getCity().getId())
                          .value(entity.getCity().getCityName())
                          .build()
                        : null)

                // Audit
                .isActive(entity.getIsActive())
                .isDeleted(entity.getIsDeleted())
                .createdBy(entity.getCreatedBy())
                .createdAt(entity.getCreatedAt())
                .updatedBy(entity.getUpdatedBy())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }
}