# Postman Collection Notes - Audit Fields

## Summary
The NEC RBAC Postman collection examples are already correctly configured for audit field values.

## Key Points

### 1. Create Role Request
```json
{
  "roleName": "Administrator",
  "description": "Full system access for NEC administrators",
  "status": "ACTIVE",
  "isParentRole": true,
  "parentRoleId": null,
  "approvalLimit": null,
  "createdByUserId": 1
}
```
- ✅ `createdByUserId` is numeric (Long)
- ✅ Correct format for API

### 2. Update Role Request
```json
{
  "roleName": "Administrator",
  "description": "Updated description",
  "status": "ACTIVE",
  "isParentRole": true,
  "parentRoleId": null,
  "approvalLimit": null,
  "modifiedByUserId": 1
}
```
- ✅ `modifiedByUserId` is numeric (Long)
- ✅ Stored as String in database (Keycloak UUID format)
- ✅ System converts numeric ID to String internally

### 3. Create User Request
```json
{
  "userName": "John Doe",
  "genderId": 1,
  "roleId": 1,
  "phone": "+255700000001",
  "email": "john.doe@nec.go.tz",
  "photoPath": "/photos/john.jpg",
  "departmentId": 1,
  "regionId": 1,
  "districtId": 1,
  "cityId": 1,
  "password": "Pass@1234",
  "passwordToBeChanged": false,
  "emailVerified": false,
  "mobileVerified": false,
  "isActive": 1,
  "createdBy": 1
}
```
- ✅ `createdBy` is numeric (Long)
- ✅ Correct format for API

## Field Type Mapping in Postman

### Role Operations
| Field | Type in Request | Type in Database | Example |
|-------|-----------------|------------------|---------|
| createdByUserId | Long | Long | 1 |
| modifiedByUserId | Long | String (UUID) | 1 (sent) → "550e8400-e29b..." (stored) |

### User Operations
| Field | Type in Request | Type in Database | Example |
|-------|-----------------|------------------|---------|
| createdBy | Long | Long | 1 |
| updatedBy | Long | String (UUID) | 1 (sent) → "550e8400-e29b..." (stored) |

## Important Notes

1. **No changes required** to existing Postman request bodies
2. **Type conversion** happens automatically on the server side
3. **Default System User**: If a Keycloak UUID cannot be converted, system uses user ID 1 as fallback
4. **Hybrid ID System**: The application supports both numeric IDs and Keycloak UUIDs internally

## Testing Workflow

1. **Create Role**: Run "Create Role" endpoint
   - Server stores `createdByUserId` as Long (1)
   
2. **Update Role**: Run "Update Role" endpoint
   - Server stores `modifiedByUserId` as String (Keycloak UUID or "1" if numeric)

3. **Create User**: Run "Create User" endpoint
   - Server stores `createdBy` as Long (1)

4. **Login**: Authenticate to get Keycloak token
   - Subsequent operations use Keycloak UUID for audit trail

## Variables Used

The Postman collection uses these variables for audit fields:
- `{{createdByUserId}}` - Numeric user ID (default: 1)
- `{{modifiedByUserId}}` - Numeric user ID for updates (default: 1)

These are extracted from responses and auto-captured by test scripts.

## No Action Required

✅ The Postman collection is **already compatible** with the audit field changes.
All numeric values in request bodies are correctly formatted and will be handled appropriately by the API.

