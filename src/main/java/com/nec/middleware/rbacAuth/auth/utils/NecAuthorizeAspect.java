package com.nec.middleware.rbacAuth.auth.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nec.middleware.rbacAuth.auth.dto.response.KeycloakErrorResponse;
import com.nec.middleware.rbacAuth.auth.exception.AuthAspectException;
import com.nec.middleware.rbacAuth.auth.exception.ForbiddenException;
import com.nec.middleware.rbacAuth.auth.exception.KeycloakException;
import com.nec.middleware.rbacAuth.auth.exception.SomethingWentWrongException;
import com.nec.middleware.rbacAuth.auth.exception.UnauthorizedException;
import com.nec.middleware.rbacAuth.rbac.constant.RbacConstants;
import com.nec.middleware.rbacAuth.rbac.entity.RbacUser;
import com.nec.middleware.rbacAuth.keycloak.provider.KeycloakAuthProvider;
import com.nec.middleware.rbacAuth.rbac.repository.RbacRolePermissionRepository;
import com.nec.middleware.rbacAuth.rbac.repository.RbacUserRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * AOP aspect that enforces Keycloak token validation and optional RBAC role checks
 * on any method annotated with {@link Authorize}.
 *
 * <p>Flow:
 * <ol>
 *   <li>Extract Bearer token from {@code Authorization} header</li>
 *   <li>Introspect token via {@link KeycloakAuthProvider#validateTokenGetUid}</li>
 *   <li>Load local {@link RbacUser} by Keycloak user ID ({@code keycloak_user_id})</li>
 *   <li>Verify the user is active</li>
 *   <li>Check optional role constraints from {@link Authorize#roles()}</li>
 *   <li>Check optional permission constraints from {@link Authorize#permissions()}</li>
 *   <li>Populate {@link SecurityContextHolder} so {@link NecSecurityUtils} works downstream</li>
 * </ol>
 */
@Aspect
@Component
@RequiredArgsConstructor
@Slf4j
public class NecAuthorizeAspect {

    private static final String BEARER_PREFIX = "Bearer ";

    private final KeycloakAuthProvider keycloakAuthProvider;
    private final RbacUserRepository rbacUserRepository;
    private final RbacRolePermissionRepository rbacRolePermissionRepository;
    private final ObjectMapper objectMapper;

    @Around("@annotation(authorize)")
    public Object authorize(ProceedingJoinPoint joinPoint, Authorize authorize) throws Throwable {
        try {
            HttpServletRequest request = ((ServletRequestAttributes)
                    RequestContextHolder.currentRequestAttributes()).getRequest();

            String token = extractBearerToken(request);
            log.info("Validating token for protected endpoint");

            // 1. Validate token with Keycloak → get Keycloak userId (sub claim)
            String keycloakUserId = keycloakAuthProvider.validateTokenGetUid(token);

            // 2. Load local user by Keycloak subject (userId and keycloakUserId store the same UUID)
            Optional<RbacUser> userOpt = rbacUserRepository
                    .findByKeycloakUserIdAndIsDeletedWithRole(keycloakUserId, RbacConstants.IS_DELETED_FALSE);

            if (userOpt.isEmpty()) {
                throw new UnauthorizedException("Unauthorized");
            }

            RbacUser user = userOpt.get();

            // 3. Block inactive / soft-deleted users
            if (!RbacConstants.IS_ACTIVE_TRUE.equals(user.getIsActive())) {
                throw new ForbiddenException("USER_INACTIVE");
            }

            // 4. Role check (only when roles are specified on the annotation)
            String[] requiredRoles = authorize.roles();
            if (requiredRoles.length > 0) {
                if (user.getRole() == null) {
                    throw new ForbiddenException("User does not have the required role(s)");
                }

                String roleName = user.getRole().getRoleName();
                Set<String> userRoles = Set.of(roleName);
                boolean hasRole = Arrays.stream(requiredRoles).anyMatch(userRoles::contains);

                if (!hasRole) {
                    throw new ForbiddenException("User does not have the required role(s)");
                }
            }

            // 5. Permission check (only when permissions are specified on the annotation)
            String[] requiredPermissions = authorize.permissions();
            if (requiredPermissions.length > 0) {
                if (user.getRoleId() == null) {
                    throw new ForbiddenException("Insufficient permissions");
                }

                boolean hasPermission = rbacRolePermissionRepository.existsByRoleIdAndStatusAndPermissionCodeIn(
                        user.getRoleId(),
                        RbacConstants.STATUS_ACTIVE,
                        List.of(requiredPermissions));

                if (!hasPermission) {
                    throw new ForbiddenException("Insufficient permissions");
                }
            }

            // 6. Populate SecurityContext so NecSecurityUtils.getCurrentUser() works
            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(user, null);
            SecurityContextHolder.getContext().setAuthentication(authentication);

            return joinPoint.proceed();

        } catch (UnauthorizedException ex) {
            throw ex;
        } catch (ForbiddenException ex) {
            throw ex;
        } catch (Exception e) {
            Throwable cause = e.getCause() != null ? e.getCause() : e;
            if (cause instanceof HttpClientErrorException httpEx) {
                String body = httpEx.getResponseBodyAsString();
                if (!isJsonResponse(httpEx, body)) {
                    log.error("Keycloak returned non-JSON error response: status={}", httpEx.getStatusCode());
                    throw new SomethingWentWrongException("authorize():aspect", "Keycloak service unavailable");
                }
                try {
                    KeycloakErrorResponse err = objectMapper.readValue(body, KeycloakErrorResponse.class);
                    if (err.getErrorDescription() == null || err.getErrorDescription().isEmpty()) {
                        err.setErrorDescription(body);
                    }
                    err.setStatus(httpEx.getStatusCode().value());
                    throw new KeycloakException(e.getMessage(), err);
                } catch (JsonProcessingException parseEx) {
                    log.error("Failed to parse Keycloak error response", parseEx);
                    throw new SomethingWentWrongException("authorize():aspect", "Keycloak service unavailable");
                }
            }
            if (cause instanceof Exception exception) {
                throw new AuthAspectException(e.getMessage(), exception.getMessage());
            }
            throw new SomethingWentWrongException("authorize():aspect " + e.getMessage(), e.getMessage());
        }
    }

    private String extractBearerToken(HttpServletRequest request) throws UnauthorizedException {
        String authorization = request.getHeader("Authorization");
        if (authorization == null || authorization.isBlank()) {
            throw new UnauthorizedException("Missing or invalid Bearer token");
        }
        if (!authorization.startsWith(BEARER_PREFIX)) {
            throw new UnauthorizedException("Invalid authorization scheme");
        }
        String token = authorization.substring(BEARER_PREFIX.length()).trim();
        if (token.isEmpty()) {
            throw new UnauthorizedException("Missing or invalid Bearer token");
        }
        return token;
    }

    private boolean isJsonResponse(HttpClientErrorException httpEx, String body) {
        if (!StringUtils.hasText(body)) {
            return false;
        }
        MediaType contentType = httpEx.getResponseHeaders() != null
                ? httpEx.getResponseHeaders().getContentType()
                : null;
        if (contentType != null && MediaType.APPLICATION_JSON.isCompatibleWith(contentType)) {
            return true;
        }
        String trimmed = body.trim();
        return trimmed.startsWith("{") || trimmed.startsWith("[");
    }
}
