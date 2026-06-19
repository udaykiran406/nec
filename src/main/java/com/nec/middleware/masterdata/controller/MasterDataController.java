package com.nec.middleware.masterdata.controller;

import com.nec.middleware.masterdata.constants.MasterDataConstants;
import com.nec.middleware.masterdata.dto.request.*;
import com.nec.middleware.masterdata.dto.response.CityResponse;
import com.nec.middleware.masterdata.dto.response.DistrictResponse;
import com.nec.middleware.masterdata.dto.response.MasterDataResponse;
import com.nec.middleware.masterdata.dto.response.PollingStationResponse;
import com.nec.middleware.masterdata.response.ApiResponse;
import com.nec.middleware.masterdata.service.MasterDataService;
import com.nec.middleware.masterdata.dto.response.VoterRegistrationCenterResponse;
import com.nec.middleware.masterdata.dto.response.UniversityResponse;
import com.nec.middleware.masterdata.dto.response.WarehouseResponse;
import com.nec.middleware.masterdata.dto.response.WarehouseSubStoreResponse;
import com.nec.middleware.masterdata.dto.response.PoliticalPartyResponse;
import com.nec.middleware.masterdata.dto.response.BankAccountResponse;
import com.nec.middleware.masterdata.dto.response.DeploymentRoleResponse;
import com.nec.middleware.masterdata.dto.response.HrTrainerTotResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.Page;
import java.util.List;

@RestController
@RequestMapping("/api/v1/masterdata")
@RequiredArgsConstructor
public class MasterDataController {

    private final MasterDataService service;

    // =========================================================================
    // REGION APIs
    // =========================================================================

    @PostMapping("/regions/save")
    public ResponseEntity<ApiResponse<MasterDataResponse>> saveRegion(
            @Valid @RequestBody MasterDataRequest request) {

        MasterDataResponse response = service.saveRegion(request);

        if (request.getId() == null) {
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ApiResponse.success(
                            MasterDataConstants.RECORD_CREATED,
                            response));
        }

