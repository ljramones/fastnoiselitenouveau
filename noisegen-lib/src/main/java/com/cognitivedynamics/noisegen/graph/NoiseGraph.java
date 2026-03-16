package com.cognitivedynamics.noisegen.graph;

import com.cognitivedynamics.noisegen.NoiseTypes.CellularDistanceFunction;
import com.cognitivedynamics.noisegen.NoiseTypes.CellularReturnType;
import com.cognitivedynamics.noisegen.graph.nodes.combiner.*;
import com.cognitivedynamics.noisegen.graph.nodes.fractal.*;
import com.cognitivedynamics.noisegen.graph.nodes.modifier.*;
import com.cognitivedynamics.noisegen.graph.nodes.source.*;
import com.cognitivedynamics.noisegen.graph.nodes.warp.DomainWarpNode;
import com.cognitivedynamics.noisegen.transforms.NoiseTransform;

/**
 * Factory class for creating noise graph nodes with fluent API support.
 *
 * <p>NoiseGraph provides convenient factory methods for creating all types of
 * noise nodes. It stores a default seed that can be used during evaluation.
 *
 * <p>Example usage:
 * <pre>{@code
 * NoiseGraph g = NoiseGraph.create(1337);
 *
 * // Simple fractal noise
 * NoiseNode terrain = g.fbm(g.simplex().frequency(0.01), 5)
 *     .add(g.ridged(g.simplex().frequency(0.02), 4).multiply(0.3))
 *     .clamp(-1.0, 1.0);
 *
 * // Evaluate
 * double height = terrain.evaluate2D(g.getSeed(), 100.0, 200.0);
 * }</pre>
 *
 * <p>This class is immutable and thread-safe.
 */
public final class NoiseGraph {

    private final int seed;

    private NoiseGraph(int seed) {
        this.seed = seed;
    }

    /**
     * Create a new NoiseGraph with the specified seed.
     *
     * @param seed The default seed for noise evaluation
     * @return A new NoiseGraph instance
     */
    public static NoiseGraph create(int seed) {
        return new NoiseGraph(seed);
    }

    /**
     * Create a new NoiseGraph with a default seed of 1337.
     *
     * @return A new NoiseGraph instance
     */
    public static NoiseGraph create() {
        return new NoiseGraph(1337);
    }

    /**
     * Get the default seed.
     *
     * @return The seed value
     */
    public int getSeed() {
        return seed;
    }

    // ==================== Source Nodes ====================

    /**
     * Create a constant value node.
     *
     * @param value The constant value
     * @return A new ConstantNode
     */
    public ConstantNode constant(double value) {
        return new ConstantNode(value);
    }

    /**
     * Create a simplex noise source (OpenSimplex2).
     *
     * @return A new SimplexSourceNode
     */
    public SimplexSourceNode simplex() {
        return new SimplexSourceNode(false);
    }

    /**
     * Create a smooth simplex noise source (OpenSimplex2S).
     *
     * @return A new SimplexSourceNode with smooth variant
     */
    public SimplexSourceNode simplexSmooth() {
        return new SimplexSourceNode(true);
    }

    /**
     * Create a Perlin noise source.
     *
     * @return A new PerlinSourceNode
     */
    public PerlinSourceNode perlin() {
        return new PerlinSourceNode();
    }

    /**
     * Create a value noise source.
     *
     * @return A new ValueSourceNode
     */
    public ValueSourceNode value() {
        return new ValueSourceNode(false);
    }

    /**
     * Create a value noise source with cubic interpolation.
     *
     * @return A new ValueSourceNode with cubic interpolation
     */
    public ValueSourceNode valueCubic() {
        return new ValueSourceNode(true);
    }

    /**
     * Create a cellular (Voronoi) noise source with default settings.
     *
     * @return A new CellularSourceNode
     */
    public CellularSourceNode cellular() {
        return new CellularSourceNode(
            CellularDistanceFunction.EuclideanSq,
            CellularReturnType.Distance,
            1.0
        );
    }

    /**
     * Create a cellular noise source with specified settings.
     *
     * @param distanceFunction The distance function to use
     * @param returnType The return type
     * @param jitterModifier The jitter modifier (1.0 = normal)
     * @return A new CellularSourceNode
     */
    public CellularSourceNode cellular(
            CellularDistanceFunction distanceFunction,
            CellularReturnType returnType,
            double jitterModifier) {
        return new CellularSourceNode(distanceFunction, returnType, jitterModifier);
    }

    /**
     * Create a 4D simplex noise source.
     *
     * @return A new Simplex4DSourceNode
     */
    public Simplex4DSourceNode simplex4D() {
        return new Simplex4DSourceNode();
    }

    // ==================== Fractal Nodes ====================

    /**
     * Create an FBm (Fractional Brownian motion) fractal node.
     *
     * @param source The source node
     * @param octaves The number of octaves
     * @return A new FBmNode
     */
    public FBmNode fbm(NoiseNode source, int octaves) {
        return new FBmNode(source, octaves);
    }

    /**
     * Create an FBm fractal node with full parameters.
     *
     * @param source The source node
     * @param octaves The number of octaves
     * @param lacunarity The frequency multiplier per octave
     * @param gain The amplitude multiplier per octave
     * @return A new FBmNode
     */
    public FBmNode fbm(NoiseNode source, int octaves, double lacunarity, double gain) {
        return new FBmNode(source, octaves, lacunarity, gain);
    }

    /**
     * Create a ridged multifractal node.
     *
     * @param source The source node
     * @param octaves The number of octaves
     * @return A new RidgedNode
     */
    public RidgedNode ridged(NoiseNode source, int octaves) {
        return new RidgedNode(source, octaves);
    }

