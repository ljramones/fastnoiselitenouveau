package com.cognitivedynamics.noisegen.graph.nodes.modifier;

import com.cognitivedynamics.noisegen.graph.NoiseNode;

/**
 * Abstract base class for unary modifier nodes.
 *
 * <p>Modifier nodes take a single input node and transform its output or
 * coordinates in some way.
 *
 * <p>This class is immutable and thread-safe.
 */
public abstract class ModifierNode implements NoiseNode {

    protected final NoiseNode source;

    /**
     * Create a modifier node with the specified source.
     *
     * @param source The source node to modify
     */
    protected ModifierNode(NoiseNode source) {
        if (source == null) {
            throw new IllegalArgumentException("Source node cannot be null");
        }
        this.source = source;
    }

    /**
     * Get the source node.
     *
     * @return The source node
     */
    public NoiseNode getSource() {
        return source;
    }

    @Override
    public boolean supports4D() {
        return source.supports4D();
    }
}
