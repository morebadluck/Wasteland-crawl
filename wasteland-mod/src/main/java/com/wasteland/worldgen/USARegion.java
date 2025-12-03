package com.wasteland.worldgen;

import net.minecraft.world.level.biome.Biomes;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.biome.Biome;

/**
 * Defines the major regions of a stylized USA wasteland map.
 * Each region has characteristic biomes and terrain.
 *
 * World coordinates: Center (0, 0), extends to ±5000 blocks
 */
public enum USARegion {
    // Northeast - Appalachian forests and mountains
    NORTHEAST("Northeast", -2000, 2000, 2000, 5000,
            Biomes.FOREST, Biomes.TAIGA, Biomes.WINDSWEPT_HILLS),

    // Southeast - Swamps, forests, coastal
    SOUTHEAST("Southeast", -2000, 2000, -5000, 0,
            Biomes.SWAMP, Biomes.FOREST, Biomes.BEACH),

    // Midwest - Great Plains
    MIDWEST("Midwest", -1000, 1500, -1000, 3000,
            Biomes.PLAINS, Biomes.SUNFLOWER_PLAINS, Biomes.RIVER),

    // Southwest - Desert and mesa
    SOUTHWEST("Southwest", -5000, -1000, -3000, 1000,
            Biomes.DESERT, Biomes.BADLANDS, Biomes.ERODED_BADLANDS),

    // Rockies - Mountain range
    ROCKIES("Rockies", -3000, -1000, 0, 3000,
            Biomes.WINDSWEPT_HILLS, Biomes.WINDSWEPT_GRAVELLY_HILLS, Biomes.STONY_PEAKS),

    // West Coast - Mixed terrain
    WEST_COAST("West Coast", -5000, -3000, 1000, 5000,
            Biomes.FOREST, Biomes.BEACH, Biomes.WINDSWEPT_FOREST),

    // Northwest - Dense forests
    NORTHWEST("Northwest", -4000, -2000, 3000, 5000,
            Biomes.TAIGA, Biomes.OLD_GROWTH_PINE_TAIGA, Biomes.WINDSWEPT_FOREST),

    // Gulf Coast - Beaches and swamps
    GULF_COAST("Gulf Coast", -1000, 1000, -5000, -3000,
            Biomes.BEACH, Biomes.SWAMP, Biomes.MANGROVE_SWAMP),

    // Great Lakes - Large lakes region
    GREAT_LAKES("Great Lakes", 500, 2500, 1000, 3500,
            Biomes.RIVER, Biomes.BEACH, Biomes.FOREST),

    // Atlantic Coast
    ATLANTIC_COAST("Atlantic Coast", 1500, 2500, -2000, 4000,
            Biomes.BEACH, Biomes.OCEAN, Biomes.STONY_SHORE),

    // Pacific Coast
    PACIFIC_COAST("Pacific Coast", -5000, -4500, -2000, 4000,
            Biomes.BEACH, Biomes.OCEAN, Biomes.STONY_SHORE);

    private final String name;
    private final int minX, maxX, minZ, maxZ;
    private final ResourceKey<Biome> primaryBiome;
    private final ResourceKey<Biome> secondaryBiome;
    private final ResourceKey<Biome> tertiaryBiome;

    USARegion(String name, int minX, int maxX, int minZ, int maxZ,
              ResourceKey<Biome> primaryBiome,
              ResourceKey<Biome> secondaryBiome,
              ResourceKey<Biome> tertiaryBiome) {
        this.name = name;
        this.minX = minX;
        this.maxX = maxX;
        this.minZ = minZ;
        this.maxZ = maxZ;
        this.primaryBiome = primaryBiome;
        this.secondaryBiome = secondaryBiome;
        this.tertiaryBiome = tertiaryBiome;
    }

    public String getName() {
        return name;
    }

    /**
     * Check if a coordinate is within this region's bounds
     */
    public boolean contains(int x, int z) {
        return x >= minX && x <= maxX && z >= minZ && z <= maxZ;
    }

    /**
     * Get a biome for this region based on local noise
     * @param noise Value between 0.0 and 1.0
     */
    public ResourceKey<Biome> getBiome(double noise) {
        if (noise < 0.33) {
            return primaryBiome;
        } else if (noise < 0.66) {
            return secondaryBiome;
        } else {
            return tertiaryBiome;
        }
    }

    /**
     * Get the region for a given coordinate
     * Returns the first matching region, or MIDWEST as default
     */
    public static USARegion getRegion(int x, int z) {
        // Check coasts first (highest priority for ocean/beach placement)
        for (USARegion region : new USARegion[]{ATLANTIC_COAST, PACIFIC_COAST, GULF_COAST}) {
            if (region.contains(x, z)) {
                return region;
            }
        }

        // Check other regions
        for (USARegion region : values()) {
            if (region.contains(x, z)) {
                return region;
            }
        }

        // Default to plains if outside all regions
        return MIDWEST;
    }

    /**
     * Get center coordinates of this region
     */
    public int getCenterX() {
        return (minX + maxX) / 2;
    }

    public int getCenterZ() {
        return (minZ + maxZ) / 2;
    }

    /**
     * Check if this is a coastal region (good for Shoals dungeon)
     */
    public boolean isCoastal() {
        return this == ATLANTIC_COAST || this == PACIFIC_COAST ||
               this == GULF_COAST || this == SOUTHEAST || this == WEST_COAST;
    }

    /**
     * Check if this is a forest region (good for Lair dungeon)
     */
    public boolean isForest() {
        return this == NORTHEAST || this == NORTHWEST || this == WEST_COAST;
    }

    /**
     * Check if this is a desert region (good for Snake Pit, Tomb)
     */
    public boolean isDesert() {
        return this == SOUTHWEST;
    }

    /**
     * Check if this is a mountain region (good for Orc Mines)
     */
    public boolean isMountain() {
        return this == ROCKIES || this == NORTHEAST;
    }

    /**
     * Check if this is a swamp region (good for Swamp dungeon)
     */
    public boolean isSwamp() {
        return this == SOUTHEAST || this == GULF_COAST;
    }
}
