-- Trigger to enforce root_org_id valorization rules:
-- - For root nodes (parent_id IS NULL): root_org_id = id
-- - For child nodes: root_org_id = parent.root_org_id

CREATE OR REPLACE FUNCTION enforce_root_org_id()
RETURNS TRIGGER AS $$
BEGIN
    IF NEW.parent_id IS NULL THEN
        -- Root node: root_org_id must point to itself
        NEW.root_org_id := NEW.id;
    ELSE
        -- Child node: copy root_org_id from parent
        SELECT root_org_id INTO NEW.root_org_id
        FROM node
        WHERE id = NEW.parent_id;

        IF NEW.root_org_id IS NULL THEN
            RAISE EXCEPTION 'Parent % has NULL root_org_id. Ensure parent is properly initialized.', NEW.parent_id;
        END IF;
    END IF;

    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

DROP TRIGGER IF EXISTS tg_enforce_root_org_id ON node;

CREATE TRIGGER tg_enforce_root_org_id
BEFORE INSERT ON node
FOR EACH ROW
EXECUTE FUNCTION enforce_root_org_id();

-- Optional: Add trigger for UPDATE to prevent accidental changes to root_org_id
CREATE OR REPLACE FUNCTION prevent_root_org_id_update()
RETURNS TRIGGER AS $$
BEGIN
    IF OLD.root_org_id IS DISTINCT FROM NEW.root_org_id THEN
        RAISE EXCEPTION 'root_org_id cannot be updated. It is automatically derived from hierarchy.';
    END IF;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

DROP TRIGGER IF EXISTS tg_prevent_root_org_id_update ON node;

CREATE TRIGGER tg_prevent_root_org_id_update
BEFORE UPDATE ON node
FOR EACH ROW
EXECUTE FUNCTION prevent_root_org_id_update();

