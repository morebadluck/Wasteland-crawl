package com.wasteland.structures;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;

import java.util.Random;

/**
 * Generates mall structures for the wasteland.
 * Large structures (60x40x20) with parking lot and portal entrances.
 * Exterior only - interior dungeons handled separately.
 */
public class MallStructure {

    // Block palette for malls
    private static final BlockState WALL = Blocks.STONE_BRICKS.defaultBlockState();
    private static final BlockState WALL_CRACKED = Blocks.CRACKED_STONE_BRICKS.defaultBlockState();
    private static final BlockState CONCRETE = Blocks.LIGHT_GRAY_CONCRETE.defaultBlockState();
    private static final BlockState CONCRETE_CRACKED = Blocks.COBBLESTONE.defaultBlockState();
    private static final BlockState WINDOW = Blocks.GLASS_PANE.defaultBlockState();
    private static final BlockState ROOF = Blocks.STONE_SLAB.defaultBlockState();
    private static final BlockState SIGN_BLOCK = Blocks.RED_CONCRETE.defaultBlockState();

    /**
     * Generate a mall at the given position
     */
    public static void generate(ServerLevel level, BlockPos origin, Random random) {
        // Total area: 60 wide (X), 40 deep (Z), 20 tall (Y)
        int totalWidth = 60;
        int totalDepth = 40;

        // Building: 40 wide, 30 deep, 20 tall (centered in back half)
        int buildingWidth = 40;
        int buildingDepth = 30;
        int buildingHeight = 20;
        int buildingX = 10; // Offset to center building
        int buildingZ = 10; // Offset to place building in back

        // Clear entire area
        clearArea(level, origin, totalWidth, totalDepth, buildingHeight + 5);

        // Generate parking lot (front area)
        generateParkingLot(level, origin, totalWidth, buildingZ + buildingDepth, random);

        // Generate main building
        BlockPos buildingOrigin = origin.offset(buildingX, 0, buildingZ);
        generateBuilding(level, buildingOrigin, buildingWidth, buildingDepth, buildingHeight, random);

        // Generate sign
        generateSign(level, buildingOrigin, buildingWidth, buildingHeight, random);

        // Generate portal entrances
        generatePortalEntrances(level, buildingOrigin, buildingWidth, buildingDepth, random);

        // Apply overgrowth and decay
        applyOvergrowth(level, origin, totalWidth, totalDepth, buildingHeight, random);
        applyDecay(level, buildingOrigin, buildingWidth, buildingDepth, buildingHeight, random);

        // Spawn exterior enemies
        spawnEnemies(level, origin, totalWidth, totalDepth, random);
    }

    /**
     * Clear the entire area
     */
    private static void clearArea(ServerLevel level, BlockPos origin, int width, int depth, int height) {
        for (int x = -5; x < width + 5; x++) {
            for (int z = -5; z < depth + 5; z++) {
                for (int y = 0; y < height; y++) {
                    BlockPos pos = origin.offset(x, y, z);
                    if (!level.getBlockState(pos).isAir()) {
                        level.setBlock(pos, Blocks.AIR.defaultBlockState(), 2);
                    }
                }
            }
        }
    }

