package com.nec.middleware.config;

import com.nec.middleware.rbacAuth.auth.utils.NecSecurityUtils;
import com.nec.middleware.rbacAuth.rbac.entity.RbacUser;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.util.StringUtils;

import java.util.Optional;

/**
 * Wires Spring Data JPA's {@code @CreatedBy}/{@code @LastModifiedBy} auditing
 * (see {@code AuditableEntity}) to the currently authenticated user.
 *
 * <p>The {@link NecSecurityUtils#getCurrentUserOrNull()} call reads the
 * {@link RbacUser} that {@code NecAuthorizeAspect} placed into the
 * {@code SecurityContextHolder} while validating the caller's Keycloak token.
 * That means {@code created_by} / {@code updated_by} columns end up holding the
 * real logged-in user's name (e.g. the HR Officer who is creating a Portal User),
 * not a static placeholder.
 *
 * <p>IMPORTANT: this only works for controller methods annotated with
 * {@code @Authorize} — that annotation is what populates the SecurityContext in
 * the first place. Endpoints without {@code @Authorize} will fall back to
 * {@code "SYSTEM"} below since no authenticated principal is available.
 */
@Configuration
@EnableJpaAuditing(auditorAwareRef = "auditorProvider")
public class JpaAuditConfig {

    private static final String SYSTEM_AUDITOR = "SYSTEM";

    @Bean
    public AuditorAware<String> auditorProvider() {
        return () -> {
            RbacUser currentUser = NecSecurityUtils.getCurrentUserOrNull();
            if (currentUser != null && StringUtils.hasText(currentUser.getUserName())) {
                return Optional.of(currentUser.getUserName());
            }
            return Optional.of(SYSTEM_AUDITOR);
        };
    }
}