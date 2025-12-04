package com.wasteland.worldgen;

import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.synth.PerlinNoise;

/**
 * Manages organic, noise-based difficulty distribution across the wasteland.
 * Replaces the old ring-based level system with natural danger zones.
 */
public class AreaDifficultyManager {

    private static final double NOISE_SCALE = 0.015; // Medium clusters (500-2000 blocks)
    private static final double NOISE_INFLUENCE = 7.5; // ±7.5 levels from noise
    private static final double DISTANCE_SCALE = 500.0; // +1 per 500 blocks
    private static final int MAX_DISTANCE_BONUS = 10;

    private final PerlinNoise difficultyNoise;
    private final BlockPos spawnPos;
    private final long worldSeed;

    // Singleton instance
    private static AreaDifficultyManager instance;

    private AreaDifficultyManager(long worldSeed, BlockPos spawnPos) {
        this.worldSeed = worldSeed;
        this.spawnPos = spawnPos;

        // Create Perlin noise for difficulty hotspots
        RandomSource random = RandomSource.create(worldSeed * 37L);
        this.difficultyNoise = PerlinNoise.create(random, -4, 1.0, 1.0, 1.0);
    }

    /**
     * Initialize the difficulty manager for a world
     */
    public static void initialize(long worldSeed, BlockPos spawnPos) {
        instance = new AreaDifficultyManager(worldSeed, spawnPos);
        System.out.println("[AreaDifficulty] Initialized organic difficulty system");
        System.out.println("[AreaDifficulty] Spawn: " + spawnPos + ", Seed: " + worldSeed);
    }

    /**
     * Get the singleton instance
     */
    public static AreaDifficultyManager getInstance() {
        if (instance == null) {
            throw new IllegalStateException("AreaDifficultyManager not initialized!");
        }
        return instance;
    }

    /**
     * Calculate the difficulty level for a position
     * @param pos World position
     * @param biome Biome at position
     * @return Difficulty level (1-25+)
     */
    public int calculateDifficulty(BlockPos pos, Biome biome) {
        // 1. Base biome level
        int baseLevel = BiomeDifficultyConfig.getBaseLevel(biome);

        // 2. Noise modifier (creates hotspots and valleys)
        double noiseValue = getNoiseFactor(pos);
        int noiseMod = (int)(noiseValue * NOISE_INFLUENCE);

        // 3. Distance modifier (gentle curve, not deterministic)
        int distanceMod = getDistanceModifier(pos);

        // 4. Combine
        int finalLevel = baseLevel + noiseMod + distanceMod;

        // Clamp to valid range
        return Mth.clamp(finalLevel, 1, 30);
    }

    /**
     * Get noise factor at position (-1.0 to +1.0)
     */
    private double getNoiseFactor(BlockPos pos) {
        double x = pos.getX() * NOISE_SCALE;
        double z = pos.getZ() * NOISE_SCALE;

        // Sample Perlin noise
        return difficultyNoise.getValue(x, 0.0, z);
    }

    /**
     * Get distance-based difficulty modifier
     */
    private int getDistanceModifier(BlockPos pos) {
        double distance = Math.sqrt(pos.distSqr(spawnPos));
        int modifier = (int)(distance / DISTANCE_SCALE);

        return Math.min(modifier, MAX_DISTANCE_BONUS);
    }

    /**
     * Get difficulty category for visual/gameplay purposes
     */
    public DifficultyCategory getDifficultyCategory(int level) {
        if (level <= 5) return DifficultyCategory.SAFE;
        if (level <= 10) return DifficultyCategory.MODERATE;
        if (level <= 15) return DifficultyCategory.DANGEROUS;
        if (level <= 20) return DifficultyCategory.VERY_DANGEROUS;
        if (level <= 25) return DifficultyCategory.DEADLY;
        return DifficultyCategory.NIGHTMARE;
    }

    /**
     * Check if spawn area is guaranteed safe (within 200 blocks)
     */
    public boolean isSpawnSafeZone(BlockPos pos) {
        return pos.distSqr(spawnPos) < 200 * 200;
    }

    /**
     * Force low difficulty in spawn zone
     */
    public int calculateDifficultyWithSpawnProtection(BlockPos pos, Biome biome) {
        if (isSpawnSafeZone(pos)) {
            // Spawn zone: force low level (1-3)
            int baseLevel = Math.min(BiomeDifficultyConfig.getBaseLevel(biome), 2);
            double noise = getNoiseFactor(pos);
            int noiseMod = (int)(noise * 2.0); // Reduced noise influence

            return Mth.clamp(baseLevel + noiseMod, 1, 3);
        }

        return calculateDifficulty(pos, biome);
    }

    /**
     * Get detailed difficulty breakdown for debugging
     */
    public String getDifficultyBreakdown(BlockPos pos, Biome biome) {
        int base = BiomeDifficultyConfig.getBaseLevel(biome);
        double noise = getNoiseFactor(pos);
        int noiseMod = (int)(noise * NOISE_INFLUENCE);
        int distance = getDistanceModifier(pos);
        int total = calculateDifficulty(pos, biome);

        return String.format("Difficulty at %s: Base=%d, Noise=%.2f(%+d), Dist=%+d, Total=%d",
            pos, base, noise, noiseMod, distance, total);
    }

    /**
     * Difficulty categories for visual representation
     */
    public enum DifficultyCategory {
        SAFE,           // 1-5: Green, minimal corruption
        MODERATE,       // 6-10: Yellow, some danger
        DANGEROUS,      // 11-15: Orange, corrupted
        VERY_DANGEROUS, // 16-20: Red, heavily corrupted
        DEADLY,         // 21-25: Dark red, dimensional tears
        NIGHTMARE       // 26+: Black/purple, reality breaking
    }
}
