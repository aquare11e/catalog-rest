CREATE TABLE section(
    id BIGSERIAL PRIMARY KEY,
    section_id BIGINT REFERENCES section(id),
    title VARCHAR(50),
    deleted BOOLEAN DEFAULT FALSE
);

CREATE TABLE product(
    id BIGSERIAL PRIMARY KEY,
    section_id BIGINT NOT NULL REFERENCES section(id),
    title VARCHAR(50),
    amount NUMERIC,
    unit VARCHAR(20),
    price NUMERIC,
    deleted BOOLEAN DEFAULT FALSE
);

CREATE INDEX section_subsection_idx ON section(section_id);
CREATE INDEX product_section_idx ON product(section_id);