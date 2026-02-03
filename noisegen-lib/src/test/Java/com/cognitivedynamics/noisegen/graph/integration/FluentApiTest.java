package com.cognitivedynamics.noisegen.graph.integration;

import com.cognitivedynamics.noisegen.graph.NoiseGraph;
import com.cognitivedynamics.noisegen.graph.NoiseNode;
import com.cognitivedynamics.noisegen.graph.util.BulkEvaluator;
import com.cognitivedynamics.noisegen.transforms.NoiseTransform;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration tests for the fluent API.
 */
class FluentApiTest {

    private NoiseGraph graph;
    private static final int SEED = 1337;

    @BeforeEach
    void setUp() {
        graph = NoiseGraph.create(SEED);
    }

    @Nested
    @DisplayName("Complex terrain generation")
    class TerrainGenerationTests {

        @Test
        @DisplayName("should generate layered terrain")
        void shouldGenerateLayeredTerrain() {
            // Base terrain with FBm
            NoiseNode baseHeight = graph.fbm(graph.simplex().frequency(0.01), 5);

            // Ridged detail for mountains
            NoiseNode mountains = graph.ridged(graph.simplex().frequency(0.02), 4)
                .multiply(0.3);

            // Combine and normalize
            NoiseNode terrain = baseHeight
                .add(mountains)
                .clamp(-1.0, 1.0);

            // Generate a small heightmap
            BulkEvaluator evaluator = new BulkEvaluator(SEED);
            double[][] heightmap = evaluator.fill2D(terrain, 64, 64, 0, 0, 1.0);

            // Verify all values are in expected range
            for (int y = 0; y < 64; y++) {
                for (int x = 0; x < 64; x++) {
                    assertTrue(heightmap[y][x] >= -1.0 && heightmap[y][x] <= 1.0,
                        String.format("Value %.4f out of range at (%d, %d)",
                            heightmap[y][x], x, y));
                }
            }
        }

        @Test
        @DisplayName("should generate warped terrain")
        void shouldGenerateWarpedTerrain() {
            NoiseNode warpSource = graph.simplex().frequency(0.005);
            NoiseNode terrain = graph.fbm(graph.simplex().frequency(0.01), 4)
                .warp(warpSource, 50.0)
                .clamp(-1.0, 1.0);

            double value = terrain.evaluate2D(SEED, 500, 500);
            assertTrue(value >= -1.0 && value <= 1.0);
        }
    }

    @Nested
    @DisplayName("Cloud/nebula generation")
    class CloudGenerationTests {

        @Test
        @DisplayName("should generate billow clouds")
        void shouldGenerateBillowClouds() {
            NoiseNode clouds = graph.billow(graph.simplex().frequency(0.02), 5)
                .multiply(0.5)
                .add(graph.constant(0.5))  // Shift to [0, 1]
                .clamp(0, 1);

            BulkEvaluator evaluator = new BulkEvaluator(SEED);
            double[][] cloudMap = evaluator.fill2D(clouds, 128, 128, 0, 0, 1.0);

            for (int y = 0; y < 128; y++) {
                for (int x = 0; x < 128; x++) {
                    assertTrue(cloudMap[y][x] >= 0 && cloudMap[y][x] <= 1);
                }
            }
        }

        @Test
        @DisplayName("should generate animated 4D clouds")
        void shouldGenerateAnimated4DClouds() {
            NoiseNode clouds = graph.fbm(graph.simplex4D().frequency(0.05), 4);

            // Sample at different time values
            double[] values = new double[10];
            for (int t = 0; t < 10; t++) {
                values[t] = clouds.evaluate4D(SEED, 50, 50, 50, t * 0.1);
            }

            // Verify animation (values change over time)
            boolean anyDifferent = false;
            for (int i = 1; i < 10; i++) {
                if (Math.abs(values[i] - values[0]) > 0.001) {
                    anyDifferent = true;
                    break;
                }
            }
            assertTrue(anyDifferent, "4D noise should animate over W coordinate");
        }
    }

    @Nested
    @DisplayName("Blending operations")
    class BlendingTests {

