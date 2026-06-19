-- =========================================================================
-- NEC Master Data Tables - Must be created BEFORE RBAC tables
-- =========================================================================
-- This SQL script creates all lookup and master data tables that are
-- referenced by the RBAC tables.
-- =========================================================================

-- Create Genders Lookup Table
CREATE TABLE IF NOT EXISTS nec_lkp_genders (
    id BIGSERIAL NOT NULL,
    gender_code VARCHAR(50) NOT NULL UNIQUE,
    gender_name VARCHAR(100) NOT NULL UNIQUE,
    is_active BOOLEAN DEFAULT TRUE,
    is_deleted BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT pk_nec_lkp_genders PRIMARY KEY (id)
);

-- Create Departments Lookup Table
CREATE TABLE IF NOT EXISTS nec_lkp_departments (
    id BIGSERIAL NOT NULL,
    department_code VARCHAR(50) NOT NULL UNIQUE,
    department_name VARCHAR(100) NOT NULL UNIQUE,
    is_active BOOLEAN DEFAULT TRUE,
    is_deleted BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT pk_nec_lkp_departments PRIMARY KEY (id)
);

-- Create Portal User Types Lookup Table
CREATE TABLE IF NOT EXISTS nec_lkp_portal_user_types (
    id BIGSERIAL NOT NULL,
    portal_user_type_code VARCHAR(50) NOT NULL UNIQUE,
    portal_user_type_name VARCHAR(100) NOT NULL UNIQUE,
    is_active BOOLEAN DEFAULT TRUE,
    is_deleted BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT pk_nec_lkp_portal_user_types PRIMARY KEY (id)
);

-- Create Regions Master Table
CREATE TABLE IF NOT EXISTS nec_regions (
    id BIGSERIAL NOT NULL,
    region_name VARCHAR(150) NOT NULL UNIQUE,
    status VARCHAR(10) NOT NULL DEFAULT 'active',
    created_by BIGINT,
    updated_by BIGINT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted_at TIMESTAMP,
    is_deleted SMALLINT NOT NULL DEFAULT 0,
    CONSTRAINT pk_nec_regions PRIMARY KEY (id),
    CONSTRAINT uq_regions_name UNIQUE (region_name)
);

-- Create Districts Master Table
CREATE TABLE IF NOT EXISTS nec_districts (
    id BIGSERIAL NOT NULL,
    district_name VARCHAR(150) NOT NULL,
    region_id BIGINT NOT NULL,
    status VARCHAR(10) NOT NULL DEFAULT 'active',
    created_by BIGINT,
    updated_by BIGINT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted_at TIMESTAMP,
    is_deleted SMALLINT NOT NULL DEFAULT 0,
    CONSTRAINT pk_nec_districts PRIMARY KEY (id),
    CONSTRAINT fk_nec_districts_region
        FOREIGN KEY (region_id)
        REFERENCES nec_regions(id),
    CONSTRAINT uq_district_name_region UNIQUE (district_name, region_id)
);

-- Create Cities Master Table
CREATE TABLE IF NOT EXISTS nec_cities (
    id BIGSERIAL NOT NULL,
    city_name VARCHAR(150) NOT NULL,
    region_id BIGINT NOT NULL,
    district_id BIGINT,
    status VARCHAR(10) NOT NULL DEFAULT 'active',
    created_by BIGINT,
    updated_by BIGINT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted_at TIMESTAMP,
    is_deleted SMALLINT NOT NULL DEFAULT 0,
    CONSTRAINT pk_nec_cities PRIMARY KEY (id),
    CONSTRAINT fk_nec_cities_region
        FOREIGN KEY (region_id)
        REFERENCES nec_regions(id),
    CONSTRAINT fk_nec_cities_district
        FOREIGN KEY (district_id)
        REFERENCES nec_districts(id)
);

-- Create Indexes
CREATE INDEX IF NOT EXISTS idx_nec_regions_status ON nec_regions(status);
CREATE INDEX IF NOT EXISTS idx_nec_regions_is_deleted ON nec_regions(is_deleted);
CREATE INDEX IF NOT EXISTS idx_nec_districts_region ON nec_districts(region_id);
CREATE INDEX IF NOT EXISTS idx_nec_districts_is_deleted ON nec_districts(is_deleted);
CREATE INDEX IF NOT EXISTS idx_nec_cities_region ON nec_cities(region_id);
CREATE INDEX IF NOT EXISTS idx_nec_cities_district ON nec_cities(district_id);
CREATE INDEX IF NOT EXISTS idx_nec_cities_is_deleted ON nec_cities(is_deleted);

-- =========================================================================
-- Insert Sample/Default Master Data
-- =========================================================================

-- Insert Genders
INSERT INTO nec_lkp_genders (gender_code, gender_name, is_active, is_deleted)
SELECT 'M', 'Male', TRUE, FALSE
WHERE NOT EXISTS (SELECT 1 FROM nec_lkp_genders WHERE gender_code = 'M');

INSERT INTO nec_lkp_genders (gender_code, gender_name, is_active, is_deleted)
SELECT 'F', 'Female', TRUE, FALSE
WHERE NOT EXISTS (SELECT 1 FROM nec_lkp_genders WHERE gender_code = 'F');

