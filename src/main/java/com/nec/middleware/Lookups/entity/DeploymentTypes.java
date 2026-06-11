package com.nec.middleware.Lookups.entity;


import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "nec_lkp_deployment_types")
@Setter
@Getter
@Builder
public class DeploymentTypes extends BaseLookupEntity{
}
