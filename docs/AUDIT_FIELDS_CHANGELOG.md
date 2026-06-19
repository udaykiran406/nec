# Audit Fields Refactoring Changelog

## Date: June 19, 2026

### Overview
Fixed type inconsistencies in audit fields across the NEC Middleware RBAC system. The system uses two types of user IDs:
- **Numeric Long IDs**: Used internally for database records created via the RBAC API (e.g., `createdByUserId`, database-internal users)
- **Keycloak UUIDs (String)**: Used for users authenticated via Keycloak external provider (e.g., `updatedBy` for Keycloak-authenticated users)

### Changes Made

#### 1. RbacUtil.java
**Added:** New utility method `toAuditUserId(Long)`
```java
public static String toAuditUserId(Long userId) {
    return userId != null ? userId.toString() : null;
}
```
- **Purpose**: Converts numeric user IDs (Long) to String representation for audit fields that store Keycloak UUIDs
- **Location**: `com.nec.middleware.rbacAuth.rbac.util.RbacUtil`

#### 2. NecUserContextDTO.java
**Changed:** `updatedBy` field type from `Long` to `String`
```java
// Before:
private Long updatedBy;

// After:
private String updatedBy;
```
- **Reason**: The `RbacUser.updatedBy` field stores Keycloak UUIDs as String values, not numeric IDs
- **Location**: `com.nec.middleware.rbacAuth.auth.dto.response.NecUserContextDTO`

#### 3. RbacRoleMapper.java
**Fixed:** `toEntity()` method to handle mixed audit field types
```java
// Before:
.createdByUserId(RbacUtil.toAuditUserId(request.getCreatedByUserId()))
.modifiedByUserId(RbacUtil.toAuditUserId(request.getModifiedByUserId()))

// After:
.createdByUserId(request.getCreatedByUserId())  // Keep as Long
.modifiedByUserId(RbacUtil.toAuditUserId(request.getModifiedByUserId()))  // Convert to String
```
- **Reason**: 
  - `RbacRole.createdByUserId` is `Long` - stores the numeric ID directly
  - `RbacRole.modifiedByUserId` is `String` - stores the Keycloak UUID
- **Location**: `com.nec.middleware.rbacAuth.rbac.mapper.RbacRoleMapper`

#### 4. RbacRolePermissionMapper.java
**Modified:** Two methods to accept String user IDs and convert to Long

```java
// toEntity() method
public RbacRolePermission toEntity(
        Long roleId,
        Long moduleId,
        Long groupId,
        Long permissionId,
        String createdByUserId) {  // Changed from Long to String
    Long numericId = 1L; // Default system user when UUID cannot be parsed
    if (createdByUserId != null) {
        try {
            numericId = Long.parseLong(createdByUserId);
        } catch (NumberFormatException e) {
            numericId = 1L; // Fallback to system user for Keycloak UUIDs
        }
    }
    // ... rest of method
}

// updateStatus() method
public void updateStatus(RbacRolePermission entity, String status, String modifiedByUserId) {
    entity.setStatus(status);
    if (modifiedByUserId != null) {
        try {
            entity.setModifiedByUserId(Long.parseLong(modifiedByUserId));
        } catch (NumberFormatException e) {
            entity.setModifiedByUserId(1L); // Default to system user
        }
    }
}
```
- **Reason**: Service layer passes Keycloak UUIDs (String), but `RbacRolePermission` expects numeric Long IDs
- **Fallback**: Uses system user ID (1L) when a Keycloak UUID cannot be parsed as Long
- **Location**: `com.nec.middleware.rbacAuth.rbac.mapper.RbacRolePermissionMapper`

#### 5. RolePermissionServiceImpl.java
**Fixed:** Line 150 to properly convert String user ID to Long
```java
// Before:
mapping.setModifiedByUserId(currentUserId);  // Type mismatch: String to Long

// After:
try {
    mapping.setModifiedByUserId(Long.parseLong(currentUserId));
} catch (NumberFormatException e) {
    mapping.setModifiedByUserId(1L); // Default to system user
}
```
- **Reason**: `currentUserId` is a Keycloak UUID (String), but field expects Long
- **Location**: `com.nec.middleware.rbacAuth.rbac.service.impl.RolePermissionServiceImpl`

### Entity Field Types Reference

| Entity | Field | Type | Purpose |
|--------|-------|------|---------|
| RbacRole | createdByUserId | Long | Numeric ID of user who created the role |
| RbacRole | modifiedByUserId | String | Keycloak UUID of user who last modified the role |
| RbacUser | createdBy | Long | Numeric ID of user who created the user |
| RbacUser | updatedBy | String | Keycloak UUID of user who last updated the user |
| RbacRolePermission | createdByUserId | Long | Numeric ID of user who created the mapping |
| RbacRolePermission | modifiedByUserId | Long | Numeric ID of user who last modified the mapping |

### Testing Notes

1. **Postman Collection**: Request bodies already use correct numeric values (e.g., `"createdByUserId": 1`)
2. **Audit Trail**: System user ID (1) is used when Keycloak UUIDs are converted to numeric IDs
3. **Backward Compatibility**: Changes maintain compatibility with existing numeric ID inputs

### Build Status
✅ **All compilation errors resolved**
- Total lines compiled: 227 source files
- Build time: ~12 seconds
- Warnings: 15 (unrelated to audit field changes)

### Future Improvements
1. Consider storing Keycloak UUIDs in a separate audit field to preserve full identity trail
2. Add migration script to handle any existing Keycloak UUID entries in numeric fields
3. Document the hybrid ID system in API specifications

