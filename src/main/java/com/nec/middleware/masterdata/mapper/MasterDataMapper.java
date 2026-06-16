package com.nec.middleware.masterdata.mapper;

import com.nec.middleware.masterdata.dto.request.MasterDataRequest;
import com.nec.middleware.masterdata.dto.request.DistrictRequest;
import com.nec.middleware.masterdata.dto.response.MasterDataResponse;
import com.nec.middleware.masterdata.dto.response.DistrictResponse;
import com.nec.middleware.masterdata.entity.MasterDataRegion;
import com.nec.middleware.masterdata.entity.MasterDataDistrict;
import com.nec.middleware.masterdata.dto.request.CityRequest;
import com.nec.middleware.masterdata.dto.response.CityResponse;
import com.nec.middleware.masterdata.entity.MasterDataCity;
import com.nec.middleware.masterdata.dto.request.PollingStationRequest;
import com.nec.middleware.masterdata.dto.response.PollingStationResponse;
import com.nec.middleware.masterdata.entity.MasterDataPollingStation;
import com.nec.middleware.masterdata.dto.request.VoterRegistrationCenterRequest;
import com.nec.middleware.masterdata.dto.response.VoterRegistrationCenterResponse;
import com.nec.middleware.masterdata.entity.MasterDataVoterRegistrationCenter;
import com.nec.middleware.masterdata.dto.request.UniversityRequest;
import com.nec.middleware.masterdata.dto.response.UniversityResponse;
import com.nec.middleware.masterdata.entity.MasterDataUniversity;
import com.nec.middleware.masterdata.dto.request.WarehouseRequest;
import com.nec.middleware.masterdata.dto.response.WarehouseResponse;
import com.nec.middleware.masterdata.entity.MasterDataWarehouse;
import com.nec.middleware.masterdata.dto.request.WarehouseSubStoreRequest;
import com.nec.middleware.masterdata.dto.response.WarehouseSubStoreResponse;
import com.nec.middleware.masterdata.entity.MasterDataWarehouseSubStore;
import com.nec.middleware.masterdata.dto.request.PoliticalPartyRequest;
import com.nec.middleware.masterdata.dto.response.PoliticalPartyResponse;
import com.nec.middleware.masterdata.entity.MasterDataPoliticalParty;
import com.nec.middleware.masterdata.dto.request.BankAccountRequest;
import com.nec.middleware.masterdata.dto.response.BankAccountResponse;
import com.nec.middleware.masterdata.entity.MasterDataBankAccount;
import com.nec.middleware.masterdata.dto.request.DeploymentRoleRequest;
import com.nec.middleware.masterdata.dto.response.DeploymentRoleResponse;
import com.nec.middleware.masterdata.entity.MasterDataDeploymentRole;
import com.nec.middleware.masterdata.dto.request.HrTrainerTotRequest;
import com.nec.middleware.masterdata.dto.response.HrTrainerTotResponse;
import com.nec.middleware.masterdata.entity.MasterDataHrTrainerTot;
import org.springframework.stereotype.Component;

/**
 * Manual mapper for converting between {@link MasterDataRegion} entity
 * and its request/response DTOs.
 *
 * <p>No MapStruct or any code-generation framework is used here –
 * all mapping logic is written explicitly for full transparency.
 */
@Component
public class MasterDataMapper {

    /**
     * Converts a {@link MasterDataRequest} to a new {@link MasterDataRegion} entity
     * ready for INSERT. The {@code id}, {@code createdAt}, {@code updatedAt},
     * {@code deletedAt}, and {@code isDeleted} fields are managed by JPA / DB defaults.
     *
     * @param request the validated request DTO
     * @return a new entity (not yet persisted)
     */
    public MasterDataRegion toEntity(MasterDataRequest request) {
        return MasterDataRegion.builder()
                .regionName(trimSafe(request.getRegionName()))
                .status(request.getStatus() != null ? request.getStatus() : "Active")
                .createdBy(request.getCreatedBy())
                .updatedBy(request.getUpdatedBy())
                .build();
    }

