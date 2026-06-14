package com.nec.middleware.idGenerator.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "id_generator")
@Getter
@Setter
public class IdGenerator {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "module_code", nullable = false, unique = true)
    private String moduleCode;

    @Column(name = "last_number", nullable = false)
    private Long lastNumber;
}