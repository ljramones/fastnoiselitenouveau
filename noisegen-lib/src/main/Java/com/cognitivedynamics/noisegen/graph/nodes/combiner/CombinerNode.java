package com.cognitivedynamics.noisegen.graph.nodes.combiner;

import com.cognitivedynamics.noisegen.graph.NoiseNode;

/**
 * Abstract base class for binary combiner nodes.
 *
 * <p>Combiner nodes take two input nodes and combine their outputs using
 * some operation (add, multiply, min, max, etc.).
 *
 * <p>This class is immutable and thread-safe.
 */
public abstract class CombinerNode implements NoiseNode {

    protected final NoiseNode inputA;
    protected final NoiseNode inputB;

    /**
     * Create a combiner node with two inputs.
     *
     * @param inputA The first input node
     * @param inputB The second input node
     */
    protected CombinerNode(NoiseNode inputA, NoiseNode inputB) {
        if (inputA == null || inputB == null) {
            throw new IllegalArgumentException("Input nodes cannot be null");
        }
        this.inputA = inputA;
        this.inputB = inputB;
    }

    /**
     * Get the first input node.
     *
     * @return The first input
     */
    public NoiseNode getInputA() {
        return inputA;
    }

    /**
     * Get the second input node.
     *
     * @return The second input
     */
    public NoiseNode getInputB() {
        return inputB;
    }

    /**
     * Combine two values using the combiner's operation.
     *
     * @param a The first value
     * @param b The second value
     * @return The combined result
     */
    protected abstract double combine(double a, double b);

    @Override
    public double evaluate2D(int seed, double x, double y) {
        double a = inputA.evaluate2D(seed, x, y);
        double b = inputB.evaluate2D(seed, x, y);
        return combine(a, b);
    }

    @Override
    public double evaluate3D(int seed, double x, double y, double z) {
        double a = inputA.evaluate3D(seed, x, y, z);
        double b = inputB.evaluate3D(seed, x, y, z);
        return combine(a, b);
    }

    @Override
    public double evaluate4D(int seed, double x, double y, double z, double w) {
        if (!supports4D()) {
            throw new UnsupportedOperationException("4D noise not supported by " + getNodeType());
        }
        double a = inputA.evaluate4D(seed, x, y, z, w);
        double b = inputB.evaluate4D(seed, x, y, z, w);
        return combine(a, b);
    }

    @Override
    public boolean supports4D() {
        return inputA.supports4D() && inputB.supports4D();
    }
}
