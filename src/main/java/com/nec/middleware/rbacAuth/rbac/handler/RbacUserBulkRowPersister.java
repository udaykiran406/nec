package com.nec.middleware.rbacAuth.rbac.handler;

import com.nec.middleware.exception.DuplicateResourceException;
import com.nec.middleware.rbacAuth.rbac.constant.RbacConstants;
import com.nec.middleware.rbacAuth.rbac.dto.request.RbacUserBulkUploadDto;
import com.nec.middleware.rbacAuth.rbac.dto.response.RbacUserResponse;
import com.nec.middleware.rbacAuth.rbac.entity.RbacUser;
import com.nec.middleware.rbacAuth.rbac.mapper.RbacUserMapper;
import com.nec.middleware.rbacAuth.rbac.repository.RbacRoleRepository;
import com.nec.middleware.rbacAuth.rbac.repository.RbacUserRepository;
import com.nec.middleware.rbacAuth.rbac.service.BulkUploadEmailService;
import com.nec.middleware.rbacAuth.rbac.util.SecurePasswordGenerator;
import com.nec.middleware.rbacAuth.keycloak.client.request.KeycloakCredentialRequest;
import com.nec.middleware.rbacAuth.keycloak.client.request.KeycloakUserCreateRequest;
import com.nec.middleware.rbacAuth.keycloak.provider.KeycloakAuthProvider;
import com.nec.middleware.exception.ValidationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class RbacUserBulkRowPersister {

    private final RbacUserRepository userRepository;
    private final RbacUserMapper userMapper;
    private final RbacRoleRepository roleRepository;
    private final KeycloakAuthProvider keycloakAuthProvider;
    private final PasswordEncoder passwordEncoder;
    private final BulkUploadEmailService emailService;
    private final SecurePasswordGenerator passwordGenerator;

    /**
     * Validate, create Keycloak user with secure password, save to database, and send
     * credentials via email in its own independent transaction.
     *
     * <p>Process:
     * 1. Validate business rules (duplicate email/phone, FK existence)
     * 2. Generate secure password (12+ characters with complexity requirements)
     * 3. Create user in Keycloak with temporary password (user must change on login)
     * 4. Save user to database with password hash
     * 5. Send credentials via email (graceful failure handling)
     *
     * <p>Email failures are logged and reported to support team but don't block the
     * user creation or cause transaction rollback. User is already created and can
     * use password reset to recover access.
     *
     * @throws DuplicateResourceException if email/phone already exists
     * @throws ValidationException if FK does not exist or business rules violated
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public RbacUserResponse persistSingleRow(RbacUserBulkUploadDto dto) {
        log.debug("Persisting RbacUser bulk row: userName='{}'", dto.getUserName());

        // Validate business rules before touching Keycloak
        validateUserData(dto);

        // Generate secure password before setting it in Keycloak
        String generatedPassword = passwordGenerator.generateSecurePassword();
        log.debug("Generated password for bulk upload user email={}: {}", dto.getEmail(), generatedPassword);

        String keycloakUserId = null;
        try {
            // Create user in Keycloak with secure password marked as temporary
            KeycloakUserCreateRequest kcRequest = buildKeycloakUserCreateRequest(dto, generatedPassword);
            keycloakUserId = keycloakAuthProvider.createUserInKeycloak(kcRequest);
            log.debug("Keycloak user creation successful: email={}, keycloakUserId={}", dto.getEmail(), keycloakUserId);

            // Map bulk DTO to entity
            RbacUser entity = mapDtoToEntity(dto, keycloakUserId);

            // Hash and store the password in local database
            entity.setPasswordHash(passwordEncoder.encode(generatedPassword));

            // Force password change on first login for bulk-uploaded users (temporary password)
            entity.setPasswordToBeChanged(true);

            // Save entity to database
            RbacUser saved = userRepository.save(entity);
            log.info("RbacUser bulk row persisted: userId='{}', userName='{}', email='{}'",
                    saved.getUserId(), saved.getUserName(), saved.getEmail());

            // Send the same generated password via email (non-blocking, failures handled gracefully)
            sendCredentialsEmail(saved, generatedPassword);

            // Fetch with associations and return response
            return userRepository.findByUserIdAndIsDeletedWithAssociations(
                    saved.getUserId(), RbacConstants.IS_DELETED_FALSE)
                    .map(userMapper::toResponseDto)
                    .orElseThrow(() -> new ValidationException("User could not be retrieved after persistence"));

        } catch (DataIntegrityViolationException e) {
            log.warn("Data integrity violation during RbacUser bulk persistence. Email: {}, Phone: {}",
                    dto.getEmail(), dto.getPhone(), e);
            if (keycloakUserId != null) {
                try {
                    keycloakAuthProvider.deleteUserInKeycloak(keycloakUserId);
                    log.info("Rolled back Keycloak user creation for userId: {}", keycloakUserId);
                } catch (Exception ex) {
                    log.error("Failed to rollback Keycloak user during database error", ex);
                }
            }
            throw e;
        }
    }

    /**
     * Sends credentials email to the user after successful creation.
     * Failures are logged but don't cause transaction rollback.
     *
     * @param user the created user entity
     * @param password the temporary password
     */
    private void sendCredentialsEmail(RbacUser user, String generatedPassword) {
        log.debug("Email send triggered for user email={}, username={}", user.getEmail(), user.getEmail());
        try {
            boolean emailSent = emailService.sendUserCredentials(
                    user.getEmail(),
                    user.getEmail(),
                    user.getUserName(),
                    generatedPassword
            );

            if (!emailSent) {
                log.warn("Email send failure for user email={}. Notifying support.", user.getEmail());
                emailService.sendEmailFailureAlert(
                        user.getEmail(),
                        user.getUserName(),
                        "Credentials email delivery failed after bulk upload"
                );
                log.warn("Credentials email delivery failed for user: {}. Support team notified.", user.getEmail());
            }
        } catch (Exception e) {
            log.error("Unexpected error sending credentials email for user: {}", user.getEmail(), e);
            try {
                emailService.sendEmailFailureAlert(
                        user.getEmail(),
                        user.getUserName(),
                        e.getMessage()
                );
            } catch (Exception alertEx) {
                log.error("Failed to send email failure alert", alertEx);
            }
        }
    }

    /**
     * Validate business rules: duplicate checks and FK existence.
     *
     * @throws DuplicateResourceException if email or phone already exists
     * @throws ValidationException if any FK does not exist
     */
    private void validateUserData(RbacUserBulkUploadDto dto) {
        String email = dto.getEmail().trim();
        String phone = dto.getPhone().trim();

        // Check for duplicate email
        if (userRepository.existsByEmailIgnoreCaseAndIsDeleted(email, RbacConstants.IS_DELETED_FALSE)) {
            throw new DuplicateResourceException(
                    "User with email '" + email + "' already exists");
        }

        // Check for duplicate phone
        if (userRepository.existsByPhoneAndIsDeleted(phone, RbacConstants.IS_DELETED_FALSE)) {
            throw new DuplicateResourceException(
                    "User with phone '" + phone + "' already exists");
        }

        // Validate role exists
        if (dto.getRoleId() != null) {
            if (!roleRepository.findByRoleIdAndIsDeleted(
                    dto.getRoleId(), RbacConstants.IS_DELETED_FALSE).isPresent()) {
                throw new ValidationException(
                        "The specified Role ID (" + dto.getRoleId() + ") does not exist");
            }
        }

        log.debug("User data validation passed for: {}", email);
    }

    /**
     * Build a Keycloak user creation request with the secure password.
     *
     * <p>CRITICAL: The {@code enabled} flag MUST match the {@code isActive}
     * value from the DTO/Excel to keep database and Keycloak in sync.
     * The password is marked as temporary (temporary=true) so Keycloak forces
     * the user to change it on first successful login.
     */
    private KeycloakUserCreateRequest buildKeycloakUserCreateRequest(
            RbacUserBulkUploadDto dto, String securePassword) {
        String[] names = splitUserName(dto.getUserName());

        // Properly handle null isActive. If null, default to IS_ACTIVE_TRUE (1)
        Integer activeStatus = dto.getIsActive() != null ? dto.getIsActive() : RbacConstants.IS_ACTIVE_TRUE;
        boolean enabled = RbacConstants.IS_ACTIVE_TRUE.equals(activeStatus);

        // CredentialRepresentation equivalent: type=password, value=generatedPassword, temporary=true
        KeycloakCredentialRequest credential = new KeycloakCredentialRequest("password", securePassword, true);
        log.debug("Keycloak credential prepared with generated password for email={}", dto.getEmail());

        return KeycloakUserCreateRequest.builder()
                .username(dto.getEmail())
                .email(dto.getEmail())
                .firstName(names[0])
                .lastName(names[1])
                .enabled(enabled)  // Now correctly set to true if isActive=1, false if isActive=0
                .emailVerified(true)
                .credentials(List.of(credential))
                .build();
    }

    /**
     * Map bulk DTO to RbacUser entity. Note: this differs from UserServiceImpl
     * in that userId is supplied (from Keycloak) rather than null.
     *
     * <p>CRITICAL: Ensures isActive and status stay in sync:
     * - isActive=1 (true) → status="ACTIVE"
     * - isActive=0 (false) → status="INACTIVE"
     */
    private RbacUser mapDtoToEntity(RbacUserBulkUploadDto dto, String keycloakUserId) {
        Integer activeStatus = dto.getIsActive() != null ? dto.getIsActive() : RbacConstants.IS_ACTIVE_TRUE;
        String status = RbacConstants.IS_ACTIVE_TRUE.equals(activeStatus) ? "ACTIVE" : "INACTIVE";

        return RbacUser.builder()
                .userId(keycloakUserId)
                .keycloakUserId(keycloakUserId)
                .userName(dto.getUserName().trim())
                .genderId(dto.getGenderId())
                .roleId(dto.getRoleId())
                .phone(dto.getPhone().trim())
                .email(dto.getEmail().trim())
                .photoPath(dto.getPhotoPath())
                .departmentId(dto.getDepartmentId())
                .regionId(dto.getRegionId())
                .districtId(dto.getDistrictId())
                .cityId(dto.getCityId())
                .isActive(activeStatus)
                .passwordToBeChanged(dto.getPasswordToBeChanged() != null
                        ? dto.getPasswordToBeChanged() : true)
                .emailVerified(true)
                .mobileVerified(false)
                .failedLoginAttempts(0)
                .status(status)  // Now synced with isActive
                .createdBy(1L) // Bulk upload system user — adjust as needed
                .isDeleted(RbacConstants.IS_DELETED_FALSE)
                .build();
    }


    /**
     * Split a full name into first and last name components.
     * If no space is found, use the entire name for both components
     * (Keycloak requires lastName to not be blank for direct-grant login).
     */
    private String[] splitUserName(String userName) {
        String trimmed = userName.trim();
        int space = trimmed.indexOf(' ');
        if (space > 0) {
            return new String[]{trimmed.substring(0, space), trimmed.substring(space + 1).trim()};
        }
        return new String[]{trimmed, trimmed};
    }
}

