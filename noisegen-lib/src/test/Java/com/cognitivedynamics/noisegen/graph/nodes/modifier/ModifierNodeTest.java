package com.cognitivedynamics.noisegen.graph.nodes.modifier;

import com.cognitivedynamics.noisegen.graph.NoiseGraph;
import com.cognitivedynamics.noisegen.graph.NoiseNode;
import com.cognitivedynamics.noisegen.transforms.NoiseTransform;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for modifier nodes.
 */
class ModifierNodeTest {

    private NoiseGraph graph;
    private static final int SEED = 1337;

    @BeforeEach
    void setUp() {
        graph = NoiseGraph.create(SEED);
    }

    @Nested
    @DisplayName("DomainScaleNode tests")
    class DomainScaleNodeTests {

        @Test
        @DisplayName("should scale coordinates by factor")
        void shouldScaleCoordinates() {
            NoiseNode source = graph.simplex();
            DomainScaleNode scaled = new DomainScaleNode(source, 2.0);

            // scaled(1, 1) should equal source(2, 2)
            double sourceAt2_2 = source.evaluate2D(SEED, 2, 2);
            double scaledAt1_1 = scaled.evaluate2D(SEED, 1, 1);
            assertEquals(sourceAt2_2, scaledAt1_1, 0.0001);
        }

        @Test
        @DisplayName("should work in 3D")
        void shouldWorkIn3D() {
            NoiseNode source = graph.simplex();
            DomainScaleNode scaled = new DomainScaleNode(source, 0.5);

            double sourceAt1 = source.evaluate3D(SEED, 1, 1, 1);
            double scaledAt2 = scaled.evaluate3D(SEED, 2, 2, 2);
            assertEquals(sourceAt1, scaledAt2, 0.0001);
        }

        @Test
        @DisplayName("fluent scale should work")
        void fluentScaleShouldWork() {
            NoiseNode scaled = graph.simplex().scale(0.01);
            assertEquals("DomainScale", scaled.getNodeType());
        }

        @Test
        @DisplayName("scale factor should be retrievable")
        void scaleFactorShouldBeRetrievable() {
            DomainScaleNode scaled = new DomainScaleNode(graph.constant(0), 2.5);
            assertEquals(2.5, scaled.getScale());
        }
    }

    @Nested
    @DisplayName("DomainOffsetNode tests")
    class DomainOffsetNodeTests {

        @Test
        @DisplayName("should offset coordinates")
        void shouldOffsetCoordinates() {
            NoiseNode source = graph.simplex();
            DomainOffsetNode offset = new DomainOffsetNode(source, 10, 20, 0);

            double sourceAt10_20 = source.evaluate2D(SEED, 10, 20);
            double offsetAt0_0 = offset.evaluate2D(SEED, 0, 0);
            assertEquals(sourceAt10_20, offsetAt0_0, 0.0001);
        }

        @Test
        @DisplayName("should work in 3D")
        void shouldWorkIn3D() {
            NoiseNode source = graph.simplex();
            DomainOffsetNode offset = new DomainOffsetNode(source, 5, 5, 5);

            double sourceAt5 = source.evaluate3D(SEED, 5, 5, 5);
            double offsetAt0 = offset.evaluate3D(SEED, 0, 0, 0);
            assertEquals(sourceAt5, offsetAt0, 0.0001);
        }

        @Test
        @DisplayName("fluent offset should work")
        void fluentOffsetShouldWork() {
            NoiseNode offset = graph.simplex().offset(10, 20, 30);
            assertEquals("DomainOffset", offset.getNodeType());
        }

        @Test
        @DisplayName("offsets should be retrievable")
        void offsetsShouldBeRetrievable() {
            DomainOffsetNode offset = new DomainOffsetNode(graph.constant(0), 1, 2, 3);
            assertEquals(1, offset.getOffsetX());
            assertEquals(2, offset.getOffsetY());
            assertEquals(3, offset.getOffsetZ());
        }
    }

    @Nested
    @DisplayName("AbsoluteNode tests")
    class AbsoluteNodeTests {

        @Test
        @DisplayName("should return absolute value of positive")
        void shouldReturnAbsoluteOfPositive() {
            NoiseNode source = graph.constant(0.5);
            AbsoluteNode abs = new AbsoluteNode(source);
            assertEquals(0.5, abs.evaluate2D(SEED, 0, 0), 0.0001);
        }

