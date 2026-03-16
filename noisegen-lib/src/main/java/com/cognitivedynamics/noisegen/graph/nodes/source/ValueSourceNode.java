package com.cognitivedynamics.noisegen.graph.nodes.source;

import com.cognitivedynamics.noisegen.generators.NoiseGenerator;
import com.cognitivedynamics.noisegen.generators.ValueNoiseGen;

/**
 * Value noise source node.
 *
 * <p>Wraps ValueNoiseGen to provide value noise as a graph node.
 * Value noise is a simpler form of noise that interpolates random
 * values at grid points.
 *
 * <p>The cubic variant uses cubic interpolation for smoother results
 * at the cost of some performance.
 *
 * <p>This class is immutable and thread-safe.
 */
public final class ValueSourceNode extends SourceNode {

    private final ValueNoiseGen generator;
    private final boolean useCubic;

    /**
     * Create a value source node.
     *
     * @param useCubic If true, use cubic interpolation for smoother results
     */
    public ValueSourceNode(boolean useCubic) {
        this(useCubic, 1.0);
    }

    /**
     * Create a value source node with specified frequency.
     *
     * @param useCubic If true, use cubic interpolation for smoother results
     * @param frequency The frequency multiplier
     */
    public ValueSourceNode(boolean useCubic, double frequency) {
        super(frequency);
        this.useCubic = useCubic;
        this.generator = new ValueNoiseGen(useCubic);
    }

    /**
     * Check if this node uses cubic interpolation.
     *
     * @return true if using cubic interpolation
     */
    public boolean isCubic() {
        return useCubic;
    }

    @Override
    protected NoiseGenerator getGenerator() {
        return generator;
    }

    @Override
    public ValueSourceNode frequency(double newFrequency) {
        return new ValueSourceNode(useCubic, newFrequency);
    }

    @Override
    public String getNodeType() {
        return useCubic ? "ValueCubic" : "Value";
    }

    @Override
    public String toString() {
        return String.format("ValueSourceNode(cubic=%s, frequency=%.4f)", useCubic, frequency);
    }
}