    /**
     * Converts a persisted {@link MasterDataRegion} entity to a {@link MasterDataResponse} DTO.
     *
     * @param entity the persisted entity
     * @return the response DTO
     */
    public MasterDataResponse toResponseDto(MasterDataRegion entity) {
        return MasterDataResponse.builder()
                .id(entity.getId())
                .regionName(entity.getRegionName())
                .status(entity.getStatus())
                .createdBy(entity.getCreatedBy())
                .updatedBy(entity.getUpdatedBy())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .deletedAt(entity.getDeletedAt())
                .isDeleted(entity.getIsDeleted())
                .build();
    }

    /**
     * Applies updatable fields from the request DTO onto an existing entity (for UPDATE).
     * Fields that are {@code null} in the request are left unchanged on the entity.
     *
     * @param entity  the existing entity to be updated
     * @param request the request DTO carrying new values
     */
    public void updateEntity(MasterDataRegion entity, MasterDataRequest request) {
        if (request.getRegionName() != null && !request.getRegionName().isBlank()) {
            entity.setRegionName(trimSafe(request.getRegionName()));
        }
        if (request.getStatus() != null && !request.getStatus().isBlank()) {
            entity.setStatus(request.getStatus());
        }
        if (request.getUpdatedBy() != null) {
            entity.setUpdatedBy(request.getUpdatedBy());
        }
    }

    // -------------------------------------------------------------------------
    // Internal helpers
    // -------------------------------------------------------------------------

    /**
     * Trims a string safely; returns {@code null} if the input is {@code null}.
     *
     * @param value the string to trim
     * @return trimmed string or {@code null}
     */
    private String trimSafe(String value) {
        return value != null ? value.trim() : null;
    }

    // =========================================================================
    // DISTRICT MAPPING METHODS
    // =========================================================================

    /**
     * Converts a {@link DistrictRequest} to a new {@link MasterDataDistrict} entity
     * ready for INSERT. The {@code id}, {@code createdAt}, {@code updatedAt},
     * {@code deletedAt}, and {@code isDeleted} fields are managed by JPA / DB defaults.
     *
     * @param request the validated request DTO
     * @return a new entity (not yet persisted)
     */
    public MasterDataDistrict toDistrictEntity(DistrictRequest request) {
        return MasterDataDistrict.builder()
                .regionId(request.getRegionId())
                .districtName(trimSafe(request.getDistrictName()))
                .status(request.getStatus() != null ? request.getStatus() : "Active")
                .createdBy(request.getCreatedBy())
                .updatedBy(request.getUpdatedBy())
                .build();
    }

    /**
     * Converts a persisted {@link MasterDataDistrict} entity to a {@link DistrictResponse} DTO.
     *
     * @param entity the persisted entity
     * @return the response DTO
     */
    public DistrictResponse toDistrictResponseDto(MasterDataDistrict entity) {
        return DistrictResponse.builder()
                .id(entity.getId())
                .regionId(entity.getRegionId())
                .districtName(entity.getDistrictName())
                .status(entity.getStatus())
                .createdBy(entity.getCreatedBy())
                .updatedBy(entity.getUpdatedBy())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .deletedAt(entity.getDeletedAt())
                .isDeleted(entity.getIsDeleted())
                .build();
    }

    /**
     * Applies updatable fields from the request DTO onto an existing district entity (for UPDATE).
     * Fields that are {@code null} in the request are left unchanged on the entity.
     *
     * @param entity  the existing entity to be updated
     * @param request the request DTO carrying new values
     */
    public void updateDistrictEntity(MasterDataDistrict entity, DistrictRequest request) {
        if (request.getRegionId() != null) {
            entity.setRegionId(request.getRegionId());
        }
        if (request.getDistrictName() != null && !request.getDistrictName().isBlank()) {
            entity.setDistrictName(trimSafe(request.getDistrictName()));
        }
        if (request.getStatus() != null && !request.getStatus().isBlank()) {
            entity.setStatus(request.getStatus());
        }
        if (request.getUpdatedBy() != null) {
            entity.setUpdatedBy(request.getUpdatedBy());
        }
    }

