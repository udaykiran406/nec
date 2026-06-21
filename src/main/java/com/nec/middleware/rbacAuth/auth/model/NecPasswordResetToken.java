package com.nec.middleware.rbacAuth.auth.model;

import com.nec.middleware.rbacAuth.rbac.entity.RbacUser;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@Entity
@Table(name = "nec_password_reset_tokens", schema = "keycloak")
public class NecPasswordResetToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "token", nullable = false, unique = true, length = 100)
    private String token;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private RbacUser user;

    @Column(name = "expires_at", nullable = false)
    private LocalDateTime expiresAt;

    @Column(name = "used", nullable = false)
    private boolean used = false;

    public static NecPasswordResetToken create(RbacUser user, long minutesValid) {
        NecPasswordResetToken t = new NecPasswordResetToken();
        t.setToken(UUID.randomUUID().toString());
        t.setUser(user);
        t.setExpiresAt(LocalDateTime.now().plusMinutes(minutesValid));
        t.setUsed(false);
        return t;
    }

    public boolean isExpired() {
        return LocalDateTime.now().isAfter(expiresAt);
    }
}
