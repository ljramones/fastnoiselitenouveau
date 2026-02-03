package com.cognitivedynamics.noisegen.graph.nodes.combiner;

import com.cognitivedynamics.noisegen.graph.NoiseGraph;
import com.cognitivedynamics.noisegen.graph.NoiseNode;
import com.cognitivedynamics.noisegen.graph.nodes.source.ConstantNode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for combiner nodes.
 */
class CombinerNodeTest {

    private NoiseGraph graph;
    private static final int SEED = 1337;

    @BeforeEach
    void setUp() {
        graph = NoiseGraph.create(SEED);
    }

    @Nested
    @DisplayName("AddNode tests")
    class AddNodeTests {

        @Test
        @DisplayName("should add constant values correctly")
        void shouldAddConstantsCorrectly() {
            NoiseNode a = graph.constant(0.3);
            NoiseNode b = graph.constant(0.5);
            AddNode add = new AddNode(a, b);

            assertEquals(0.8, add.evaluate2D(SEED, 0, 0), 0.0001);
            assertEquals(0.8, add.evaluate3D(SEED, 0, 0, 0), 0.0001);
        }

        @Test
        @DisplayName("should add noise sources")
        void shouldAddNoiseSources() {
            NoiseNode simplex = graph.simplex();
            NoiseNode perlin = graph.perlin();
            AddNode add = graph.add(simplex, perlin);

            double simplexVal = simplex.evaluate2D(SEED, 10, 20);
            double perlinVal = perlin.evaluate2D(SEED, 10, 20);
            double addVal = add.evaluate2D(SEED, 10, 20);

            assertEquals(simplexVal + perlinVal, addVal, 0.0001);
        }

        @Test
        @DisplayName("should handle negative values")
        void shouldHandleNegativeValues() {
            NoiseNode a = graph.constant(-0.5);
            NoiseNode b = graph.constant(0.3);
            AddNode add = new AddNode(a, b);

            assertEquals(-0.2, add.evaluate2D(SEED, 0, 0), 0.0001);
        }

        @Test
        @DisplayName("should report correct node type")
        void shouldReportCorrectNodeType() {
            AddNode add = new AddNode(graph.constant(0), graph.constant(0));
            assertEquals("Add", add.getNodeType());
        }

        @Test
        @DisplayName("fluent add should work")
        void fluentAddShouldWork() {
            NoiseNode result = graph.simplex().add(graph.perlin());
            assertEquals("Add", result.getNodeType());
        }
    }

    @Nested
    @DisplayName("SubtractNode tests")
    class SubtractNodeTests {

        @Test
        @DisplayName("should subtract constant values correctly")
        void shouldSubtractConstantsCorrectly() {
            NoiseNode a = graph.constant(0.8);
            NoiseNode b = graph.constant(0.3);
            SubtractNode sub = new SubtractNode(a, b);

            assertEquals(0.5, sub.evaluate2D(SEED, 0, 0), 0.0001);
        }

        @Test
        @DisplayName("should produce negative results")
        void shouldProduceNegativeResults() {
            NoiseNode a = graph.constant(0.2);
            NoiseNode b = graph.constant(0.7);
            SubtractNode sub = new SubtractNode(a, b);

            assertEquals(-0.5, sub.evaluate2D(SEED, 0, 0), 0.0001);
        }

        @Test
        @DisplayName("fluent subtract should work")
        void fluentSubtractShouldWork() {
            NoiseNode result = graph.simplex().subtract(graph.perlin());
            assertEquals("Subtract", result.getNodeType());
        }
    }

    @Nested
    @DisplayName("MultiplyNode tests")
    class MultiplyNodeTests {

        @Test
        @DisplayName("should multiply constant values correctly")
        void shouldMultiplyConstantsCorrectly() {
            NoiseNode a = graph.constant(0.5);
            NoiseNode b = graph.constant(0.4);
            MultiplyNode mul = new MultiplyNode(a, b);

            assertEquals(0.2, mul.evaluate2D(SEED, 0, 0), 0.0001);
        }

        @Test
        @DisplayName("should handle zero")
        void shouldHandleZero() {
            NoiseNode a = graph.simplex();
            NoiseNode b = graph.constant(0);
            MultiplyNode mul = new MultiplyNode(a, b);

            assertEquals(0, mul.evaluate2D(SEED, 100, 200), 0.0001);
        }

