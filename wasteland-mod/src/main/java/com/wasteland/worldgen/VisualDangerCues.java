package com.wasteland.worldgen;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Random;

/**
 * Generates visual and audio danger cues based on area difficulty.
 * Helps players recognize dangerous zones before entering.
 */
public class VisualDangerCues {

    private static final Random RANDOM = new Random();

    /**
     * Apply corruption effects to an area based on difficulty
     */
    public static void applyCorruption(ServerLevel level, BlockPos centerPos, int difficulty, int radius) {
        AreaDifficultyManager.DifficultyCategory category =
            AreaDifficultyManager.getInstance().getDifficultyCategory(difficulty);

        switch (category) {
            case SAFE -> {} // No corruption
            case MODERATE -> applyLightCorruption(level, centerPos, radius);
            case DANGEROUS -> applyModerateCorruption(level, centerPos, radius);
            case VERY_DANGEROUS -> applyHeavyCorruption(level, centerPos, radius);
            case DEADLY -> applyDeadlyCorruption(level, centerPos, radius);
            case NIGHTMARE -> applyNightmareCorruption(level, centerPos, radius);
        }
    }

    /**
     * Light corruption (6-10): Some withered plants
     */
    private static void applyLightCorruption(ServerLevel level, BlockPos center, int radius) {
        for (int i = 0; i < radius * 2; i++) {
            BlockPos pos = getRandomPosInRadius(center, radius);
            BlockState state = level.getBlockState(pos);

            // 20% chance to wither grass
            if (state.is(Blocks.GRASS_BLOCK) && RANDOM.nextFloat() < 0.2f) {
                level.setBlock(pos, Blocks.COARSE_DIRT.defaultBlockState(), 2);
            }
        }
    }

    /**
     * Moderate corruption (11-15): Darkened ground, dead plants
     */
    private static void applyModerateCorruption(ServerLevel level, BlockPos center, int radius) {
        for (int i = 0; i < radius * 3; i++) {
            BlockPos pos = getRandomPosInRadius(center, radius);
            BlockState state = level.getBlockState(pos);

            // 40% chance to darken/corrupt ground
            if (state.is(Blocks.GRASS_BLOCK) && RANDOM.nextFloat() < 0.4f) {
                level.setBlock(pos, Blocks.PODZOL.defaultBlockState(), 2);
            } else if (state.is(Blocks.DIRT) && RANDOM.nextFloat() < 0.3f) {
                level.setBlock(pos, Blocks.COARSE_DIRT.defaultBlockState(), 2);
            }

            // Kill flowers
            BlockPos above = pos.above();
            if (level.getBlockState(above).is(net.minecraft.tags.BlockTags.FLOWERS)) {
                level.setBlock(above, Blocks.DEAD_BUSH.defaultBlockState(), 2);
            }
        }
    }

    /**
     * Heavy corruption (16-20): Very dark ground, some particles
     */
    private static void applyHeavyCorruption(ServerLevel level, BlockPos center, int radius) {
        for (int i = 0; i < radius * 4; i++) {
            BlockPos pos = getRandomPosInRadius(center, radius);
            BlockState state = level.getBlockState(pos);

            // 60% chance to heavily corrupt
            if ((state.is(Blocks.GRASS_BLOCK) || state.is(Blocks.DIRT)) && RANDOM.nextFloat() < 0.6f) {
                // Use darker blocks for heavy corruption
                level.setBlock(pos, Blocks.SOUL_SOIL.defaultBlockState(), 2);
            }

            // Remove all vegetation
            BlockPos above = pos.above();
            BlockState aboveState = level.getBlockState(above);
            if (aboveState.is(net.minecraft.tags.BlockTags.FLOWERS) ||
                aboveState.is(net.minecraft.tags.BlockTags.SAPLINGS) ||
                aboveState.is(Blocks.TALL_GRASS)) {
                level.setBlock(above, Blocks.AIR.defaultBlockState(), 2);
            }
        }

        // Add occasional dimensional particles
        if (RANDOM.nextFloat() < 0.1f) {
            spawnDimensionalParticles(level, center);
        }
    }

