package com.cognitivedynamics.noisegen.benchmark;

import com.cognitivedynamics.noisegen.FastNoiseLite;
import com.cognitivedynamics.noisegen.FastNoiseLite.*;

/**
 * Performance benchmarks for FastNoiseLite Nouveau.
 *
 * Measures millions of noise points generated per second for each noise type
 * and configuration, similar to the original FastNoiseLite benchmarks.
 *
 * Run with: mvn exec:java -Dexec.mainClass="com.cognitivedynamics.noisegen.benchmark.NoiseBenchmark"
 * Or run the main method directly from your IDE.
 */
public class NoiseBenchmark {

    // Benchmark configuration
    private static final int WARMUP_ITERATIONS = 3;
    private static final int BENCHMARK_ITERATIONS = 5;
    private static final int POINTS_PER_ITERATION = 10_000_000; // 10 million points
    private static final int GRID_SIZE = 2154; // ~10M points when squared (for 2D)
    private static final int GRID_SIZE_3D = 215; // ~10M points when cubed (for 3D)

    public static void main(String[] args) {
        System.out.println("═══════════════════════════════════════════════════════════════════════");
        System.out.println("                    FastNoiseLite Nouveau Benchmarks");
        System.out.println("═══════════════════════════════════════════════════════════════════════");
        System.out.println();
        printSystemInfo();
        System.out.println();

        // Run 2D benchmarks
        run2DBenchmarks();

        System.out.println();

        // Run 3D benchmarks
        run3DBenchmarks();

        System.out.println();

        // Run 4D benchmarks (extension)
        run4DBenchmarks();

        System.out.println();

        // Run fractal benchmarks
        runFractalBenchmarks();

        System.out.println();

        // Run spatial utility benchmarks (extensions)
        runSpatialBenchmarks();

        System.out.println("═══════════════════════════════════════════════════════════════════════");
    }

    private static void printSystemInfo() {
        Runtime runtime = Runtime.getRuntime();
        System.out.println("System Information:");
        System.out.println("───────────────────────────────────────────────────────────────────────");
        System.out.printf("  Java Version:    %s (%s)%n",
            System.getProperty("java.version"),
            System.getProperty("java.vendor"));
        System.out.printf("  OS:              %s %s (%s)%n",
            System.getProperty("os.name"),
            System.getProperty("os.version"),
            System.getProperty("os.arch"));
        System.out.printf("  Available CPUs:  %d%n", runtime.availableProcessors());
        System.out.printf("  Max Memory:      %d MB%n", runtime.maxMemory() / (1024 * 1024));
        System.out.printf("  Points/iter:     %,d%n", POINTS_PER_ITERATION);
    }

    private static void run2DBenchmarks() {
        System.out.println("2D Noise Benchmarks (million points/second, higher = better):");
        System.out.println("───────────────────────────────────────────────────────────────────────");
        System.out.printf("  %-20s %12s%n", "Noise Type", "M points/s");
        System.out.println("  ────────────────────────────────────────");

        benchmark2D("Value", NoiseType.Value);
        benchmark2D("ValueCubic", NoiseType.ValueCubic);
        benchmark2D("Perlin", NoiseType.Perlin);
        benchmark2D("OpenSimplex2", NoiseType.OpenSimplex2);
        benchmark2D("OpenSimplex2S", NoiseType.OpenSimplex2S);
        benchmark2D("Cellular", NoiseType.Cellular);
    }

    private static void run3DBenchmarks() {
        System.out.println("3D Noise Benchmarks (million points/second, higher = better):");
        System.out.println("───────────────────────────────────────────────────────────────────────");
        System.out.printf("  %-20s %12s%n", "Noise Type", "M points/s");
        System.out.println("  ────────────────────────────────────────");

        benchmark3D("Value", NoiseType.Value);
        benchmark3D("ValueCubic", NoiseType.ValueCubic);
        benchmark3D("Perlin", NoiseType.Perlin);
        benchmark3D("OpenSimplex2", NoiseType.OpenSimplex2);
        benchmark3D("OpenSimplex2S", NoiseType.OpenSimplex2S);
        benchmark3D("Cellular", NoiseType.Cellular);
    }

