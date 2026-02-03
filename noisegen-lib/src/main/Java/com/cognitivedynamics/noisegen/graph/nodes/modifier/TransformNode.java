package com.cognitivedynamics.noisegen.graph.nodes.modifier;

import com.cognitivedynamics.noisegen.graph.NoiseNode;
import com.cognitivedynamics.noisegen.transforms.NoiseTransform;

/**
 * Modifier node that applies a NoiseTransform to the source output.
 *
 * <p>This bridges the existing transform system with the node graph system,
 * allowing reuse of all existing transforms (RangeTransform, PowerTransform, etc.).
 *
 * <p>This class is immutable and thread-safe (assuming the transform is thread-safe).
 */
public final class TransformNode extends ModifierNode {

    private final NoiseTransform transform;

    /**
     * Create a transform node.
     *
     * @param source The source node
     * @param transform The transform to apply
     */
    public TransformNode(NoiseNode source, NoiseTransform transform) {
        super(source);
        if (transform == null) {
            throw new IllegalArgumentException("Transform cannot be null");
        }
        this.transform = transform;
    }

    /**
     * Get the transform.
     *
     * @return The transform
     */
    public NoiseTransform getTransform() {
        return transform;
    }

    @Override
    public double evaluate2D(int seed, double x, double y) {
        float value = (float) source.evaluate2D(seed, x, y);
        return transform.apply(value);
    }

    @Override
    public double evaluate3D(int seed, double x, double y, double z) {
        float value = (float) source.evaluate3D(seed, x, y, z);
        return transform.apply(value);
    }

    @Override
    public double evaluate4D(int seed, double x, double y, double z, double w) {
        if (!supports4D()) {
            throw new UnsupportedOperationException("4D noise not supported by " + getNodeType());
        }
        float value = (float) source.evaluate4D(seed, x, y, z, w);
        return transform.apply(value);
    }

    @Override
    public String getNodeType() {
        return "Transform";
    }

    @Override
    public String toString() {
        return "TransformNode(" + source.getNodeType() + ", " + transform.getDescription() + ")";
    }
}
