package com.cognitivedynamics.noisegen.graph.nodes.fractal;

import com.cognitivedynamics.noisegen.graph.NoiseNode;

/**
 * Billow fractal noise node.
 *
 * <p>Billow noise is similar to ridged noise but without the inversion,
 * creating soft, cloud-like, "billowy" patterns. It uses the absolute
 * value of the noise to create rounded hills rather than sharp ridges.
 *
 * <p>Useful for clouds, soft terrain, and organic textures.
 *
 * <p>This class is immutable and thread-safe.
 */
public final class BillowNode extends FractalNode {

    private final double weightedStrength;

    /**
     * Create a billow node with default parameters.
     *
     * @param source The source noise node
     * @param octaves The number of octaves
     */
    public BillowNode(NoiseNode source, int octaves) {
        this(source, octaves, 2.0, 0.5, 0.0);
    }

    /**
     * Create a billow node with full parameters.
     *
     * @param source The source noise node
     * @param octaves The number of octaves
     * @param lacunarity The frequency multiplier per octave
     * @param gain The amplitude multiplier per octave
     */
    public BillowNode(NoiseNode source, int octaves, double lacunarity, double gain) {
        this(source, octaves, lacunarity, gain, 0.0);
    }

    /**
     * Create a billow node with weighted strength.
     *
     * @param source The source noise node
     * @param octaves The number of octaves
     * @param lacunarity The frequency multiplier per octave
     * @param gain The amplitude multiplier per octave
     * @param weightedStrength Controls weight blending (0 = no weighting)
     */
    public BillowNode(NoiseNode source, int octaves, double lacunarity, double gain, double weightedStrength) {
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
            double noise = Math.abs(source.evaluate2D(seed++, x, y)) * 2 - 1;
            sum += noise * amp;
            amp *= lerp(1.0, (noise + 1) * 0.5, weightedStrength);

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
            double noise = Math.abs(source.evaluate3D(seed++, x, y, z)) * 2 - 1;
            sum += noise * amp;
            amp *= lerp(1.0, (noise + 1) * 0.5, weightedStrength);

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
            double noise = Math.abs(source.evaluate4D(seed++, x, y, z, w)) * 2 - 1;
            sum += noise * amp;
            amp *= lerp(1.0, (noise + 1) * 0.5, weightedStrength);

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
        return "Billow";
    }

    @Override
    public String toString() {
        return String.format("BillowNode(%s, octaves=%d, lacunarity=%.2f, gain=%.2f)",
            source.getNodeType(), octaves, lacunarity, gain);
    }
}
