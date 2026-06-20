package com.nec.middleware.rbacAuth.auth.job;

import com.nec.middleware.rbacAuth.auth.repository.NecPasswordResetTokenRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * Scheduled job for cleaning up expired and used password reset tokens.
 * Runs daily at 2:00 AM.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class PasswordResetTokenCleanupJob {

    private final NecPasswordResetTokenRepository passwordResetTokenRepository;

    /**
     * Deletes expired and already-used password reset tokens.
     * Scheduled to run daily at 2:00 AM.
     */
    @Scheduled(cron = "0 0 2 * * *")
    public void cleanupExpiredTokens() {
        try {
            log.info("Starting password reset token cleanup job");
            passwordResetTokenRepository.deleteExpiredAndUsedTokens(LocalDateTime.now());
            log.info("Password reset token cleanup job completed successfully");
        } catch (Exception ex) {
            log.error("Error during password reset token cleanup: {}", ex.getMessage(), ex);
        }
    }
}

