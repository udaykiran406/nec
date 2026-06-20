package com.nec.middleware.rbacAuth.auth.repository;

import com.nec.middleware.rbacAuth.auth.model.NecPasswordResetToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface NecPasswordResetTokenRepository extends JpaRepository<NecPasswordResetToken, Long> {

    Optional<NecPasswordResetToken> findByToken(String token);

    @Modifying
    @Query("UPDATE NecPasswordResetToken t SET t.used = true WHERE t.user.userId = :userId AND t.used = false")
    void invalidateExistingTokensForUser(@Param("userId") String userId);

    @Modifying
    @Query("DELETE FROM NecPasswordResetToken t WHERE t.expiresAt < :now OR t.used = true")
    void deleteExpiredAndUsedTokens(@Param("now") LocalDateTime now);
}
