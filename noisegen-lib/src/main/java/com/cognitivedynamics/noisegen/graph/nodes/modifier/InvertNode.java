package com.cognitivedynamics.noisegen.graph.nodes.modifier;

import com.cognitivedynamics.noisegen.graph.NoiseNode;

/**
 * Modifier node that inverts (negates) the source output.
 *
 * <p>Output = -source
 *
 * <p>This class is immutable and thread-safe.
 */
public final class InvertNode extends ModifierNode {

    /**
     * Create an invert node.
     *
     * @param source The source node
     */
    public InvertNode(NoiseNode source) {
        super(source);
    }

    @Override
    public double evaluate2D(int seed, double x, double y) {
        return -source.evaluate2D(seed, x, y);
    }

    @Override
    public double evaluate3D(int seed, double x, double y, double z) {
        return -source.evaluate3D(seed, x, y, z);
    }

    @Override
    public double evaluate4D(int seed, double x, double y, double z, double w) {
        if (!supports4D()) {
            throw new UnsupportedOperationException("4D noise not supported by " + getNodeType());
        }
        return -source.evaluate4D(seed, x, y, z, w);
    }

    @Override
    public String getNodeType() {
        return "Invert";
    }

    @Override
    public String toString() {
        return "InvertNode(-" + source.getNodeType() + ")";
    }
}
