package com.cognitivedynamics.noisegen.samples.multibiome;

import com.cognitivedynamics.noisegen.graph.NoiseGraph;
import com.cognitivedynamics.noisegen.graph.NoiseNode;
import com.cognitivedynamics.noisegen.graph.util.BulkEvaluator;

/**
 * Multi-Biome Terrain Generator using the Node Graph System.
 *
 * <p>This example demonstrates how to build complex, layered terrain using
 * the composable noise graph API. The terrain features multiple biomes
 * (oceans, plains, mountains) with smooth transitions and organic shapes.
 *
 * <h2>Architecture Overview</h2>
 * The terrain is built from several noise layers:
 * <ol>
 *   <li><b>Continental Shape</b> - Very low frequency noise defining landmasses vs oceans</li>
 *   <li><b>Mountain Ranges</b> - Ridged noise for dramatic peaks, masked by a mountain selector</li>
 *   <li><b>Rolling Hills</b> - Medium frequency FBm for gentle terrain variation</li>
 *   <li><b>Fine Detail</b> - High frequency noise for small-scale features</li>
 *   <li><b>Domain Warping</b> - Applied to create organic, non-grid-aligned coastlines</li>
 * </ol>
 *
 * <h2>Key Techniques Demonstrated</h2>
 * <ul>
 *   <li>Layered noise composition with add/multiply</li>
 *   <li>Biome blending using noise-based control signals</li>
 *   <li>Domain warping for organic coastlines</li>
 *   <li>Frequency separation for different terrain scales</li>
 *   <li>Clamping and range control</li>
 * </ul>
 */
public class MultiBiomeTerrain {

    private final NoiseGraph graph;
    private final int seed;

    // The final composed terrain node
    private final NoiseNode terrainNode;

    // Individual layers (exposed for educational purposes)
    private final NoiseNode continentalNode;
    private final NoiseNode mountainNode;
    private final NoiseNode hillsNode;
    private final NoiseNode detailNode;

    // Configuration
    private final double continentalScale;
    private final double mountainScale;
    private final double hillScale;
    private final double detailScale;
    private final double warpAmplitude;

    /**
     * Create a multi-biome terrain generator with default settings.
     *
     * @param seed Random seed for reproducible generation
     */
    public MultiBiomeTerrain(int seed) {
        this(seed, 0.002, 0.008, 0.02, 0.1, 50.0);
    }

    /**
     * Create a multi-biome terrain generator with custom scale settings.
     *
     * @param seed             Random seed
     * @param continentalScale Frequency for continental shapes (lower = larger continents)
     * @param mountainScale    Frequency for mountain ranges
     * @param hillScale        Frequency for rolling hills
     * @param detailScale      Frequency for fine detail
     * @param warpAmplitude    Strength of domain warping for organic shapes
     */
    public MultiBiomeTerrain(int seed, double continentalScale, double mountainScale,
                             double hillScale, double detailScale, double warpAmplitude) {
        this.seed = seed;
        this.continentalScale = continentalScale;
        this.mountainScale = mountainScale;
        this.hillScale = hillScale;
        this.detailScale = detailScale;
        this.warpAmplitude = warpAmplitude;

        this.graph = NoiseGraph.create(seed);

        // Build each terrain layer
        this.continentalNode = buildContinentalLayer();
        this.mountainNode = buildMountainLayer();
        this.hillsNode = buildHillsLayer();
        this.detailNode = buildDetailLayer();

        // Compose the final terrain
        this.terrainNode = buildCompositeTerrain();
    }

    // ==================== Layer Construction ====================

    /**
     * Build the continental layer - defines the basic landmass vs ocean shapes.
     *
     * <p>Uses very low frequency noise with domain warping to create
     * organic, continent-like shapes. The warping prevents the coastlines
     * from looking too regular or grid-aligned.
     */
    private NoiseNode buildContinentalLayer() {
        // Base continental noise - very low frequency for large landmasses
        NoiseNode continents = graph.fbm(
            graph.simplex().frequency(continentalScale),
            4,      // 4 octaves for some detail in coastlines
            2.0,    // Standard lacunarity
            0.5     // Standard gain
        );

        // Warp source for organic coastlines
        // Uses different frequency to avoid correlation with the main noise
        NoiseNode warpSource = graph.simplex().frequency(continentalScale * 0.7);

        // Apply domain warping for organic shapes
        // This is key to avoiding "blobby" or grid-aligned continents
        return continents.warp(warpSource, warpAmplitude);
    }

