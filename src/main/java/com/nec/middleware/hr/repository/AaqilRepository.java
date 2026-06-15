package com.nec.middleware.hr.repository;

import com.nec.middleware.hr.entity.Aaqil;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AaqilRepository extends JpaRepository<Aaqil, Long>,
        JpaSpecificationExecutor<Aaqil> {



    Optional<Aaqil> findByAaqilId(String aaqilId);

    // Duplicate checks (CREATE)
    boolean existsByEmail(String email);

    boolean existsByPhone(String phone);

    // Duplicate checks (UPDATE)
    boolean existsByEmailAndIdNot(
            String email,
            Long id
    );

    boolean existsByPhoneAndIdNot(
            String phone,
            Long id
    );


    // Code generation helper
    @Query("""
            SELECT COALESCE(
                MAX(CAST(SUBSTRING(a.aaqilId, 3) AS int)), 0
            )
            FROM Aaqil a
            WHERE a.aaqilId LIKE 'AA%'
            """)
    int findMaxCodeSequence();
}