        return ResponseEntity.ok(
                ApiResponse.success(
                        MasterDataConstants.RECORD_UPDATED,
                        response));
    }

    @GetMapping("/regions/list")
    public ResponseEntity<ApiResponse<List<MasterDataResponse>>> getAllRegions() {

        return ResponseEntity.ok(
                ApiResponse.success(
                        MasterDataConstants.RECORDS_FETCHED,
                        service.getAllRegions()));
    }

    @GetMapping("/regions/{id}")
    public ResponseEntity<ApiResponse<MasterDataResponse>> getRegionById(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                ApiResponse.success(
                        MasterDataConstants.RECORD_FETCHED,
                        service.getRegionById(id)));
    }

    // =========================================================================
    // DISTRICT APIs
    // =========================================================================

    @PostMapping("/districts/save")
    public ResponseEntity<ApiResponse<DistrictResponse>> saveDistrict(
            @Valid @RequestBody DistrictRequest request) {

        DistrictResponse response = service.saveDistrict(request);

        if (request.getId() == null) {
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ApiResponse.success(
                            MasterDataConstants.RECORD_CREATED,
                            response));
        }

        return ResponseEntity.ok(
                ApiResponse.success(
                        MasterDataConstants.RECORD_UPDATED,
                        response));
    }

    @GetMapping("/districts/list")
    public ResponseEntity<ApiResponse<Page<DistrictResponse>>> getAllDistricts(DistrictListRequestDto filterDto) {

        return ResponseEntity.ok(
                ApiResponse.success(
                        MasterDataConstants.RECORDS_FETCHED,
                        service.getAllDistricts(filterDto)));
    }



    @GetMapping("/districts/{id}")
    public ResponseEntity<ApiResponse<DistrictResponse>> getDistrictById(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                ApiResponse.success(
                        MasterDataConstants.RECORD_FETCHED,
                        service.getDistrictById(id)));
    }

    // =========================================================================
    // CITY APIs
    // =========================================================================

    @PostMapping("/cities/save")
    public ResponseEntity<ApiResponse<CityResponse>> saveCity(
            @Valid @RequestBody CityRequest request) {

        CityResponse response = service.saveCity(request);

        if (request.getId() == null) {
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ApiResponse.success(
                            MasterDataConstants.RECORD_CREATED,
                            response));
        }

        return ResponseEntity.ok(
                ApiResponse.success(
                        MasterDataConstants.RECORD_UPDATED,
                        response));
    }

    @GetMapping("/cities/list")
    public ResponseEntity<ApiResponse<Page<CityResponse>>> getAllCities(CityListRequestDto filterDto) {

        return ResponseEntity.ok(
                ApiResponse.success(
                        MasterDataConstants.RECORDS_FETCHED,
                        service.getAllCities(filterDto)));
    }



    @GetMapping("/cities/{id}")
    public ResponseEntity<ApiResponse<CityResponse>> getCityById(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                ApiResponse.success(
                        MasterDataConstants.RECORD_FETCHED,
                        service.getCityById(id)));
    }

    // =========================================================================
    // POLLING STATION APIs
    // =========================================================================

    @PostMapping("/polling-stations/save")
    public ResponseEntity<ApiResponse<PollingStationResponse>> savePollingStation(
            @Valid @RequestBody PollingStationRequest request) {

        PollingStationResponse response = service.savePollingStation(request);

        if (request.getId() == null) {
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ApiResponse.success(
                            MasterDataConstants.RECORD_CREATED,
                            response));
        }

        return ResponseEntity.ok(
                ApiResponse.success(
                        MasterDataConstants.RECORD_UPDATED,
                        response));
    }

    @GetMapping("/polling-stations/list")
    public ResponseEntity<ApiResponse<Page<PollingStationResponse>>> getAllPollingStations(
            @ParameterObject PollingStationListRequestDto filterDto) {

        return ResponseEntity.ok(
                ApiResponse.success(
                        MasterDataConstants.RECORDS_FETCHED,
                        service.getAllPollingStations(filterDto)
                )
        );
    }


    @GetMapping("/polling-stations/{id}")
    public ResponseEntity<ApiResponse<PollingStationResponse>> getPollingStationById(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                ApiResponse.success(
                        MasterDataConstants.RECORD_FETCHED,
                        service.getPollingStationById(id)));
    }

    // =========================================================================
    // VOTER REGISTRATION CENTER APIs
    // =========================================================================

    @PostMapping("/voter-registration-centers/save")
    public ResponseEntity<ApiResponse<VoterRegistrationCenterResponse>> saveVoterRegistrationCenter(
            @Valid @RequestBody VoterRegistrationCenterRequest request) {

        VoterRegistrationCenterResponse response =
                service.saveVoterRegistrationCenter(request);

        if (request.getId() == null) {
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ApiResponse.success(
                            MasterDataConstants.RECORD_CREATED,
                            response));
        }

        return ResponseEntity.ok(
                ApiResponse.success(
                        MasterDataConstants.RECORD_UPDATED,
                        response));
    }

    @GetMapping("/voter-registration-centers/list")
    public ResponseEntity<ApiResponse<Page<VoterRegistrationCenterResponse>>>
    getAllVoterRegistrationCenters(
            VoterRegistrationCenterListRequestDto filterDto) {

        return ResponseEntity.ok(
                ApiResponse.success(
                        MasterDataConstants.RECORDS_FETCHED,
                        service.getAllVoterRegistrationCenters(filterDto)
                )
        );
    }



    @GetMapping("/voter-registration-centers/{id}")
    public ResponseEntity<ApiResponse<VoterRegistrationCenterResponse>> getVoterRegistrationCenterById(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                ApiResponse.success(
                        MasterDataConstants.RECORD_FETCHED,
                        service.getVoterRegistrationCenterById(id)));
    }

    // =========================================================================
    // UNIVERSITY APIs
    // =========================================================================

    @PostMapping("/universities/save")
    public ResponseEntity<ApiResponse<UniversityResponse>> saveUniversity(
            @Valid @RequestBody UniversityRequest request) {

        UniversityResponse response = service.saveUniversity(request);

        if (request.getId() == null) {
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ApiResponse.success(
                            MasterDataConstants.RECORD_CREATED,
                            response));
        }

        return ResponseEntity.ok(
                ApiResponse.success(
                        MasterDataConstants.RECORD_UPDATED,
                        response));
    }

    @GetMapping("/universities/list")
    public ResponseEntity<ApiResponse<List<UniversityResponse>>> getAllUniversities() {

        return ResponseEntity.ok(
                ApiResponse.success(
                        MasterDataConstants.RECORDS_FETCHED,
                        service.getAllUniversities()));
    }


    @GetMapping("/universities/{id}")
    public ResponseEntity<ApiResponse<UniversityResponse>> getUniversityById(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                ApiResponse.success(
                        MasterDataConstants.RECORD_FETCHED,
                        service.getUniversityById(id)));
    }

    // =========================================================================
    // WAREHOUSE APIs
    // =========================================================================

    @PostMapping("/warehouses/save")
    public ResponseEntity<ApiResponse<WarehouseResponse>> saveWarehouse(
            @Valid @RequestBody WarehouseRequest request) {

        WarehouseResponse response = service.saveWarehouse(request);

        if (request.getId() == null) {
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ApiResponse.success(
                            MasterDataConstants.RECORD_CREATED,
                            response));
        }

        return ResponseEntity.ok(
                ApiResponse.success(
                        MasterDataConstants.RECORD_UPDATED,
                        response));
    }

    @GetMapping("/warehouses/list")
    public ResponseEntity<ApiResponse<Page<WarehouseResponse>>> getAllWarehouses(
            WarehouseListRequestDto filterDto) {

        return ResponseEntity.ok(
                ApiResponse.success(
                        MasterDataConstants.RECORDS_FETCHED,
                        service.getAllWarehouses(filterDto)
                )
        );
    }



    @GetMapping("/warehouses/{id}")
    public ResponseEntity<ApiResponse<WarehouseResponse>> getWarehouseById(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                ApiResponse.success(
                        MasterDataConstants.RECORD_FETCHED,
                        service.getWarehouseById(id)));
    }

    // =========================================================================
    // WAREHOUSE SUB STORE APIs
    // =========================================================================

    @PostMapping("/warehouse-sub-stores/save")
    public ResponseEntity<ApiResponse<WarehouseSubStoreResponse>> saveWarehouseSubStore(
            @Valid @RequestBody WarehouseSubStoreRequest request) {

        WarehouseSubStoreResponse response = service.saveWarehouseSubStore(request);

        if (request.getId() == null) {
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ApiResponse.success(
                            MasterDataConstants.RECORD_CREATED,
                            response));
        }

        return ResponseEntity.ok(
                ApiResponse.success(
                        MasterDataConstants.RECORD_UPDATED,
                        response));
    }

    @GetMapping("/warehouse-sub-stores/list")
    public ResponseEntity<ApiResponse<Page<WarehouseSubStoreResponse>>>
    getAllWarehouseSubStores(
            WarehouseSubStoreListRequestDto filterDto) {

        return ResponseEntity.ok(
                ApiResponse.success(
                        MasterDataConstants.RECORDS_FETCHED,
                        service.getAllWarehouseSubStores(filterDto)
                )
        );
    }



    @GetMapping("/warehouse-sub-stores/{id}")
    public ResponseEntity<ApiResponse<WarehouseSubStoreResponse>> getWarehouseSubStoreById(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                ApiResponse.success(
                        MasterDataConstants.RECORD_FETCHED,
                        service.getWarehouseSubStoreById(id)));
    }

    // =========================================================================
    // POLITICAL PARTY APIs
    // =========================================================================

    @PostMapping("/political-parties/save")
    public ResponseEntity<ApiResponse<PoliticalPartyResponse>> savePoliticalParty(
            @Valid @RequestBody PoliticalPartyRequest request) {

        PoliticalPartyResponse response = service.savePoliticalParty(request);

        if (request.getId() == null) {
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ApiResponse.success(
                            MasterDataConstants.RECORD_CREATED,
                            response));
        }

        return ResponseEntity.ok(
                ApiResponse.success(
                        MasterDataConstants.RECORD_UPDATED,
                        response));
    }

    @GetMapping("/political-parties/list")
    public ResponseEntity<ApiResponse<List<PoliticalPartyResponse>>> getAllPoliticalParties() {

        return ResponseEntity.ok(
                ApiResponse.success(
                        MasterDataConstants.RECORDS_FETCHED,
                        service.getAllPoliticalParties()));
    }

    @GetMapping("/political-parties/{id}")
    public ResponseEntity<ApiResponse<PoliticalPartyResponse>> getPoliticalPartyById(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                ApiResponse.success(
                        MasterDataConstants.RECORD_FETCHED,
                        service.getPoliticalPartyById(id)));
    }

    // =========================================================================
    // BANK ACCOUNT APIs
    // =========================================================================

    @PostMapping("/bank-accounts/save")
    public ResponseEntity<ApiResponse<BankAccountResponse>> saveBankAccount(
            @Valid @RequestBody BankAccountRequest request) {

        BankAccountResponse response = service.saveBankAccount(request);

        if (request.getId() == null) {
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ApiResponse.success(
                            MasterDataConstants.RECORD_CREATED,
                            response));
        }

        return ResponseEntity.ok(
                ApiResponse.success(
                        MasterDataConstants.RECORD_UPDATED,
                        response));
    }

    @GetMapping("/bank-accounts/list")
    public ResponseEntity<ApiResponse<Page<BankAccountResponse>>> getAllBankAccounts(
            BankAccountListRequestDto filterDto) {

        return ResponseEntity.ok(
                ApiResponse.success(
                        MasterDataConstants.RECORDS_FETCHED,
                        service.getAllBankAccounts(filterDto)
                )
        );
    }



    @GetMapping("/bank-accounts/{id}")
    public ResponseEntity<ApiResponse<BankAccountResponse>> getBankAccountById(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                ApiResponse.success(
                        MasterDataConstants.RECORD_FETCHED,
                        service.getBankAccountById(id)));
    }

    // =========================================================================
    // DEPLOYMENT ROLE APIs
    // =========================================================================

    @PostMapping("/deployment-roles/save")
    public ResponseEntity<ApiResponse<DeploymentRoleResponse>> saveDeploymentRole(
            @Valid @RequestBody DeploymentRoleRequest request) {

        DeploymentRoleResponse response = service.saveDeploymentRole(request);

        if (request.getId() == null) {
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ApiResponse.success(
                            MasterDataConstants.RECORD_CREATED,
                            response));
        }

        return ResponseEntity.ok(
                ApiResponse.success(
                        MasterDataConstants.RECORD_UPDATED,
                        response));
    }

    @GetMapping("/deployment-roles/list")
    public ResponseEntity<ApiResponse<List<DeploymentRoleResponse>>> getAllDeploymentRoles() {

        return ResponseEntity.ok(
                ApiResponse.success(
                        MasterDataConstants.RECORDS_FETCHED,
                        service.getAllDeploymentRoles()));
    }


    @GetMapping("/deployment-roles/{id}")
    public ResponseEntity<ApiResponse<DeploymentRoleResponse>> getDeploymentRoleById(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                ApiResponse.success(
                        MasterDataConstants.RECORD_FETCHED,
                        service.getDeploymentRoleById(id)));
    }
    

    // =========================================================================
    // HR TRAINER TOT APIs
    // =========================================================================

    @PostMapping("/hr-trainer-tots/save")
    public ResponseEntity<ApiResponse<HrTrainerTotResponse>> saveHrTrainerTot(
            @Valid @RequestBody HrTrainerTotRequest request) {

        HrTrainerTotResponse response = service.saveHrTrainerTot(request);

        if (request.getId() == null) {
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ApiResponse.success(
                            MasterDataConstants.RECORD_CREATED,
                            response));
        }

        return ResponseEntity.ok(
                ApiResponse.success(
                        MasterDataConstants.RECORD_UPDATED,
                        response));
    }

    @GetMapping("/hr-trainer-tots/list")
    public ResponseEntity<ApiResponse<List<HrTrainerTotResponse>>> getAllHrTrainerTots() {

        return ResponseEntity.ok(
                ApiResponse.success(
                        MasterDataConstants.RECORDS_FETCHED,
                        service.getAllHrTrainerTots()));
    }



    @GetMapping("/hr-trainer-tots/{id}")
    public ResponseEntity<ApiResponse<HrTrainerTotResponse>> getHrTrainerTotById(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                ApiResponse.success(
                        MasterDataConstants.RECORD_FETCHED,
                        service.getHrTrainerTotById(id)));
    }
}