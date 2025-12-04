package com.wasteland;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.Blocks;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Random;
import java.util.UUID;

/**
 * Generates dungeon entrance structures in the overworld
 * These are ruined buildings with portals leading underground
 */
public class DungeonEntrance {
    private static final Logger LOGGER = LogManager.getLogger();
    private static final Random RANDOM = new Random();

    public enum EntranceType {
        RUINED_BUILDING,    // Standard cracked stone structure
        COLLAPSED_BUNKER,   // Concrete bunker partially buried
        ABANDONED_SHACK     // Wooden wasteland shack
    }

    /**
     * Place a random entrance type at the specified location
     */
    public static void placeRandomEntrance(ServerLevel level, BlockPos pos) {
        placeRandomEntrance(level, pos, null);
    }

    /**
     * Place a random entrance type for a specific dungeon
     */
    public static void placeRandomEntrance(ServerLevel level, BlockPos pos, UUID dungeonId) {
        EntranceType[] types = EntranceType.values();
        EntranceType type = types[RANDOM.nextInt(types.length)];
        placeEntrance(level, pos, type, dungeonId);
    }

    /**
     * Place a dungeon entrance structure at the specified location
     *
     * Structure design (top view):
     * xxxxxxxxxxxxx
     * x...........x
     * x...........x
     * x.....P.....x  (P = portal down)
     * x...........x
     * x...........x
     * xxxxxxxxxxxxx
     *
     * This is a ruined building with cracked walls and a blue portal in the center
     */
    public static void placeEntrance(ServerLevel level, BlockPos pos) {
        placeEntrance(level, pos, EntranceType.RUINED_BUILDING, null);
    }

    /**
     * Place a dungeon entrance structure of a specific type
     */
    public static void placeEntrance(ServerLevel level, BlockPos pos, EntranceType type) {
        placeEntrance(level, pos, type, null);
    }

    /**
     * Place a dungeon entrance structure of a specific type for a specific dungeon
     */
    public static void placeEntrance(ServerLevel level, BlockPos pos, EntranceType type, UUID dungeonId) {
        LOGGER.info("═══════════════════════════════════════════════════════");
        LOGGER.info("  Building Dungeon Entrance Structure");
        LOGGER.info("  Type: {}", type);
        LOGGER.info("  Location: {}", pos);
        LOGGER.info("═══════════════════════════════════════════════════════");

        // Structure dimensions
        int width = 13;
        int depth = 13;
        int height = 5;  // Taller structure for wasteland aesthetic

        // Build the ruined structure
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                for (int z = 0; z < depth; z++) {
                    BlockPos buildPos = pos.offset(x, y, z);

                    boolean isWall = (x == 0 || x == width - 1 || z == 0 || z == depth - 1);
                    boolean isFloor = (y == 0);
                    boolean isCeiling = (y == height - 1);

                    if (isFloor) {
                        if (isWall) {
                            // Wall base - cracked stone bricks
                            level.setBlock(buildPos, Blocks.CRACKED_STONE_BRICKS.defaultBlockState(), 3);
                        } else {
                            // Floor - gray concrete (wasteland ground)
                            level.setBlock(buildPos, Blocks.GRAY_CONCRETE.defaultBlockState(), 3);
                        }
                    } else if (isCeiling) {
                        // Partially destroyed ceiling - mossy stone bricks
                        if (isWall || (x % 2 == 0 && z % 2 == 0)) {
                            level.setBlock(buildPos, Blocks.MOSSY_STONE_BRICKS.defaultBlockState(), 3);
                        } else {
                            level.setBlock(buildPos, Blocks.AIR.defaultBlockState(), 3);
                        }
                    } else {
                        // Middle layers
                        if (isWall) {
                            // Walls - mix of stone bricks and cracked variants (ruined look)
                            if ((x + z + y) % 3 == 0) {
                                level.setBlock(buildPos, Blocks.CRACKED_STONE_BRICKS.defaultBlockState(), 3);
                            } else if ((x + z + y) % 4 == 0) {
                                level.setBlock(buildPos, Blocks.MOSSY_STONE_BRICKS.defaultBlockState(), 3);
                            } else {
                                level.setBlock(buildPos, Blocks.STONE_BRICKS.defaultBlockState(), 3);
                            }
                        } else {
                            // Interior - air (open space)
                            level.setBlock(buildPos, Blocks.AIR.defaultBlockState(), 3);
                        }
                    }
                }
            }
        }

        // Add entrance doorway on one side
        BlockPos doorPos = pos.offset(6, 1, 0);
        level.setBlock(doorPos, Blocks.AIR.defaultBlockState(), 3);
        level.setBlock(doorPos.above(), Blocks.AIR.defaultBlockState(), 3);
        level.setBlock(doorPos.above(2), Blocks.AIR.defaultBlockState(), 3);

        // Place portal in center (blue wool = dungeon entrance)
        BlockPos portalPos = pos.offset(6, 1, 6);
        level.setBlock(portalPos, Blocks.BLUE_WOOL.defaultBlockState(), 3);

        // Register portal with PortalManager, linking it to the dungeon instance
        PortalManager.registerPortal(portalPos, PortalManager.PortalType.DUNGEON_ENTRANCE, null, dungeonId);

        // Add some wasteland decorations
        // Torches on walls (still working in ruins)
        level.setBlock(pos.offset(2, 2, 2), Blocks.TORCH.defaultBlockState(), 3);
        level.setBlock(pos.offset(width - 3, 2, 2), Blocks.TORCH.defaultBlockState(), 3);
        level.setBlock(pos.offset(2, 2, depth - 3), Blocks.TORCH.defaultBlockState(), 3);
        level.setBlock(pos.offset(width - 3, 2, depth - 3), Blocks.TORCH.defaultBlockState(), 3);

        // Add some debris (dead bushes for wasteland feel)
        level.setBlock(pos.offset(3, 1, 3), Blocks.DEAD_BUSH.defaultBlockState(), 3);
        level.setBlock(pos.offset(9, 1, 4), Blocks.DEAD_BUSH.defaultBlockState(), 3);

        // Sign to indicate entrance
        level.setBlock(pos.offset(6, 2, 1), Blocks.OAK_SIGN.defaultBlockState(), 3);

        LOGGER.info("  Dungeon entrance structure complete!");
        LOGGER.info("  Portal registered at: {}", portalPos);
        LOGGER.info("  Step on blue wool to enter the dungeon");
        LOGGER.info("═══════════════════════════════════════════════════════");
    }
}
