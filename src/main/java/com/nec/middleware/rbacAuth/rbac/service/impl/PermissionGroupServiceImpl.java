package com.nec.middleware.rbacAuth.rbac.service.impl;

import com.nec.middleware.exception.ExceptionUtil;
import com.nec.middleware.exception.ResourceNotFoundException;
import com.nec.middleware.exception.ValidationException;
import com.nec.middleware.rbacAuth.rbac.constant.RbacConstants;
import com.nec.middleware.rbacAuth.rbac.dto.request.RbacModuleRequest;
import com.nec.middleware.rbacAuth.rbac.dto.request.RbacPermissionGroupRequest;
import com.nec.middleware.rbacAuth.rbac.dto.request.ModuleListRequestDto;
import com.nec.middleware.rbacAuth.rbac.dto.request.GroupListRequestDto;
import com.nec.middleware.rbacAuth.rbac.dto.response.RbacModuleResponse;
import com.nec.middleware.rbacAuth.rbac.dto.response.RbacGroupResponse;
import com.nec.middleware.rbacAuth.rbac.dto.response.RbacPermissionGroupResponse;
import com.nec.middleware.rbacAuth.rbac.dto.response.RbacPermissionGroupModuleResponse;
import com.nec.middleware.rbacAuth.rbac.dto.response.RbacPermissionGroupGroupResponse;
import com.nec.middleware.rbacAuth.rbac.dto.response.RbacPermissionGroupPermissionResponse;
import com.nec.middleware.rbacAuth.rbac.dto.response.ApiResponse;
import com.nec.middleware.rbacAuth.rbac.dto.response.PaginatedResponse;
import com.nec.middleware.rbacAuth.rbac.entity.RbacModule;
import com.nec.middleware.rbacAuth.rbac.entity.RbacPermissionGroup;
import com.nec.middleware.rbacAuth.rbac.entity.RbacPermission;
import com.nec.middleware.rbacAuth.rbac.mapper.RbacModuleMapper;
import com.nec.middleware.rbacAuth.rbac.mapper.RbacHierarchyMapper;
import com.nec.middleware.rbacAuth.rbac.mapper.RbacGroupMapper;
import com.nec.middleware.rbacAuth.rbac.mapper.AssociationMapper;
import com.nec.middleware.rbacAuth.rbac.repository.RbacModuleRepository;
import com.nec.middleware.rbacAuth.rbac.repository.RbacPermissionGroupRepository;
import com.nec.middleware.rbacAuth.rbac.repository.RbacPermissionRepository;
import com.nec.middleware.rbacAuth.rbac.service.PermissionGroupService;
import com.nec.middleware.rbacAuth.rbac.specification.ModuleSpecification;
import com.nec.middleware.rbacAuth.rbac.specification.GroupSpecification;
import com.nec.middleware.rbacAuth.rbac.util.RbacPaginationUtil;
import com.nec.middleware.rbacAuth.rbac.util.RbacUtil;
import com.nec.middleware.rbacAuth.rbac.validation.RbacModuleValidationUtil;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Service implementation for Permission Group, Module, and Permission operations.
 * Handles hierarchical permission group management and module/group/permission CRUD operations.
 */
@Service
@AllArgsConstructor
@Transactional
@Slf4j
public class PermissionGroupServiceImpl implements PermissionGroupService {

    private final RbacModuleRepository moduleRepository;
    private final RbacModuleMapper moduleMapper;
    private final RbacModuleValidationUtil moduleValidationUtil;
    private final RbacPermissionGroupRepository groupRepository;
    private final RbacPermissionRepository permissionRepository;
    private final RbacHierarchyMapper hierarchyMapper;
    private final RbacGroupMapper groupMapper;
    private final AssociationMapper associationMapper;

    // =========================================================================
    // PERMISSION GROUP OPERATIONS
    // =========================================================================

    @Override
    public ApiResponse<RbacPermissionGroupResponse> createPermissionGroup(RbacPermissionGroupRequest request) {
        return persistPermissionGroup(request, true);
    }

