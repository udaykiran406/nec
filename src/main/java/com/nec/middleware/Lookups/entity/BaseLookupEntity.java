package com.nec.middleware.Lookups.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@MappedSuperclass
public abstract class BaseLookupEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 50)
    private String code;

    @Column(nullable = false, length = 100)
    private String value;

    private String description;

    private Short isActive;

    private Integer displayOrder;

    private Short isDeleted;

    private String createdBy;

    private String updatedBy;

    private LocalDateTime createdDt;

    private LocalDateTime updatedDt;
}