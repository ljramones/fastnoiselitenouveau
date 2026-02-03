package com.cognitivedynamics.noisegen.graph.nodes.source;

import com.cognitivedynamics.noisegen.generators.NoiseGenerator;
import com.cognitivedynamics.noisegen.generators.SimplexNoiseGen;

/**
 * Simplex noise source node.
 *
 * <p>Wraps SimplexNoiseGen to provide OpenSimplex2 or OpenSimplex2S noise
 * as a graph node. OpenSimplex2S is smoother but slightly slower.
 *
 * <p>This class is immutable and thread-safe.
 */
public final class SimplexSourceNode extends SourceNode {

    private final SimplexNoiseGen generator;
    private final boolean useSmooth;

    /**
     * Create a simplex source node.
     *
     * @param useSmooth If true, use OpenSimplex2S (smoother). If false, use OpenSimplex2.
     */
    public SimplexSourceNode(boolean useSmooth) {
        this(useSmooth, 1.0);
    }

    /**
     * Create a simplex source node with specified frequency.
     *
     * @param useSmooth If true, use OpenSimplex2S (smoother). If false, use OpenSimplex2.
     * @param frequency The frequency multiplier
     */
    public SimplexSourceNode(boolean useSmooth, double frequency) {
        super(frequency);
        this.useSmooth = useSmooth;
        this.generator = new SimplexNoiseGen(useSmooth);
    }

    /**
     * Check if this node uses the smooth variant.
     *
     * @return true if using OpenSimplex2S
     */
    public boolean isSmooth() {
        return useSmooth;
    }

    @Override
    protected NoiseGenerator getGenerator() {
        return generator;
    }

    @Override
    public SimplexSourceNode frequency(double newFrequency) {
        return new SimplexSourceNode(useSmooth, newFrequency);
    }

    @Override
    public String getNodeType() {
        return useSmooth ? "SimplexSmooth" : "Simplex";
    }

    @Override
    public String toString() {
        return String.format("SimplexSourceNode(smooth=%s, frequency=%.4f)", useSmooth, frequency);
    }
}