        @Test
        @DisplayName("should return absolute value of negative")
        void shouldReturnAbsoluteOfNegative() {
            NoiseNode source = graph.constant(-0.7);
            AbsoluteNode abs = new AbsoluteNode(source);
            assertEquals(0.7, abs.evaluate2D(SEED, 0, 0), 0.0001);
        }

        @Test
        @DisplayName("should work with noise")
        void shouldWorkWithNoise() {
            NoiseNode noise = graph.simplex();
            NoiseNode abs = noise.abs();

            for (int i = 0; i < 100; i++) {
                double x = i * 0.1;
                double y = i * 0.15;
                double absVal = abs.evaluate2D(SEED, x, y);
                assertTrue(absVal >= 0, "Absolute value should be non-negative");
            }
        }

        @Test
        @DisplayName("fluent abs should work")
        void fluentAbsShouldWork() {
            NoiseNode abs = graph.simplex().abs();
            assertEquals("Absolute", abs.getNodeType());
        }
    }

    @Nested
    @DisplayName("ClampNode tests")
    class ClampNodeTests {

        @Test
        @DisplayName("should clamp below minimum")
        void shouldClampBelowMin() {
            NoiseNode source = graph.constant(-0.8);
            ClampNode clamped = new ClampNode(source, -0.5, 0.5);
            assertEquals(-0.5, clamped.evaluate2D(SEED, 0, 0), 0.0001);
        }

        @Test
        @DisplayName("should clamp above maximum")
        void shouldClampAboveMax() {
            NoiseNode source = graph.constant(0.9);
            ClampNode clamped = new ClampNode(source, -0.5, 0.5);
            assertEquals(0.5, clamped.evaluate2D(SEED, 0, 0), 0.0001);
        }

        @Test
        @DisplayName("should pass through values in range")
        void shouldPassThroughValuesInRange() {
            NoiseNode source = graph.constant(0.3);
            ClampNode clamped = new ClampNode(source, -0.5, 0.5);
            assertEquals(0.3, clamped.evaluate2D(SEED, 0, 0), 0.0001);
        }

        @Test
        @DisplayName("should reject invalid range")
        void shouldRejectInvalidRange() {
            assertThrows(IllegalArgumentException.class, () ->
                new ClampNode(graph.constant(0), 1.0, 0.0));
        }

        @Test
        @DisplayName("fluent clamp should work")
        void fluentClampShouldWork() {
            NoiseNode clamped = graph.simplex().clamp(-0.5, 0.5);
            assertEquals("Clamp", clamped.getNodeType());

            // Verify it actually clamps
            for (int i = 0; i < 100; i++) {
                double value = clamped.evaluate2D(SEED, i * 0.5, i * 0.7);
                assertTrue(value >= -0.5 && value <= 0.5,
                    "Clamped value " + value + " out of range");
            }
        }

        @Test
        @DisplayName("min and max should be retrievable")
        void minAndMaxShouldBeRetrievable() {
            ClampNode clamped = new ClampNode(graph.constant(0), -0.3, 0.7);
            assertEquals(-0.3, clamped.getMin());
            assertEquals(0.7, clamped.getMax());
        }
    }

    @Nested
    @DisplayName("InvertNode tests")
    class InvertNodeTests {

        @Test
        @DisplayName("should invert positive value")
        void shouldInvertPositive() {
            NoiseNode source = graph.constant(0.5);
            InvertNode inverted = new InvertNode(source);
            assertEquals(-0.5, inverted.evaluate2D(SEED, 0, 0), 0.0001);
        }

        @Test
        @DisplayName("should invert negative value")
        void shouldInvertNegative() {
            NoiseNode source = graph.constant(-0.3);
            InvertNode inverted = new InvertNode(source);
            assertEquals(0.3, inverted.evaluate2D(SEED, 0, 0), 0.0001);
        }

        @Test
        @DisplayName("should invert zero to zero")
        void shouldInvertZeroToZero() {
            NoiseNode source = graph.constant(0);
            InvertNode inverted = new InvertNode(source);
            assertEquals(0, inverted.evaluate2D(SEED, 0, 0), 0.0001);
        }

