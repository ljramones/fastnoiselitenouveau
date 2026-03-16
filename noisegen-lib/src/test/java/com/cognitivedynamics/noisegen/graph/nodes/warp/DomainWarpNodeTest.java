package com.cognitivedynamics.noisegen.graph.nodes.warp;

import com.cognitivedynamics.noisegen.graph.NoiseGraph;
import com.cognitivedynamics.noisegen.graph.NoiseNode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for DomainWarpNode.
 */
class DomainWarpNodeTest {

    private NoiseGraph graph;
    private static final int SEED = 1337;

    @BeforeEach
    void setUp() {
        graph = NoiseGraph.create(SEED);
    }

    @Nested
    @DisplayName("Basic functionality tests")
    class BasicFunctionalityTests {

        @Test
        @DisplayName("should produce bounded values")
        void shouldProduceBoundedValues() {
            NoiseNode source = graph.simplex();
            NoiseNode warp = graph.simplex().scale(0.5);
            DomainWarpNode warped = new DomainWarpNode(source, warp, 10.0);

            for (int x = -30; x <= 30; x += 10) {
                for (int y = -30; y <= 30; y += 10) {
                    double value = warped.evaluate2D(SEED, x * 0.1, y * 0.1);
                    assertFalse(Double.isNaN(value));
                    assertTrue(value >= -1.5 && value <= 1.5);
                }
            }
        }

        @Test
        @DisplayName("should be deterministic")
        void shouldBeDeterministic() {
            DomainWarpNode warped = graph.warp(graph.simplex(), graph.simplex(), 5.0);
            double v1 = warped.evaluate2D(SEED, 100, 200);
            double v2 = warped.evaluate2D(SEED, 100, 200);
            assertEquals(v1, v2);
        }

        @Test
        @DisplayName("zero amplitude should equal source")
        void zeroAmplitudeShouldEqualSource() {
            NoiseNode source = graph.simplex();
            DomainWarpNode warped = new DomainWarpNode(source, graph.simplex(), 0.0);

            for (int i = 0; i < 50; i++) {
                double x = i * 0.2;
                double y = i * 0.3;
                assertEquals(source.evaluate2D(SEED, x, y),
                    warped.evaluate2D(SEED, x, y), 0.0001);
            }
        }

        @Test
        @DisplayName("larger amplitude should increase distortion")
        void largerAmplitudeShouldIncreaseDistortion() {
            NoiseNode source = graph.simplex();
            NoiseNode warpSource = graph.simplex().scale(0.3);

            DomainWarpNode warpSmall = new DomainWarpNode(source, warpSource, 1.0);
            DomainWarpNode warpLarge = new DomainWarpNode(source, warpSource, 20.0);

            // Measure how different the warped output is from unwarped
            double smallDiff = 0;
            double largeDiff = 0;
            int n = 100;

            for (int i = 0; i < n; i++) {
                double x = i * 0.1;
                double y = i * 0.15;
                double orig = source.evaluate2D(SEED, x, y);
                smallDiff += Math.abs(warpSmall.evaluate2D(SEED, x, y) - orig);
                largeDiff += Math.abs(warpLarge.evaluate2D(SEED, x, y) - orig);
            }

            assertTrue(largeDiff > smallDiff,
                "Larger amplitude should create more distortion");
        }
    }

    @Nested
    @DisplayName("3D tests")
    class ThreeDTests {

        @Test
        @DisplayName("should work in 3D")
        void shouldWorkIn3D() {
            DomainWarpNode warped = graph.warp(graph.simplex(), graph.simplex(), 5.0);

            double value = warped.evaluate3D(SEED, 10, 20, 30);
            assertFalse(Double.isNaN(value));
            assertFalse(Double.isInfinite(value));
        }

        @Test
        @DisplayName("should be deterministic in 3D")
        void shouldBeDeterministicIn3D() {
            DomainWarpNode warped = graph.warp(graph.simplex(), graph.simplex(), 5.0);
            double v1 = warped.evaluate3D(SEED, 10, 20, 30);
            double v2 = warped.evaluate3D(SEED, 10, 20, 30);
            assertEquals(v1, v2);
        }
    }

    @Nested
    @DisplayName("4D support tests")
    class FourDSupportTests {

        @Test
        @DisplayName("should support 4D when both sources support 4D")
        void shouldSupport4DWhenBothSourcesSupport() {
            DomainWarpNode warped = graph.warp(graph.simplex4D(), graph.simplex4D(), 5.0);

            assertTrue(warped.supports4D());
            double value = warped.evaluate4D(SEED, 1, 2, 3, 4);
            assertFalse(Double.isNaN(value));
        }

        @Test
        @DisplayName("should not support 4D when source doesn't")
        void shouldNotSupport4DWhenSourceDoesNot() {
            DomainWarpNode warped = graph.warp(graph.simplex(), graph.simplex4D(), 5.0);

            assertFalse(warped.supports4D());
            assertThrows(UnsupportedOperationException.class,
                () -> warped.evaluate4D(SEED, 1, 2, 3, 4));
        }

        @Test
        @DisplayName("should not support 4D when warp source doesn't")
        void shouldNotSupport4DWhenWarpSourceDoesNot() {
            DomainWarpNode warped = graph.warp(graph.simplex4D(), graph.simplex(), 5.0);

            assertFalse(warped.supports4D());
        }
    }

    @Nested
    @DisplayName("Fluent API tests")
    class FluentApiTests {

        @Test
        @DisplayName("fluent warp should work")
        void fluentWarpShouldWork() {
            NoiseNode warped = graph.simplex().warp(graph.simplex(), 10.0);
            assertEquals("DomainWarp", warped.getNodeType());
        }

        @Test
        @DisplayName("should be chainable")
        void shouldBeChainable() {
            NoiseNode result = graph.simplex()
                .warp(graph.simplex().scale(0.5), 5.0)
                .clamp(-0.5, 0.5)
                .multiply(2.0);

            double value = result.evaluate2D(SEED, 100, 200);
            assertTrue(value >= -1.0 && value <= 1.0);
        }
    }

    @Nested
    @DisplayName("Validation tests")
    class ValidationTests {

        @Test
        @DisplayName("should reject null source")
        void shouldRejectNullSource() {
            assertThrows(IllegalArgumentException.class,
                () -> new DomainWarpNode(null, graph.simplex(), 1.0));
        }

        @Test
        @DisplayName("should reject null warp source")
        void shouldRejectNullWarpSource() {
            assertThrows(IllegalArgumentException.class,
                () -> new DomainWarpNode(graph.simplex(), null, 1.0));
        }
    }

    @Nested
    @DisplayName("Property tests")
    class PropertyTests {

        @Test
        @DisplayName("should expose properties")
        void shouldExposeProperties() {
            NoiseNode source = graph.simplex();
            NoiseNode warpSource = graph.perlin();
            DomainWarpNode warped = new DomainWarpNode(source, warpSource, 7.5);

            assertSame(source, warped.getSource());
            assertSame(warpSource, warped.getWarpSource());
            assertEquals(7.5, warped.getAmplitude());
        }
    }
}
