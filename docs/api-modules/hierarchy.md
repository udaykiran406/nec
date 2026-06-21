# Module Hierarchy API - Comprehensive Request/Response/Error Documentation

---

## Table of Contents
1. [POST - Save Module Hierarchy](#post---save-module-hierarchy)
2. [GET - Retrieve All Module Hierarchies](#get---retrieve-all-module-hierarchies)
3. [GET - Retrieve Specific Module Hierarchy](#get---retrieve-specific-module-hierarchy)

---

## POST - Save Module Hierarchy

**Endpoint:** `/api/v1/rbac/saveModuleHierarchy`

### REQUEST

#### Save Complete Module Hierarchy Request
```json
{
  "modules": [
    {
      "moduleId": null,
      "moduleCode": "HR",
      "moduleName": "Human Resource",
      "description": "Human Resource Management Module",
      "displayOrder": 1,
      "status": "ACTIVE",
      "createdByUserId": 1,
      "groups": [
        {
          "groupId": null,
          "groupCode": "EMP_MGT",
          "groupName": "Employee Management",
          "description": "Employee management operations",
          "displayOrder": 1,
          "status": "ACTIVE",
          "createdByUserId": 1,
          "permissions": [
            {
              "permissionId": null,
              "permissionCode": "CREATE_EMPLOYEE",
              "permissionName": "Create Employee",
              "description": "Create new employee record",
              "displayOrder": 1,
              "status": "ACTIVE",
              "isSideMenu": true,
              "createdByUserId": 1
            },
            {
              "permissionId": null,
              "permissionCode": "EDIT_EMPLOYEE",
              "permissionName": "Edit Employee",
              "description": "Edit employee information",
              "displayOrder": 2,
              "status": "ACTIVE",
              "isSideMenu": true,
              "createdByUserId": 1
            },
            {
              "permissionId": null,
              "permissionCode": "DELETE_EMPLOYEE",
              "permissionName": "Delete Employee",
              "description": "Delete employee record",
              "displayOrder": 3,
              "status": "ACTIVE",
              "isSideMenu": false,
              "createdByUserId": 1
            }
          ]
        },
        {
          "groupId": null,
          "groupCode": "LEAVE_MGT",
          "groupName": "Leave Management",
          "description": "Leave request and approval",
          "displayOrder": 2,
          "status": "ACTIVE",
          "createdByUserId": 1,
          "permissions": [
            {
              "permissionId": null,
              "permissionCode": "REQUEST_LEAVE",
              "permissionName": "Request Leave",
              "description": "Request leave from work",
              "displayOrder": 1,
              "status": "ACTIVE",
              "isSideMenu": true,
              "createdByUserId": 1
            },
            {
              "permissionId": null,
              "permissionCode": "APPROVE_LEAVE",
              "permissionName": "Approve Leave",
              "description": "Approve leave requests",
              "displayOrder": 2,
              "status": "ACTIVE",
              "isSideMenu": true,
              "createdByUserId": 1
            }
          ]
        }
      ]
    },
    {
      "moduleId": null,
      "moduleCode": "FIN",
      "moduleName": "Finance",
      "description": "Financial Module",
      "displayOrder": 2,
      "status": "ACTIVE",
      "createdByUserId": 1,
      "groups": [
        {
          "groupId": null,
          "groupCode": "PAYMENT",
          "groupName": "Payment Management",
          "description": "Payment processing",
          "displayOrder": 1,
          "status": "ACTIVE",
          "createdByUserId": 1,
          "permissions": [
            {
              "permissionId": null,
              "permissionCode": "CREATE_PAYMENT",
              "permissionName": "Create Payment",
              "description": "Create new payment request",
              "displayOrder": 1,
              "status": "ACTIVE",
              "isSideMenu": true,
              "createdByUserId": 1
            },
            {
              "permissionId": null,
              "permissionCode": "APPROVE_PAYMENT",
              "permissionName": "Approve Payment",
              "description": "Approve payment requests",
              "displayOrder": 2,
              "status": "ACTIVE",
              "isSideMenu": true,
              "createdByUserId": 1
            }
          ]
        }
      ]
    }
  ]
}
```

### RESPONSE

#### Save Module Hierarchy Success Response (HTTP 201 Created)
```json
{
  "status": "SUCCESS",
  "message": "Modules along with groups and permissions added successfully",
  "data": {
    "modules": [
      {
        "moduleId": 1,
        "moduleCode": "HR",
        "moduleName": "Human Resource",
        "description": "Human Resource Management Module",
        "displayOrder": 1,
        "status": "ACTIVE",
        "createdByUserId": 1,
        "groups": [
          {
            "groupId": 101,
            "groupCode": "EMP_MGT",
            "groupName": "Employee Management",
            "description": "Employee management operations",
            "displayOrder": 1,
            "status": "ACTIVE",
            "createdByUserId": 1,
            "permissions": [
              {
                "permissionId": 10001,
                "permissionCode": "CREATE_EMPLOYEE",
                "permissionName": "Create Employee",
                "description": "Create new employee record",
                "displayOrder": 1,
                "status": "ACTIVE",
                "isSideMenu": true,
                "createdByUserId": 1
              },
              {
                "permissionId": 10002,
                "permissionCode": "EDIT_EMPLOYEE",
                "permissionName": "Edit Employee",
                "description": "Edit employee information",
                "displayOrder": 2,
                "status": "ACTIVE",
                "isSideMenu": true,
                "createdByUserId": 1
              },
              {
                "permissionId": 10003,
                "permissionCode": "DELETE_EMPLOYEE",
                "permissionName": "Delete Employee",
                "description": "Delete employee record",
                "displayOrder": 3,
                "status": "ACTIVE",
                "isSideMenu": false,
                "createdByUserId": 1
              }
            ]
          },
          {
            "groupId": 102,
            "groupCode": "LEAVE_MGT",
            "groupName": "Leave Management",
            "description": "Leave request and approval",
            "displayOrder": 2,
            "status": "ACTIVE",
            "createdByUserId": 1,
            "permissions": [
              {
                "permissionId": 10004,
                "permissionCode": "REQUEST_LEAVE",
                "permissionName": "Request Leave",
                "description": "Request leave from work",
                "displayOrder": 1,
                "status": "ACTIVE",
                "isSideMenu": true,
                "createdByUserId": 1
              },
              {
                "permissionId": 10005,
                "permissionCode": "APPROVE_LEAVE",
                "permissionName": "Approve Leave",
                "description": "Approve leave requests",
                "displayOrder": 2,
                "status": "ACTIVE",
                "isSideMenu": true,
                "createdByUserId": 1
              }
            ]
          }
        ]
      },
      {
        "moduleId": 2,
        "moduleCode": "FIN",
        "moduleName": "Finance",
        "description": "Financial Module",
        "displayOrder": 2,
        "status": "ACTIVE",
        "createdByUserId": 1,
        "groups": [
          {
            "groupId": 201,
            "groupCode": "PAYMENT",
            "groupName": "Payment Management",
            "description": "Payment processing",
            "displayOrder": 1,
            "status": "ACTIVE",
            "createdByUserId": 1,
            "permissions": [
              {
                "permissionId": 20001,
                "permissionCode": "CREATE_PAYMENT",
                "permissionName": "Create Payment",
                "description": "Create new payment request",
                "displayOrder": 1,
                "status": "ACTIVE",
                "isSideMenu": true,
                "createdByUserId": 1
              },
              {
                "permissionId": 20002,
                "permissionCode": "APPROVE_PAYMENT",
                "permissionName": "Approve Payment",
                "description": "Approve payment requests",
                "displayOrder": 2,
                "status": "ACTIVE",
                "isSideMenu": true,
                "createdByUserId": 1
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

#### Validation Error - Missing Modules List (HTTP 400 Bad Request)
```json
{
  "status": "VALIDATION_ERROR",
  "message": "Validation Error",
  "errors": [
    {
      "field": "modules",
      "message": "At least one module must be provided."
    }
  ],
  "timestamp": "2026-06-11T10:30:00"
}
```

#### Validation Error - Missing Groups List (HTTP 400 Bad Request)
```json
{
  "status": "VALIDATION_ERROR",
  "message": "Validation Error",
  "errors": [
    {
      "field": "groups",
      "message": "At least one permission group must be provided."
    }
  ],
  "timestamp": "2026-06-11T10:30:00"
}
```

#### Validation Error - Missing Permissions List (HTTP 400 Bad Request)
```json
{
  "status": "VALIDATION_ERROR",
  "message": "Validation Error",
  "errors": [
    {
      "field": "permissions",
      "message": "At least one permission must be provided."
    }
  ],
  "timestamp": "2026-06-11T10:30:00"
}
```

#### Validation Error - Invalid Field Value (HTTP 400 Bad Request)
```json
{
  "status": "VALIDATION_ERROR",
  "message": "Validation Error",
  "errors": [
    {
      "field": "status",
      "message": "Status must be 'ACTIVE' or 'INACTIVE'."
    },
    {
      "field": "displayOrder",
      "message": "Display Order must be non-negative."
    }
  ],
  "timestamp": "2026-06-11T10:30:00"
}
```

#### Duplicate Module Code Error (HTTP 409 Conflict)
```json
{
  "status": "ERROR",
  "message": "Module Code already exists. Please use another one.",
  "errorCode": "DUPLICATE_MODULE_CODE",
  "timestamp": "2026-06-11T10:30:00"
}
```

#### Duplicate Group Code Error (HTTP 409 Conflict)
```json
{
  "status": "ERROR",
  "message": "Group Code already exists. Please use another one.",
  "errorCode": "DUPLICATE_GROUP_CODE",
  "timestamp": "2026-06-11T10:30:00"
}
```

#### Duplicate Permission Code Error (HTTP 409 Conflict)
```json
{
  "status": "ERROR",
  "message": "Permission Code already exists. Please use another one.",
  "errorCode": "DUPLICATE_PERMISSION_CODE",
  "timestamp": "2026-06-11T10:30:00"
}
```

#### Module Not Found Error (HTTP 404 Not Found)
```json
{
  "status": "ERROR",
  "message": "The requested module could not be found.",
  "errorCode": "NOT_FOUND",
  "timestamp": "2026-06-11T10:30:00"
}
```

#### Transaction Failure Error (HTTP 500 Internal Server Error)
```json
{
  "status": "ERROR",
  "message": "Failed to save module hierarchy",
  "errorCode": "DATABASE_ERROR",
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

## GET - Retrieve All Module Hierarchies

**Endpoint:** `/api/v1/rbac/moduleHierarchy/all`

### REQUEST

No request body.

Example: `GET /api/v1/rbac/moduleHierarchy/all`

### RESPONSE

#### Success Response (HTTP 200 OK)
```json
{
  "status": "SUCCESS",
  "message": "Module hierarchy fetched successfully",
  "data": {
    "modules": [
      {
        "moduleId": 1,
        "moduleCode": "HR",
        "moduleName": "Human Resource",
        "description": "Human Resource Management Module",
        "displayOrder": 1,
        "status": "ACTIVE",
        "createdByUserId": 1,
        "groups": [
          {
            "groupId": 101,
            "groupCode": "EMP_MGT",
            "groupName": "Employee Management",
            "description": "Employee management operations",
            "displayOrder": 1,
            "status": "ACTIVE",
            "createdByUserId": 1,
            "permissions": [
              {
                "permissionId": 10001,
                "permissionCode": "CREATE_EMPLOYEE",
                "permissionName": "Create Employee",
                "description": "Create new employee record",
                "displayOrder": 1,
                "status": "ACTIVE",
                "isSideMenu": true,
                "createdByUserId": 1
              }
            ]
          }
        ]
      }
    ]
  }
}
```

#### Empty List Response (HTTP 200 OK)
```json
{
  "status": "SUCCESS",
  "message": "Module hierarchy fetched successfully",
  "data": {
    "modules": []
  }
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

## GET - Retrieve Specific Module Hierarchy

**Endpoint:** `/api/v1/rbac/moduleHierarchy/{moduleId}`

### REQUEST

No request body. URL parameter required: `moduleId` (positive integer)

Example: `GET /api/v1/rbac/moduleHierarchy/1`

### RESPONSE

#### Success Response (HTTP 200 OK)
```json
{
  "status": "SUCCESS",
  "message": "Module hierarchy fetched successfully",
  "data": {
    "modules": [
      {
        "moduleId": 1,
        "moduleCode": "HR",
        "moduleName": "Human Resource",
        "description": "Human Resource Management Module",
        "displayOrder": 1,
        "status": "ACTIVE",
        "createdByUserId": 1,
        "groups": [
          {
            "groupId": 101,
            "groupCode": "EMP_MGT",
            "groupName": "Employee Management",
            "description": "Employee management operations",
            "displayOrder": 1,
            "status": "ACTIVE",
            "createdByUserId": 1,
            "permissions": [
              {
                "permissionId": 10001,
                "permissionCode": "CREATE_EMPLOYEE",
                "permissionName": "Create Employee",
                "description": "Create new employee record",
                "displayOrder": 1,
                "status": "ACTIVE",
                "isSideMenu": true,
                "createdByUserId": 1
              },
              {
                "permissionId": 10002,
                "permissionCode": "EDIT_EMPLOYEE",
                "permissionName": "Edit Employee",
                "description": "Edit employee information",
                "displayOrder": 2,
                "status": "ACTIVE",
                "isSideMenu": true,
                "createdByUserId": 1
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

#### Validation Error - Invalid Module ID (HTTP 400 Bad Request)
```json
{
  "status": "VALIDATION_ERROR",
  "message": "Validation Error",
  "errors": [
    {
      "field": "moduleId",
      "message": "Module ID must be a positive number."
    }
  ],
  "timestamp": "2026-06-11T10:30:00"
}
```

#### Module Not Found (HTTP 404 Not Found)
```json
{
  "status": "ERROR",
  "message": "The requested module could not be found.",
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

### Save Complete Module Hierarchy
```bash
curl -X POST http://localhost:8080/api/v1/rbac/saveModuleHierarchy \
  -H "Content-Type: application/json" \
  -d '{
    "modules": [
      {
        "moduleCode": "HR",
        "moduleName": "Human Resource",
        "description": "HR Module",
        "displayOrder": 1,
        "status": "ACTIVE",
        "createdByUserId": 1,
        "groups": [
          {
            "groupCode": "EMP_MGT",
            "groupName": "Employee Management",
            "displayOrder": 1,
            "status": "ACTIVE",
            "createdByUserId": 1,
            "permissions": [
              {
                "permissionCode": "CREATE_EMP",
                "permissionName": "Create Employee",
                "displayOrder": 1,
                "status": "ACTIVE",
                "isSideMenu": true,
                "createdByUserId": 1
              }
            ]
          }
        ]
      }
    ]
  }'
```

### Get All Module Hierarchies
```bash
curl -X GET http://localhost:8080/api/v1/rbac/moduleHierarchy/all \
  -H "Content-Type: application/json"
```

### Get Specific Module Hierarchy
```bash
curl -X GET http://localhost:8080/api/v1/rbac/moduleHierarchy/1 \
  -H "Content-Type: application/json"
```

---

## Field Definitions

### Module Level Fields
| Field | Data Type | Mandatory | Max Length | Description |
|:---|:---|:---:|---:|:---|
| `moduleId` | Long | ❌ | - | Module ID (null for new, value for update) |
| `moduleCode` | String | ✅ | 50 | Unique module code (e.g., HR, FIN, PROC) |
| `moduleName` | String | ✅ | 100 | Human-readable module name |
| `description` | String | ❌ | 500 | Module description |
| `displayOrder` | Integer | ✅ | - | Display order (0 or positive) |
| `status` | String | ✅ | - | Status: 'ACTIVE' or 'INACTIVE' |
| `createdByUserId` | Long | ✅ | - | ID of user creating module |
| `groups` | List | ✅ | - | List of permission groups in this module |

### Group Level Fields
| Field | Data Type | Mandatory | Max Length | Description |
|:---|:---|:---:|---:|:---|
| `groupId` | Long | ❌ | - | Group ID (null for new, value for update) |
| `groupCode` | String | ✅ | 50 | Unique group code within module |
| `groupName` | String | ✅ | 100 | Human-readable group name |
| `description` | String | ❌ | 500 | Group description |
| `displayOrder` | Integer | ✅ | - | Display order (0 or positive) |
| `status` | String | ✅ | - | Status: 'ACTIVE' or 'INACTIVE' |
| `createdByUserId` | Long | ✅ | - | ID of user creating group |
| `permissions` | List | ✅ | - | List of permissions in this group |

### Permission Level Fields
| Field | Data Type | Mandatory | Max Length | Description |
|:---|:---|:---:|---:|:---|
| `permissionId` | Long | ❌ | - | Permission ID (null for new, value for update) |
| `permissionCode` | String | ✅ | 50 | Unique permission code within module |
| `permissionName` | String | ✅ | 100 | Human-readable permission name |
| `description` | String | ❌ | 500 | Permission description |
| `displayOrder` | Integer | ✅ | - | Display order (0 or positive) |
| `status` | String | ✅ | - | Status: 'ACTIVE' or 'INACTIVE' |
| `isSideMenu` | Boolean | ❌ | - | Whether to display in side menu (default: false) |
| `createdByUserId` | Long | ✅ | - | ID of user creating permission |

---

## HTTP Status Codes

| Status Code | Meaning | Scenario |
|:---|:---|:---|
| `201` | Created | Successful POST creation of module hierarchy |
| `200` | OK | Successful GET retrieval of hierarchy data |
| `400` | Bad Request | Validation error, missing required fields, empty lists |
| `404` | Not Found | Module/Group/Permission not found (during retrieve) |
| `409` | Conflict | Duplicate code or unique constraint violation |
| `422` | Unprocessable Entity | Business rule violation |
| `500` | Internal Server Error | Database error, transaction failure, unexpected error |

---

## Notes

- Module Hierarchy operations are **atomic** - all-or-nothing
- Nested structure can be multiple levels deep
- IDs starting with null are treated as inserts; existing IDs trigger updates
- Display order determines sequencing in UI components
- Side menu flag helps control which permissions appear in user interfaces
- All hierarchical data should be consistent before save
- Retrieved data is filtered to exclude soft-deleted items
- Codes should follow consistent naming conventions for UI generation
- All timestamps are in ISO 8601 format (e.g., `2026-06-11T10:30:00`)

