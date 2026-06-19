package com.nec.middleware.rbacAuth.auth.utils;

import com.nec.middleware.rbacAuth.rbac.entity.RbacUser;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marks a controller method as protected.
 * <p>
 * The {@link NecAuthorizeAspect} intercepts every method annotated with {@code @Authorize},
 * validates the Bearer token against Keycloak, loads the local {@link RbacUser},
 * checks optional role and permission constraints, and populates the Spring SecurityContext.
 *
 * <pre>
 * &#64;Authorize                                    // token-only validation
 * &#64;Authorize(roles = {"ADMIN"})                 // token + role check
 * &#64;Authorize(permissions = {"USER_CREATE"})      // token + permission check
 * &#64;Authorize(roles = {"ADMIN"}, permissions = {"USER_CREATE"})  // both required
 * </pre>
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Authorize {
    /** Role names required to access the method. Empty = no role constraint. */
    String[] roles() default {};

    /** Permission codes required to access the method. Empty = no permission constraint. */
    String[] permissions() default {};
}
