# Role Permissions API - Comprehensive Request/Response/Error Documentation

---

## Table of Contents
1. [POST - Create/Update Role Permissions](#post---createupdate-role-permissions)
2. [GET - Retrieve Role Permissions by ID](#get---retrieve-role-permissions-by-id)
3. [GET - Retrieve All Role Permissions](#get---retrieve-all-role-permissions)
4. [DELETE - Delete Role Permission](#delete---delete-role-permission)

---

## POST - Create/Update Role Permissions

**Endpoint:** `/api/v1/rbac/role-permission/save?createdByUserId=1`

### REQUEST

#### Create Role-Permission Mappings Request
```json
{
  "roleName": "Manager",
  "modules": [
    {
      "moduleId": 1,
      "moduleName": "Human Resource",
      "groups": [
        {
          "groupId": 101,
          "groupName": "Employee Management",
          "permissions": [
            {
              "permissionId": 10001,
              "permissionName": "Create Employee"
            },
            {
              "permissionId": 10002,
              "permissionName": "Edit Employee"
            }
          ]
        },
        {
          "groupId": 102,
          "groupName": "Leave Management",
          "permissions": [
            {
              "permissionId": 10004,
              "permissionName": "Request Leave"
            },
            {
              "permissionId": 10005,
              "permissionName": "Approve Leave"
            }
          ]
        }
      ]
    },
    {
      "moduleId": 2,
      "moduleName": "Finance",
      "groups": [
        {
          "groupId": 201,
          "groupName": "Payment Management",
          "permissions": [
            {
              "permissionId": 20001,
              "permissionName": "Create Payment"
            },
            {
              "permissionId": 20002,
              "permissionName": "Approve Payment"
            }
          ]
        }
      ]
    }
  ]
}
```

#### Update Role-Permission Mapping Status Request
```json
{
  "rolePermissionId": 1001,
  "roleName": "Manager"
}
```

### RESPONSE

#### Create Role-Permission Mappings Success Response (HTTP 201 Created)
```json
{
  "status": "SUCCESS",
  "message": "Role-permission mappings created successfully",
  "data": [
    {
      "rolePermissionId": 1001,
      "roleId": 2,
      "roleName": "Manager",
      "moduleId": 1,
      "moduleName": "Human Resource",
      "groupId": 101,
      "groupName": "Employee Management",
      "permissionId": 10001,
      "permissionName": "Create Employee",
      "status": "ACTIVE",
      "createdByUserId": 1,
      "createdDate": "2026-06-11T10:30:00",
      "modifiedByUserId": null,
      "modifiedDate": "2026-06-11T10:30:00"
    },
    {
      "rolePermissionId": 1002,
      "roleId": 2,
      "roleName": "Manager",
      "moduleId": 1,
      "moduleName": "Human Resource",
      "groupId": 101,
      "groupName": "Employee Management",
      "permissionId": 10002,
      "permissionName": "Edit Employee",
      "status": "ACTIVE",
      "createdByUserId": 1,
      "createdDate": "2026-06-11T10:30:00",
      "modifiedByUserId": null,
      "modifiedDate": "2026-06-11T10:30:00"
    },
    {
      "rolePermissionId": 1003,
      "roleId": 2,
      "roleName": "Manager",
      "moduleId": 1,
      "moduleName": "Human Resource",
      "groupId": 102,
      "groupName": "Leave Management",
      "permissionId": 10004,
      "permissionName": "Request Leave",
      "status": "ACTIVE",
      "createdByUserId": 1,
      "createdDate": "2026-06-11T10:30:00",
      "modifiedByUserId": null,
      "modifiedDate": "2026-06-11T10:30:00"
    },
    {
      "rolePermissionId": 1004,
      "roleId": 2,
      "roleName": "Manager",
      "moduleId": 1,
      "moduleName": "Human Resource",
      "groupId": 102,
      "groupName": "Leave Management",
      "permissionId": 10005,
      "permissionName": "Approve Leave",
      "status": "ACTIVE",
      "createdByUserId": 1,
      "createdDate": "2026-06-11T10:30:00",
      "modifiedByUserId": null,
      "modifiedDate": "2026-06-11T10:30:00"
    },
    {
      "rolePermissionId": 1005,
      "roleId": 2,
      "roleName": "Manager",
      "moduleId": 2,
      "moduleName": "Finance",
      "groupId": 201,
      "groupName": "Payment Management",
      "permissionId": 20001,
      "permissionName": "Create Payment",
      "status": "ACTIVE",
      "createdByUserId": 1,
      "createdDate": "2026-06-11T10:30:00",
      "modifiedByUserId": null,
      "modifiedDate": "2026-06-11T10:30:00"
    },
    {
      "rolePermissionId": 1006,
      "roleId": 2,
      "roleName": "Manager",
      "moduleId": 2,
      "moduleName": "Finance",
      "groupId": 201,
      "groupName": "Payment Management",
      "permissionId": 20002,
      "permissionName": "Approve Payment",
      "status": "ACTIVE",
      "createdByUserId": 1,
      "createdDate": "2026-06-11T10:30:00",
      "modifiedByUserId": null,
      "modifiedDate": "2026-06-11T10:30:00"
    }
  ]
}
```

#### Update Role-Permission Mapping Status Success Response (HTTP 200 OK)
```json
{
  "status": "SUCCESS",
  "message": "Role-permission mapping updated successfully",
  "data": {
    "rolePermissionId": 1001,
    "roleId": 2,
    "roleName": "Manager",
    "moduleId": 1,
    "moduleName": "Human Resource",
    "groupId": 101,
    "groupName": "Employee Management",
    "permissionId": 10001,
    "permissionName": "Create Employee",
    "status": "ACTIVE",
    "createdByUserId": 1,
    "createdDate": "2026-06-11T10:30:00",
    "modifiedByUserId": 2,
    "modifiedDate": "2026-06-11T15:00:00"
  }
}
```

### ERROR

#### Validation Error - Missing Role Name (HTTP 400 Bad Request)
```json
{
  "status": "VALIDATION_ERROR",
  "message": "Validation Error",
  "errors": [
    {
      "field": "roleName",
      "message": "Role Name is missing."
    }
  ],
  "timestamp": "2026-06-11T10:30:00"
}
```

#### Validation Error - Empty Module List (HTTP 400 Bad Request)
```json
{
  "status": "VALIDATION_ERROR",
  "message": "Validation Error",
  "errors": [
    {
      "field": "modules",
      "message": "Module list is missing."
    }
  ],
  "timestamp": "2026-06-11T10:30:00"
}
```

#### Validation Error - Empty Group List (HTTP 400 Bad Request)
```json
{
  "status": "VALIDATION_ERROR",
  "message": "Validation Error",
  "errors": [
    {
      "field": "groups",
      "message": "Group list is missing."
    }
  ],
  "timestamp": "2026-06-11T10:30:00"
}
```

#### Validation Error - Empty Permission List (HTTP 400 Bad Request)
```json
{
  "status": "VALIDATION_ERROR",
  "message": "Validation Error",
  "errors": [
    {
      "field": "permissions",
      "message": "Permission list is missing."
    }
  ],
  "timestamp": "2026-06-11T10:30:00"
}
```

#### Validation Error - Invalid Module ID (HTTP 400 Bad Request)
```json
{
  "status": "VALIDATION_ERROR",
  "message": "Validation Error",
  "errors": [
    {
      "field": "moduleId",
      "message": "Module Id must be a positive number."
    }
  ],
  "timestamp": "2026-06-11T10:30:00"
}
```

#### Role Not Found (HTTP 404 Not Found)
```json
{
  "status": "ERROR",
  "message": "The requested role could not be found.",
  "errorCode": "NOT_FOUND",
  "timestamp": "2026-06-11T10:30:00"
}
```

#### Invalid Module Reference (HTTP 400 Bad Request)
```json
{
  "status": "ERROR",
  "message": "The selected reference is not valid.",
  "errorCode": "INVALID_REFERENCE",
  "timestamp": "2026-06-11T10:30:00"
}
```

#### Invalid Group Reference (HTTP 400 Bad Request)
```json
{
  "status": "ERROR",
  "message": "The selected reference is not valid.",
  "errorCode": "INVALID_REFERENCE",
  "timestamp": "2026-06-11T10:30:00"
}
```

#### Invalid Permission Reference (HTTP 400 Bad Request)
```json
{
  "status": "ERROR",
  "message": "The selected reference is not valid.",
  "errorCode": "INVALID_REFERENCE",
  "timestamp": "2026-06-11T10:30:00"
}
```

#### Database Error (HTTP 500 Internal Server Error)
```json
{
  "status": "ERROR",
  "message": "A database error occurred. Please try again later.",
  "errorCode": "DATABASE_ERROR",
  "timestamp": "2026-06-11T10:30:00"
}
```

#### Internal Server Error (HTTP 500 Internal Server Error)
```json
{
  "status": "ERROR",
  "message": "An unexpected error occurred. Please contact support if the problem persists.",
  "errorCode": "INTERNAL_SERVER_ERROR",
  "timestamp": "2026-06-11T10:30:00"
}
```

#### Concurrent Update Error (HTTP 409 Conflict)
```json
{
  "status": "ERROR",
  "message": "The information was updated by another user. Please refresh and try again.",
  "errorCode": "CONCURRENT_UPDATE",
  "timestamp": "2026-06-11T10:30:00"
}
```

---

## GET - Retrieve Role Permissions by ID

**Endpoint:** `/api/v1/rbac/role-permission/{rolePermissionId}`

### REQUEST

No request body. URL parameter required: `rolePermissionId` (positive integer)

Example: `GET /api/v1/rbac/role-permission/1001`

### RESPONSE

#### Success Response (HTTP 200 OK)
Returns all permissions for the role in hierarchical format grouped by Module -> Group -> Permissions.

```json
{
  "status": "SUCCESS",
  "message": "Role-permission mapping fetched successfully",
  "data": {
    "roleId": 2,
    "roleName": "Manager",
    "modules": [
      {
        "moduleId": 1,
        "moduleCode": "HR",
        "moduleName": "Human Resource",
        "groups": [
          {
            "groupId": 101,
            "groupCode": "EMP_MGT",
            "groupName": "Employee Management",
            "permissions": [
              {
                "rolePermissionId": 1001,
                "permissionId": 10001,
                "permissionCode": "CREATE_EMPLOYEE",
                "permissionName": "Create Employee",
                "status": "ACTIVE"
              },
              {
                "rolePermissionId": 1002,
                "permissionId": 10002,
                "permissionCode": "EDIT_EMPLOYEE",
                "permissionName": "Edit Employee",
                "status": "ACTIVE"
              }
            ]
          },
          {
            "groupId": 102,
            "groupCode": "LEAVE_MGT",
            "groupName": "Leave Management",
            "permissions": [
              {
                "rolePermissionId": 1003,
                "permissionId": 10004,
                "permissionCode": "REQUEST_LEAVE",
                "permissionName": "Request Leave",
                "status": "ACTIVE"
              }
            ]
          }
        ]
      },
      {
        "moduleId": 2,
        "moduleCode": "FIN",
        "moduleName": "Finance",
        "groups": [
          {
            "groupId": 201,
            "groupCode": "PAYMENT",
            "groupName": "Payment Management",
            "permissions": [
              {
                "rolePermissionId": 1005,
                "permissionId": 20001,
                "permissionCode": "CREATE_PAYMENT",
                "permissionName": "Create Payment",
                "status": "ACTIVE"
              }
            ]
          }
        ]
      }
    ]
  }
}
```

### ERROR

#### Validation Error - Invalid Role Permission ID (HTTP 400 Bad Request)
```json
{
  "status": "VALIDATION_ERROR",
  "message": "Validation Error",
  "errors": [
    {
      "field": "rolePermissionId",
      "message": "Role Permission ID must be a positive number."
    }
  ],
  "timestamp": "2026-06-11T10:30:00"
}
```

#### Role-Permission Mapping Not Found (HTTP 404 Not Found)
```json
{
  "status": "ERROR",
  "message": "The requested information could not be found.",
  "errorCode": "NOT_FOUND",
  "timestamp": "2026-06-11T10:30:00"
}
```

#### Database Error (HTTP 500 Internal Server Error)
```json
{
  "status": "ERROR",
  "message": "A database error occurred. Please try again later.",
  "errorCode": "DATABASE_ERROR",
  "timestamp": "2026-06-11T10:30:00"
}
```

---

## GET - Retrieve All Role Permissions

**Endpoint:** `/api/v1/rbac/role-permission/get-all`

### REQUEST

No request body.

Example: `GET /api/v1/rbac/role-permission/get-all`

### RESPONSE

#### Success Response (HTTP 200 OK)
Returns all permissions for all roles in hierarchical format grouped by Role -> Module -> Group -> Permissions.

```json
{
  "status": "SUCCESS",
  "message": "Role-permission mappings fetched successfully",
  "data": [
    {
      "roleId": 2,
      "roleName": "Manager",
      "modules": [
        {
          "moduleId": 1,
          "moduleCode": "HR",
          "moduleName": "Human Resource",
          "groups": [
            {
              "groupId": 101,
              "groupCode": "EMP_MGT",
              "groupName": "Employee Management",
              "permissions": [
                {
                  "rolePermissionId": 1001,
                  "permissionId": 10001,
                  "permissionCode": "CREATE_EMPLOYEE",
                  "permissionName": "Create Employee",
                  "status": "ACTIVE"
                },
                {
                  "rolePermissionId": 1002,
                  "permissionId": 10002,
                  "permissionCode": "EDIT_EMPLOYEE",
                  "permissionName": "Edit Employee",
                  "status": "ACTIVE"
                }
              ]
            }
          ]
        }
      ]
    },
    {
      "roleId": 1,
      "roleName": "Admin",
      "modules": [
        {
          "moduleId": 1,
          "moduleCode": "HR",
          "moduleName": "Human Resource",
          "groups": [
            {
              "groupId": 101,
              "groupCode": "EMP_MGT",
              "groupName": "Employee Management",
              "permissions": [
                {
                  "rolePermissionId": 1003,
                  "permissionId": 10001,
                  "permissionCode": "CREATE_EMPLOYEE",
                  "permissionName": "Create Employee",
                  "status": "ACTIVE"
                },
                {
                  "rolePermissionId": 1004,
                  "permissionId": 10002,
                  "permissionCode": "EDIT_EMPLOYEE",
                  "permissionName": "Edit Employee",
                  "status": "ACTIVE"
                },
                {
                  "rolePermissionId": 1005,
                  "permissionId": 10003,
                  "permissionCode": "DELETE_EMPLOYEE",
                  "permissionName": "Delete Employee",
                  "status": "ACTIVE"
                }
              ]
            }
          ]
        }
      ]
    }
  ]
}
```

#### Empty List Response (HTTP 200 OK)
```json
{
  "status": "SUCCESS",
  "message": "Role-permission mappings fetched successfully",
  "data": []
}
```

### ERROR

#### Database Error (HTTP 500 Internal Server Error)
```json
{
  "status": "ERROR",
  "message": "A database error occurred. Please try again later.",
  "errorCode": "DATABASE_ERROR",
  "timestamp": "2026-06-11T10:30:00"
}
```

#### Internal Server Error (HTTP 500 Internal Server Error)
```json
{
  "status": "ERROR",
  "message": "An unexpected error occurred. Please contact support if the problem persists.",
  "errorCode": "INTERNAL_SERVER_ERROR",
  "timestamp": "2026-06-11T10:30:00"
}
```

---

## DELETE - Delete Role Permission

**Endpoint:** `/api/v1/rbac/role-permission/{rolePermissionId}?modifiedByUserId=2`

### REQUEST

Query Parameter:
- `modifiedByUserId` (long, optional): User ID performing the deletion

No request body.

Example: `DELETE /api/v1/rbac/role-permission/1001?modifiedByUserId=2`

### RESPONSE

#### Success Response (HTTP 200 OK)
```json
{
  "status": "SUCCESS",
  "message": "Role-permission mapping deleted successfully",
  "data": null
}
```

### ERROR

#### Validation Error - Invalid Role Permission ID (HTTP 400 Bad Request)
```json
{
  "status": "VALIDATION_ERROR",
  "message": "Validation Error",
  "errors": [
    {
      "field": "rolePermissionId",
      "message": "Role Permission ID must be a positive number."
    }
  ],
  "timestamp": "2026-06-11T10:30:00"
}
```

#### Role-Permission Mapping Not Found (HTTP 404 Not Found)
```json
{
  "status": "ERROR",
  "message": "The requested information could not be found.",
  "errorCode": "NOT_FOUND",
  "timestamp": "2026-06-11T10:30:00"
}
```

#### Concurrent Update Error (HTTP 409 Conflict)
```json
{
  "status": "ERROR",
  "message": "The information was updated by another user. Please refresh and try again.",
  "errorCode": "CONCURRENT_UPDATE",
  "timestamp": "2026-06-11T10:30:00"
}
```

#### Database Error (HTTP 500 Internal Server Error)
```json
{
  "status": "ERROR",
  "message": "A database error occurred. Please try again later.",
  "errorCode": "DATABASE_ERROR",
  "timestamp": "2026-06-11T10:30:00"
}
```

#### Internal Server Error (HTTP 500 Internal Server Error)
```json
{
  "status": "ERROR",
  "message": "An unexpected error occurred. Please contact support if the problem persists.",
  "errorCode": "INTERNAL_SERVER_ERROR",
  "timestamp": "2026-06-11T10:30:00"
}
```

---

## cURL Examples

### Create Role-Permission Mappings
```bash
curl -X POST "http://localhost:8080/api/v1/rbac/role-permission/save?createdByUserId=1" \
  -H "Content-Type: application/json" \
  -d '{
    "roleName": "Manager",
    "modules": [
      {
        "moduleId": 1,
        "moduleName": "Human Resource",
        "groups": [
          {
            "groupId": 101,
            "groupName": "Employee Management",
            "permissions": [
              {
                "permissionId": 10001,
                "permissionName": "Create Employee"
              },
              {
                "permissionId": 10002,
                "permissionName": "Edit Employee"
              }
            ]
          }
        ]
      }
    ]
  }'
```

### Update Role-Permission Mapping Status
```bash
curl -X POST "http://localhost:8080/api/v1/rbac/role-permission/save?createdByUserId=2" \
  -H "Content-Type: application/json" \
  -d '{
    "rolePermissionId": 1001,
    "roleName": "Manager"
  }'
```

### Get Role-Permission Mapping by ID
```bash
curl -X GET http://localhost:8080/api/v1/rbac/role-permission/1001
```

### Get All Role-Permission Mappings
```bash
curl -X GET http://localhost:8080/api/v1/rbac/role-permission/get-all
```

### Delete Role-Permission Mapping
```bash
curl -X DELETE "http://localhost:8080/api/v1/rbac/role-permission/1001?modifiedByUserId=2"
```

