package com.cognitivedynamics.noisegen.graph.nodes.modifier;

import com.cognitivedynamics.noisegen.graph.NoiseNode;

/**
 * Modifier node that clamps the source output to a range.
 *
 * <p>Output = clamp(source, min, max)
 *
 * <p>This class is immutable and thread-safe.
 */
public final class ClampNode extends ModifierNode {

    private final double min;
    private final double max;

    /**
     * Create a clamp node.
     *
     * @param source The source node
     * @param min The minimum value
     * @param max The maximum value
     */
    public ClampNode(NoiseNode source, double min, double max) {
        super(source);
        if (min > max) {
            throw new IllegalArgumentException("min (" + min + ") must be <= max (" + max + ")");
        }
        this.min = min;
        this.max = max;
    }

    /**
     * Get the minimum value.
     *
     * @return The minimum value
     */
    public double getMin() {
        return min;
    }

    /**
     * Get the maximum value.
     *
     * @return The maximum value
     */
    public double getMax() {
        return max;
    }

    private double clamp(double value) {
        return Math.max(min, Math.min(max, value));
    }

    @Override
    public double evaluate2D(int seed, double x, double y) {
        return clamp(source.evaluate2D(seed, x, y));
    }

    @Override
    public double evaluate3D(int seed, double x, double y, double z) {
        return clamp(source.evaluate3D(seed, x, y, z));
    }

    @Override
    public double evaluate4D(int seed, double x, double y, double z, double w) {
        if (!supports4D()) {
            throw new UnsupportedOperationException("4D noise not supported by " + getNodeType());
        }
        return clamp(source.evaluate4D(seed, x, y, z, w));
    }

    @Override
    public String getNodeType() {
        return "Clamp";
    }

    @Override
    public String toString() {
        return String.format("ClampNode(%s, [%.2f, %.2f])", source.getNodeType(), min, max);
    }
}
