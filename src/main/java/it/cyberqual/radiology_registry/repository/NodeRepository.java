package it.cyberqual.radiology_registry.repository;

import it.cyberqual.radiology_registry.domain.model.Node;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Base repository for all hierarchical nodes.
 * Works with Organization, Container and Equipment due to JOINED inheritance.
 */
public interface NodeRepository extends JpaRepository<Node, UUID> {

    interface NodeTreeRow {
        UUID getId();

        String getName();

        String getNodeType();

        UUID getParentId();

        String getSerialNumber();

        LocalDate getInstallationDate();
    }

    /**
     * Fetch a node with its parent (useful for tree building).
     */
    @Query("""
                SELECT n FROM Node n
                LEFT JOIN FETCH n.parent
                WHERE n.id = :id
            """)
    Optional<Node> findByIdWithParent(UUID id);

    @Query(value = """
            SELECT n.id AS id,
                   n.name AS name,
                   n.node_type AS nodeType,
                   n.parent_id AS parentId,
                   e.serial_number AS serialNumber,
                   e.installation_date AS installationDate
            FROM node n
            LEFT JOIN equipment e ON e.id = n.id
            WHERE n.id = :organizationId OR n.root_org_id = :organizationId
            ORDER BY n.parent_id NULLS FIRST, n.name, n.id
            """, nativeQuery = true)
    List<NodeTreeRow> findFullTreeRowsByRootOrganizationId(@Param("organizationId") UUID organizationId);

    @Query(value = """
            WITH RECURSIVE tree AS (
                SELECT n.id,
                       n.name,
                       n.node_type,
                       n.parent_id
                FROM node n
                WHERE n.id = :organizationId

                UNION ALL

                SELECT c.id,
                       c.name,
                       c.node_type,
                       c.parent_id
                FROM node c
                JOIN tree t ON c.parent_id = t.id
            )
            SELECT t.id AS id,
                   t.name AS name,
                   t.node_type AS nodeType,
                   t.parent_id AS parentId,
                   e.serial_number AS serialNumber,
                   e.installation_date AS installationDate
            FROM tree t
            LEFT JOIN equipment e ON e.id = t.id
            ORDER BY t.parent_id NULLS FIRST, t.name, t.id
            """, nativeQuery = true)
    List<NodeTreeRow> findFullTreeRowsRecursive(@Param("organizationId") UUID organizationId);
}