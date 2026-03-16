package com.cognitivedynamics.noisegen.graph.nodes.source;

import com.cognitivedynamics.noisegen.generators.NoiseGenerator;
import com.cognitivedynamics.noisegen.graph.NoiseNode;

/**
 * Abstract base class for noise source nodes.
 *
 * <p>Source nodes wrap existing NoiseGenerator implementations and provide
 * frequency control. They convert between double precision coordinates used
 * by the graph API and float precision used by the underlying generators.
 *
 * <p>Subclasses must provide the underlying NoiseGenerator via {@link #getGenerator()}.
 *
 * <p>This class is immutable and thread-safe.
 */
public abstract class SourceNode implements NoiseNode {

    protected final double frequency;

    /**
     * Create a source node with default frequency of 1.0.
     */
    protected SourceNode() {
        this(1.0);
    }

    /**
     * Create a source node with the specified frequency.
     *
     * @param frequency The frequency multiplier for coordinates
     */
    protected SourceNode(double frequency) {
        this.frequency = frequency;
    }

    /**
     * Get the underlying noise generator.
     *
     * @return The NoiseGenerator implementation
     */
    protected abstract NoiseGenerator getGenerator();

    /**
     * Get the current frequency.
     *
     * @return The frequency multiplier
     */
    public double getFrequency() {
        return frequency;
    }

    /**
     * Create a new source node with the specified frequency.
     * This method should be overridden by subclasses to return the correct type.
     *
     * @param newFrequency The new frequency
     * @return A new source node with the updated frequency
     */
    public abstract SourceNode frequency(double newFrequency);

    @Override
    public double evaluate2D(int seed, double x, double y) {
        float fx = (float) (x * frequency);
        float fy = (float) (y * frequency);
        return getGenerator().single2D(seed, fx, fy);
    }

    @Override
    public double evaluate3D(int seed, double x, double y, double z) {
        float fx = (float) (x * frequency);
        float fy = (float) (y * frequency);
        float fz = (float) (z * frequency);
        return getGenerator().single3D(seed, fx, fy, fz);
    }

    @Override
    public double evaluate4D(int seed, double x, double y, double z, double w) {
        if (!supports4D()) {
            throw new UnsupportedOperationException("4D noise not supported by " + getNodeType());
        }
        float fx = (float) (x * frequency);
        float fy = (float) (y * frequency);
        float fz = (float) (z * frequency);
        float fw = (float) (w * frequency);
        return getGenerator().single4D(seed, fx, fy, fz, fw);
    }

    @Override
    public boolean supports4D() {
        return getGenerator().supports4D();
    }
}