    private static void run4DBenchmarks() {
        System.out.println("4D Noise Benchmarks [EXT] (million points/second, higher = better):");
        System.out.println("───────────────────────────────────────────────────────────────────────");
        System.out.printf("  %-20s %12s%n", "Noise Type", "M points/s");
        System.out.println("  ────────────────────────────────────────");

        benchmark4D("Simplex 4D", NoiseType.OpenSimplex2);
    }

    private static void runFractalBenchmarks() {
        System.out.println("Fractal Noise Benchmarks - 3D with 4 octaves (million points/second):");
        System.out.println("───────────────────────────────────────────────────────────────────────");
        System.out.printf("  %-20s %12s%n", "Fractal Type", "M points/s");
        System.out.println("  ────────────────────────────────────────");

        benchmarkFractal3D("FBm", FractalType.FBm);
        benchmarkFractal3D("Ridged", FractalType.Ridged);
        benchmarkFractal3D("PingPong", FractalType.PingPong);
        benchmarkFractal3D("Billow [EXT]", FractalType.Billow);
        benchmarkFractal3D("HybridMulti [EXT]", FractalType.HybridMulti);
    }

    private static void runSpatialBenchmarks() {
        System.out.println("Spatial Utilities Benchmarks [EXT] - 2D (million points/second):");
        System.out.println("───────────────────────────────────────────────────────────────────────");
        System.out.printf("  %-20s %12s%n", "Utility", "M points/s");
        System.out.println("  ────────────────────────────────────────");

        benchmarkChunkedNoise();
        benchmarkDoublePrecisionNoise();
        benchmarkTiledNoise();
    }

    // ─────────────────────────────────────────────────────────────────────────
    // 2D Benchmarks
    // ─────────────────────────────────────────────────────────────────────────

    private static void benchmark2D(String name, NoiseType noiseType) {
        FastNoiseLite noise = new FastNoiseLite(1337);
        noise.SetNoiseType(noiseType);
        noise.SetFrequency(0.01f);

        // Warmup
        for (int i = 0; i < WARMUP_ITERATIONS; i++) {
            run2DIteration(noise);
        }

        // Benchmark
        long totalTime = 0;
        for (int i = 0; i < BENCHMARK_ITERATIONS; i++) {
            long start = System.nanoTime();
            run2DIteration(noise);
            totalTime += System.nanoTime() - start;
        }

        double avgTimeMs = (totalTime / BENCHMARK_ITERATIONS) / 1_000_000.0;
        double pointsPerSecond = (POINTS_PER_ITERATION / avgTimeMs) * 1000.0;
        double millionPointsPerSecond = pointsPerSecond / 1_000_000.0;

        System.out.printf("  %-20s %12.2f%n", name, millionPointsPerSecond);
    }

    private static float run2DIteration(FastNoiseLite noise) {
        float sum = 0;
        int idx = 0;
        for (int y = 0; y < GRID_SIZE && idx < POINTS_PER_ITERATION; y++) {
            for (int x = 0; x < GRID_SIZE && idx < POINTS_PER_ITERATION; x++) {
                sum += noise.GetNoise(x, y);
                idx++;
            }
        }
        return sum; // Prevent dead code elimination
    }

    // ─────────────────────────────────────────────────────────────────────────
    // 3D Benchmarks
    // ─────────────────────────────────────────────────────────────────────────

    private static void benchmark3D(String name, NoiseType noiseType) {
        FastNoiseLite noise = new FastNoiseLite(1337);
        noise.SetNoiseType(noiseType);
        noise.SetFrequency(0.01f);

        // Warmup
        for (int i = 0; i < WARMUP_ITERATIONS; i++) {
            run3DIteration(noise);
        }

        // Benchmark
        long totalTime = 0;
        for (int i = 0; i < BENCHMARK_ITERATIONS; i++) {
            long start = System.nanoTime();
            run3DIteration(noise);
            totalTime += System.nanoTime() - start;
        }

        double avgTimeMs = (totalTime / BENCHMARK_ITERATIONS) / 1_000_000.0;
        double pointsPerSecond = (POINTS_PER_ITERATION / avgTimeMs) * 1000.0;
        double millionPointsPerSecond = pointsPerSecond / 1_000_000.0;

        System.out.printf("  %-20s %12.2f%n", name, millionPointsPerSecond);
    }

