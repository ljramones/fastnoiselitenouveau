package com.cognitivedynamics.noisegen.graph.nodes.combiner;

import com.cognitivedynamics.noisegen.graph.NoiseNode;

/**
 * Combiner node that subtracts the second source from the first.
 *
 * <p>Output = A - B
 *
 * <p>This class is immutable and thread-safe.
 */
public final class SubtractNode extends CombinerNode {

    /**
     * Create a subtract node.
     *
     * @param inputA The minuend
     * @param inputB The subtrahend
     */
    public SubtractNode(NoiseNode inputA, NoiseNode inputB) {
        super(inputA, inputB);
    }

    @Override
    protected double combine(double a, double b) {
        return a - b;
    }

    @Override
    public String getNodeType() {
        return "Subtract";
    }

    @Override
    public String toString() {
        return "SubtractNode(" + inputA.getNodeType() + " - " + inputB.getNodeType() + ")";
    }
}
