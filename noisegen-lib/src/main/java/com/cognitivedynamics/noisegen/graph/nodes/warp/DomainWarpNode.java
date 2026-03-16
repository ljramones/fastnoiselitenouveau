package com.cognitivedynamics.noisegen.graph.nodes.warp;

import com.cognitivedynamics.noisegen.graph.NoiseNode;

/**
 * Domain warp node that distorts input coordinates using noise.
 *
 * <p>Domain warping uses one noise source to distort the coordinates of another,
 * creating organic, flowing, turbulent patterns. This is achieved by sampling
 * the warp noise at the input coordinates and using the result to offset those
 * coordinates before evaluating the source.
 *
 * <p>Example effects:
 * <ul>
 *   <li>Swirling cloud patterns</li>
 *   <li>Organic terrain features</li>
 *   <li>Flowing water effects</li>
 *   <li>Distorted textures</li>
 * </ul>
 *
 * <p>This class is immutable and thread-safe.
 */
public final class DomainWarpNode implements NoiseNode {

    private final NoiseNode source;
    private final NoiseNode warpSource;
    private final double amplitude;

    /**
     * Create a domain warp node.
     *
     * @param source The source node to warp
     * @param warpSource The noise source used for warping
     * @param amplitude The warp amplitude (how much distortion to apply)
     */
    public DomainWarpNode(NoiseNode source, NoiseNode warpSource, double amplitude) {
        if (source == null || warpSource == null) {
            throw new IllegalArgumentException("Source and warp source cannot be null");
        }
        this.source = source;
        this.warpSource = warpSource;
        this.amplitude = amplitude;
    }

    /**
     * Get the source node.
     *
     * @return The source node
     */
    public NoiseNode getSource() {
        return source;
    }

    /**
     * Get the warp source node.
     *
     * @return The warp source node
     */
    public NoiseNode getWarpSource() {
        return warpSource;
    }

    /**
     * Get the warp amplitude.
     *
     * @return The amplitude
     */
    public double getAmplitude() {
        return amplitude;
    }

    @Override
    public double evaluate2D(int seed, double x, double y) {
        // Sample warp noise at offset positions to get independent x/y warps
        double warpX = warpSource.evaluate2D(seed, x, y) * amplitude;
        double warpY = warpSource.evaluate2D(seed, x + 100, y + 100) * amplitude;

        return source.evaluate2D(seed, x + warpX, y + warpY);
    }

    @Override
    public double evaluate3D(int seed, double x, double y, double z) {
        // Sample warp noise at offset positions to get independent x/y/z warps
        double warpX = warpSource.evaluate3D(seed, x, y, z) * amplitude;
        double warpY = warpSource.evaluate3D(seed, x + 100, y + 100, z + 100) * amplitude;
        double warpZ = warpSource.evaluate3D(seed, x + 200, y + 200, z + 200) * amplitude;

        return source.evaluate3D(seed, x + warpX, y + warpY, z + warpZ);
    }

    @Override
    public double evaluate4D(int seed, double x, double y, double z, double w) {
        if (!supports4D()) {
            throw new UnsupportedOperationException("4D noise not supported by " + getNodeType());
        }

        // Sample warp noise at offset positions to get independent x/y/z/w warps
        double warpX = warpSource.evaluate4D(seed, x, y, z, w) * amplitude;
        double warpY = warpSource.evaluate4D(seed, x + 100, y + 100, z + 100, w + 100) * amplitude;
        double warpZ = warpSource.evaluate4D(seed, x + 200, y + 200, z + 200, w + 200) * amplitude;
        double warpW = warpSource.evaluate4D(seed, x + 300, y + 300, z + 300, w + 300) * amplitude;

        return source.evaluate4D(seed, x + warpX, y + warpY, z + warpZ, w + warpW);
    }

    @Override
    public boolean supports4D() {
        return source.supports4D() && warpSource.supports4D();
    }

    @Override
    public String getNodeType() {
        return "DomainWarp";
    }

    @Override
    public String toString() {
        return String.format("DomainWarpNode(%s, warp=%s, amplitude=%.2f)",
            source.getNodeType(), warpSource.getNodeType(), amplitude);
    }
}
