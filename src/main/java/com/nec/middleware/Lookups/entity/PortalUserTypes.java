package com.nec.middleware.Lookups.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "nec_lkp_portal_user_types", schema = "NEC")
@Getter @Setter @Builder
@NoArgsConstructor
public class PortalUserTypes extends BaseLookupEntity {}