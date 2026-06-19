package com.nec.middleware.masterdata.repository;

import com.nec.middleware.masterdata.entity.MasterDataHrTrainerTot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface HrTrainerTotRepository
        extends JpaRepository<MasterDataHrTrainerTot, Long> {

    Optional<MasterDataHrTrainerTot> findByIdAndIsDeleted(
            Long id,
            Boolean isDeleted
    );

    List<MasterDataHrTrainerTot>
    findAllByIsDeletedOrderByFullNameAsc(
            Boolean isDeleted
    );

    boolean existsByCodeIgnoreCaseAndIsDeleted(
            String code,
            Boolean isDeleted
    );

    boolean existsByCodeIgnoreCaseAndIsDeletedAndIdNot(
            String code,
            Boolean isDeleted,
            Long id
    );

    Page<MasterDataHrTrainerTot> findAllByIsDeletedOrderByFullNameAsc(Boolean isDeleted, Pageable pageable);
}