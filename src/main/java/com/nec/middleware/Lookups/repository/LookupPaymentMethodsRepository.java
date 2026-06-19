package com.nec.middleware.Lookups.repository;

import com.nec.middleware.Lookups.entity.PaymentMethods;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LookupPaymentMethodsRepository extends JpaRepository<PaymentMethods, Long> {
}
