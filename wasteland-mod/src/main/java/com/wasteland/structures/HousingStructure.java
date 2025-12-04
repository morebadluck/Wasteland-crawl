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
 * Generates suburban houses for the wasteland.
 * Small structures (12x10x8) with 2 floors, overgrown and decayed.
 */
public class HousingStructure {

    // Block palette for houses
    private static final BlockState WALL = Blocks.OAK_PLANKS.defaultBlockState();
    private static final BlockState WALL_CRACKED = Blocks.STRIPPED_OAK_WOOD.defaultBlockState();
    private static final BlockState FLOOR = Blocks.OAK_PLANKS.defaultBlockState();
    private static final BlockState CEILING = Blocks.OAK_PLANKS.defaultBlockState();
    private static final BlockState WINDOW = Blocks.GLASS_PANE.defaultBlockState();
    private static final BlockState DOOR = Blocks.OAK_DOOR.defaultBlockState();
    private static final BlockState ROOF = Blocks.OAK_STAIRS.defaultBlockState();
    private static final BlockState FOUNDATION = Blocks.STONE_BRICKS.defaultBlockState();
    private static final BlockState FOUNDATION_CRACKED = Blocks.CRACKED_STONE_BRICKS.defaultBlockState();

    /**
     * Generate a suburban house at the given position
     */
    public static void generate(ServerLevel level, BlockPos origin, Random random) {
        // Dimensions: 12 wide (X), 10 deep (Z), 8 tall (Y)
        int width = 12;
        int depth = 10;
        int height = 8;

        // Clear area first
        clearArea(level, origin, width, depth, height + 2);

        // Generate foundation
        generateFoundation(level, origin, width, depth, random);

        // Generate first floor
        generateFirstFloor(level, origin.above(), width, depth, random);

        // Generate second floor
        generateSecondFloor(level, origin.above(4), width, depth, random);

        // Generate roof
        generateRoof(level, origin.above(7), width, depth, random);

        // Apply overgrown aesthetics
        applyOvergrowth(level, origin, width, depth, height, random);

        // Apply decay
        applyDecay(level, origin, width, depth, height, random);

        // Place loot
        placeLoot(level, origin, width, depth, random);

        // Spawn enemies
        spawnEnemies(level, origin, width, depth, random);
    }

    /**
     * Clear the area for the house
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
                // Mix cracked and normal foundation
                BlockState foundationBlock = random.nextFloat() < 0.3f ? FOUNDATION_CRACKED : FOUNDATION;
                level.setBlock(pos, foundationBlock, 2);
            }
        }
    }

    /**
     * Generate first floor (Living room, Kitchen, Dining)
     */
    private static void generateFirstFloor(ServerLevel level, BlockPos origin, int width, int depth, Random random) {
        // Exterior walls
        buildWalls(level, origin, width, depth, 3, random);

        // Floor
        for (int x = 0; x < width; x++) {
            for (int z = 0; z < depth; z++) {
                level.setBlock(origin.offset(x, 0, z), FLOOR, 2);
            }
        }

        // Interior wall dividing rooms (at z=5)
        for (int x = 0; x < width; x++) {
            BlockPos wallPos = origin.offset(x, 0, 5).above();
            if (x == 6) continue; // Doorway
            level.setBlock(wallPos, WALL, 2);
            level.setBlock(wallPos.above(), WALL, 2);
        }

        // Front door (center of front wall)
        BlockPos doorPos = origin.offset(width / 2, 0, 0).above();
        level.setBlock(doorPos, DOOR.setValue(BlockStateProperties.HORIZONTAL_FACING, Direction.SOUTH), 2);
        level.setBlock(doorPos.above(), DOOR.setValue(BlockStateProperties.HORIZONTAL_FACING, Direction.SOUTH)
            .setValue(BlockStateProperties.DOUBLE_BLOCK_HALF, net.minecraft.world.level.block.state.properties.DoubleBlockHalf.UPPER), 2);

        // Windows (4 windows)
        placeWindow(level, origin.offset(2, 0, 0).above(), Direction.SOUTH);
        placeWindow(level, origin.offset(9, 0, 0).above(), Direction.SOUTH);
        placeWindow(level, origin.offset(0, 0, 3).above(), Direction.WEST);
        placeWindow(level, origin.offset(width - 1, 0, 7).above(), Direction.EAST);

        // Furniture - Living room (front left)
        level.setBlock(origin.offset(2, 1, 2), Blocks.OAK_STAIRS.defaultBlockState(), 2);
        level.setBlock(origin.offset(3, 1, 2), Blocks.OAK_STAIRS.defaultBlockState(), 2);

        // Furniture - Kitchen (back left)
        level.setBlock(origin.offset(1, 1, 7), Blocks.CRAFTING_TABLE.defaultBlockState(), 2);
        level.setBlock(origin.offset(2, 1, 7), Blocks.FURNACE.defaultBlockState(), 2);

        // Furniture - Dining (back right)
        level.setBlock(origin.offset(8, 1, 7), Blocks.OAK_SLAB.defaultBlockState(), 2);
    }

