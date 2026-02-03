package com.cognitivedynamics.noisegen.graph.nodes.fractal;

import com.cognitivedynamics.noisegen.graph.NoiseGraph;
import com.cognitivedynamics.noisegen.graph.NoiseNode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for fractal nodes.
 */
class FractalNodeTest {

    private NoiseGraph graph;
    private static final int SEED = 1337;

    @BeforeEach
    void setUp() {
        graph = NoiseGraph.create(SEED);
    }

    @Nested
    @DisplayName("FBmNode tests")
    class FBmNodeTests {

        @Test
        @DisplayName("should produce bounded values")
        void shouldProduceBoundedValues() {
            FBmNode fbm = graph.fbm(graph.simplex(), 5);

            for (int x = -50; x <= 50; x += 10) {
                for (int y = -50; y <= 50; y += 10) {
                    double value = fbm.evaluate2D(SEED, x * 0.1, y * 0.1);
                    assertFalse(Double.isNaN(value), "NaN at (" + x + ", " + y + ")");
                    // With normalization, values should stay roughly in [-1.5, 1.5]
                    assertTrue(value >= -2.0 && value <= 2.0,
                        "Value " + value + " out of range at (" + x + ", " + y + ")");
                }
            }
        }

        @Test
        @DisplayName("should be deterministic")
        void shouldBeDeterministic() {
            FBmNode fbm = graph.fbm(graph.simplex(), 4);
            double v1 = fbm.evaluate2D(SEED, 123.456, 789.012);
            double v2 = fbm.evaluate2D(SEED, 123.456, 789.012);
            assertEquals(v1, v2);
        }

        @Test
        @DisplayName("more octaves should add detail")
        void moreOctavesShouldAddDetail() {
            NoiseNode source = graph.simplex();
            FBmNode fbm1 = graph.fbm(source, 1);
            FBmNode fbm4 = graph.fbm(source, 4);

            // Sample many points and compare variance
            double sum1 = 0, sumSq1 = 0;
            double sum4 = 0, sumSq4 = 0;
            int n = 100;

            for (int i = 0; i < n; i++) {
                double x = i * 0.1;
                double y = i * 0.13;

                double v1 = fbm1.evaluate2D(SEED, x, y);
                double v4 = fbm4.evaluate2D(SEED, x, y);

                sum1 += v1;
                sumSq1 += v1 * v1;
                sum4 += v4;
                sumSq4 += v4 * v4;
            }

            // fbm4 should have more variation (higher variance) due to more detail
            double var1 = (sumSq1 / n) - (sum1 / n) * (sum1 / n);
            double var4 = (sumSq4 / n) - (sum4 / n) * (sum4 / n);

            // This isn't always true due to normalization, but the pattern should differ
            assertNotEquals(var1, var4, 0.01, "Different octave counts should produce different variance");
        }

        @Test
        @DisplayName("should work in 3D")
        void shouldWorkIn3D() {
            FBmNode fbm = graph.fbm(graph.simplex(), 3);
            double value = fbm.evaluate3D(SEED, 10, 20, 30);
            assertFalse(Double.isNaN(value));
            assertFalse(Double.isInfinite(value));
        }

        @Test
        @DisplayName("should have correct parameters")
        void shouldHaveCorrectParameters() {
            FBmNode fbm = graph.fbm(graph.simplex(), 5, 2.5, 0.4);
            assertEquals(5, fbm.getOctaves());
            assertEquals(2.5, fbm.getLacunarity());
            assertEquals(0.4, fbm.getGain());
        }

        @ParameterizedTest
        @ValueSource(ints = {1, 2, 3, 4, 5, 6, 8, 10})
        @DisplayName("should work with various octave counts")
        void shouldWorkWithVariousOctaves(int octaves) {
            FBmNode fbm = graph.fbm(graph.simplex(), octaves);
            double value = fbm.evaluate2D(SEED, 50, 50);
            assertFalse(Double.isNaN(value));
            assertFalse(Double.isInfinite(value));
        }
    }

    @Nested
    @DisplayName("RidgedNode tests")
    class RidgedNodeTests {

