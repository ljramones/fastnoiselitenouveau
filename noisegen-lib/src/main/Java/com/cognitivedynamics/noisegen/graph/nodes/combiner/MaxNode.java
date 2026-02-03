package com.cognitivedynamics.noisegen.graph.nodes.combiner;

import com.cognitivedynamics.noisegen.graph.NoiseNode;

/**
 * Combiner node that returns the maximum of two noise sources.
 *
 * <p>Output = max(A, B)
 *
 * <p>This class is immutable and thread-safe.
 */
public final class MaxNode extends CombinerNode {

    /**
     * Create a max node.
     *
     * @param inputA The first input
     * @param inputB The second input
     */
    public MaxNode(NoiseNode inputA, NoiseNode inputB) {
        super(inputA, inputB);
    }

    @Override
    protected double combine(double a, double b) {
        return Math.max(a, b);
    }

    @Override
    public String getNodeType() {
        return "Max";
    }

    @Override
    public String toString() {
        return "MaxNode(max(" + inputA.getNodeType() + ", " + inputB.getNodeType() + "))";
    }
}
