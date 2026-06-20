# Users API - Comprehensive Request/Response/Error Documentation

---

## Table of Contents
1. [POST - Create/Update User](#post---createupdate-user)
2. [GET - Retrieve User by ID](#get---retrieve-user-by-id)
3. [GET - Retrieve All Users](#get---retrieve-all-users)
4. [GET - Retrieve Users by Role](#get---retrieve-users-by-role)
5. [POST - Change User Status](#post---change-user-status)
6. [DELETE - Delete User](#delete---delete-user)

---

## POST - Create/Update User

**Endpoint:** `/api/v1/rbac/users/save`

### REQUEST

#### Create User Request
```json
{
  "userName": "John Doe",
  "genderId": 1,
  "roleId": 2,
  "phone": "+255700000001",
  "email": "john.doe@nec.go.tz",
  "photoPath": "/photos/john.jpg",
  "departmentId": 3,
  "regionId": 1,
  "districtId": 5,
  "cityId": 10,
  "isActive": true,
  "createdBy": 1
}
```

#### Update User Request
```json
{
  "id": 5,
  "userName": "Jane Smith",
  "genderId": 2,
  "roleId": 3,
  "phone": "+255700000002",
  "email": "jane.smith@nec.go.tz",
  "photoPath": "/photos/jane.jpg",
  "departmentId": 4,
  "regionId": 2,
  "districtId": 6,
  "cityId": 11,
  "isActive": true,
  "createdBy": 1,
  "updatedBy": 2
}
```

### RESPONSE

#### Create User Success Response (HTTP 201 Created)
```json
{
  "status": "SUCCESS",
  "message": "User created successfully",
  "data": {
    "id": 5,
    "userName": "John Doe",
    "genderId": 1,
    "roleId": 2,
    "phone": "+255700000001",
    "email": "john.doe@nec.go.tz",
    "photoPath": "/photos/john.jpg",
    "departmentId": 3,
    "regionId": 1,
    "districtId": 5,
    "cityId": 10,
    "isActive": true,
    "isDeleted": false,
    "createdBy": 1,
    "createdAt": "2026-06-11T10:30:00",
    "updatedBy": null,
    "updatedAt": "2026-06-11T10:30:00"
  }
}
```

#### Update User Success Response (HTTP 200 OK)
```json
{
  "status": "SUCCESS",
  "message": "User updated successfully",
  "data": {
    "id": 5,
    "userName": "Jane Smith",
    "genderId": 2,
    "roleId": 3,
    "phone": "+255700000002",
    "email": "jane.smith@nec.go.tz",
    "photoPath": "/photos/jane.jpg",
    "departmentId": 4,
    "regionId": 2,
    "districtId": 6,
    "cityId": 11,
    "isActive": true,
    "isDeleted": false,
    "createdBy": 1,
    "createdAt": "2026-06-08T10:00:00",
    "updatedBy": 2,
    "updatedAt": "2026-06-11T14:45:00"
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
      "field": "email",
      "message": "Email is required."
    },
    {
      "field": "userName",
      "message": "User Name is required."
    }
  ],
  "timestamp": "2026-06-11T10:30:00"
}
```

#### Validation Error - Invalid Format (HTTP 400 Bad Request)
```json
{
  "status": "VALIDATION_ERROR",
  "message": "Validation Error",
  "errors": [
    {
      "field": "email",
      "message": "Email is not in a valid format."
    },
    {
      "field": "phone",
      "message": "Phone Number is not in a valid format."
    }
  ],
  "timestamp": "2026-06-11T10:30:00"
}
```

#### Duplicate Email Error (HTTP 409 Conflict)
```json
{
  "status": "ERROR",
  "message": "Email already exists. Please use another one.",
  "errorCode": "DUPLICATE_EMAIL",
  "timestamp": "2026-06-11T10:30:00"
}
```

#### Duplicate Phone Error (HTTP 409 Conflict)
```json
{
  "status": "ERROR",
  "message": "Phone Number already exists. Please use another one.",
  "errorCode": "DUPLICATE_PHONE",
  "timestamp": "2026-06-11T10:30:00"
}
```

#### Invalid Role Reference Error (HTTP 400 Bad Request)
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

---

## GET - Retrieve User by ID

**Endpoint:** `/api/v1/rbac/users/{id}`

### REQUEST

No request body. URL parameter required: `id` (positive integer)

Example: `GET /api/v1/rbac/users/5`

### RESPONSE

#### Success Response (HTTP 200 OK)
```json
{
  "status": "SUCCESS",
  "message": "User fetched successfully",
  "data": {
    "id": 5,
    "userName": "John Doe",
    "genderId": 1,
    "roleId": 2,
    "phone": "+255700000001",
    "email": "john.doe@nec.go.tz",
    "photoPath": "/photos/john.jpg",
    "departmentId": 3,
    "regionId": 1,
    "districtId": 5,
    "cityId": 10,
    "portalUserTypeId": 1,
    "referenceId": null,
    "isActive": true,
    "isDeleted": false,
    "createdBy": 1,
    "createdAt": "2026-06-08T10:00:00",
    "updatedBy": null,
    "updatedAt": "2026-06-08T10:00:00"
  }
}
```

### ERROR

#### Validation Error - Invalid ID Format (HTTP 400 Bad Request)
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

#### User Not Found (HTTP 404 Not Found)
```json
{
  "status": "ERROR",
  "message": "The requested user could not be found.",
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

## GET - Retrieve All Users

**Endpoint:** `/api/v1/rbac/users/all`

### REQUEST

No request body.

Example: `GET /api/v1/rbac/users/all`

### RESPONSE

#### Success Response (HTTP 200 OK)
```json
{
  "status": "SUCCESS",
  "message": "Users fetched successfully",
  "data": [
    {
      "id": 1,
      "userName": "Admin User",
      "genderId": 1,
      "roleId": 1,
      "phone": "+255700000001",
      "email": "admin@nec.go.tz",
      "photoPath": null,
      "departmentId": 1,
      "regionId": 1,
      "districtId": 1,
      "cityId": 1,
      "portalUserTypeId": null,
      "referenceId": null,
      "isActive": true,
      "isDeleted": false,
      "createdBy": 1,
      "createdAt": "2026-06-01T08:00:00",
      "updatedBy": null,
      "updatedAt": "2026-06-01T08:00:00"
    },
    {
      "id": 5,
      "userName": "John Doe",
      "genderId": 1,
      "roleId": 2,
      "phone": "+255700000001",
      "email": "john.doe@nec.go.tz",
      "photoPath": "/photos/john.jpg",
      "departmentId": 3,
      "regionId": 1,
      "districtId": 5,
      "cityId": 10,
      "portalUserTypeId": 1,
      "referenceId": null,
      "isActive": true,
      "isDeleted": false,
      "createdBy": 1,
      "createdAt": "2026-06-08T10:00:00",
      "updatedBy": null,
      "updatedAt": "2026-06-08T10:00:00"
    }
  ]
}
```

#### Empty List Response (HTTP 200 OK)
```json
{
  "status": "SUCCESS",
  "message": "Users fetched successfully",
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

## GET - Retrieve Users by Role

**Endpoint:** `/api/v1/rbac/users/role/{roleId}`

### REQUEST

No request body. URL parameter required: `roleId` (positive integer)

Example: `GET /api/v1/rbac/users/role/2`

### RESPONSE

#### Success Response (HTTP 200 OK)
```json
{
  "status": "SUCCESS",
  "message": "Users fetched successfully",
  "data": [
    {
      "id": 5,
      "userName": "John Doe",
      "genderId": 1,
      "roleId": 2,
      "phone": "+255700000001",
      "email": "john.doe@nec.go.tz",
      "photoPath": "/photos/john.jpg",
      "departmentId": 3,
      "regionId": 1,
      "districtId": 5,
      "cityId": 10,
      "portalUserTypeId": 1,
      "referenceId": null,
      "isActive": true,
      "isDeleted": false,
      "createdBy": 1,
      "createdAt": "2026-06-08T10:00:00",
      "updatedBy": null,
      "updatedAt": "2026-06-08T10:00:00"
    }
  ]
}
```

### ERROR

#### Validation Error - Invalid Role ID (HTTP 400 Bad Request)
```json
{
  "status": "VALIDATION_ERROR",
  "message": "Validation Error",
  "errors": [
    {
      "field": "roleId",
      "message": "Role ID must be a positive number."
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

## POST - Change User Status

**Endpoint:** `/api/v1/rbac/users/{id}/status?isActive=true&updatedBy=2`

### REQUEST

Query Parameters:
- `isActive` (boolean, required): true/false
- `updatedBy` (long, optional): User ID making the change

No request body.

Example: `POST /api/v1/rbac/users/5/status?isActive=false&updatedBy=2`

### RESPONSE

#### Success Response - Status Changed to Inactive (HTTP 200 OK)
```json
{
  "status": "SUCCESS",
  "message": "User active status changed successfully",
  "data": {
    "id": 5,
    "userName": "John Doe",
    "genderId": 1,
    "roleId": 2,
    "phone": "+255700000001",
    "email": "john.doe@nec.go.tz",
    "photoPath": "/photos/john.jpg",
    "departmentId": 3,
    "regionId": 1,
    "districtId": 5,
    "cityId": 10,
    "portalUserTypeId": 1,
    "referenceId": null,
    "isActive": false,
    "isDeleted": false,
    "createdBy": 1,
    "createdAt": "2026-06-08T10:00:00",
    "updatedBy": 2,
    "updatedAt": "2026-06-11T15:00:00"
  }
}
```

#### Success Response - Status Changed to Active (HTTP 200 OK)
```json
{
  "status": "SUCCESS",
  "message": "User active status changed successfully",
  "data": {
    "id": 5,
    "userName": "John Doe",
    "genderId": 1,
    "roleId": 2,
    "phone": "+255700000001",
    "email": "john.doe@nec.go.tz",
    "photoPath": "/photos/john.jpg",
    "departmentId": 3,
    "regionId": 1,
    "districtId": 5,
    "cityId": 10,
    "portalUserTypeId": 1,
    "referenceId": null,
    "isActive": true,
    "isDeleted": false,
    "createdBy": 1,
    "createdAt": "2026-06-08T10:00:00",
    "updatedBy": 2,
    "updatedAt": "2026-06-11T15:00:00"
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

#### Validation Error - Invalid isActive Format (HTTP 400 Bad Request)
```json
{
  "status": "VALIDATION_ERROR",
  "message": "Validation Error",
  "errors": [
    {
      "field": "isActive",
      "message": "isActive must be a boolean value (true/false)."
    }
  ],
  "timestamp": "2026-06-11T10:30:00"
}
```

#### User Not Found (HTTP 404 Not Found)
```json
{
  "status": "ERROR",
  "message": "The requested user could not be found.",
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

---

## DELETE - Delete User

**Endpoint:** `/api/v1/rbac/users/{id}?updatedBy=2`

### REQUEST

Query Parameter:
- `updatedBy` (long, optional): User ID performing the deletion

No request body.

Example: `DELETE /api/v1/rbac/users/5?updatedBy=2`

### RESPONSE

#### Success Response (HTTP 200 OK)
```json
{
  "status": "SUCCESS",
  "message": "User deleted successfully",
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

#### User Not Found (HTTP 404 Not Found)
```json
{
  "status": "ERROR",
  "message": "The requested user could not be found.",
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

### Create User
```bash
curl -X POST http://localhost:8080/api/v1/rbac/users/save \
  -H "Content-Type: application/json" \
  -d '{
    "userName": "John Doe",
    "genderId": 1,
    "roleId": 2,
    "phone": "+255700000001",
    "email": "john.doe@nec.go.tz",
    "departmentId": 3,
    "regionId": 1,
    "districtId": 5,
    "cityId": 10,
    "createdBy": 1
  }'
```

### Get User by ID
```bash
curl -X GET http://localhost:8080/api/v1/rbac/users/5
```

### Get All Users
```bash
curl -X GET http://localhost:8080/api/v1/rbac/users/all
```

### Get Users by Role
```bash
curl -X GET http://localhost:8080/api/v1/rbac/users/role/2
```

### Change User Status
```bash
curl -X POST "http://localhost:8080/api/v1/rbac/users/5/status?isActive=false&updatedBy=2"
```

### Delete User
```bash
curl -X DELETE "http://localhost:8080/api/v1/rbac/users/5?updatedBy=2"
```

