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

        System.out.println();

        // Run advanced algorithm benchmarks (extensions)
        runAdvancedBenchmarks();

        System.out.println();

        // Run derivative benchmarks (extensions)
        runDerivativeBenchmarks();

        System.out.println();

        // Run domain warp benchmarks
        runDomainWarpBenchmarks();

        System.out.println();

        // Run node graph system benchmarks
        runGraphBenchmarks();

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
        benchmarkLODNoise();
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

    private static void benchmarkLODNoise() {
        FastNoiseLite base = new FastNoiseLite(1337);
        base.SetNoiseType(NoiseType.OpenSimplex2);
        base.SetFractalType(FractalType.FBm);
        base.SetFractalOctaves(8);
        base.SetFrequency(0.01f);

        var lod = new com.cognitivedynamics.noisegen.spatial.LODNoise(base, 8);

        // Warmup
        for (int i = 0; i < WARMUP_ITERATIONS; i++) {
            runLODIteration(lod);
        }

        // Benchmark
        long totalTime = 0;
        for (int i = 0; i < BENCHMARK_ITERATIONS; i++) {
            long start = System.nanoTime();
            runLODIteration(lod);
            totalTime += System.nanoTime() - start;
        }

        double avgTimeMs = (totalTime / BENCHMARK_ITERATIONS) / 1_000_000.0;
        double pointsPerSecond = (POINTS_PER_ITERATION / avgTimeMs) * 1000.0;
        double millionPointsPerSecond = pointsPerSecond / 1_000_000.0;

        System.out.printf("  %-20s %12.2f%n", "LODNoise", millionPointsPerSecond);
    }

    private static float runLODIteration(com.cognitivedynamics.noisegen.spatial.LODNoise noise) {
        float sum = 0;
        int idx = 0;
        for (int y = 0; y < GRID_SIZE && idx < POINTS_PER_ITERATION; y++) {
            for (int x = 0; x < GRID_SIZE && idx < POINTS_PER_ITERATION; x++) {
                // Simulate varying distance (near to far)
                float distance = (x + y) * 0.5f;
                sum += noise.getNoise(x, y, distance);
                idx++;
            }
        }
        return sum;
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Advanced Algorithm Benchmarks
    // ─────────────────────────────────────────────────────────────────────────

    private static void runAdvancedBenchmarks() {
        System.out.println("Advanced Algorithms Benchmarks [EXT] (million points/second):");
        System.out.println("───────────────────────────────────────────────────────────────────────");
        System.out.printf("  %-20s %12s%n", "Algorithm", "M points/s");
        System.out.println("  ────────────────────────────────────────────────────────────────────");

        benchmarkWaveletNoise();
        benchmarkSparseConvolution();
        benchmarkHierarchicalNoise();
        benchmarkTurbulenceCurl();
    }

    private static void benchmarkWaveletNoise() {
        var wavelet = new com.cognitivedynamics.noisegen.generators.WaveletNoiseGen(1337, 128);

        // Warmup
        for (int i = 0; i < WARMUP_ITERATIONS; i++) {
            runWaveletIteration(wavelet);
        }

        // Benchmark
        long totalTime = 0;
        for (int i = 0; i < BENCHMARK_ITERATIONS; i++) {
            long start = System.nanoTime();
            runWaveletIteration(wavelet);
            totalTime += System.nanoTime() - start;
        }

        double avgTimeMs = (totalTime / BENCHMARK_ITERATIONS) / 1_000_000.0;
        double pointsPerSecond = (POINTS_PER_ITERATION / avgTimeMs) * 1000.0;
        double millionPointsPerSecond = pointsPerSecond / 1_000_000.0;

        System.out.printf("  %-20s %12.2f%n", "WaveletNoise 2D", millionPointsPerSecond);
    }

    private static float runWaveletIteration(com.cognitivedynamics.noisegen.generators.WaveletNoiseGen noise) {
        float sum = 0;
        int idx = 0;
        for (int y = 0; y < GRID_SIZE && idx < POINTS_PER_ITERATION; y++) {
            for (int x = 0; x < GRID_SIZE && idx < POINTS_PER_ITERATION; x++) {
                sum += noise.sample2D(x * 0.01f, y * 0.01f);
                idx++;
            }
        }
        return sum;
    }

    private static void benchmarkSparseConvolution() {
        var sparse = new com.cognitivedynamics.noisegen.spatial.SparseConvolutionNoise(1337);

        // Warmup
        for (int i = 0; i < WARMUP_ITERATIONS; i++) {
            runSparseIteration(sparse);
        }

        // Benchmark
        long totalTime = 0;
        for (int i = 0; i < BENCHMARK_ITERATIONS; i++) {
            long start = System.nanoTime();
            runSparseIteration(sparse);
            totalTime += System.nanoTime() - start;
        }

        double avgTimeMs = (totalTime / BENCHMARK_ITERATIONS) / 1_000_000.0;
        double pointsPerSecond = (POINTS_PER_ITERATION / avgTimeMs) * 1000.0;
        double millionPointsPerSecond = pointsPerSecond / 1_000_000.0;

        System.out.printf("  %-20s %12.2f%n", "SparseConvolution", millionPointsPerSecond);
    }

    private static float runSparseIteration(com.cognitivedynamics.noisegen.spatial.SparseConvolutionNoise noise) {
        float sum = 0;
        int idx = 0;
        for (int y = 0; y < GRID_SIZE && idx < POINTS_PER_ITERATION; y++) {
            for (int x = 0; x < GRID_SIZE && idx < POINTS_PER_ITERATION; x++) {
                sum += noise.getNoise(x * 0.1f, y * 0.1f);
                idx++;
            }
        }
        return sum;
    }

    private static void benchmarkHierarchicalNoise() {
        FastNoiseLite base = new FastNoiseLite(1337);
        base.SetNoiseType(NoiseType.OpenSimplex2);
        base.SetFrequency(0.01f);

        var hierarchical = new com.cognitivedynamics.noisegen.spatial.HierarchicalNoise(base, 8);

        // Warmup
        for (int i = 0; i < WARMUP_ITERATIONS; i++) {
            runHierarchicalIteration(hierarchical);
        }

        // Benchmark
        long totalTime = 0;
        for (int i = 0; i < BENCHMARK_ITERATIONS; i++) {
            long start = System.nanoTime();
            runHierarchicalIteration(hierarchical);
            totalTime += System.nanoTime() - start;
        }

        double avgTimeMs = (totalTime / BENCHMARK_ITERATIONS) / 1_000_000.0;
        double pointsPerSecond = (POINTS_PER_ITERATION / avgTimeMs) * 1000.0;
        double millionPointsPerSecond = pointsPerSecond / 1_000_000.0;

        System.out.printf("  %-20s %12.2f%n", "HierarchicalNoise", millionPointsPerSecond);
    }

    private static float runHierarchicalIteration(com.cognitivedynamics.noisegen.spatial.HierarchicalNoise noise) {
        float sum = 0;
        int idx = 0;
        for (int y = 0; y < GRID_SIZE && idx < POINTS_PER_ITERATION; y++) {
            for (int x = 0; x < GRID_SIZE && idx < POINTS_PER_ITERATION; x++) {
                sum += noise.sampleCumulative(x, y, 4); // 4 levels
                idx++;
            }
        }
        return sum;
    }

    private static void benchmarkTurbulenceCurl() {
        FastNoiseLite base = new FastNoiseLite(1337);
        base.SetNoiseType(NoiseType.OpenSimplex2);
        base.SetFrequency(0.01f);

        var turbulence = new com.cognitivedynamics.noisegen.spatial.TurbulenceNoise(base, 0.01f);

        // Warmup
        for (int i = 0; i < WARMUP_ITERATIONS; i++) {
            runCurlIteration(turbulence);
        }

        // Benchmark
        long totalTime = 0;
        for (int i = 0; i < BENCHMARK_ITERATIONS; i++) {
            long start = System.nanoTime();
            runCurlIteration(turbulence);
            totalTime += System.nanoTime() - start;
        }

        double avgTimeMs = (totalTime / BENCHMARK_ITERATIONS) / 1_000_000.0;
        double pointsPerSecond = (POINTS_PER_ITERATION / avgTimeMs) * 1000.0;
        double millionPointsPerSecond = pointsPerSecond / 1_000_000.0;

        System.out.printf("  %-20s %12.2f%n", "Curl3D", millionPointsPerSecond);

        // Also benchmark curlFBm3D
        for (int i = 0; i < WARMUP_ITERATIONS; i++) {
            runCurlFBmIteration(turbulence);
        }

        totalTime = 0;
        for (int i = 0; i < BENCHMARK_ITERATIONS; i++) {
            long start = System.nanoTime();
            runCurlFBmIteration(turbulence);
            totalTime += System.nanoTime() - start;
        }

        avgTimeMs = (totalTime / BENCHMARK_ITERATIONS) / 1_000_000.0;
        pointsPerSecond = (POINTS_PER_ITERATION / avgTimeMs) * 1000.0;
        millionPointsPerSecond = pointsPerSecond / 1_000_000.0;

        System.out.printf("  %-20s %12.2f%n", "CurlFBm3D (4 oct)", millionPointsPerSecond);
    }

    private static float runCurlIteration(com.cognitivedynamics.noisegen.spatial.TurbulenceNoise turbulence) {
        float sum = 0;
        int idx = 0;
        for (int z = 0; z < GRID_SIZE_3D && idx < POINTS_PER_ITERATION; z++) {
            for (int y = 0; y < GRID_SIZE_3D && idx < POINTS_PER_ITERATION; y++) {
                for (int x = 0; x < GRID_SIZE_3D && idx < POINTS_PER_ITERATION; x++) {
                    float[] curl = turbulence.curl3D(x, y, z);
                    sum += curl[0] + curl[1] + curl[2];
                    idx++;
                }
            }
        }
        return sum;
    }

    private static float runCurlFBmIteration(com.cognitivedynamics.noisegen.spatial.TurbulenceNoise turbulence) {
        float sum = 0;
        int idx = 0;
        for (int z = 0; z < GRID_SIZE_3D && idx < POINTS_PER_ITERATION; z++) {
            for (int y = 0; y < GRID_SIZE_3D && idx < POINTS_PER_ITERATION; y++) {
                for (int x = 0; x < GRID_SIZE_3D && idx < POINTS_PER_ITERATION; x++) {
                    float[] curl = turbulence.curlFBm3D(x, y, z, 4);
                    sum += curl[0] + curl[1] + curl[2];
                    idx++;
                }
            }
        }
        return sum;
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Derivative Benchmarks
    // ─────────────────────────────────────────────────────────────────────────

    private static void runDerivativeBenchmarks() {
        System.out.println("Noise Derivatives Benchmarks [EXT] (million points/second):");
        System.out.println("───────────────────────────────────────────────────────────────────────");
        System.out.printf("  %-20s %12s%n", "Derivative Type", "M points/s");
        System.out.println("  ────────────────────────────────────────────────────────────────────");

        FastNoiseLite base = new FastNoiseLite(1337);
        base.SetNoiseType(NoiseType.OpenSimplex2);
        base.SetFrequency(0.01f);

        var derivatives = new com.cognitivedynamics.noisegen.derivatives.NoiseDerivatives(base);

        // Benchmark analytical derivatives
        derivatives.setUseAnalytical(true);

        // Warmup
        for (int i = 0; i < WARMUP_ITERATIONS; i++) {
            runDerivativeIteration(derivatives);
        }

        // Benchmark
        long totalTime = 0;
        for (int i = 0; i < BENCHMARK_ITERATIONS; i++) {
            long start = System.nanoTime();
            runDerivativeIteration(derivatives);
            totalTime += System.nanoTime() - start;
        }

        double avgTimeMs = (totalTime / BENCHMARK_ITERATIONS) / 1_000_000.0;
        double pointsPerSecond = (POINTS_PER_ITERATION / avgTimeMs) * 1000.0;
        double millionPointsPerSecond = pointsPerSecond / 1_000_000.0;

        System.out.printf("  %-20s %12.2f%n", "Analytical 2D", millionPointsPerSecond);

        // Benchmark numerical derivatives
        derivatives.setUseAnalytical(false);

        // Warmup
        for (int i = 0; i < WARMUP_ITERATIONS; i++) {
            runDerivativeIteration(derivatives);
        }

        // Benchmark
        totalTime = 0;
        for (int i = 0; i < BENCHMARK_ITERATIONS; i++) {
            long start = System.nanoTime();
            runDerivativeIteration(derivatives);
            totalTime += System.nanoTime() - start;
        }

        avgTimeMs = (totalTime / BENCHMARK_ITERATIONS) / 1_000_000.0;
        pointsPerSecond = (POINTS_PER_ITERATION / avgTimeMs) * 1000.0;
        millionPointsPerSecond = pointsPerSecond / 1_000_000.0;

        System.out.printf("  %-20s %12.2f%n", "Numerical 2D", millionPointsPerSecond);

        // 3D Derivatives
        derivatives.setUseAnalytical(true);

        // Warmup
        for (int i = 0; i < WARMUP_ITERATIONS; i++) {
            runDerivative3DIteration(derivatives);
        }

        // Benchmark
        totalTime = 0;
        for (int i = 0; i < BENCHMARK_ITERATIONS; i++) {
            long start = System.nanoTime();
            runDerivative3DIteration(derivatives);
            totalTime += System.nanoTime() - start;
        }

        avgTimeMs = (totalTime / BENCHMARK_ITERATIONS) / 1_000_000.0;
        pointsPerSecond = (POINTS_PER_ITERATION / avgTimeMs) * 1000.0;
        millionPointsPerSecond = pointsPerSecond / 1_000_000.0;

        System.out.printf("  %-20s %12.2f%n", "Analytical 3D", millionPointsPerSecond);

        // Numerical 3D
        derivatives.setUseAnalytical(false);

        for (int i = 0; i < WARMUP_ITERATIONS; i++) {
            runDerivative3DIteration(derivatives);
        }

        totalTime = 0;
        for (int i = 0; i < BENCHMARK_ITERATIONS; i++) {
            long start = System.nanoTime();
            runDerivative3DIteration(derivatives);
            totalTime += System.nanoTime() - start;
        }

        avgTimeMs = (totalTime / BENCHMARK_ITERATIONS) / 1_000_000.0;
        pointsPerSecond = (POINTS_PER_ITERATION / avgTimeMs) * 1000.0;
        millionPointsPerSecond = pointsPerSecond / 1_000_000.0;

        System.out.printf("  %-20s %12.2f%n", "Numerical 3D", millionPointsPerSecond);
    }

    private static float runDerivativeIteration(com.cognitivedynamics.noisegen.derivatives.NoiseDerivatives derivatives) {
        float sum = 0;
        int idx = 0;
        for (int y = 0; y < GRID_SIZE && idx < POINTS_PER_ITERATION; y++) {
            for (int x = 0; x < GRID_SIZE && idx < POINTS_PER_ITERATION; x++) {
                var result = derivatives.getNoiseWithGradient2D(x, y);
                sum += result.value + result.dx + result.dy;
                idx++;
            }
        }
        return sum;
    }

    private static float runDerivative3DIteration(com.cognitivedynamics.noisegen.derivatives.NoiseDerivatives derivatives) {
        float sum = 0;
        int idx = 0;
        for (int z = 0; z < GRID_SIZE_3D && idx < POINTS_PER_ITERATION; z++) {
            for (int y = 0; y < GRID_SIZE_3D && idx < POINTS_PER_ITERATION; y++) {
                for (int x = 0; x < GRID_SIZE_3D && idx < POINTS_PER_ITERATION; x++) {
                    var result = derivatives.getNoiseWithGradient3D(x, y, z);
                    sum += result.value + result.dx + result.dy + result.dz;
                    idx++;
                }
            }
        }
        return sum;
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Domain Warp Benchmarks
    // ─────────────────────────────────────────────────────────────────────────

    private static void runDomainWarpBenchmarks() {
        System.out.println("Domain Warp Benchmarks (million points/second):");
        System.out.println("───────────────────────────────────────────────────────────────────────");
        System.out.printf("  %-20s %12s%n", "Warp Type", "M points/s");
        System.out.println("  ────────────────────────────────────────────────────────────────────");

        // 2D Domain Warp using FastNoiseLite built-in
        FastNoiseLite noise2D = new FastNoiseLite(1337);
        noise2D.SetDomainWarpType(FastNoiseLite.DomainWarpType.OpenSimplex2);
        noise2D.SetDomainWarpAmp(30f);
        noise2D.SetFrequency(0.01f);

        // Warmup
        for (int i = 0; i < WARMUP_ITERATIONS; i++) {
            runDomainWarp2DIteration(noise2D);
        }

        // Benchmark
        long totalTime = 0;
        for (int i = 0; i < BENCHMARK_ITERATIONS; i++) {
            long start = System.nanoTime();
            runDomainWarp2DIteration(noise2D);
            totalTime += System.nanoTime() - start;
        }

        double avgTimeMs = (totalTime / BENCHMARK_ITERATIONS) / 1_000_000.0;
        double pointsPerSecond = (POINTS_PER_ITERATION / avgTimeMs) * 1000.0;
        double millionPointsPerSecond = pointsPerSecond / 1_000_000.0;

        System.out.printf("  %-20s %12.2f%n", "OpenSimplex2 2D", millionPointsPerSecond);

        // 3D Domain Warp
        FastNoiseLite noise3D = new FastNoiseLite(1337);
        noise3D.SetDomainWarpType(FastNoiseLite.DomainWarpType.OpenSimplex2);
        noise3D.SetDomainWarpAmp(30f);
        noise3D.SetFrequency(0.01f);

        // Warmup
        for (int i = 0; i < WARMUP_ITERATIONS; i++) {
            runDomainWarp3DIteration(noise3D);
        }

        // Benchmark
        totalTime = 0;
        for (int i = 0; i < BENCHMARK_ITERATIONS; i++) {
            long start = System.nanoTime();
            runDomainWarp3DIteration(noise3D);
            totalTime += System.nanoTime() - start;
        }

        avgTimeMs = (totalTime / BENCHMARK_ITERATIONS) / 1_000_000.0;
        pointsPerSecond = (POINTS_PER_ITERATION / avgTimeMs) * 1000.0;
        millionPointsPerSecond = pointsPerSecond / 1_000_000.0;

        System.out.printf("  %-20s %12.2f%n", "OpenSimplex2 3D", millionPointsPerSecond);

        // Fractal Domain Warp 2D
        FastNoiseLite noiseFractal = new FastNoiseLite(1337);
        noiseFractal.SetDomainWarpType(FastNoiseLite.DomainWarpType.OpenSimplex2);
        noiseFractal.SetFractalType(FractalType.DomainWarpProgressive);
        noiseFractal.SetFractalOctaves(4);
        noiseFractal.SetDomainWarpAmp(30f);
        noiseFractal.SetFrequency(0.01f);

        // Warmup
        for (int i = 0; i < WARMUP_ITERATIONS; i++) {
            runDomainWarp2DIteration(noiseFractal);
        }

        // Benchmark
        totalTime = 0;
        for (int i = 0; i < BENCHMARK_ITERATIONS; i++) {
            long start = System.nanoTime();
            runDomainWarp2DIteration(noiseFractal);
            totalTime += System.nanoTime() - start;
        }

        avgTimeMs = (totalTime / BENCHMARK_ITERATIONS) / 1_000_000.0;
        pointsPerSecond = (POINTS_PER_ITERATION / avgTimeMs) * 1000.0;
        millionPointsPerSecond = pointsPerSecond / 1_000_000.0;

        System.out.printf("  %-20s %12.2f%n", "Progressive 2D", millionPointsPerSecond);
    }

    private static float runDomainWarp2DIteration(FastNoiseLite noise) {
        float sum = 0;
        int idx = 0;
        for (int y = 0; y < GRID_SIZE && idx < POINTS_PER_ITERATION; y++) {
            for (int x = 0; x < GRID_SIZE && idx < POINTS_PER_ITERATION; x++) {
                com.cognitivedynamics.noisegen.Vector2 coord = new com.cognitivedynamics.noisegen.Vector2(x, y);
                noise.DomainWarp(coord);
                sum += coord.x + coord.y;
                idx++;
            }
        }
        return sum;
    }

    private static float runDomainWarp3DIteration(FastNoiseLite noise) {
        float sum = 0;
        int idx = 0;
        for (int z = 0; z < GRID_SIZE_3D && idx < POINTS_PER_ITERATION; z++) {
            for (int y = 0; y < GRID_SIZE_3D && idx < POINTS_PER_ITERATION; y++) {
                for (int x = 0; x < GRID_SIZE_3D && idx < POINTS_PER_ITERATION; x++) {
                    com.cognitivedynamics.noisegen.Vector3 coord = new com.cognitivedynamics.noisegen.Vector3(x, y, z);
                    noise.DomainWarp(coord);
                    sum += coord.x + coord.y + coord.z;
                    idx++;
                }
            }
        }
        return sum;
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Node Graph System Benchmarks
    // ─────────────────────────────────────────────────────────────────────────

    private static void runGraphBenchmarks() {
        System.out.println("Node Graph System Benchmarks [EXT] (million points/second):");
        System.out.println("═══════════════════════════════════════════════════════════════════════");

        runGraphSourceBenchmarks();
        System.out.println();
        runGraphFractalBenchmarks();
        System.out.println();
        runGraphCombinerBenchmarks();
        System.out.println();
        runGraphComplexBenchmarks();
        System.out.println();
        runBulkEvaluatorBenchmarks();
    }

    private static void runGraphSourceBenchmarks() {
        System.out.println("  Source Nodes - 2D (million points/second):");
        System.out.println("  ───────────────────────────────────────────────────────────────────");
        System.out.printf("    %-24s %12s%n", "Node Type", "M points/s");
        System.out.println("    ────────────────────────────────────────");

        var graph = com.cognitivedynamics.noisegen.graph.NoiseGraph.create(1337);

        // Simplex
        benchmarkGraphNode2D("Simplex", graph.simplex().frequency(0.01));

        // Perlin
        benchmarkGraphNode2D("Perlin", graph.perlin().frequency(0.01));

        // Value
        benchmarkGraphNode2D("Value", graph.value().frequency(0.01));

        // ValueCubic
        benchmarkGraphNode2D("ValueCubic", graph.valueCubic().frequency(0.01));

        // Cellular
        benchmarkGraphNode2D("Cellular", graph.cellular().frequency(0.01));

        // Constant
        benchmarkGraphNode2D("Constant", graph.constant(0.5));

        System.out.println();
        System.out.println("  Source Nodes - 3D (million points/second):");
        System.out.println("  ───────────────────────────────────────────────────────────────────");
        System.out.printf("    %-24s %12s%n", "Node Type", "M points/s");
        System.out.println("    ────────────────────────────────────────");

        // 3D benchmarks
        benchmarkGraphNode3D("Simplex", graph.simplex().frequency(0.01));
        benchmarkGraphNode3D("Perlin", graph.perlin().frequency(0.01));
        benchmarkGraphNode3D("Cellular", graph.cellular().frequency(0.01));
    }

    private static void runGraphFractalBenchmarks() {
        System.out.println("  Fractal Nodes - 3D with 4 octaves (million points/second):");
        System.out.println("  ───────────────────────────────────────────────────────────────────");
        System.out.printf("    %-24s %12s%n", "Fractal Type", "M points/s");
        System.out.println("    ────────────────────────────────────────");

        var graph = com.cognitivedynamics.noisegen.graph.NoiseGraph.create(1337);
        var source = graph.simplex().frequency(0.01);

        benchmarkGraphNode3D("FBm (4 oct)", graph.fbm(source, 4));
        benchmarkGraphNode3D("Ridged (4 oct)", graph.ridged(source, 4));
        benchmarkGraphNode3D("Billow (4 oct)", graph.billow(source, 4));
        benchmarkGraphNode3D("HybridMulti (4 oct)", graph.hybridMulti(source, 4));
    }

    private static void runGraphCombinerBenchmarks() {
        System.out.println("  Combiner & Modifier Nodes - 2D (million points/second):");
        System.out.println("  ───────────────────────────────────────────────────────────────────");
        System.out.printf("    %-24s %12s%n", "Operation", "M points/s");
        System.out.println("    ────────────────────────────────────────");

        var graph = com.cognitivedynamics.noisegen.graph.NoiseGraph.create(1337);
        var nodeA = graph.simplex().frequency(0.01);
        var nodeB = graph.perlin().frequency(0.02);

        // Combiners
        benchmarkGraphNode2D("Add (2 nodes)", nodeA.add(nodeB));
        benchmarkGraphNode2D("Multiply (2 nodes)", nodeA.multiply(nodeB));
        benchmarkGraphNode2D("Min (2 nodes)", nodeA.min(nodeB));
        benchmarkGraphNode2D("Max (2 nodes)", nodeA.max(nodeB));
        benchmarkGraphNode2D("Blend (3 nodes)", graph.blend(nodeA, nodeB, graph.simplex().frequency(0.005)));

        // Modifiers
        benchmarkGraphNode2D("Scale (domain)", nodeA.scale(2.0));
        benchmarkGraphNode2D("Multiply (constant)", nodeA.multiply(0.5));
        benchmarkGraphNode2D("Add (constant)", nodeA.add(0.3));
        benchmarkGraphNode2D("Clamp", nodeA.clamp(-0.5, 0.5));
        benchmarkGraphNode2D("Abs", nodeA.abs());
        benchmarkGraphNode2D("Invert", nodeA.invert());
    }

    private static void runGraphComplexBenchmarks() {
        System.out.println("  Complex Graph Configurations - 2D (million points/second):");
        System.out.println("  ───────────────────────────────────────────────────────────────────");
        System.out.printf("    %-24s %12s%n", "Configuration", "M points/s");
        System.out.println("    ────────────────────────────────────────");

        var graph = com.cognitivedynamics.noisegen.graph.NoiseGraph.create(1337);

        // Simple terrain: FBm + Ridged
        var simpleTerrain = graph.fbm(graph.simplex().frequency(0.01), 4)
            .add(graph.ridged(graph.simplex().frequency(0.02), 3).multiply(0.3));
        benchmarkGraphNode2D("Simple Terrain", simpleTerrain);

        // Domain warped
        var warpSource = graph.simplex().frequency(0.005);
        var warped = graph.fbm(graph.simplex().frequency(0.01), 4)
            .warp(warpSource, 30.0);
        benchmarkGraphNode2D("Domain Warped FBm", warped);

        // Multi-layer terrain (like MultiBiomeTerrain sample)
        var continental = graph.fbm(graph.simplex().frequency(0.002), 3)
            .warp(graph.simplex().frequency(0.001), 50.0);
        var mountains = graph.ridged(graph.simplex().frequency(0.008), 4)
            .multiply(0.5);
        var hills = graph.fbm(graph.simplex().frequency(0.02), 3)
            .multiply(0.3);
        var detail = graph.fbm(graph.simplex().frequency(0.1), 2)
            .multiply(0.1);
        var fullTerrain = continental.add(mountains).add(hills).add(detail).clamp(-1.0, 1.0);
        benchmarkGraphNode2D("Multi-Layer Terrain", fullTerrain);

        // Cave-like configuration
        var caverns = graph.cellular(
                com.cognitivedynamics.noisegen.NoiseTypes.CellularDistanceFunction.EuclideanSq,
                com.cognitivedynamics.noisegen.NoiseTypes.CellularReturnType.Distance2Sub,
                0.8
            ).frequency(0.02).invert().add(0.3);
        var tunnels = graph.ridged(graph.simplex().frequency(0.03), 4).invert().add(0.5);
        var caves = caverns.min(tunnels);
        benchmarkGraphNode3D("Cave System (3D)", caves);
    }

    private static void runBulkEvaluatorBenchmarks() {
        System.out.println("  BulkEvaluator Performance (million points/second):");
        System.out.println("  ───────────────────────────────────────────────────────────────────");
        System.out.printf("    %-24s %12s%n", "Method", "M points/s");
        System.out.println("    ────────────────────────────────────────");

        var graph = com.cognitivedynamics.noisegen.graph.NoiseGraph.create(1337);
        var node = graph.fbm(graph.simplex().frequency(0.01), 4);
        var bulk = new com.cognitivedynamics.noisegen.graph.util.BulkEvaluator(1337);

        // 2D fill
        int size2D = 3163; // ~10M points
        benchmarkBulkFill2D("fill2D (double[][])", bulk, node, size2D);
        benchmarkBulkFill2DFlat("fill2DFlat (double[])", bulk, node, size2D);
        benchmarkBulkFill2DFloat("fill2DFloat (float[][])", bulk, node, size2D);

        // 3D fill
        benchmarkBulkFill3D("fill3D (double[][][])", bulk, node, GRID_SIZE_3D);
    }

    private static void benchmarkGraphNode2D(String name, com.cognitivedynamics.noisegen.graph.NoiseNode node) {
        int seed = 1337;

        // Warmup
        for (int i = 0; i < WARMUP_ITERATIONS; i++) {
            runGraphNode2DIteration(node, seed);
        }

        // Benchmark
        long totalTime = 0;
        for (int i = 0; i < BENCHMARK_ITERATIONS; i++) {
            long start = System.nanoTime();
            runGraphNode2DIteration(node, seed);
            totalTime += System.nanoTime() - start;
        }

        double avgTimeMs = (totalTime / BENCHMARK_ITERATIONS) / 1_000_000.0;
        double pointsPerSecond = (POINTS_PER_ITERATION / avgTimeMs) * 1000.0;
        double millionPointsPerSecond = pointsPerSecond / 1_000_000.0;

        System.out.printf("    %-24s %12.2f%n", name, millionPointsPerSecond);
    }

    private static double runGraphNode2DIteration(com.cognitivedynamics.noisegen.graph.NoiseNode node, int seed) {
        double sum = 0;
        int idx = 0;
        for (int y = 0; y < GRID_SIZE && idx < POINTS_PER_ITERATION; y++) {
            for (int x = 0; x < GRID_SIZE && idx < POINTS_PER_ITERATION; x++) {
                sum += node.evaluate2D(seed, x, y);
                idx++;
            }
        }
        return sum;
    }

    private static void benchmarkGraphNode3D(String name, com.cognitivedynamics.noisegen.graph.NoiseNode node) {
        int seed = 1337;

        // Warmup
        for (int i = 0; i < WARMUP_ITERATIONS; i++) {
            runGraphNode3DIteration(node, seed);
        }

        // Benchmark
        long totalTime = 0;
        for (int i = 0; i < BENCHMARK_ITERATIONS; i++) {
            long start = System.nanoTime();
            runGraphNode3DIteration(node, seed);
            totalTime += System.nanoTime() - start;
        }

        double avgTimeMs = (totalTime / BENCHMARK_ITERATIONS) / 1_000_000.0;
        double pointsPerSecond = (POINTS_PER_ITERATION / avgTimeMs) * 1000.0;
        double millionPointsPerSecond = pointsPerSecond / 1_000_000.0;

        System.out.printf("    %-24s %12.2f%n", name, millionPointsPerSecond);
    }

    private static double runGraphNode3DIteration(com.cognitivedynamics.noisegen.graph.NoiseNode node, int seed) {
        double sum = 0;
        int idx = 0;
        for (int z = 0; z < GRID_SIZE_3D && idx < POINTS_PER_ITERATION; z++) {
            for (int y = 0; y < GRID_SIZE_3D && idx < POINTS_PER_ITERATION; y++) {
                for (int x = 0; x < GRID_SIZE_3D && idx < POINTS_PER_ITERATION; x++) {
                    sum += node.evaluate3D(seed, x, y, z);
                    idx++;
                }
            }
        }
        return sum;
    }

    private static void benchmarkBulkFill2D(String name,
            com.cognitivedynamics.noisegen.graph.util.BulkEvaluator bulk,
            com.cognitivedynamics.noisegen.graph.NoiseNode node, int size) {

        // Warmup
        for (int i = 0; i < WARMUP_ITERATIONS; i++) {
            bulk.fill2D(node, size, size, 0, 0, 1.0);
        }

        // Benchmark
        long totalTime = 0;
        int totalPoints = size * size;
        for (int i = 0; i < BENCHMARK_ITERATIONS; i++) {
            long start = System.nanoTime();
            bulk.fill2D(node, size, size, 0, 0, 1.0);
            totalTime += System.nanoTime() - start;
        }

        double avgTimeMs = (totalTime / BENCHMARK_ITERATIONS) / 1_000_000.0;
        double pointsPerSecond = (totalPoints / avgTimeMs) * 1000.0;
        double millionPointsPerSecond = pointsPerSecond / 1_000_000.0;

        System.out.printf("    %-24s %12.2f%n", name, millionPointsPerSecond);
    }

    private static void benchmarkBulkFill2DFlat(String name,
            com.cognitivedynamics.noisegen.graph.util.BulkEvaluator bulk,
            com.cognitivedynamics.noisegen.graph.NoiseNode node, int size) {

        // Warmup
        for (int i = 0; i < WARMUP_ITERATIONS; i++) {
            bulk.fill2DFlat(node, size, size, 0, 0, 1.0);
        }

        // Benchmark
        long totalTime = 0;
        int totalPoints = size * size;
        for (int i = 0; i < BENCHMARK_ITERATIONS; i++) {
            long start = System.nanoTime();
            bulk.fill2DFlat(node, size, size, 0, 0, 1.0);
            totalTime += System.nanoTime() - start;
        }

        double avgTimeMs = (totalTime / BENCHMARK_ITERATIONS) / 1_000_000.0;
        double pointsPerSecond = (totalPoints / avgTimeMs) * 1000.0;
        double millionPointsPerSecond = pointsPerSecond / 1_000_000.0;

        System.out.printf("    %-24s %12.2f%n", name, millionPointsPerSecond);
    }

    private static void benchmarkBulkFill2DFloat(String name,
            com.cognitivedynamics.noisegen.graph.util.BulkEvaluator bulk,
            com.cognitivedynamics.noisegen.graph.NoiseNode node, int size) {

        // Warmup
        for (int i = 0; i < WARMUP_ITERATIONS; i++) {
            bulk.fill2DFloat(node, size, size, 0, 0, 1.0);
        }

        // Benchmark
        long totalTime = 0;
        int totalPoints = size * size;
        for (int i = 0; i < BENCHMARK_ITERATIONS; i++) {
            long start = System.nanoTime();
            bulk.fill2DFloat(node, size, size, 0, 0, 1.0);
            totalTime += System.nanoTime() - start;
        }

        double avgTimeMs = (totalTime / BENCHMARK_ITERATIONS) / 1_000_000.0;
        double pointsPerSecond = (totalPoints / avgTimeMs) * 1000.0;
        double millionPointsPerSecond = pointsPerSecond / 1_000_000.0;

        System.out.printf("    %-24s %12.2f%n", name, millionPointsPerSecond);
    }

    private static void benchmarkBulkFill3D(String name,
            com.cognitivedynamics.noisegen.graph.util.BulkEvaluator bulk,
            com.cognitivedynamics.noisegen.graph.NoiseNode node, int size) {

        // Warmup
        for (int i = 0; i < WARMUP_ITERATIONS; i++) {
            bulk.fill3D(node, size, size, size, 0, 0, 0, 1.0);
        }

        // Benchmark
        long totalTime = 0;
        int totalPoints = size * size * size;
        for (int i = 0; i < BENCHMARK_ITERATIONS; i++) {
            long start = System.nanoTime();
            bulk.fill3D(node, size, size, size, 0, 0, 0, 1.0);
            totalTime += System.nanoTime() - start;
        }

        double avgTimeMs = (totalTime / BENCHMARK_ITERATIONS) / 1_000_000.0;
        double pointsPerSecond = (totalPoints / avgTimeMs) * 1000.0;
        double millionPointsPerSecond = pointsPerSecond / 1_000_000.0;

        System.out.printf("    %-24s %12.2f%n", name, millionPointsPerSecond);
    }
}