        @Test
        @DisplayName("should scale noise by constant")
        void shouldScaleNoiseByConstant() {
            NoiseNode noise = graph.simplex();
            NoiseNode factor = graph.constant(0.5);
            MultiplyNode scaled = new MultiplyNode(noise, factor);

            double noiseVal = noise.evaluate2D(SEED, 10, 20);
            double scaledVal = scaled.evaluate2D(SEED, 10, 20);

            assertEquals(noiseVal * 0.5, scaledVal, 0.0001);
        }

        @Test
        @DisplayName("fluent multiply by constant should work")
        void fluentMultiplyByConstantShouldWork() {
            NoiseNode noise = graph.simplex();
            NoiseNode scaled = noise.multiply(0.5);

            double noiseVal = noise.evaluate2D(SEED, 10, 20);
            double scaledVal = scaled.evaluate2D(SEED, 10, 20);

            assertEquals(noiseVal * 0.5, scaledVal, 0.0001);
        }
    }

    @Nested
    @DisplayName("MinNode tests")
    class MinNodeTests {

        @Test
        @DisplayName("should return minimum of constants")
        void shouldReturnMinOfConstants() {
            NoiseNode a = graph.constant(0.3);
            NoiseNode b = graph.constant(0.7);
            MinNode min = new MinNode(a, b);

            assertEquals(0.3, min.evaluate2D(SEED, 0, 0), 0.0001);
        }

        @Test
        @DisplayName("should work with negative values")
        void shouldWorkWithNegativeValues() {
            NoiseNode a = graph.constant(-0.5);
            NoiseNode b = graph.constant(0.2);
            MinNode min = new MinNode(a, b);

            assertEquals(-0.5, min.evaluate2D(SEED, 0, 0), 0.0001);
        }

        @Test
        @DisplayName("fluent min should work")
        void fluentMinShouldWork() {
            NoiseNode result = graph.simplex().min(graph.constant(0.5));
            assertEquals("Min", result.getNodeType());
        }
    }

    @Nested
    @DisplayName("MaxNode tests")
    class MaxNodeTests {

        @Test
        @DisplayName("should return maximum of constants")
        void shouldReturnMaxOfConstants() {
            NoiseNode a = graph.constant(0.3);
            NoiseNode b = graph.constant(0.7);
            MaxNode max = new MaxNode(a, b);

            assertEquals(0.7, max.evaluate2D(SEED, 0, 0), 0.0001);
        }

        @Test
        @DisplayName("should work with negative values")
        void shouldWorkWithNegativeValues() {
            NoiseNode a = graph.constant(-0.5);
            NoiseNode b = graph.constant(-0.2);
            MaxNode max = new MaxNode(a, b);

            assertEquals(-0.2, max.evaluate2D(SEED, 0, 0), 0.0001);
        }

        @Test
        @DisplayName("fluent max should work")
        void fluentMaxShouldWork() {
            NoiseNode result = graph.simplex().max(graph.constant(-0.5));
            assertEquals("Max", result.getNodeType());
        }
    }

    @Nested
    @DisplayName("BlendNode tests")
    class BlendNodeTests {

        @Test
        @DisplayName("should return A when control is 0")
        void shouldReturnAWhenControlIs0() {
            NoiseNode a = graph.constant(0.2);
            NoiseNode b = graph.constant(0.8);
            NoiseNode control = graph.constant(0.0);
            BlendNode blend = new BlendNode(a, b, control);

            assertEquals(0.2, blend.evaluate2D(SEED, 0, 0), 0.0001);
        }

        @Test
        @DisplayName("should return B when control is 1")
        void shouldReturnBWhenControlIs1() {
            NoiseNode a = graph.constant(0.2);
            NoiseNode b = graph.constant(0.8);
            NoiseNode control = graph.constant(1.0);
            BlendNode blend = new BlendNode(a, b, control);

            assertEquals(0.8, blend.evaluate2D(SEED, 0, 0), 0.0001);
        }

