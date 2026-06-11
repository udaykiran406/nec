package com.nec.middleware.hr.mapper;

import com.nec.middleware.hr.dto.request.PortalUserRequestDto;
import com.nec.middleware.hr.dto.response.PortalUserResponseDto;
import com.nec.middleware.hr.entity.PortalUser;

import org.springframework.stereotype.Component;

@Component
public class PortalUserMapper {

    /**
     * Map RequestDto → new Entity (for CREATE)
     */
    public PortalUser toEntity(PortalUserRequestDto dto) {
        PortalUser entity = PortalUser.builder()
                .userName(dto.getUserName())
                .genderId(dto.getGenderId())
                .roleId(dto.getRoleId())
                .phone(dto.getPhone())
                .email(dto.getEmail())
                .photoPath(dto.getPhotoPath())
                .faculty(dto.getFaculty())
                .universityId(dto.getUniversityId())
                .regionId(dto.getRegionId())
                .districtId(dto.getDistrictId())
                .cityId(dto.getCityId())
                .portalUserTypeId(dto.getPortalUserTypeId())
//                .referenceId(dto.getReferenceId())
                .build();

        entity.setIsActive(Boolean.TRUE);
        entity.setIsDeleted(Boolean.FALSE);
        entity.setCreatedBy(dto.getCreatedBy());
        entity.setUpdatedBy(dto.getCreatedBy());

        return entity;
    }

    /**
     * Merge RequestDto → existing Entity (for UPDATE)
     */
    public void updateEntity(PortalUser entity, PortalUserRequestDto dto) {
        entity.setUserName(dto.getUserName());
        entity.setGenderId(dto.getGenderId());
        entity.setRoleId(dto.getRoleId());
        entity.setPhone(dto.getPhone());
        entity.setEmail(dto.getEmail());
        entity.setPhotoPath(dto.getPhotoPath());
        entity.setFaculty(dto.getFaculty());
        entity.setUniversityId(dto.getUniversityId());
        entity.setRegionId(dto.getRegionId());
        entity.setDistrictId(dto.getDistrictId());
        entity.setCityId(dto.getCityId());
        entity.setPortalUserTypeId(dto.getPortalUserTypeId());
//        entity.setReferenceId(dto.getReferenceId());
        entity.setUpdatedBy(dto.getUpdatedBy());
    }

    /**
     * Map Entity → ResponseDto
     */
    public PortalUserResponseDto toResponseDto(PortalUser entity) {
        return PortalUserResponseDto.builder()
                // Identity
                .id(entity.getId())
                .portalUserId(entity.getPortalUserId())
                .userName(entity.getUserName())
                .phone(entity.getPhone())
                .email(entity.getEmail())
                .photoPath(entity.getPhotoPath())
                .faculty(entity.getFaculty())

                // Lookup FKs — name resolved via lazy-loaded association
                .genderId(entity.getGenderId())
                .genderName(entity.getGender() != null
                        ? entity.getGender().getValue() : null)

                .roleId(entity.getRoleId())
                .roleName(entity.getRole() != null
                        ? entity.getRole().getValue() : null)

                .portalUserTypeId(entity.getPortalUserTypeId())
                .portalUserTypeName(entity.getPortalUserType() != null
                        ? entity.getPortalUserType().getValue() : null)

                // Master data FKs — name resolved via lazy-loaded association
                .universityId(entity.getUniversityId())
                .universityName(entity.getUniversity() != null
                        ? entity.getUniversity().getUniversityName() : null)

                .regionId(entity.getRegionId())
                .regionName(entity.getRegion() != null
                        ? entity.getRegion().getRegionName() : null)

                .districtId(entity.getDistrictId())
                .districtName(entity.getDistrict() != null
                        ? entity.getDistrict().getDistrictName() : null)

                .cityId(entity.getCityId())
                .cityName(entity.getCity() != null
                        ? entity.getCity().getCityName() : null)

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
