package com.nec.middleware.Lookups.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "nec_lkp_payment_methods")
@Getter
@Setter
@Builder
public class PaymentMethods extends BaseLookupEntity{
}
