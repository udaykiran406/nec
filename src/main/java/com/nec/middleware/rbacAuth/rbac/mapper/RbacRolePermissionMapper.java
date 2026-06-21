package com.nec.middleware.rbacAuth.rbac.mapper;

import com.nec.middleware.rbacAuth.rbac.dto.response.nested.GroupResponse;
import com.nec.middleware.rbacAuth.rbac.dto.response.nested.ModuleResponse;
import com.nec.middleware.rbacAuth.rbac.dto.response.nested.PermissionResponse;
import com.nec.middleware.rbacAuth.rbac.dto.response.nested.RoleResponse;
import com.nec.middleware.rbacAuth.rbac.dto.response.RbacRolePermissionResponse;
import com.nec.middleware.rbacAuth.rbac.dto.response.RbacRolePermissionResponseDto;
import com.nec.middleware.rbacAuth.rbac.entity.RbacModule;
import com.nec.middleware.rbacAuth.rbac.entity.RbacPermission;
import com.nec.middleware.rbacAuth.rbac.entity.RbacPermissionGroup;
import com.nec.middleware.rbacAuth.rbac.entity.RbacRole;
import com.nec.middleware.rbacAuth.rbac.entity.RbacRolePermission;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class RbacRolePermissionMapper {

    private final AssociationMapper associationMapper;

    public RbacRolePermissionResponseDto toResponseDto(RbacRolePermission entity) {
        return toResponseDto(
                entity,
                entity.getRole(),
                entity.getModule(),
                entity.getGroup(),
                entity.getPermission());
    }

    public RbacRolePermissionResponseDto toResponseDto(
            RbacRolePermission entity,
            RbacRole role,
            RbacModule module,
            RbacPermissionGroup group,
            RbacPermission permission) {
        return RbacRolePermissionResponseDto.builder()
                .role(associationMapper.toRole(role))
                .module(associationMapper.toModule(module))
                .group(associationMapper.toGroup(group))
                .permission(associationMapper.toPermission(permission))
                .status(entity.getStatus())
                .build();
    }

    public RbacRolePermission toEntity(
            Long roleId,
            Long moduleId,
            Long groupId,
            Long permissionId,
            String createdByUserId) {
        return RbacRolePermission.builder()
                .roleId(roleId)
                .moduleId(moduleId)
                .groupId(groupId)
                .permissionId(permissionId)
                .status("ACTIVE")
                .createdByUserId(createdByUserId != null ? createdByUserId : "SYSTEM")
                .build();
    }

    public void updateStatus(RbacRolePermission entity, String status, String modifiedByUserId) {
        entity.setStatus(status);
        if (modifiedByUserId != null) {
            entity.setModifiedByUserId(modifiedByUserId);
        }
    }

    public List<RbacRolePermissionResponse> toHierarchicalList(
            List<RbacRolePermissionResponseDto> flatPermissions) {

        if (flatPermissions == null || flatPermissions.isEmpty()) {
            return Collections.emptyList();
        }

        Map<String, List<RbacRolePermissionResponseDto>> byRole = flatPermissions.stream()
                .filter(dto -> dto.getRole() != null && StringUtils.hasText(dto.getRole().getRoleCode()))
                .collect(Collectors.groupingBy(dto -> dto.getRole().getRoleCode()));

        return byRole.values().stream()
                .map(this::toHierarchicalResponse)
                .collect(Collectors.toList());
    }

    public RbacRolePermissionResponse toHierarchicalResponse(
            RoleResponse role,
            List<RbacRolePermissionResponseDto> flatPermissions) {

        if (flatPermissions == null || flatPermissions.isEmpty()) {
            return RbacRolePermissionResponse.builder()
                    .role(role)
                    .modules(Collections.emptyList())
                    .build();
        }

        return toHierarchicalResponse(flatPermissions);
    }

    public RbacRolePermissionResponse toHierarchicalResponse(
            List<RbacRolePermissionResponseDto> flatPermissions) {

        if (flatPermissions == null || flatPermissions.isEmpty()) {
            return RbacRolePermissionResponse.builder()
                    .modules(Collections.emptyList())
                    .build();
        }

        RoleResponse role = flatPermissions.get(0).getRole();

        Map<String, List<RbacRolePermissionResponseDto>> byModule = flatPermissions.stream()
                .filter(dto -> dto.getModule() != null && StringUtils.hasText(dto.getModule().getModuleCode()))
                .collect(Collectors.groupingBy(dto -> dto.getModule().getModuleCode()));

        List<RbacRolePermissionResponse.ModulePermissionResponse> modules =
                byModule.values().stream()
                        .map(this::buildModuleResponse)
                        .collect(Collectors.toList());

        return RbacRolePermissionResponse.builder()
                .role(role)
                .modules(modules)
                .build();
    }

    private RbacRolePermissionResponse.ModulePermissionResponse buildModuleResponse(
            List<RbacRolePermissionResponseDto> flatPermissions) {

        ModuleResponse module = flatPermissions.get(0).getModule();

        Map<String, List<RbacRolePermissionResponseDto>> byGroup = flatPermissions.stream()
                .filter(dto -> dto.getGroup() != null && StringUtils.hasText(dto.getGroup().getGroupCode()))
                .collect(Collectors.groupingBy(dto -> dto.getGroup().getGroupCode()));

        List<RbacRolePermissionResponse.GroupPermissionResponse> groups =
                byGroup.values().stream()
                        .map(this::buildGroupResponse)
                        .collect(Collectors.toList());

        return RbacRolePermissionResponse.ModulePermissionResponse.builder()
                .module(module)
                .groups(groups)
                .build();
    }

    private RbacRolePermissionResponse.GroupPermissionResponse buildGroupResponse(
            List<RbacRolePermissionResponseDto> flatPermissions) {

        GroupResponse group = flatPermissions.get(0).getGroup();

        List<RbacRolePermissionResponse.PermissionMappingResponse> permissions =
                flatPermissions.stream()
                        .map(this::buildPermissionMapping)
                        .filter(Objects::nonNull)
                        .collect(Collectors.toList());

        return RbacRolePermissionResponse.GroupPermissionResponse.builder()
                .group(group)
                .permissions(permissions)
                .build();
    }

    private RbacRolePermissionResponse.PermissionMappingResponse buildPermissionMapping(
            RbacRolePermissionResponseDto flatPermission) {

        PermissionResponse permission = flatPermission.getPermission();
        if (permission == null) {
            return null;
        }

        return RbacRolePermissionResponse.PermissionMappingResponse.builder()
                .permission(permission)
                .build();
    }
}