    /**
     * Generate second floor (Bedrooms, Bathroom)
     */
    private static void generateSecondFloor(ServerLevel level, BlockPos origin, int width, int depth, Random random) {
        // Exterior walls
        buildWalls(level, origin, width, depth, 3, random);

        // Floor
        for (int x = 0; x < width; x++) {
            for (int z = 0; z < depth; z++) {
                level.setBlock(origin.offset(x, 0, z), FLOOR, 2);
            }
        }

        // Interior wall dividing bedrooms (at x=6)
        for (int z = 0; z < depth; z++) {
            BlockPos wallPos = origin.offset(6, 0, z).above();
            if (z == 5) continue; // Doorway
            level.setBlock(wallPos, WALL, 2);
            level.setBlock(wallPos.above(), WALL, 2);
        }

        // Windows
        placeWindow(level, origin.offset(2, 0, 0).above(), Direction.SOUTH);
        placeWindow(level, origin.offset(9, 0, 0).above(), Direction.SOUTH);

        // Furniture - Bedroom 1 (left side)
        level.setBlock(origin.offset(2, 1, 3), Blocks.RED_BED.defaultBlockState()
            .setValue(BlockStateProperties.HORIZONTAL_FACING, Direction.NORTH), 2);
        level.setBlock(origin.offset(2, 1, 4), Blocks.RED_BED.defaultBlockState()
            .setValue(BlockStateProperties.HORIZONTAL_FACING, Direction.NORTH)
            .setValue(net.minecraft.world.level.block.BedBlock.PART, net.minecraft.world.level.block.state.properties.BedPart.HEAD), 2);

        // Furniture - Bedroom 2 (right side)
        level.setBlock(origin.offset(9, 1, 3), Blocks.BLUE_BED.defaultBlockState()
            .setValue(BlockStateProperties.HORIZONTAL_FACING, Direction.NORTH), 2);
        level.setBlock(origin.offset(9, 1, 4), Blocks.BLUE_BED.defaultBlockState()
            .setValue(BlockStateProperties.HORIZONTAL_FACING, Direction.NORTH)
            .setValue(net.minecraft.world.level.block.BedBlock.PART, net.minecraft.world.level.block.state.properties.BedPart.HEAD), 2);

        // Bookshelf in hallway
        level.setBlock(origin.offset(6, 1, 8), Blocks.BOOKSHELF.defaultBlockState(), 2);
    }

    /**
     * Generate roof
     */
    private static void generateRoof(ServerLevel level, BlockPos origin, int width, int depth, Random random) {
        // Simple flat roof with some holes for decay
        for (int x = 0; x < width; x++) {
            for (int z = 0; z < depth; z++) {
                // 10% chance for hole in roof
                if (random.nextFloat() < 0.1f) {
                    continue;
                }
                level.setBlock(origin.offset(x, 0, z), CEILING, 2);
            }
        }
    }

    /**
     * Build exterior walls with windows
     */
    private static void buildWalls(ServerLevel level, BlockPos origin, int width, int depth, int height, Random random) {
        for (int y = 0; y < height; y++) {
            // North and South walls (along X axis)
            for (int x = 0; x < width; x++) {
                BlockPos northPos = origin.offset(x, y, 0);
                BlockPos southPos = origin.offset(x, y, depth - 1);

                // Mix in cracked walls
                BlockState wallBlock = random.nextFloat() < 0.2f ? WALL_CRACKED : WALL;

                level.setBlock(northPos, wallBlock, 2);
                level.setBlock(southPos, wallBlock, 2);
            }

            // East and West walls (along Z axis)
            for (int z = 1; z < depth - 1; z++) {
                BlockPos westPos = origin.offset(0, y, z);
                BlockPos eastPos = origin.offset(width - 1, y, z);

                BlockState wallBlock = random.nextFloat() < 0.2f ? WALL_CRACKED : WALL;

                level.setBlock(westPos, wallBlock, 2);
                level.setBlock(eastPos, wallBlock, 2);
            }
        }
    }

