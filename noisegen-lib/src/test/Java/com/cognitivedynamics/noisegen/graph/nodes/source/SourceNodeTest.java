package com.cognitivedynamics.noisegen.graph.nodes.source;

import com.cognitivedynamics.noisegen.NoiseTypes.CellularDistanceFunction;
import com.cognitivedynamics.noisegen.NoiseTypes.CellularReturnType;
import com.cognitivedynamics.noisegen.graph.NoiseGraph;
import com.cognitivedynamics.noisegen.graph.NoiseNode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for source nodes.
 */
class SourceNodeTest {

    private NoiseGraph graph;
    private static final int SEED = 1337;

    @BeforeEach
    void setUp() {
        graph = NoiseGraph.create(SEED);
    }

    @Nested
    @DisplayName("SimplexSourceNode tests")
    class SimplexSourceNodeTests {

        @Test
        @DisplayName("should produce bounded 2D values")
        void shouldProduceBounded2DValues() {
            SimplexSourceNode node = graph.simplex();
            for (int x = -50; x <= 50; x += 10) {
                for (int y = -50; y <= 50; y += 10) {
                    double value = node.evaluate2D(SEED, x * 0.1, y * 0.1);
                    assertFalse(Double.isNaN(value), "NaN at (" + x + ", " + y + ")");
                    assertTrue(value >= -1.5 && value <= 1.5,
                        "Value " + value + " out of range at (" + x + ", " + y + ")");
                }
            }
        }

        @Test
        @DisplayName("should produce bounded 3D values")
        void shouldProduceBounded3DValues() {
            SimplexSourceNode node = graph.simplex();
            for (int x = -20; x <= 20; x += 10) {
                for (int y = -20; y <= 20; y += 10) {
                    for (int z = -20; z <= 20; z += 10) {
                        double value = node.evaluate3D(SEED, x * 0.1, y * 0.1, z * 0.1);
                        assertFalse(Double.isNaN(value));
                        assertTrue(value >= -1.5 && value <= 1.5);
                    }
                }
            }
        }

        @Test
        @DisplayName("should be deterministic")
        void shouldBeDeterministic() {
            SimplexSourceNode node = graph.simplex();
            double v1 = node.evaluate2D(SEED, 123.456, 789.012);
            double v2 = node.evaluate2D(SEED, 123.456, 789.012);
            assertEquals(v1, v2);
        }

        @Test
        @DisplayName("different positions should produce different values")
        void differentPositionsShouldProduceDifferentValues() {
            SimplexSourceNode node = graph.simplex();
            double v1 = node.evaluate2D(SEED, 0.5, 0.5);
            double v2 = node.evaluate2D(SEED, 10.3, 10.7);
            assertNotEquals(v1, v2, 0.001);
        }

        @Test
        @DisplayName("smooth variant should differ from standard")
        void smoothShouldDifferFromStandard() {
            SimplexSourceNode standard = graph.simplex();
            SimplexSourceNode smooth = graph.simplexSmooth();

            int differences = 0;
            for (int i = 0; i < 50; i++) {
                double x = i * 0.2;
                double y = i * 0.3;
                if (Math.abs(standard.evaluate2D(SEED, x, y) - smooth.evaluate2D(SEED, x, y)) > 0.001) {
                    differences++;
                }
            }
            assertTrue(differences > 20, "Standard and smooth should produce different values");
        }

        @Test
        @DisplayName("frequency should scale coordinates")
        void frequencyShouldScaleCoordinates() {
            SimplexSourceNode base = graph.simplex();
            SimplexSourceNode scaled = base.frequency(2.0);

            // At frequency 2, (1, 1) should equal base at (2, 2)
            double baseAt2_2 = base.evaluate2D(SEED, 2, 2);
            double scaledAt1_1 = scaled.evaluate2D(SEED, 1, 1);
            assertEquals(baseAt2_2, scaledAt1_1, 0.0001);
        }

        @Test
        @DisplayName("should not support 4D")
        void shouldNotSupport4D() {
            SimplexSourceNode node = graph.simplex();
            assertFalse(node.supports4D());
            assertThrows(UnsupportedOperationException.class,
                () -> node.evaluate4D(SEED, 0, 0, 0, 0));
        }