    @Override
    public ApiResponse<RbacPermissionGroupResponse> updatePermissionGroup(RbacPermissionGroupRequest request) {
        return persistPermissionGroup(request, false);
    }

    private ApiResponse<RbacPermissionGroupResponse> persistPermissionGroup(
            RbacPermissionGroupRequest request, boolean isCreate) {
        log.info("Starting permission group save process");

        try {
            // Validate request
            if (request == null || request.getModules() == null || request.getModules().isEmpty()) {
                log.error("Invalid permission group request: modules list is null or empty");
                throw new ValidationException(RbacConstants.MODULES_LIST_EMPTY);
            }

            List<RbacPermissionGroupModuleResponse> savedModules = new ArrayList<>();

            // Iterate through modules
            for (RbacPermissionGroupRequest.ModuleRequest moduleRequest : request.getModules()) {
                log.debug("Processing module: {}", moduleRequest.getModuleCode());

                // Handle module save - create or update
                RbacModule savedModule;
                if (moduleRequest.getModuleId() != null) {
                    // Update existing module
                    RbacModule existingModule = moduleRepository.findByModuleIdAndIsDeleted(
                            moduleRequest.getModuleId(), RbacConstants.IS_DELETED_FALSE)
                            .orElseThrow(() -> new ResourceNotFoundException(
                                    "Module not found with ID: " + moduleRequest.getModuleId()));
                    existingModule.setModuleCode(trimSafe(moduleRequest.getModuleCode()));
                    existingModule.setModuleName(trimSafe(moduleRequest.getModuleName()));
                    existingModule.setDescription(trimSafe(moduleRequest.getDescription()));
                    existingModule.setDisplayOrder(moduleRequest.getDisplayOrder());
                    existingModule.setStatus(moduleRequest.getStatus());
                    if (moduleRequest.getModifiedByUserId() != null) {
                        existingModule.setModifiedByUserId(moduleRequest.getModifiedByUserId());
                    }
                    savedModule = moduleRepository.save(existingModule);
                } else {
                    // Create new module
                    RbacModule moduleEntity = hierarchyMapper.toModuleEntity(moduleRequest);
                    savedModule = moduleRepository.save(moduleEntity);
                }
                Long savedModuleId = savedModule.getModuleId();

                // Validate groups list
                if (moduleRequest.getGroups() == null || moduleRequest.getGroups().isEmpty()) {
                    log.error("Invalid hierarchy: module {} has no groups", moduleRequest.getModuleCode());
                    throw new ValidationException(RbacConstants.GROUPS_LIST_EMPTY);
                }

                List<RbacPermissionGroupGroupResponse> savedGroups = new ArrayList<>();

                // Iterate through groups
                for (RbacPermissionGroupRequest.GroupRequest groupRequest : moduleRequest.getGroups()) {
                    log.debug("Processing permission group: {}", groupRequest.getGroupCode());

                    // Handle group save - create or update
                    RbacPermissionGroup savedGroup;
                    if (groupRequest.getGroupId() != null) {
                        // Update existing group
                        RbacPermissionGroup existingGroup = groupRepository.findById(groupRequest.getGroupId())
                                .orElseThrow(() -> new ResourceNotFoundException(
                                        "Permission group not found with ID: " + groupRequest.getGroupId()));
                        existingGroup.setGroupCode(trimSafe(groupRequest.getGroupCode()));
                        existingGroup.setGroupName(trimSafe(groupRequest.getGroupName()));
                        existingGroup.setDescription(trimSafe(groupRequest.getDescription()));
                        existingGroup.setDisplayOrder(groupRequest.getDisplayOrder());
                        existingGroup.setStatus(groupRequest.getStatus());
                        if (groupRequest.getModifiedByUserId() != null) {
                            existingGroup.setModifiedByUserId(groupRequest.getModifiedByUserId());
                        }
                        savedGroup = groupRepository.save(existingGroup);
                    } else {
                        // Create new group
                        RbacPermissionGroup groupEntity = hierarchyMapper.toGroupEntity(groupRequest, savedModuleId);
                        savedGroup = groupRepository.save(groupEntity);
                    }
                    Long savedGroupId = savedGroup.getGroupId();

                    // Validate permissions list
                    if (groupRequest.getPermissions() == null || groupRequest.getPermissions().isEmpty()) {
                        log.error("Invalid hierarchy: group {} has no permissions", groupRequest.getGroupCode());
                        throw new ValidationException(RbacConstants.PERMISSIONS_LIST_EMPTY);
                    }

                    List<RbacPermissionGroupPermissionResponse> savedPermissions = new ArrayList<>();

                    // Iterate through permissions
                    for (RbacPermissionGroupRequest.PermissionRequest permissionRequest : groupRequest.getPermissions()) {
                        log.debug("Processing permission: {}", permissionRequest.getPermissionCode());

                        // Handle permission save - create or update
                        RbacPermission savedPermission;
                        if (permissionRequest.getPermissionId() != null) {
                            // Update existing permission
                            RbacPermission existingPermission = permissionRepository.findById(permissionRequest.getPermissionId())
                                    .orElseThrow(() -> new ResourceNotFoundException(
                                            "Permission not found with ID: " + permissionRequest.getPermissionId()));
                            existingPermission.setPermissionCode(trimSafe(permissionRequest.getPermissionCode()));
                            existingPermission.setPermissionName(trimSafe(permissionRequest.getPermissionName()));
                            existingPermission.setDescription(trimSafe(permissionRequest.getDescription()));
                            existingPermission.setDisplayOrder(permissionRequest.getDisplayOrder());
                            existingPermission.setStatus(permissionRequest.getStatus());
                            existingPermission.setIsSideMenu(permissionRequest.getIsSideMenu() != null ? permissionRequest.getIsSideMenu() : false);
                            if (permissionRequest.getModifiedByUserId() != null) {
                                existingPermission.setModifiedByUserId(permissionRequest.getModifiedByUserId());
                            }
                            savedPermission = permissionRepository.save(existingPermission);
                        } else {
                            // Create new permission
                            RbacPermission permissionEntity = hierarchyMapper.toPermissionEntity(
                                    permissionRequest, savedModuleId, savedGroupId);
                            savedPermission = permissionRepository.save(permissionEntity);
                        }

                        savedPermissions.add(
                                buildHierarchyPermissionResponse(savedPermission, savedGroup, savedModule));
                    }

                    // Build group response
                    RbacPermissionGroupGroupResponse groupResponse =
                            RbacPermissionGroupGroupResponse.builder()
                                    .groupCode(savedGroup.getGroupCode())
                                    .groupName(savedGroup.getGroupName())
                                    .description(savedGroup.getDescription())
                                    .displayOrder(savedGroup.getDisplayOrder())
                                    .status(savedGroup.getStatus())
                                    .module(associationMapper.toModule(savedModule))
                                    .permissions(savedPermissions)
                                    .build();
                    savedGroups.add(groupResponse);
                }

                // Build module response
                RbacPermissionGroupModuleResponse moduleResponse =
                        RbacPermissionGroupModuleResponse.builder()
                                .moduleCode(savedModule.getModuleCode())
                                .moduleName(savedModule.getModuleName())
                                .description(savedModule.getDescription())
                                .displayOrder(savedModule.getDisplayOrder())
                                .status(savedModule.getStatus())
                                .groups(savedGroups)
                                .build();
                savedModules.add(moduleResponse);
            }

            RbacPermissionGroupResponse hierarchyResponse = RbacPermissionGroupResponse.builder()
                    .modules(savedModules)
                    .build();

            log.info("Permission group saved successfully");
            if (isCreate) {
                return ApiResponse.created(RbacConstants.PERMISSION_GROUP_SAVED_SUCCESS, hierarchyResponse);
            }
            return ApiResponse.ok(RbacConstants.PERMISSION_GROUP_SAVED_SUCCESS, hierarchyResponse);

        } catch (ValidationException e) {
            log.error("Validation error during permission group save: {}", e.getMessage());
            throw e;
        } catch (DataIntegrityViolationException e) {
            log.warn("Database constraint violation during permission group save", e);
            throw ExceptionUtil.fromDataIntegrityViolation(e);
        } catch (ResourceNotFoundException e) {
            log.error("Resource not found during permission group save: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Unexpected error during permission group save", e);
            throw new ValidationException("Unable to save permission group. Please try again.");
        }
    }

