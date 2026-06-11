package com.nec.middleware.masterdata.repository;

import com.nec.middleware.masterdata.entity.MasterDataPoliticalParty;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface PoliticalPartyRepository
        extends JpaRepository<MasterDataPoliticalParty, Long> {

    Optional<MasterDataPoliticalParty> findByIdAndIsDeleted(
            Long id,
            Short isDeleted
    );

    List<MasterDataPoliticalParty>
    findAllByIsDeletedOrderByPartyNameAsc(
            Short isDeleted
    );

    boolean existsByPartyNameIgnoreCaseAndIsDeleted(
            String partyName,
            Short isDeleted
    );

    boolean existsByPartyNameIgnoreCaseAndIsDeletedAndIdNot(
            String partyName,
            Short isDeleted,
            Long id
    );

    Page<MasterDataPoliticalParty> findAllByIsDeletedOrderByPartyNameAsc(Short isDeleted, Pageable pageable);
}