package com.nec.middleware.rbacAuth.rbac.service.impl;

import com.nec.middleware.exception.ErrorMessageMapper;
import com.nec.middleware.exception.ExceptionUtil;
import com.nec.middleware.exception.ResourceNotFoundException;
import com.nec.middleware.exception.ValidationException;
import com.nec.middleware.rbacAuth.rbac.constant.RbacConstants;
import com.nec.middleware.rbacAuth.rbac.dto.request.RbacUserRequest;
import com.nec.middleware.rbacAuth.rbac.dto.request.UserListRequestDto;
import com.nec.middleware.rbacAuth.rbac.dto.response.RbacUserResponse;
import com.nec.middleware.rbacAuth.rbac.dto.response.PaginatedResponse;
import com.nec.middleware.rbacAuth.rbac.entity.RbacUser;
import com.nec.middleware.rbacAuth.rbac.mapper.RbacUserMapper;
import com.nec.middleware.rbacAuth.rbac.repository.RbacRoleRepository;
import com.nec.middleware.rbacAuth.rbac.repository.RbacUserRepository;
import com.nec.middleware.rbacAuth.rbac.service.UserService;
import com.nec.middleware.rbacAuth.rbac.specification.UserSpecification;
import com.nec.middleware.rbacAuth.rbac.util.RbacPaginationUtil;
import com.nec.middleware.rbacAuth.rbac.util.RbacUtil;
import com.nec.middleware.rbacAuth.keycloak.client.request.KeycloakCredentialRequest;
import com.nec.middleware.rbacAuth.keycloak.client.request.KeycloakUserCreateRequest;
import com.nec.middleware.rbacAuth.keycloak.provider.KeycloakAuthProvider;
import jakarta.persistence.EntityManager;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Service implementation for User-related operations.
 * Handles user CRUD operations, status management, and role-based queries.
 */
@Service
@AllArgsConstructor
@Transactional
@Slf4j
public class UserServiceImpl implements UserService {

    private final RbacUserRepository userRepository;
    private final RbacUserMapper userMapper;
    private final RbacRoleRepository roleRepository;
    private final KeycloakAuthProvider keycloakAuthProvider;
    private final EntityManager entityManager;
    private final PasswordEncoder passwordEncoder;

    @Override
    public RbacUserResponse createUser(RbacUserRequest request) {
        if (request == null) {
            throw new ValidationException(RbacConstants.REQUEST_NULL);
        }

        validateUserForeignKeyReferences(request);

        if (!RbacUtil.isNotBlank(request.getPhone())) {
            throw new ValidationException("Phone is required and cannot be blank");
        }
        String phone = request.getPhone().trim();

        if (userRepository.existsByPhoneAndIsDeleted(phone, RbacConstants.IS_DELETED_FALSE)) {
            throw ExceptionUtil.duplicateConstraint("uq_nec_rbac_users_phone", phone);
        }

        if (!RbacUtil.isNotBlank(request.getEmail())) {
            throw new ValidationException("Email is required and cannot be blank");
        }
        String email = request.getEmail().trim();
        log.info("Starting RBAC user creation: email={}", email);

        if (userRepository.existsByEmailIgnoreCaseAndIsDeleted(email, RbacConstants.IS_DELETED_FALSE)) {
            throw ExceptionUtil.duplicateConstraint("uq_nec_rbac_users_email", email);
        }

        if (!RbacUtil.isNotBlank(request.getPassword())) {
            throw new ValidationException("Password is required for user creation");
        }
        String password = request.getPassword().trim();

        String keycloakUserId = null;
        try {
            request.setPhone(phone);
            request.setEmail(email);

            KeycloakUserCreateRequest kcRequest = buildKeycloakUserCreateRequest(request, email, password);
            keycloakUserId = keycloakAuthProvider.createUserInKeycloak(kcRequest);

            RbacUser entity = userMapper.toEntity(request);
            entity.setUserId(keycloakUserId);
            entity.setKeycloakUserId(keycloakUserId);
            entity.setPasswordHash(passwordEncoder.encode(password));
            entity.setPasswordToBeChanged(
                request.getPasswordToBeChanged() != null ? request.getPasswordToBeChanged() : false);
            entity.setEmailVerified(
                request.getEmailVerified() != null ? request.getEmailVerified() : false);
            entity.setMobileVerified(
                request.getMobileVerified() != null ? request.getMobileVerified() : false);
            entity.setFailedLoginAttempts(0);
            entity.setStatus(RbacConstants.IS_ACTIVE_TRUE.equals(request.getIsActive()) ? "ACTIVE" : "INACTIVE");

            RbacUser saved = userRepository.saveAndFlush(entity);
            log.info("RBAC user saved: userId={}, keycloakUserId={}", saved.getUserId(), saved.getKeycloakUserId());
            return mapUserWithAssociations(saved);
        } catch (DataIntegrityViolationException e) {
            log.warn("Data integrity violation during user creation. Phone: {}, Email: {}", phone, email, e);
            if (keycloakUserId != null) {
                keycloakAuthProvider.deleteUserInKeycloak(keycloakUserId);
            }
            String combinedMessage = ErrorMessageMapper.collectExceptionMessages(e);
            if (combinedMessage.contains("foreign key") || combinedMessage.contains("fk_")) {
                throw new ValidationException(parseForeignKeyViolation(combinedMessage));
            }
            throw ExceptionUtil.fromDataIntegrityViolation(e);
        }
    }

