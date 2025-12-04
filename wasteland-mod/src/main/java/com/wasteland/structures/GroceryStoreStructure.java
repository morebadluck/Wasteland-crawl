package com.wasteland.structures;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.ChestBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;

import java.util.Random;

/**
 * Generates grocery stores for the wasteland.
 * Medium structures (30x20x12) with aisles, checkout, and storage room.
 */
public class GroceryStoreStructure {

    // Block palette for grocery stores
    private static final BlockState WALL = Blocks.STONE_BRICKS.defaultBlockState();
    private static final BlockState WALL_CRACKED = Blocks.CRACKED_STONE_BRICKS.defaultBlockState();
    private static final BlockState FLOOR = Blocks.SMOOTH_STONE.defaultBlockState();
    private static final BlockState FLOOR_CRACKED = Blocks.COBBLESTONE.defaultBlockState();
    private static final BlockState CEILING = Blocks.SMOOTH_STONE_SLAB.defaultBlockState();
    private static final BlockState WINDOW = Blocks.GLASS_PANE.defaultBlockState();
    private static final BlockState DOOR = Blocks.IRON_DOOR.defaultBlockState();
    private static final BlockState SHELF = Blocks.BARREL.defaultBlockState();
    private static final BlockState FOUNDATION = Blocks.SMOOTH_STONE.defaultBlockState();

    /**
     * Generate a grocery store at the given position
     */
    public static void generate(ServerLevel level, BlockPos origin, Random random) {
        // Dimensions: 30 wide (X), 20 deep (Z), 12 tall (Y)
        int width = 30;
        int depth = 20;
        int height = 12;

        // Clear area first
        clearArea(level, origin, width, depth, height + 2);

        // Generate foundation
        generateFoundation(level, origin, width, depth, random);

        // Generate interior
        generateInterior(level, origin.above(), width, depth, height, random);

        // Generate roof
        generateRoof(level, origin.above(height - 1), width, depth, random);

        // Generate aisles
        generateAisles(level, origin.above(), width, depth, random);

        // Generate checkout area
        generateCheckout(level, origin.above(), random);

        // Generate storage room (boss area)
        generateStorage(level, origin.above(), width, random);

        // Apply overgrowth aesthetics
        applyOvergrowth(level, origin, width, depth, height, random);

        // Apply heavy decay
        applyDecay(level, origin, width, depth, height, random);

        // Place loot
        placeLoot(level, origin, width, depth, random);

        // Spawn enemies
        spawnEnemies(level, origin, width, depth, random);
    }

