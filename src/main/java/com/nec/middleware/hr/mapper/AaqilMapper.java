package com.nec.middleware.hr.mapper;

import com.nec.middleware.hr.dto.request.AaqilRequestDto;
import com.nec.middleware.hr.dto.response.AaqilResponseDto;
import com.nec.middleware.hr.entity.Aaqil;
import org.springframework.stereotype.Component;

@Component
public class AaqilMapper {

    /**
     * Map RequestDto → new Entity (for CREATE).
     * Note: {@code code} is NOT set here — the service generates and assigns it
     * immediately after calling this method.
     */
    public Aaqil toEntity(AaqilRequestDto dto) {
        Aaqil entity = Aaqil.builder()
                .aaqilTypeId(dto.getAaqilTypeId())
                .fullName(dto.getFullName())
                .genderId(dto.getGenderId())
                .age(dto.getAge())
                .phone(dto.getPhone())
                .email(dto.getEmail())
                .regionId(dto.getRegionId())
                .districtId(dto.getDistrictId())
                .cityId(dto.getCityId())
                .statusId(dto.getStatusId())
                .build();

        entity.setIsActive(Boolean.TRUE);
        entity.setCreatedBy(dto.getCreatedBy());
        entity.setUpdatedBy(dto.getCreatedBy());

        return entity;
    }

    /**
     * Merge RequestDto → existing Entity (for UPDATE).
     * {@code code} is deliberately excluded — it is immutable after creation.
     */
    public void updateEntity(Aaqil entity, AaqilRequestDto dto) {
        entity.setAaqilTypeId(dto.getAaqilTypeId());
        entity.setFullName(dto.getFullName());
        entity.setGenderId(dto.getGenderId());
        entity.setAge(dto.getAge());
        entity.setPhone(dto.getPhone());
        entity.setEmail(dto.getEmail());
        entity.setRegionId(dto.getRegionId());
        entity.setDistrictId(dto.getDistrictId());
        entity.setCityId(dto.getCityId());
        entity.setStatusId(dto.getStatusId());
        entity.setUpdatedBy(dto.getUpdatedBy());
    }

    /**
     * Map Entity → ResponseDto
     */
    public AaqilResponseDto toResponseDto(Aaqil entity) {
        return AaqilResponseDto.builder()
                .id(entity.getId())
                .code(entity.getCode())
                .aaqilTypeId(entity.getAaqilTypeId())
                .fullName(entity.getFullName())
                .genderId(entity.getGenderId())
                .age(entity.getAge())
                .phone(entity.getPhone())
                .email(entity.getEmail())
                .regionId(entity.getRegionId())
                .districtId(entity.getDistrictId())
                .cityId(entity.getCityId())
                .statusId(entity.getStatusId())
                .isActive(entity.getIsActive())
                .createdBy(entity.getCreatedBy())
                .createdAt(entity.getCreatedAt())
                .updatedBy(entity.getUpdatedBy())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }
}
