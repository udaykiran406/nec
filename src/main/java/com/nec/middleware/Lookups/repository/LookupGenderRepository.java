package com.nec.middleware.Lookups.repository;

import com.nec.middleware.Lookups.entity.Genders;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LookupGenderRepository
        extends JpaRepository<Genders, Long> {
}