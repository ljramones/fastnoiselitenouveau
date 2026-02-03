package com.cognitivedynamics.noisegen.samples.nebula;

import com.cognitivedynamics.noisegen.FastNoiseLite;
import com.cognitivedynamics.noisegen.graph.NoiseGraph;
import com.cognitivedynamics.noisegen.graph.NoiseNode;
import com.cognitivedynamics.noisegen.spatial.TurbulenceNoise;

/**
 * Nebula generator using curl noise and 4D animation.
 *
 * <p>Creates realistic nebula structures with:
 * <ul>
 *   <li>Curl noise for wispy, filamentary structures</li>
 *   <li>Multiple emission layers (red hydrogen, blue oxygen, etc.)</li>
 *   <li>4D noise for time-based animation</li>
 *   <li>Domain warping for organic shapes</li>
 * </ul>
 *
 * <p>Nebulae are composed of several visual elements:
 * <ul>
 *   <li><b>Density field</b> - Overall gas distribution</li>
 *   <li><b>Filaments</b> - Thin wispy structures from turbulent flow</li>
 *   <li><b>Emission regions</b> - Bright areas where gas is ionized</li>
 *   <li><b>Dark lanes</b> - Dust absorption areas</li>
 * </ul>
 */
public class NebulaGenerator {

    private final int seed;
    private final NoiseGraph graph;

    // Noise nodes for different nebula components
    private final NoiseNode densityNode;
    private final NoiseNode filamentNode;
    private final NoiseNode emissionNode;
    private final NoiseNode dustNode;

    // Turbulence for curl noise effects
    private final TurbulenceNoise turbulence;

    // Configuration
    private final double baseFrequency;
    private final double filamentFrequency;
    private final double turbulenceStrength;

    /**
     * Create a nebula generator with default settings.
     */
    public NebulaGenerator(int seed) {
        this(seed, 0.003, 0.008, 30.0);
    }

    /**
     * Create a nebula generator with custom settings.
     *
     * @param seed               Random seed
     * @param baseFrequency      Frequency for main density structures
     * @param filamentFrequency  Frequency for fine filaments
     * @param turbulenceStrength Strength of curl noise distortion
     */
    public NebulaGenerator(int seed, double baseFrequency, double filamentFrequency,
                           double turbulenceStrength) {
        this.seed = seed;
        this.baseFrequency = baseFrequency;
        this.filamentFrequency = filamentFrequency;
        this.turbulenceStrength = turbulenceStrength;

        this.graph = NoiseGraph.create(seed);

        // Create turbulence source for curl noise
        FastNoiseLite fnl = new FastNoiseLite(seed);
        fnl.SetNoiseType(FastNoiseLite.NoiseType.OpenSimplex2S);
        fnl.SetFrequency((float) baseFrequency);
        this.turbulence = new TurbulenceNoise(fnl);

        // Build nebula components
        this.densityNode = buildDensityField();
        this.filamentNode = buildFilaments();
        this.emissionNode = buildEmissionRegions();
        this.dustNode = buildDustLanes();
    }

    /**
     * Build the main density field - overall gas distribution.
     */
    private NoiseNode buildDensityField() {
        // Base cloud shapes - large scale structure
        NoiseNode baseCloud = graph.fbm(
            graph.simplex().frequency(baseFrequency),
            4, 2.0, 0.5
        );

        // Add some cellular structure for interesting shapes
        NoiseNode cells = graph.cellular().frequency(baseFrequency * 0.5);

        // Combine for varied density
        NoiseNode combined = baseCloud.multiply(0.7)
            .add(cells.multiply(0.3));

        // Warp for organic look
        NoiseNode warpSource = graph.simplex().frequency(baseFrequency * 0.3);
        return combined.warp(warpSource, turbulenceStrength);
    }

    /**
     * Build filament structures - thin wispy features.
     */
    private NoiseNode buildFilaments() {
        // Ridged noise creates strand-like patterns
        NoiseNode ridges = graph.ridged(
            graph.simplex().frequency(filamentFrequency),
            5, 2.2, 0.5
        );

        // High-frequency detail
        NoiseNode detail = graph.fbm(
            graph.simplex().frequency(filamentFrequency * 3),
            3, 2.0, 0.5
        ).multiply(0.3);

        // Combine
        NoiseNode combined = ridges.add(detail);

        // Strong warping for wispy appearance
        NoiseNode warpSource = graph.simplex().frequency(filamentFrequency * 0.5);
        return combined.warp(warpSource, turbulenceStrength * 1.5);
    }

