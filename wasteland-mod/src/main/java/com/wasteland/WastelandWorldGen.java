package com.wasteland;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraftforge.event.level.ChunkEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

/**
 * Handles world generation for wasteland features
 * - Spawns dungeon entrance structures naturally
 * - Adds wasteland decorations (ruins, debris)
 */
@Mod.EventBusSubscriber(modid = WastelandMod.MOD_ID)
public class WastelandWorldGen {
    private static final Logger LOGGER = LogManager.getLogger();
    private static final Random RANDOM = new Random();

    // Track which chunks have been processed (prevent duplicates)
    private static final Set<String> PROCESSED_CHUNKS = new HashSet<>();

    // Entrance spawning configuration
    private static final int ENTRANCE_SPACING = 16; // Chunks between entrances (16 chunks = 256 blocks)
    private static final double ENTRANCE_CHANCE = 0.15; // 15% chance per valid chunk

    /**
     * Generate wasteland features when chunks load
     * TEMPORARILY DISABLED - causing hang during world generation
     * TODO: Re-enable with proper async handling
     */
    // @SubscribeEvent
    public static void onChunkLoad_DISABLED(ChunkEvent.Load event) {
        // Only run on server side, only for overworld
        if (!(event.getLevel() instanceof ServerLevel level)) return;
        if (level.dimension() != Level.OVERWORLD) return;

        ChunkAccess chunk = event.getChunk();
        int chunkX = chunk.getPos().x;
        int chunkZ = chunk.getPos().z;

        // Create unique key for this chunk
        String chunkKey = chunkX + "," + chunkZ;

        // Skip if we've already processed this chunk
        if (PROCESSED_CHUNKS.contains(chunkKey)) return;
        PROCESSED_CHUNKS.add(chunkKey);

        // Check if this chunk should have an entrance
        // Only consider chunks on a grid (for spacing)
        if (chunkX % ENTRANCE_SPACING == 0 && chunkZ % ENTRANCE_SPACING == 0) {
            // Random chance to spawn entrance
            if (RANDOM.nextDouble() < ENTRANCE_CHANCE) {
                generateDungeonEntrance(level, chunkX, chunkZ);
            }
        }

        // Small chance for wasteland decorations in any chunk
        if (RANDOM.nextDouble() < 0.05) { // 5% chance
            generateWastelandDecoration(level, chunkX, chunkZ);
        }
    }

    /**
     * Generate a dungeon entrance structure in the chunk
     */
    private static void generateDungeonEntrance(ServerLevel level, int chunkX, int chunkZ) {
        // Convert chunk coordinates to world coordinates
        int worldX = (chunkX << 4) + RANDOM.nextInt(16);
        int worldZ = (chunkZ << 4) + RANDOM.nextInt(16);

        // Find ground level
        BlockPos searchPos = new BlockPos(worldX, 64, worldZ);
        BlockPos groundPos = level.getHeightmapPos(
            net.minecraft.world.level.levelgen.Heightmap.Types.WORLD_SURFACE,
            searchPos
        );

        // Don't place in water or very high/low locations
        if (groundPos.getY() < 60 || groundPos.getY() > 100) {
            return;
        }

        // Check if there's solid ground
        if (!level.getBlockState(groundPos.below()).isSolidRender(level, groundPos.below())) {
            return;
        }

        LOGGER.info("Generating dungeon entrance at chunk ({}, {}) - world pos {}",
                    chunkX, chunkZ, groundPos);

        // Place entrance structure
        DungeonEntrance.placeRandomEntrance(level, groundPos);
    }

    /**
     * Generate wasteland decorations (ruins, debris)
     */
    private static void generateWastelandDecoration(ServerLevel level, int chunkX, int chunkZ) {
        // Convert chunk coordinates to world coordinates
        int worldX = (chunkX << 4) + RANDOM.nextInt(16);
        int worldZ = (chunkZ << 4) + RANDOM.nextInt(16);

        // Find ground level
        BlockPos searchPos = new BlockPos(worldX, 64, worldZ);
        BlockPos groundPos = level.getHeightmapPos(
            net.minecraft.world.level.levelgen.Heightmap.Types.WORLD_SURFACE,
            searchPos
        );

        // Place wasteland debris (random scattered blocks)
        int decorationType = RANDOM.nextInt(5);

        switch (decorationType) {
            case 0: // Rubble pile
                placeRubble(level, groundPos);
                break;
            case 1: // Dead bushes
                placeDeadBushes(level, groundPos);
                break;
            case 2: // Rusted barrel
                placeBarrel(level, groundPos);
                break;
            case 3: // Crater
                placeCrater(level, groundPos);
                break;
            case 4: // Abandoned campfire
                placeCampfire(level, groundPos);
                break;
        }
    }

    private static void placeRubble(ServerLevel level, BlockPos pos) {
        // Small pile of cracked stone bricks
        level.setBlock(pos, net.minecraft.world.level.block.Blocks.CRACKED_STONE_BRICKS.defaultBlockState(), 3);
        level.setBlock(pos.offset(1, 0, 0), net.minecraft.world.level.block.Blocks.STONE_BRICKS.defaultBlockState(), 3);
        level.setBlock(pos.offset(0, 0, 1), net.minecraft.world.level.block.Blocks.MOSSY_STONE_BRICKS.defaultBlockState(), 3);
    }

    private static void placeDeadBushes(ServerLevel level, BlockPos pos) {
        level.setBlock(pos, net.minecraft.world.level.block.Blocks.DEAD_BUSH.defaultBlockState(), 3);
        if (RANDOM.nextBoolean()) {
            level.setBlock(pos.offset(2, 0, 1), net.minecraft.world.level.block.Blocks.DEAD_BUSH.defaultBlockState(), 3);
        }
    }

    private static void placeBarrel(ServerLevel level, BlockPos pos) {
        // Use barrel block (rusted container)
        level.setBlock(pos, net.minecraft.world.level.block.Blocks.BARREL.defaultBlockState(), 3);
    }

    private static void placeCrater(ServerLevel level, BlockPos pos) {
        // Small depression with coarse dirt
        level.setBlock(pos, net.minecraft.world.level.block.Blocks.COARSE_DIRT.defaultBlockState(), 3);
        level.setBlock(pos.below(), net.minecraft.world.level.block.Blocks.COARSE_DIRT.defaultBlockState(), 3);
        for (int x = -1; x <= 1; x++) {
            for (int z = -1; z <= 1; z++) {
                if (x != 0 || z != 0) {
                    level.setBlock(pos.offset(x, 0, z), net.minecraft.world.level.block.Blocks.COARSE_DIRT.defaultBlockState(), 3);
                }
            }
        }
    }

    private static void placeCampfire(ServerLevel level, BlockPos pos) {
        // Extinguished campfire
        level.setBlock(pos, net.minecraft.world.level.block.Blocks.CAMPFIRE.defaultBlockState().setValue(
            net.minecraft.world.level.block.CampfireBlock.LIT, false
        ), 3);
    }

    /**
     * Clear cached chunks (for world reload)
     */
    public static void clearCache() {
        PROCESSED_CHUNKS.clear();
        LOGGER.info("Cleared worldgen cache");
    }
}