    @Override
    public RbacUserResponse updateUser(String id, RbacUserRequest request) {
        log.info("Starting RBAC user update: userId={}", id);
        if (request == null || id == null) {
            throw new ValidationException(RbacConstants.REQUEST_NULL);
        }
        RbacUser existing = userRepository.findByUserIdAndIsDeleted(id, RbacConstants.IS_DELETED_FALSE)
                .orElseThrow(() -> new ResourceNotFoundException(
                        RbacUtil.buildMessage(RbacConstants.USER_NOT_FOUND, id)));

        // Validate all foreign key references BEFORE attempting update (if they are being changed)
        validateUserForeignKeyReferences(request);

        if (RbacUtil.isNotBlank(request.getPhone())) {
            String phone = request.getPhone().trim();
            if (userRepository.existsByPhoneAndUserIdNot(phone, id)) {
                throw ExceptionUtil.duplicateConstraint("uq_nec_rbac_users_phone", phone);
            }
            request.setPhone(phone);
        }
        if (RbacUtil.isNotBlank(request.getEmail())) {
            String email = request.getEmail().trim();
            if (userRepository.existsByEmailIgnoreCaseAndUserIdNot(email, id)) {
                throw ExceptionUtil.duplicateConstraint("uq_nec_rbac_users_email", email);
            }
            request.setEmail(email);
        }

        try {
            userMapper.updateEntity(existing, request);
            RbacUser saved = userRepository.saveAndFlush(existing);

            // Sync profile changes to Keycloak (email, username)
            // DB change is already committed — a Keycloak failure must not roll it back.
            try {
                // Sync email and/or username if present in request
                if (RbacUtil.isNotBlank(request.getEmail()) || RbacUtil.isNotBlank(request.getUserName())) {
                    keycloakAuthProvider.updateUserProfile(
                            saved.getKeycloakUserId(),
                            request.getEmail(),
                            request.getUserName());
                }
            } catch (Exception ex) {
                // Include throwable so the stacktrace is available in logs for debugging
                log.warn("User {} profile updated locally but Keycloak sync failed (will retry on next login)",
                        saved.getKeycloakUserId(), ex);
            }

            log.info("RBAC user updated: userId={}", saved.getUserId());
            return mapUserWithAssociations(saved);
        } catch (DataIntegrityViolationException e) {
            log.warn("Data integrity violation during user update. UserId: {}", id, e);
            String combinedMessage = ErrorMessageMapper.collectExceptionMessages(e);
            if (combinedMessage.contains("foreign key") || combinedMessage.contains("fk_")) {
                throw new ValidationException(parseForeignKeyViolation(combinedMessage));
            }
            throw ExceptionUtil.fromDataIntegrityViolation(e);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public RbacUserResponse getUserById(String id) {
        RbacUser user = userRepository.findByUserIdAndIsDeletedWithAssociations(id, RbacConstants.IS_DELETED_FALSE)
                .orElseThrow(() -> new ResourceNotFoundException(
                        RbacUtil.buildMessage(RbacConstants.USER_NOT_FOUND, id)));
        return userMapper.toResponseDto(user);
    }

    @Override
    @Transactional(readOnly = true)
    public List<RbacUserResponse> getAllUsers() {
        return userRepository.findAllByIsDeletedWithAssociations(RbacConstants.IS_DELETED_FALSE)
                .stream()
                .map(userMapper::toResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public PaginatedResponse<RbacUserResponse> listUsers(UserListRequestDto request) {
        UserListRequestDto listRequest = request != null ? request : new UserListRequestDto();
        log.debug("Listing RBAC users: page={}, size={}", listRequest.getPage(), listRequest.getSize());
        Sort sort = RbacPaginationUtil.buildSort(
                listRequest.getSortBy(), listRequest.getSortDirection(), "createdAt");
        return fetchList(
                UserSpecification.build(listRequest),
                userRepository,
                userMapper::toResponseDto,
                listRequest.getPage(),
                listRequest.getSize(),
                sort);
    }

    @Override
    @Transactional(readOnly = true)
    public List<RbacUserResponse> getUsersByRoleId(Long roleId) {
        if (!RbacUtil.isValidId(roleId)) {
            throw new ValidationException("Role ID must be a positive number");
        }
        return userRepository.findAllByRoleIdAndIsDeletedWithAssociations(roleId, RbacConstants.IS_DELETED_FALSE)
                .stream()
                .map(userMapper::toResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    public RbacUserResponse changeUserActiveStatus(String id, Integer isActive) {
        log.info("Changing RBAC user status: userId={}, isActive={}", id, isActive);
        if (isActive == null) {
            throw new ValidationException(RbacConstants.USER_ACTIVE_STATUS_REQUIRED);
        }
        RbacUser user = userRepository.findByUserIdAndIsDeleted(id, RbacConstants.IS_DELETED_FALSE)
                .orElseThrow(() -> new ResourceNotFoundException(
                        RbacUtil.buildMessage(RbacConstants.USER_NOT_FOUND, id)));
        user.setIsActive(isActive);
        user.setStatus(isActive.equals(1) ? "ACTIVE" : "INACTIVE");
        RbacUser saved = userRepository.saveAndFlush(user);

        // Sync to Keycloak; DB change is already committed — a Keycloak failure must not roll it back.
        try {
            keycloakAuthProvider.setUserEnabled(saved.getKeycloakUserId(), isActive.equals(1));
        } catch (Exception ex) {
            // Include throwable so the stacktrace is available in logs for debugging
            log.warn("User {} status updated locally but Keycloak sync failed (will retry on next login)",
                    saved.getKeycloakUserId(), ex);
        }

        return mapUserWithAssociations(saved);
    }

    @Override
    public void deleteUser(String id) {
        log.info("Soft deleting RBAC user: userId={}", id);
        RbacUser user = userRepository.findByUserIdAndIsDeleted(id, RbacConstants.IS_DELETED_FALSE)
                .orElseThrow(() -> new ResourceNotFoundException(
                        RbacUtil.buildMessage(RbacConstants.USER_NOT_FOUND, id)));
        user.setIsDeleted(RbacConstants.IS_DELETED_TRUE);
        userRepository.save(user);

        // Soft-delete in Keycloak by disabling the user (keep user in Keycloak for audit/restore purposes)
        try {
            keycloakAuthProvider.setUserEnabled(user.getKeycloakUserId(), false);
        } catch (Exception ex) {
            // Include throwable so the stacktrace is available in logs for debugging
            log.warn("User {} soft-deleted locally but Keycloak disable failed (will retry on next login)",
                    user.getKeycloakUserId(), ex);
        }
        log.info("RBAC user soft-deleted: userId={}", id);
    }

    @Override
    public RbacUserResponse restoreUser(String id) {
        log.info("Restoring RBAC user: userId={}", id);
        // Find the soft-deleted user
        RbacUser user = userRepository.findByUserIdAndIsDeleted(id, RbacConstants.IS_DELETED_TRUE)
                .orElseThrow(() -> new ResourceNotFoundException(
                        RbacUtil.buildMessage(RbacConstants.USER_NOT_FOUND, id)));

        // Restore in database
        user.setIsDeleted(RbacConstants.IS_DELETED_FALSE);
        RbacUser saved = userRepository.saveAndFlush(user);

        // Re-enable in Keycloak to sync with database restoration
        try {
            keycloakAuthProvider.setUserEnabled(saved.getKeycloakUserId(), true);
        } catch (Exception ex) {
            // Include throwable so the stacktrace is available in logs for debugging
            log.warn("User {} restored locally but Keycloak enable failed (will retry on next login)",
                    saved.getKeycloakUserId(), ex);
        }

        log.info("RBAC user restored: userId={}", saved.getUserId());
        return mapUserWithAssociations(saved);
    }

    // Helper methods

    private KeycloakUserCreateRequest buildKeycloakUserCreateRequest(
            RbacUserRequest request, String email, String password) {
        String[] names = splitUserName(request.getUserName());
        boolean enabled = RbacConstants.IS_ACTIVE_TRUE.equals(request.getIsActive());
        return KeycloakUserCreateRequest.builder()
                .username(email)
                .email(email)
                .firstName(names[0])
                .lastName(names[1])
                .enabled(enabled)
                .emailVerified(true)
                .credentials(List.of(KeycloakCredentialRequest.password(password)))
                .build();
    }

    private String[] splitUserName(String userName) {
        String trimmed = userName.trim();
        int space = trimmed.indexOf(' ');
        if (space > 0) {
            return new String[]{trimmed.substring(0, space), trimmed.substring(space + 1).trim()};
        }
        // Keycloak rejects direct-grant login when lastName is blank ("Account is not fully set up").
        return new String[]{trimmed, trimmed};
    }

    private void validateUserForeignKeyReferences(RbacUserRequest request) {
        // Validate role exists if provided
        if (request.getRoleId() != null && RbacUtil.isValidId(request.getRoleId())) {
            if (!roleRepository.findByRoleIdAndIsDeleted(request.getRoleId(), RbacConstants.IS_DELETED_FALSE).isPresent()) {
                throw new ValidationException(
                        RbacUtil.buildMessage(RbacConstants.ROLE_NOT_FOUND, request.getRoleId()));
            }
        }
    }

    private String parseForeignKeyViolation(String errorMessage) {
        String lower = errorMessage.toLowerCase();

        if (lower.contains("nec_rbac_roles") || lower.contains("role_id")) {
            return "The specified Role ID does not exist. Please provide a valid Role ID.";
        } else if (lower.contains("nec_lkp_genders") || lower.contains("gender_id")) {
            return "The specified Gender ID does not exist. Please provide a valid Gender ID.";
        } else if (lower.contains("nec_lkp_departments") || lower.contains("department_id")) {
            return "The specified Department ID does not exist. Please provide a valid Department ID.";
        } else if (lower.contains("nec_regions") || lower.contains("region_id")) {
            return "The specified Region ID does not exist. Please provide a valid Region ID.";
        } else if (lower.contains("nec_districts") || lower.contains("district_id")) {
            return "The specified District ID does not exist. Please provide a valid District ID.";
        } else if (lower.contains("nec_cities") || lower.contains("city_id")) {
            return "The specified City ID does not exist. Please provide a valid City ID.";
        }

        return "User creation failed due to an invalid foreign key reference. Please ensure all referenced entities exist.";
    }

    private RbacUserResponse mapUserWithAssociations(RbacUser user) {
        String userId = user.getUserId();

        entityManager.flush();
        entityManager.clear();

        return userRepository.findByUserIdAndIsDeletedWithAssociations(userId, RbacConstants.IS_DELETED_FALSE)
                .map(userMapper::toResponseDto)
                .orElseThrow(() -> new ResourceNotFoundException(
                        RbacUtil.buildMessage(RbacConstants.USER_NOT_FOUND, userId)));
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

