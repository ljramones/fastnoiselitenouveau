package com.cognitivedynamics.noisegen.graph.nodes.fractal;

import com.cognitivedynamics.noisegen.graph.NoiseNode;

/**
 * Ridged multifractal noise node.
 *
 * <p>Ridged noise creates sharp, ridge-like features by taking the absolute
 * value of noise and inverting it. The result resembles mountain ridges,
 * veins, or lightning patterns.
 *
 * <p>Each octave's contribution is weighted based on the previous octave's
 * value, creating interesting erosion-like details at ridge intersections.
 *
 * <p>This class is immutable and thread-safe.
 */
public final class RidgedNode extends FractalNode {

    private final double weightedStrength;

    /**
     * Create a ridged node with default parameters.
     *
     * @param source The source noise node
     * @param octaves The number of octaves
     */
    public RidgedNode(NoiseNode source, int octaves) {
        this(source, octaves, 2.0, 0.5, 0.0);
    }

    /**
     * Create a ridged node with full parameters.
     *
     * @param source The source noise node
     * @param octaves The number of octaves
     * @param lacunarity The frequency multiplier per octave
     * @param gain The amplitude multiplier per octave
     */
    public RidgedNode(NoiseNode source, int octaves, double lacunarity, double gain) {
        this(source, octaves, lacunarity, gain, 0.0);
    }

    /**
     * Create a ridged node with weighted strength.
     *
     * @param source The source noise node
     * @param octaves The number of octaves
     * @param lacunarity The frequency multiplier per octave
     * @param gain The amplitude multiplier per octave
     * @param weightedStrength Controls weight blending (0 = no weighting)
     */
    public RidgedNode(NoiseNode source, int octaves, double lacunarity, double gain, double weightedStrength) {
        super(source, octaves, lacunarity, gain);
        this.weightedStrength = weightedStrength;
    }

    /**
     * Get the weighted strength.
     *
     * @return The weighted strength
     */
    public double getWeightedStrength() {
        return weightedStrength;
    }

    @Override
    public double evaluate2D(int seed, double x, double y) {
        double sum = 0;
        double amp = fractalBounding;

        for (int i = 0; i < octaves; i++) {
            double noise = Math.abs(source.evaluate2D(seed++, x, y));
            sum += (noise * -2 + 1) * amp;
            amp *= lerp(1.0, 1 - noise, weightedStrength);

            x *= lacunarity;
            y *= lacunarity;
            amp *= gain;
        }

        return sum;
    }

    @Override
    public double evaluate3D(int seed, double x, double y, double z) {
        double sum = 0;
        double amp = fractalBounding;

        for (int i = 0; i < octaves; i++) {
            double noise = Math.abs(source.evaluate3D(seed++, x, y, z));
            sum += (noise * -2 + 1) * amp;
            amp *= lerp(1.0, 1 - noise, weightedStrength);

            x *= lacunarity;
            y *= lacunarity;
            z *= lacunarity;
            amp *= gain;
        }

        return sum;
    }

    @Override
    public double evaluate4D(int seed, double x, double y, double z, double w) {
        if (!supports4D()) {
            throw new UnsupportedOperationException("4D noise not supported by " + getNodeType());
        }

        double sum = 0;
        double amp = fractalBounding;

        for (int i = 0; i < octaves; i++) {
            double noise = Math.abs(source.evaluate4D(seed++, x, y, z, w));
            sum += (noise * -2 + 1) * amp;
            amp *= lerp(1.0, 1 - noise, weightedStrength);

            x *= lacunarity;
            y *= lacunarity;
            z *= lacunarity;
            w *= lacunarity;
            amp *= gain;
        }

        return sum;
    }

    @Override
    public String getNodeType() {
        return "Ridged";
    }

    @Override
    public String toString() {
        return String.format("RidgedNode(%s, octaves=%d, lacunarity=%.2f, gain=%.2f)",
            source.getNodeType(), octaves, lacunarity, gain);
    }
}
