package com.nec.middleware.hr.mapper;

import com.nec.middleware.hr.dto.request.PoliticalPartyAgentRequestDto;
import com.nec.middleware.hr.dto.response.PoliticalPartyAgentResponseDto;
import com.nec.middleware.hr.entity.PoliticalPartyAgent;
import org.springframework.stereotype.Component;

@Component
public class PoliticalPartyAgentMapper {

    /**
     * Map RequestDto → new Entity (for CREATE).
     * Note: {@code code} is NOT set here — the service generates and assigns it
     * immediately after calling this method.
     */
    public PoliticalPartyAgent toEntity(PoliticalPartyAgentRequestDto dto) {
        PoliticalPartyAgent entity = PoliticalPartyAgent.builder()
                .politicalPartyNameId(dto.getPoliticalPartyNameId())
                .agentName(dto.getAgentName())
                .genderId(dto.getGenderId())
                .phone(dto.getPhone())
                .email(dto.getEmail())
                .photoUrl(dto.getPhotoUrl())
                .pollingStationId(dto.getPollingStationId())
                .regionId(dto.getRegionId())
                .districtId(dto.getDistrictId())
                .cityId(dto.getCityId())
                .statusId(dto.getStatusId())
                .build();

        entity.setIsActive(Boolean.TRUE);
        entity.setIsDeleted(Boolean.FALSE);
        entity.setCreatedBy(dto.getCreatedBy());
        entity.setUpdatedBy(dto.getCreatedBy());

        return entity;
    }

    /**
     * Merge RequestDto → existing Entity (for UPDATE).
     * {@code code} is deliberately excluded — it is immutable after creation.
     */
    public void updateEntity(PoliticalPartyAgent entity, PoliticalPartyAgentRequestDto dto) {
        entity.setPoliticalPartyNameId(dto.getPoliticalPartyNameId());
        entity.setAgentName(dto.getAgentName());
        entity.setGenderId(dto.getGenderId());
        entity.setPhone(dto.getPhone());
        entity.setEmail(dto.getEmail());
        entity.setPhotoUrl(dto.getPhotoUrl());
        entity.setPollingStationId(dto.getPollingStationId());
        entity.setRegionId(dto.getRegionId());
        entity.setDistrictId(dto.getDistrictId());
        entity.setCityId(dto.getCityId());
        entity.setStatusId(dto.getStatusId());
        entity.setUpdatedBy(dto.getUpdatedBy());
    }

    /**
     * Map Entity → ResponseDto
     */
    public PoliticalPartyAgentResponseDto toResponseDto(PoliticalPartyAgent entity) {
        return PoliticalPartyAgentResponseDto.builder()
                .id(entity.getId())
                .code(entity.getCode())
                .politicalPartyNameId(entity.getPoliticalPartyNameId())
                .agentName(entity.getAgentName())
                .genderId(entity.getGenderId())
                .phone(entity.getPhone())
                .email(entity.getEmail())
                .photoUrl(entity.getPhotoUrl())
                .pollingStationId(entity.getPollingStationId())
                .regionId(entity.getRegionId())
                .districtId(entity.getDistrictId())
                .cityId(entity.getCityId())
                .statusId(entity.getStatusId())
                .isActive(entity.getIsActive())
                .isDeleted(entity.getIsDeleted())
                .createdBy(entity.getCreatedBy())
                .createdAt(entity.getCreatedAt())
                .updatedBy(entity.getUpdatedBy())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }
}
