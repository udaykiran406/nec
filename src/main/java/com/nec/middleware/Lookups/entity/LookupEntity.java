package com.nec.middleware.Lookups.entity;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "nec_lkp_lookups")
public class LookupEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "table_name", nullable = false, unique = true, length = 100)
    private String tableName;

    @Column(name = "is_active", nullable = false)
    private Integer isActive;
}