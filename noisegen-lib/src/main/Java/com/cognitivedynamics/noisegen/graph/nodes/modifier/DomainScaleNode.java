package com.cognitivedynamics.noisegen.graph.nodes.modifier;

import com.cognitivedynamics.noisegen.graph.NoiseNode;

/**
 * Modifier node that scales the input coordinates.
 *
 * <p>Larger scale factors result in higher frequency noise (more detail).
 * This is equivalent to applying a frequency multiplier.
 *
 * <p>This class is immutable and thread-safe.
 */
public final class DomainScaleNode extends ModifierNode {

    private final double scale;

    /**
     * Create a domain scale node.
     *
     * @param source The source node
     * @param scale The scale factor (coordinates are multiplied by this)
     */
    public DomainScaleNode(NoiseNode source, double scale) {
        super(source);
        this.scale = scale;
    }

    /**
     * Get the scale factor.
     *
     * @return The scale factor
     */
    public double getScale() {
        return scale;
    }

    @Override
    public double evaluate2D(int seed, double x, double y) {
        return source.evaluate2D(seed, x * scale, y * scale);
    }

    @Override
    public double evaluate3D(int seed, double x, double y, double z) {
        return source.evaluate3D(seed, x * scale, y * scale, z * scale);
    }

    @Override
    public double evaluate4D(int seed, double x, double y, double z, double w) {
        if (!supports4D()) {
            throw new UnsupportedOperationException("4D noise not supported by " + getNodeType());
        }
        return source.evaluate4D(seed, x * scale, y * scale, z * scale, w * scale);
    }

    @Override
    public String getNodeType() {
        return "DomainScale";
    }

    @Override
    public String toString() {
        return String.format("DomainScaleNode(%s, scale=%.4f)", source.getNodeType(), scale);
    }
}
