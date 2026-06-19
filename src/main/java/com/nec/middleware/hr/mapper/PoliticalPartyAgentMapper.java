package com.nec.middleware.hr.mapper;

import com.nec.middleware.hr.dto.request.PoliticalPartyAgentRequestDto;
import com.nec.middleware.dto.IdValueDto;
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
    public PoliticalPartyAgent toPoliticalPartyAgentEntity(PoliticalPartyAgentRequestDto politicalPartyAgentRequestDto) {
        PoliticalPartyAgent politicalPartyAgentEntity = PoliticalPartyAgent.builder()
                .agentName(politicalPartyAgentRequestDto.getAgentName())
                .phone(politicalPartyAgentRequestDto.getPhone())
                .email(politicalPartyAgentRequestDto.getEmail())
                .photoPath(politicalPartyAgentRequestDto.getPhotoPath())
                .build();

        politicalPartyAgentEntity.setIsActive(Boolean.TRUE);
        politicalPartyAgentEntity.setCreatedBy(politicalPartyAgentRequestDto.getCreatedBy());
        politicalPartyAgentEntity.setUpdatedBy(politicalPartyAgentRequestDto.getCreatedBy());

        return politicalPartyAgentEntity;
    }

    /**
     * Merge RequestDto → existing Entity (for UPDATE).
     * {@code code} is deliberately excluded — it is immutable after creation.
     */
    public void updateEntity(PoliticalPartyAgent entity, PoliticalPartyAgentRequestDto dto) {
//        entity.setPoliticalPartyNameId(dto.getPoliticalPartyNameId());
        entity.setAgentName(dto.getAgentName());
        entity.setPhone(dto.getPhone());
        entity.setEmail(dto.getEmail());
        entity.setPhotoPath(dto.getPhotoPath());
//        entity.setPollingStation(dto.getPollingStationId());
//        entity.setRegion(dto.getRegionId());
//        entity.setDistrict(dto.getDistrictId());
//        entity.setCity(dto.getCityId());
//        entity.setStatusId(dto.getStatusId());
        entity.setUpdatedBy(dto.getUpdatedBy());
    }

    /**
     * Map Entity → ResponseDto
     */
    public PoliticalPartyAgentResponseDto politicalPartyResponseDto(PoliticalPartyAgent politicalPartyAgent) {
        return PoliticalPartyAgentResponseDto.builder()
                .politicalPartyAgentUserId(politicalPartyAgent.getPoliticalPartyAgentUserId())
                .agentName(politicalPartyAgent.getAgentName())
                .phone(politicalPartyAgent.getPhone())
                .email(politicalPartyAgent.getEmail())
                .photoPath(politicalPartyAgent.getPhotoPath())
                .politicalPartyName(politicalPartyAgent.getPoliticalPartyName()!=null ? IdValueDto.builder().id(
                        politicalPartyAgent.getPoliticalPartyName().getId())
                        .value(politicalPartyAgent.getPoliticalPartyName().getPartyName()).build():null)
                .gender(politicalPartyAgent.getGender() != null
                        ? IdValueDto.builder()
                        .id(politicalPartyAgent.getGender().getId())
                        .value(politicalPartyAgent.getGender().getValue())
                        .build()
                        : null)
                .region(politicalPartyAgent.getRegion() != null
                        ? IdValueDto.builder()
                        .id(politicalPartyAgent.getRegion().getId())
                        .value(politicalPartyAgent.getRegion().getRegionName())
                        .build()
                        : null)

                .district(politicalPartyAgent.getDistrict() != null
                        ? IdValueDto.builder()
                        .id(politicalPartyAgent.getDistrict().getId())
                        .value(politicalPartyAgent.getDistrict().getDistrictName())
                        .build()
                        : null)

                .city(politicalPartyAgent.getCity() != null
                        ? IdValueDto.builder()
                        .id(politicalPartyAgent.getCity().getId())
                        .value(politicalPartyAgent.getCity().getCityName())
                        .build()
                        : null)
                .pollingStation(politicalPartyAgent.getPollingStation() != null
                        ? IdValueDto.builder()
                        .id(politicalPartyAgent.getPollingStation().getId())
                        .value(politicalPartyAgent.getPollingStation().getPollingStationName())
                        .build()
                        : null)
                .status(politicalPartyAgent.getStatus() != null
                        ? IdValueDto.builder()
                        .id(politicalPartyAgent.getStatus().getId())
                        .value(politicalPartyAgent.getStatus().getValue())
                        .build()
                        : null)
                .isActive(politicalPartyAgent.getIsActive())
                .createdBy(politicalPartyAgent.getCreatedBy())
                .updatedBy(politicalPartyAgent.getUpdatedBy())
                .updatedBy(politicalPartyAgent.getUpdatedBy())
                .updatedAt(politicalPartyAgent.getUpdatedAt())
                .build();


    }
}
