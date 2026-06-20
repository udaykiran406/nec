package com.nec.middleware.rbacAuth.rbac.service.impl;

import com.nec.middleware.exception.DuplicateException;
import com.nec.middleware.exception.ResourceNotFoundException;
import com.nec.middleware.exception.ValidationException;
import com.nec.middleware.rbacAuth.rbac.constant.RbacConstants;
import com.nec.middleware.rbacAuth.rbac.constant.RbacRolePermissionConstants;
import com.nec.middleware.rbacAuth.rbac.dto.request.RbacRolePermissionRequest;
import com.nec.middleware.rbacAuth.rbac.dto.request.RolePermissionListRequestDto;
import com.nec.middleware.rbacAuth.rbac.dto.response.ApiResponse;
import com.nec.middleware.rbacAuth.rbac.dto.response.RbacRolePermissionResponse;
import com.nec.middleware.rbacAuth.rbac.dto.response.RbacRolePermissionResponseDto;
import com.nec.middleware.rbacAuth.rbac.dto.response.PaginatedResponse;
import com.nec.middleware.rbacAuth.rbac.entity.RbacRole;
import com.nec.middleware.rbacAuth.rbac.entity.RbacModule;
import com.nec.middleware.rbacAuth.rbac.entity.RbacPermissionGroup;
import com.nec.middleware.rbacAuth.rbac.entity.RbacPermission;
import com.nec.middleware.rbacAuth.rbac.entity.RbacRolePermission;
import com.nec.middleware.rbacAuth.rbac.mapper.RbacRolePermissionMapper;
import com.nec.middleware.rbacAuth.rbac.mapper.AssociationMapper;
import com.nec.middleware.rbacAuth.rbac.repository.RbacRoleRepository;
import com.nec.middleware.rbacAuth.rbac.repository.RbacModuleRepository;
import com.nec.middleware.rbacAuth.rbac.repository.RbacPermissionGroupRepository;
import com.nec.middleware.rbacAuth.rbac.repository.RbacPermissionRepository;
import com.nec.middleware.rbacAuth.rbac.repository.RbacRolePermissionRepository;
import com.nec.middleware.rbacAuth.rbac.service.RolePermissionService;
import com.nec.middleware.rbacAuth.rbac.specification.RolePermissionSpecification;
import com.nec.middleware.rbacAuth.rbac.util.RbacPaginationUtil;
import com.nec.middleware.rbacAuth.rbac.util.RbacUtil;
import jakarta.persistence.EntityManager;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Service implementation for Role-Permission mapping operations.
 * Handles creation, updating, deletion, and retrieval of role-permission associations.
 */
@Service
@AllArgsConstructor
@Transactional
@Slf4j
public class RolePermissionServiceImpl implements RolePermissionService {

    private final RbacRolePermissionRepository rolePermissionRepository;
    private final RbacRoleRepository roleRepository;
    private final RbacModuleRepository moduleRepository;
    private final RbacPermissionGroupRepository groupRepository;
    private final RbacPermissionRepository permissionRepository;
    private final RbacRolePermissionMapper rolePermissionMapper;
    private final AssociationMapper associationMapper;
    private final EntityManager entityManager;

    @Override
    public ApiResponse<RbacRolePermissionResponse> createRolePermission(RbacRolePermissionRequest request) {
        return persistRolePermission(request, false);
    }

    @Override
    public ApiResponse<RbacRolePermissionResponse> updateRolePermission(RbacRolePermissionRequest request) {
        return persistRolePermission(request, true);
    }