    public MasterDataCity toCityEntity(CityRequest request) {
        return MasterDataCity.builder()
                .regionId(request.getRegionId())
                .districtId(request.getDistrictId())
                .cityName(trimSafe(request.getCityName()))
                .status(request.getStatus() != null ? request.getStatus() : "Active")
                .createdBy(request.getCreatedBy())
                .updatedBy(request.getUpdatedBy())
                .build();
    }

    public CityResponse toCityResponseDto(MasterDataCity entity) {
        return CityResponse.builder()
                .id(entity.getId())
                .regionId(entity.getRegionId())
                .districtId(entity.getDistrictId())
                .cityName(entity.getCityName())
                .status(entity.getStatus())
                .createdBy(entity.getCreatedBy())
                .updatedBy(entity.getUpdatedBy())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .deletedAt(entity.getDeletedAt())
                .isDeleted(entity.getIsDeleted())
                .build();
    }

    public void updateCityEntity(MasterDataCity entity, CityRequest request) {
        if (request.getRegionId() != null) {
            entity.setRegionId(request.getRegionId());
        }
        if (request.getDistrictId() != null) {
            entity.setDistrictId(request.getDistrictId());
        }
        if (request.getCityName() != null && !request.getCityName().isBlank()) {
            entity.setCityName(trimSafe(request.getCityName()));
        }
        if (request.getStatus() != null && !request.getStatus().isBlank()) {
            entity.setStatus(request.getStatus());
        }
        if (request.getUpdatedBy() != null) {
            entity.setUpdatedBy(request.getUpdatedBy());
        }
    }
    public MasterDataPollingStation toPollingStationEntity(PollingStationRequest request) {

        return MasterDataPollingStation.builder()
                .pollingStationName(trimSafe(request.getPollingStationName()))
                .pollingStationCode(trimSafe(request.getPollingStationCode()))
                .voterCapacity(request.getVoterCapacity())
                .regionId(request.getRegionId())
                .districtId(request.getDistrictId())
                .cityId(request.getCityId())
                .status(request.getStatus() != null ? request.getStatus() : "Active")
                .createdBy(request.getCreatedBy())
                .updatedBy(request.getUpdatedBy())
                .build();
    }

    public PollingStationResponse toPollingStationResponseDto(
            MasterDataPollingStation entity) {

        return PollingStationResponse.builder()
                .id(entity.getId())
                .pollingStationName(entity.getPollingStationName())
                .pollingStationCode(entity.getPollingStationCode())
                .voterCapacity(entity.getVoterCapacity())
                .regionId(entity.getRegionId())
                .districtId(entity.getDistrictId())
                .cityId(entity.getCityId())
                .status(entity.getStatus())
                .createdBy(entity.getCreatedBy())
                .updatedBy(entity.getUpdatedBy())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .isDeleted(entity.getIsDeleted())
                .build();
    }

    public void updatePollingStationEntity(
            MasterDataPollingStation entity,
            PollingStationRequest request) {

        entity.setPollingStationName(
                trimSafe(request.getPollingStationName()));

        entity.setPollingStationCode(
                trimSafe(request.getPollingStationCode()));

        entity.setVoterCapacity(
                request.getVoterCapacity());

        entity.setRegionId(
                request.getRegionId());

        entity.setDistrictId(
                request.getDistrictId());

        entity.setCityId(
                request.getCityId());

        entity.setStatus(
                request.getStatus());

        entity.setUpdatedBy(
                request.getUpdatedBy());
    }

    public MasterDataVoterRegistrationCenter toVoterRegistrationCenterEntity(
            VoterRegistrationCenterRequest request) {

        return MasterDataVoterRegistrationCenter.builder()
                .vrcName(trimSafe(request.getVrcName()))
                .vrcCode(trimSafe(request.getVrcCode()))
                .regionId(request.getRegionId())
                .districtId(request.getDistrictId())
                .cityId(request.getCityId())
                .status(request.getStatus() != null ? request.getStatus() : "Active")
                .createdBy(request.getCreatedBy())
                .updatedBy(request.getUpdatedBy())
                .build();
    }

