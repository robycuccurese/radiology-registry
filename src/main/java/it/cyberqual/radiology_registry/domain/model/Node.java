package it.cyberqual.radiology_registry.domain.model;

import it.cyberqual.radiology_registry.domain.vo.AuditInfo;
import jakarta.persistence.*;

import java.util.UUID;

/**
 * Base entity representing a node in the hierarchical structure.
 *
 * The system is based on a recursive tree model where each node
 * can have a parent node, enabling unlimited nesting.
 *
 * Node types:
 * - Organization: root-level entity
 * - Container: hierarchical grouping unit
 * - Equipment: leaf node representing a medical device
 */
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class Node {

    @Id
    private UUID id;

    private String name;

    @Enumerated(EnumType.STRING)
    private NodeType nodeType;

    @ManyToOne(fetch = FetchType.LAZY)
    private Node parent;

    @Embedded
    private AuditInfo audit;

    public UUID getId() { return id; }
    public String getName() { return name; }
    public NodeType getNodeType() { return nodeType; }
    public Node getParent() { return parent; }

    public void setId(UUID id) { this.id = id; }
    public void setName(String name) { this.name = name; }
    public void setNodeType(NodeType nodeType) { this.nodeType = nodeType; }
    public void setParent(Node parent) { this.parent = parent; }
}