    /**
     * Generate parking lot with overgrown cracked concrete
     */
    private static void generateParkingLot(ServerLevel level, BlockPos origin, int width, int depth, Random random) {
        for (int x = 0; x < width; x++) {
            for (int z = 0; z < depth; z++) {
                BlockPos pos = origin.offset(x, 0, z);

                // Parking lot surface: 50% concrete, 40% grass/dirt (overgrown), 10% gravel
                float roll = random.nextFloat();
                BlockState surface;
                if (roll < 0.5f) {
                    // Concrete (30% cracked)
                    surface = random.nextFloat() < 0.3f ? CONCRETE_CRACKED : CONCRETE;
                } else if (roll < 0.9f) {
                    // Overgrown
                    surface = random.nextBoolean() ? Blocks.GRASS_BLOCK.defaultBlockState() : Blocks.DIRT.defaultBlockState();
                } else {
                    // Gravel
                    surface = Blocks.GRAVEL.defaultBlockState();
                }
                level.setBlock(pos, surface, 2);
            }
        }

        // Parking spaces (faded white wool lines)
        for (int row = 0; row < 3; row++) {
            int z = 2 + (row * 4);
            for (int col = 0; col < 8; col++) {
                int x = 5 + (col * 7);

                // 60% chance space still visible
                if (random.nextFloat() < 0.6f) {
                    // Parking space outline
                    level.setBlock(origin.offset(x, 1, z), Blocks.WHITE_WOOL.defaultBlockState(), 2);
                    level.setBlock(origin.offset(x + 5, 1, z), Blocks.WHITE_WOOL.defaultBlockState(), 2);
                    level.setBlock(origin.offset(x, 1, z + 3), Blocks.WHITE_WOOL.defaultBlockState(), 2);
                    level.setBlock(origin.offset(x + 5, 1, z + 3), Blocks.WHITE_WOOL.defaultBlockState(), 2);
                }
            }
        }

        // Abandoned vehicles (iron blocks as cars)
        for (int i = 0; i < 4; i++) {
            int x = 10 + random.nextInt(40);
            int z = 2 + random.nextInt(depth - 2);
            BlockPos carPos = origin.offset(x, 1, z);

            // Car body (2x4)
            level.setBlock(carPos, Blocks.IRON_BLOCK.defaultBlockState(), 2);
            level.setBlock(carPos.offset(1, 0, 0), Blocks.IRON_BLOCK.defaultBlockState(), 2);
            level.setBlock(carPos.offset(0, 0, 1), Blocks.IRON_BLOCK.defaultBlockState(), 2);
            level.setBlock(carPos.offset(1, 0, 1), Blocks.IRON_BLOCK.defaultBlockState(), 2);
        }
    }

    /**
     * Generate main building structure
     */
    private static void generateBuilding(ServerLevel level, BlockPos origin, int width, int depth,
                                        int height, Random random) {
        // Foundation
        for (int x = 0; x < width; x++) {
            for (int z = 0; z < depth; z++) {
                level.setBlock(origin.offset(x, 0, z), CONCRETE, 2);
            }
        }

        // Exterior walls
        buildWalls(level, origin, width, depth, height, random);

        // Floor (interior)
        for (int x = 1; x < width - 1; x++) {
            for (int z = 1; z < depth - 1; z++) {
                level.setBlock(origin.offset(x, 1, z), CONCRETE, 2);
            }
        }

        // Roof
        for (int x = 0; x < width; x++) {
            for (int z = 0; z < depth; z++) {
                // 20% holes in roof
                if (random.nextFloat() < 0.2f) continue;
                level.setBlock(origin.offset(x, height - 1, z), ROOF, 2);
            }
        }

        // Windows along walls (many broken)
        placeWindows(level, origin, width, depth, height, random);
    }

    /**
     * Build exterior walls
     */
    private static void buildWalls(ServerLevel level, BlockPos origin, int width, int depth,
                                   int height, Random random) {
        for (int y = 1; y < height - 1; y++) {
            // North and South walls (along X axis)
            for (int x = 0; x < width; x++) {
                BlockPos northPos = origin.offset(x, y, 0);
                BlockPos southPos = origin.offset(x, y, depth - 1);

                // 30% cracked
                BlockState wallBlock = random.nextFloat() < 0.3f ? WALL_CRACKED : WALL;

                level.setBlock(northPos, wallBlock, 2);
                level.setBlock(southPos, wallBlock, 2);
            }

            // East and West walls (along Z axis)
            for (int z = 1; z < depth - 1; z++) {
                BlockPos westPos = origin.offset(0, y, z);
                BlockPos eastPos = origin.offset(width - 1, y, z);

                BlockState wallBlock = random.nextFloat() < 0.3f ? WALL_CRACKED : WALL;

                level.setBlock(westPos, wallBlock, 2);
                level.setBlock(eastPos, wallBlock, 2);
            }
        }
    }

