package com.nec.middleware.Lookups.entity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "nec_lkp_genders")
@Getter @Setter @Builder
@NoArgsConstructor
public class Genders extends BaseLookupEntity {}