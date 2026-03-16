package com.cognitivedynamics.noisegen.graph;

/**
 * Core interface for noise graph nodes.
 *
 * <p>All nodes in the noise graph implement this interface, providing a unified way
 * to evaluate noise at 2D, 3D, and optionally 4D coordinates. The interface uses
 * double precision coordinates to support astronomical scales (matching the
 * DoublePrecisionNoise pattern).
 *
 * <p>Nodes are immutable and thread-safe. Fluent convenience methods return new
 * node instances, enabling composable noise generation through method chaining.
 *
 * <p>Example usage:
 * <pre>{@code
 * NoiseNode terrain = graph.simplex()
 *     .scale(0.01)
 *     .multiply(graph.ridged(graph.simplex(), 5))
 *     .clamp(-1.0, 1.0);
 *
 * double height = terrain.evaluate2D(1337, x, y);
 * }</pre>
 *
 * @see NoiseGraph
 */
public interface NoiseNode {

    /**
     * Evaluate noise at a 2D coordinate.
     *
     * @param seed The random seed
     * @param x X coordinate
     * @param y Y coordinate
     * @return Noise value, typically in range [-1, 1]
     */
    double evaluate2D(int seed, double x, double y);

    /**
     * Evaluate noise at a 3D coordinate.
     *
     * @param seed The random seed
     * @param x X coordinate
     * @param y Y coordinate
     * @param z Z coordinate
     * @return Noise value, typically in range [-1, 1]
     */
    double evaluate3D(int seed, double x, double y, double z);

    /**
     * Evaluate noise at a 4D coordinate.
     * Default implementation throws UnsupportedOperationException.
     *
     * @param seed The random seed
     * @param x X coordinate
     * @param y Y coordinate
     * @param z Z coordinate
     * @param w W coordinate (often used for time-based animation)
     * @return Noise value, typically in range [-1, 1]
     * @throws UnsupportedOperationException if 4D is not supported
     */
    default double evaluate4D(int seed, double x, double y, double z, double w) {
        throw new UnsupportedOperationException("4D noise not supported by " + getNodeType());
    }

    /**
     * Check if this node supports 4D evaluation.
     *
     * @return true if evaluate4D is implemented
     */
    default boolean supports4D() {
        return false;
    }

    /**
     * Get the type identifier for this node.
     * Used for debugging and serialization.
     *
     * @return A human-readable type name
     */
    String getNodeType();

    // ==================== Fluent Convenience Methods ====================
    // These methods create new nodes that wrap this node, enabling method chaining.
    // Default implementations are provided here; they will be completed as node
    // types are implemented.

    /**
     * Scale the input coordinates uniformly.
     * Larger values result in higher frequency noise (more detail).
     *
     * @param factor The scale factor to apply to all coordinates
     * @return A new node that scales coordinates before evaluating this node
     */
    default NoiseNode scale(double factor) {
        return new com.cognitivedynamics.noisegen.graph.nodes.modifier.DomainScaleNode(this, factor);
    }

    /**
     * Add another node's output to this node's output.
     *
     * @param other The node to add
     * @return A new node that sums the outputs
     */
    default NoiseNode add(NoiseNode other) {
        return new com.cognitivedynamics.noisegen.graph.nodes.combiner.AddNode(this, other);
    }

    /**
     * Add a constant value to this node's output.
     *
     * @param constant The constant to add
     * @return A new node that adds the constant to the output
     */
    default NoiseNode add(double constant) {
        return new com.cognitivedynamics.noisegen.graph.nodes.combiner.AddNode(
            this, new com.cognitivedynamics.noisegen.graph.nodes.source.ConstantNode(constant));
    }

    /**
     * Subtract another node's output from this node's output.
     *
     * @param other The node to subtract
     * @return A new node that computes this - other
     */
    default NoiseNode subtract(NoiseNode other) {
        return new com.cognitivedynamics.noisegen.graph.nodes.combiner.SubtractNode(this, other);
    }

    /**
     * Multiply this node's output by another node's output.
     *
     * @param other The node to multiply by
     * @return A new node that multiplies the outputs
     */
    default NoiseNode multiply(NoiseNode other) {
        return new com.cognitivedynamics.noisegen.graph.nodes.combiner.MultiplyNode(this, other);
    }

    /**
     * Multiply this node's output by a constant value.
     *
     * @param constant The constant multiplier
     * @return A new node that multiplies the output by the constant
     */
    default NoiseNode multiply(double constant) {
        return new com.cognitivedynamics.noisegen.graph.nodes.combiner.MultiplyNode(
            this, new com.cognitivedynamics.noisegen.graph.nodes.source.ConstantNode(constant));
    }

    /**
     * Clamp this node's output to a range.
     *
     * @param min The minimum value
     * @param max The maximum value
     * @return A new node that clamps the output
     */
    default NoiseNode clamp(double min, double max) {
        return new com.cognitivedynamics.noisegen.graph.nodes.modifier.ClampNode(this, min, max);
    }

    /**
     * Take the absolute value of this node's output.
     *
     * @return A new node that computes |output|
     */
    default NoiseNode abs() {
        return new com.cognitivedynamics.noisegen.graph.nodes.modifier.AbsoluteNode(this);
    }

    /**
     * Invert this node's output (multiply by -1).
     *
     * @return A new node that computes -output
     */
    default NoiseNode invert() {
        return new com.cognitivedynamics.noisegen.graph.nodes.modifier.InvertNode(this);
    }

    /**
     * Apply a transform to this node's output.
     *
     * @param transform The transform to apply
     * @return A new node that applies the transform
     */
    default NoiseNode transform(com.cognitivedynamics.noisegen.transforms.NoiseTransform transform) {
        return new com.cognitivedynamics.noisegen.graph.nodes.modifier.TransformNode(this, transform);
    }

    /**
     * Apply domain warping using another noise source.
     *
     * @param warpSource The noise source for warping
     * @param amplitude The warp amplitude
     * @return A new node that applies domain warping
     */
    default NoiseNode warp(NoiseNode warpSource, double amplitude) {
        return new com.cognitivedynamics.noisegen.graph.nodes.warp.DomainWarpNode(this, warpSource, amplitude);
    }

    /**
     * Compute the minimum of this node and another.
     *
     * @param other The other node
     * @return A new node that computes min(this, other)
     */
    default NoiseNode min(NoiseNode other) {
        return new com.cognitivedynamics.noisegen.graph.nodes.combiner.MinNode(this, other);
    }

    /**
     * Compute the maximum of this node and another.
     *
     * @param other The other node
     * @return A new node that computes max(this, other)
     */
    default NoiseNode max(NoiseNode other) {
        return new com.cognitivedynamics.noisegen.graph.nodes.combiner.MaxNode(this, other);
    }

    /**
     * Offset the input coordinates.
     *
     * @param dx X offset
     * @param dy Y offset
     * @param dz Z offset
     * @return A new node with offset coordinates
     */
    default NoiseNode offset(double dx, double dy, double dz) {
        return new com.cognitivedynamics.noisegen.graph.nodes.modifier.DomainOffsetNode(this, dx, dy, dz);
    }
}
