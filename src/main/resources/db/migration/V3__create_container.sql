CREATE TABLE container (
    id UUID PRIMARY KEY,

    CONSTRAINT fk_container_node FOREIGN KEY (id) REFERENCES node(id) ON DELETE CASCADE
);