    /**
     * Build the mountain layer - creates dramatic ridged peaks.
     *
     * <p>Uses ridged multifractal noise which naturally creates ridge-like
     * formations perfect for mountain ranges. This is blended with the
     * continental layer so mountains only appear on land.
     */
    private NoiseNode buildMountainLayer() {
        // Ridged noise creates sharp peaks and valleys
        NoiseNode ridges = graph.ridged(
            graph.simplex().frequency(mountainScale),
            5,      // More octaves for detailed ridges
            2.2,    // Slightly higher lacunarity for sharper detail
            0.5
        );

        // Mountain placement selector - determines WHERE mountains appear
        // Uses different seed offset to decorrelate from continental shape
        NoiseNode mountainSelector = graph.fbm(
            graph.simplex().frequency(continentalScale * 1.5),
            3
        ).clamp(-1, 1)
         .multiply(0.5)
         .add(graph.constant(0.5));  // Map to [0, 1]

        // Only show mountains where selector is high AND we're on land
        // This creates isolated mountain ranges rather than mountains everywhere
        return ridges.multiply(mountainSelector);
    }

    /**
     * Build the hills layer - gentle rolling terrain variation.
     *
     * <p>Standard FBm noise at medium frequency creates the "background"
     * terrain variation - rolling hills, gentle valleys, etc.
     */
    private NoiseNode buildHillsLayer() {
        return graph.fbm(
            graph.simplex().frequency(hillScale),
            4,
            2.0,
            0.5
        );
    }

    /**
     * Build the detail layer - fine-scale terrain features.
     *
     * <p>High frequency noise adds small rocks, bumps, and surface
     * irregularities. This is scaled down significantly so it doesn't
     * dominate the terrain.
     */
    private NoiseNode buildDetailLayer() {
        return graph.fbm(
            graph.simplex().frequency(detailScale),
            3,
            2.0,
            0.5
        ).multiply(0.1);  // Scale down to be subtle
    }

    /**
     * Compose all layers into the final terrain.
     *
     * <p>The key insight here is that different layers contribute
     * different amounts based on context:
     * <ul>
     *   <li>Continental layer dominates (sets land vs water)</li>
     *   <li>Mountains are masked to only appear on high continental values</li>
     *   <li>Hills provide everywhere variation</li>
     *   <li>Detail is always subtle</li>
     * </ul>
     */
    private NoiseNode buildCompositeTerrain() {
        // Create a "land mask" from continental layer
        // This is 0 in deep ocean, ramping up to 1 on land
        NoiseNode landMask = continentalNode
            .add(graph.constant(0.3))  // Shift so more area is "land"
            .clamp(0, 1);

        // Mountains only appear where both:
        // 1. The mountain layer has high values (ridges)
        // 2. We're solidly on land (high continental value)
        NoiseNode maskedMountains = mountainNode
            .multiply(landMask)
            .multiply(0.4);  // Scale mountain contribution

        // Hills are everywhere but stronger on land
        NoiseNode blendedHills = hillsNode
            .multiply(0.15);  // Subtle hills everywhere

        // Combine all layers:
        // Continental provides the base elevation
        // + Mountains add dramatic peaks where appropriate
        // + Hills add rolling variation
        // + Detail adds fine texture
        NoiseNode combined = continentalNode
            .add(maskedMountains)
            .add(blendedHills)
            .add(detailNode);

        // Final clamping to ensure we stay in [-1, 1]
        return combined.clamp(-1.0, 1.0);
    }

    // ==================== Evaluation Methods ====================

    /**
     * Get the terrain height at a single point.
     *
     * @param x X coordinate
     * @param y Y coordinate
     * @return Height value in range [-1, 1]
     */
    public double getHeight(double x, double y) {
        return terrainNode.evaluate2D(seed, x, y);
    }

    /**
     * Get the biome at a given position based on elevation.
     *
     * @param x X coordinate
     * @param y Y coordinate
     * @return The biome type at this location
     */
    public BiomeType getBiome(double x, double y) {
        double elevation = getHeight(x, y);
        return BiomeType.fromElevation(elevation);
    }

    /**
     * Generate a heightmap for a region.
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

    // ==================== Individual Layer Access ====================
    // These are exposed for educational purposes - you can visualize each
    // layer independently to understand how they combine.

    /**
     * Get the continental layer value (landmass shapes).
     */
    public double getContinentalValue(double x, double y) {
        return continentalNode.evaluate2D(seed, x, y);
    }

    /**
     * Get the mountain layer value (ridged peaks).
     */
    public double getMountainValue(double x, double y) {
        return mountainNode.evaluate2D(seed, x, y);
    }

    /**
     * Get the hills layer value (rolling terrain).
     */
    public double getHillsValue(double x, double y) {
        return hillsNode.evaluate2D(seed, x, y);
    }

    /**
     * Get the detail layer value (fine features).
     */
    public double getDetailValue(double x, double y) {
        return detailNode.evaluate2D(seed, x, y);
    }

    // ==================== Accessors ====================

    public int getSeed() {
        return seed;
    }

    public NoiseNode getTerrainNode() {
        return terrainNode;
    }

    public double getContinentalScale() {
        return continentalScale;
    }

    public double getMountainScale() {
        return mountainScale;
    }

    public double getHillScale() {
        return hillScale;
    }

    public double getDetailScale() {
        return detailScale;
    }

    public double getWarpAmplitude() {
        return warpAmplitude;
    }
}