    private ApiResponse<RbacRolePermissionResponse> persistRolePermission(
            RbacRolePermissionRequest request,
            boolean isUpdate) {

        // Validate request
        if (request == null) {
            throw new ValidationException(RbacRolePermissionConstants.MODULE_LIST_MISSING);
        }
        if (request.getRoleId() == null) {
            throw new ValidationException(RbacRolePermissionConstants.ROLE_ID_MISSING);
        }
        if (!RbacUtil.isNotEmpty(request.getModules())) {
            throw new ValidationException(RbacRolePermissionConstants.MODULE_LIST_MISSING);
        }

        log.info("Starting role-permission {}: roleId={}", isUpdate ? "update" : "create", request.getRoleId());

        // Find the role by ID
        RbacRole role = roleRepository.findByRoleIdAndIsDeleted(request.getRoleId(), RbacConstants.IS_DELETED_FALSE)
                .orElseThrow(() -> new ResourceNotFoundException(RbacRolePermissionConstants.INVALID_ROLE));

        // Iterate through modules to validate and save mappings
        for (RbacRolePermissionRequest.ModuleRequestDto moduleDto : request.getModules()) {
            if (moduleDto == null || moduleDto.getModuleId() == null) {
                throw new ValidationException(RbacRolePermissionConstants.MODULE_ID_MISSING);
            }

            // Validate module exists
            RbacModule module = moduleRepository.findByModuleIdAndIsDeleted(moduleDto.getModuleId(), RbacConstants.IS_DELETED_FALSE)
                    .orElseThrow(() -> new ResourceNotFoundException(RbacRolePermissionConstants.INVALID_MODULE));

            if (!RbacUtil.isNotEmpty(moduleDto.getGroups())) {
                throw new ValidationException(RbacRolePermissionConstants.GROUP_LIST_MISSING);
            }

            // Iterate through groups
            for (RbacRolePermissionRequest.GroupRequestDto groupDto : moduleDto.getGroups()) {
                if (groupDto == null || groupDto.getGroupId() == null) {
                    throw new ValidationException(RbacRolePermissionConstants.GROUP_ID_MISSING);
                }

                // Validate group exists and belongs to module
                RbacPermissionGroup group = groupRepository.findByGroupIdAndModuleIdAndStatusOrderByDisplayOrderAsc(
                        groupDto.getGroupId(), moduleDto.getModuleId(), RbacConstants.STATUS_ACTIVE)
                        .stream()
                        .findFirst()
                        .orElseThrow(() -> new ResourceNotFoundException(RbacRolePermissionConstants.INVALID_GROUP));

                if (!RbacUtil.isNotEmpty(groupDto.getPermissions())) {
                    throw new ValidationException(RbacRolePermissionConstants.PERMISSION_LIST_MISSING);
                }

                // Iterate through permissions
                for (RbacRolePermissionRequest.PermissionRequestDto permissionDto : groupDto.getPermissions()) {
                    if (permissionDto == null || permissionDto.getPermissionId() == null) {
                        throw new ValidationException(RbacRolePermissionConstants.PERMISSION_ID_MISSING);
                    }

                    // Validate permission exists and belongs to group and module
                    RbacPermission permission = permissionRepository.findByPermissionIdAndGroupIdAndModuleIdAndStatus(
                            permissionDto.getPermissionId(), groupDto.getGroupId(), moduleDto.getModuleId(),
                            RbacConstants.STATUS_ACTIVE)
                            .orElseThrow(() -> new ResourceNotFoundException(RbacRolePermissionConstants.INVALID_PERMISSION));

                    // Check if mapping exists
                    Optional<RbacRolePermission> existingMapping = rolePermissionRepository.findOptionalByRoleIdAndModuleIdAndGroupIdAndPermissionId(
                            role.getRoleId(),
                            moduleDto.getModuleId(),
                            groupDto.getGroupId(),
                            permissionDto.getPermissionId());

                    if (existingMapping.isPresent() && isUpdate) {
                        // UPDATE: Set to ACTIVE if it was INACTIVE
                        RbacRolePermission mapping = existingMapping.get();
                        if (RbacConstants.STATUS_INACTIVE.equals(mapping.getStatus())) {
                            mapping.setStatus(RbacConstants.STATUS_ACTIVE);
                            rolePermissionRepository.save(mapping);
                        }
                    } else if (!existingMapping.isPresent()) {
                        RbacRolePermission mapping = rolePermissionMapper.toEntity(
                                role.getRoleId(),
                                moduleDto.getModuleId(),
                                groupDto.getGroupId(),
                                permissionDto.getPermissionId());

                        rolePermissionRepository.save(mapping);
                    } else if (existingMapping.isPresent() && !isUpdate) {
                        // CREATE mode but mapping already exists - throw duplicate error
                        throw new DuplicateException(RbacRolePermissionConstants.PERMISSION_ALREADY_MAPPED);
                    }
                }
            }
        }

        // Retrieve all permissions for this role and build hierarchical response
        RbacRolePermissionResponse hierarchicalResponse = getRolePermissionHierarchy(role.getRoleId());

        log.info("Role-permission mappings persisted for roleId={}", role.getRoleId());

        String message = isUpdate ?
                RbacRolePermissionConstants.ROLE_PERMISSION_UPDATED :
                RbacRolePermissionConstants.ROLE_PERMISSION_SAVED;

        return isUpdate
                ? ApiResponse.ok(message, hierarchicalResponse)
                : ApiResponse.created(message, hierarchicalResponse);
    }