        @Test
        @DisplayName("should report correct node type")
        void shouldReportCorrectNodeType() {
            assertEquals("Simplex", graph.simplex().getNodeType());
            assertEquals("SimplexSmooth", graph.simplexSmooth().getNodeType());
        }
    }

    @Nested
    @DisplayName("PerlinSourceNode tests")
    class PerlinSourceNodeTests {

        @Test
        @DisplayName("should produce bounded values")
        void shouldProduceBoundedValues() {
            PerlinSourceNode node = graph.perlin();
            for (int x = -50; x <= 50; x += 10) {
                for (int y = -50; y <= 50; y += 10) {
                    double value = node.evaluate2D(SEED, x * 0.1, y * 0.1);
                    assertFalse(Double.isNaN(value));
                    assertTrue(value >= -1.5 && value <= 1.5);
                }
            }
        }

        @Test
        @DisplayName("should be deterministic")
        void shouldBeDeterministic() {
            PerlinSourceNode node = graph.perlin();
            double v1 = node.evaluate2D(SEED, 50.0, 50.0);
            double v2 = node.evaluate2D(SEED, 50.0, 50.0);
            assertEquals(v1, v2);
        }

        @Test
        @DisplayName("frequency should work correctly")
        void frequencyShouldWorkCorrectly() {
            PerlinSourceNode node = graph.perlin();
            assertEquals(1.0, node.getFrequency());

            PerlinSourceNode scaled = node.frequency(0.5);
            assertEquals(0.5, scaled.getFrequency());
        }

        @Test
        @DisplayName("should not support 4D")
        void shouldNotSupport4D() {
            assertFalse(graph.perlin().supports4D());
        }
    }

    @Nested
    @DisplayName("ValueSourceNode tests")
    class ValueSourceNodeTests {

        @Test
        @DisplayName("should produce bounded values")
        void shouldProduceBoundedValues() {
            ValueSourceNode node = graph.value();
            for (int x = -50; x <= 50; x += 10) {
                for (int y = -50; y <= 50; y += 10) {
                    double value = node.evaluate2D(SEED, x * 0.1, y * 0.1);
                    assertFalse(Double.isNaN(value));
                    assertTrue(value >= -1.5 && value <= 1.5);
                }
            }
        }

        @Test
        @DisplayName("cubic should differ from standard")
        void cubicShouldDifferFromStandard() {
            ValueSourceNode standard = graph.value();
            ValueSourceNode cubic = graph.valueCubic();

            assertFalse(standard.isCubic());
            assertTrue(cubic.isCubic());
            assertEquals("Value", standard.getNodeType());
            assertEquals("ValueCubic", cubic.getNodeType());
        }

        @Test
        @DisplayName("should not support 4D")
        void shouldNotSupport4D() {
            assertFalse(graph.value().supports4D());
        }
    }

    @Nested
    @DisplayName("CellularSourceNode tests")
    class CellularSourceNodeTests {

        @Test
        @DisplayName("should produce bounded values")
        void shouldProduceBoundedValues() {
            CellularSourceNode node = graph.cellular();
            for (int x = -20; x <= 20; x += 5) {
                for (int y = -20; y <= 20; y += 5) {
                    double value = node.evaluate2D(SEED, x * 0.1, y * 0.1);
                    assertFalse(Double.isNaN(value));
                    // Cellular noise has a wider range
                    assertTrue(value >= -2.0 && value <= 2.0,
                        "Value " + value + " out of range");
                }
            }
        }

        @Test
        @DisplayName("should be configurable")
        void shouldBeConfigurable() {
            CellularSourceNode node = graph.cellular();

            CellularSourceNode withManhattan = node.withDistanceFunction(CellularDistanceFunction.Manhattan);
            assertEquals(CellularDistanceFunction.Manhattan, withManhattan.getDistanceFunction());

            CellularSourceNode withCellValue = node.withReturnType(CellularReturnType.CellValue);
            assertEquals(CellularReturnType.CellValue, withCellValue.getReturnType());

            CellularSourceNode withJitter = node.withJitter(0.5);
            assertEquals(0.5, withJitter.getJitterModifier());
        }

        @Test
        @DisplayName("should not support 4D")
        void shouldNotSupport4D() {
            assertFalse(graph.cellular().supports4D());
        }
    }

