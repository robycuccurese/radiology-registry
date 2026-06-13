package it.cyberqual.radiology_registry.domain.model;

import it.cyberqual.radiology_registry.domain.vo.AuditInfo;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.util.UUID;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "node")
@EntityListeners(AuditingEntityListener.class)
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class Node {

    @Id
    @Column(nullable = false, updatable = false)
    private UUID id;

    @Column(nullable = false)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(name = "node_type", nullable = false, updatable = false)
    private NodeType nodeType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    private Node parent;

    @Embedded
    private AuditInfo auditInfo;

    protected Node(UUID id, String name, NodeType nodeType, Node parent) {
        this.id = id;
        this.name = name;
        this.nodeType = nodeType;
        this.parent = parent;
    }
}