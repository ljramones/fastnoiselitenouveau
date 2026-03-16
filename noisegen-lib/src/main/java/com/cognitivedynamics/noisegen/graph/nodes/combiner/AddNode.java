package com.cognitivedynamics.noisegen.graph.nodes.combiner;

import com.cognitivedynamics.noisegen.graph.NoiseNode;

/**
 * Combiner node that adds two noise sources.
 *
 * <p>Output = A + B
 *
 * <p>This class is immutable and thread-safe.
 */
public final class AddNode extends CombinerNode {

    /**
     * Create an add node.
     *
     * @param inputA The first input
     * @param inputB The second input
     */
    public AddNode(NoiseNode inputA, NoiseNode inputB) {
        super(inputA, inputB);
    }

    @Override
    protected double combine(double a, double b) {
        return a + b;
    }

    @Override
    public String getNodeType() {
        return "Add";
    }

    @Override
    public String toString() {
        return "AddNode(" + inputA.getNodeType() + " + " + inputB.getNodeType() + ")";
    }
}
