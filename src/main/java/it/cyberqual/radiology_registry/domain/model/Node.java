package it.cyberqual.radiology_registry.domain.model;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.Instant;
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

    @Column(name = "root_org_id", nullable = false)
    private UUID rootOrganizationId;

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    @CreatedBy
    @Column(nullable = false, updatable = false)
    private String createdBy;

    @LastModifiedDate
    @Column(insertable = false)
    private Instant updatedAt;

    @LastModifiedBy
    @Column(insertable = false)
    private String updatedBy;

    protected Node(UUID id, String name, NodeType nodeType, Node parent, UUID rootOrganizationId) {
        this.id = id;
        this.name = name;
        this.nodeType = nodeType;
        this.parent = parent;
        this.rootOrganizationId = rootOrganizationId;
    }
}