    @Nested
    @DisplayName("Simplex4DSourceNode tests")
    class Simplex4DSourceNodeTests {

        @Test
        @DisplayName("should support 4D")
        void shouldSupport4D() {
            Simplex4DSourceNode node = graph.simplex4D();
            assertTrue(node.supports4D());
        }

        @Test
        @DisplayName("should produce bounded 4D values")
        void shouldProduceBounded4DValues() {
            Simplex4DSourceNode node = graph.simplex4D();
            for (int x = -10; x <= 10; x += 5) {
                for (int y = -10; y <= 10; y += 5) {
                    for (int z = -10; z <= 10; z += 5) {
                        for (int w = -10; w <= 10; w += 5) {
                            double value = node.evaluate4D(SEED, x * 0.1, y * 0.1, z * 0.1, w * 0.1);
                            assertFalse(Double.isNaN(value));
                            assertTrue(value >= -1.5 && value <= 1.5);
                        }
                    }
                }
            }
        }

        @Test
        @DisplayName("should be deterministic in 4D")
        void shouldBeDeterministicIn4D() {
            Simplex4DSourceNode node = graph.simplex4D();
            double v1 = node.evaluate4D(SEED, 1, 2, 3, 4);
            double v2 = node.evaluate4D(SEED, 1, 2, 3, 4);
            assertEquals(v1, v2);
        }

        @Test
        @DisplayName("W coordinate should affect output")
        void wCoordinateShouldAffectOutput() {
            Simplex4DSourceNode node = graph.simplex4D();
            double v1 = node.evaluate4D(SEED, 10, 20, 30, 0);
            double v2 = node.evaluate4D(SEED, 10, 20, 30, 1);
            assertNotEquals(v1, v2, 0.001);
        }

        @Test
        @DisplayName("frequency should work for 4D")
        void frequencyShouldWorkFor4D() {
            Simplex4DSourceNode base = graph.simplex4D();
            Simplex4DSourceNode scaled = base.frequency(2.0);

            double baseAt2 = base.evaluate4D(SEED, 2, 2, 2, 2);
            double scaledAt1 = scaled.evaluate4D(SEED, 1, 1, 1, 1);
            assertEquals(baseAt2, scaledAt1, 0.0001);
        }
    }

    @Nested
    @DisplayName("Common source node tests")
    class CommonSourceNodeTests {

        @ParameterizedTest
        @ValueSource(ints = {0, 1, 42, 1337, -1, Integer.MAX_VALUE, Integer.MIN_VALUE})
        @DisplayName("all sources should work with various seeds")
        void allSourcesShouldWorkWithVariousSeeds(int seed) {
            NoiseNode[] nodes = {
                graph.simplex(),
                graph.simplexSmooth(),
                graph.perlin(),
                graph.value(),
                graph.valueCubic(),
                graph.cellular(),
                graph.simplex4D()
            };

            for (NoiseNode node : nodes) {
                double value = node.evaluate2D(seed, 50.0, 50.0);
                assertFalse(Double.isNaN(value), "NaN for " + node.getNodeType());
                assertFalse(Double.isInfinite(value), "Infinite for " + node.getNodeType());
            }
        }

        @Test
        @DisplayName("all sources should handle large coordinates")
        void allSourcesShouldHandleLargeCoordinates() {
            NoiseNode[] nodes = {
                graph.simplex(),
                graph.perlin(),
                graph.value(),
                graph.cellular()
            };

            double large = 1000000.0;
            for (NoiseNode node : nodes) {
                double value = node.evaluate2D(SEED, large, large);
                assertFalse(Double.isNaN(value));
                assertFalse(Double.isInfinite(value));
            }
        }

        @Test
        @DisplayName("all sources should have non-empty node type")
        void allSourcesShouldHaveNodeType() {
            NoiseNode[] nodes = {
                graph.simplex(),
                graph.simplexSmooth(),
                graph.perlin(),
                graph.value(),
                graph.valueCubic(),
                graph.cellular(),
                graph.simplex4D()
            };

            for (NoiseNode node : nodes) {
                assertNotNull(node.getNodeType());
                assertFalse(node.getNodeType().isEmpty());
            }
        }
    }
}
