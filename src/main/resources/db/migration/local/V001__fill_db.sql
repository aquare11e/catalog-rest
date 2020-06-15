-- Books
-- |
-- -> Business & Money
-- -> -> Rich Dad Poor Dad
-- -> -> The 7 Habits of Highly Effective People
-- |
-- -> Computers & Technology
-- -> -> Python Crash Course, 2nd Edition
-- -> -> Designing Data-Intensive Applications
--
-- Computers
-- |
-- -> Computer Components
-- -> -> AMD Ryzen 5 3600
-- -> -> Intel Core i7-9700K
-- -> -> Corsair Vengeance LPX 8Gb
-- |
-- -> Data Storage
-- -> -> External Hard Drives
-- -> -> -> Seagate STGX2000400 Portable 2TB
-- -> -> -> Seagate STGX2000400 Portable 2TB
-- |
-- -> -> Internal Hard Drives
-- -> -> -> Seagate BarraCuda 2TB
-- -> -> -> WD Blue 1TB
-- |
-- -> -> Internal Solid State Drives
-- -> -> -> WD Blue 3D NAND 500GB
-- -> -> -> Samsung  970 EVO Plus SSD 1TB
--
-- Software
-- |
-- -> Antivirus & Security
-- -> -> Symantec Norton Security Deluxe
-- -> -> Kaspersky Total Security 2019 Software
-- |
-- -> Programming & Web Development
-- -> -> Simply Coding for Kids: Learn to Code Python (Course)
-- -> -> Serif WebPlus X5


-- Root sections
INSERT INTO section(title)
VALUES ('Books'),
       ('Computers'),
       ('Software');

-- Subsections
WITH books_id AS (
    SELECT id FROM section
    WHERE title = 'Books'
)
INSERT INTO section(section_id, title)
SELECT id, 'Business & Money' FROM books_id
UNION
SELECT id, 'Computers & Technology' FROM books_id;

WITH comps_id AS (
    SELECT id FROM section
    WHERE title = 'Computers'
)
INSERT INTO section(section_id, title)
SELECT id, 'Computer Components' FROM comps_id
UNION
SELECT id, 'Data Storage' FROM comps_id;

WITH data_storage_id AS (
    SELECT id FROM section
    WHERE title = 'Data Storage'
)
INSERT INTO section(section_id, title)
SELECT id, 'External Hard Drives' FROM data_storage_id
UNION
SELECT id, 'Internal Hard Drives' FROM data_storage_id
UNION
SELECT id, 'Internal Solid State Drives' FROM data_storage_id;

WITH soft_id AS (
    SELECT id FROM section
    WHERE title = 'Software'
)
INSERT INTO section(section_id, title)
SELECT id, 'Antivirus & Security' FROM soft_id
UNION
SELECT id, 'Programming & Web Development' FROM soft_id;

-- Products
WITH books_bm_id AS (
    SELECT id FROM section
    WHERE title = 'Business & Money'
)
INSERT INTO product(section_id, title, amount, unit, price)
SELECT id, 'Rich Dad Poor Dad', 1, 'PIECE', 92.99 FROM books_bm_id
UNION
SELECT id, 'The 7 Habits of Highly Effective People', 1, 'PIECE', 10.83 FROM books_bm_id;

WITH books_ct_id AS (
    SELECT id FROM section
    WHERE title = 'Computers & Technology'
)
INSERT INTO product(section_id, title, amount, unit, price)
SELECT id, 'Python Crash Course, 2nd Edition', 1, 'PIECE', 22.99 FROM books_ct_id
UNION
SELECT id, 'Designing Data-Intensive Applications', 1, 'PIECE', 34.99 FROM books_ct_id;

WITH comp_cc_id AS (
    SELECT id FROM section
    WHERE title = 'Computer Components'
)
INSERT INTO product(section_id, title, amount, unit, price)
SELECT id, 'AMD Ryzen 5 3600', 1, 'PIECE', 166.89 FROM comp_cc_id
UNION
SELECT id, 'Intel Core i7-9700K', 1, 'PIECE', 374 FROM comp_cc_id
UNION
SELECT id, 'Corsair Vengeance LPX 8Gb', 2, 'PIECE', 73.99 FROM comp_cc_id;

WITH comp_ds_ehd_id AS (
    SELECT id FROM section
    WHERE title = 'External Hard Drives'
)
INSERT INTO product(section_id, title, amount, unit, price)
SELECT id, 'Seagate STGX2000400 Portable 2TB', 1, 'PIECE', 63.99  FROM comp_ds_ehd_id
UNION
SELECT id, 'WD Black 5TB P10 Game Drive', 1, 'PIECE', 119.96  FROM comp_ds_ehd_id;

WITH comp_ds_ihd_id AS (
    SELECT id FROM section
    WHERE title = 'Internal Hard Drives'
)
INSERT INTO product(section_id, title, amount, unit, price)
SELECT id, 'Seagate BarraCuda 2TB', 1, 'PIECE', 54.99 FROM comp_ds_ihd_id
UNION
SELECT id, 'WD Blue 1TB', 1, 'PIECE', 44.94 FROM comp_ds_ihd_id;

WITH comp_ds_issd_id AS (
    SELECT id FROM section
    WHERE title = 'Internal Solid State Drives'
)
INSERT INTO product(section_id, title, amount, unit, price)
SELECT id, 'WD Blue 3D NAND 500GB', 1, 'PIECE', 64.99 FROM comp_ds_issd_id
UNION
SELECT id, 'Samsung  970 EVO Plus SSD 1TB', 1, 'PIECE', 199.99 FROM comp_ds_issd_id;

WITH soft_as_id AS (
    SELECT id FROM section
    WHERE title = 'Antivirus & Security'
)
INSERT INTO product(section_id, title, amount, unit, price)
SELECT id, 'Symantec Norton Security Deluxe', 1, 'PIECE', 59.99 FROM soft_as_id
UNION
SELECT id, 'Kaspersky Total Security 2019 Software', 1, 'PIECE', 34.99 FROM soft_as_id;

WITH soft_pwd_id AS (
    SELECT id FROM section
    WHERE title = 'Programming & Web Development'
)
INSERT INTO product(section_id, title, amount, unit, price)
SELECT id, 'Simply Coding for Kids: Learn to Code Python', 1, 'PIECE', 69.95 FROM soft_pwd_id
UNION
SELECT id, 'Serif WebPlus X5', 1, 'PIECE', NULL FROM soft_pwd_id;