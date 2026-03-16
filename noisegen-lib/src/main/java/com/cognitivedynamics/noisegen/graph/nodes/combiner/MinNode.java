package com.cognitivedynamics.noisegen.graph.nodes.combiner;

import com.cognitivedynamics.noisegen.graph.NoiseNode;

/**
 * Combiner node that returns the minimum of two noise sources.
 *
 * <p>Output = min(A, B)
 *
 * <p>This class is immutable and thread-safe.
 */
public final class MinNode extends CombinerNode {

    /**
     * Create a min node.
     *
     * @param inputA The first input
     * @param inputB The second input
     */
    public MinNode(NoiseNode inputA, NoiseNode inputB) {
        super(inputA, inputB);
    }

    @Override
    protected double combine(double a, double b) {
        return Math.min(a, b);
    }

    @Override
    public String getNodeType() {
        return "Min";
    }

    @Override
    public String toString() {
        return "MinNode(min(" + inputA.getNodeType() + ", " + inputB.getNodeType() + "))";
    }
}
