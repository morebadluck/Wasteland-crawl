package com.wasteland;

import com.wasteland.worldgen.DungeonType;
import com.wasteland.worldgen.USARegion;
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
 * Handles world generation for wasteland features in a USA-styled map
 * - Spawns dungeon entrance structures in biome-appropriate locations
 * - Places 7-11 stores as safe zones
 * - Adds wasteland decorations (ruins, debris)
 */
@Mod.EventBusSubscriber(modid = WastelandMod.MOD_ID)
public class WastelandWorldGen {
    private static final Logger LOGGER = LogManager.getLogger();
    private static final Random RANDOM = new Random();

    // Track which chunks have been processed (prevent duplicates)
    private static final Set<String> PROCESSED_CHUNKS = new HashSet<>();

    // Structure spawning configuration
    private static final int DUNGEON_SPACING = 32; // Chunks between dungeon entrances (512 blocks)
    private static final double DUNGEON_CHANCE = 0.20; // 20% chance per valid chunk

    private static final int SEVEN_ELEVEN_SPACING = 24; // Chunks between 7-11 stores (384 blocks)
    private static final double SEVEN_ELEVEN_CHANCE = 0.15; // 15% chance per valid chunk

    private static final int MALL_SPACING = 64; // Chunks between malls (1024 blocks)
    private static final double MALL_CHANCE = 0.10; // 10% chance per valid chunk

    private static final int CHURCH_SPACING = 40; // Chunks between churches (640 blocks)
    private static final double CHURCH_CHANCE = 0.12; // 12% chance per valid chunk

    /**
     * Generate wasteland features when chunks load
     * Uses USA region system to place biome-appropriate structures
     */
    // Queue of chunks waiting for structure generation
    private static final java.util.Queue<PendingStructure> PENDING_STRUCTURES = new java.util.concurrent.ConcurrentLinkedQueue<>();

    private static class PendingStructure {
        final ServerLevel level;
        final int chunkX;
        final int chunkZ;
        final StructureType type;

        PendingStructure(ServerLevel level, int chunkX, int chunkZ, StructureType type) {
            this.level = level;
            this.chunkX = chunkX;
            this.chunkZ = chunkZ;
            this.type = type;
        }
    }

    private enum StructureType {
        DUNGEON_ENTRANCE,
        SEVEN_ELEVEN,
        MALL,
        CHURCH,
        DECORATION
    }

    /**
     * Mark chunks that need structure generation (doesn't place blocks yet)
     */
    @SubscribeEvent
    public static void onChunkLoad(ChunkEvent.Load event) {
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

        // Convert chunk coords to block coords (center of chunk)
        int blockX = (chunkX << 4) + 8;
        int blockZ = (chunkZ << 4) + 8;

        // Queue structures for later placement
        // Check for dungeon entrance spawning (grid-based)
        if (chunkX % DUNGEON_SPACING == 0 && chunkZ % DUNGEON_SPACING == 0) {
            if (RANDOM.nextDouble() < DUNGEON_CHANCE) {
                PENDING_STRUCTURES.add(new PendingStructure(level, chunkX, chunkZ, StructureType.DUNGEON_ENTRANCE));
            }
        }

        // Check for 7-11 store spawning (different grid)
        if ((chunkX + 12) % SEVEN_ELEVEN_SPACING == 0 && (chunkZ + 12) % SEVEN_ELEVEN_SPACING == 0) {
            if (RANDOM.nextDouble() < SEVEN_ELEVEN_CHANCE) {
                PENDING_STRUCTURES.add(new PendingStructure(level, chunkX, chunkZ, StructureType.SEVEN_ELEVEN));
            }
        }

        // Check for mall spawning (different grid offset)
        if ((chunkX + 24) % MALL_SPACING == 0 && (chunkZ + 24) % MALL_SPACING == 0) {
            if (RANDOM.nextDouble() < MALL_CHANCE) {
                PENDING_STRUCTURES.add(new PendingStructure(level, chunkX, chunkZ, StructureType.MALL));
            }
        }

        // Check for church spawning (different grid offset)
        if ((chunkX + 18) % CHURCH_SPACING == 0 && (chunkZ + 18) % CHURCH_SPACING == 0) {
            if (RANDOM.nextDouble() < CHURCH_CHANCE) {
                PENDING_STRUCTURES.add(new PendingStructure(level, chunkX, chunkZ, StructureType.CHURCH));
            }
        }

        // Small chance for wasteland decorations in any chunk
        if (RANDOM.nextDouble() < 0.03) { // 3% chance
            PENDING_STRUCTURES.add(new PendingStructure(level, chunkX, chunkZ, StructureType.DECORATION));
        }
    }