    /**
     * Clear the area for the store
     */
    private static void clearArea(ServerLevel level, BlockPos origin, int width, int depth, int height) {
        for (int x = -1; x < width + 1; x++) {
            for (int z = -1; z < depth + 1; z++) {
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
     * Generate the foundation
     */
    private static void generateFoundation(ServerLevel level, BlockPos origin, int width, int depth, Random random) {
        for (int x = 0; x < width; x++) {
            for (int z = 0; z < depth; z++) {
                BlockPos pos = origin.offset(x, 0, z);
                level.setBlock(pos, FOUNDATION, 2);
            }
        }
    }

    /**
     * Generate interior walls and floor
     * Layout:
     * ┌─────────────────────────────────┐
     * │ Produce  │ Aisles  │  Frozen   │
     * │   (L)    │ ══════  │   Foods   │
     * ├──────────┼─────────┼───────────┤
     * │ Checkout │ Aisles  │ Storage   │
     * │   Area   │ ══════  │  (BOSS)   │
     * └─────────────────────────────────┘
     */
    private static void generateInterior(ServerLevel level, BlockPos origin, int width, int depth, int height, Random random) {
        // Build exterior walls
        buildWalls(level, origin, width, depth, height - 2, random);

        // Floor (60% cracked)
        for (int x = 0; x < width; x++) {
            for (int z = 0; z < depth; z++) {
                BlockState floorBlock = random.nextFloat() < 0.6f ? FLOOR_CRACKED : FLOOR;
                level.setBlock(origin.offset(x, 0, z), floorBlock, 2);
            }
        }

        // Storage room wall (back right corner, x=20-30, z=10-20)
        for (int z = 10; z < depth; z++) {
            if (z == 15) continue; // Doorway
            BlockPos wallPos = origin.offset(20, 1, z);
            level.setBlock(wallPos, WALL, 2);
            level.setBlock(wallPos.above(), WALL, 2);
            level.setBlock(wallPos.above(2), WALL, 2);
        }

        // Produce section divider (left side, x=7)
        for (int z = 0; z < 10; z++) {
            if (z == 5) continue; // Opening
            BlockPos wallPos = origin.offset(7, 1, z);
            level.setBlock(wallPos, Blocks.OAK_FENCE.defaultBlockState(), 2);
        }

        // Frozen section divider (right side, before storage)
        for (int z = 0; z < 10; z++) {
            if (z == 5) continue; // Opening
            BlockPos wallPos = origin.offset(19, 1, z);
            level.setBlock(wallPos, Blocks.OAK_FENCE.defaultBlockState(), 2);
        }

        // Main entrance (front center)
        BlockPos doorPos = origin.offset(width / 2, 0, 0);
        level.setBlock(doorPos.above(), DOOR.setValue(BlockStateProperties.HORIZONTAL_FACING, Direction.SOUTH), 2);
        level.setBlock(doorPos.above(2), DOOR.setValue(BlockStateProperties.HORIZONTAL_FACING, Direction.SOUTH)
            .setValue(BlockStateProperties.DOUBLE_BLOCK_HALF, net.minecraft.world.level.block.state.properties.DoubleBlockHalf.UPPER), 2);

        // Side entrance (for loading bay)
        BlockPos sideEntry = origin.offset(width - 1, 0, depth - 5);
        level.setBlock(sideEntry.above(), Blocks.AIR.defaultBlockState(), 2);
        level.setBlock(sideEntry.above(2), Blocks.AIR.defaultBlockState(), 2);

        // Windows - all broken
        placeWindows(level, origin, width, depth, random);
    }

    /**
     * Place windows on exterior walls
     */
    private static void placeWindows(ServerLevel level, BlockPos origin, int width, int depth, Random random) {
        // Front windows (many along storefront)
        for (int x = 2; x < width - 2; x += 3) {
            placeWindow(level, origin.offset(x, 2, 0), Direction.NORTH, random);
        }

        // Side windows
        for (int z = 3; z < 10; z += 3) {
            placeWindow(level, origin.offset(0, 2, z), Direction.WEST, random);
            placeWindow(level, origin.offset(width - 1, 2, z), Direction.EAST, random);
        }
    }

    /**
     * Place a window (all broken)
     */
    private static void placeWindow(ServerLevel level, BlockPos pos, Direction facing, Random random) {
        level.setBlock(pos, Blocks.AIR.defaultBlockState(), 2); // All windows broken
    }

    /**
     * Generate aisles with shelving
     */
    private static void generateAisles(ServerLevel level, BlockPos origin, int width, int depth, Random random) {
        // 5 aisles running north-south in the center area
        for (int aisle = 0; aisle < 5; aisle++) {
            int x = 9 + (aisle * 2);

            for (int z = 2; z < 18; z++) {
                // 60% chance shelf is knocked over or empty
                if (random.nextFloat() > 0.4f) {
                    continue;
                }

                // Place shelf (barrel representing shelving unit)
                BlockPos shelfPos = origin.offset(x, 1, z);
                level.setBlock(shelfPos, SHELF, 2);

                // Occasionally stack two high
                if (random.nextFloat() < 0.3f) {
                    level.setBlock(shelfPos.above(), SHELF, 2);
                }
            }
        }
    }

    /**
     * Generate checkout area (front left)
     */
    private static void generateCheckout(ServerLevel level, BlockPos origin, Random random) {
        // Checkout counters (x=1-6, z=11-15)
        for (int lane = 0; lane < 3; lane++) {
            int x = 2 + (lane * 2);
            int z = 12;

            // Counter
            level.setBlock(origin.offset(x, 1, z), Blocks.STONE_BRICK_STAIRS.defaultBlockState()
                .setValue(BlockStateProperties.HORIZONTAL_FACING, Direction.NORTH), 2);
            level.setBlock(origin.offset(x, 1, z + 1), Blocks.STONE_BRICK_STAIRS.defaultBlockState()
                .setValue(BlockStateProperties.HORIZONTAL_FACING, Direction.NORTH), 2);
        }

        // Scattered shopping carts (minecart as proxy)
        for (int i = 0; i < 3; i++) {
            int x = 1 + random.nextInt(6);
            int z = 16 + random.nextInt(3);
            BlockPos cartPos = origin.offset(x, 1, z);
            if (level.getBlockState(cartPos).isAir()) {
                level.setBlock(cartPos, Blocks.IRON_BARS.defaultBlockState(), 2);
            }
        }
    }

    /**
     * Generate storage room (boss area)
     */
    private static void generateStorage(ServerLevel level, BlockPos origin, int width, Random random) {
        // Storage room is x=21-29, z=11-19

        // Shelving units and boxes
        for (int i = 0; i < 8; i++) {
            int x = 21 + random.nextInt(8);
            int z = 11 + random.nextInt(8);
            BlockPos boxPos = origin.offset(x, 1, z);

            if (level.getBlockState(boxPos).isAir()) {
                // Mix of barrels and chests
                if (random.nextBoolean()) {
                    level.setBlock(boxPos, Blocks.BARREL.defaultBlockState(), 2);
                } else {
                    level.setBlock(boxPos, Blocks.CHEST.defaultBlockState()
                        .setValue(ChestBlock.FACING, Direction.values()[2 + random.nextInt(4)]), 2);
                }
            }
        }

        // Refrigeration units (ice blocks)
        level.setBlock(origin.offset(22, 1, 18), Blocks.PACKED_ICE.defaultBlockState(), 2);
        level.setBlock(origin.offset(23, 1, 18), Blocks.PACKED_ICE.defaultBlockState(), 2);
        level.setBlock(origin.offset(24, 1, 18), Blocks.PACKED_ICE.defaultBlockState(), 2);
    }

    /**
     * Generate roof with many holes
     */
    private static void generateRoof(ServerLevel level, BlockPos origin, int width, int depth, Random random) {
        // Roof with many holes (exposed wiring/decay)
        for (int x = 0; x < width; x++) {
            for (int z = 0; z < depth; z++) {
                // 30% chance for hole in roof
                if (random.nextFloat() < 0.3f) {
                    continue;
                }
                level.setBlock(origin.offset(x, 0, z), CEILING, 2);
            }
        }
    }

    /**
     * Build exterior walls
     */
    private static void buildWalls(ServerLevel level, BlockPos origin, int width, int depth, int height, Random random) {
        for (int y = 0; y < height; y++) {
            // North and South walls (along X axis)
            for (int x = 0; x < width; x++) {
                BlockPos northPos = origin.offset(x, y, 0);
                BlockPos southPos = origin.offset(x, y, depth - 1);

                // Mix in cracked walls (40%)
                BlockState wallBlock = random.nextFloat() < 0.4f ? WALL_CRACKED : WALL;

                level.setBlock(northPos, wallBlock, 2);
                level.setBlock(southPos, wallBlock, 2);
            }

            // East and West walls (along Z axis)
            for (int z = 1; z < depth - 1; z++) {
                BlockPos westPos = origin.offset(0, y, z);
                BlockPos eastPos = origin.offset(width - 1, y, z);

                BlockState wallBlock = random.nextFloat() < 0.4f ? WALL_CRACKED : WALL;

                level.setBlock(westPos, wallBlock, 2);
                level.setBlock(eastPos, wallBlock, 2);
            }
        }
    }

    /**
     * Apply overgrown aesthetics
     */
    private static void applyOvergrowth(ServerLevel level, BlockPos origin, int width, int depth, int height, Random random) {
        // Vines through roof holes
        for (int i = 0; i < (width * depth) * 0.2; i++) {
            int x = random.nextInt(width);
            int z = random.nextInt(depth);
            BlockPos vinePos = origin.offset(x, height - 2, z);

            if (level.getBlockState(vinePos).isAir()) {
                level.setBlock(vinePos, Blocks.VINE.defaultBlockState(), 2);
            }
        }

        // Grass and weeds growing through floor
        for (int i = 0; i < (width * depth) * 0.25; i++) {
            int x = random.nextInt(width);
            int z = random.nextInt(depth);
            BlockPos grassPos = origin.offset(x, 1, z);

            if (level.getBlockState(grassPos).isAir()) {
                if (random.nextFloat() < 0.7f) {
                    level.setBlock(grassPos, Blocks.GRASS.defaultBlockState(), 2);
                } else {
                    level.setBlock(grassPos, Blocks.DEAD_BUSH.defaultBlockState(), 2);
                }
            }
        }
    }

    /**
     * Apply heavy decay to structure
     */
    private static void applyDecay(ServerLevel level, BlockPos origin, int width, int depth, int height, Random random) {
        // Scattered products/debris on floor
        for (int i = 0; i < 40; i++) {
            int x = random.nextInt(width);
            int z = random.nextInt(depth);
            BlockPos debrisPos = origin.offset(x, 1, z);

            if (level.getBlockState(debrisPos).isAir()) {
                // Various debris types
                float roll = random.nextFloat();
                BlockState debris;
                if (roll < 0.3f) {
                    debris = Blocks.COBBLESTONE.defaultBlockState();
                } else if (roll < 0.5f) {
                    debris = Blocks.GRAVEL.defaultBlockState();
                } else if (roll < 0.7f) {
                    debris = Blocks.DIRT.defaultBlockState();
                } else {
                    // "Products" (colored wool as proxy)
                    debris = Blocks.WHITE_WOOL.defaultBlockState();
                }
                level.setBlock(debrisPos, debris, 2);
            }
        }

        // Collapsed ceiling sections
        for (int i = 0; i < 5; i++) {
            int x = random.nextInt(width);
            int z = random.nextInt(depth);
            BlockPos collapsePos = origin.offset(x, 1, z);

            // Pile of rubble
            if (level.getBlockState(collapsePos).isAir()) {
                level.setBlock(collapsePos, Blocks.COBBLESTONE.defaultBlockState(), 2);
                if (random.nextBoolean()) {
                    level.setBlock(collapsePos.above(), Blocks.GRAVEL.defaultBlockState(), 2);
                }
            }
        }
    }

    /**
     * Place loot chests in logical locations
     */
    private static void placeLoot(ServerLevel level, BlockPos origin, int width, int depth, Random random) {
        // Manager's office (storage room)
        BlockPos managerChest = origin.above().offset(28, 1, 12);
        level.setBlock(managerChest, Blocks.CHEST.defaultBlockState()
            .setValue(ChestBlock.FACING, Direction.WEST), 2);

        // Hidden behind checkout
        if (random.nextFloat() < 0.7f) {
            BlockPos checkoutChest = origin.above().offset(4, 1, 10);
            level.setBlock(checkoutChest, Blocks.CHEST.defaultBlockState()
                .setValue(ChestBlock.FACING, Direction.NORTH), 2);
        }

        // Pharmacy section (medical supplies)
        if (random.nextFloat() < 0.6f) {
            BlockPos pharmacyChest = origin.above().offset(1, 1, 2);
            level.setBlock(pharmacyChest, Blocks.CHEST.defaultBlockState()
                .setValue(ChestBlock.FACING, Direction.EAST), 2);
        }
    }

    /**
     * Spawn enemies throughout the store
     */
    private static void spawnEnemies(ServerLevel level, BlockPos origin, int width, int depth, Random random) {
        // Goblins in aisles (3-5)
        int goblinCount = 3 + random.nextInt(3);
        for (int i = 0; i < goblinCount; i++) {
            int x = 8 + random.nextInt(12);
            int z = 2 + random.nextInt(16);
            spawnEnemy(level, origin.offset(x, 2, z), random);
        }

        // Rats near produce/trash (2-3)
        int ratCount = 2 + random.nextInt(2);
        for (int i = 0; i < ratCount; i++) {
            int x = 1 + random.nextInt(6);
            int z = 1 + random.nextInt(8);
            spawnEnemy(level, origin.offset(x, 2, z), random);
        }

        // Bandits at checkout (1-2)
        int banditCount = 1 + random.nextInt(2);
        for (int i = 0; i < banditCount; i++) {
            int x = 1 + random.nextInt(6);
            int z = 11 + random.nextInt(7);
            spawnEnemy(level, origin.offset(x, 2, z), random);
        }

        // Boss in storage room (Bandit Leader or Giant Rat)
        BlockPos bossPos = origin.offset(25, 2, 15);
        spawnEnemy(level, bossPos, random);
    }

    /**
     * Helper method to spawn an enemy
     */
    private static void spawnEnemy(ServerLevel level, BlockPos pos, Random random) {
        // TODO: Use custom mob types when available (Goblins, Rats, Bandits)
        Zombie enemy = EntityType.ZOMBIE.create(level);
        if (enemy != null) {
            enemy.moveTo(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5,
                random.nextFloat() * 360, 0);
            level.addFreshEntity(enemy);
        }
    }
}
