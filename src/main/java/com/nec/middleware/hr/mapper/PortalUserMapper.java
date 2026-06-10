package com.nec.middleware.hr.mapper;

import com.nec.middleware.portal.dto.request.PortalUserRequestDto;
import com.nec.middleware.portal.dto.response.PortalUserResponseDto;
import org.springframework.stereotype.Component;

@Component
public class PortalUserMapper {

    /**
     * Map RequestDto → new Entity (for CREATE)
     */
    public com.nec.middleware.portal.entity.PortalUser toEntity(PortalUserRequestDto dto) {
        com.nec.middleware.portal.entity.PortalUser entity = com.nec.middleware.portal.entity.PortalUser.builder()
                .userName(dto.getUserName())
                .genderId(dto.getGenderId())
                .roleId(dto.getRoleId())
                .phone(dto.getPhone())
                .email(dto.getEmail())
                .photoPath(dto.getPhotoPath())
                .faculty(dto.getFaculty())
                .departmentId(dto.getDepartmentId())
                .regionId(dto.getRegionId())
                .districtId(dto.getDistrictId())
                .cityId(dto.getCityId())
                .portalUserTypeId(dto.getPortalUserTypeId())
                .referenceId(dto.getReferenceId())
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
    public void updateEntity(com.nec.middleware.portal.entity.PortalUser entity, PortalUserRequestDto dto) {
        entity.setUserName(dto.getUserName());
        entity.setGenderId(dto.getGenderId());
        entity.setRoleId(dto.getRoleId());
        entity.setPhone(dto.getPhone());
        entity.setEmail(dto.getEmail());
        entity.setPhotoPath(dto.getPhotoPath());
        entity.setFaculty(dto.getFaculty());
        entity.setDepartmentId(dto.getDepartmentId());
        entity.setRegionId(dto.getRegionId());
        entity.setDistrictId(dto.getDistrictId());
        entity.setCityId(dto.getCityId());
        entity.setPortalUserTypeId(dto.getPortalUserTypeId());
        entity.setReferenceId(dto.getReferenceId());
        entity.setUpdatedBy(dto.getUpdatedBy());
    }

    /**
     * Map Entity → ResponseDto
     */
    public PortalUserResponseDto toResponseDto(com.nec.middleware.portal.entity.PortalUser entity) {
        return PortalUserResponseDto.builder()
                .id(entity.getId())
                .userName(entity.getUserName())
                .genderId(entity.getGenderId())
                .roleId(entity.getRoleId())
                .phone(entity.getPhone())
                .email(entity.getEmail())
                .photoPath(entity.getPhotoPath())
                .faculty(entity.getFaculty())
                .departmentId(entity.getDepartmentId())
                .regionId(entity.getRegionId())
                .districtId(entity.getDistrictId())
                .cityId(entity.getCityId())
                .portalUserTypeId(entity.getPortalUserTypeId())
                .referenceId(entity.getReferenceId())
                .isActive(entity.getIsActive())
                .isDeleted(entity.getIsDeleted())
                .createdBy(entity.getCreatedBy())
                .createdAt(entity.getCreatedAt())
                .updatedBy(entity.getUpdatedBy())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }
}