        @Test
        @DisplayName("should blend between noise sources")
        void shouldBlendBetweenSources() {
            NoiseNode smooth = graph.simplex().frequency(0.01);
            NoiseNode detailed = graph.fbm(graph.simplex().frequency(0.05), 4);
            NoiseNode control = graph.simplex().frequency(0.002)
                .multiply(0.5)
                .add(graph.constant(0.5));  // Map to [0, 1]

            NoiseNode blended = graph.blend(smooth, detailed, control);

            double value = blended.evaluate2D(SEED, 1000, 1000);
            assertFalse(Double.isNaN(value));
        }

        @Test
        @DisplayName("should select between biomes")
        void shouldSelectBetweenBiomes() {
            // Plains biome - smooth
            NoiseNode plains = graph.fbm(graph.simplex().frequency(0.01), 3)
                .multiply(0.3);

            // Mountain biome - rough
            NoiseNode mountains = graph.ridged(graph.simplex().frequency(0.02), 5)
                .multiply(1.5);

            // Biome selection
            NoiseNode biomeSelector = graph.simplex().frequency(0.001)
                .clamp(-1, 1)
                .multiply(0.5)
                .add(graph.constant(0.5));

            NoiseNode terrain = graph.blend(plains, mountains, biomeSelector);

            // Generate test area
            BulkEvaluator evaluator = new BulkEvaluator(SEED);
            double[][] heightmap = evaluator.fill2D(terrain, 32, 32, 0, 0, 10.0);

            // Values should vary
            double min = Double.MAX_VALUE;
            double max = Double.MIN_VALUE;
            for (int y = 0; y < 32; y++) {
                for (int x = 0; x < 32; x++) {
                    min = Math.min(min, heightmap[y][x]);
                    max = Math.max(max, heightmap[y][x]);
                }
            }
            assertTrue(max - min > 0.1, "Terrain should have variation");
        }
    }

    @Nested
    @DisplayName("Transform integration")
    class TransformIntegrationTests {

        @Test
        @DisplayName("should apply custom transforms")
        void shouldApplyCustomTransforms() {
            NoiseTransform squareIt = value -> value * value;

            NoiseNode noise = graph.simplex()
                .abs()  // Make positive first
                .transform(squareIt);

            for (int i = 0; i < 50; i++) {
                double value = noise.evaluate2D(SEED, i * 0.5, i * 0.7);
                assertTrue(value >= 0, "Squared absolute value should be non-negative");
            }
        }

        @Test
        @DisplayName("should chain multiple transforms")
        void shouldChainMultipleTransforms() {
            NoiseTransform addOne = value -> value + 1;
            NoiseTransform halve = value -> value * 0.5f;

            NoiseNode noise = graph.simplex()
                .transform(addOne)   // [-1,1] -> [0,2]
                .transform(halve);   // [0,2] -> [0,1]

            for (int i = 0; i < 50; i++) {
                double value = noise.evaluate2D(SEED, i * 0.3, i * 0.4);
                assertTrue(value >= -0.1 && value <= 1.1,
                    "Transformed value " + value + " out of expected range");
            }
        }
    }

    @Nested
    @DisplayName("Min/Max operations")
    class MinMaxTests {

        @Test
        @DisplayName("should create floor effect with max")
        void shouldCreateFloorWithMax() {
            NoiseNode noise = graph.simplex();
            NoiseNode floor = graph.constant(-0.5);
            NoiseNode floored = noise.max(floor);

            for (int i = 0; i < 100; i++) {
                double value = floored.evaluate2D(SEED, i * 0.2, i * 0.3);
                assertTrue(value >= -0.5,
                    "Value " + value + " should be >= floor");
            }
        }

        @Test
        @DisplayName("should create ceiling effect with min")
        void shouldCreateCeilingWithMin() {
            NoiseNode noise = graph.simplex();
            NoiseNode ceiling = graph.constant(0.5);
            NoiseNode ceilinged = noise.min(ceiling);

            for (int i = 0; i < 100; i++) {
                double value = ceilinged.evaluate2D(SEED, i * 0.2, i * 0.3);
                assertTrue(value <= 0.5,
                    "Value " + value + " should be <= ceiling");
            }
        }
    }

