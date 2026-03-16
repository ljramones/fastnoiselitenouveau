package com.cognitivedynamics.noisegen.graph.nodes.fractal;

import com.cognitivedynamics.noisegen.graph.NoiseNode;

/**
 * Fractional Brownian motion (FBm) fractal node.
 *
 * <p>FBm is the classic fractal noise type, summing octaves of noise with
 * decreasing amplitude. It produces smooth, organic-looking patterns suitable
 * for clouds, terrain, and other natural phenomena.
 *
 * <p>This class is immutable and thread-safe.
 */
public final class FBmNode extends FractalNode {

    /**
     * Create an FBm node with default parameters.
     *
     * @param source The source noise node
     * @param octaves The number of octaves
     */
    public FBmNode(NoiseNode source, int octaves) {
        super(source, octaves);
    }

    /**
     * Create an FBm node with full parameters.
     *
     * @param source The source noise node
     * @param octaves The number of octaves
     * @param lacunarity The frequency multiplier per octave
     * @param gain The amplitude multiplier per octave
     */
    public FBmNode(NoiseNode source, int octaves, double lacunarity, double gain) {
        super(source, octaves, lacunarity, gain);
    }

    @Override
    public double evaluate2D(int seed, double x, double y) {
        double sum = 0;
        double amp = fractalBounding;

        for (int i = 0; i < octaves; i++) {
            double noise = source.evaluate2D(seed++, x, y);
            sum += noise * amp;

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
            double noise = source.evaluate3D(seed++, x, y, z);
            sum += noise * amp;

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
            double noise = source.evaluate4D(seed++, x, y, z, w);
            sum += noise * amp;

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
        return "FBm";
    }

    @Override
    public String toString() {
        return String.format("FBmNode(%s, octaves=%d, lacunarity=%.2f, gain=%.2f)",
            source.getNodeType(), octaves, lacunarity, gain);
    }
}
