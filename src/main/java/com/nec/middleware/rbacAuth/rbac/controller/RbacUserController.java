package com.nec.middleware.rbacAuth.rbac.controller;

import com.nec.middleware.rbacAuth.rbac.constant.ApiMessageConstants;
import com.nec.middleware.rbacAuth.rbac.constant.ErrorCodeConstants;
import com.nec.middleware.rbacAuth.rbac.constant.RbacConstants;
import com.nec.middleware.rbacAuth.rbac.dto.request.RbacUserRequest;
import com.nec.middleware.rbacAuth.rbac.dto.request.UserListRequestDto;
import com.nec.middleware.rbacAuth.rbac.dto.response.ApiResponse;
import com.nec.middleware.rbacAuth.rbac.dto.response.PaginatedResponse;
import com.nec.middleware.rbacAuth.rbac.dto.response.RbacUserResponse;
import com.nec.middleware.rbacAuth.rbac.service.UserService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/v1/rbac")
@AllArgsConstructor
public class RbacUserController {

    private final UserService userService;

    @PostMapping("/users")
    public ResponseEntity<ApiResponse<RbacUserResponse>> createUser(
            @Valid @RequestBody RbacUserRequest request) {
        log.info("Create RBAC user request received: email={}", request.getEmail());
        RbacUserResponse response = userService.createUser(request);
        log.info("RBAC user created successfully: userId={}", response.getId());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.created(RbacConstants.USER_CREATED, response));
    }

    @PutMapping("/users/{id}")
    public ResponseEntity<ApiResponse<RbacUserResponse>> updateUser(
            @PathVariable String id,
            @Valid @RequestBody RbacUserRequest request) {
        log.info("Update RBAC user request received: userId={}, email={}", id, request.getEmail());
        RbacUserResponse response = userService.updateUser(id, request);
        log.info("RBAC user updated successfully: userId={}", response.getId());
        return ResponseEntity.ok(ApiResponse.ok(RbacConstants.USER_UPDATED, response));
    }

    @GetMapping("/users/{id}")
    public ResponseEntity<ApiResponse<RbacUserResponse>> getUserById(@PathVariable String id) {
        log.info("Fetch RBAC user request received: userId={}", id);
        RbacUserResponse response = userService.getUserById(id);
        log.debug("RBAC user fetched successfully: userId={}", id);
        return ResponseEntity.ok(ApiResponse.ok(RbacConstants.USER_FETCHED, response));
    }

    @GetMapping("/users/all")
    public ResponseEntity<ApiResponse<List<RbacUserResponse>>> getAllUsers() {
        log.info("Fetching all RBAC users");
        List<RbacUserResponse> response = userService.getAllUsers();
        log.debug("Fetched {} RBAC users", response.size());
        return ResponseEntity.ok(ApiResponse.ok(RbacConstants.USERS_FETCHED, response));
    }

    @GetMapping("/users/role/{roleId}")
    public ResponseEntity<ApiResponse<List<RbacUserResponse>>> getUsersByRoleId(@PathVariable Long roleId) {
        log.info("Fetching RBAC users by role: roleId={}", roleId);
        List<RbacUserResponse> response = userService.getUsersByRoleId(roleId);
        log.debug("Fetched {} RBAC users for roleId={}", response.size(), roleId);
        return ResponseEntity.ok(ApiResponse.ok(RbacConstants.USERS_FETCHED, response));
    }

    @PatchMapping("/users/{id}/status")
    public ResponseEntity<ApiResponse<RbacUserResponse>> changeUserActiveStatus(
            @PathVariable String id,
            @RequestParam(required = false) Integer isActive) {

        if (isActive == null) {
            log.warn("Change RBAC user status rejected: userId={}, reason=isActive missing", id);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error(HttpStatus.BAD_REQUEST.value(),
                            ErrorCodeConstants.MISSING_REQUIRED_FIELDS,
                            "isActive is required"));
        }

        log.info("Change RBAC user status request received: userId={}, isActive={}", id, isActive);
        RbacUserResponse response = userService.changeUserActiveStatus(id, isActive);
        log.info("RBAC user status changed successfully: userId={}, isActive={}", id, isActive);
        return ResponseEntity.ok(ApiResponse.ok(RbacConstants.USER_STATUS_CHANGED, response));
    }

    @DeleteMapping("/users/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteUser(@PathVariable String id) {
        log.info("Soft deleting RBAC user: userId={}", id);
        userService.deleteUser(id);
        log.info("RBAC user soft-deleted successfully: userId={}", id);
        return ResponseEntity.ok(ApiResponse.ok(RbacConstants.USER_DELETED));
    }

    @PostMapping("/users/{id}/restore")
    public ResponseEntity<ApiResponse<RbacUserResponse>> restoreUser(@PathVariable String id) {
        log.info("Restore RBAC user request received: userId={}", id);
        RbacUserResponse response = userService.restoreUser(id);
        log.info("RBAC user restored successfully: userId={}", response.getId());
        return ResponseEntity.ok(ApiResponse.ok(RbacConstants.USER_RESTORED, response));
    }

    /**
     * Retrieves users with optional pagination, sorting, global search, and dynamic filters.
     */
    @PostMapping("/user/list")
    public ResponseEntity<ApiResponse<PaginatedResponse<RbacUserResponse>>> listUsers(
            @Valid @RequestBody(required = false) UserListRequestDto request) {
        UserListRequestDto listRequest = request != null ? request : new UserListRequestDto();
        log.info("Fetching RBAC users with filters: page={}, size={}, search={}",
                listRequest.getPage(), listRequest.getSize(), listRequest.getSearch());
        PaginatedResponse<RbacUserResponse> response = userService.listUsers(request);
        return ResponseEntity.ok(ApiResponse.ok(ApiMessageConstants.DATA_FETCHED, response));
    }

}