        @Test
        @DisplayName("double inversion should restore original")
        void doubleInversionShouldRestoreOriginal() {
            NoiseNode noise = graph.simplex();
            NoiseNode doubleInverted = noise.invert().invert();

            for (int i = 0; i < 50; i++) {
                double x = i * 0.2;
                double y = i * 0.3;
                assertEquals(noise.evaluate2D(SEED, x, y),
                    doubleInverted.evaluate2D(SEED, x, y), 0.0001);
            }
        }

        @Test
        @DisplayName("fluent invert should work")
        void fluentInvertShouldWork() {
            NoiseNode inverted = graph.simplex().invert();
            assertEquals("Invert", inverted.getNodeType());
        }
    }

    @Nested
    @DisplayName("TransformNode tests")
    class TransformNodeTests {

        @Test
        @DisplayName("should apply transform to output")
        void shouldApplyTransformToOutput() {
            NoiseNode source = graph.constant(0.5f);
            NoiseTransform doubleIt = value -> value * 2;
            TransformNode transformed = new TransformNode(source, doubleIt);

            assertEquals(1.0, transformed.evaluate2D(SEED, 0, 0), 0.0001);
        }

        @Test
        @DisplayName("should work with noise")
        void shouldWorkWithNoise() {
            NoiseNode noise = graph.simplex();
            NoiseTransform shift = value -> value + 1; // Shift from [-1,1] to [0,2]
            TransformNode transformed = new TransformNode(noise, shift);

            for (int i = 0; i < 50; i++) {
                double x = i * 0.1;
                double y = i * 0.2;
                double noiseVal = noise.evaluate2D(SEED, x, y);
                double transformedVal = transformed.evaluate2D(SEED, x, y);
                assertEquals(noiseVal + 1, transformedVal, 0.0001);
            }
        }

        @Test
        @DisplayName("fluent transform should work")
        void fluentTransformShouldWork() {
            NoiseTransform scale = value -> value * 0.5f;
            NoiseNode transformed = graph.simplex().transform(scale);
            assertEquals("Transform", transformed.getNodeType());
        }

        @Test
        @DisplayName("should reject null transform")
        void shouldRejectNullTransform() {
            assertThrows(IllegalArgumentException.class, () ->
                new TransformNode(graph.constant(0), null));
        }
    }

    @Nested
    @DisplayName("4D support tests")
    class FourDSupportTests {

        @Test
        @DisplayName("should support 4D when source supports 4D")
        void shouldSupport4DWhenSourceSupports() {
            NoiseNode source = graph.constant(0.5);
            ClampNode clamped = new ClampNode(source, -1, 1);

            assertTrue(clamped.supports4D());
            assertEquals(0.5, clamped.evaluate4D(SEED, 0, 0, 0, 0), 0.0001);
        }

        @Test
        @DisplayName("should not support 4D when source doesn't")
        void shouldNotSupport4DWhenSourceDoesNot() {
            NoiseNode source = graph.simplex();
            ClampNode clamped = new ClampNode(source, -1, 1);

            assertFalse(clamped.supports4D());
            assertThrows(UnsupportedOperationException.class, () ->
                clamped.evaluate4D(SEED, 0, 0, 0, 0));
        }
    }

    @Nested
    @DisplayName("Null input validation tests")
    class NullInputTests {

        @Test
        @DisplayName("ModifierNode subclasses should reject null source")
        void shouldRejectNullSource() {
            assertThrows(IllegalArgumentException.class, () -> new DomainScaleNode(null, 1.0));
            assertThrows(IllegalArgumentException.class, () -> new DomainOffsetNode(null, 0, 0, 0));
            assertThrows(IllegalArgumentException.class, () -> new AbsoluteNode(null));
            assertThrows(IllegalArgumentException.class, () -> new ClampNode(null, 0, 1));
            assertThrows(IllegalArgumentException.class, () -> new InvertNode(null));
        }
    }

    @Nested
    @DisplayName("Chaining tests")
    class ChainingTests {

        @Test
        @DisplayName("should support chained modifiers")
        void shouldSupportChainedModifiers() {
            // Scale, then clamp, then invert
            NoiseNode result = graph.simplex()
                .scale(0.01)
                .clamp(-0.5, 0.5)
                .invert();

            // Verify the chain works
            double value = result.evaluate2D(SEED, 1000, 2000);
            assertTrue(value >= -0.5 && value <= 0.5,
                "Final value should still be in clamped range");
        }
    }
}