    /**
     * Create a ridged multifractal node with full parameters.
     *
     * @param source The source node
     * @param octaves The number of octaves
     * @param lacunarity The frequency multiplier per octave
     * @param gain The amplitude multiplier per octave
     * @return A new RidgedNode
     */
    public RidgedNode ridged(NoiseNode source, int octaves, double lacunarity, double gain) {
        return new RidgedNode(source, octaves, lacunarity, gain);
    }

    /**
     * Create a billow fractal node.
     *
     * @param source The source node
     * @param octaves The number of octaves
     * @return A new BillowNode
     */
    public BillowNode billow(NoiseNode source, int octaves) {
        return new BillowNode(source, octaves);
    }

    /**
     * Create a billow fractal node with full parameters.
     *
     * @param source The source node
     * @param octaves The number of octaves
     * @param lacunarity The frequency multiplier per octave
     * @param gain The amplitude multiplier per octave
     * @return A new BillowNode
     */
    public BillowNode billow(NoiseNode source, int octaves, double lacunarity, double gain) {
        return new BillowNode(source, octaves, lacunarity, gain);
    }

    /**
     * Create a hybrid multifractal node.
     *
     * @param source The source node
     * @param octaves The number of octaves
     * @return A new HybridMultiNode
     */
    public HybridMultiNode hybridMulti(NoiseNode source, int octaves) {
        return new HybridMultiNode(source, octaves);
    }

    /**
     * Create a hybrid multifractal node with full parameters.
     *
     * @param source The source node
     * @param octaves The number of octaves
     * @param lacunarity The frequency multiplier per octave
     * @param gain The amplitude multiplier per octave
     * @return A new HybridMultiNode
     */
    public HybridMultiNode hybridMulti(NoiseNode source, int octaves, double lacunarity, double gain) {
        return new HybridMultiNode(source, octaves, lacunarity, gain);
    }

    // ==================== Combiner Nodes ====================

    /**
     * Create an add node.
     *
     * @param a First input
     * @param b Second input
     * @return A new AddNode
     */
    public AddNode add(NoiseNode a, NoiseNode b) {
        return new AddNode(a, b);
    }

    /**
     * Create a subtract node.
     *
     * @param a First input (minuend)
     * @param b Second input (subtrahend)
     * @return A new SubtractNode computing a - b
     */
    public SubtractNode subtract(NoiseNode a, NoiseNode b) {
        return new SubtractNode(a, b);
    }

    /**
     * Create a multiply node.
     *
     * @param a First input
     * @param b Second input
     * @return A new MultiplyNode
     */
    public MultiplyNode multiply(NoiseNode a, NoiseNode b) {
        return new MultiplyNode(a, b);
    }

    /**
     * Create a min node.
     *
     * @param a First input
     * @param b Second input
     * @return A new MinNode
     */
    public MinNode min(NoiseNode a, NoiseNode b) {
        return new MinNode(a, b);
    }

    /**
     * Create a max node.
     *
     * @param a First input
     * @param b Second input
     * @return A new MaxNode
     */
    public MaxNode max(NoiseNode a, NoiseNode b) {
        return new MaxNode(a, b);
    }

    /**
     * Create a blend node that interpolates between two sources.
     *
     * @param a First input
     * @param b Second input
     * @param control Control signal (typically 0-1, where 0=a, 1=b)
     * @return A new BlendNode
     */
    public BlendNode blend(NoiseNode a, NoiseNode b, NoiseNode control) {
        return new BlendNode(a, b, control);
    }

    // ==================== Modifier Nodes ====================

    /**
     * Create a domain scale node.
     *
     * @param source The source node
     * @param factor The scale factor
     * @return A new DomainScaleNode
     */
    public DomainScaleNode scale(NoiseNode source, double factor) {
        return new DomainScaleNode(source, factor);
    }

    /**
     * Create a domain offset node.
     *
     * @param source The source node
     * @param dx X offset
     * @param dy Y offset
     * @param dz Z offset
     * @return A new DomainOffsetNode
     */
    public DomainOffsetNode offset(NoiseNode source, double dx, double dy, double dz) {
        return new DomainOffsetNode(source, dx, dy, dz);
    }

    /**
     * Create a clamp node.
     *
     * @param source The source node
     * @param min Minimum value
     * @param max Maximum value
     * @return A new ClampNode
     */
    public ClampNode clamp(NoiseNode source, double min, double max) {
        return new ClampNode(source, min, max);
    }

    /**
     * Create an absolute value node.
     *
     * @param source The source node
     * @return A new AbsoluteNode
     */
    public AbsoluteNode abs(NoiseNode source) {
        return new AbsoluteNode(source);
    }

    /**
     * Create an invert node.
     *
     * @param source The source node
     * @return A new InvertNode
     */
    public InvertNode invert(NoiseNode source) {
        return new InvertNode(source);
    }

    /**
     * Create a transform node.
     *
     * @param source The source node
     * @param transform The transform to apply
     * @return A new TransformNode
     */
    public TransformNode transform(NoiseNode source, NoiseTransform transform) {
        return new TransformNode(source, transform);
    }

    // ==================== Warp Nodes ====================

    /**
     * Create a domain warp node.
     *
     * @param source The source node to warp
     * @param warpSource The warp noise source
     * @param amplitude The warp amplitude
     * @return A new DomainWarpNode
     */
    public DomainWarpNode warp(NoiseNode source, NoiseNode warpSource, double amplitude) {
        return new DomainWarpNode(source, warpSource, amplitude);
    }
}