    /**
     * Place windows (40% broken)
     */
    private static void placeWindows(ServerLevel level, BlockPos origin, int width, int depth,
                                     int height, Random random) {
        // Front windows (storefront glass)
        for (int x = 5; x < width - 5; x += 2) {
            for (int y = 2; y < Math.min(6, height - 2); y++) {
                BlockPos windowPos = origin.offset(x, y, 0);
                level.setBlock(windowPos, Blocks.AIR.defaultBlockState(), 2);

                // 60% have glass (40% broken)
                if (random.nextFloat() < 0.6f) {
                    level.setBlock(windowPos, WINDOW, 2);
                }
            }
        }

        // Side windows (fewer)
        for (int z = 5; z < depth - 5; z += 4) {
            int y = height / 2;

            // West wall
            level.setBlock(origin.offset(0, y, z), Blocks.AIR.defaultBlockState(), 2);
            if (random.nextFloat() < 0.5f) {
                level.setBlock(origin.offset(0, y, z), WINDOW, 2);
            }

            // East wall
            level.setBlock(origin.offset(width - 1, y, z), Blocks.AIR.defaultBlockState(), 2);
            if (random.nextFloat() < 0.5f) {
                level.setBlock(origin.offset(width - 1, y, z), WINDOW, 2);
            }
        }
    }

    /**
     * Generate broken sign
     */
    private static void generateSign(ServerLevel level, BlockPos origin, int width, int height, Random random) {
        // Sign above entrance
        int signY = height - 3;
        int signStartX = (width / 2) - 6;

        // "MALL" sign (partially broken)
        for (int x = 0; x < 12; x++) {
            // 70% of sign still intact
            if (random.nextFloat() < 0.7f) {
                level.setBlock(origin.offset(signStartX + x, signY, -1), SIGN_BLOCK, 2);
            }
        }
    }

    /**
     * Generate portal entrances (2-4 entrances)
     */
    private static void generatePortalEntrances(ServerLevel level, BlockPos origin, int width,
                                                int depth, Random random) {
        int entranceCount = 2 + random.nextInt(3); // 2-4 entrances

        for (int i = 0; i < entranceCount; i++) {
            // Distribute entrances along front wall
            int entranceX = 10 + (i * (width - 20) / Math.max(1, entranceCount - 1));

            // Create entrance opening (4 blocks wide, 4 blocks tall)
            for (int x = 0; x < 4; x++) {
                for (int y = 1; y < 5; y++) {
                    BlockPos doorPos = origin.offset(entranceX + x, y, 0);
                    level.setBlock(doorPos, Blocks.AIR.defaultBlockState(), 2);
                }
            }

            // Portal blocks (purple) at entrance
            BlockPos portalPos = origin.offset(entranceX + 1, 1, 0);
            level.setBlock(portalPos, Blocks.NETHER_PORTAL.defaultBlockState(), 2);
            level.setBlock(portalPos.above(), Blocks.NETHER_PORTAL.defaultBlockState(), 2);
            level.setBlock(portalPos.above(2), Blocks.NETHER_PORTAL.defaultBlockState(), 2);
            level.setBlock(portalPos.offset(1, 0, 0), Blocks.NETHER_PORTAL.defaultBlockState(), 2);
            level.setBlock(portalPos.offset(1, 1, 0), Blocks.NETHER_PORTAL.defaultBlockState(), 2);
            level.setBlock(portalPos.offset(1, 2, 0), Blocks.NETHER_PORTAL.defaultBlockState(), 2);

            // TODO: Register portal for dungeon access
        }
    }