    @Override
    @Transactional(readOnly = true)
    public RbacPermissionGroupResponse getPermissionGroupByModuleId(Long moduleId) {
        log.info("Fetching permission group for module ID: {}", moduleId);

        // Get module
        RbacModule module = moduleRepository.findByModuleIdAndIsDeleted(moduleId, RbacConstants.IS_DELETED_FALSE)
                .orElseThrow(() -> {
                    log.error("Module not found with ID: {}", moduleId);
                    return new ResourceNotFoundException("Module not found with ID: " + moduleId);
                });

        // Get active (non-deleted) groups for this module ordered by display order and filtered by status
        List<RbacPermissionGroup> groups = groupRepository.findByModuleIdAndStatusAndIsDeletedOrderByDisplayOrderAsc(
                moduleId, RbacConstants.STATUS_ACTIVE, RbacConstants.IS_DELETED_FALSE);

        // Build response
        RbacPermissionGroupModuleResponse moduleResponse = RbacPermissionGroupModuleResponse.builder()
                .moduleCode(module.getModuleCode())
                .moduleName(module.getModuleName())
                .description(module.getDescription())
                .displayOrder(module.getDisplayOrder())
                .status(module.getStatus())
                .groups(groups.stream()
                        .map(this::buildGroupResponse)
                        .collect(Collectors.toList()))
                .build();

        List<RbacPermissionGroupModuleResponse> modules = new ArrayList<>();
        modules.add(moduleResponse);

        return RbacPermissionGroupResponse.builder()
                .modules(modules)
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public RbacPermissionGroupResponse getAllPermissionGroups() {
        log.info("Fetching all permission groups");

        // Get all non-deleted modules ordered by display order
        List<RbacModule> modules = moduleRepository.findAllByIsDeletedOrderByDisplayOrderAsc(RbacConstants.IS_DELETED_FALSE);

        List<RbacPermissionGroupModuleResponse> moduleResponses = modules.stream()
                .map(module -> {
                    // Get active (non-deleted) groups for this module ordered by display order and filtered by status
                    List<RbacPermissionGroup> groups = groupRepository.findByModuleIdAndStatusAndIsDeletedOrderByDisplayOrderAsc(
                            module.getModuleId(), RbacConstants.STATUS_ACTIVE, RbacConstants.IS_DELETED_FALSE);

                    return RbacPermissionGroupModuleResponse.builder()
                            .moduleCode(module.getModuleCode())
                            .moduleName(module.getModuleName())
                            .description(module.getDescription())
                            .displayOrder(module.getDisplayOrder())
                            .status(module.getStatus())
                            .groups(groups.stream()
                                    .map(this::buildGroupResponse)
                                    .collect(Collectors.toList()))
                            .build();
                })
                .collect(Collectors.toList());

        return RbacPermissionGroupResponse.builder()
                .modules(moduleResponses)
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public PaginatedResponse<RbacPermissionGroupModuleResponse> listPermissionGroups(ModuleListRequestDto request) {
        ModuleListRequestDto listRequest = request != null ? request : new ModuleListRequestDto();
        Sort sort = RbacPaginationUtil.buildSort(
                listRequest.getSortBy(), listRequest.getSortDirection(), "createdDate");

        return fetchList(
                ModuleSpecification.build(listRequest),
                moduleRepository,
                this::buildModuleHierarchyResponse,
                listRequest.getPage(),
                listRequest.getSize(),
                sort);
    }

    // =========================================================================
    // MODULE OPERATIONS
    // =========================================================================

    @Override
    public RbacModuleResponse saveModule(RbacModuleRequest request) {
        if (request.getModuleId() == null) {
            return createModule(request);
        } else {
            return updateModule(request);
        }
    }

    @Override
    public RbacModuleResponse createModule(RbacModuleRequest request) {
        // Business-level validation
        moduleValidationUtil.validateCreateRequest(request);

        // Check for duplicate module code
        if (moduleRepository.existsByModuleCodeIgnoreCaseAndIsDeleted(
                request.getModuleCode(), RbacConstants.IS_DELETED_FALSE)) {
            throw ExceptionUtil.duplicateConstraint("uq_nec_rbac_module_code", request.getModuleCode());
        }

        try {
            // Map request to entity and save
            RbacModule entity = moduleMapper.toEntity(request);
            RbacModule savedEntity = moduleRepository.save(entity);
            return moduleMapper.toResponseDto(savedEntity);
        } catch (DataIntegrityViolationException e) {
            throw ExceptionUtil.fromDataIntegrityViolation(
                    e, "uq_nec_rbac_module_code", request.getModuleCode());
        }
    }

    @Override
    public RbacModuleResponse updateModule(RbacModuleRequest request) {
        // Business-level validation
        moduleValidationUtil.validateUpdateRequest(request);

        // Fetch the existing module
        RbacModule existingModule = moduleRepository.findByModuleIdAndIsDeleted(
                request.getModuleId(), RbacConstants.IS_DELETED_FALSE)
                .orElseThrow(() -> new ResourceNotFoundException(
                        RbacUtil.buildMessage(RbacConstants.MODULE_NOT_FOUND, request.getModuleId())));

        // Check for duplicate module code (excluding the current module)
        if (RbacUtil.isNotBlank(request.getModuleCode())) {
            if (moduleRepository.existsByModuleCodeIgnoreCaseAndModuleIdNot(
                    request.getModuleCode(), request.getModuleId())) {
                throw ExceptionUtil.duplicateConstraint("uq_nec_rbac_module_code", request.getModuleCode());
            }
        }

        try {
            // Apply updates to the entity
            moduleMapper.updateEntity(existingModule, request);
            RbacModule savedEntity = moduleRepository.save(existingModule);
            return moduleMapper.toResponseDto(savedEntity);
        } catch (DataIntegrityViolationException e) {
            throw ExceptionUtil.fromDataIntegrityViolation(
                    e, "uq_nec_rbac_module_code", request.getModuleCode());
        }
    }

    @Override
    @Transactional(readOnly = true)
    public RbacModuleResponse getModuleById(Long moduleId) {
        RbacModule module = moduleRepository.findByModuleIdAndIsDeleted(moduleId, RbacConstants.IS_DELETED_FALSE)
                .orElseThrow(() -> new ResourceNotFoundException(
                        RbacUtil.buildMessage(RbacConstants.MODULE_NOT_FOUND, moduleId)));
        return moduleMapper.toResponseDto(module);
    }

    @Override
    @Transactional(readOnly = true)
    public List<RbacModuleResponse> getAllModules() {
        List<RbacModule> modules = moduleRepository.findAllByIsDeletedOrderByDisplayOrderAsc(RbacConstants.IS_DELETED_FALSE);
        return modules.stream()
                .map(moduleMapper::toResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<RbacModuleResponse> getModulesByStatus(String status) {
        if (!RbacUtil.isValidStatus(status)) {
            throw new ValidationException(RbacConstants.INVALID_STATUS);
        }
        List<RbacModule> modules = moduleRepository.findAllByStatusAndIsDeletedOrderByDisplayOrderAsc(
                status.toUpperCase(), RbacConstants.IS_DELETED_FALSE);
        return modules.stream()
                .map(moduleMapper::toResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteModule(Long moduleId) {
        RbacModule module = moduleRepository.findByModuleIdAndIsDeleted(moduleId, RbacConstants.IS_DELETED_FALSE)
                .orElseThrow(() -> new ResourceNotFoundException(
                        RbacUtil.buildMessage(RbacConstants.MODULE_NOT_FOUND, moduleId)));
        module.setIsDeleted(RbacConstants.IS_DELETED_TRUE);
        moduleRepository.save(module);
    }

    @Override
    @Transactional(readOnly = true)
    public PaginatedResponse<RbacModuleResponse> listModules(ModuleListRequestDto request) {
        ModuleListRequestDto listRequest = request != null ? request : new ModuleListRequestDto();
        Sort sort = RbacPaginationUtil.buildSort(
                listRequest.getSortBy(), listRequest.getSortDirection(), "createdDate");
        return fetchList(
                ModuleSpecification.build(listRequest),
                moduleRepository,
                moduleMapper::toResponseDto,
                listRequest.getPage(),
                listRequest.getSize(),
                sort);
    }

    // =========================================================================
    // GROUP OPERATIONS
    // =========================================================================

    @Override
    @Transactional(readOnly = true)
    public PaginatedResponse<RbacGroupResponse> listGroups(GroupListRequestDto request) {
        GroupListRequestDto listRequest = request != null ? request : new GroupListRequestDto();
        Sort sort = RbacPaginationUtil.buildSort(
                listRequest.getSortBy(), listRequest.getSortDirection(), "createdDate");
        return fetchList(
                GroupSpecification.build(listRequest),
                groupRepository,
                groupMapper::toResponseDto,
                listRequest.getPage(),
                listRequest.getSize(),
                sort);
    }

    // Helper methods

    private RbacPermissionGroupModuleResponse buildModuleHierarchyResponse(RbacModule module) {
        List<RbacPermissionGroup> groups = groupRepository.findByModuleIdAndStatusAndIsDeletedOrderByDisplayOrderAsc(
                module.getModuleId(), RbacConstants.STATUS_ACTIVE, RbacConstants.IS_DELETED_FALSE);

        return RbacPermissionGroupModuleResponse.builder()
                .moduleCode(module.getModuleCode())
                .moduleName(module.getModuleName())
                .description(module.getDescription())
                .displayOrder(module.getDisplayOrder())
                .status(module.getStatus())
                .groups(groups.stream()
                        .map(this::buildGroupResponse)
                        .collect(Collectors.toList()))
                .build();
    }

    private RbacPermissionGroupGroupResponse buildGroupResponse(RbacPermissionGroup group) {
        RbacPermissionGroup groupWithAssociations = groupRepository.findByGroupIdWithAssociations(group.getGroupId())
                .orElse(group);
        RbacModule module = groupWithAssociations.getModule();
        if (module == null && group.getModuleId() != null) {
            module = moduleRepository.findByModuleIdAndIsDeleted(
                    group.getModuleId(), RbacConstants.IS_DELETED_FALSE).orElse(null);
        }

        List<RbacPermission> permissions = permissionRepository.findByGroupIdAndStatusAndIsDeletedOrderByDisplayOrderAsc(
                group.getGroupId(), RbacConstants.STATUS_ACTIVE, RbacConstants.IS_DELETED_FALSE);

        RbacModule resolvedModule = module;
        RbacPermissionGroup resolvedGroup = groupWithAssociations;

        return RbacPermissionGroupGroupResponse.builder()
                .groupCode(group.getGroupCode())
                .groupName(group.getGroupName())
                .description(group.getDescription())
                .displayOrder(group.getDisplayOrder())
                .status(group.getStatus())
                .module(associationMapper.toModule(resolvedModule))
                .permissions(permissions.stream()
                        .map(permission -> buildHierarchyPermissionResponse(
                                permission, resolvedGroup, resolvedModule))
                        .collect(Collectors.toList()))
                .build();
    }

    private RbacPermissionGroupPermissionResponse buildHierarchyPermissionResponse(
            RbacPermission permission,
            RbacPermissionGroup group,
            RbacModule module) {
        return RbacPermissionGroupPermissionResponse.builder()
                .permissionCode(permission.getPermissionCode())
                .permissionName(permission.getPermissionName())
                .description(permission.getDescription())
                .displayOrder(permission.getDisplayOrder())
                .status(permission.getStatus())
                .isSideMenu(permission.getIsSideMenu())
                .group(associationMapper.toGroup(group))
                .module(associationMapper.toModule(module))
                .build();
    }

    private String trimSafe(String value) {
        return value != null ? value.trim() : null;
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

