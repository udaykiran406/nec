package com.nec.middleware.idGenerator.entity;

import com.nec.middleware.idGenerator.Enum.ModuleCode;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "id_generator", schema = "NEC")
@Getter
@Setter
public class IdGenerator {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

   @Enumerated(EnumType.STRING)
   @Column(name="module_code", nullable=false)
    private ModuleCode moduleCode;

    @Column(name = "last_number", nullable = false)
    private Long lastNumber;
}