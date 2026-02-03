package com.cognitivedynamics.noisegen.graph.integration;

import com.cognitivedynamics.noisegen.graph.NoiseGraph;
import com.cognitivedynamics.noisegen.graph.NoiseNode;
import com.cognitivedynamics.noisegen.graph.util.BulkEvaluator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.RepeatedTest;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Thread safety tests for the noise graph system.
 */
class ThreadSafetyTest {

    private NoiseGraph graph;
    private static final int SEED = 1337;

    @BeforeEach
    void setUp() {
        graph = NoiseGraph.create(SEED);
    }

    @Nested
    @DisplayName("Concurrent evaluation tests")
    class ConcurrentEvaluationTests {

        @Test
        @DisplayName("should produce consistent results from multiple threads")
        void shouldProduceConsistentResults() throws InterruptedException, ExecutionException {
            NoiseNode node = graph.fbm(graph.simplex(), 4);

            // Pre-compute expected values
            double[] expected = new double[100];
            for (int i = 0; i < 100; i++) {
                expected[i] = node.evaluate2D(SEED, i * 0.5, i * 0.7);
            }

            // Verify from multiple threads
            ExecutorService executor = Executors.newFixedThreadPool(8);
            List<Future<Boolean>> futures = new ArrayList<>();

            for (int t = 0; t < 16; t++) {
                futures.add(executor.submit(() -> {
                    for (int i = 0; i < 100; i++) {
                        double actual = node.evaluate2D(SEED, i * 0.5, i * 0.7);
                        if (Math.abs(actual - expected[i]) > 0.0001) {
                            return false;
                        }
                    }
                    return true;
                }));
            }

            executor.shutdown();
            assertTrue(executor.awaitTermination(30, TimeUnit.SECONDS));

            for (Future<Boolean> future : futures) {
                assertTrue(future.get(), "Thread produced inconsistent results");
            }
        }

        @Test
        @DisplayName("should handle concurrent access to same node")
        void shouldHandleConcurrentAccessToSameNode() throws InterruptedException {
            // Create a moderately complex node
            NoiseNode node = graph.fbm(graph.simplex().frequency(0.01), 4)
                .add(graph.ridged(graph.simplex().frequency(0.02), 3).multiply(0.5))
                .clamp(-1, 1);

            int numThreads = 8;
            int iterationsPerThread = 1000;
            CountDownLatch latch = new CountDownLatch(numThreads);
            AtomicInteger errors = new AtomicInteger(0);

            for (int t = 0; t < numThreads; t++) {
                final int threadId = t;
                new Thread(() -> {
                    try {
                        for (int i = 0; i < iterationsPerThread; i++) {
                            double x = threadId * 100 + i * 0.1;
                            double y = threadId * 200 + i * 0.2;
                            double value = node.evaluate2D(SEED, x, y);

                            if (Double.isNaN(value) || Double.isInfinite(value)) {
                                errors.incrementAndGet();
                            }
                        }
                    } finally {
                        latch.countDown();
                    }
                }).start();
            }

            assertTrue(latch.await(30, TimeUnit.SECONDS));
            assertEquals(0, errors.get(), "Some evaluations produced invalid values");
        }

        @RepeatedTest(5)
        @DisplayName("determinism should hold under concurrent load")
        void determinismShouldHoldUnderLoad() throws InterruptedException, ExecutionException {
            NoiseNode node = graph.warp(
                graph.simplex().frequency(0.01),
                graph.simplex().frequency(0.005),
                10.0
            );

            // Single-threaded reference
            double reference = node.evaluate2D(SEED, 12345.678, 98765.432);

            // Verify from many threads simultaneously
            ExecutorService executor = Executors.newFixedThreadPool(16);
            List<Future<Double>> futures = new ArrayList<>();

            for (int i = 0; i < 100; i++) {
                futures.add(executor.submit(() ->
                    node.evaluate2D(SEED, 12345.678, 98765.432)
                ));
            }

            executor.shutdown();
            assertTrue(executor.awaitTermination(30, TimeUnit.SECONDS));

            for (Future<Double> future : futures) {
                assertEquals(reference, future.get(), 0.0,
                    "Concurrent evaluation differs from reference");
            }
        }
    }

    @Nested
    @DisplayName("BulkEvaluator thread safety")
    class BulkEvaluatorThreadSafetyTests {