    public VoterRegistrationCenterResponse toVoterRegistrationCenterResponseDto(
            MasterDataVoterRegistrationCenter entity) {

        return VoterRegistrationCenterResponse.builder()
                .id(entity.getId())
                .vrcName(entity.getVrcName())
                .vrcCode(entity.getVrcCode())
                .regionId(entity.getRegionId())
                .districtId(entity.getDistrictId())
                .cityId(entity.getCityId())
                .status(entity.getStatus())
                .createdBy(entity.getCreatedBy())
                .updatedBy(entity.getUpdatedBy())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .isDeleted(entity.getIsDeleted())
                .build();
    }

    public void updateVoterRegistrationCenterEntity(
            MasterDataVoterRegistrationCenter entity,
            VoterRegistrationCenterRequest request) {

        entity.setVrcName(trimSafe(request.getVrcName()));
        entity.setVrcCode(trimSafe(request.getVrcCode()));
        entity.setRegionId(request.getRegionId());
        entity.setDistrictId(request.getDistrictId());
        entity.setCityId(request.getCityId());
        entity.setStatus(request.getStatus());
        entity.setUpdatedBy(request.getUpdatedBy());
    }

    public MasterDataUniversity toUniversityEntity(UniversityRequest request) {

        return MasterDataUniversity.builder()
                .universityName(trimSafe(request.getUniversityName()))
                .regionId(request.getRegionId())
                .districtId(request.getDistrictId())
                .cityId(request.getCityId())
                .location(trimSafe(request.getLocation()))
                .status(request.getStatus() != null ? request.getStatus() : "Active")
                .createdBy(request.getCreatedBy())
                .updatedBy(request.getUpdatedBy())
                .build();
    }

    public UniversityResponse toUniversityResponseDto(
            MasterDataUniversity entity) {

        return UniversityResponse.builder()
                .id(entity.getId())
                .universityName(entity.getUniversityName())
                .location(entity.getLocation())
                .regionId(entity.getRegionId())
                .districtId(entity.getDistrictId())
                .cityId(entity.getCityId())
                .status(entity.getStatus())
                .createdBy(entity.getCreatedBy())
                .updatedBy(entity.getUpdatedBy())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .isDeleted(entity.getIsDeleted())
                .build();
    }

    public void updateUniversityEntity(
            MasterDataUniversity entity,
            UniversityRequest request) {

        entity.setUniversityName(trimSafe(request.getUniversityName()));
        entity.setLocation(trimSafe(request.getLocation()));
        entity.setStatus(request.getStatus());
        entity.setUpdatedBy(request.getUpdatedBy());
    }
    public MasterDataWarehouse toWarehouseEntity(WarehouseRequest request) {

        return MasterDataWarehouse.builder()
                .warehouseName(trimSafe(request.getWarehouseName()))
                .regionId(request.getRegionId())
                .districtId(request.getDistrictId())
                .cityId(request.getCityId())
                .purpose(request.getPurpose() != null
                        ? trimSafe(request.getPurpose())
                        : "Main Storage & Distribution")
                .status(request.getStatus() != null ? request.getStatus() : "Active")
                .createdBy(request.getCreatedBy())
                .updatedBy(request.getUpdatedBy())
                .build();
    }

    public WarehouseResponse toWarehouseResponseDto(MasterDataWarehouse entity) {

        return WarehouseResponse.builder()
                .id(entity.getId())
                .warehouseName(entity.getWarehouseName())
                .regionId(entity.getRegionId())
                .districtId(entity.getDistrictId())
                .cityId(entity.getCityId())
                .purpose(entity.getPurpose())
                .status(entity.getStatus())
                .createdBy(entity.getCreatedBy())
                .updatedBy(entity.getUpdatedBy())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .isDeleted(entity.getIsDeleted())
                .build();
    }

    public void updateWarehouseEntity(
            MasterDataWarehouse entity,
            WarehouseRequest request) {

        entity.setWarehouseName(trimSafe(request.getWarehouseName()));
        entity.setRegionId(request.getRegionId());
        entity.setDistrictId(request.getDistrictId());
        entity.setCityId(request.getCityId());
        entity.setPurpose(trimSafe(request.getPurpose()));
        entity.setStatus(request.getStatus());
        entity.setUpdatedBy(request.getUpdatedBy());
    }

