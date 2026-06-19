package com.nec.middleware.hr.specification;

import com.nec.middleware.hr.dto.request.PoliticalPartyAgentFilterRequestDto;
import com.nec.middleware.hr.entity.PoliticalPartyAgent;
import org.springframework.data.jpa.domain.Specification;
import java.util.ArrayList;
import java.util.List;
import jakarta.persistence.criteria.Predicate;


public class PoliticalPartyAgentSearchSpecification {

    private PoliticalPartyAgentSearchSpecification() {
    }

    public static Specification<PoliticalPartyAgent> buildSpecification(
            PoliticalPartyAgentFilterRequestDto filterRequest) {

        return (entityRoot, criteriaQuery, criteriaBuilder) -> {

            List<Predicate> filterPredicates = new ArrayList<>();

            // Political Party Agent UserId
            if (filterRequest.getPoliticalPartyAgentUserId() != null
                    && !filterRequest.getPoliticalPartyAgentUserId().isBlank()) {

                filterPredicates.add(
                        criteriaBuilder.like(
                                criteriaBuilder.lower(
                                        entityRoot.get("politicalPartyAgentUserId")),
                                "%" + filterRequest.getPoliticalPartyAgentUserId()
                                        .toLowerCase() + "%"
                        )
                );
            }

            // Agent Name
            if (filterRequest.getAgentName() != null
                    && !filterRequest.getAgentName().isBlank()) {

                filterPredicates.add(
                        criteriaBuilder.like(
                                criteriaBuilder.lower(
                                        entityRoot.get("agentName")),
                                "%" + filterRequest.getAgentName()
                                        .toLowerCase() + "%"
                        )
                );
            }

            // Phone
            if (filterRequest.getPhone() != null
                    && !filterRequest.getPhone().isBlank()) {

                filterPredicates.add(
                        criteriaBuilder.like(
                                entityRoot.get("phone"),
                                "%" + filterRequest.getPhone() + "%"
                        )
                );
            }

            // Email
            if (filterRequest.getEmail() != null
                    && !filterRequest.getEmail().isBlank()) {

                filterPredicates.add(
                        criteriaBuilder.like(
                                criteriaBuilder.lower(
                                        entityRoot.get("email")),
                                "%" + filterRequest.getEmail()
                                        .toLowerCase() + "%"
                        )
                );
            }

            // Gender
            if (filterRequest.getGenderId() != null) {
                filterPredicates.add(
                        criteriaBuilder.equal(
                                entityRoot.get("gender").get("id"),
                                filterRequest.getGenderId()
                        )
                );
            }

            // Political Party
            if (filterRequest.getPoliticalPartyNameId() != null) {
                filterPredicates.add(
                        criteriaBuilder.equal(
                                entityRoot.get("politicalPartyName").get("id"),
                                filterRequest.getPoliticalPartyNameId()
                        )
                );
            }

            // Polling Station
            if (filterRequest.getPollingStationId() != null) {
                filterPredicates.add(
                        criteriaBuilder.equal(
                                entityRoot.get("pollingStation").get("id"),
                                filterRequest.getPollingStationId()
                        )
                );
            }

            // Region
            if (filterRequest.getRegionId() != null) {
                filterPredicates.add(
                        criteriaBuilder.equal(
                                entityRoot.get("region").get("id"),
                                filterRequest.getRegionId()
                        )
                );
            }

            // District
            if (filterRequest.getDistrictId() != null) {
                filterPredicates.add(
                        criteriaBuilder.equal(
                                entityRoot.get("district").get("id"),
                                filterRequest.getDistrictId()
                        )
                );
            }

            // City
            if (filterRequest.getCityId() != null) {
                filterPredicates.add(
                        criteriaBuilder.equal(
                                entityRoot.get("city").get("id"),
                                filterRequest.getCityId()
                        )
                );
            }

            // Status
            if (filterRequest.getStatusId() != null) {
                filterPredicates.add(
                        criteriaBuilder.equal(
                                entityRoot.get("status").get("id"),
                                filterRequest.getStatusId()
                        )
                );
            }

            // Active Flag
            if (filterRequest.getIsActive() != null) {
                filterPredicates.add(
                        criteriaBuilder.equal(
                                entityRoot.get("isActive"),
                                filterRequest.getIsActive()
                        )
                );
            }

            return criteriaBuilder.and(
                    filterPredicates.toArray(new Predicate[0])
            );
        };
    }
}