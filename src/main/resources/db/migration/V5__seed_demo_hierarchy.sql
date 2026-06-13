-- ==========================================================
-- Demo hierarchy
--
-- ASL 1
-- ├── Mobile CT Scanner
-- └── Building A
--     └── Building 2
--         └── Radiology Department
--             └── GE Revolution CT
-- ==========================================================

-- ==========================================================
-- ORGANIZATION: ASL 1
-- ==========================================================

INSERT INTO node (id,
                  name,
                  node_type,
                  parent_id,
                  created_at,
                  created_by,
                  updated_at,
                  updated_by)
VALUES ('11111111-1111-1111-1111-111111111111',
        'ASL 1',
        'ORGANIZATION',
        NULL,
        CURRENT_TIMESTAMP,
        'SYSTEM',
        NULL,
        NULL);

INSERT INTO organization (id)
VALUES ('11111111-1111-1111-1111-111111111111');

-- ==========================================================
-- CONTAINER: Building A
-- ==========================================================

INSERT INTO node (id,
                  name,
                  node_type,
                  parent_id,
                  created_at,
                  created_by,
                  updated_at,
                  updated_by)
VALUES ('22222222-2222-2222-2222-222222222222',
        'Building A',
        'CONTAINER',
        '11111111-1111-1111-1111-111111111111',
        CURRENT_TIMESTAMP,
        'SYSTEM',
        NULL,
        NULL);

INSERT INTO container (id)
VALUES ('22222222-2222-2222-2222-222222222222');

-- ==========================================================
-- CONTAINER: Building 2
-- ==========================================================

INSERT INTO node (id,
                  name,
                  node_type,
                  parent_id,
                  created_at,
                  created_by,
                  updated_at,
                  updated_by)
VALUES ('33333333-3333-3333-3333-333333333333',
        'Building 2',
        'CONTAINER',
        '22222222-2222-2222-2222-222222222222',
        CURRENT_TIMESTAMP,
        'SYSTEM',
        NULL,
        NULL);

INSERT INTO container (id)
VALUES ('33333333-3333-3333-3333-333333333333');

-- ==========================================================
-- CONTAINER: Radiology Department
-- ==========================================================

INSERT INTO node (id,
                  name,
                  node_type,
                  parent_id,
                  created_at,
                  created_by,
                  updated_at,
                  updated_by)
VALUES ('44444444-4444-4444-4444-444444444444',
        'Radiology Department',
        'CONTAINER',
        '33333333-3333-3333-3333-333333333333',
        CURRENT_TIMESTAMP,
        'SYSTEM',
        NULL,
        NULL);

INSERT INTO container (id)
VALUES ('44444444-4444-4444-4444-444444444444');

-- ==========================================================
-- EQUIPMENT: GE Revolution CT
-- Attached to Radiology Department
-- ==========================================================

INSERT INTO node (id,
                  name,
                  node_type,
                  parent_id,
                  created_at,
                  created_by,
                  updated_at,
                  updated_by)
VALUES ('55555555-5555-5555-5555-555555555555',
        'GE Revolution CT',
        'EQUIPMENT',
        '44444444-4444-4444-4444-444444444444',
        CURRENT_TIMESTAMP,
        'SYSTEM',
        NULL,
        NULL);

INSERT INTO equipment (id,
                       equipment_type,
                       serial_number,
                       installation_date)
VALUES ('55555555-5555-5555-5555-555555555555',
        'CT',
        'GE-REV-001',
        DATE '2024-01-15');

-- ==========================================================
-- EQUIPMENT: Mobile CT Scanner
-- Attached directly to Organization
-- ==========================================================

INSERT INTO node (id,
                  name,
                  node_type,
                  parent_id,
                  created_at,
                  created_by,
                  updated_at,
                  updated_by)
VALUES ('66666666-6666-6666-6666-666666666666',
        'Mobile CT Scanner',
        'EQUIPMENT',
        '11111111-1111-1111-1111-111111111111',
        CURRENT_TIMESTAMP,
        'SYSTEM',
        NULL,
        NULL);

INSERT INTO equipment (id,
                       equipment_type,
                       serial_number,
                       installation_date)
VALUES ('66666666-6666-6666-6666-666666666666',
        'CT',
        'MOBILE-CT-001',
        DATE '2024-03-20');