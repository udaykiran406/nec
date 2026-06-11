package com.nec.middleware.Lookups.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "nec_lkp_roles")
@Getter @Setter @Builder
public class Roles extends BaseLookupEntity {}