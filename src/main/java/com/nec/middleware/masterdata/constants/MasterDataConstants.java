package com.nec.middleware.masterdata.constants;

public final class MasterDataConstants {

    private MasterDataConstants() {
        throw new UnsupportedOperationException("MasterDataConstants is a utility class");
    }

    // -------------------------------------------------------------------------
    // Success messages
    // -------------------------------------------------------------------------

    public static final String RECORD_CREATED = "Record created successfully";

    public static final String RECORD_UPDATED = "Record updated successfully";

    public static final String RECORD_DELETED = "Record deleted successfully";

    public static final String RECORD_FETCHED = "Record fetched successfully";

    public static final String RECORDS_FETCHED = "Records fetched successfully";

    public static final String STATUS_CHANGED = "Status changed successfully";

    // -------------------------------------------------------------------------
    // Error / validation messages
    // -------------------------------------------------------------------------

    public static final String RECORD_NOT_FOUND = "Record not found with id: ";

    public static final String DUPLICATE_REGION_NAME =
            "A region with this name already exists: ";

    public static final String DUPLICATE_DISTRICT_NAME =
            "A district with this name already exists in this region: ";

    public static final String DUPLICATE_CITY_NAME =
            "A city with this name already exists in this district: ";

    public static final String INVALID_STATUS =
            "Invalid status value. Allowed values are 'Active' and 'Inactive'";

    public static final String REQUEST_NULL = "Request body must not be null";

    public static final String DUPLICATE_POLLING_STATION_CODE =
            "A polling station with this code already exists: ";
    public static final String DUPLICATE_VRC_CODE =
            "A voter registration center with this code already exists: ";
    public static final String DUPLICATE_UNIVERSITY_NAME =
            "A university with this name already exists: ";
    public static final String DUPLICATE_WAREHOUSE_NAME =
            "A warehouse with this name already exists: ";
    public static final String DUPLICATE_WAREHOUSE_SUB_STORE_NAME =
            "A warehouse sub store with this name already exists in this warehouse: ";
    public static final String DUPLICATE_POLITICAL_PARTY_NAME =
            "A political party with this name already exists: ";
    public static final String DUPLICATE_BANK_ACCOUNT_NUMBER =
            "A bank account with this account number already exists: ";
    public static final String DUPLICATE_DEPLOYMENT_ROLE_NAME =
            "A deployment role with this name already exists: ";

    public static final String DUPLICATE_TRAINER_TOT_CODE =
            "Trainer TOT code already exists: ";

    // -------------------------------------------------------------------------
    // Soft-delete flags
    // -------------------------------------------------------------------------

    public static final Short IS_DELETED_FALSE = 0;

    public static final Short IS_DELETED_TRUE = 1;

    // -------------------------------------------------------------------------
    // Allowed status values
    // -------------------------------------------------------------------------

    public static final String STATUS_ACTIVE = "Active";

    public static final String STATUS_INACTIVE = "Inactive";
}