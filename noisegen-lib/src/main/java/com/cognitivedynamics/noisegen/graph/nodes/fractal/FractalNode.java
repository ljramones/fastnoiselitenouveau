package com.cognitivedynamics.noisegen.graph.nodes.fractal;

import com.cognitivedynamics.noisegen.graph.NoiseNode;

/**
 * Abstract base class for fractal noise nodes.
 *
 * <p>Fractal nodes combine multiple octaves of a source noise at different
 * frequencies and amplitudes to create complex, self-similar patterns.
 *
 * <p>Key parameters:
 * <ul>
 *   <li><b>octaves</b> - Number of noise layers to combine (more = more detail)</li>
 *   <li><b>lacunarity</b> - Frequency multiplier per octave (typically 2.0)</li>
 *   <li><b>gain</b> - Amplitude multiplier per octave (typically 0.5)</li>
 * </ul>
 *
 * <p>This class is immutable and thread-safe.
 */
public abstract class FractalNode implements NoiseNode {

    protected final NoiseNode source;
    protected final int octaves;
    protected final double lacunarity;
    protected final double gain;
    protected final double fractalBounding;

    /**
     * Create a fractal node with default lacunarity (2.0) and gain (0.5).
     *
     * @param source The source noise node
     * @param octaves The number of octaves
     */
    protected FractalNode(NoiseNode source, int octaves) {
        this(source, octaves, 2.0, 0.5);
    }

    /**
     * Create a fractal node with full parameters.
     *
     * @param source The source noise node
     * @param octaves The number of octaves
     * @param lacunarity The frequency multiplier per octave
     * @param gain The amplitude multiplier per octave
     */
    protected FractalNode(NoiseNode source, int octaves, double lacunarity, double gain) {
        if (source == null) {
            throw new IllegalArgumentException("Source node cannot be null");
        }
        if (octaves < 1) {
            throw new IllegalArgumentException("Octaves must be at least 1");
        }
        this.source = source;
        this.octaves = octaves;
        this.lacunarity = lacunarity;
        this.gain = gain;
        this.fractalBounding = calculateFractalBounding(octaves, gain);
    }

    /**
     * Calculate fractal bounding for amplitude normalization.
     * This ensures the output stays roughly in [-1, 1] range.
     */
    protected static double calculateFractalBounding(int octaves, double gain) {
        double amp = gain;
        double ampFractal = 1.0;
        for (int i = 1; i < octaves; i++) {
            ampFractal += amp;
            amp *= gain;
        }
        return 1.0 / ampFractal;
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
     * Get the number of octaves.
     *
     * @return The octave count
     */
    public int getOctaves() {
        return octaves;
    }

    /**
     * Get the lacunarity (frequency multiplier).
     *
     * @return The lacunarity
     */
    public double getLacunarity() {
        return lacunarity;
    }

    /**
     * Get the gain (amplitude multiplier).
     *
     * @return The gain
     */
    public double getGain() {
        return gain;
    }

    @Override
    public boolean supports4D() {
        return source.supports4D();
    }

    /**
     * Helper to lerp between two values.
     */
    protected static double lerp(double a, double b, double t) {
        return a + (b - a) * t;
    }
}
