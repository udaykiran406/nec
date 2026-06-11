package com.nec.middleware.masterdata.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "nec_political_parties",
        uniqueConstraints = @UniqueConstraint(
                name = "uq_party_name",
                columnNames = "party_name"
        )
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MasterDataPoliticalParty {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "party_name", nullable = false)
    private String partyName;

    @Column(name = "location", nullable = false)
    private String location;

    @Column(name = "status", nullable = false)
    @Builder.Default
    private String status = "Active";

    @Column(name = "created_by")
    private Long createdBy;

    @Column(name = "updated_by")
    private Long updatedBy;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Builder.Default
    @Column(name = "is_deleted")
    private Short isDeleted = 0;
}