    private static float run3DIteration(FastNoiseLite noise) {
        float sum = 0;
        int idx = 0;
        for (int z = 0; z < GRID_SIZE_3D && idx < POINTS_PER_ITERATION; z++) {
            for (int y = 0; y < GRID_SIZE_3D && idx < POINTS_PER_ITERATION; y++) {
                for (int x = 0; x < GRID_SIZE_3D && idx < POINTS_PER_ITERATION; x++) {
                    sum += noise.GetNoise(x, y, z);
                    idx++;
                }
            }
        }
        return sum;
    }

    // ─────────────────────────────────────────────────────────────────────────
    // 4D Benchmarks
    // ─────────────────────────────────────────────────────────────────────────

    private static void benchmark4D(String name, NoiseType noiseType) {
        FastNoiseLite noise = new FastNoiseLite(1337);
        noise.SetNoiseType(noiseType);
        noise.SetFrequency(0.01f);

        // Warmup
        for (int i = 0; i < WARMUP_ITERATIONS; i++) {
            run4DIteration(noise);
        }

        // Benchmark
        long totalTime = 0;
        for (int i = 0; i < BENCHMARK_ITERATIONS; i++) {
            long start = System.nanoTime();
            run4DIteration(noise);
            totalTime += System.nanoTime() - start;
        }

        double avgTimeMs = (totalTime / BENCHMARK_ITERATIONS) / 1_000_000.0;
        double pointsPerSecond = (POINTS_PER_ITERATION / avgTimeMs) * 1000.0;
        double millionPointsPerSecond = pointsPerSecond / 1_000_000.0;

        System.out.printf("  %-20s %12.2f%n", name, millionPointsPerSecond);
    }

