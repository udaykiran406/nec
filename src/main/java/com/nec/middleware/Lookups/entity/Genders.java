package com.nec.middleware.Lookups.entity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "nec_lkp_genders", schema = "NEC")
@Getter @Setter @Builder
@NoArgsConstructor
public class Genders extends BaseLookupEntity {}