-- Move Hibernate-created RBAC tables from public into the keycloak schema.
DO $$
DECLARE
    tbl text;
BEGIN
    FOREACH tbl IN ARRAY ARRAY[
        'nec_rbac_module',
        'nec_rbac_permission_group',
        'nec_rbac_permission',
        'nec_rbac_roles',
        'nec_rbac_role_permission',
        'nec_rbac_users',
        'nec_password_reset_tokens'
    ]
    LOOP
        IF EXISTS (
            SELECT 1 FROM information_schema.tables
            WHERE table_schema = 'public' AND table_name = tbl
        ) THEN
            EXECUTE format('ALTER TABLE public.%I SET SCHEMA keycloak', tbl);
        END IF;
    END LOOP;
END $$;
