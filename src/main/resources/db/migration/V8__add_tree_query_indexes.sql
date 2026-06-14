-- Supports full tree reads filtered by root organization and parent linkage.
CREATE INDEX IF NOT EXISTS idx_node_root_parent ON node(root_org_id, parent_id);

