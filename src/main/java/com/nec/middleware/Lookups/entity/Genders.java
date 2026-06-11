package com.nec.middleware.Lookups.entity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "nec_lkp_genders")
@Getter @Setter @Builder
public class Genders extends BaseLookupEntity {}