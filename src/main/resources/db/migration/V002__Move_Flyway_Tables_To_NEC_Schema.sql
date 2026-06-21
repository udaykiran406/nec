-- Move Flyway-created master data tables from public into the NEC schema.
DO $$
BEGIN
    IF EXISTS (
        SELECT 1 FROM information_schema.tables
        WHERE table_schema = 'public' AND table_name = 'nec_lkp_genders'
    ) THEN
        ALTER TABLE public.nec_lkp_genders SET SCHEMA "NEC";
    END IF;

    IF EXISTS (
        SELECT 1 FROM information_schema.tables
        WHERE table_schema = 'public' AND table_name = 'nec_lkp_departments'
    ) THEN
        ALTER TABLE public.nec_lkp_departments SET SCHEMA "NEC";
    END IF;

    IF EXISTS (
        SELECT 1 FROM information_schema.tables
        WHERE table_schema = 'public' AND table_name = 'nec_lkp_portal_user_types'
    ) THEN
        ALTER TABLE public.nec_lkp_portal_user_types SET SCHEMA "NEC";
    END IF;

    IF EXISTS (
        SELECT 1 FROM information_schema.tables
        WHERE table_schema = 'public' AND table_name = 'nec_regions'
    ) THEN
        ALTER TABLE public.nec_regions SET SCHEMA "NEC";
    END IF;

    IF EXISTS (
        SELECT 1 FROM information_schema.tables
        WHERE table_schema = 'public' AND table_name = 'nec_districts'
    ) THEN
        ALTER TABLE public.nec_districts SET SCHEMA "NEC";
    END IF;

    IF EXISTS (
        SELECT 1 FROM information_schema.tables
        WHERE table_schema = 'public' AND table_name = 'nec_cities'
    ) THEN
        ALTER TABLE public.nec_cities SET SCHEMA "NEC";
    END IF;
END $$;