    public MasterDataWarehouseSubStore toWarehouseSubStoreEntity(
            WarehouseSubStoreRequest request) {

        return MasterDataWarehouseSubStore.builder()
                .warehouseId(request.getWarehouseId())
                .subStoreName(trimSafe(request.getSubStoreName()))
                .regionId(request.getRegionId())
                .districtId(request.getDistrictId())
                .cityId(request.getCityId())
                .purpose(request.getPurpose() != null
                        ? trimSafe(request.getPurpose())
                        : "Main Storage & Distribution")
                .status(request.getStatus() != null ? request.getStatus() : "Active")
                .createdBy(request.getCreatedBy())
                .updatedBy(request.getUpdatedBy())
                .build();
    }

    public WarehouseSubStoreResponse toWarehouseSubStoreResponseDto(
            MasterDataWarehouseSubStore entity) {

        return WarehouseSubStoreResponse.builder()
                .id(entity.getId())
                .warehouseId(entity.getWarehouseId())
                .subStoreName(entity.getSubStoreName())
                .regionId(entity.getRegionId())
                .districtId(entity.getDistrictId())
                .cityId(entity.getCityId())
                .purpose(entity.getPurpose())
                .status(entity.getStatus())
                .createdBy(entity.getCreatedBy())
                .updatedBy(entity.getUpdatedBy())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .isDeleted(entity.getIsDeleted())
                .build();
    }

    public void updateWarehouseSubStoreEntity(
            MasterDataWarehouseSubStore entity,
            WarehouseSubStoreRequest request) {

        entity.setWarehouseId(request.getWarehouseId());
        entity.setSubStoreName(trimSafe(request.getSubStoreName()));
        entity.setRegionId(request.getRegionId());
        entity.setDistrictId(request.getDistrictId());
        entity.setCityId(request.getCityId());
        entity.setPurpose(trimSafe(request.getPurpose()));
        entity.setStatus(request.getStatus());
        entity.setUpdatedBy(request.getUpdatedBy());
    }

    public MasterDataPoliticalParty toPoliticalPartyEntity(
            PoliticalPartyRequest request) {

        return MasterDataPoliticalParty.builder()
                .partyName(trimSafe(request.getPartyName()))
                .location(trimSafe(request.getLocation()))
                .status(request.getStatus() != null ? request.getStatus() : "Active")
                .createdBy(request.getCreatedBy())
                .updatedBy(request.getUpdatedBy())
                .build();
    }

    public PoliticalPartyResponse toPoliticalPartyResponseDto(
            MasterDataPoliticalParty entity) {

        return PoliticalPartyResponse.builder()
                .id(entity.getId())
                .partyName(entity.getPartyName())
                .location(entity.getLocation())
                .status(entity.getStatus())
                .createdBy(entity.getCreatedBy())
                .updatedBy(entity.getUpdatedBy())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .isDeleted(entity.getIsDeleted())
                .build();
    }

    public void updatePoliticalPartyEntity(
            MasterDataPoliticalParty entity,
            PoliticalPartyRequest request) {

        entity.setPartyName(trimSafe(request.getPartyName()));
        entity.setLocation(trimSafe(request.getLocation()));
        entity.setStatus(request.getStatus());
        entity.setUpdatedBy(request.getUpdatedBy());
    }

    public MasterDataBankAccount toBankAccountEntity(
            BankAccountRequest request) {

        return MasterDataBankAccount.builder()
                .accountHolderName(trimSafe(request.getAccountHolderName()))
                .accountNumber(trimSafe(request.getAccountNumber()))
                .accountType(trimSafe(request.getAccountType()))
                .bankName(trimSafe(request.getBankName()))
                .branchName(trimSafe(request.getBranchName()))
                .ifscSwiftCode(trimSafe(request.getIfscSwiftCode()))
                .status(request.getStatus() != null ? request.getStatus() : "Active")
                .createdBy(request.getCreatedBy())
                .updatedBy(request.getUpdatedBy())
                .build();
    }

