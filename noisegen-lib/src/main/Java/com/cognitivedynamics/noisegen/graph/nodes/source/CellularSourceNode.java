package com.cognitivedynamics.noisegen.graph.nodes.source;

import com.cognitivedynamics.noisegen.NoiseTypes.CellularDistanceFunction;
import com.cognitivedynamics.noisegen.NoiseTypes.CellularReturnType;
import com.cognitivedynamics.noisegen.generators.CellularNoiseGen;
import com.cognitivedynamics.noisegen.generators.NoiseGenerator;

/**
 * Cellular (Voronoi/Worley) noise source node.
 *
 * <p>Wraps CellularNoiseGen to provide cellular noise as a graph node.
 * Cellular noise creates patterns based on distances to randomly distributed
 * feature points, useful for stone textures, cell structures, and more.
 *
 * <p>This class is immutable and thread-safe.
 */
public final class CellularSourceNode extends SourceNode {

    private final CellularNoiseGen generator;
    private final CellularDistanceFunction distanceFunction;
    private final CellularReturnType returnType;
    private final double jitterModifier;

    /**
     * Create a cellular source node with specified settings.
     *
     * @param distanceFunction The distance function to use
     * @param returnType The type of value to return
     * @param jitterModifier The jitter modifier (1.0 = normal jitter)
     */
    public CellularSourceNode(
            CellularDistanceFunction distanceFunction,
            CellularReturnType returnType,
            double jitterModifier) {
        this(distanceFunction, returnType, jitterModifier, 1.0);
    }

    /**
     * Create a cellular source node with specified settings and frequency.
     *
     * @param distanceFunction The distance function to use
     * @param returnType The type of value to return
     * @param jitterModifier The jitter modifier (1.0 = normal jitter)
     * @param frequency The frequency multiplier
     */
    public CellularSourceNode(
            CellularDistanceFunction distanceFunction,
            CellularReturnType returnType,
            double jitterModifier,
            double frequency) {
        super(frequency);
        this.distanceFunction = distanceFunction;
        this.returnType = returnType;
        this.jitterModifier = jitterModifier;
        this.generator = new CellularNoiseGen(distanceFunction, returnType, (float) jitterModifier);
    }

    /**
     * Get the distance function.
     *
     * @return The distance function
     */
    public CellularDistanceFunction getDistanceFunction() {
        return distanceFunction;
    }

    /**
     * Get the return type.
     *
     * @return The return type
     */
    public CellularReturnType getReturnType() {
        return returnType;
    }

    /**
     * Get the jitter modifier.
     *
     * @return The jitter modifier
     */
    public double getJitterModifier() {
        return jitterModifier;
    }

    /**
     * Create a new node with a different distance function.
     *
     * @param newDistanceFunction The new distance function
     * @return A new CellularSourceNode
     */
    public CellularSourceNode withDistanceFunction(CellularDistanceFunction newDistanceFunction) {
        return new CellularSourceNode(newDistanceFunction, returnType, jitterModifier, frequency);
    }

    /**
     * Create a new node with a different return type.
     *
     * @param newReturnType The new return type
     * @return A new CellularSourceNode
     */
    public CellularSourceNode withReturnType(CellularReturnType newReturnType) {
        return new CellularSourceNode(distanceFunction, newReturnType, jitterModifier, frequency);
    }

    /**
     * Create a new node with a different jitter modifier.
     *
     * @param newJitterModifier The new jitter modifier
     * @return A new CellularSourceNode
     */
    public CellularSourceNode withJitter(double newJitterModifier) {
        return new CellularSourceNode(distanceFunction, returnType, newJitterModifier, frequency);
    }

    @Override
    protected NoiseGenerator getGenerator() {
        return generator;
    }

    @Override
    public CellularSourceNode frequency(double newFrequency) {
        return new CellularSourceNode(distanceFunction, returnType, jitterModifier, newFrequency);
    }

    @Override
    public String getNodeType() {
        return "Cellular";
    }

    @Override
    public String toString() {
        return String.format("CellularSourceNode(distance=%s, return=%s, jitter=%.2f, frequency=%.4f)",
            distanceFunction, returnType, jitterModifier, frequency);
    }
}
