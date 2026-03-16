package com.cognitivedynamics.noisegen.graph.nodes.modifier;

import com.cognitivedynamics.noisegen.graph.NoiseNode;

/**
 * Modifier node that offsets the input coordinates.
 *
 * <p>Useful for shifting the noise pattern in space.
 *
 * <p>This class is immutable and thread-safe.
 */
public final class DomainOffsetNode extends ModifierNode {

    private final double offsetX;
    private final double offsetY;
    private final double offsetZ;
    private final double offsetW;

    /**
     * Create a domain offset node for 3D coordinates.
     *
     * @param source The source node
     * @param offsetX X offset
     * @param offsetY Y offset
     * @param offsetZ Z offset
     */
    public DomainOffsetNode(NoiseNode source, double offsetX, double offsetY, double offsetZ) {
        this(source, offsetX, offsetY, offsetZ, 0);
    }

    /**
     * Create a domain offset node for 4D coordinates.
     *
     * @param source The source node
     * @param offsetX X offset
     * @param offsetY Y offset
     * @param offsetZ Z offset
     * @param offsetW W offset
     */
    public DomainOffsetNode(NoiseNode source, double offsetX, double offsetY, double offsetZ, double offsetW) {
        super(source);
        this.offsetX = offsetX;
        this.offsetY = offsetY;
        this.offsetZ = offsetZ;
        this.offsetW = offsetW;
    }

    /**
     * Get the X offset.
     *
     * @return The X offset
     */
    public double getOffsetX() {
        return offsetX;
    }

    /**
     * Get the Y offset.
     *
     * @return The Y offset
     */
    public double getOffsetY() {
        return offsetY;
    }

    /**
     * Get the Z offset.
     *
     * @return The Z offset
     */
    public double getOffsetZ() {
        return offsetZ;
    }

    /**
     * Get the W offset.
     *
     * @return The W offset
     */
    public double getOffsetW() {
        return offsetW;
    }

    @Override
    public double evaluate2D(int seed, double x, double y) {
        return source.evaluate2D(seed, x + offsetX, y + offsetY);
    }

    @Override
    public double evaluate3D(int seed, double x, double y, double z) {
        return source.evaluate3D(seed, x + offsetX, y + offsetY, z + offsetZ);
    }

    @Override
    public double evaluate4D(int seed, double x, double y, double z, double w) {
        if (!supports4D()) {
            throw new UnsupportedOperationException("4D noise not supported by " + getNodeType());
        }
        return source.evaluate4D(seed, x + offsetX, y + offsetY, z + offsetZ, w + offsetW);
    }

    @Override
    public String getNodeType() {
        return "DomainOffset";
    }

    @Override
    public String toString() {
        return String.format("DomainOffsetNode(%s, offset=(%.2f, %.2f, %.2f))",
            source.getNodeType(), offsetX, offsetY, offsetZ);
    }
}
