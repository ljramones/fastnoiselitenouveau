package com.cognitivedynamics.noisegen.samples.multibiome;

import javafx.scene.paint.Color;

/**
 * Defines the different biome types and their visual characteristics.
 * Each biome has a distinct color palette for rendering.
 */
public enum BiomeType {
    /**
     * Deep ocean - lowest elevations, dark blue
     */
    DEEP_OCEAN("Deep Ocean", -1.0, -0.6, Color.rgb(20, 40, 80)),

    /**
     * Shallow ocean - underwater but near coast
     */
    SHALLOW_OCEAN("Shallow Ocean", -0.6, -0.3, Color.rgb(40, 80, 140)),

    /**
     * Beach/coast - transition zone at water level
     */
    BEACH("Beach", -0.3, -0.1, Color.rgb(238, 214, 175)),

    /**
     * Plains/grassland - low elevation land
     */
    PLAINS("Plains", -0.1, 0.2, Color.rgb(86, 152, 72)),

    /**
     * Forest - mid elevation with trees
     */
    FOREST("Forest", 0.2, 0.4, Color.rgb(34, 100, 34)),

    /**
     * Hills/highlands - higher elevation rolling terrain
     */
    HILLS("Hills", 0.4, 0.6, Color.rgb(96, 108, 72)),

    /**
     * Mountains - high elevation rocky terrain
     */
    MOUNTAINS("Mountains", 0.6, 0.8, Color.rgb(128, 128, 128)),

    /**
     * Snow peaks - highest elevations with permanent snow
     */
    SNOW_PEAKS("Snow Peaks", 0.8, 1.0, Color.rgb(250, 250, 255));

    private final String displayName;
    private final double minElevation;
    private final double maxElevation;
    private final Color baseColor;

    BiomeType(String displayName, double minElevation, double maxElevation, Color baseColor) {
        this.displayName = displayName;
        this.minElevation = minElevation;
        this.maxElevation = maxElevation;
        this.baseColor = baseColor;
    }

    public String getDisplayName() {
        return displayName;
    }

    public double getMinElevation() {
        return minElevation;
    }

    public double getMaxElevation() {
        return maxElevation;
    }

    public Color getBaseColor() {
        return baseColor;
    }

    /**
     * Get the biome for a given elevation value.
     *
     * @param elevation Elevation value in range [-1, 1]
     * @return The corresponding biome type
     */
    public static BiomeType fromElevation(double elevation) {
        for (BiomeType biome : values()) {
            if (elevation >= biome.minElevation && elevation < biome.maxElevation) {
                return biome;
            }
        }
        // Handle edge case for exactly 1.0
        return elevation >= 0.8 ? SNOW_PEAKS : DEEP_OCEAN;
    }

    /**
     * Get a color with elevation-based shading within the biome.
     *
     * @param elevation Elevation value in range [-1, 1]
     * @return Shaded color based on elevation within biome
     */
    public Color getShadedColor(double elevation) {
        // Calculate position within biome range [0, 1]
        double range = maxElevation - minElevation;
        double position = (elevation - minElevation) / range;
        position = Math.max(0, Math.min(1, position));

        // Apply subtle shading (darker at bottom, lighter at top of range)
        double shadeFactor = 0.85 + (position * 0.3); // Range: 0.85 to 1.15

        return Color.rgb(
            clampColor(baseColor.getRed() * shadeFactor),
            clampColor(baseColor.getGreen() * shadeFactor),
            clampColor(baseColor.getBlue() * shadeFactor)
        );
    }

    private static int clampColor(double value) {
        return (int) Math.max(0, Math.min(255, value * 255));
    }
}
