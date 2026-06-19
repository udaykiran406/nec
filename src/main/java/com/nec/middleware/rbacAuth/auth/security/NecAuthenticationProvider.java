package com.nec.middleware.rbacAuth.auth.security;

import com.nec.middleware.rbacAuth.auth.constants.AuthServiceConstants;
import com.nec.middleware.rbacAuth.auth.exception.InvalidCredentialsException;
import com.nec.middleware.rbacAuth.keycloak.client.response.KeycloakTokenResponse;
import com.nec.middleware.rbacAuth.keycloak.provider.KeycloakAuthProvider;
import com.nec.middleware.rbacAuth.rbac.constant.RbacConstants;
import com.nec.middleware.rbacAuth.rbac.entity.RbacUser;
import com.nec.middleware.rbacAuth.rbac.repository.RbacUserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Validates NEC users locally, then delegates password verification to Keycloak
 * before any access token is returned to the caller.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class NecAuthenticationProvider implements AuthenticationProvider {

    private final RbacUserRepository rbacUserRepository;
    private final KeycloakAuthProvider keycloakAuthProvider;
    private final PasswordEncoder passwordEncoder;

     @Override
     public Authentication authenticate(Authentication authentication) throws AuthenticationException {
         String usernameOrEmail = authentication.getName();
         String password = authentication.getCredentials() != null
                 ? authentication.getCredentials().toString()
                 : null;

         log.debug("NecAuthenticationProvider.authenticate() called for: {}", usernameOrEmail);
         log.debug("Password provided: {}, length: {}", password != null, password != null ? password.length() : 0);

         if (!org.springframework.util.StringUtils.hasText(password)) { // SECURITY FIX: Reject blank or null passwords immediately
             log.warn("Login rejected — blank or null password supplied for: {}", usernameOrEmail);
             throw new BadCredentialsException(AuthServiceConstants.INVALID_CREDENTIALS);
         }

         RbacUser user = rbacUserRepository
                 .findByEmailIgnoreCaseAndIsDeleted(usernameOrEmail, RbacConstants.IS_DELETED_FALSE)
                 .orElseThrow(() -> {
                     log.warn("Login failed — user not found: {}", usernameOrEmail);
                     return new BadCredentialsException(AuthServiceConstants.INVALID_CREDENTIALS);
                 });

         if (!RbacConstants.IS_ACTIVE_TRUE.equals(user.getIsActive())) {
             log.warn("Login failed — inactive user: {}", user.getEmail());
             throw new DisabledException("USER_INACTIVE");
         }

         if (!RbacConstants.IS_ACTIVE_TRUE.equals(user.getIsActive())) {
             log.warn("Login failed — inactive user: {}", user.getEmail());
             throw new DisabledException("USER_INACTIVE");
         }

         if (org.springframework.util.StringUtils.hasText(user.getPasswordHash())
                 && !passwordEncoder.matches(password, user.getPasswordHash())) {
             log.warn("Login failed — password mismatch for user: {}", user.getEmail());
             throw new BadCredentialsException(AuthServiceConstants.INVALID_CREDENTIALS);
         }

         log.debug("User found and is active: {}. Delegating to Keycloak for token issuance.", user.getEmail());

         KeycloakTokenResponse keycloakTokenResponse;
         try {
             keycloakTokenResponse = keycloakAuthProvider.authenticateAndGetToken(user.getEmail(), password);
             log.debug("Keycloak returned token response for user: {}", user.getEmail());
         } catch (InvalidCredentialsException ex) {
             log.warn("Login failed — invalid credentials for user: {}", user.getEmail());
             throw new BadCredentialsException(AuthServiceConstants.INVALID_CREDENTIALS);
         }

         log.info("Authentication successful for user: {}", user.getEmail());

         return new KeycloakAuthenticatedToken(
                 user.getEmail(),
                 null,
                 List.of(new SimpleGrantedAuthority("ROLE_USER")),
                 keycloakTokenResponse);
     }

    @Override
    public boolean supports(Class<?> authentication) {
        return UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication);
    }
}
