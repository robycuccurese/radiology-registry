CREATE TABLE node (
    id UUID PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    node_type VARCHAR(50) NOT NULL,
    parent_id UUID NULL,

    created_at TIMESTAMP NOT NULL,
    created_by VARCHAR(100) NOT NULL,
    updated_at TIMESTAMP NULL,
    updated_by VARCHAR(100) NULL,

    CONSTRAINT fk_node_parent FOREIGN KEY (parent_id) REFERENCES node(id)
);

CREATE INDEX idx_node_parent ON node(parent_id);