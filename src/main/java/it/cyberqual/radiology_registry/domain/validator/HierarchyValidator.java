package it.cyberqual.radiology_registry.domain.validator;

import it.cyberqual.radiology_registry.domain.model.Node;
import it.cyberqual.radiology_registry.domain.model.NodeType;
import org.springframework.stereotype.Component;

/**
 * Validates hierarchy business rules for Nodes.
 */
@Component
public class HierarchyValidator {

    /**
     * Validates if a node can be attached to a given parent.
     */
    public void validateParent(Node parent, NodeType childType) {

        if (parent == null) {
            if (childType != NodeType.ORGANIZATION) {
                throw new IllegalStateException("Only Organization can be root node");
            }
            return;
        }

        switch (childType) {

            case ORGANIZATION ->
                    throw new IllegalStateException("Organization cannot have a parent");

            case CONTAINER -> {
                if (parent.getNodeType() == NodeType.EQUIPMENT) {
                    throw new IllegalStateException("Container cannot be child of Equipment");
                }
            }

            case EQUIPMENT -> {
                if (parent.getNodeType() == NodeType.EQUIPMENT) {
                    throw new IllegalStateException("Equipment cannot be child of Equipment");
                }
            }
        }
    }
}