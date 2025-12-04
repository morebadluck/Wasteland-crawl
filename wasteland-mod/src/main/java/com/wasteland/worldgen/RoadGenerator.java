package com.wasteland.worldgen;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.*;

/**
 * Generates degraded wasteland roads between nearby dungeon entrances (7-11s).
 * Roads are more degraded in hostile biomes (swamps, forests) and better preserved
 * in open areas (plains, deserts).
 */
public class RoadGenerator {
    private static final Logger LOGGER = LogManager.getLogger();
    private static final int MAX_ROAD_DISTANCE = 1500; // Only connect dungeons within 1.5km
    private static final int ROAD_WIDTH = 3; // 3 blocks wide
    private static final Random RANDOM = new Random();

    /**
     * Generate roads between all nearby dungeon pairs
     */
    public static void generateRoads(ServerLevel level, long worldSeed) {
        RANDOM.setSeed(worldSeed * 37); // Different seed for roads

        Collection<DungeonInstance> dungeons = DungeonManager.getAllDungeons();
        List<DungeonInstance> dungeonList = new ArrayList<>(dungeons);

        LOGGER.info("Generating roads between {} dungeons", dungeonList.size());

        int roadsCreated = 0;

        // Find pairs of nearby dungeons
        for (int i = 0; i < dungeonList.size(); i++) {
            DungeonInstance dungeon1 = dungeonList.get(i);

            // Find 1-2 closest dungeons to connect to
            List<DungeonPair> nearbyDungeons = findNearbyDungeons(dungeon1, dungeonList, i);

            for (DungeonPair pair : nearbyDungeons) {
                generateRoadBetween(level, pair.start, pair.end);
                roadsCreated++;
            }
        }

        LOGGER.info("Generated {} wasteland roads", roadsCreated);
    }

    /**
     * Find the 1-2 closest dungeons to connect with roads
     */
    private static List<DungeonPair> findNearbyDungeons(DungeonInstance start, List<DungeonInstance> all, int startIndex) {
        List<DungeonPair> pairs = new ArrayList<>();

        for (int i = startIndex + 1; i < all.size(); i++) {
            DungeonInstance end = all.get(i);
            double distance = start.getEntrancePos().distSqr(end.getEntrancePos());

            if (distance < MAX_ROAD_DISTANCE * MAX_ROAD_DISTANCE) {
                pairs.add(new DungeonPair(start, end, distance));
            }
        }

        // Sort by distance and take top 1-2
        pairs.sort(Comparator.comparingDouble(p -> p.distance));
        int connectCount = RANDOM.nextInt(2) + 1; // 1-2 connections
        return pairs.subList(0, Math.min(connectCount, pairs.size()));
    }

    /**
     * Generate a degraded road between two dungeon entrances
     */
    private static void generateRoadBetween(ServerLevel level, DungeonInstance start, DungeonInstance end) {
        BlockPos startPos = start.getEntrancePos();
        BlockPos endPos = end.getEntrancePos();

        LOGGER.debug("Creating road from {} to {}", startPos, endPos);

        // Use Bresenham-like algorithm to trace line between dungeons
        int dx = endPos.getX() - startPos.getX();
        int dz = endPos.getZ() - startPos.getZ();
        int steps = (int) Math.sqrt(dx * dx + dz * dz);

        for (int step = 0; step < steps; step++) {
            float t = (float) step / steps;
            int x = (int) (startPos.getX() + dx * t);
            int z = (int) (startPos.getZ() + dz * t);

            BlockPos roadCenter = new BlockPos(x, 64, z);
            BlockPos groundPos = level.getHeightmapPos(Heightmap.Types.WORLD_SURFACE, roadCenter);

            // Place road segment with random degradation
            placeRoadSegment(level, groundPos);
        }
    }

    /**
     * Place a road segment at a specific location
     */
    private static void placeRoadSegment(ServerLevel level, BlockPos center) {
        // Get biome to determine degradation level
        Biome biome = level.getBiome(center).value();
        double degradation = getDegradationForBiome(biome);

        // Place road blocks in a 3-wide pattern
        for (int offsetX = -1; offsetX <= 1; offsetX++) {
            for (int offsetZ = -1; offsetZ <= 1; offsetZ++) {
                // Skip corners for more natural look
                if (Math.abs(offsetX) == 1 && Math.abs(offsetZ) == 1) {
                    if (RANDOM.nextDouble() > 0.3) continue; // 70% chance to skip corners
                }

                BlockPos roadPos = center.offset(offsetX, 0, offsetZ);

                // Random chance to skip this block based on degradation
                if (RANDOM.nextDouble() < degradation) {
                    continue; // Leave as natural terrain (degraded)
                }

                // Place road block
                placeRoadBlock(level, roadPos);
            }
        }
    }

    /**
     * Place a single road block with weathering effects
     */
    private static void placeRoadBlock(ServerLevel level, BlockPos pos) {
        BlockState currentBlock = level.getBlockState(pos);

        // Don't replace solid blocks (buildings, rocks, etc.)
        if (!currentBlock.isAir() && currentBlock.isSolidRender(level, pos)) {
            return;
        }

        // Choose road material with random variation
        BlockState roadBlock = chooseRoadMaterial();

        level.setBlock(pos, roadBlock, 3);

        // Sometimes place cracked variants or debris
        if (RANDOM.nextDouble() < 0.1) {
            // Place gravel/dirt for weathering
            if (RANDOM.nextBoolean()) {
                level.setBlock(pos, Blocks.GRAVEL.defaultBlockState(), 3);
            } else {
                level.setBlock(pos, Blocks.COARSE_DIRT.defaultBlockState(), 3);
            }
        }
    }

    /**
     * Choose road material with variation
     */
    private static BlockState chooseRoadMaterial() {
        double roll = RANDOM.nextDouble();

        if (roll < 0.4) {
            return Blocks.GRAY_CONCRETE.defaultBlockState(); // Main road material
        } else if (roll < 0.7) {
            return Blocks.STONE.defaultBlockState(); // Weathered to stone
        } else if (roll < 0.85) {
            return Blocks.COBBLESTONE.defaultBlockState(); // More weathered
        } else {
            return Blocks.ANDESITE.defaultBlockState(); // Broken up
        }
    }

    /**
     * Determine degradation level based on position
     * 0.0 = perfect roads, 1.0 = completely destroyed
     *
     * Uses position-based randomness to vary degradation naturally
     */
    private static double getDegradationForBiome(Biome biome) {
        // Use random degradation based on position for variety
        // Most roads will be 40-70% degraded
        double base = 0.4 + RANDOM.nextDouble() * 0.3;

        // Add some clusters of better/worse preservation
        if (RANDOM.nextDouble() < 0.2) {
            // 20% chance of better preserved section (20-40% degraded)
            return 0.2 + RANDOM.nextDouble() * 0.2;
        } else if (RANDOM.nextDouble() < 0.15) {
            // 15% chance of heavily degraded section (70-90% degraded)
            return 0.7 + RANDOM.nextDouble() * 0.2;
        }

        return base;
    }

    /**
     * Helper class to store dungeon pairs for road generation
     */
    private static class DungeonPair {
        final DungeonInstance start;
        final DungeonInstance end;
        final double distance;

        DungeonPair(DungeonInstance start, DungeonInstance end, double distance) {
            this.start = start;
            this.end = end;
            this.distance = distance;
        }
    }
}