    /**
     * Place a window
     */
    private static void placeWindow(ServerLevel level, BlockPos pos, Direction facing) {
        level.setBlock(pos, Blocks.AIR.defaultBlockState(), 2); // Clear wall
        // 40% chance window is broken (air) instead of glass
        if (new Random().nextFloat() < 0.6f) {
            level.setBlock(pos, WINDOW, 2);
        }
    }

    /**
     * Apply overgrown aesthetics
     */
    private static void applyOvergrowth(ServerLevel level, BlockPos origin, int width, int depth, int height, Random random) {
        // Vines on exterior walls (40% coverage)
        for (int i = 0; i < (width * height + depth * height) * 0.4; i++) {
            int side = random.nextInt(4);
            int y = random.nextInt(height);

            BlockPos vinePos;
            if (side == 0) {
                vinePos = origin.offset(random.nextInt(width), y, -1);
            } else if (side == 1) {
                vinePos = origin.offset(random.nextInt(width), y, depth);
            } else if (side == 2) {
                vinePos = origin.offset(-1, y, random.nextInt(depth));
            } else {
                vinePos = origin.offset(width, y, random.nextInt(depth));
            }

            if (level.getBlockState(vinePos).isAir()) {
                level.setBlock(vinePos, Blocks.VINE.defaultBlockState(), 2);
            }
        }

        // Grass growing through floor (30% of tiles)
        for (int i = 0; i < (width * depth) * 0.3; i++) {
            int x = random.nextInt(width);
            int z = random.nextInt(depth);
            BlockPos grassPos = origin.offset(x, 1, z).above();

            if (level.getBlockState(grassPos).isAir()) {
                level.setBlock(grassPos, Blocks.GRASS.defaultBlockState(), 2);
            }
        }

        // Dead bushes scattered
        for (int i = 0; i < 5; i++) {
            int x = random.nextInt(width);
            int z = random.nextInt(depth);
            BlockPos bushPos = origin.offset(x, 1, z).above();

            if (level.getBlockState(bushPos).isAir()) {
                level.setBlock(bushPos, Blocks.DEAD_BUSH.defaultBlockState(), 2);
            }
        }
    }

    /**
     * Apply decay to structure
     */
    private static void applyDecay(ServerLevel level, BlockPos origin, int width, int depth, int height, Random random) {
        // Cracked blocks already mixed in during generation

        // Add some debris (cobblestone, gravel)
        for (int i = 0; i < 3; i++) {
            int x = random.nextInt(width);
            int z = random.nextInt(depth);
            int y = random.nextInt(height);
            BlockPos debrisPos = origin.offset(x, y, z);

            if (level.getBlockState(debrisPos).isAir()) {
                BlockState debris = random.nextBoolean() ? Blocks.COBBLESTONE.defaultBlockState() : Blocks.GRAVEL.defaultBlockState();
                level.setBlock(debrisPos, debris, 2);
            }
        }
    }

    /**
     * Place loot chests in logical locations
     */
    private static void placeLoot(ServerLevel level, BlockPos origin, int width, int depth, Random random) {
        // Chest in bedroom 1 (closet)
        if (random.nextBoolean()) {
            BlockPos chestPos = origin.above(5).offset(1, 0, 2);
            level.setBlock(chestPos, Blocks.CHEST.defaultBlockState()
                .setValue(ChestBlock.FACING, Direction.EAST), 2);
            // TODO: Fill with loot table
        }

        // Chest in bedroom 2 (under bed)
        if (random.nextBoolean()) {
            BlockPos chestPos = origin.above(5).offset(10, 0, 6);
            level.setBlock(chestPos, Blocks.CHEST.defaultBlockState()
                .setValue(ChestBlock.FACING, Direction.SOUTH), 2);
            // TODO: Fill with loot table
        }
    }

    /**
     * Spawn 1-3 enemies in the house
     */
    private static void spawnEnemies(ServerLevel level, BlockPos origin, int width, int depth, Random random) {
        int enemyCount = 1 + random.nextInt(3); // 1-3 enemies

        for (int i = 0; i < enemyCount; i++) {
            // Random floor (1st or 2nd)
            int floor = random.nextBoolean() ? 1 : 5;

            // Random position in house
            int x = 1 + random.nextInt(width - 2);
            int z = 1 + random.nextInt(depth - 2);

            BlockPos spawnPos = origin.offset(x, floor + 1, z);

            // 10% chance for ghost (more dangerous)
            // For now, use zombies as placeholder
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
