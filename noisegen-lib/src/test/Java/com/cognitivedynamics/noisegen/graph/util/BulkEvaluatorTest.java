package com.cognitivedynamics.noisegen.graph.util;

import com.cognitivedynamics.noisegen.graph.NoiseGraph;
import com.cognitivedynamics.noisegen.graph.NoiseNode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for BulkEvaluator.
 */
class BulkEvaluatorTest {

    private NoiseGraph graph;
    private BulkEvaluator evaluator;
    private static final int SEED = 1337;

    @BeforeEach
    void setUp() {
        graph = NoiseGraph.create(SEED);
        evaluator = new BulkEvaluator(SEED);
    }

    @Nested
    @DisplayName("fill2D tests")
    class Fill2DTests {

        @Test
        @DisplayName("should return correct dimensions")
        void shouldReturnCorrectDimensions() {
            NoiseNode node = graph.simplex();
            double[][] result = evaluator.fill2D(node, 64, 32, 0, 0, 1.0);

            assertEquals(32, result.length);
            assertEquals(64, result[0].length);
        }

        @Test
        @DisplayName("should produce values matching direct evaluation")
        void shouldMatchDirectEvaluation() {
            NoiseNode node = graph.simplex();
            double[][] result = evaluator.fill2D(node, 10, 10, 0, 0, 0.5);

            for (int y = 0; y < 10; y++) {
                for (int x = 0; x < 10; x++) {
                    double expected = node.evaluate2D(SEED, x * 0.5, y * 0.5);
                    assertEquals(expected, result[y][x], 0.0001,
                        String.format("Mismatch at [%d][%d]", y, x));
                }
            }
        }

        @Test
        @DisplayName("should work with fractals")
        void shouldWorkWithFractals() {
            NoiseNode node = graph.fbm(graph.simplex(), 4);
            double[][] result = evaluator.fill2D(node, 32, 32, 0, 0, 0.1);

            for (int y = 0; y < 32; y++) {
                for (int x = 0; x < 32; x++) {
                    assertFalse(Double.isNaN(result[y][x]));
                }
            }
        }

        @Test
        @DisplayName("should respect start coordinates")
        void shouldRespectStartCoordinates() {
            NoiseNode node = graph.simplex();
            double[][] result1 = evaluator.fill2D(node, 5, 5, 0.5, 0.5, 0.1);
            double[][] result2 = evaluator.fill2D(node, 5, 5, 100.5, 200.5, 0.1);

            // Results should be different due to different start positions
            boolean anyDifferent = false;
            for (int y = 0; y < 5; y++) {
                for (int x = 0; x < 5; x++) {
                    if (Math.abs(result1[y][x] - result2[y][x]) > 0.001) {
                        anyDifferent = true;
                        break;
                    }
                }
            }
            assertTrue(anyDifferent, "Different start positions should produce different results");
        }
    }

    @Nested
    @DisplayName("fill2DRange tests")
    class Fill2DRangeTests {

        @Test
        @DisplayName("should sample corners correctly")
        void shouldSampleCornersCorrectly() {
            NoiseNode node = graph.simplex();
            double[][] result = evaluator.fill2DRange(node, 11, 11, 0, 0, 10, 10);

            // Check corners
            assertEquals(node.evaluate2D(SEED, 0, 0), result[0][0], 0.0001);
            assertEquals(node.evaluate2D(SEED, 10, 0), result[0][10], 0.0001);
            assertEquals(node.evaluate2D(SEED, 0, 10), result[10][0], 0.0001);
            assertEquals(node.evaluate2D(SEED, 10, 10), result[10][10], 0.0001);
        }
    }

    @Nested
    @DisplayName("fill2DFlat tests")
    class Fill2DFlatTests {

        @Test
        @DisplayName("should return correct length")
        void shouldReturnCorrectLength() {
            NoiseNode node = graph.simplex();
            double[] result = evaluator.fill2DFlat(node, 64, 32, 0, 0, 1.0);

            assertEquals(64 * 32, result.length);
        }

        @Test
        @DisplayName("should match 2D array indexing")
        void shouldMatch2DArrayIndexing() {
            NoiseNode node = graph.simplex();
            int width = 10;
            int height = 8;

            double[][] result2D = evaluator.fill2D(node, width, height, 0, 0, 0.5);
            double[] resultFlat = evaluator.fill2DFlat(node, width, height, 0, 0, 0.5);

            for (int y = 0; y < height; y++) {
                for (int x = 0; x < width; x++) {
                    assertEquals(result2D[y][x], resultFlat[y * width + x], 0.0001,
                        String.format("Mismatch at (%d, %d)", x, y));
                }
            }
        }
    }

    @Nested
    @DisplayName("fill3D tests")
    class Fill3DTests {

