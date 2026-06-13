package it.cyberqual.radiology_registry.repository;

import it.cyberqual.radiology_registry.domain.model.Node;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Base repository for all hierarchical nodes.
 * Works with Organization, Container and Equipment due to JOINED inheritance.
 */
public interface NodeRepository extends JpaRepository<Node, UUID> {

    /**
     * Fetch a node with its parent (useful for tree building).
     */
    @Query("""
                SELECT n FROM Node n
                LEFT JOIN FETCH n.parent
                WHERE n.id = :id
            """)
    Optional<Node> findByIdWithParent(UUID id);

    /**
     * Fetch all nodes belonging to an organization root.
     */
    @Query("""
                SELECT n FROM Node n
                WHERE n.id = :rootId OR n.parent.id = :rootId
            """)
    List<Node> findDirectChildren(UUID rootId);
}