    /**
     * Deadly corruption (21-25): Reality tearing, dimensional rifts
     */
    private static void applyDeadlyCorruption(ServerLevel level, BlockPos center, int radius) {
        for (int i = 0; i < radius * 5; i++) {
            BlockPos pos = getRandomPosInRadius(center, radius);
            BlockState state = level.getBlockState(pos);

            // 80% chance to severely corrupt
            if (!state.isAir() && RANDOM.nextFloat() < 0.8f) {
                // Reality breaking - use End/Nether blocks
                if (RANDOM.nextBoolean()) {
                    level.setBlock(pos, Blocks.NETHERRACK.defaultBlockState(), 2);
                } else {
                    level.setBlock(pos, Blocks.SOUL_SAND.defaultBlockState(), 2);
                }
            }
        }

        // Spawn dimensional rift particles frequently
        for (int i = 0; i < 5; i++) {
            if (RANDOM.nextFloat() < 0.3f) {
                BlockPos riftPos = getRandomPosInRadius(center, radius/2);
                spawnDimensionalRift(level, riftPos);
            }
        }

        // Ominous sounds
        if (RANDOM.nextFloat() < 0.2f) {
            level.playSound(null, center, SoundEvents.PORTAL_AMBIENT, SoundSource.AMBIENT,
                2.0f, 0.5f + RANDOM.nextFloat() * 0.3f);
        }
    }

    /**
     * Nightmare corruption (26+): Complete reality breakdown
     */
    private static void applyNightmareCorruption(ServerLevel level, BlockPos center, int radius) {
        // Maximum corruption
        for (int i = 0; i < radius * 6; i++) {
            BlockPos pos = getRandomPosInRadius(center, radius);

            // 90% chance to corrupt to obsidian/crying obsidian
            if (RANDOM.nextFloat() < 0.9f) {
                if (RANDOM.nextFloat() < 0.3f) {
                    level.setBlock(pos, Blocks.CRYING_OBSIDIAN.defaultBlockState(), 2);
                } else {
                    level.setBlock(pos, Blocks.OBSIDIAN.defaultBlockState(), 2);
                }
            }
        }

        // Multiple dimensional rifts
        for (int i = 0; i < 10; i++) {
            BlockPos riftPos = getRandomPosInRadius(center, radius);
            spawnDimensionalRift(level, riftPos);

            // Portal particles
            level.sendParticles(ParticleTypes.REVERSE_PORTAL,
                riftPos.getX() + 0.5, riftPos.getY() + 1, riftPos.getZ() + 0.5,
                20, 1.0, 1.0, 1.0, 0.1);
        }

        // Constant ominous sounds
        level.playSound(null, center, SoundEvents.WITHER_AMBIENT, SoundSource.AMBIENT,
            3.0f, 0.1f);
    }

    /**
     * Spawn dimensional particles at a location
     */
    private static void spawnDimensionalParticles(ServerLevel level, BlockPos pos) {
        // Purple/red particles indicating dimensional energy
        level.sendParticles(ParticleTypes.DRAGON_BREATH,
            pos.getX() + 0.5, pos.getY() + 1, pos.getZ() + 0.5,
            10, 0.5, 0.5, 0.5, 0.05);
    }

    /**
     * Create a dimensional rift effect
     */
    private static void spawnDimensionalRift(ServerLevel level, BlockPos pos) {
        // Tear in reality visual
        level.sendParticles(ParticleTypes.PORTAL,
            pos.getX() + 0.5, pos.getY() + 1, pos.getZ() + 0.5,
            30, 0.3, 1.0, 0.3, 0.5);

        level.sendParticles(ParticleTypes.WARPED_SPORE,
            pos.getX() + 0.5, pos.getY() + 1, pos.getZ() + 0.5,
            15, 0.5, 1.0, 0.5, 0.1);
    }

    /**
     * Get random position within radius of center
     */
    private static BlockPos getRandomPosInRadius(BlockPos center, int radius) {
        int x = center.getX() + RANDOM.nextInt(radius * 2) - radius;
        int z = center.getZ() + RANDOM.nextInt(radius * 2) - radius;
        return new BlockPos(x, center.getY(), z);
    }

    /**
     * Check if position should have danger particles (for client-side rendering)
     */
    public static boolean shouldSpawnDangerParticles(Level level, BlockPos pos) {
        // This would be called client-side to add ambient particles
        // For now, just a placeholder for future client rendering
        return false;
    }

    /**
     * Get ambient sound for danger level
     */
    public static net.minecraft.sounds.SoundEvent getDangerSound(int difficulty) {
        if (difficulty < 10) return null;
        if (difficulty < 15) return SoundEvents.AMBIENT_CAVE.value();
        if (difficulty < 20) return SoundEvents.WARDEN_AMBIENT;
        if (difficulty < 25) return SoundEvents.PORTAL_AMBIENT;
        return SoundEvents.WITHER_AMBIENT;
    }
}
