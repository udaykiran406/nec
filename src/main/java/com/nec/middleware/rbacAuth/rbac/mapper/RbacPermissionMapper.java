package com.nec.middleware.rbacAuth.rbac.mapper;

import com.nec.middleware.rbacAuth.rbac.dto.response.RbacPermissionResponse;
import com.nec.middleware.rbacAuth.rbac.entity.RbacPermission;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RbacPermissionMapper {

    private final AssociationMapper associationMapper;

    public RbacPermissionResponse toResponseDto(RbacPermission entity) {
        return RbacPermissionResponse.builder()
                .module(associationMapper.toModule(entity.getModule()))
                .group(associationMapper.toGroup(entity.getGroup()))
                .permissionCode(entity.getPermissionCode())
                .permissionName(entity.getPermissionName())
                .description(entity.getDescription())
                .displayOrder(entity.getDisplayOrder())
                .status(entity.getStatus())
                .isSideMenu(entity.getIsSideMenu())
                .build();
    }
}
