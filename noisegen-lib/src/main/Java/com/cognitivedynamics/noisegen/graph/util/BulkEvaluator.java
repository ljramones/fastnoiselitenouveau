package com.cognitivedynamics.noisegen.graph.util;

import com.cognitivedynamics.noisegen.graph.NoiseNode;

/**
 * Utility class for bulk evaluation of noise nodes.
 *
 * <p>Provides efficient methods to fill arrays with noise values, useful for
 * generating heightmaps, textures, and other grid-based data.
 *
 * <p>This class is thread-safe. The same evaluator can be used from multiple
 * threads simultaneously.
 */
public final class BulkEvaluator {

    private final int seed;

    /**
     * Create a bulk evaluator with the specified seed.
     *
     * @param seed The seed for noise evaluation
     */
    public BulkEvaluator(int seed) {
        this.seed = seed;
    }

    /**
     * Get the seed.
     *
     * @return The seed
     */
    public int getSeed() {
        return seed;
    }

    /**
     * Fill a 2D array with noise values.
     *
     * @param node The noise node to evaluate
     * @param width Width of the array
     * @param height Height of the array
     * @param startX Starting X coordinate in noise space
     * @param startY Starting Y coordinate in noise space
     * @param step Step size between samples in noise space
     * @return A 2D array of noise values [y][x]
     */
    public double[][] fill2D(NoiseNode node, int width, int height,
                             double startX, double startY, double step) {
        double[][] result = new double[height][width];

        for (int y = 0; y < height; y++) {
            double ny = startY + y * step;
            for (int x = 0; x < width; x++) {
                double nx = startX + x * step;
                result[y][x] = node.evaluate2D(seed, nx, ny);
            }
        }

        return result;
    }

    /**
     * Fill a 2D array with noise values using specified coordinate ranges.
     *
     * @param node The noise node to evaluate
     * @param width Width of the array
     * @param height Height of the array
     * @param minX Minimum X coordinate in noise space
     * @param minY Minimum Y coordinate in noise space
     * @param maxX Maximum X coordinate in noise space
     * @param maxY Maximum Y coordinate in noise space
     * @return A 2D array of noise values [y][x]
     */
    public double[][] fill2DRange(NoiseNode node, int width, int height,
                                   double minX, double minY, double maxX, double maxY) {
        double stepX = (maxX - minX) / (width - 1);
        double stepY = (maxY - minY) / (height - 1);

        double[][] result = new double[height][width];

        for (int y = 0; y < height; y++) {
            double ny = minY + y * stepY;
            for (int x = 0; x < width; x++) {
                double nx = minX + x * stepX;
                result[y][x] = node.evaluate2D(seed, nx, ny);
            }
        }

        return result;
    }

    /**
     * Fill a 1D array with noise values for a 2D grid (row-major order).
     *
     * @param node The noise node to evaluate
     * @param width Width of the grid
     * @param height Height of the grid
     * @param startX Starting X coordinate in noise space
     * @param startY Starting Y coordinate in noise space
     * @param step Step size between samples
     * @return A 1D array of noise values in row-major order
     */
    public double[] fill2DFlat(NoiseNode node, int width, int height,
                               double startX, double startY, double step) {
        double[] result = new double[width * height];

        for (int y = 0; y < height; y++) {
            double ny = startY + y * step;
            int rowOffset = y * width;
            for (int x = 0; x < width; x++) {
                double nx = startX + x * step;
                result[rowOffset + x] = node.evaluate2D(seed, nx, ny);
            }
        }

        return result;
    }

    /**
     * Fill a 3D array with noise values.
     *
     * @param node The noise node to evaluate
     * @param width Width (X dimension)
     * @param height Height (Y dimension)
     * @param depth Depth (Z dimension)
     * @param startX Starting X coordinate
     * @param startY Starting Y coordinate
     * @param startZ Starting Z coordinate
     * @param step Step size between samples
     * @return A 3D array of noise values [z][y][x]
     */
    public double[][][] fill3D(NoiseNode node, int width, int height, int depth,
                               double startX, double startY, double startZ, double step) {
        double[][][] result = new double[depth][height][width];

        for (int z = 0; z < depth; z++) {
            double nz = startZ + z * step;
            for (int y = 0; y < height; y++) {
                double ny = startY + y * step;
                for (int x = 0; x < width; x++) {
                    double nx = startX + x * step;
                    result[z][y][x] = node.evaluate3D(seed, nx, ny, nz);
                }
            }
        }

        return result;
    }

    /**
     * Fill a 1D array with noise values for a 3D grid.
     *
     * @param node The noise node to evaluate
     * @param width Width (X dimension)
     * @param height Height (Y dimension)
     * @param depth Depth (Z dimension)
     * @param startX Starting X coordinate
     * @param startY Starting Y coordinate
     * @param startZ Starting Z coordinate
     * @param step Step size between samples
     * @return A 1D array of noise values (z * height * width + y * width + x)
     */
    public double[] fill3DFlat(NoiseNode node, int width, int height, int depth,
                               double startX, double startY, double startZ, double step) {
        double[] result = new double[width * height * depth];

        for (int z = 0; z < depth; z++) {
            double nz = startZ + z * step;
            int sliceOffset = z * height * width;
            for (int y = 0; y < height; y++) {
                double ny = startY + y * step;
                int rowOffset = y * width;
                for (int x = 0; x < width; x++) {
                    double nx = startX + x * step;
                    result[sliceOffset + rowOffset + x] = node.evaluate3D(seed, nx, ny, nz);
                }
            }
        }

        return result;
    }

    /**
     * Fill a 2D float array with noise values.
     * Convenience method for applications that use float arrays.
     *
     * @param node The noise node to evaluate
     * @param width Width of the array
     * @param height Height of the array
     * @param startX Starting X coordinate
     * @param startY Starting Y coordinate
     * @param step Step size between samples
     * @return A 2D float array of noise values [y][x]
     */
    public float[][] fill2DFloat(NoiseNode node, int width, int height,
                                 double startX, double startY, double step) {
        float[][] result = new float[height][width];

        for (int y = 0; y < height; y++) {
            double ny = startY + y * step;
            for (int x = 0; x < width; x++) {
                double nx = startX + x * step;
                result[y][x] = (float) node.evaluate2D(seed, nx, ny);
            }
        }

        return result;
    }

    /**
     * Evaluate a single line of 2D noise.
     *
     * @param node The noise node to evaluate
     * @param length Number of samples
     * @param startX Starting X coordinate
     * @param startY Starting Y coordinate
     * @param stepX X step per sample
     * @param stepY Y step per sample
     * @return Array of noise values
     */
    public double[] fillLine2D(NoiseNode node, int length,
                               double startX, double startY,
                               double stepX, double stepY) {
        double[] result = new double[length];

        for (int i = 0; i < length; i++) {
            double x = startX + i * stepX;
            double y = startY + i * stepY;
            result[i] = node.evaluate2D(seed, x, y);
        }

        return result;
    }
}
