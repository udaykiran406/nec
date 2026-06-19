package com.nec.middleware.rbacAuth.rbac.service.impl;

import com.nec.middleware.exception.DuplicateException;
import com.nec.middleware.exception.ExceptionUtil;
import com.nec.middleware.exception.ResourceNotFoundException;
import com.nec.middleware.exception.ValidationException;
import com.nec.middleware.rbacAuth.auth.utils.NecSecurityUtils;
import com.nec.middleware.rbacAuth.rbac.constant.RbacConstants;
import com.nec.middleware.rbacAuth.rbac.dto.request.RbacRoleRequest;
import com.nec.middleware.rbacAuth.rbac.dto.request.RoleListRequestDto;
import com.nec.middleware.rbacAuth.rbac.dto.response.RbacRoleResponse;
import com.nec.middleware.rbacAuth.rbac.dto.response.PaginatedResponse;
import com.nec.middleware.rbacAuth.rbac.entity.RbacRole;
import com.nec.middleware.rbacAuth.rbac.mapper.RbacRoleMapper;
import com.nec.middleware.rbacAuth.rbac.repository.RbacRoleRepository;
import com.nec.middleware.rbacAuth.rbac.service.RoleService;
import com.nec.middleware.rbacAuth.rbac.specification.RoleSpecification;
import com.nec.middleware.rbacAuth.rbac.util.RbacPaginationUtil;
import com.nec.middleware.rbacAuth.rbac.util.RbacUtil;
import com.nec.middleware.rbacAuth.rbac.validation.RbacValidationUtil;
import jakarta.persistence.EntityManager;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Service implementation for Role-related operations.
 * Handles CRUD operations, status management, and hierarchical role queries.
 */
@Service
@AllArgsConstructor
@Transactional
@Slf4j
public class RoleServiceImpl implements RoleService {

    private final RbacRoleRepository roleRepository;
    private final RbacRoleMapper mapper;
    private final RbacValidationUtil validationUtil;
    private final EntityManager entityManager;

    @Override
    public RbacRoleResponse createRole(RbacRoleRequest request) {
        // Business-level validation (roleCode is excluded – it is auto-generated)
        validationUtil.validateCreateRequest(request);

        // Auto-generate a unique role code from the role name
        String generatedCode = generateRoleCode(request.getRoleName());
        request.setRoleCode(generatedCode);

        // Check for duplicate role name
        if (roleRepository.existsByRoleNameIgnoreCaseAndIsDeleted(
                request.getRoleName(), RbacConstants.IS_DELETED_FALSE)) {
            throw ExceptionUtil.duplicateConstraint("uq_nec_rbac_roles_name", request.getRoleName());
        }

        // If parentRoleId is provided, validate that it exists
        if (request.getParentRoleId() != null && RbacUtil.isValidId(request.getParentRoleId())) {
            roleRepository.findByRoleIdAndIsDeleted(
                    request.getParentRoleId(), RbacConstants.IS_DELETED_FALSE)
                    .orElseThrow(() -> new ResourceNotFoundException(
                            RbacUtil.buildMessage(RbacConstants.PARENT_ROLE_NOT_FOUND,
                                    request.getParentRoleId())));
        }

        try {
            // Map request to entity and save
            RbacRole entity = mapper.toEntity(request);
            RbacRole savedEntity = roleRepository.save(entity);
            return mapRoleWithAssociations(savedEntity);
        } catch (DataIntegrityViolationException e) {
            throw ExceptionUtil.fromDataIntegrityViolation(e);
        }
    }

