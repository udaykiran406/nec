package com.nec.middleware.rbacAuth.rbac.controller;

import com.nec.middleware.rbacAuth.rbac.constant.ApiMessageConstants;
import com.nec.middleware.rbacAuth.rbac.constant.RbacConstants;
import com.nec.middleware.rbacAuth.rbac.dto.request.RbacUserRequest;
import com.nec.middleware.rbacAuth.rbac.dto.request.UserListRequestDto;
import com.nec.middleware.rbacAuth.rbac.dto.response.ApiResponse;
import com.nec.middleware.rbacAuth.rbac.dto.response.PaginatedResponse;
import com.nec.middleware.rbacAuth.rbac.dto.response.RbacUserResponse;
import com.nec.middleware.rbacAuth.rbac.service.UserService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/rbac")
@AllArgsConstructor
public class RbacUserController {

    private final UserService userService;

    @PostMapping("/users")
    public ResponseEntity<ApiResponse<RbacUserResponse>> createUser(
            @Valid @RequestBody RbacUserRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.created(RbacConstants.USER_CREATED, userService.createUser(request)));
    }

    @PutMapping("/users/{id}")
    public ResponseEntity<ApiResponse<RbacUserResponse>> updateUser(
            @PathVariable String id,
            @Valid @RequestBody RbacUserRequest request) {
        return ResponseEntity.ok(ApiResponse.ok(RbacConstants.USER_UPDATED, userService.updateUser(id, request)));
    }

    @GetMapping("/users/{id}")
    public ResponseEntity<ApiResponse<RbacUserResponse>> getUserById(@PathVariable String id) {
        return ResponseEntity.ok(ApiResponse.ok(RbacConstants.USER_FETCHED, userService.getUserById(id)));
    }

    @GetMapping("/users/all")
    public ResponseEntity<ApiResponse<List<RbacUserResponse>>> getAllUsers() {
        return ResponseEntity.ok(ApiResponse.ok(RbacConstants.USERS_FETCHED, userService.getAllUsers()));
    }

    @GetMapping("/users/role/{roleId}")
    public ResponseEntity<ApiResponse<List<RbacUserResponse>>> getUsersByRoleId(@PathVariable Long roleId) {
        return ResponseEntity.ok(ApiResponse.ok(RbacConstants.USERS_FETCHED, userService.getUsersByRoleId(roleId)));
    }

    @PatchMapping("/users/{id}/status")
    public ResponseEntity<ApiResponse<RbacUserResponse>> changeUserActiveStatus(
            @PathVariable String id,
            @RequestParam Integer isActive) {
        return ResponseEntity.ok(ApiResponse.ok(RbacConstants.USER_STATUS_CHANGED,
                userService.changeUserActiveStatus(id, isActive)));
    }

    @DeleteMapping("/users/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteUser(@PathVariable String id) {
        userService.deleteUser(id);
        return ResponseEntity.ok(ApiResponse.ok(RbacConstants.USER_DELETED));
    }

    /**
     * Retrieves users with optional pagination, sorting, global search, and dynamic filters.
     */
    @PostMapping("/user/list")
    public ResponseEntity<ApiResponse<PaginatedResponse<RbacUserResponse>>> listUsers(
            @Valid @RequestBody(required = false) UserListRequestDto request) {
        return ResponseEntity.ok(ApiResponse.ok(ApiMessageConstants.DATA_FETCHED, userService.listUsers(request)));
    }

}
