-- Backfill root_org_id for existing hierarchies created before V6.
WITH RECURSIVE ancestry AS (
    SELECT n.id AS node_id,
           n.parent_id,
           n.id AS current_id
    FROM node n

    UNION ALL

    SELECT a.node_id,
           p.parent_id,
           p.id AS current_id
    FROM ancestry a
    JOIN node p ON p.id = a.parent_id
), roots AS (
    SELECT a.node_id,
           a.current_id AS root_id
    FROM ancestry a
    WHERE a.parent_id IS NULL
)
UPDATE node n
SET root_org_id = r.root_id
FROM roots r
WHERE n.id = r.node_id
  AND n.root_org_id IS NULL;

ALTER TABLE node
    ALTER COLUMN root_org_id SET NOT NULL;

