package com.nec.middleware.rbacAuth.auth.constants;

public class AuthServiceConstants {

    private AuthServiceConstants() {
        // Private constructor to prevent instantiation
    }
    public static final String API_SUCCESS_MESSAGE = "success";
    public static final String API_FAILED_MESSAGE = "failed";
    public static final Integer API_SUCCESS_CODE = 200;
    public static final String AUTH_SERVICE = "AUTH-SERVICE";
    public static final String USERNAME_ALREADY_EXISTS = "Username already exists";
    public static final String EMAIL_ALREADY_EXISTS = "Email already exists";
    public static final String INVALID_CREDENTIALS = "Invalid username or password";
    public static final String INVALID_TOKEN = "Invalid or expired token";
    public static final String UNAUTHORIZED_ACCESS = "Unauthorized access";
    public static final String CLIENT_ID_HEADER = "client-id";
    public static final String CLIENT_SECRET_HEADER = "client-secret";
    public static final String REFRESH_TOKEN = "refresh-token";

    // ---- Registration Context Constants (used to resolve schoolCode during user registration) ----
    /** Staff: schoolCode is fetched from the creator's SecurityContext */
    public static final String REG_CTX_STAFF = "STAFF";
    /** Admin: schoolCode is derived from the username in the request */
    public static final String REG_CTX_SCHOOL_ADMIN = "SCHOOL_ADMIN";
    /** Teacher: schoolCode is null (teacher registers independently) */
    public static final String REG_CTX_TEACHER = "TEACHER";
    /** TeacherAdmin: schoolCode is null (teacher-admin registers independently) */
    public static final String REG_CTX_TEACHER_ADMIN = "TEACHER_ADMIN";
    /** EsimAdmin: schoolCode is null (esim admin registers independently) */
    public static final String REG_CTX_ESIM_ADMIN = "EMIS_ADMIN";
    /** Student: schoolCode is taken from schoolId in the request */
    public static final String REG_CTX_STUDENT = "STUDENT";
}