-- Insert Departments
INSERT INTO nec_lkp_departments (department_code, department_name, is_active, is_deleted)
SELECT 'IT', 'Information Technology', TRUE, FALSE
WHERE NOT EXISTS (SELECT 1 FROM nec_lkp_departments WHERE department_code = 'IT');

INSERT INTO nec_lkp_departments (department_code, department_name, is_active, is_deleted)
SELECT 'HR', 'Human Resources', TRUE, FALSE
WHERE NOT EXISTS (SELECT 1 FROM nec_lkp_departments WHERE department_code = 'HR');

INSERT INTO nec_lkp_departments (department_code, department_name, is_active, is_deleted)
SELECT 'FIN', 'Finance', TRUE, FALSE
WHERE NOT EXISTS (SELECT 1 FROM nec_lkp_departments WHERE department_code = 'FIN');

-- Insert Portal User Types
INSERT INTO nec_lkp_portal_user_types (portal_user_type_code, portal_user_type_name, is_active, is_deleted)
SELECT 'ADMIN', 'Administrator', TRUE, FALSE
WHERE NOT EXISTS (SELECT 1 FROM nec_lkp_portal_user_types WHERE portal_user_type_code = 'ADMIN');

INSERT INTO nec_lkp_portal_user_types (portal_user_type_code, portal_user_type_name, is_active, is_deleted)
SELECT 'USER', 'Regular User', TRUE, FALSE
WHERE NOT EXISTS (SELECT 1 FROM nec_lkp_portal_user_types WHERE portal_user_type_code = 'USER');

-- Insert Regions (including region_id=2 that the current data references)
INSERT INTO nec_regions (id, region_name, status, is_deleted)
SELECT 1, 'Region One', 'active', 0
WHERE NOT EXISTS (SELECT 1 FROM nec_regions WHERE id = 1);

INSERT INTO nec_regions (id, region_name, status, is_deleted)
SELECT 2, 'Region Two', 'active', 0
WHERE NOT EXISTS (SELECT 1 FROM nec_regions WHERE id = 2);

INSERT INTO nec_regions (id, region_name, status, is_deleted)
SELECT 3, 'Region Three', 'active', 0
WHERE NOT EXISTS (SELECT 1 FROM nec_regions WHERE id = 3);

-- Ensure the BIGSERIAL sequence is updated to avoid conflicts
SELECT setval('nec_regions_id_seq', (SELECT COALESCE(MAX(id), 0) FROM nec_regions) + 1);
SELECT setval('nec_lkp_genders_id_seq', (SELECT COALESCE(MAX(id), 0) FROM nec_lkp_genders) + 1);
SELECT setval('nec_lkp_departments_id_seq', (SELECT COALESCE(MAX(id), 0) FROM nec_lkp_departments) + 1);
SELECT setval('nec_lkp_portal_user_types_id_seq', (SELECT COALESCE(MAX(id), 0) FROM nec_lkp_portal_user_types) + 1);

-- Insert default districts for each region
INSERT INTO nec_districts (district_name, region_id, status, is_deleted)
SELECT 'District 1', 1, 'active', 0
WHERE NOT EXISTS (SELECT 1 FROM nec_districts WHERE region_id = 1 AND district_name = 'District 1');

INSERT INTO nec_districts (district_name, region_id, status, is_deleted)
SELECT 'District 2', 2, 'active', 0
WHERE NOT EXISTS (SELECT 1 FROM nec_districts WHERE region_id = 2 AND district_name = 'District 2');

INSERT INTO nec_districts (district_name, region_id, status, is_deleted)
SELECT 'District 3', 3, 'active', 0
WHERE NOT EXISTS (SELECT 1 FROM nec_districts WHERE region_id = 3 AND district_name = 'District 3');

SELECT setval('nec_districts_id_seq', (SELECT COALESCE(MAX(id), 0) FROM nec_districts) + 1);

-- Insert default cities for each region
INSERT INTO nec_cities (city_name, region_id, district_id, status, is_deleted)
SELECT 'City 1', 1, (SELECT id FROM nec_districts WHERE region_id = 1 LIMIT 1), 'active', 0
WHERE NOT EXISTS (SELECT 1 FROM nec_cities WHERE region_id = 1 AND city_name = 'City 1');

INSERT INTO nec_cities (city_name, region_id, district_id, status, is_deleted)
SELECT 'City 2', 2, (SELECT id FROM nec_districts WHERE region_id = 2 LIMIT 1), 'active', 0
WHERE NOT EXISTS (SELECT 1 FROM nec_cities WHERE region_id = 2 AND city_name = 'City 2');

INSERT INTO nec_cities (city_name, region_id, district_id, status, is_deleted)
SELECT 'City 3', 3, (SELECT id FROM nec_districts WHERE region_id = 3 LIMIT 1), 'active', 0
WHERE NOT EXISTS (SELECT 1 FROM nec_cities WHERE region_id = 3 AND city_name = 'City 3');

SELECT setval('nec_cities_id_seq', (SELECT COALESCE(MAX(id), 0) FROM nec_cities) + 1);

