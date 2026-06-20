package com.nec.middleware.config;

import com.nec.middleware.rbacAuth.auth.utils.NecSecurityUtils;
import com.nec.middleware.rbacAuth.rbac.entity.RbacUser;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.util.StringUtils;

import java.util.Optional;


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