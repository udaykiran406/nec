package com.nec.middleware.rbacAuth.rbac.mapper;

import com.nec.middleware.rbacAuth.rbac.constant.RbacConstants;
import com.nec.middleware.rbacAuth.rbac.dto.request.RbacUserRequest;
import com.nec.middleware.rbacAuth.rbac.dto.response.RbacUserResponse;
import com.nec.middleware.rbacAuth.rbac.entity.RbacUser;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RbacUserMapper {

    private final AssociationMapper associationMapper;

    public RbacUser toEntity(RbacUserRequest request) {
        return RbacUser.builder()
                .userName(trimSafe(request.getUserName()))
                .genderId(request.getGenderId())
                .roleId(request.getRoleId())
                .phone(trimSafe(request.getPhone()))
                .email(trimSafe(request.getEmail()))
                .photoPath(trimSafe(request.getPhotoPath()))
                .departmentId(request.getDepartmentId())
                .regionId(request.getRegionId())
                .districtId(request.getDistrictId())
                .cityId(request.getCityId())
                .isActive(request.getIsActive() != null ? request.getIsActive() : RbacConstants.IS_ACTIVE_TRUE)
                .createdBy(request.getCreatedBy())
                .passwordToBeChanged(request.getPasswordToBeChanged() != null
                    ? request.getPasswordToBeChanged() : false)
                .emailVerified(request.getEmailVerified() != null
                    ? request.getEmailVerified() : false)
                .mobileVerified(request.getMobileVerified() != null
                    ? request.getMobileVerified() : false)
                .failedLoginAttempts(0)
                .updatedBy(toUserIdString(request.getUpdatedBy()))
                .build();
    }

    public RbacUserResponse toResponseDto(RbacUser entity) {
        return RbacUserResponse.builder()
                .id(entity.getUserId())
                .keycloakUserId(entity.getKeycloakUserId())
                .userName(entity.getUserName())
                .gender(associationMapper.toGender(entity.getGender()))
                .role(associationMapper.toRole(entity.getRole()))
                .phone(entity.getPhone())
                .email(entity.getEmail())
                .photoPath(entity.getPhotoPath())
                .department(associationMapper.toDepartment(entity.getDepartment()))
                .region(associationMapper.toRegion(entity.getRegion()))
                .district(associationMapper.toDistrict(entity.getDistrict()))
                .city(associationMapper.toCity(entity.getCity()))
                .isActive(entity.getIsActive())
                .passwordToBeChanged(entity.getPasswordToBeChanged())
                .emailVerified(entity.getEmailVerified())
                .mobileVerified(entity.getMobileVerified())
                .lastLoginAt(entity.getLastLoginAt())
                .failedLoginAttempts(entity.getFailedLoginAttempts())
                .build();
    }

    public void updateEntity(RbacUser entity, RbacUserRequest request) {
        if (request.getUserName() != null && !request.getUserName().isBlank()) {
            entity.setUserName(trimSafe(request.getUserName()));
        }
        if (request.getGenderId() != null) {
            entity.setGenderId(request.getGenderId());
        }
        if (request.getRoleId() != null) {
            entity.setRoleId(request.getRoleId());
        }
        if (request.getPhone() != null && !request.getPhone().isBlank()) {
            entity.setPhone(trimSafe(request.getPhone()));
        }
        if (request.getEmail() != null && !request.getEmail().isBlank()) {
            entity.setEmail(trimSafe(request.getEmail()));
        }
        if (request.getPhotoPath() != null) {
            entity.setPhotoPath(trimSafe(request.getPhotoPath()));
        }
        if (request.getDepartmentId() != null) {
            entity.setDepartmentId(request.getDepartmentId());
        }
        if (request.getRegionId() != null) {
            entity.setRegionId(request.getRegionId());
        }
        if (request.getDistrictId() != null) {
            entity.setDistrictId(request.getDistrictId());
        }
        if (request.getCityId() != null) {
            entity.setCityId(request.getCityId());
        }
        if (request.getIsActive() != null) {
            entity.setIsActive(request.getIsActive());
            entity.setStatus(request.getIsActive().equals(1) ? "ACTIVE" : "INACTIVE");
        }
        if (request.getUpdatedBy() != null) {
            entity.setUpdatedBy(toUserIdString(request.getUpdatedBy()));
        }
        if (request.getEmailVerified() != null) {
            entity.setEmailVerified(request.getEmailVerified());
        }
        if (request.getMobileVerified() != null) {
            entity.setMobileVerified(request.getMobileVerified());
        }
        if (request.getPasswordToBeChanged() != null) {
            entity.setPasswordToBeChanged(request.getPasswordToBeChanged());
        }
    }

    private String trimSafe(String value) {
        return value != null ? value.trim() : null;
    }

    private String toUserIdString(Long userId) {
        return userId != null ? String.valueOf(userId) : null;
    }

}
