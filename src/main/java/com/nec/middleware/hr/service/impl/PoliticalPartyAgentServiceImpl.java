package com.nec.middleware.hr.service.impl;

import com.nec.middleware.Lookups.repository.LookupGenderRepository;
import com.nec.middleware.Lookups.repository.ThirdPartyStatusRepository;
import com.nec.middleware.exception.DuplicateResourceException;
import com.nec.middleware.exception.ResourceNotFoundException;
import com.nec.middleware.hr.constant.PoliticalPartyAgentConstants;
import com.nec.middleware.hr.constant.PortalUserConstants;
import com.nec.middleware.hr.dto.request.PoliticalPartyAgentFilterRequestDto;
import com.nec.middleware.hr.dto.request.PoliticalPartyAgentRequestDto;
import com.nec.middleware.hr.dto.response.PoliticalPartyAgentResponseDto;
import com.nec.middleware.hr.entity.PoliticalPartyAgent;
import com.nec.middleware.hr.mapper.PoliticalPartyAgentMapper;
import com.nec.middleware.hr.repository.PoliticalPartyAgentRepository;
import com.nec.middleware.hr.service.PoliticalPartyAgentService;
import com.nec.middleware.hr.specification.PoliticalPartyAgentSearchSpecification;
import com.nec.middleware.masterdata.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class PoliticalPartyAgentServiceImpl implements PoliticalPartyAgentService {

 // PA001, PA002 …

    private final PoliticalPartyAgentRepository politicalPartyAgentRepository;
    private final PoliticalPartyAgentMapper politicalPartyAgentMapper;
    private final MasterDataRepository regionRepository;
    private final DistrictRepository districtRepository;
    private final CityRepository cityRepository;
    private final LookupGenderRepository genderRepository;
    private final PollingStationRepository pollingStationRepository;
    private final PoliticalPartyRepository politicalPartyRepository;

    private final ThirdPartyStatusRepository statusRepository;

    // ------------------------------------------------------------------ SAVE / UPDATE

    @Override
    @Transactional
    public PoliticalPartyAgentResponseDto savePartyAgent(PoliticalPartyAgentRequestDto politicalPartyAgentRequestDto) {

        validateAgent(politicalPartyAgentRequestDto);
        log.info("Creating political party agent. AgentName: {}",politicalPartyAgentRequestDto.getAgentName());
        PoliticalPartyAgent politicalPartyAgentEntity = politicalPartyAgentMapper.toPoliticalPartyAgentEntity(politicalPartyAgentRequestDto);
        politicalPartyAgentEntity.setPoliticalPartyAgentUserId(generateUserID(PoliticalPartyAgentConstants.CODE_PREFIX, PoliticalPartyAgentConstants.CODE_PAD));

        politicalPartyAgentEntity.setPollingStation(
                pollingStationRepository.findById(politicalPartyAgentRequestDto.getPollingStationId()).orElseThrow(
                ()->new ResourceNotFoundException("Polling Station Not Found")));

        politicalPartyAgentEntity.setPoliticalPartyName(
                politicalPartyRepository.findById(politicalPartyAgentRequestDto.getPoliticalPartyNameId()).orElseThrow(
                        () -> new ResourceNotFoundException("Political Party Name Not Found")));

        politicalPartyAgentEntity.setGender(
                genderRepository.findById(politicalPartyAgentRequestDto.getGenderId())
                        .orElseThrow(() ->
                                new ResourceNotFoundException("Gender Not Found"))
        );

        politicalPartyAgentEntity.setRegion(
                regionRepository.findById(
                                politicalPartyAgentRequestDto.getRegionId())
                        .orElseThrow(() ->
                                new ResourceNotFoundException("Region Not Found"))
        );
        politicalPartyAgentEntity.setDistrict(
                districtRepository.findById(
                                politicalPartyAgentRequestDto.getDistrictId())
                        .orElseThrow(() ->
                                new ResourceNotFoundException("District Not Found"))
        );
        politicalPartyAgentEntity.setCity(
                cityRepository.findById(
                                politicalPartyAgentRequestDto.getCityId())
                        .orElseThrow(() ->
                                new ResourceNotFoundException("City Not Found"))
        );
        politicalPartyAgentEntity.setStatus(statusRepository.findById(politicalPartyAgentRequestDto.getStatusId()).orElseThrow(()-> new ResourceNotFoundException("Status Not Found")));
        PoliticalPartyAgent politicalPartyAgent = politicalPartyAgentRepository.save(politicalPartyAgentEntity);
        return politicalPartyAgentMapper.politicalPartyResponseDto(politicalPartyAgent);
    }

    // ------------------------------------------------------------------ READ

    @Override
    @Transactional(readOnly = true)
    public PoliticalPartyAgentResponseDto getPolticalPartyAgentById(String agentUserId) {
        return politicalPartyAgentMapper.politicalPartyResponseDto(findByAgentUserId(agentUserId));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<PoliticalPartyAgentResponseDto> getAllPartyAgents(PoliticalPartyAgentFilterRequestDto filterDto, int page, int size) {

        if (filterDto == null) {
            filterDto = new PoliticalPartyAgentFilterRequestDto();
        }
        Pageable pageable = PageRequest.of(
                page,
                size,
                Sort.by(Sort.Direction.DESC, "createdAt")
        );
        return politicalPartyAgentRepository.findAll(
                        PoliticalPartyAgentSearchSpecification.buildSpecification(filterDto),
                        pageable)
                .map(politicalPartyAgentMapper::politicalPartyResponseDto);
    }

    // ------------------------------------------------------------------ SOFT DELETE

    @Override
    @Transactional
    public PoliticalPartyAgentResponseDto changeStatus(String partyAgentId, Boolean isActiveFlag) {

        PoliticalPartyAgent partyAgent = findByAgentUserId(partyAgentId);
        partyAgent.setIsActive(isActiveFlag);
        log.info("Political party agent soft deleted with id: {}", partyAgent);
        return politicalPartyAgentMapper.politicalPartyResponseDto(
                politicalPartyAgentRepository.save(politicalPartyAgentRepository.save(partyAgent)));
    }
    //-------------------------------------------------------------Update
    @Override
    @Transactional
    public PoliticalPartyAgentResponseDto updatePoliticalPartyAgent(
            String agentUserId,
            PoliticalPartyAgentRequestDto politicalPartyAgentRequestDto) {

        PoliticalPartyAgent politicalPartyAgent =
                findByAgentUserId(agentUserId);

        validateAgentForUpdate(
                politicalPartyAgentRequestDto,
                politicalPartyAgent.getId()
        );

        updatePoliticalPartyAgents(
                politicalPartyAgent,
                politicalPartyAgentRequestDto
        );

        politicalPartyAgentRepository.save(politicalPartyAgent);

        return politicalPartyAgentMapper.politicalPartyResponseDto(
                politicalPartyAgent
        );
    }
    // ------------------------------------------------------------------
    // Code generation
    // ------------------------------------------------------------------

    private String generateUserID(String codePrefix, int codePad)  {
            int next = politicalPartyAgentRepository.findMaxCodeSequence() + 1;
            return codePrefix + String.format("%0" + codePad + "d", next);
    }

    // ------------------------------------------------------------------
    // Validation
    // ------------------------------------------------------------------

    private void validateAgent(PoliticalPartyAgentRequestDto politicalPartyAgentRequest) {

            if (politicalPartyAgentRepository.existsByEmail(politicalPartyAgentRequest.getEmail()) ||
                    politicalPartyAgentRepository.existsByPhone(politicalPartyAgentRequest.getPhone())) {
                throw new DuplicateResourceException(PoliticalPartyAgentConstants.AGENT_ALREADY_EXISTS);
            }
    }

    private void validateAgentForUpdate(
            PoliticalPartyAgentRequestDto request,
            Long id) {

        if (politicalPartyAgentRepository.existsByEmailAndIdNot(
                request.getEmail(), id)
                ||
                politicalPartyAgentRepository.existsByPhoneAndIdNot(
                        request.getPhone(), id)) {

            throw new DuplicateResourceException(
                    PoliticalPartyAgentConstants.AGENT_ALREADY_EXISTS
            );
        }
    }


    private PoliticalPartyAgent findByAgentUserId(String agentUserId) {
        return politicalPartyAgentRepository.findByPoliticalPartyAgentUserId(agentUserId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                PoliticalPartyAgentConstants.AGENT_NOT_FOUND + agentUserId));
    }

    public void updatePoliticalPartyAgents(
            PoliticalPartyAgent politicalPartyAgent,
            PoliticalPartyAgentRequestDto politicalPartyAgentRequestDto) {

        if (politicalPartyAgentRequestDto.getAgentName() != null) {
            politicalPartyAgent.setAgentName(politicalPartyAgentRequestDto.getAgentName());
        }

        if (politicalPartyAgentRequestDto.getPhone() != null) {
            politicalPartyAgent.setPhone(politicalPartyAgentRequestDto.getPhone());
        }

        if (politicalPartyAgentRequestDto.getEmail() != null) {
            politicalPartyAgent.setEmail(politicalPartyAgentRequestDto.getEmail());
        }

        if (politicalPartyAgentRequestDto.getPhotoUrl() != null) {
            politicalPartyAgent.setPhotoUrl(politicalPartyAgentRequestDto.getPhotoUrl());
        }

        if (politicalPartyAgentRequestDto.getUpdatedBy() != null) {
            politicalPartyAgent.setUpdatedBy(politicalPartyAgentRequestDto.getUpdatedBy());
        }

        // Political Party
        if (politicalPartyAgentRequestDto.getPoliticalPartyNameId() != null) {
            politicalPartyAgent.setPoliticalPartyName(
                    politicalPartyRepository.findById(politicalPartyAgentRequestDto.getPoliticalPartyNameId())
                            .orElseThrow(() ->
                                    new ResourceNotFoundException("Political Party not found"))
            );
        }

        // Gender
        if (politicalPartyAgentRequestDto.getGenderId() != null) {
            politicalPartyAgent.setGender(
                    genderRepository.findById(politicalPartyAgentRequestDto.getGenderId())
                            .orElseThrow(() ->
                                    new ResourceNotFoundException("Gender not found"))
            );
        }

        // Polling Station
        if (politicalPartyAgentRequestDto.getPollingStationId() != null) {
            politicalPartyAgent.setPollingStation(
                    pollingStationRepository.findById(politicalPartyAgentRequestDto.getPollingStationId())
                            .orElseThrow(() ->
                                    new ResourceNotFoundException("Polling Station not found"))
            );
        }

        // Region
        if (politicalPartyAgentRequestDto.getRegionId() != null) {
            politicalPartyAgent.setRegion(
                    regionRepository.findById(politicalPartyAgentRequestDto.getRegionId())
                            .orElseThrow(() ->
                                    new ResourceNotFoundException("Region not found"))
            );
        }

        // District
        if (politicalPartyAgentRequestDto.getDistrictId() != null) {
            politicalPartyAgent.setDistrict(
                    districtRepository.findById(politicalPartyAgentRequestDto.getDistrictId())
                            .orElseThrow(() ->
                                    new ResourceNotFoundException("District not found"))
            );
        }

        // City
        if (politicalPartyAgentRequestDto.getCityId() != null) {
            politicalPartyAgent.setCity(
                    cityRepository.findById(politicalPartyAgentRequestDto.getCityId())
                            .orElseThrow(() ->
                                    new ResourceNotFoundException("City not found"))
            );
        }

        // Status
        if (politicalPartyAgentRequestDto.getStatusId() != null) {
            politicalPartyAgent.setStatus(
                    statusRepository.findById(politicalPartyAgentRequestDto.getStatusId())
                            .orElseThrow(() ->
                                    new ResourceNotFoundException("Status not found"))
            );
        }
    }
}