        @Test
        @DisplayName("should return correct dimensions")
        void shouldReturnCorrectDimensions() {
            NoiseNode node = graph.simplex();
            double[][][] result = evaluator.fill3D(node, 8, 6, 4, 0, 0, 0, 1.0);

            assertEquals(4, result.length);       // depth (z)
            assertEquals(6, result[0].length);    // height (y)
            assertEquals(8, result[0][0].length); // width (x)
        }

        @Test
        @DisplayName("should produce values matching direct evaluation")
        void shouldMatchDirectEvaluation() {
            NoiseNode node = graph.simplex();
            double[][][] result = evaluator.fill3D(node, 5, 5, 5, 0, 0, 0, 0.5);

            for (int z = 0; z < 5; z++) {
                for (int y = 0; y < 5; y++) {
                    for (int x = 0; x < 5; x++) {
                        double expected = node.evaluate3D(SEED, x * 0.5, y * 0.5, z * 0.5);
                        assertEquals(expected, result[z][y][x], 0.0001);
                    }
                }
            }
        }
    }

    @Nested
    @DisplayName("fill3DFlat tests")
    class Fill3DFlatTests {

        @Test
        @DisplayName("should return correct length")
        void shouldReturnCorrectLength() {
            NoiseNode node = graph.simplex();
            double[] result = evaluator.fill3DFlat(node, 8, 6, 4, 0, 0, 0, 1.0);

            assertEquals(8 * 6 * 4, result.length);
        }

        @Test
        @DisplayName("should match 3D array indexing")
        void shouldMatch3DArrayIndexing() {
            NoiseNode node = graph.simplex();
            int width = 4;
            int height = 3;
            int depth = 2;

            double[][][] result3D = evaluator.fill3D(node, width, height, depth, 0, 0, 0, 0.5);
            double[] resultFlat = evaluator.fill3DFlat(node, width, height, depth, 0, 0, 0, 0.5);

            for (int z = 0; z < depth; z++) {
                for (int y = 0; y < height; y++) {
                    for (int x = 0; x < width; x++) {
                        int flatIndex = z * height * width + y * width + x;
                        assertEquals(result3D[z][y][x], resultFlat[flatIndex], 0.0001);
                    }
                }
            }
        }
    }

    @Nested
    @DisplayName("fill2DFloat tests")
    class Fill2DFloatTests {

        @Test
        @DisplayName("should return float array")
        void shouldReturnFloatArray() {
            NoiseNode node = graph.simplex();
            float[][] result = evaluator.fill2DFloat(node, 10, 10, 0, 0, 1.0);

            assertEquals(10, result.length);
            assertEquals(10, result[0].length);
        }

        @Test
        @DisplayName("should match double version")
        void shouldMatchDoubleVersion() {
            NoiseNode node = graph.simplex();
            double[][] doubleResult = evaluator.fill2D(node, 10, 10, 0, 0, 1.0);
            float[][] floatResult = evaluator.fill2DFloat(node, 10, 10, 0, 0, 1.0);

            for (int y = 0; y < 10; y++) {
                for (int x = 0; x < 10; x++) {
                    assertEquals((float) doubleResult[y][x], floatResult[y][x], 0.0001f);
                }
            }
        }
    }

    @Nested
    @DisplayName("fillLine2D tests")
    class FillLine2DTests {

        @Test
        @DisplayName("should return correct length")
        void shouldReturnCorrectLength() {
            NoiseNode node = graph.simplex();
            double[] result = evaluator.fillLine2D(node, 100, 0, 0, 0.1, 0.1);

            assertEquals(100, result.length);
        }

        @Test
        @DisplayName("should follow line direction")
        void shouldFollowLineDirection() {
            NoiseNode node = graph.simplex();
            double[] result = evaluator.fillLine2D(node, 10, 0, 0, 1.0, 0.5);

            for (int i = 0; i < 10; i++) {
                double expected = node.evaluate2D(SEED, i * 1.0, i * 0.5);
                assertEquals(expected, result[i], 0.0001);
            }
        }
    }

    @Nested
    @DisplayName("Seed tests")
    class SeedTests {

        @Test
        @DisplayName("should use provided seed")
        void shouldUseProvidedSeed() {
            assertEquals(SEED, evaluator.getSeed());
        }

        @Test
        @DisplayName("different seeds should produce different results")
        void differentSeedsShouldProduceDifferentResults() {
            BulkEvaluator evaluator1 = new BulkEvaluator(1);
            BulkEvaluator evaluator2 = new BulkEvaluator(2);

            NoiseNode node = graph.simplex();
            double[][] result1 = evaluator1.fill2D(node, 5, 5, 0.5, 0.5, 0.1);
            double[][] result2 = evaluator2.fill2D(node, 5, 5, 0.5, 0.5, 0.1);

            boolean anyDifferent = false;
            for (int y = 0; y < 5; y++) {
                for (int x = 0; x < 5; x++) {
                    if (Math.abs(result1[y][x] - result2[y][x]) > 0.001) {
                        anyDifferent = true;
                        break;
                    }
                }
            }
            assertTrue(anyDifferent, "Different seeds should produce different results");
        }
    }
}