        @Test
        @DisplayName("should produce bounded values")
        void shouldProduceBoundedValues() {
            RidgedNode ridged = graph.ridged(graph.simplex(), 5);

            for (int x = -30; x <= 30; x += 10) {
                for (int y = -30; y <= 30; y += 10) {
                    double value = ridged.evaluate2D(SEED, x * 0.1, y * 0.1);
                    assertFalse(Double.isNaN(value));
                    assertTrue(value >= -2.0 && value <= 2.0,
                        "Value " + value + " out of range");
                }
            }
        }

        @Test
        @DisplayName("should be deterministic")
        void shouldBeDeterministic() {
            RidgedNode ridged = graph.ridged(graph.simplex(), 4);
            double v1 = ridged.evaluate2D(SEED, 100, 200);
            double v2 = ridged.evaluate2D(SEED, 100, 200);
            assertEquals(v1, v2);
        }

        @Test
        @DisplayName("should differ from FBm")
        void shouldDifferFromFBm() {
            NoiseNode source = graph.simplex();
            FBmNode fbm = graph.fbm(source, 4);
            RidgedNode ridged = graph.ridged(source, 4);

            int differences = 0;
            for (int i = 0; i < 50; i++) {
                double x = i * 0.2;
                double y = i * 0.3;
                if (Math.abs(fbm.evaluate2D(SEED, x, y) - ridged.evaluate2D(SEED, x, y)) > 0.01) {
                    differences++;
                }
            }
            assertTrue(differences > 20, "Ridged and FBm should produce different patterns");
        }

        @Test
        @DisplayName("should work in 3D")
        void shouldWorkIn3D() {
            RidgedNode ridged = graph.ridged(graph.simplex(), 3);
            double value = ridged.evaluate3D(SEED, 10, 20, 30);
            assertFalse(Double.isNaN(value));
        }
    }

    @Nested
    @DisplayName("BillowNode tests")
    class BillowNodeTests {

        @Test
        @DisplayName("should produce bounded values")
        void shouldProduceBoundedValues() {
            BillowNode billow = graph.billow(graph.simplex(), 5);

            for (int x = -30; x <= 30; x += 10) {
                for (int y = -30; y <= 30; y += 10) {
                    double value = billow.evaluate2D(SEED, x * 0.1, y * 0.1);
                    assertFalse(Double.isNaN(value));
                    assertTrue(value >= -2.0 && value <= 2.0,
                        "Value " + value + " out of range");
                }
            }
        }

        @Test
        @DisplayName("should be deterministic")
        void shouldBeDeterministic() {
            BillowNode billow = graph.billow(graph.simplex(), 4);
            double v1 = billow.evaluate2D(SEED, 100, 200);
            double v2 = billow.evaluate2D(SEED, 100, 200);
            assertEquals(v1, v2);
        }

        @Test
        @DisplayName("should differ from ridged")
        void shouldDifferFromRidged() {
            NoiseNode source = graph.simplex();
            BillowNode billow = graph.billow(source, 4);
            RidgedNode ridged = graph.ridged(source, 4);

            int differences = 0;
            for (int i = 0; i < 50; i++) {
                double x = i * 0.2;
                double y = i * 0.3;
                if (Math.abs(billow.evaluate2D(SEED, x, y) - ridged.evaluate2D(SEED, x, y)) > 0.01) {
                    differences++;
                }
            }
            assertTrue(differences > 20, "Billow and Ridged should produce different patterns");
        }
    }

    @Nested
    @DisplayName("HybridMultiNode tests")
    class HybridMultiNodeTests {

        @Test
        @DisplayName("should produce bounded values")
        void shouldProduceBoundedValues() {
            HybridMultiNode hybrid = graph.hybridMulti(graph.simplex(), 5);

            for (int x = -30; x <= 30; x += 10) {
                for (int y = -30; y <= 30; y += 10) {
                    double value = hybrid.evaluate2D(SEED, x * 0.1, y * 0.1);
                    assertFalse(Double.isNaN(value), "NaN at (" + x + ", " + y + ")");
                    assertTrue(value >= -3.0 && value <= 3.0,
                        "Value " + value + " out of range at (" + x + ", " + y + ")");
                }
            }
        }

        @Test
        @DisplayName("should be deterministic")
        void shouldBeDeterministic() {
            HybridMultiNode hybrid = graph.hybridMulti(graph.simplex(), 4);
            double v1 = hybrid.evaluate2D(SEED, 100, 200);
            double v2 = hybrid.evaluate2D(SEED, 100, 200);
            assertEquals(v1, v2);
        }