    @Nested
    @DisplayName("Complex chaining")
    class ComplexChainingTests {

        @Test
        @DisplayName("should handle deeply nested graphs")
        void shouldHandleDeeplyNestedGraphs() {
            // Build a complex multi-layered noise
            NoiseNode layer1 = graph.fbm(graph.simplex().frequency(0.01), 3);
            NoiseNode layer2 = graph.ridged(graph.simplex().frequency(0.02), 4);
            NoiseNode layer3 = graph.billow(graph.simplex().frequency(0.015), 3);

            NoiseNode combined = layer1
                .add(layer2.multiply(0.5))
                .add(layer3.multiply(0.25))
                .warp(graph.simplex().frequency(0.005), 20.0)
                .clamp(-1.5, 1.5)
                .multiply(0.667);  // Normalize back to ~[-1, 1]

            // Should still evaluate correctly
            for (int i = 0; i < 20; i++) {
                double value = combined.evaluate2D(SEED, i * 10, i * 15);
                assertFalse(Double.isNaN(value), "Should not produce NaN");
                assertTrue(value >= -2.0 && value <= 2.0, "Value should be bounded");
            }
        }

        @Test
        @DisplayName("example from plan should work")
        void exampleFromPlanShouldWork() {
            // Example from the plan documentation
            NoiseNode terrain = graph.fbm(graph.simplex().frequency(0.01), 5)
                .add(graph.ridged(graph.simplex().frequency(0.02), 4).multiply(0.3))
                .clamp(-1.0, 1.0);

            // Evaluate
            double height = terrain.evaluate2D(SEED, 100.0, 200.0);

            assertFalse(Double.isNaN(height));
            assertTrue(height >= -1.0 && height <= 1.0);

            // Bulk evaluation
            BulkEvaluator bulk = new BulkEvaluator(SEED);
            double[][] heightmap = bulk.fill2D(terrain, 512, 512, 0, 0, 1.0);

            assertEquals(512, heightmap.length);
            assertEquals(512, heightmap[0].length);
        }
    }

    @Nested
    @DisplayName("Factory method tests")
    class FactoryMethodTests {

        @Test
        @DisplayName("all factory methods should create valid nodes")
        void allFactoryMethodsShouldCreateValidNodes() {
            // Source nodes
            assertNotNull(graph.constant(0.5).getNodeType());
            assertNotNull(graph.simplex().getNodeType());
            assertNotNull(graph.simplexSmooth().getNodeType());
            assertNotNull(graph.perlin().getNodeType());
            assertNotNull(graph.value().getNodeType());
            assertNotNull(graph.valueCubic().getNodeType());
            assertNotNull(graph.cellular().getNodeType());
            assertNotNull(graph.simplex4D().getNodeType());

            // Fractal nodes
            NoiseNode source = graph.simplex();
            assertNotNull(graph.fbm(source, 4).getNodeType());
            assertNotNull(graph.ridged(source, 4).getNodeType());
            assertNotNull(graph.billow(source, 4).getNodeType());
            assertNotNull(graph.hybridMulti(source, 4).getNodeType());

            // Combiner nodes
            NoiseNode a = graph.simplex();
            NoiseNode b = graph.perlin();
            assertNotNull(graph.add(a, b).getNodeType());
            assertNotNull(graph.subtract(a, b).getNodeType());
            assertNotNull(graph.multiply(a, b).getNodeType());
            assertNotNull(graph.min(a, b).getNodeType());
            assertNotNull(graph.max(a, b).getNodeType());
            assertNotNull(graph.blend(a, b, graph.constant(0.5)).getNodeType());

            // Modifier nodes
            assertNotNull(graph.scale(source, 2.0).getNodeType());
            assertNotNull(graph.offset(source, 1, 2, 3).getNodeType());
            assertNotNull(graph.clamp(source, -1, 1).getNodeType());
            assertNotNull(graph.abs(source).getNodeType());
            assertNotNull(graph.invert(source).getNodeType());
            assertNotNull(graph.transform(source, v -> v * 2).getNodeType());

            // Warp nodes
            assertNotNull(graph.warp(source, graph.simplex(), 5.0).getNodeType());
        }
    }
}
