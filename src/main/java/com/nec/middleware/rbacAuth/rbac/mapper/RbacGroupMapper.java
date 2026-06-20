package com.nec.middleware.rbacAuth.rbac.mapper;

import com.nec.middleware.rbacAuth.rbac.dto.response.RbacGroupResponse;
import com.nec.middleware.rbacAuth.rbac.entity.RbacPermissionGroup;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RbacGroupMapper {

    private final AssociationMapper associationMapper;

    public RbacGroupResponse toResponseDto(RbacPermissionGroup entity) {
        return RbacGroupResponse.builder()
                .module(associationMapper.toModule(entity.getModule()))
                .groupCode(entity.getGroupCode())
                .groupName(entity.getGroupName())
                .description(entity.getDescription())
                .displayOrder(entity.getDisplayOrder())
                .status(entity.getStatus())
                .build();
    }
}
