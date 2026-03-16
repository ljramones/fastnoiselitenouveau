package com.cognitivedynamics.noisegen.graph.nodes.fractal;

import com.cognitivedynamics.noisegen.graph.NoiseNode;

/**
 * Hybrid multifractal noise node.
 *
 * <p>Hybrid multifractal combines additive and multiplicative octave blending.
 * The first octave is added directly, while subsequent octaves are weighted
 * by the accumulated signal. This creates terrain-like features where flat
 * areas stay smooth and mountainous areas have more detail.
 *
 * <p>Particularly useful for terrain generation with natural-looking erosion
 * patterns.
 *
 * <p>This class is immutable and thread-safe.
 */
public final class HybridMultiNode extends FractalNode {

    /**
     * Create a hybrid multifractal node with default parameters.
     *
     * @param source The source noise node
     * @param octaves The number of octaves
     */
    public HybridMultiNode(NoiseNode source, int octaves) {
        super(source, octaves);
    }

    /**
     * Create a hybrid multifractal node with full parameters.
     *
     * @param source The source noise node
     * @param octaves The number of octaves
     * @param lacunarity The frequency multiplier per octave
     * @param gain The amplitude multiplier per octave
     */
    public HybridMultiNode(NoiseNode source, int octaves, double lacunarity, double gain) {
        super(source, octaves, lacunarity, gain);
    }

    @Override
    public double evaluate2D(int seed, double x, double y) {
        // First octave (additive base)
        double result = source.evaluate2D(seed++, x, y) + 1;  // Shift to [0, 2]
        double weight = result;
        double amp = gain;

        x *= lacunarity;
        y *= lacunarity;

        // Remaining octaves (multiplicative blend)
        for (int i = 1; i < octaves; i++) {
            // Clamp weight to prevent runaway multiplication
            if (weight > 1) {
                weight = 1;
            }

            double noise = (source.evaluate2D(seed++, x, y) + 1) * amp;
            result += weight * noise;
            weight *= noise;

            x *= lacunarity;
            y *= lacunarity;
            amp *= gain;
        }

        // Normalize to approximately [-1, 1]
        return result * fractalBounding - 1;
    }

    @Override
    public double evaluate3D(int seed, double x, double y, double z) {
        // First octave (additive base)
        double result = source.evaluate3D(seed++, x, y, z) + 1;
        double weight = result;
        double amp = gain;

        x *= lacunarity;
        y *= lacunarity;
        z *= lacunarity;

        // Remaining octaves (multiplicative blend)
        for (int i = 1; i < octaves; i++) {
            if (weight > 1) {
                weight = 1;
            }

            double noise = (source.evaluate3D(seed++, x, y, z) + 1) * amp;
            result += weight * noise;
            weight *= noise;

            x *= lacunarity;
            y *= lacunarity;
            z *= lacunarity;
            amp *= gain;
        }

        return result * fractalBounding - 1;
    }

    @Override
    public double evaluate4D(int seed, double x, double y, double z, double w) {
        if (!supports4D()) {
            throw new UnsupportedOperationException("4D noise not supported by " + getNodeType());
        }

        // First octave (additive base)
        double result = source.evaluate4D(seed++, x, y, z, w) + 1;
        double weight = result;
        double amp = gain;

        x *= lacunarity;
        y *= lacunarity;
        z *= lacunarity;
        w *= lacunarity;

        // Remaining octaves (multiplicative blend)
        for (int i = 1; i < octaves; i++) {
            if (weight > 1) {
                weight = 1;
            }

            double noise = (source.evaluate4D(seed++, x, y, z, w) + 1) * amp;
            result += weight * noise;
            weight *= noise;

            x *= lacunarity;
            y *= lacunarity;
            z *= lacunarity;
            w *= lacunarity;
            amp *= gain;
        }

        return result * fractalBounding - 1;
    }

    @Override
    public String getNodeType() {
        return "HybridMulti";
    }

    @Override
    public String toString() {
        return String.format("HybridMultiNode(%s, octaves=%d, lacunarity=%.2f, gain=%.2f)",
            source.getNodeType(), octaves, lacunarity, gain);
    }
}