    /**
     * Process pending structures on server tick (deferred generation)
     */
    @SubscribeEvent
    public static void onServerTick(net.minecraftforge.event.TickEvent.ServerTickEvent event) {
        // Only process at end of tick
        if (event.phase != net.minecraftforge.event.TickEvent.Phase.END) return;

        // Process up to 5 structures per tick to avoid lag
        int processed = 0;
        while (processed < 5 && !PENDING_STRUCTURES.isEmpty()) {
            PendingStructure pending = PENDING_STRUCTURES.poll();
            if (pending != null) {
                processStructure(pending);
                processed++;
            }
        }
    }

    /**
     * Actually place the structure blocks
     */
    private static void processStructure(PendingStructure pending) {
        // Convert chunk coords to block coords (center of chunk)
        int blockX = (pending.chunkX << 4) + 8;
        int blockZ = (pending.chunkZ << 4) + 8;

        switch (pending.type) {
            case DUNGEON_ENTRANCE:
                USARegion region = USARegion.getRegion(blockX, blockZ);
                generateDungeonEntrance(pending.level, pending.chunkX, pending.chunkZ, region);
                break;
            case SEVEN_ELEVEN:
                generateSevenEleven(pending.level, pending.chunkX, pending.chunkZ);
                break;
            case MALL:
                generateMall(pending.level, pending.chunkX, pending.chunkZ);
                break;
            case CHURCH:
                generateChurch(pending.level, pending.chunkX, pending.chunkZ);
                break;
            case DECORATION:
                generateWastelandDecoration(pending.level, pending.chunkX, pending.chunkZ);
                break;
        }
    }

    /**
     * Generate a biome-appropriate dungeon entrance structure
     */
    private static void generateDungeonEntrance(ServerLevel level, int chunkX, int chunkZ, USARegion region) {
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
        if (groundPos.getY() < 60 || groundPos.getY() > 120) {
            return;
        }

        // Check if there's solid ground
        if (!level.getBlockState(groundPos.below()).isSolidRender(level, groundPos.below())) {
            return;
        }

        // Select appropriate dungeon type for this region
        DungeonType dungeonType = DungeonType.getRandomForRegion(region, RANDOM);

        LOGGER.info("Generating {} entrance at chunk ({}, {}) in {} region - world pos {}",
                    dungeonType.getDisplayName(), chunkX, chunkZ, region.getName(), groundPos);

        // Place entrance structure
        // TODO: Pass dungeon type to entrance generator
        DungeonEntrance.placeRandomEntrance(level, groundPos);
    }

    /**
     * Generate a 7-11 convenience store (safe zone)
     */
    private static void generateSevenEleven(ServerLevel level, int chunkX, int chunkZ) {
        // Convert chunk coordinates to world coordinates
        int worldX = (chunkX << 4) + RANDOM.nextInt(16);
        int worldZ = (chunkZ << 4) + RANDOM.nextInt(16);

        // Find ground level
        BlockPos searchPos = new BlockPos(worldX, 64, worldZ);
        BlockPos groundPos = level.getHeightmapPos(
            net.minecraft.world.level.levelgen.Heightmap.Types.WORLD_SURFACE,
            searchPos
        );

        // Don't place in water or extreme elevations
        if (groundPos.getY() < 62 || groundPos.getY() > 100) {
            return;
        }

        // Check if there's solid ground
        if (!level.getBlockState(groundPos.below()).isSolidRender(level, groundPos.below())) {
            return;
        }

        LOGGER.info("Generating 7-11 store at chunk ({}, {}) - world pos {}", chunkX, chunkZ, groundPos);

        // Place 7-11 structure
        placeSevenElevenStructure(level, groundPos);
    }

