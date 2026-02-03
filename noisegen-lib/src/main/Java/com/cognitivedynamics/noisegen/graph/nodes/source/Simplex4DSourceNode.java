package com.cognitivedynamics.noisegen.graph.nodes.source;

import com.cognitivedynamics.noisegen.generators.NoiseGenerator;
import com.cognitivedynamics.noisegen.generators.Simplex4DNoiseGen;

/**
 * 4D Simplex noise source node.
 *
 * <p>Wraps Simplex4DNoiseGen to provide full 4D simplex noise as a graph node.
 * The 4th dimension (W) is commonly used for time-based animations.
 *
 * <p>4D simplex noise is useful for:
 * <ul>
 *   <li>Smoothly animated 3D noise (using W as time)</li>
 *   <li>Looping animations (by moving in a circle through XW or YW plane)</li>
 *   <li>Volumetric effects with temporal variation</li>
 * </ul>
 *
 * <p>This class is immutable and thread-safe.
 */
public final class Simplex4DSourceNode extends SourceNode {

    private final Simplex4DNoiseGen generator;

    /**
     * Create a 4D simplex source node with default frequency.
     */
    public Simplex4DSourceNode() {
        this(1.0);
    }

    /**
     * Create a 4D simplex source node with specified frequency.
     *
     * @param frequency The frequency multiplier
     */
    public Simplex4DSourceNode(double frequency) {
        super(frequency);
        this.generator = new Simplex4DNoiseGen();
    }

    @Override
    protected NoiseGenerator getGenerator() {
        return generator;
    }

    @Override
    public Simplex4DSourceNode frequency(double newFrequency) {
        return new Simplex4DSourceNode(newFrequency);
    }

    @Override
    public boolean supports4D() {
        return true;
    }

    @Override
    public String getNodeType() {
        return "Simplex4D";
    }

    @Override
    public String toString() {
        return String.format("Simplex4DSourceNode(frequency=%.4f)", frequency);
    }
}
