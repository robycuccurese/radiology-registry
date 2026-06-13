CREATE TABLE equipment (
    id UUID PRIMARY KEY,
    equipment_type VARCHAR(50) NOT NULL,
    serial_number VARCHAR(100) NOT NULL,
    installation_date DATE NOT NULL,

    CONSTRAINT fk_equipment_node FOREIGN KEY (id) REFERENCES node(id) ON DELETE CASCADE
);