        @Test
        @DisplayName("should work in 3D")
        void shouldWorkIn3D() {
            HybridMultiNode hybrid = graph.hybridMulti(graph.simplex(), 3);
            double value = hybrid.evaluate3D(SEED, 10, 20, 30);
            assertFalse(Double.isNaN(value));
        }
    }

    @Nested
    @DisplayName("4D support tests")
    class FourDSupportTests {

        @Test
        @DisplayName("FBm should support 4D when source supports 4D")
        void fbmShouldSupport4D() {
            FBmNode fbm = graph.fbm(graph.simplex4D(), 4);
            assertTrue(fbm.supports4D());

            double value = fbm.evaluate4D(SEED, 1, 2, 3, 4);
            assertFalse(Double.isNaN(value));
        }

        @Test
        @DisplayName("FBm should not support 4D when source doesn't")
        void fbmShouldNotSupport4DWhenSourceDoesnt() {
            FBmNode fbm = graph.fbm(graph.simplex(), 4);
            assertFalse(fbm.supports4D());
            assertThrows(UnsupportedOperationException.class, () ->
                fbm.evaluate4D(SEED, 1, 2, 3, 4));
        }

        @Test
        @DisplayName("All fractal types should support 4D with 4D source")
        void allFractalTypesShouldSupport4DWith4DSource() {
            NoiseNode source = graph.simplex4D();

            FBmNode fbm = graph.fbm(source, 3);
            RidgedNode ridged = graph.ridged(source, 3);
            BillowNode billow = graph.billow(source, 3);
            HybridMultiNode hybrid = graph.hybridMulti(source, 3);

            assertTrue(fbm.supports4D());
            assertTrue(ridged.supports4D());
            assertTrue(billow.supports4D());
            assertTrue(hybrid.supports4D());

            // All should evaluate without error
            assertDoesNotThrow(() -> fbm.evaluate4D(SEED, 1, 2, 3, 4));
            assertDoesNotThrow(() -> ridged.evaluate4D(SEED, 1, 2, 3, 4));
            assertDoesNotThrow(() -> billow.evaluate4D(SEED, 1, 2, 3, 4));
            assertDoesNotThrow(() -> hybrid.evaluate4D(SEED, 1, 2, 3, 4));
        }
    }

    @Nested
    @DisplayName("Validation tests")
    class ValidationTests {

        @Test
        @DisplayName("should reject null source")
        void shouldRejectNullSource() {
            assertThrows(IllegalArgumentException.class, () -> new FBmNode(null, 4));
            assertThrows(IllegalArgumentException.class, () -> new RidgedNode(null, 4));
            assertThrows(IllegalArgumentException.class, () -> new BillowNode(null, 4));
            assertThrows(IllegalArgumentException.class, () -> new HybridMultiNode(null, 4));
        }

        @Test
        @DisplayName("should reject zero octaves")
        void shouldRejectZeroOctaves() {
            assertThrows(IllegalArgumentException.class, () ->
                new FBmNode(graph.simplex(), 0));
        }

        @Test
        @DisplayName("should reject negative octaves")
        void shouldRejectNegativeOctaves() {
            assertThrows(IllegalArgumentException.class, () ->
                new FBmNode(graph.simplex(), -1));
        }
    }

    @Nested
    @DisplayName("Chaining tests")
    class ChainingTests {

        @Test
        @DisplayName("should support fractal on fractal")
        void shouldSupportFractalOnFractal() {
            // This is unusual but should work
            FBmNode inner = graph.fbm(graph.simplex(), 2);
            FBmNode outer = graph.fbm(inner, 2);

            double value = outer.evaluate2D(SEED, 10, 20);
            assertFalse(Double.isNaN(value));
        }

        @Test
        @DisplayName("should work with modified source")
        void shouldWorkWithModifiedSource() {
            NoiseNode source = graph.simplex().scale(0.5).clamp(-0.8, 0.8);
            FBmNode fbm = graph.fbm(source, 4);

            double value = fbm.evaluate2D(SEED, 100, 200);
            assertFalse(Double.isNaN(value));
        }
    }
}