        @Test
        @DisplayName("should work from multiple threads")
        void shouldWorkFromMultipleThreads() throws InterruptedException, ExecutionException {
            NoiseNode node = graph.fbm(graph.simplex(), 3);
            BulkEvaluator evaluator = new BulkEvaluator(SEED);

            // Pre-compute reference
            double[][] reference = evaluator.fill2D(node, 64, 64, 0, 0, 0.5);

            ExecutorService executor = Executors.newFixedThreadPool(4);
            List<Future<double[][]>> futures = new ArrayList<>();

            for (int t = 0; t < 8; t++) {
                futures.add(executor.submit(() ->
                    evaluator.fill2D(node, 64, 64, 0, 0, 0.5)
                ));
            }

            executor.shutdown();
            assertTrue(executor.awaitTermination(30, TimeUnit.SECONDS));

            for (Future<double[][]> future : futures) {
                double[][] result = future.get();
                for (int y = 0; y < 64; y++) {
                    for (int x = 0; x < 64; x++) {
                        assertEquals(reference[y][x], result[y][x], 0.0,
                            "Results differ from reference");
                    }
                }
            }
        }

        @Test
        @DisplayName("different evaluators should work independently")
        void differentEvaluatorsShouldWorkIndependently() throws InterruptedException {
            NoiseNode node = graph.simplex();

            int numThreads = 4;
            CountDownLatch latch = new CountDownLatch(numThreads);
            AtomicInteger errors = new AtomicInteger(0);

            for (int t = 0; t < numThreads; t++) {
                final int seed = t * 1000;
                new Thread(() -> {
                    try {
                        BulkEvaluator evaluator = new BulkEvaluator(seed);
                        double[][] result = evaluator.fill2D(node, 32, 32, 0, 0, 1.0);

                        // Verify results match individual evaluation
                        for (int y = 0; y < 32; y++) {
                            for (int x = 0; x < 32; x++) {
                                double expected = node.evaluate2D(seed, x, y);
                                if (Math.abs(expected - result[y][x]) > 0.0001) {
                                    errors.incrementAndGet();
                                }
                            }
                        }
                    } finally {
                        latch.countDown();
                    }
                }).start();
            }

            assertTrue(latch.await(30, TimeUnit.SECONDS));
            assertEquals(0, errors.get());
        }
    }

    @Nested
    @DisplayName("Node immutability tests")
    class ImmutabilityTests {

        @Test
        @DisplayName("fluent methods should return new instances")
        void fluentMethodsShouldReturnNewInstances() {
            NoiseNode original = graph.simplex();
            NoiseNode scaled = original.scale(2.0);
            NoiseNode clamped = original.clamp(-0.5, 0.5);

            assertNotSame(original, scaled);
            assertNotSame(original, clamped);
            assertNotSame(scaled, clamped);
        }

        @Test
        @DisplayName("frequency changes should return new instances")
        void frequencyChangesShouldReturnNewInstances() {
            var original = graph.simplex();
            var modified = original.frequency(2.0);

            assertNotSame(original, modified);
            assertEquals(1.0, original.getFrequency());
            assertEquals(2.0, modified.getFrequency());
        }

        @Test
        @DisplayName("original node should be unaffected by modifications")
        void originalShouldBeUnaffected() {
            NoiseNode original = graph.simplex();
            double originalValue = original.evaluate2D(SEED, 100, 100);

            // Create modified versions
            NoiseNode scaled = original.scale(10.0);
            NoiseNode inverted = original.invert();
            NoiseNode clamped = original.clamp(0, 0.1);

            // Original should still produce same value
            assertEquals(originalValue, original.evaluate2D(SEED, 100, 100), 0.0);
        }
    }

    @Nested
    @DisplayName("Stress tests")
    class StressTests {

        @Test
        @DisplayName("should handle many concurrent graph evaluations")
        void shouldHandleManyConcurrentEvaluations() throws InterruptedException {
            // Create several different noise graphs
            NoiseNode[] graphs = {
                graph.simplex(),
                graph.fbm(graph.perlin(), 4),
                graph.ridged(graph.simplex(), 3).multiply(0.5),
                graph.billow(graph.value(), 5).clamp(-1, 1),
                graph.warp(graph.simplex(), graph.simplex(), 5.0)
            };

            int totalOperations = 10000;
            CountDownLatch latch = new CountDownLatch(totalOperations);
            AtomicInteger successCount = new AtomicInteger(0);

            ExecutorService executor = Executors.newFixedThreadPool(16);

            for (int i = 0; i < totalOperations; i++) {
                final int index = i;
                executor.submit(() -> {
                    try {
                        NoiseNode node = graphs[index % graphs.length];
                        double value = node.evaluate2D(SEED, index * 0.1, index * 0.2);
                        if (!Double.isNaN(value) && !Double.isInfinite(value)) {
                            successCount.incrementAndGet();
                        }
                    } finally {
                        latch.countDown();
                    }
                });
            }

            executor.shutdown();
            assertTrue(latch.await(60, TimeUnit.SECONDS));
            assertEquals(totalOperations, successCount.get(),
                "All operations should succeed");
        }
    }
}