    @Override
    public RbacRolePermissionResponseDto updateRolePermissionStatus(
            Long rolePermissionId,
            String status) {

        if (!RbacUtil.isValidId(rolePermissionId)) {
            throw new ValidationException("Role Permission ID must be a positive number");
        }
        if (!RbacUtil.isValidStatus(status)) {
            throw new ValidationException(RbacConstants.INVALID_STATUS);
        }

        RbacRolePermission mapping = rolePermissionRepository.findByRolePermissionId(rolePermissionId)
                .orElseThrow(() -> new ResourceNotFoundException(RbacRolePermissionConstants.ROLE_PERMISSION_NOT_FOUND));

        rolePermissionMapper.updateStatus(mapping, status);
        rolePermissionRepository.save(mapping);

        RbacRolePermission updatedMapping = rolePermissionRepository
                .findByRolePermissionIdWithAssociations(rolePermissionId)
                .orElseThrow(() -> new ResourceNotFoundException(RbacRolePermissionConstants.ROLE_PERMISSION_NOT_FOUND));

        return rolePermissionMapper.toResponseDto(updatedMapping);
    }

    @Override
    public RbacRolePermissionResponse deleteRolePermission(Long rolePermissionId) {
        if (!RbacUtil.isValidId(rolePermissionId)) {
            throw new ValidationException("Role Permission ID must be a positive number");
        }

        log.info("Soft deleting role-permission mapping: rolePermissionId={}", rolePermissionId);

        RbacRolePermission mapping = rolePermissionRepository.findByRolePermissionId(rolePermissionId)
                .orElseThrow(() -> new ResourceNotFoundException(RbacRolePermissionConstants.ROLE_PERMISSION_NOT_FOUND));

        // Get the roleId before deletion
        Long roleId = mapping.getRoleId();

        rolePermissionMapper.updateStatus(mapping, RbacConstants.STATUS_INACTIVE);
        rolePermissionRepository.save(mapping);

        log.info("Role permission mapping {} soft-deleted", rolePermissionId);

        // Retrieve remaining active permissions for the role and return in hierarchical format
        return getRolePermissionHierarchy(roleId);
    }

    @Override
    @Transactional(readOnly = true)
    public RbacRolePermissionResponse getRolePermissionsByRoleId(Long roleId) {
        log.debug("Fetching role-permission hierarchy: roleId={}", roleId);
        return getRolePermissionHierarchy(roleId);
    }

    @Override
    @Transactional(readOnly = true)
    public RbacRolePermissionResponse getRolePermissionById(Long rolePermissionId) {
        if (!RbacUtil.isValidId(rolePermissionId)) {
            throw new ValidationException("Role Permission ID must be a positive number");
        }

        RbacRolePermission mapping = rolePermissionRepository.findByRolePermissionId(rolePermissionId)
                .orElseThrow(() -> new ResourceNotFoundException(RbacRolePermissionConstants.ROLE_PERMISSION_NOT_FOUND));

        // Get the roleId from the mapping and fetch all permissions for that role
        Long roleId = mapping.getRoleId();
        RbacRole role = roleRepository.findByRoleIdAndIsDeleted(roleId, RbacConstants.IS_DELETED_FALSE)
                .orElseThrow(() -> new ResourceNotFoundException(RbacRolePermissionConstants.INVALID_ROLE));
        List<RbacRolePermissionResponseDto> flatPermissions = getRolePermissionsByRoleIdFlat(roleId);

        return rolePermissionMapper.toHierarchicalResponse(
                associationMapper.toRole(role), flatPermissions);
    }