    public BankAccountResponse toBankAccountResponseDto(
            MasterDataBankAccount entity) {

        return BankAccountResponse.builder()
                .id(entity.getId())
                .accountHolderName(entity.getAccountHolderName())
                .accountNumber(entity.getAccountNumber())
                .accountType(entity.getAccountType())
                .bankName(entity.getBankName())
                .branchName(entity.getBranchName())
                .ifscSwiftCode(entity.getIfscSwiftCode())
                .status(entity.getStatus())
                .createdBy(entity.getCreatedBy())
                .updatedBy(entity.getUpdatedBy())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .isDeleted(entity.getIsDeleted())
                .build();
    }

    public void updateBankAccountEntity(
            MasterDataBankAccount entity,
            BankAccountRequest request) {

        entity.setAccountHolderName(trimSafe(request.getAccountHolderName()));
        entity.setAccountNumber(trimSafe(request.getAccountNumber()));
        entity.setAccountType(trimSafe(request.getAccountType()));
        entity.setBankName(trimSafe(request.getBankName()));
        entity.setBranchName(trimSafe(request.getBranchName()));
        entity.setIfscSwiftCode(trimSafe(request.getIfscSwiftCode()));
        entity.setStatus(request.getStatus());
        entity.setUpdatedBy(request.getUpdatedBy());
    }

    public MasterDataDeploymentRole toDeploymentRoleEntity(
            DeploymentRoleRequest request) {

        return MasterDataDeploymentRole.builder()
                .roleName(trimSafe(request.getRoleName()))
                .status(request.getStatus() != null ? request.getStatus() : "Active")
                .createdBy(request.getCreatedBy())
                .updatedBy(request.getUpdatedBy())
                .build();
    }

    public DeploymentRoleResponse toDeploymentRoleResponseDto(
            MasterDataDeploymentRole entity) {

        return DeploymentRoleResponse.builder()
                .id(entity.getId())
                .roleName(entity.getRoleName())
                .status(entity.getStatus())
                .createdBy(entity.getCreatedBy())
                .updatedBy(entity.getUpdatedBy())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .isDeleted(entity.getIsDeleted())
                .build();
    }

    public void updateDeploymentRoleEntity(
            MasterDataDeploymentRole entity,
            DeploymentRoleRequest request) {

        entity.setRoleName(trimSafe(request.getRoleName()));
        entity.setStatus(request.getStatus());
        entity.setUpdatedBy(request.getUpdatedBy());
    }



    public MasterDataHrTrainerTot toHrTrainerTotEntity(
            HrTrainerTotRequest request) {

        return MasterDataHrTrainerTot.builder()
                .code(trimSafe(request.getCode()))
                .fullName(trimSafe(request.getFullName()))
                .phone(trimSafe(request.getPhone()))
                .email(trimSafe(request.getEmail()))
                .description(trimSafe(request.getDescription()))
                .isActive(request.getIsActive() != null ? request.getIsActive() : true)
                .createdBy(request.getCreatedBy())
                .updatedBy(request.getUpdatedBy())
                .build();
    }

    public HrTrainerTotResponse toHrTrainerTotResponseDto(
            MasterDataHrTrainerTot entity) {

        return HrTrainerTotResponse.builder()
                .id(entity.getId())
                .code(entity.getCode())
                .fullName(entity.getFullName())
                .phone(entity.getPhone())
                .email(entity.getEmail())
                .description(entity.getDescription())
                .isActive(entity.getIsActive())
                .isDeleted(entity.getIsDeleted())
                .createdBy(entity.getCreatedBy())
                .updatedBy(entity.getUpdatedBy())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }

    public void updateHrTrainerTotEntity(
            MasterDataHrTrainerTot entity,
            HrTrainerTotRequest request) {

        entity.setCode(trimSafe(request.getCode()));
        entity.setFullName(trimSafe(request.getFullName()));
        entity.setPhone(trimSafe(request.getPhone()));
        entity.setEmail(trimSafe(request.getEmail()));
        entity.setDescription(trimSafe(request.getDescription()));
        entity.setIsActive(request.getIsActive() != null ? request.getIsActive() : true);
        entity.setUpdatedBy(request.getUpdatedBy());
    }

}

