package com.nec.middleware.hr.mapper;

import com.nec.middleware.dto.IdValueDto;
import com.nec.middleware.hr.dto.request.MinistryofInteriorRequestDto;
import com.nec.middleware.hr.dto.response.MinistryofInteriorResponseDto;
import com.nec.middleware.hr.entity.MinistryofInterior;
import org.springframework.stereotype.Component;

@Component
public class MinistryofInteriorMapper {

    /**
     * Map RequestDto → new Entity (CREATE).
     * Only scalar fields are set here.
     * FK associations (aaqilType, gender, status, region, district, city)
     * are resolved and set by the service layer immediately after this call.
     */
    public MinistryofInterior toEntity(MinistryofInteriorRequestDto dto) {
        MinistryofInterior entity = MinistryofInterior.builder()
                .Name(dto.getName())
                .age(dto.getAge())
                .phone(dto.getPhone())
                .email(dto.getEmail())
                .photoPath(dto.getPhotoPath())
                .build();

        entity.setIsActive(Boolean.TRUE);
        entity.setCreatedBy(dto.getCreatedBy());
        entity.setUpdatedBy(dto.getCreatedBy());

        return entity;
    }

    /**
     * Merge RequestDto → existing Entity (UPDATE).
     * Scalar fields only; FK associations handled by service.
     * {@code aaqilId} is immutable and deliberately excluded.
     */
    public void updateEntity(MinistryofInterior entity, MinistryofInteriorRequestDto dto) {
        if (dto.getName() != null) entity.setName(dto.getName());
        if (dto.getAge()      != null) entity.setAge(dto.getAge());
        if (dto.getPhone()    != null) entity.setPhone(dto.getPhone());
        if (dto.getEmail()    != null) entity.setEmail(dto.getEmail());
        if (dto.getPhotoPath()  != null) entity.setPhotoPath(dto.getPhotoPath());
        if (dto.getUpdatedBy()!= null) entity.setUpdatedBy(dto.getUpdatedBy());
    }

    /**
     * Map Entity → ResponseDto.
     * Reads lazy-loaded @ManyToOne associations and converts each to IdValueDto.
     */
    public MinistryofInteriorResponseDto toResponseDto(MinistryofInterior entity) {
        return MinistryofInteriorResponseDto.builder()
                .ministryofInteriorId(entity.getMinistryofInteriorId())
                .Name(entity.getName())
                .age(entity.getAge())
                .phone(entity.getPhone())
                .email(entity.getEmail())
                .photoPath(entity.getPhotoPath())

                // ---- Lookups → IdValueDto
                .moiTitle(entity.getMoiTitle() != null
                        ? IdValueDto.builder()
                          .id(entity.getMoiTitle().getId())
                          .value(entity.getMoiTitle().getValue())
                          .build()
                        : null)

                .gender(entity.getGender() != null
                        ? IdValueDto.builder()
                          .id(entity.getGender().getId())
                          .value(entity.getGender().getValue())
                          .build()
                        : null)

                .status(entity.getStatus() != null
                        ? IdValueDto.builder()
                          .id(entity.getStatus().getId())
                          .value(entity.getStatus().getValue())
                          .build()
                        : null)

                // ---- Master Data → IdValueDto
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
                .vrc(entity.getVrc() != null
                        ? IdValueDto.builder()
                          .id(entity.getVrc().getId())
                          .value(entity.getVrc().getVrcName())
                          .build()
                        : null)

                // ---- Audit
                .isActive(entity.getIsActive())
                .createdBy(entity.getCreatedBy())
                .createdAt(entity.getCreatedAt())
                .updatedBy(entity.getUpdatedBy())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }
}