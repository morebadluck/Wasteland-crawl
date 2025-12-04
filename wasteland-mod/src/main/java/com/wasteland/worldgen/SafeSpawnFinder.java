package com.wasteland.worldgen;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Finds safe spawn locations in the wasteland.
 * Safe = solid ground, no trees/vegetation above, enough air space to spawn.
 */
public class SafeSpawnFinder {
    private static final Logger LOGGER = LogManager.getLogger();
    private static final int MAX_SEARCH_RADIUS = 32;
    private static final int MIN_AIR_HEIGHT = 3; // Need at least 3 blocks of air for player

    /**
     * Find a safe spawn point near the given position.
     * Searches in a spiral pattern outward from the center.
     */
    public static BlockPos findSafeSpawn(ServerLevel level, BlockPos center) {
        LOGGER.info("Searching for safe spawn near {}", center);

        // Start at world surface
        BlockPos surfacePos = level.getHeightmapPos(Heightmap.Types.WORLD_SURFACE, center);

        // Check center first
        BlockPos safe = checkAndFindSafe(level, surfacePos);
        if (safe != null) {
            LOGGER.info("Found safe spawn at center: {}", safe);
            return safe;
        }

        // Spiral search outward
        for (int radius = 1; radius <= MAX_SEARCH_RADIUS; radius++) {
            for (int angle = 0; angle < 360; angle += 45) {
                double rad = Math.toRadians(angle);
                int offsetX = (int) (radius * Math.cos(rad));
                int offsetZ = (int) (radius * Math.sin(rad));

                BlockPos testPos = center.offset(offsetX, 0, offsetZ);
                BlockPos testSurface = level.getHeightmapPos(Heightmap.Types.WORLD_SURFACE, testPos);

                safe = checkAndFindSafe(level, testSurface);
                if (safe != null) {
                    LOGGER.info("Found safe spawn at radius {}: {}", radius, safe);
                    return safe;
                }
            }
        }

        // Fallback: just use surface position with clearance
        LOGGER.warn("Could not find ideal safe spawn, using fallback position");
        return clearAreaForSpawn(level, surfacePos);
    }

    /**
     * Check if a position is safe, and if not, try to find solid ground below
     */
    private static BlockPos checkAndFindSafe(ServerLevel level, BlockPos pos) {
        // Scan down from surface to find actual solid ground
        BlockPos groundPos = findSolidGround(level, pos);
        if (groundPos == null) {
            return null;
        }

        // Check if there's enough air space above ground
        if (hasEnoughAirSpace(level, groundPos)) {
            return groundPos.above(); // Spawn player one block above ground
        }

        return null;
    }

    /**
     * Find solid ground by scanning downward
     */
    private static BlockPos findSolidGround(ServerLevel level, BlockPos start) {
        for (int y = start.getY(); y >= level.getMinBuildHeight(); y--) {
            BlockPos testPos = new BlockPos(start.getX(), y, start.getZ());
            BlockState state = level.getBlockState(testPos);

            // Check if this block is solid and the block above is air/replaceable
            if (!state.isAir() && state.isSolidRender(level, testPos)) {
                BlockState above = level.getBlockState(testPos.above());
                if (above.isAir() || above.canBeReplaced()) {
                    return testPos;
                }
            }
        }
        return null;
    }

    /**
     * Check if there's enough vertical air space for a player to spawn
     */
    private static boolean hasEnoughAirSpace(ServerLevel level, BlockPos groundPos) {
        for (int y = 1; y <= MIN_AIR_HEIGHT; y++) {
            BlockPos checkPos = groundPos.above(y);
            BlockState state = level.getBlockState(checkPos);

            // If we hit a non-air, non-replaceable block, not enough space
            if (!state.isAir() && !state.canBeReplaced()) {
                return false;
            }
        }
        return true;
    }

    /**
     * Clear a 3x3 area above a spawn point to ensure safety
     * This removes leaves, grass, etc. that might be in the way
     */
    private static BlockPos clearAreaForSpawn(ServerLevel level, BlockPos groundPos) {
        LOGGER.info("Clearing spawn area at {}", groundPos);

        // Clear 3x3 area, 4 blocks tall
        for (int x = -1; x <= 1; x++) {
            for (int z = -1; z <= 1; z++) {
                BlockPos basePos = groundPos.offset(x, 0, z);

                // Find solid ground at this position
                BlockPos solidGround = findSolidGround(level, basePos);
                if (solidGround != null) {
                    // Clear vegetation above
                    for (int y = 1; y <= 4; y++) {
                        BlockPos clearPos = solidGround.above(y);
                        BlockState state = level.getBlockState(clearPos);

                        // Remove leaves, grass, flowers, etc.
                        if (state.canBeReplaced() || !state.isSolidRender(level, clearPos)) {
                            level.removeBlock(clearPos, false);
                        }
                    }
                }
            }
        }

        return groundPos.above();
    }
}
