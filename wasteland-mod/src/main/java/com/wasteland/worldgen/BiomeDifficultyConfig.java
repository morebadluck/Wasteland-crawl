package com.wasteland.worldgen;

import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;

import java.util.HashMap;
import java.util.Map;

/**
 * Defines base difficulty levels for each biome type.
 * These are modified by noise and distance to create organic difficulty distribution.
 */
public class BiomeDifficultyConfig {

    private static final Map<ResourceKey<Biome>, Integer> BIOME_BASE_LEVELS = new HashMap<>();

    static {
        // SAFE BIOMES (1-3) - Starting areas
        register(Biomes.PLAINS, 2);
        register(Biomes.SUNFLOWER_PLAINS, 2);
        register(Biomes.MEADOW, 2);
        register(Biomes.FOREST, 3);
        register(Biomes.FLOWER_FOREST, 2);
        register(Biomes.BIRCH_FOREST, 3);
        register(Biomes.CHERRY_GROVE, 2);

        // MODERATE BIOMES (3-6) - Early exploration
        register(Biomes.TAIGA, 4);
        register(Biomes.SNOWY_PLAINS, 4);
        register(Biomes.SNOWY_TAIGA, 5);
        register(Biomes.SAVANNA, 4);
        register(Biomes.SAVANNA_PLATEAU, 5);
        register(Biomes.RIVER, 3);
        register(Biomes.BEACH, 3);
        register(Biomes.STONY_SHORE, 5);

        // DANGEROUS BIOMES (5-10) - Mid-game
        register(Biomes.DESERT, 6);
        register(Biomes.SWAMP, 7);
        register(Biomes.MANGROVE_SWAMP, 8);
        register(Biomes.JUNGLE, 7);
        register(Biomes.SPARSE_JUNGLE, 6);
        register(Biomes.BAMBOO_JUNGLE, 7);
        register(Biomes.BADLANDS, 8);
        register(Biomes.WOODED_BADLANDS, 7);
        register(Biomes.ERODED_BADLANDS, 9);
        register(Biomes.OLD_GROWTH_BIRCH_FOREST, 6);
        register(Biomes.OLD_GROWTH_SPRUCE_TAIGA, 7);
        register(Biomes.OLD_GROWTH_PINE_TAIGA, 7);

        // VERY DANGEROUS BIOMES (8-15) - Late-game
        register(Biomes.WINDSWEPT_HILLS, 9);
        register(Biomes.WINDSWEPT_GRAVELLY_HILLS, 10);
        register(Biomes.WINDSWEPT_FOREST, 10);
        register(Biomes.WINDSWEPT_SAVANNA, 9);
        register(Biomes.DARK_FOREST, 12);
        register(Biomes.ICE_SPIKES, 10);
        register(Biomes.FROZEN_PEAKS, 11);
        register(Biomes.JAGGED_PEAKS, 12);
        register(Biomes.SNOWY_SLOPES, 10);
        register(Biomes.GROVE, 9);
        register(Biomes.STONY_PEAKS, 11);

        // DEADLY BIOMES (15-20) - Endgame
        register(Biomes.DEEP_DARK, 18);
        register(Biomes.DRIPSTONE_CAVES, 12);
        register(Biomes.LUSH_CAVES, 8);
        register(Biomes.MUSHROOM_FIELDS, 15); // Strange dimensional area

        // OCEAN BIOMES (3-12)
        register(Biomes.OCEAN, 5);
        register(Biomes.DEEP_OCEAN, 8);
        register(Biomes.COLD_OCEAN, 6);
        register(Biomes.DEEP_COLD_OCEAN, 9);
        register(Biomes.FROZEN_OCEAN, 7);
        register(Biomes.DEEP_FROZEN_OCEAN, 10);
        register(Biomes.LUKEWARM_OCEAN, 5);
        register(Biomes.DEEP_LUKEWARM_OCEAN, 8);
        register(Biomes.WARM_OCEAN, 6);
        register(Biomes.FROZEN_RIVER, 4);
    }

    /**
     * Register a biome with its base difficulty level
     */
    private static void register(ResourceKey<Biome> biomeKey, int baseLevel) {
        BIOME_BASE_LEVELS.put(biomeKey, baseLevel);
    }

    /**
     * Get base difficulty level for a biome
     * @param biome The biome
     * @return Base level (1-20), defaults to 5 if unknown
     */
    public static int getBaseLevel(Biome biome) {
        // Try to get registered holder key
        // Note: This is a simplified version - in practice you'd need to
        // access the biome registry to get the ResourceLocation

        // For now, we'll use a fallback approach based on biome properties
        return getBiomeLevelByProperties(biome);
    }

    /**
     * Get biome level by analyzing biome properties
     * Fallback method when we can't get the ResourceLocation
     */
    private static int getBiomeLevelByProperties(Biome biome) {
        Biome.ClimateSettings climate = biome.getModifiedClimateSettings();
        float temp = climate.temperature();
        float downfall = climate.downfall();

        // Very cold biomes (frozen) - medium danger
        if (temp < 0.0f) {
            return 9;
        }

        // Hot and dry (desert/badlands) - dangerous
        if (temp > 1.5f && downfall < 0.2f) {
            return 7;
        }

        // Wet and warm (jungle/swamp) - dangerous
        if (temp > 0.8f && downfall > 0.8f) {
            return 8;
        }

        // Temperate - safe/moderate
        if (temp >= 0.4f && temp <= 0.8f) {
            return 3;
        }

        // Default moderate
        return 5;
    }

    /**
     * Get level by ResourceKey (for registered biomes)
     */
    public static int getLevelByKey(ResourceKey<Biome> biomeKey) {
        return BIOME_BASE_LEVELS.getOrDefault(biomeKey, 5);
    }

    /**
     * Check if biome is considered "safe" for starting
     */
    public static boolean isSafeBiome(Biome biome) {
        return getBaseLevel(biome) <= 4;
    }

    /**
     * Check if biome is endgame difficulty
     */
    public static boolean isEndgameBiome(Biome biome) {
        return getBaseLevel(biome) >= 15;
    }

    /**
     * Get all registered biome levels (for debugging)
     */
    public static Map<ResourceKey<Biome>, Integer> getAllLevels() {
        return new HashMap<>(BIOME_BASE_LEVELS);
    }
}
