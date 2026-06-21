# Roles API - Comprehensive Request/Response/Error Documentation

---

## Table of Contents
1. [POST - Create/Update Role](#post---createupdate-role)
2. [GET - Retrieve Role by ID](#get---retrieve-role-by-id)
3. [GET - Retrieve All Roles](#get---retrieve-all-roles)
4. [GET - Retrieve Roles by Status](#get---retrieve-roles-by-status)
5. [GET - Retrieve Child Roles](#get---retrieve-child-roles)
6. [DELETE - Delete Role](#delete---delete-role)

---

## POST - Create/Update Role

**Endpoint:** `/api/v1/rbac/roles/save`

> **Role code auto-generation:** Do **not** send `roleCode` when creating a role. The service generates it automatically in the format `<PREFIX><3-digit-sequence>` (e.g. `ADMIN001`, `FIN001`, `HR001`). The prefix is derived from the first word of `roleName` using a vowel-boundary algorithm. On update, `roleCode` is immutable — omit it from the request body.

| Role name | Generated prefix | Example codes |
|---|---|---|
| Administrator | ADMIN | ADMIN001, ADMIN002, … |
| Finance | FIN | FIN001, FIN002, … |
| HR Officer | HR | HR001, HR002, … |
| Procurement Manager | PROC | PROC001, … |
| Logistics Officer | LOG | LOG001, … |
| Communication Officer | COMM | COMM001, … |
| Director General | DIR | DIR001, … |
| Deputy Chairman | DEP | DEP001, … |

### REQUEST

#### Create Role Request
```json
{
  "roleName": "Administrator",
  "description": "Administrator role with full access to all modules",
  "isParentRole": true,
  "status": "ACTIVE",
  "createdByUserId": 1
}
```

#### Create Child Role Request
```json
{
  "roleName": "Manager",
  "description": "Manager role with approval authority up to 100000",
  "parentRoleId": 1,
  "approvalLimit": 100000.00,
  "isParentRole": false,
  "status": "ACTIVE",
  "createdByUserId": 1
}
```

#### Update Role Request
```json
{
  "roleId": 1,
  "roleName": "Administrator",
  "description": "Administrator role with full access to all modules and approval authority",
  "isParentRole": true,
  "approvalLimit": 500000.00,
  "status": "ACTIVE",
  "modifiedByUserId": 2
}
```

### RESPONSE

#### Create Role Success Response (HTTP 201 Created)
```json
{
  "status": "SUCCESS",
  "message": "Role created successfully",
  "data": {
    "roleId": 1,
    "roleCode": "ADMIN001",
    "roleName": "Administrator",
    "description": "Administrator role with full access to all modules",
    "parentRoleId": null,
    "approvalLimit": null,
    "isParentRole": true,
    "status": "ACTIVE",
    "isDeleted": false,
    "createdByUserId": 1,
    "createdDate": "2026-06-11T10:30:00",
    "modifiedByUserId": null,
    "modifiedDate": "2026-06-11T10:30:00"
  }
}
```

#### Create Child Role Success Response (HTTP 201 Created)
```json
{
  "status": "SUCCESS",
  "message": "Role created successfully",
  "data": {
    "roleId": 2,
    "roleCode": "MAN001",
    "roleName": "Manager",
    "description": "Manager role with approval authority up to 100000",
    "parentRoleId": 1,
    "approvalLimit": 100000.00,
    "isParentRole": false,
    "status": "ACTIVE",
    "isDeleted": false,
    "createdByUserId": 1,
    "createdDate": "2026-06-11T10:30:00",
    "modifiedByUserId": null,
    "modifiedDate": "2026-06-11T10:30:00"
  }
}
```

#### Update Role Success Response (HTTP 200 OK)
```json
{
  "status": "SUCCESS",
  "message": "Role updated successfully",
  "data": {
    "roleId": 1,
    "roleCode": "ADMIN001",
    "roleName": "Administrator",
    "description": "Administrator role with full access to all modules and approval authority",
    "parentRoleId": null,
    "approvalLimit": 500000.00,
    "isParentRole": true,
    "status": "ACTIVE",
    "isDeleted": false,
    "createdByUserId": 1,
    "createdDate": "2026-06-05T10:30:00",
    "modifiedByUserId": 2,
    "modifiedDate": "2026-06-11T14:45:00"
  }
}
```

### ERROR

#### Validation Error - Missing Required Fields (HTTP 400 Bad Request)
```json
{
  "status": "VALIDATION_ERROR",
  "message": "Validation Error",
  "errors": [
    {
      "field": "roleName",
      "message": "Role Name is required."
    },
    {
      "field": "status",
      "message": "Status is required."
    },
    {
      "field": "createdByUserId",
      "message": "Created By User ID is required."
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
      "field": "roleName",
      "message": "Role Name must be between 1 and 100 characters."
    }
  ],
  "timestamp": "2026-06-11T10:30:00"
}
```

#### Validation Error - Invalid Approval Limit (HTTP 400 Bad Request)
```json
{
  "status": "VALIDATION_ERROR",
  "message": "Validation Error",
  "errors": [
    {
      "field": "approvalLimit",
      "message": "Approval Limit must be non-negative."
    }
  ],
  "timestamp": "2026-06-11T10:30:00"
}
```

#### Duplicate Role Code Error (HTTP 409 Conflict)
```json
{
  "status": "ERROR",
  "message": "Role Code already exists. Please use another one.",
  "errorCode": "DUPLICATE_ROLE_CODE",
  "timestamp": "2026-06-11T10:30:00"
}
```

#### Duplicate Role Name Error (HTTP 409 Conflict)
```json
{
  "status": "ERROR",
  "message": "Role Name already exists. Please use another one.",
  "errorCode": "DUPLICATE_ROLE_NAME",
  "timestamp": "2026-06-11T10:30:00"
}
```

#### Invalid Parent Role Error (HTTP 400 Bad Request)
```json
{
  "status": "ERROR",
  "message": "Parent role not found with id: 999",
  "errorCode": "INVALID_REFERENCE",
  "timestamp": "2026-06-11T10:30:00"
}
```

#### Self-Reference Error (HTTP 422 Unprocessable Entity)
```json
{
  "status": "ERROR",
  "message": "A role cannot be its own parent",
  "errorCode": "BUSINESS_RULE_VIOLATION",
  "timestamp": "2026-06-11T10:30:00"
}
```

#### Role Not Found Error (HTTP 404 Not Found)
```json
{
  "status": "ERROR",
  "message": "Role not found with id: 999",
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

## GET - Retrieve Role by ID

**Endpoint:** `/api/v1/rbac/roles/{id}`

### REQUEST

No request body. URL parameter required: `id` (positive integer)

Example: `GET /api/v1/rbac/roles/1`

### RESPONSE

#### Success Response (HTTP 200 OK)
```json
{
  "status": "SUCCESS",
  "message": "Role fetched successfully",
  "data": {
    "roleId": 1,
    "roleCode": "ADMIN001",
    "roleName": "Administrator",
    "description": "Administrator role with full access to all modules",
    "parentRoleId": null,
    "approvalLimit": null,
    "isParentRole": true,
    "status": "ACTIVE",
    "isDeleted": false,
    "createdByUserId": 1,
    "createdDate": "2026-06-05T10:30:00",
    "modifiedByUserId": null,
    "modifiedDate": "2026-06-05T10:30:00"
  }
}
```

### ERROR

#### Validation Error - Invalid ID (HTTP 400 Bad Request)
```json
{
  "status": "VALIDATION_ERROR",
  "message": "Validation Error",
  "errors": [
    {
      "field": "id",
      "message": "ID must be a positive number."
    }
  ],
  "timestamp": "2026-06-11T10:30:00"
}
```

#### Role Not Found (HTTP 404 Not Found)
```json
{
  "status": "ERROR",
  "message": "Role not found with id: 999",
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

## GET - Retrieve All Roles

**Endpoint:** `/api/v1/rbac/roles/all`

### REQUEST

No request body.

Example: `GET /api/v1/rbac/roles/all`

### RESPONSE

#### Success Response (HTTP 200 OK)
```json
{
  "status": "SUCCESS",
  "message": "Roles fetched successfully",
  "data": [
    {
      "roleId": 1,
      "roleCode": "ADMIN001",
      "roleName": "Administrator",
      "description": "Administrator role with full access",
      "parentRoleId": null,
      "approvalLimit": null,
      "isParentRole": true,
      "status": "ACTIVE",
      "isDeleted": false,
      "createdByUserId": 1,
      "createdDate": "2026-06-05T10:30:00",
      "modifiedByUserId": null,
      "modifiedDate": "2026-06-05T10:30:00"
    },
    {
      "roleId": 2,
      "roleCode": "MAN001",
      "roleName": "Manager",
      "description": "Manager role with approval authority up to 100000",
      "parentRoleId": 1,
      "approvalLimit": 100000.00,
      "isParentRole": false,
      "status": "ACTIVE",
      "isDeleted": false,
      "createdByUserId": 1,
      "createdDate": "2026-06-05T11:00:00",
      "modifiedByUserId": null,
      "modifiedDate": "2026-06-05T11:00:00"
    },
    {
      "roleId": 3,
      "roleCode": "VIE001",
      "roleName": "Viewer",
      "description": "Read-only access role",
      "parentRoleId": null,
      "approvalLimit": null,
      "isParentRole": false,
      "status": "ACTIVE",
      "isDeleted": false,
      "createdByUserId": 1,
      "createdDate": "2026-06-05T12:00:00",
      "modifiedByUserId": null,
      "modifiedDate": "2026-06-05T12:00:00"
    }
  ]
}
```

#### Empty List Response (HTTP 200 OK)
```json
{
  "status": "SUCCESS",
  "message": "Roles fetched successfully",
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

## GET - Retrieve Roles by Status

**Endpoint:** `/api/v1/rbac/roles/status/{status}`

### REQUEST

No request body. URL parameter required: `status` (ACTIVE or INACTIVE)

Example: `GET /api/v1/rbac/roles/status/ACTIVE`

### RESPONSE

#### Success Response - Active Roles (HTTP 200 OK)
```json
{
  "status": "SUCCESS",
  "message": "Roles fetched successfully",
  "data": [
    {
      "roleId": 1,
      "roleCode": "ADMIN001",
      "roleName": "Administrator",
      "description": "Administrator role with full access",
      "parentRoleId": null,
      "approvalLimit": null,
      "isParentRole": true,
      "status": "ACTIVE",
      "isDeleted": false,
      "createdByUserId": 1,
      "createdDate": "2026-06-05T10:30:00",
      "modifiedByUserId": null,
      "modifiedDate": "2026-06-05T10:30:00"
    },
    {
      "roleId": 2,
      "roleCode": "MAN001",
      "roleName": "Manager",
      "description": "Manager role with approval authority",
      "parentRoleId": 1,
      "approvalLimit": 100000.00,
      "isParentRole": false,
      "status": "ACTIVE",
      "isDeleted": false,
      "createdByUserId": 1,
      "createdDate": "2026-06-05T11:00:00",
      "modifiedByUserId": null,
      "modifiedDate": "2026-06-05T11:00:00"
    }
  ]
}
```

#### Success Response - Inactive Roles (HTTP 200 OK)
```json
{
  "status": "SUCCESS",
  "message": "Roles fetched successfully",
  "data": [
    {
      "roleId": 5,
      "roleCode": "DEP001",
      "roleName": "Deprecated Role",
      "description": "Old role no longer in use",
      "parentRoleId": null,
      "approvalLimit": null,
      "isParentRole": false,
      "status": "INACTIVE",
      "isDeleted": false,
      "createdByUserId": 1,
      "createdDate": "2026-05-01T08:00:00",
      "modifiedByUserId": 2,
      "modifiedDate": "2026-06-10T15:30:00"
    }
  ]
}
```

### ERROR

#### Validation Error - Invalid Status (HTTP 400 Bad Request)
```json
{
  "status": "VALIDATION_ERROR",
  "message": "Validation Error",
  "errors": [
    {
      "field": "status",
      "message": "Status must be 'ACTIVE' or 'INACTIVE'."
    }
  ],
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

## GET - Retrieve Child Roles

**Endpoint:** `/api/v1/rbac/roles/parent/{parentId}/children`

### REQUEST

No request body. URL parameter required: `parentId` (positive integer)

Example: `GET /api/v1/rbac/roles/parent/1/children`

### RESPONSE

#### Success Response (HTTP 200 OK)
```json
{
  "status": "SUCCESS",
  "message": "Roles fetched successfully",
  "data": [
    {
      "roleId": 2,
      "roleCode": "MAN001",
      "roleName": "Manager",
      "description": "Manager role with approval authority",
      "parentRoleId": 1,
      "approvalLimit": 100000.00,
      "isParentRole": false,
      "status": "ACTIVE",
      "isDeleted": false,
      "createdByUserId": 1,
      "createdDate": "2026-06-05T11:00:00",
      "modifiedByUserId": null,
      "modifiedDate": "2026-06-05T11:00:00"
    },
    {
      "roleId": 4,
      "roleCode": "ADMI001",
      "roleName": "Admin Assistant",
      "description": "Junior administrator with limited access",
      "parentRoleId": 1,
      "approvalLimit": 50000.00,
      "isParentRole": false,
      "status": "ACTIVE",
      "isDeleted": false,
      "createdByUserId": 1,
      "createdDate": "2026-06-06T09:00:00",
      "modifiedByUserId": null,
      "modifiedDate": "2026-06-06T09:00:00"
    }
  ]
}
```

#### Empty List Response - No Child Roles (HTTP 200 OK)
```json
{
  "status": "SUCCESS",
  "message": "Roles fetched successfully",
  "data": []
}
```

### ERROR

#### Validation Error - Invalid Parent ID (HTTP 400 Bad Request)
```json
{
  "status": "VALIDATION_ERROR",
  "message": "Validation Error",
  "errors": [
    {
      "field": "parentId",
      "message": "Parent ID must be a positive number."
    }
  ],
  "timestamp": "2026-06-11T10:30:00"
}
```

#### Parent Role Not Found (HTTP 404 Not Found)
```json
{
  "status": "ERROR",
  "message": "Parent role not found with id: 999",
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

## DELETE - Delete Role

**Endpoint:** `/api/v1/rbac/roles/{id}`

### REQUEST

No request body. URL parameter required: `id` (positive integer)

Example: `DELETE /api/v1/rbac/roles/5`

### RESPONSE

#### Success Response (HTTP 200 OK)
```json
{
  "status": "SUCCESS",
  "message": "Role deleted successfully",
  "data": null
}
```

### ERROR

#### Validation Error - Invalid ID (HTTP 400 Bad Request)
```json
{
  "status": "VALIDATION_ERROR",
  "message": "Validation Error",
  "errors": [
    {
      "field": "id",
      "message": "ID must be a positive number."
    }
  ],
  "timestamp": "2026-06-11T10:30:00"
}
```

#### Role Not Found (HTTP 404 Not Found)
```json
{
  "status": "ERROR",
  "message": "Role not found with id: 999",
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

### Create Role
```bash
curl -X POST http://localhost:8080/api/v1/rbac/roles/save \
  -H "Content-Type: application/json" \
  -d '{
    "roleName": "Administrator",
    "description": "Administrator role with full access",
    "isParentRole": true,
    "status": "ACTIVE",
    "createdByUserId": 1
  }'
```

### Create Child Role
```bash
curl -X POST http://localhost:8080/api/v1/rbac/roles/save \
  -H "Content-Type: application/json" \
  -d '{
    "roleName": "Manager",
    "description": "Manager role with approval authority",
    "parentRoleId": 1,
    "approvalLimit": 100000.00,
    "isParentRole": false,
    "status": "ACTIVE",
    "createdByUserId": 1
  }'
```

### Update Role
```bash
curl -X POST http://localhost:8080/api/v1/rbac/roles/save \
  -H "Content-Type: application/json" \
  -d '{
    "roleId": 1,
    "roleName": "Administrator",
    "description": "Administrator role with full access and approval authority",
    "isParentRole": true,
    "approvalLimit": 500000.00,
    "status": "ACTIVE",
    "modifiedByUserId": 2
  }'
```

### Get Role by ID
```bash
curl -X GET http://localhost:8080/api/v1/rbac/roles/1
```

### Get All Roles
```bash
curl -X GET http://localhost:8080/api/v1/rbac/roles/all
```

### Get Active Roles
```bash
curl -X GET http://localhost:8080/api/v1/rbac/roles/status/ACTIVE
```

### Get Child Roles
```bash
curl -X GET http://localhost:8080/api/v1/rbac/roles/parent/1/children
```

### Delete Role
```bash
curl -X DELETE http://localhost:8080/api/v1/rbac/roles/5
```

