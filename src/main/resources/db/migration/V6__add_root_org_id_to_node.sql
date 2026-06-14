ALTER TABLE node ADD COLUMN root_org_id UUID;
ALTER TABLE node ADD CONSTRAINT fk_node_root_org FOREIGN KEY (root_org_id) REFERENCES node(id) ON DELETE CASCADE;

-- Optional but highly recommended for performance on tree queries
CREATE INDEX idx_node_root_org_id ON node(root_org_id);
