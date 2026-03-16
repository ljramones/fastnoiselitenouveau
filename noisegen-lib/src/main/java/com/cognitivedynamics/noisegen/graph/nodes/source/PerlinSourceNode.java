package com.cognitivedynamics.noisegen.graph.nodes.source;

import com.cognitivedynamics.noisegen.generators.NoiseGenerator;
import com.cognitivedynamics.noisegen.generators.PerlinNoiseGen;

/**
 * Perlin noise source node.
 *
 * <p>Wraps PerlinNoiseGen to provide classic Perlin noise as a graph node.
 *
 * <p>This class is immutable and thread-safe.
 */
public final class PerlinSourceNode extends SourceNode {

    private final PerlinNoiseGen generator;

    /**
     * Create a Perlin source node with default frequency.
     */
    public PerlinSourceNode() {
        this(1.0);
    }

    /**
     * Create a Perlin source node with specified frequency.
     *
     * @param frequency The frequency multiplier
     */
    public PerlinSourceNode(double frequency) {
        super(frequency);
        this.generator = new PerlinNoiseGen();
    }

    @Override
    protected NoiseGenerator getGenerator() {
        return generator;
    }

    @Override
    public PerlinSourceNode frequency(double newFrequency) {
        return new PerlinSourceNode(newFrequency);
    }

    @Override
    public String getNodeType() {
        return "Perlin";
    }

    @Override
    public String toString() {
        return String.format("PerlinSourceNode(frequency=%.4f)", frequency);
    }
}