    private static float run4DIteration(FastNoiseLite noise) {
        float sum = 0;
        int size = 56; // 56^4 ≈ 10M
        int idx = 0;
        for (int w = 0; w < size && idx < POINTS_PER_ITERATION; w++) {
            for (int z = 0; z < size && idx < POINTS_PER_ITERATION; z++) {
                for (int y = 0; y < size && idx < POINTS_PER_ITERATION; y++) {
                    for (int x = 0; x < size && idx < POINTS_PER_ITERATION; x++) {
                        sum += noise.GetNoise(x, y, z, w);
                        idx++;
                    }
                }
            }
        }
        return sum;
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Fractal Benchmarks
    // ─────────────────────────────────────────────────────────────────────────

    private static void benchmarkFractal3D(String name, FractalType fractalType) {
        FastNoiseLite noise = new FastNoiseLite(1337);
        noise.SetNoiseType(NoiseType.OpenSimplex2);
        noise.SetFractalType(fractalType);
        noise.SetFractalOctaves(4);
        noise.SetFrequency(0.01f);

        // Warmup
        for (int i = 0; i < WARMUP_ITERATIONS; i++) {
            run3DIteration(noise);
        }

        // Benchmark
        long totalTime = 0;
        for (int i = 0; i < BENCHMARK_ITERATIONS; i++) {
            long start = System.nanoTime();
            run3DIteration(noise);
            totalTime += System.nanoTime() - start;
        }

        double avgTimeMs = (totalTime / BENCHMARK_ITERATIONS) / 1_000_000.0;
        double pointsPerSecond = (POINTS_PER_ITERATION / avgTimeMs) * 1000.0;
        double millionPointsPerSecond = pointsPerSecond / 1_000_000.0;

        System.out.printf("  %-20s %12.2f%n", name, millionPointsPerSecond);
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Spatial Utility Benchmarks
    // ─────────────────────────────────────────────────────────────────────────

    private static void benchmarkChunkedNoise() {
        FastNoiseLite base = new FastNoiseLite(1337);
        base.SetNoiseType(NoiseType.OpenSimplex2);
        base.SetFrequency(0.01f);

        var chunked = new com.cognitivedynamics.noisegen.spatial.ChunkedNoise(base, 1000.0);

        // Warmup
        for (int i = 0; i < WARMUP_ITERATIONS; i++) {
            runChunkedIteration(chunked);
        }

        // Benchmark
        long totalTime = 0;
        for (int i = 0; i < BENCHMARK_ITERATIONS; i++) {
            long start = System.nanoTime();
            runChunkedIteration(chunked);
            totalTime += System.nanoTime() - start;
        }

        double avgTimeMs = (totalTime / BENCHMARK_ITERATIONS) / 1_000_000.0;
        double pointsPerSecond = (POINTS_PER_ITERATION / avgTimeMs) * 1000.0;
        double millionPointsPerSecond = pointsPerSecond / 1_000_000.0;

        System.out.printf("  %-20s %12.2f%n", "ChunkedNoise", millionPointsPerSecond);
    }

    private static float runChunkedIteration(com.cognitivedynamics.noisegen.spatial.ChunkedNoise noise) {
        float sum = 0;
        int idx = 0;
        double offset = 1_000_000_000.0; // Test at large coordinates
        for (int y = 0; y < GRID_SIZE && idx < POINTS_PER_ITERATION; y++) {
            for (int x = 0; x < GRID_SIZE && idx < POINTS_PER_ITERATION; x++) {
                sum += noise.getNoise(offset + x, offset + y);
                idx++;
            }
        }
        return sum;
    }

    private static void benchmarkDoublePrecisionNoise() {
        FastNoiseLite base = new FastNoiseLite(1337);
        base.SetNoiseType(NoiseType.OpenSimplex2);
        base.SetFrequency(0.01f);

        var precise = new com.cognitivedynamics.noisegen.spatial.DoublePrecisionNoise(base);

        // Warmup
        for (int i = 0; i < WARMUP_ITERATIONS; i++) {
            runDoublePrecisionIteration(precise);
        }

        // Benchmark
        long totalTime = 0;
        for (int i = 0; i < BENCHMARK_ITERATIONS; i++) {
            long start = System.nanoTime();
            runDoublePrecisionIteration(precise);
            totalTime += System.nanoTime() - start;
        }

        double avgTimeMs = (totalTime / BENCHMARK_ITERATIONS) / 1_000_000.0;
        double pointsPerSecond = (POINTS_PER_ITERATION / avgTimeMs) * 1000.0;
        double millionPointsPerSecond = pointsPerSecond / 1_000_000.0;

        System.out.printf("  %-20s %12.2f%n", "DoublePrecision", millionPointsPerSecond);
    }

    private static float runDoublePrecisionIteration(com.cognitivedynamics.noisegen.spatial.DoublePrecisionNoise noise) {
        float sum = 0;
        int idx = 0;
        double offset = 1_000_000_000_000.0; // Test at astronomical coordinates
        for (int y = 0; y < GRID_SIZE && idx < POINTS_PER_ITERATION; y++) {
            for (int x = 0; x < GRID_SIZE && idx < POINTS_PER_ITERATION; x++) {
                sum += noise.getNoise(offset + x, offset + y);
                idx++;
            }
        }
        return sum;
    }

    private static void benchmarkTiledNoise() {
        FastNoiseLite base = new FastNoiseLite(1337);
        base.SetNoiseType(NoiseType.OpenSimplex2);
        base.SetFrequency(0.01f);

        var tiled = new com.cognitivedynamics.noisegen.spatial.TiledNoise(base, 256, 256);

        // Warmup
        for (int i = 0; i < WARMUP_ITERATIONS; i++) {
            runTiledIteration(tiled);
        }

        // Benchmark
        long totalTime = 0;
        for (int i = 0; i < BENCHMARK_ITERATIONS; i++) {
            long start = System.nanoTime();
            runTiledIteration(tiled);
            totalTime += System.nanoTime() - start;
        }

        double avgTimeMs = (totalTime / BENCHMARK_ITERATIONS) / 1_000_000.0;
        double pointsPerSecond = (POINTS_PER_ITERATION / avgTimeMs) * 1000.0;
        double millionPointsPerSecond = pointsPerSecond / 1_000_000.0;

        System.out.printf("  %-20s %12.2f%n", "TiledNoise", millionPointsPerSecond);
    }

    private static float runTiledIteration(com.cognitivedynamics.noisegen.spatial.TiledNoise noise) {
        float sum = 0;
        int idx = 0;
        for (int y = 0; y < GRID_SIZE && idx < POINTS_PER_ITERATION; y++) {
            for (int x = 0; x < GRID_SIZE && idx < POINTS_PER_ITERATION; x++) {
                sum += noise.getNoise(x % 256, y % 256);
                idx++;
            }
        }
        return sum;
    }
}
