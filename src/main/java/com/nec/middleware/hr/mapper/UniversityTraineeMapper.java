package com.nec.middleware.hr.mapper;

import com.nec.middleware.hr.dto.request.UniversityTraineeRequestDto;
import com.nec.middleware.hr.dto.response.UniversityTraineeResponseDto;
import com.nec.middleware.hr.entity.UniversityTrainee;
import org.springframework.stereotype.Component;

@Component
public class UniversityTraineeMapper {

    /**
     * Map RequestDto → new Entity (for CREATE).
     * Note: {@code code} is NOT set here — the service generates and assigns it
     * immediately after calling this method.
     */
    public UniversityTrainee toEntity(UniversityTraineeRequestDto dto) {
        UniversityTrainee entity = UniversityTrainee.builder()
                .fullName(dto.getFullName())
                .genderId(dto.getGenderId())
                .age(dto.getAge())
                .phone(dto.getPhone())
                .email(dto.getEmail())
                .paymentMethodId(dto.getPaymentMethodId())
                .universityId(dto.getUniversityId())
                .semester(dto.getSemester())
                .faculty(dto.getFaculty())
                .regionId(dto.getRegionId())
                .districtId(dto.getDistrictId())
                .cityId(dto.getCityId())
                .photoUrl(dto.getPhotoUrl())
                .statusId(dto.getStatusId())
                .build();

        entity.setIsActive(Boolean.TRUE);
        entity.setIsDeleted(Boolean.FALSE);
        entity.setCreatedBy(dto.getCreatedBy());
        entity.setUpdatedBy(dto.getCreatedBy());

        return entity;
    }

    /**
     * Merge RequestDto → existing Entity (for UPDATE).
     * {@code code} is deliberately excluded — it is immutable after creation.
     */
    public void updateEntity(UniversityTrainee entity, UniversityTraineeRequestDto dto) {
        entity.setFullName(dto.getFullName());
        entity.setGenderId(dto.getGenderId());
        entity.setAge(dto.getAge());
        entity.setPhone(dto.getPhone());
        entity.setEmail(dto.getEmail());
        entity.setPaymentMethodId(dto.getPaymentMethodId());
        entity.setUniversityId(dto.getUniversityId());
        entity.setSemester(dto.getSemester());
        entity.setFaculty(dto.getFaculty());
        entity.setRegionId(dto.getRegionId());
        entity.setDistrictId(dto.getDistrictId());
        entity.setCityId(dto.getCityId());
        entity.setPhotoUrl(dto.getPhotoUrl());
        entity.setStatusId(dto.getStatusId());
        entity.setUpdatedBy(dto.getUpdatedBy());
    }

    /**
     * Map Entity → ResponseDto
     */
    public UniversityTraineeResponseDto toResponseDto(UniversityTrainee entity) {
        return UniversityTraineeResponseDto.builder()
                .id(entity.getId())
                .code(entity.getCode())
                .fullName(entity.getFullName())
                .genderId(entity.getGenderId())
                .age(entity.getAge())
                .phone(entity.getPhone())
                .email(entity.getEmail())
                .paymentMethodId(entity.getPaymentMethodId())
                .universityId(entity.getUniversityId())
                .semester(entity.getSemester())
                .faculty(entity.getFaculty())
                .regionId(entity.getRegionId())
                .districtId(entity.getDistrictId())
                .cityId(entity.getCityId())
                .photoUrl(entity.getPhotoUrl())
                .statusId(entity.getStatusId())
                .isActive(entity.getIsActive())
                .isDeleted(entity.getIsDeleted())
                .createdBy(entity.getCreatedBy())
                .createdAt(entity.getCreatedAt())
                .updatedBy(entity.getUpdatedBy())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }
}