    @Transactional(readOnly = true)
    public List<RbacRolePermissionResponseDto> getRolePermissionsByRoleIdFlat(Long roleId) {
        if (!RbacUtil.isValidId(roleId)) {
            throw new ValidationException("Role ID must be a positive number");
        }

        // Verify role exists
        roleRepository.findByRoleIdAndIsDeleted(roleId, RbacConstants.IS_DELETED_FALSE)
                .orElseThrow(() -> new ResourceNotFoundException(RbacRolePermissionConstants.INVALID_ROLE));

        // Use optimized query with eager loading
        List<RbacRolePermission> mappings = rolePermissionRepository.findAllByRoleIdAndStatusWithEagerLoading(
                roleId, RbacConstants.STATUS_ACTIVE);

        return mappings.stream()
                .map(rolePermissionMapper::toResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<RbacRolePermissionResponseDto> getRolePermissionsByRoleIdAndModuleId(Long roleId, Long moduleId) {
        if (!RbacUtil.isValidId(roleId)) {
            throw new ValidationException("Role ID must be a positive number");
        }
        if (!RbacUtil.isValidId(moduleId)) {
            throw new ValidationException("Module ID must be a positive number");
        }

        // Verify role and module exist
        roleRepository.findByRoleIdAndIsDeleted(roleId, RbacConstants.IS_DELETED_FALSE)
                .orElseThrow(() -> new ResourceNotFoundException(RbacRolePermissionConstants.INVALID_ROLE));
        moduleRepository.findByModuleIdAndIsDeleted(moduleId, RbacConstants.IS_DELETED_FALSE)
                .orElseThrow(() -> new ResourceNotFoundException(RbacRolePermissionConstants.INVALID_MODULE));

        List<RbacRolePermission> mappings = rolePermissionRepository
                .findAllByRoleIdAndModuleIdWithAssociations(roleId, moduleId);

        return mappings.stream()
                .map(rolePermissionMapper::toResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<RbacRolePermissionResponse> getAllRolePermissions() {
        log.info("Fetching all active role-permission mappings in hierarchical format");

        // Use optimized query with eager loading
        List<RbacRolePermission> mappings = rolePermissionRepository.findAllByStatusWithEagerLoading(RbacConstants.STATUS_ACTIVE);

        List<RbacRolePermissionResponseDto> flatPermissions = mappings.stream()
                .map(rolePermissionMapper::toResponseDto)
                .collect(Collectors.toList());

        // Convert to hierarchical format grouped by role
        return rolePermissionMapper.toHierarchicalList(flatPermissions);
    }

    @Override
    @Transactional(readOnly = true)
    public PaginatedResponse<RbacRolePermissionResponseDto> listRolePermissions(
            RolePermissionListRequestDto request) {
        RolePermissionListRequestDto listRequest = request != null ? request : new RolePermissionListRequestDto();
        log.debug("Listing role-permissions: page={}, size={}", listRequest.getPage(), listRequest.getSize());
        Sort sort = RbacPaginationUtil.buildSort(
                listRequest.getSortBy(), listRequest.getSortDirection(), "createdAt");

        return fetchList(
                RolePermissionSpecification.build(listRequest),
                rolePermissionRepository,
                rolePermissionMapper::toResponseDto,
                listRequest.getPage(),
                listRequest.getSize(),
                sort);
    }

    // Helper methods

    private RbacRolePermissionResponse getRolePermissionHierarchy(Long roleId) {
        entityManager.flush();
        entityManager.clear();

        RbacRole role = roleRepository.findByRoleIdAndIsDeleted(roleId, RbacConstants.IS_DELETED_FALSE)
                .orElseThrow(() -> new ResourceNotFoundException(RbacRolePermissionConstants.INVALID_ROLE));
        List<RbacRolePermissionResponseDto> flatPermissions = getRolePermissionsByRoleIdFlat(roleId);
        return rolePermissionMapper.toHierarchicalResponse(
                associationMapper.toRole(role), flatPermissions);
    }

    private <E, R> PaginatedResponse<R> fetchList(
            Specification<E> specification,
            JpaSpecificationExecutor<E> repository,
            Function<E, R> mapperFunction,
            Integer page,
            Integer size,
            Sort sort) {

        if (RbacPaginationUtil.isPaginationRequested(page, size)) {
            Page<E> result = repository.findAll(
                    specification,
                    RbacPaginationUtil.buildPageable(page, size, sort));
            return RbacPaginationUtil.fromPage(result, mapperFunction);
        }

        List<R> content = repository.findAll(specification, sort).stream()
                .map(mapperFunction)
                .collect(Collectors.toList());
        return RbacPaginationUtil.fromList(content);
    }
}