        @Test
        @DisplayName("should interpolate at control 0.5")
        void shouldInterpolateAtHalf() {
            NoiseNode a = graph.constant(0.0);
            NoiseNode b = graph.constant(1.0);
            NoiseNode control = graph.constant(0.5);
            BlendNode blend = new BlendNode(a, b, control);

            assertEquals(0.5, blend.evaluate2D(SEED, 0, 0), 0.0001);
        }

        @Test
        @DisplayName("should extrapolate when control > 1")
        void shouldExtrapolateWhenControlGreaterThan1() {
            NoiseNode a = graph.constant(0.0);
            NoiseNode b = graph.constant(1.0);
            NoiseNode control = graph.constant(2.0);
            BlendNode blend = new BlendNode(a, b, control);

            assertEquals(2.0, blend.evaluate2D(SEED, 0, 0), 0.0001);
        }

        @Test
        @DisplayName("should use noise as control signal")
        void shouldUseNoiseAsControl() {
            NoiseNode a = graph.constant(0.0);
            NoiseNode b = graph.constant(1.0);
            NoiseNode control = graph.simplex();
            BlendNode blend = graph.blend(a, b, control);

            double controlVal = control.evaluate2D(SEED, 10, 20);
            double blendVal = blend.evaluate2D(SEED, 10, 20);

            // blend(0, 1, t) = t
            assertEquals(controlVal, blendVal, 0.0001);
        }
    }

    @Nested
    @DisplayName("4D support tests")
    class FourDSupportTests {

        @Test
        @DisplayName("should support 4D when both inputs support 4D")
        void shouldSupport4DWhenBothInputsSupport() {
            NoiseNode a = graph.constant(0.5);
            NoiseNode b = graph.constant(0.5);
            AddNode add = new AddNode(a, b);

            assertTrue(add.supports4D());
            assertEquals(1.0, add.evaluate4D(SEED, 0, 0, 0, 0), 0.0001);
        }

        @Test
        @DisplayName("should not support 4D when one input doesn't support it")
        void shouldNotSupport4DWhenOneInputDoesNot() {
            NoiseNode a = graph.simplex(); // 3D only
            NoiseNode b = graph.constant(0.5);
            AddNode add = new AddNode(a, b);

            assertFalse(add.supports4D());
            assertThrows(UnsupportedOperationException.class,
                () -> add.evaluate4D(SEED, 0, 0, 0, 0));
        }
    }

    @Nested
    @DisplayName("Null input validation tests")
    class NullInputTests {

        @Test
        @DisplayName("AddNode should reject null inputs")
        void addNodeShouldRejectNull() {
            assertThrows(IllegalArgumentException.class, () -> new AddNode(null, graph.constant(0)));
            assertThrows(IllegalArgumentException.class, () -> new AddNode(graph.constant(0), null));
        }

        @Test
        @DisplayName("BlendNode should reject null inputs")
        void blendNodeShouldRejectNull() {
            NoiseNode node = graph.constant(0);
            assertThrows(IllegalArgumentException.class, () -> new BlendNode(null, node, node));
            assertThrows(IllegalArgumentException.class, () -> new BlendNode(node, null, node));
            assertThrows(IllegalArgumentException.class, () -> new BlendNode(node, node, null));
        }
    }

    @Nested
    @DisplayName("Chaining tests")
    class ChainingTests {

        @Test
        @DisplayName("should support complex chaining")
        void shouldSupportComplexChaining() {
            // (simplex + perlin) * 0.5
            NoiseNode result = graph.simplex()
                .add(graph.perlin())
                .multiply(0.5);

            double simplexVal = graph.simplex().evaluate2D(SEED, 10, 20);
            double perlinVal = graph.perlin().evaluate2D(SEED, 10, 20);
            double expected = (simplexVal + perlinVal) * 0.5;

            assertEquals(expected, result.evaluate2D(SEED, 10, 20), 0.0001);
        }

        @Test
        @DisplayName("should support deeply nested graphs")
        void shouldSupportDeeplyNestedGraphs() {
            NoiseNode node = graph.constant(1.0);
            for (int i = 0; i < 10; i++) {
                node = node.add(graph.constant(1.0));
            }

            assertEquals(11.0, node.evaluate2D(SEED, 0, 0), 0.0001);
        }
    }
}