    @Override
    public RbacRoleResponse updateRole(Long id, RbacRoleRequest request) {
        request.setRoleId(id);
        // Business-level validation
        validationUtil.validateUpdateRequest(request);

        // Fetch the existing role
        RbacRole existingRole = roleRepository.findByRoleIdAndIsDeleted(
                id, RbacConstants.IS_DELETED_FALSE)
                .orElseThrow(() -> new ResourceNotFoundException(
                        RbacUtil.buildMessage(RbacConstants.ROLE_NOT_FOUND, id)));

        // Check for duplicate role code (excluding the current role)
        if (RbacUtil.isNotBlank(request.getRoleCode())) {
            if (roleRepository.existsByRoleCodeIgnoreCaseAndRoleIdNot(
                    request.getRoleCode(), id)) {
                throw new DuplicateException(
                        RbacUtil.buildMessage(RbacConstants.DUPLICATE_ROLE_CODE, request.getRoleCode()));
            }
        }

        // Check for duplicate role name (excluding the current role)
        if (RbacUtil.isNotBlank(request.getRoleName())) {
            if (roleRepository.existsByRoleNameIgnoreCaseAndRoleIdNot(
                    request.getRoleName(), id)) {
                throw ExceptionUtil.duplicateConstraint("uq_nec_rbac_roles_name", request.getRoleName());
            }
        }

        // Validate parentRoleId if provided (cannot reference itself)
        if (request.getParentRoleId() != null && RbacUtil.isValidId(request.getParentRoleId())) {
            if (request.getParentRoleId().equals(id)) {
                throw new ValidationException(RbacConstants.INVALID_PARENT_ROLE);
            }
            roleRepository.findByRoleIdAndIsDeleted(
                    request.getParentRoleId(), RbacConstants.IS_DELETED_FALSE)
                    .orElseThrow(() -> new ResourceNotFoundException(
                            RbacUtil.buildMessage(RbacConstants.PARENT_ROLE_NOT_FOUND,
                                    request.getParentRoleId())));
        }

        try {
            // Apply updates to the entity
            mapper.updateEntity(existingRole, request);
            RbacRole savedEntity = roleRepository.save(existingRole);
            return mapRoleWithAssociations(savedEntity);
        } catch (DataIntegrityViolationException e) {
            throw ExceptionUtil.fromDataIntegrityViolation(e);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public RbacRoleResponse getRoleById(Long roleId) {
        RbacRole role = roleRepository.findByRoleIdAndIsDeletedWithAssociations(roleId, RbacConstants.IS_DELETED_FALSE)
                .orElseThrow(() -> new ResourceNotFoundException(
                        RbacUtil.buildMessage(RbacConstants.ROLE_NOT_FOUND, roleId)));
        return mapper.toResponseDto(role);
    }

    @Override
    @Transactional(readOnly = true)
    public List<RbacRoleResponse> getAllRoles() {
        List<RbacRole> roles = roleRepository.findAllByIsDeletedWithAssociations(RbacConstants.IS_DELETED_FALSE);
        return roles.stream()
                .map(mapper::toResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public PaginatedResponse<RbacRoleResponse> listRoles(RoleListRequestDto request) {
        RoleListRequestDto listRequest = request != null ? request : new RoleListRequestDto();
        Sort sort = RbacPaginationUtil.buildSort(
                listRequest.getSortBy(), listRequest.getSortDirection(), "createdDate");
        return fetchList(
                RoleSpecification.build(listRequest),
                roleRepository,
                mapper::toResponseDto,
                listRequest.getPage(),
                listRequest.getSize(),
                sort);
    }

    @Override
    @Transactional(readOnly = true)
    public List<RbacRoleResponse> getRolesByStatus(String status) {
        if (!RbacUtil.isValidStatus(status)) {
            throw new ValidationException(RbacConstants.INVALID_STATUS);
        }
        List<RbacRole> roles = roleRepository.findAllByStatusAndIsDeletedWithAssociations(
                status.toUpperCase(), RbacConstants.IS_DELETED_FALSE);
        return roles.stream()
                .map(mapper::toResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<RbacRoleResponse> getChildRoles(Long parentRoleId) {
        // Verify that the parent role exists and is not deleted
        roleRepository.findByRoleIdAndIsDeleted(parentRoleId, RbacConstants.IS_DELETED_FALSE)
                .orElseThrow(() -> new ResourceNotFoundException(
                        RbacUtil.buildMessage(RbacConstants.PARENT_ROLE_NOT_FOUND, parentRoleId)));

        List<RbacRole> childRoles = roleRepository.findAllByParentRoleIdAndIsDeletedWithAssociations(
                parentRoleId, RbacConstants.IS_DELETED_FALSE);
        return childRoles.stream()
                .map(mapper::toResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteRole(Long roleId) {
        RbacRole role = roleRepository.findByRoleIdAndIsDeleted(roleId, RbacConstants.IS_DELETED_FALSE)
                .orElseThrow(() -> new ResourceNotFoundException(
                        RbacUtil.buildMessage(RbacConstants.ROLE_NOT_FOUND, roleId)));
        role.setIsDeleted(RbacConstants.IS_DELETED_TRUE);
        roleRepository.save(role);
    }

    @Override
    public RbacRoleResponse changeRoleStatus(Long roleId, String status) {
        // Validate the new status value
        if (!RbacUtil.isValidStatus(status)) {
            throw new ValidationException(RbacConstants.INVALID_STATUS);
        }

        // Fetch the existing non-deleted role
        RbacRole role = roleRepository.findByRoleIdAndIsDeleted(roleId, RbacConstants.IS_DELETED_FALSE)
                .orElseThrow(() -> new ResourceNotFoundException(
                        RbacUtil.buildMessage(RbacConstants.ROLE_NOT_FOUND, roleId)));

        // Apply status change
        role.setStatus(status.toUpperCase());
        String actingUserId = resolveActingUserId();
        if (org.springframework.util.StringUtils.hasText(actingUserId)) {
            role.setModifiedByUserId(actingUserId);
        }

        RbacRole savedEntity = roleRepository.saveAndFlush(role);
        return mapRoleWithAssociations(savedEntity);
    }

    private String resolveActingUserId() {
        var user = NecSecurityUtils.getCurrentUserOrNull();
        return user != null ? user.getUserId() : null;
    }

    // Helper methods

    /**
     * Generates a unique role code in the format {@code <PREFIX><3-digit-sequence>}.
     */
    private String generateRoleCode(String roleName) {
        String prefix = generateRolePrefix(roleName);
        List<String> existingCodes = roleRepository.findRoleCodesByPrefixPattern(prefix + "%");

        int maxSeq = existingCodes.stream()
                .filter(code -> code != null && code.length() > prefix.length())
                .mapToInt(code -> {
                    try {
                        return Integer.parseInt(code.substring(prefix.length()));
                    } catch (NumberFormatException e) {
                        return 0;
                    }
                })
                .max()
                .orElse(0);

        return prefix + String.format("%03d", maxSeq + 1);
    }

    /**
     * Derives a 2–5 character uppercase prefix from the first word of a role name.
     */
    private String generateRolePrefix(String roleName) {
        if (roleName == null || roleName.isBlank()) {
            return "ROLE";
        }
        String firstWord = roleName.trim().split("\\s+")[0]
                .toUpperCase()
                .replaceAll("[^A-Z]", "");

        if (firstWord.isEmpty()) {
            return "ROLE";
        }

        final String VOWELS = "AEIOU";
        boolean startsWithVowel = VOWELS.indexOf(firstWord.charAt(0)) >= 0;
        int stopAt = startsWithVowel ? 3 : 2;
        int maxLen = 5;

        StringBuilder prefix = new StringBuilder();
        int vowelCount = 0;

        for (int i = 0; i < firstWord.length() && prefix.length() < maxLen; i++) {
            char c = firstWord.charAt(i);
            if (VOWELS.indexOf(c) >= 0) {
                vowelCount++;
                if (vowelCount == stopAt) {
                    break;
                }
            }
            prefix.append(c);
        }

        String result = prefix.toString();
        if (result.length() < 2 && firstWord.length() >= 2) {
            result = firstWord.substring(0, 2);
        }
        return result.isEmpty() ? "ROLE" : result;
    }

    /**
     * Maps a managed RbacRole entity to a response DTO with all associations loaded.
     */
    private RbacRoleResponse mapRoleWithAssociations(RbacRole role) {
        Long roleId = role.getRoleId();
        entityManager.flush();
        entityManager.clear();

        return roleRepository.findByRoleIdAndIsDeletedWithAssociations(roleId, RbacConstants.IS_DELETED_FALSE)
                .map(mapper::toResponseDto)
                .orElseThrow(() -> new ResourceNotFoundException(
                        RbacUtil.buildMessage(RbacConstants.ROLE_NOT_FOUND, roleId)));
    }

    /**
     * Executes a specification-based list query with optional pagination.
     */
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