    /**
     * Place a simple 7-11 store structure
     * TODO: Replace with proper structure file
     */
    private static void placeSevenElevenStructure(ServerLevel level, BlockPos pos) {
        // Simple 7x7 structure for now
        // Floor
        for (int x = -3; x <= 3; x++) {
            for (int z = -3; z <= 3; z++) {
                level.setBlock(pos.offset(x, -1, z),
                    net.minecraft.world.level.block.Blocks.STONE_BRICKS.defaultBlockState(), 3);
            }
        }

        // Walls
        for (int x = -3; x <= 3; x++) {
            for (int y = 0; y < 3; y++) {
                // North and south walls
                level.setBlock(pos.offset(x, y, -3),
                    net.minecraft.world.level.block.Blocks.BRICKS.defaultBlockState(), 3);
                level.setBlock(pos.offset(x, y, 3),
                    net.minecraft.world.level.block.Blocks.BRICKS.defaultBlockState(), 3);
            }
        }

        for (int z = -3; z <= 3; z++) {
            for (int y = 0; y < 3; y++) {
                // East and west walls
                level.setBlock(pos.offset(-3, y, z),
                    net.minecraft.world.level.block.Blocks.BRICKS.defaultBlockState(), 3);
                level.setBlock(pos.offset(3, y, z),
                    net.minecraft.world.level.block.Blocks.BRICKS.defaultBlockState(), 3);
            }
        }

        // Doorway (south side)
        level.setBlock(pos.offset(0, 0, 3), net.minecraft.world.level.block.Blocks.AIR.defaultBlockState(), 3);
        level.setBlock(pos.offset(0, 1, 3), net.minecraft.world.level.block.Blocks.AIR.defaultBlockState(), 3);

        // Roof
        for (int x = -3; x <= 3; x++) {
            for (int z = -3; z <= 3; z++) {
                level.setBlock(pos.offset(x, 3, z),
                    net.minecraft.world.level.block.Blocks.QUARTZ_BLOCK.defaultBlockState(), 3);
            }
        }

        // Interior - some barrels and chests for loot
        level.setBlock(pos.offset(-2, 0, -2),
            net.minecraft.world.level.block.Blocks.BARREL.defaultBlockState(), 3);
        level.setBlock(pos.offset(2, 0, -2),
            net.minecraft.world.level.block.Blocks.BARREL.defaultBlockState(), 3);
        level.setBlock(pos.offset(0, 0, -2),
            net.minecraft.world.level.block.Blocks.CHEST.defaultBlockState(), 3);

        // Glowstone for lighting
        level.setBlock(pos.offset(0, 2, 0),
            net.minecraft.world.level.block.Blocks.GLOWSTONE.defaultBlockState(), 3);

        // Sign outside
        level.setBlock(pos.offset(1, 0, 4),
            net.minecraft.world.level.block.Blocks.OAK_SIGN.defaultBlockState(), 3);
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
     * Generate a mall structure
     */
    private static void generateMall(ServerLevel level, int chunkX, int chunkZ) {
        // Convert chunk coordinates to world coordinates
        int worldX = (chunkX << 4) + RANDOM.nextInt(16);
        int worldZ = (chunkZ << 4) + RANDOM.nextInt(16);

        // Find ground level
        BlockPos searchPos = new BlockPos(worldX, 64, worldZ);
        BlockPos groundPos = level.getHeightmapPos(
            net.minecraft.world.level.levelgen.Heightmap.Types.WORLD_SURFACE,
            searchPos
        );

        // Don't place in water or extreme elevations
        if (groundPos.getY() < 62 || groundPos.getY() > 100) {
            return;
        }

        LOGGER.info("Generating mall at chunk ({}, {}) - world pos {}", chunkX, chunkZ, groundPos);

        // Try to use MallStructure class
        try {
            com.wasteland.structures.MallStructure.generate(level, groundPos, RANDOM);
        } catch (Exception e) {
            // Fallback to simple structure
            LOGGER.warn("Could not use MallStructure, using fallback: {}", e.getMessage());
            placeMallFallback(level, groundPos);
        }
    }

    /**
     * Fallback mall structure (simple version)
     */
    private static void placeMallFallback(ServerLevel level, BlockPos pos) {
        // Large 15x15 structure
        // Floor
        for (int x = -7; x <= 7; x++) {
            for (int z = -7; z <= 7; z++) {
                level.setBlock(pos.offset(x, -1, z),
                    net.minecraft.world.level.block.Blocks.POLISHED_ANDESITE.defaultBlockState(), 3);
            }
        }

        // Walls (brick)
        for (int x = -7; x <= 7; x++) {
            for (int y = 0; y < 5; y++) {
                level.setBlock(pos.offset(x, y, -7),
                    net.minecraft.world.level.block.Blocks.BRICKS.defaultBlockState(), 3);
                level.setBlock(pos.offset(x, y, 7),
                    net.minecraft.world.level.block.Blocks.BRICKS.defaultBlockState(), 3);
            }
        }

        for (int z = -7; z <= 7; z++) {
            for (int y = 0; y < 5; y++) {
                level.setBlock(pos.offset(-7, y, z),
                    net.minecraft.world.level.block.Blocks.BRICKS.defaultBlockState(), 3);
                level.setBlock(pos.offset(7, y, z),
                    net.minecraft.world.level.block.Blocks.BRICKS.defaultBlockState(), 3);
            }
        }

        // Main entrance (south side)
        for (int y = 0; y < 3; y++) {
            level.setBlock(pos.offset(0, y, 7), net.minecraft.world.level.block.Blocks.AIR.defaultBlockState(), 3);
            level.setBlock(pos.offset(1, y, 7), net.minecraft.world.level.block.Blocks.AIR.defaultBlockState(), 3);
        }

        // Roof
        for (int x = -7; x <= 7; x++) {
            for (int z = -7; z <= 7; z++) {
                level.setBlock(pos.offset(x, 5, z),
                    net.minecraft.world.level.block.Blocks.QUARTZ_BLOCK.defaultBlockState(), 3);
            }
        }

        // Interior lighting
        level.setBlock(pos.offset(0, 3, 0), net.minecraft.world.level.block.Blocks.GLOWSTONE.defaultBlockState(), 3);
        level.setBlock(pos.offset(-4, 3, -4), net.minecraft.world.level.block.Blocks.GLOWSTONE.defaultBlockState(), 3);
        level.setBlock(pos.offset(4, 3, 4), net.minecraft.world.level.block.Blocks.GLOWSTONE.defaultBlockState(), 3);

        // Some loot chests
        level.setBlock(pos.offset(-5, 0, -5), net.minecraft.world.level.block.Blocks.CHEST.defaultBlockState(), 3);
        level.setBlock(pos.offset(5, 0, -5), net.minecraft.world.level.block.Blocks.CHEST.defaultBlockState(), 3);
    }

    /**
     * Generate a church structure
     */
    private static void generateChurch(ServerLevel level, int chunkX, int chunkZ) {
        // Convert chunk coordinates to world coordinates
        int worldX = (chunkX << 4) + RANDOM.nextInt(16);
        int worldZ = (chunkZ << 4) + RANDOM.nextInt(16);

        // Find ground level
        BlockPos searchPos = new BlockPos(worldX, 64, worldZ);
        BlockPos groundPos = level.getHeightmapPos(
            net.minecraft.world.level.levelgen.Heightmap.Types.WORLD_SURFACE,
            searchPos
        );

        // Don't place in water or extreme elevations
        if (groundPos.getY() < 62 || groundPos.getY() > 100) {
            return;
        }

        LOGGER.info("Generating church at chunk ({}, {}) - world pos {}", chunkX, chunkZ, groundPos);

        placeChurchStructure(level, groundPos);
    }

    /**
     * Place church structure
     */
    private static void placeChurchStructure(ServerLevel level, BlockPos pos) {
        // 11x11 church with steeple
        // Floor
        for (int x = -5; x <= 5; x++) {
            for (int z = -5; z <= 5; z++) {
                level.setBlock(pos.offset(x, -1, z),
                    net.minecraft.world.level.block.Blocks.STONE_BRICKS.defaultBlockState(), 3);
            }
        }

        // Walls (stone brick)
        for (int x = -5; x <= 5; x++) {
            for (int y = 0; y < 4; y++) {
                level.setBlock(pos.offset(x, y, -5),
                    net.minecraft.world.level.block.Blocks.STONE_BRICKS.defaultBlockState(), 3);
                level.setBlock(pos.offset(x, y, 5),
                    net.minecraft.world.level.block.Blocks.STONE_BRICKS.defaultBlockState(), 3);
            }
        }

        for (int z = -5; z <= 5; z++) {
            for (int y = 0; y < 4; y++) {
                level.setBlock(pos.offset(-5, y, z),
                    net.minecraft.world.level.block.Blocks.STONE_BRICKS.defaultBlockState(), 3);
                level.setBlock(pos.offset(5, y, z),
                    net.minecraft.world.level.block.Blocks.STONE_BRICKS.defaultBlockState(), 3);
            }
        }

        // Main entrance (south)
        level.setBlock(pos.offset(0, 0, 5), net.minecraft.world.level.block.Blocks.AIR.defaultBlockState(), 3);
        level.setBlock(pos.offset(0, 1, 5), net.minecraft.world.level.block.Blocks.AIR.defaultBlockState(), 3);
        level.setBlock(pos.offset(0, 2, 5), net.minecraft.world.level.block.Blocks.AIR.defaultBlockState(), 3);

        // Roof
        for (int x = -5; x <= 5; x++) {
            for (int z = -5; z <= 5; z++) {
                level.setBlock(pos.offset(x, 4, z),
                    net.minecraft.world.level.block.Blocks.DARK_OAK_PLANKS.defaultBlockState(), 3);
            }
        }

        // Steeple (center tower)
        for (int y = 5; y < 10; y++) {
            level.setBlock(pos.offset(0, y, 0), net.minecraft.world.level.block.Blocks.STONE_BRICKS.defaultBlockState(), 3);
        }
        // Cross on top
        level.setBlock(pos.offset(0, 10, 0), net.minecraft.world.level.block.Blocks.IRON_BARS.defaultBlockState(), 3);
        level.setBlock(pos.offset(0, 11, 0), net.minecraft.world.level.block.Blocks.IRON_BARS.defaultBlockState(), 3);
        level.setBlock(pos.offset(1, 11, 0), net.minecraft.world.level.block.Blocks.IRON_BARS.defaultBlockState(), 3);
        level.setBlock(pos.offset(-1, 11, 0), net.minecraft.world.level.block.Blocks.IRON_BARS.defaultBlockState(), 3);

        // Interior - altar and pews
        level.setBlock(pos.offset(0, 0, -4), net.minecraft.world.level.block.Blocks.CHISELED_QUARTZ_BLOCK.defaultBlockState(), 3);
        level.setBlock(pos.offset(0, 1, -4), net.minecraft.world.level.block.Blocks.CANDLE.defaultBlockState(), 3);

        // Lighting
        level.setBlock(pos.offset(0, 3, 0), net.minecraft.world.level.block.Blocks.GLOWSTONE.defaultBlockState(), 3);
    }

    /**
     * Clear cached chunks (for world reload)
     */
    public static void clearCache() {
        PROCESSED_CHUNKS.clear();
        PENDING_STRUCTURES.clear();
        LOGGER.info("Cleared worldgen cache");
    }
}
