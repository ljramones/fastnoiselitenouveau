package com.cognitivedynamics.noisegen.graph.nodes.combiner;

import com.cognitivedynamics.noisegen.graph.NoiseNode;

/**
 * Ternary combiner node that blends between two sources using a control signal.
 *
 * <p>Output = A + (B - A) * control = lerp(A, B, control)
 *
 * <p>When control = 0, output = A. When control = 1, output = B.
 * Values outside [0, 1] extrapolate.
 *
 * <p>This class is immutable and thread-safe.
 */
public final class BlendNode implements NoiseNode {

    private final NoiseNode inputA;
    private final NoiseNode inputB;
    private final NoiseNode control;

    /**
     * Create a blend node.
     *
     * @param inputA The first input (output when control = 0)
     * @param inputB The second input (output when control = 1)
     * @param control The control signal (typically in range [0, 1])
     */
    public BlendNode(NoiseNode inputA, NoiseNode inputB, NoiseNode control) {
        if (inputA == null || inputB == null || control == null) {
            throw new IllegalArgumentException("Input nodes cannot be null");
        }
        this.inputA = inputA;
        this.inputB = inputB;
        this.control = control;
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
     * Get the control node.
     *
     * @return The control node
     */
    public NoiseNode getControl() {
        return control;
    }

    private double blend(double a, double b, double t) {
        return a + (b - a) * t;
    }

    @Override
    public double evaluate2D(int seed, double x, double y) {
        double a = inputA.evaluate2D(seed, x, y);
        double b = inputB.evaluate2D(seed, x, y);
        double t = control.evaluate2D(seed, x, y);
        return blend(a, b, t);
    }

    @Override
    public double evaluate3D(int seed, double x, double y, double z) {
        double a = inputA.evaluate3D(seed, x, y, z);
        double b = inputB.evaluate3D(seed, x, y, z);
        double t = control.evaluate3D(seed, x, y, z);
        return blend(a, b, t);
    }

    @Override
    public double evaluate4D(int seed, double x, double y, double z, double w) {
        if (!supports4D()) {
            throw new UnsupportedOperationException("4D noise not supported by " + getNodeType());
        }
        double a = inputA.evaluate4D(seed, x, y, z, w);
        double b = inputB.evaluate4D(seed, x, y, z, w);
        double t = control.evaluate4D(seed, x, y, z, w);
        return blend(a, b, t);
    }

    @Override
    public boolean supports4D() {
        return inputA.supports4D() && inputB.supports4D() && control.supports4D();
    }

    @Override
    public String getNodeType() {
        return "Blend";
    }

    @Override
    public String toString() {
        return "BlendNode(blend(" + inputA.getNodeType() + ", " + inputB.getNodeType() +
            ", " + control.getNodeType() + "))";
    }
}
