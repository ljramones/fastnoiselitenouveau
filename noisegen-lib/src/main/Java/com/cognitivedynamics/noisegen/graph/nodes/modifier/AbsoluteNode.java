package com.cognitivedynamics.noisegen.graph.nodes.modifier;

import com.cognitivedynamics.noisegen.graph.NoiseNode;

/**
 * Modifier node that takes the absolute value of the source output.
 *
 * <p>Output = |source|
 *
 * <p>Useful for creating ridged or billow-like effects from standard noise.
 *
 * <p>This class is immutable and thread-safe.
 */
public final class AbsoluteNode extends ModifierNode {

    /**
     * Create an absolute value node.
     *
     * @param source The source node
     */
    public AbsoluteNode(NoiseNode source) {
        super(source);
    }

    @Override
    public double evaluate2D(int seed, double x, double y) {
        return Math.abs(source.evaluate2D(seed, x, y));
    }

    @Override
    public double evaluate3D(int seed, double x, double y, double z) {
        return Math.abs(source.evaluate3D(seed, x, y, z));
    }

    @Override
    public double evaluate4D(int seed, double x, double y, double z, double w) {
        if (!supports4D()) {
            throw new UnsupportedOperationException("4D noise not supported by " + getNodeType());
        }
        return Math.abs(source.evaluate4D(seed, x, y, z, w));
    }

    @Override
    public String getNodeType() {
        return "Absolute";
    }

    @Override
    public String toString() {
        return "AbsoluteNode(|" + source.getNodeType() + "|)";
    }
}
