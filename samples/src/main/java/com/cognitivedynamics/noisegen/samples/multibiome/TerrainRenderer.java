package com.cognitivedynamics.noisegen.samples.multibiome;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;

/**
 * Utility class for rendering terrain heightmaps to JavaFX canvases.
 *
 * <p>Provides methods to render heightmaps with biome-based coloring,
 * elevation shading, and optional layer visualization.
 */
public class TerrainRenderer {

    private final MultiBiomeTerrain terrain;

    public TerrainRenderer(MultiBiomeTerrain terrain) {
        this.terrain = terrain;
    }

    /**
     * Render the terrain to a canvas with biome coloring.
     *
     * @param canvas  Target canvas
     * @param startX  World X coordinate of top-left corner
     * @param startY  World Y coordinate of top-left corner
     * @param scale   World units per pixel
     */
    public void renderBiome(Canvas canvas, double startX, double startY, double scale) {
        int width = (int) canvas.getWidth();
        int height = (int) canvas.getHeight();

        WritableImage image = new WritableImage(width, height);
        PixelWriter writer = image.getPixelWriter();

        for (int py = 0; py < height; py++) {
            double worldY = startY + py * scale;
            for (int px = 0; px < width; px++) {
                double worldX = startX + px * scale;
                double elevation = terrain.getHeight(worldX, worldY);
                BiomeType biome = BiomeType.fromElevation(elevation);
                Color color = biome.getShadedColor(elevation);
                writer.setColor(px, py, color);
            }
        }

        GraphicsContext gc = canvas.getGraphicsContext2D();
        gc.drawImage(image, 0, 0);
    }

    /**
     * Render only the continental layer for visualization.
     */
    public void renderContinental(Canvas canvas, double startX, double startY, double scale) {
        renderLayer(canvas, startX, startY, scale, LayerType.CONTINENTAL);
    }

    /**
     * Render only the mountain layer for visualization.
     */
    public void renderMountains(Canvas canvas, double startX, double startY, double scale) {
        renderLayer(canvas, startX, startY, scale, LayerType.MOUNTAINS);
    }

    /**
     * Render only the hills layer for visualization.
     */
    public void renderHills(Canvas canvas, double startX, double startY, double scale) {
        renderLayer(canvas, startX, startY, scale, LayerType.HILLS);
    }

    /**
     * Render only the detail layer for visualization.
     */
    public void renderDetail(Canvas canvas, double startX, double startY, double scale) {
        renderLayer(canvas, startX, startY, scale, LayerType.DETAIL);
    }

    /**
     * Render a grayscale heightmap.
     */
    public void renderGrayscale(Canvas canvas, double startX, double startY, double scale) {
        int width = (int) canvas.getWidth();
        int height = (int) canvas.getHeight();

        WritableImage image = new WritableImage(width, height);
        PixelWriter writer = image.getPixelWriter();

        for (int py = 0; py < height; py++) {
            double worldY = startY + py * scale;
            for (int px = 0; px < width; px++) {
                double worldX = startX + px * scale;
                double elevation = terrain.getHeight(worldX, worldY);
                // Map [-1, 1] to [0, 1]
                double normalized = (elevation + 1.0) * 0.5;
                normalized = Math.max(0, Math.min(1, normalized));
                Color color = Color.gray(normalized);
                writer.setColor(px, py, color);
            }
        }

        GraphicsContext gc = canvas.getGraphicsContext2D();
        gc.drawImage(image, 0, 0);
    }

    private void renderLayer(Canvas canvas, double startX, double startY, double scale, LayerType layer) {
        int width = (int) canvas.getWidth();
        int height = (int) canvas.getHeight();

        WritableImage image = new WritableImage(width, height);
        PixelWriter writer = image.getPixelWriter();

        for (int py = 0; py < height; py++) {
            double worldY = startY + py * scale;
            for (int px = 0; px < width; px++) {
                double worldX = startX + px * scale;
                double value = switch (layer) {
                    case CONTINENTAL -> terrain.getContinentalValue(worldX, worldY);
                    case MOUNTAINS -> terrain.getMountainValue(worldX, worldY);
                    case HILLS -> terrain.getHillsValue(worldX, worldY);
                    case DETAIL -> terrain.getDetailValue(worldX, worldY);
                };
                // Map [-1, 1] to [0, 1]
                double normalized = (value + 1.0) * 0.5;
                normalized = Math.max(0, Math.min(1, normalized));
                Color color = layer.colorize(normalized);
                writer.setColor(px, py, color);
            }
        }

        GraphicsContext gc = canvas.getGraphicsContext2D();
        gc.drawImage(image, 0, 0);
    }

    /**
     * Layer types for visualization with distinct color schemes.
     */
    public enum LayerType {
        CONTINENTAL {
            @Override
            Color colorize(double normalized) {
                // Blue to green gradient (water to land)
                return Color.color(
                    normalized * 0.2,
                    normalized * 0.8,
                    1.0 - normalized * 0.5
                );
            }
        },
        MOUNTAINS {
            @Override
            Color colorize(double normalized) {
                // Gray to white (rock to snow)
                return Color.gray(0.3 + normalized * 0.7);
            }
        },
        HILLS {
            @Override
            Color colorize(double normalized) {
                // Brown gradient
                return Color.color(
                    0.3 + normalized * 0.4,
                    0.2 + normalized * 0.3,
                    0.1 + normalized * 0.1
                );
            }
        },
        DETAIL {
            @Override
            Color colorize(double normalized) {
                // Purple gradient for detail visibility
                return Color.color(
                    0.3 + normalized * 0.5,
                    0.1 + normalized * 0.2,
                    0.4 + normalized * 0.5
                );
            }
        };

        abstract Color colorize(double normalized);
    }
}
