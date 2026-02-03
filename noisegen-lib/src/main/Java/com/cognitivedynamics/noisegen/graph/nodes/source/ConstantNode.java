package com.cognitivedynamics.noisegen.graph.nodes.source;

import com.cognitivedynamics.noisegen.graph.NoiseNode;

/**
 * A noise node that returns a constant value regardless of coordinates.
 *
 * <p>Useful for:
 * <ul>
 *   <li>Providing constant offsets or multipliers in node graphs</li>
 *   <li>Creating baseline values</li>
 *   <li>Testing and debugging</li>
 * </ul>
 *
 * <p>This node is immutable and thread-safe.
 */
public final class ConstantNode implements NoiseNode {

    private final double value;

    /**
     * Create a constant node with the specified value.
     *
     * @param value The constant value to return
     */
    public ConstantNode(double value) {
        this.value = value;
    }

    /**
     * Get the constant value.
     *
     * @return The constant value
     */
    public double getValue() {
        return value;
    }

    @Override
    public double evaluate2D(int seed, double x, double y) {
        return value;
    }

    @Override
    public double evaluate3D(int seed, double x, double y, double z) {
        return value;
    }

    @Override
    public double evaluate4D(int seed, double x, double y, double z, double w) {
        return value;
    }

    @Override
    public boolean supports4D() {
        return true;
    }

    @Override
    public String getNodeType() {
        return "Constant";
    }

    @Override
    public String toString() {
        return "ConstantNode(" + value + ")";
    }
}