    /**
     * Build emission regions - bright ionized gas areas.
     */
    private NoiseNode buildEmissionRegions() {
        // Soft, blob-like regions
        NoiseNode blobs = graph.billow(
            graph.simplex().frequency(baseFrequency * 1.5),
            4, 2.0, 0.5
        );

        // Threshold to create distinct emission areas
        return blobs.clamp(0.2, 1.0)
            .multiply(1.25)  // Brighten
            .add(graph.constant(-0.25));  // Shift
    }

    /**
     * Build dust lanes - dark absorption areas.
     */
    private NoiseNode buildDustLanes() {
        // Elongated structures
        NoiseNode dust = graph.fbm(
            graph.simplex().frequency(baseFrequency * 2),
            3, 2.5, 0.4  // Higher lacunarity for streaky appearance
        );

        // Warp to create lanes
        NoiseNode warpSource = graph.simplex().frequency(baseFrequency);
        return dust.warp(warpSource, turbulenceStrength * 0.8);
    }

    /**
     * Sample the nebula at a point with time for animation.
     *
     * @param x    X coordinate
     * @param y    Y coordinate
     * @param time Time value for animation (use as Z or W dimension)
     * @return NebulaData containing density and color channel values
     */
    public NebulaData sample(double x, double y, double time) {
        // Apply time-varying curl noise displacement for fluid-like motion
        // The curl field itself evolves with time
        float[] curl = turbulence.curl3D((float) x, (float) y, (float) (time * 0.3));
        double displacedX = x + curl[0] * turbulenceStrength * 0.5;
        double displacedY = y + curl[1] * turbulenceStrength * 0.5;

        // Sample density using displaced coordinates
        // Use time as Z coordinate for animation - faster multiplier for visible motion
        double density = densityNode.evaluate3D(seed, displacedX, displacedY, time * 0.5);

        // Sample filaments with different time rate for layered motion
        double filaments = filamentNode.evaluate3D(seed + 1000, displacedX, displacedY, time * 0.7);

        // Sample emission regions - slower, more stable
        double emission = emissionNode.evaluate3D(seed + 2000, x, y, time * 0.3);

        // Sample dust - medium speed
        double dust = dustNode.evaluate3D(seed + 3000, x, y, time * 0.4);

        return new NebulaData(density, filaments, emission, dust);
    }

    /**
     * Sample with curl noise flow field for particle advection.
     */
    public float[] getFlowField(double x, double y, double z) {
        return turbulence.curl3D((float) x, (float) y, (float) z);
    }

    /**
     * Data class holding nebula sample values.
     */
    public record NebulaData(double density, double filaments, double emission, double dust) {

        /**
         * Get combined brightness (0-1 range).
         */
        public double brightness() {
            double base = (density + 1) * 0.5;  // Map from [-1,1] to [0,1]
            double fil = Math.max(0, filaments) * 0.5;
            double emit = Math.max(0, emission) * 0.8;
            double combined = base * 0.4 + fil * 0.4 + emit * 0.5;
            // Reduce by dust
            double dustFactor = 1.0 - Math.max(0, dust) * 0.3;
            return Math.min(1, Math.max(0, combined * dustFactor));
        }

        /**
         * Get red channel (hydrogen-alpha emission).
         */
        public double red() {
            double base = brightness();
            double emitBoost = Math.max(0, emission) * 0.4;
            return Math.min(1, base * 0.9 + emitBoost);
        }

        /**
         * Get green channel (mixed emission).
         */
        public double green() {
            double base = brightness();
            double filBoost = Math.max(0, filaments) * 0.2;
            return Math.min(1, base * 0.6 + filBoost);
        }

        /**
         * Get blue channel (oxygen emission, reflection).
         */
        public double blue() {
            double base = brightness();
            double densityBoost = Math.max(0, density) * 0.3;
            return Math.min(1, base * 0.8 + densityBoost);
        }
    }

    // Getters
    public int getSeed() { return seed; }
    public double getBaseFrequency() { return baseFrequency; }
    public double getFilamentFrequency() { return filamentFrequency; }
    public double getTurbulenceStrength() { return turbulenceStrength; }
}
