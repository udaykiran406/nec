package com.nec.middleware.masterdata.repository;

import com.nec.middleware.masterdata.entity.MasterDataUniversity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface UniversityRepository extends JpaRepository<MasterDataUniversity, Long> {

    Optional<MasterDataUniversity> findByIdAndIsDeleted(Long id, Short isDeleted);

    List<MasterDataUniversity> findAllByIsDeletedOrderByUniversityNameAsc(Short isDeleted);

    boolean existsByUniversityNameIgnoreCaseAndIsDeleted(
            String universityName,
            Short isDeleted
    );

    boolean existsByUniversityNameIgnoreCaseAndIsDeletedAndIdNot(
            String universityName,
            Short isDeleted,
            Long id
    );

    Page<MasterDataUniversity> findAllByIsDeletedOrderByUniversityNameAsc(Short isDeleted, Pageable pageable);
}