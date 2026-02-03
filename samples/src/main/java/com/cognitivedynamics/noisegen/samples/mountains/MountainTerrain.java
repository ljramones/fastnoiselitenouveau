package com.cognitivedynamics.noisegen.samples.mountains;

import com.cognitivedynamics.noisegen.graph.NoiseGraph;
import com.cognitivedynamics.noisegen.graph.NoiseNode;
import com.cognitivedynamics.noisegen.graph.util.BulkEvaluator;

/**
 * Mountain terrain generator optimized for dramatic 3D visualization.
 *
 * <p>Uses the Node Graph System to create realistic alpine terrain with:
 * <ul>
 *   <li>Sharp ridged peaks using ridged multifractal noise</li>
 *   <li>Erosion-like detail using hybrid multifractal</li>
 *   <li>Domain warping for organic, non-uniform mountain ranges</li>
 *   <li>Multiple octaves for detail at all scales</li>
 * </ul>
 */
public class MountainTerrain {

    private final NoiseGraph graph;
    private final int seed;
    private final NoiseNode terrainNode;

    // Configurable parameters
    private final double baseFrequency;
    private final double ridgeFrequency;
    private final double detailFrequency;
    private final double warpAmplitude;
    private final double heightScale;

    /**
     * Create mountain terrain with default settings.
     */
    public MountainTerrain(int seed) {
        this(seed, 0.003, 0.006, 0.02, 80.0, 1.0);
    }

    /**
     * Create mountain terrain with custom settings.
     *
     * @param seed            Random seed
     * @param baseFrequency   Frequency for base terrain shape
     * @param ridgeFrequency  Frequency for ridge features
     * @param detailFrequency Frequency for fine detail
     * @param warpAmplitude   Domain warp strength for organic shapes
     * @param heightScale     Overall height multiplier
     */
    public MountainTerrain(int seed, double baseFrequency, double ridgeFrequency,
                           double detailFrequency, double warpAmplitude, double heightScale) {
        this.seed = seed;
        this.baseFrequency = baseFrequency;
        this.ridgeFrequency = ridgeFrequency;
        this.detailFrequency = detailFrequency;
        this.warpAmplitude = warpAmplitude;
        this.heightScale = heightScale;

        this.graph = NoiseGraph.create(seed);
        this.terrainNode = buildTerrain();
    }

    private NoiseNode buildTerrain() {
        // Base terrain shape - broad mountain ranges
        // Using FBm for smooth underlying terrain
        NoiseNode baseShape = graph.fbm(
            graph.simplex().frequency(baseFrequency),
            4, 2.0, 0.5
        );

        // Ridge layer - sharp mountain peaks
        // Ridged noise creates the dramatic alpine peaks
        NoiseNode ridges = graph.ridged(
            graph.simplex().frequency(ridgeFrequency),
            6,      // More octaves for detailed ridges
            2.2,    // Higher lacunarity for sharper detail
            0.5
        );

        // Erosion-like detail using hybrid multifractal
        // This adds detail where there's already detail (like erosion patterns)
        NoiseNode erosion = graph.hybridMulti(
            graph.simplex().frequency(detailFrequency),
            4, 2.0, 0.5
        ).multiply(0.15);

        // Mountain mask - controls where peaks appear
        // Higher values = more mountainous
        NoiseNode mountainMask = graph.fbm(
            graph.simplex().frequency(baseFrequency * 0.5),
            3
        ).clamp(-0.5, 1.0)
         .multiply(0.5)
         .add(graph.constant(0.5));  // Map to roughly [0, 1]

        // Combine base with ridges, weighted by mountain mask
        NoiseNode mountains = baseShape.multiply(0.4)
            .add(ridges.multiply(mountainMask).multiply(0.6));

        // Add erosion detail
        NoiseNode detailed = mountains.add(erosion);

        // Domain warp for organic, non-grid-aligned features
        NoiseNode warpSource = graph.simplex().frequency(baseFrequency * 0.7);
        NoiseNode warped = detailed.warp(warpSource, warpAmplitude);

        // Fine detail layer - small rocks and surface texture
        NoiseNode fineDetail = graph.fbm(
            graph.simplex().frequency(detailFrequency * 3),
            3, 2.0, 0.5
        ).multiply(0.05);

        // Final combination
        NoiseNode combined = warped.add(fineDetail);

        // Scale and bias for good height distribution
        // We want mostly positive values with some valleys
        return combined
            .multiply(heightScale)
            .add(graph.constant(0.3))  // Bias upward
            .clamp(-0.2, 1.5);         // Allow some valleys, tall peaks
    }

    /**
     * Get height at a single point.
     */
    public double getHeight(double x, double y) {
        return terrainNode.evaluate2D(seed, x, y);
    }

    /**
     * Generate a heightmap for 3D mesh generation.
     *
     * @param width   Width in samples
     * @param height  Height in samples
     * @param startX  Starting X coordinate
     * @param startY  Starting Y coordinate
     * @param step    Distance between samples
     * @return 2D array of height values [y][x]
     */
    public double[][] generateHeightmap(int width, int height,
                                        double startX, double startY, double step) {
        BulkEvaluator evaluator = new BulkEvaluator(seed);
        return evaluator.fill2D(terrainNode, width, height, startX, startY, step);
    }

    /**
     * Generate a heightmap as float array for mesh generation.
     */
    public float[][] generateHeightmapFloat(int width, int height,
                                            double startX, double startY, double step) {
        double[][] doubles = generateHeightmap(width, height, startX, startY, step);
        float[][] floats = new float[height][width];
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                floats[y][x] = (float) doubles[y][x];
            }
        }
        return floats;
    }

    // Getters for UI binding
    public int getSeed() { return seed; }
    public double getBaseFrequency() { return baseFrequency; }
    public double getRidgeFrequency() { return ridgeFrequency; }
    public double getDetailFrequency() { return detailFrequency; }
    public double getWarpAmplitude() { return warpAmplitude; }
    public double getHeightScale() { return heightScale; }
}