    /**
     * Apply overgrowth aesthetics
     */
    private static void applyOvergrowth(ServerLevel level, BlockPos origin, int width, int depth,
                                       int height, Random random) {
        // Vines on building exterior
        int buildingX = 10;
        int buildingZ = 10;
        int buildingWidth = 40;
        int buildingDepth = 30;

        for (int i = 0; i < buildingWidth * height; i++) {
            int side = random.nextInt(4);
            int y = 1 + random.nextInt(height - 1);

            BlockPos vinePos;
            if (side == 0) {
                vinePos = origin.offset(buildingX + random.nextInt(buildingWidth), y, buildingZ - 1);
            } else if (side == 1) {
                vinePos = origin.offset(buildingX + random.nextInt(buildingWidth), y, buildingZ + buildingDepth);
            } else if (side == 2) {
                vinePos = origin.offset(buildingX - 1, y, buildingZ + random.nextInt(buildingDepth));
            } else {
                vinePos = origin.offset(buildingX + buildingWidth, y, buildingZ + random.nextInt(buildingDepth));
            }

            if (level.getBlockState(vinePos).isAir()) {
                level.setBlock(vinePos, Blocks.VINE.defaultBlockState(), 2);
            }
        }

        // Trees breaking through parking lot
        for (int i = 0; i < 3; i++) {
            int x = 10 + random.nextInt(width - 20);
            int z = 2 + random.nextInt(buildingZ - 2);
            BlockPos treePos = origin.offset(x, 1, z);

            // Simple tree (oak log + leaves)
            for (int h = 0; h < 4; h++) {
                level.setBlock(treePos.above(h), Blocks.OAK_LOG.defaultBlockState(), 2);
            }
            for (int dx = -1; dx <= 1; dx++) {
                for (int dz = -1; dz <= 1; dz++) {
                    level.setBlock(treePos.offset(dx, 4, dz), Blocks.OAK_LEAVES.defaultBlockState(), 2);
                }
            }
        }

        // Dead bushes scattered in parking lot
        for (int i = 0; i < 20; i++) {
            int x = random.nextInt(width);
            int z = random.nextInt(buildingZ);
            BlockPos bushPos = origin.offset(x, 1, z);

            if (level.getBlockState(bushPos).isAir()) {
                level.setBlock(bushPos, Blocks.DEAD_BUSH.defaultBlockState(), 2);
            }
        }
    }

    /**
     * Apply decay to building
     */
    private static void applyDecay(ServerLevel level, BlockPos origin, int width, int depth,
                                  int height, Random random) {
        // Rubble piles around building
        for (int i = 0; i < 10; i++) {
            int x = random.nextInt(width);
            int z = random.nextInt(depth);
            BlockPos rubblePos = origin.offset(x, 1, z);

            if (level.getBlockState(rubblePos).isAir()) {
                level.setBlock(rubblePos, Blocks.COBBLESTONE.defaultBlockState(), 2);
                if (random.nextBoolean()) {
                    level.setBlock(rubblePos.above(), Blocks.GRAVEL.defaultBlockState(), 2);
                }
            }
        }
    }

    /**
     * Spawn enemies in parking lot and around building
     */
    private static void spawnEnemies(ServerLevel level, BlockPos origin, int width, int depth, Random random) {
        // 5-8 enemies patrolling exterior
        int enemyCount = 5 + random.nextInt(4);

        for (int i = 0; i < enemyCount; i++) {
            int x = 5 + random.nextInt(width - 10);
            int z = 2 + random.nextInt(depth - 4);
            BlockPos spawnPos = origin.offset(x, 1, z);

            // TODO: Use custom mob types when available
            Zombie enemy = EntityType.ZOMBIE.create(level);
            if (enemy != null) {
                enemy.moveTo(spawnPos.getX() + 0.5, spawnPos.getY(), spawnPos.getZ() + 0.5,
                    random.nextFloat() * 360, 0);
                level.addFreshEntity(enemy);
            }
        }
    }
}
