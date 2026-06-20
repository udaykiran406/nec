-- =========================================================================
-- Seed RBAC management permission codes and grant them to the admin role.
-- Required by @Authorize on rbacAuth management controllers.
-- Idempotent: safe to re-run.
-- =========================================================================

-- Module for API-level RBAC management permissions
INSERT INTO nec_rbac_module (module_code, module_name, display_order, status, created_by_user_id, created_date, is_deleted)
VALUES ('RBAC_MGMT', 'RBAC Management', 99, 'ACTIVE', 1, CURRENT_TIMESTAMP, 0)
ON CONFLICT (module_code) DO NOTHING;

-- Permission group within the module
INSERT INTO nec_rbac_permission_group (module_id, group_code, group_name, display_order, status, created_by_user_id, created_date, is_deleted)
SELECT m.module_id, 'RBAC_API', 'RBAC API Access', 1, 'ACTIVE', 1, CURRENT_TIMESTAMP, 0
FROM nec_rbac_module m
WHERE m.module_code = 'RBAC_MGMT'
ON CONFLICT (module_id, group_code) DO NOTHING;

-- All permission codes enforced by @Authorize on management endpoints
INSERT INTO nec_rbac_permission (module_id, group_id, permission_code, permission_name, display_order, status, created_by_user_id, created_date, is_deleted, is_side_menu)
SELECT m.module_id, g.group_id, v.code, v.name, v.display_order, 'ACTIVE', 1, CURRENT_TIMESTAMP, 0, FALSE
FROM nec_rbac_module m
JOIN nec_rbac_permission_group g ON g.module_id = m.module_id AND g.group_code = 'RBAC_API'
CROSS JOIN (VALUES
    ('USER_CREATE',            'Create User',                 1),
    ('USER_READ',              'Read User',                   2),
    ('USER_UPDATE',            'Update User',                 3),
    ('USER_DELETE',            'Delete User',                 4),
    ('ROLE_CREATE',            'Create Role',                 5),
    ('ROLE_READ',              'Read Role',                   6),
    ('ROLE_UPDATE',            'Update Role',                 7),
    ('ROLE_DELETE',            'Delete Role',                 8),
    ('ROLE_PERMISSION_CREATE', 'Create Role Permission',      9),
    ('ROLE_PERMISSION_READ',   'Read Role Permission',       10),
    ('ROLE_PERMISSION_UPDATE', 'Update Role Permission',     11),
    ('ROLE_PERMISSION_DELETE', 'Delete Role Permission',     12),
    ('PERMISSION_GROUP_CREATE','Create Permission Group',    13),
    ('PERMISSION_GROUP_READ',  'Read Permission Group',      14),
    ('PERMISSION_GROUP_UPDATE','Update Permission Group',    15),
    ('PERMISSION_GROUP_DELETE','Delete Permission Group',    16)
) AS v(code, name, display_order)
WHERE m.module_code = 'RBAC_MGMT'
ON CONFLICT (module_id, permission_code) DO NOTHING;

-- Grant every RBAC management permission to the admin role (ADMIN code, else lowest role_id)
INSERT INTO nec_rbac_role_permission (role_id, module_id, group_id, permission_id, status, created_by_user_id, created_date)
SELECT
    admin.role_id,
    p.module_id,
    p.group_id,
    p.permission_id,
    'ACTIVE',
    1,
    CURRENT_TIMESTAMP
FROM nec_rbac_permission p
JOIN nec_rbac_module m ON m.module_id = p.module_id AND m.module_code = 'RBAC_MGMT'
CROSS JOIN LATERAL (
    SELECT role_id
    FROM nec_rbac_roles
    WHERE is_deleted = 0
      AND status = 'ACTIVE'
      AND role_code = 'ADMIN'
    LIMIT 1
) admin
WHERE p.permission_code IN (
    'USER_CREATE', 'USER_READ', 'USER_UPDATE', 'USER_DELETE',
    'ROLE_CREATE', 'ROLE_READ', 'ROLE_UPDATE', 'ROLE_DELETE',
    'ROLE_PERMISSION_CREATE', 'ROLE_PERMISSION_READ', 'ROLE_PERMISSION_UPDATE', 'ROLE_PERMISSION_DELETE',
    'PERMISSION_GROUP_CREATE', 'PERMISSION_GROUP_READ', 'PERMISSION_GROUP_UPDATE', 'PERMISSION_GROUP_DELETE'
)
ON CONFLICT (role_id, module_id, group_id, permission_id) DO NOTHING;

-- Fallback: if no ADMIN role exists, grant to the first active role
INSERT INTO nec_rbac_role_permission (role_id, module_id, group_id, permission_id, status, created_by_user_id, created_date)
SELECT
    fallback.role_id,
    p.module_id,
    p.group_id,
    p.permission_id,
    'ACTIVE',
    1,
    CURRENT_TIMESTAMP
FROM nec_rbac_permission p
JOIN nec_rbac_module m ON m.module_id = p.module_id AND m.module_code = 'RBAC_MGMT'
CROSS JOIN LATERAL (
    SELECT MIN(role_id) AS role_id
    FROM nec_rbac_roles
    WHERE is_deleted = 0 AND status = 'ACTIVE'
) fallback
WHERE fallback.role_id IS NOT NULL
  AND NOT EXISTS (
      SELECT 1 FROM nec_rbac_roles WHERE role_code = 'ADMIN' AND is_deleted = 0
  )
  AND p.permission_code IN (
    'USER_CREATE', 'USER_READ', 'USER_UPDATE', 'USER_DELETE',
    'ROLE_CREATE', 'ROLE_READ', 'ROLE_UPDATE', 'ROLE_DELETE',
    'ROLE_PERMISSION_CREATE', 'ROLE_PERMISSION_READ', 'ROLE_PERMISSION_UPDATE', 'ROLE_PERMISSION_DELETE',
    'PERMISSION_GROUP_CREATE', 'PERMISSION_GROUP_READ', 'PERMISSION_GROUP_UPDATE', 'PERMISSION_GROUP_DELETE'
)
ON CONFLICT (role_id, module_id, group_id, permission_id) DO NOTHING;
