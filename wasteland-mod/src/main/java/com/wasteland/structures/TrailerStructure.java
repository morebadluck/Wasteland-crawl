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
 * Generates trailer/mobile homes for the wasteland.
 * Small compact structures (8x15x6) with rusty exterior and sparse furniture.
 */
public class TrailerStructure {

    // Block palette for trailers - rusty and weathered
    private static final BlockState WALL = Blocks.LIGHT_GRAY_TERRACOTTA.defaultBlockState();
    private static final BlockState WALL_RUSTY = Blocks.GRAY_TERRACOTTA.defaultBlockState();
    private static final BlockState FLOOR = Blocks.SMOOTH_STONE.defaultBlockState();
    private static final BlockState CEILING = Blocks.LIGHT_GRAY_CONCRETE.defaultBlockState();
    private static final BlockState WINDOW = Blocks.GLASS_PANE.defaultBlockState();
    private static final BlockState DOOR = Blocks.IRON_DOOR.defaultBlockState();
    private static final BlockState FOUNDATION = Blocks.SMOOTH_STONE_SLAB.defaultBlockState();

    /**
     * Generate a trailer/mobile home at the given position
     */
    public static void generate(ServerLevel level, BlockPos origin, Random random) {
        // Dimensions: 8 wide (X), 15 deep (Z), 6 tall (Y)
        int width = 8;
        int depth = 15;
        int height = 6;

        // Clear area first
        clearArea(level, origin, width, depth, height + 2);

        // Generate foundation (concrete blocks or slabs)
        generateFoundation(level, origin, width, depth, random);

        // Generate interior
        generateInterior(level, origin.above(), width, depth, random);

        // Generate roof
        generateRoof(level, origin.above(height - 1), width, depth, random);

        // Apply overgrowth aesthetics
        applyOvergrowth(level, origin, width, depth, height, random);

        // Apply decay
        applyDecay(level, origin, width, depth, height, random);

        // Place loot
        placeLoot(level, origin, width, depth, random);

        // Spawn enemies
        spawnEnemies(level, origin, width, depth, random);
    }

    /**
     * Clear the area for the trailer
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
     * Generate the foundation - raised on blocks/slabs
     */
    private static void generateFoundation(ServerLevel level, BlockPos origin, int width, int depth, Random random) {
        // Trailers are slightly elevated
        for (int x = 0; x < width; x++) {
            for (int z = 0; z < depth; z++) {
                BlockPos pos = origin.offset(x, 0, z);
                level.setBlock(pos, FOUNDATION, 2);
            }
        }
    }

    /**
     * Generate interior with 4 rooms:
     * ┌─────────────┐
     * │ LR  │ Kit   │  (Living Room | Kitchen)
     * ├─────┼───────┤
     * │ Bed │ Bath  │  (Bedroom | Bathroom)
     * └─────────────┘
     */
    private static void generateInterior(ServerLevel level, BlockPos origin, int width, int depth, Random random) {
        // Build exterior walls
        buildWalls(level, origin, width, depth, 4, random);

        // Floor
        for (int x = 0; x < width; x++) {
            for (int z = 0; z < depth; z++) {
                level.setBlock(origin.offset(x, 0, z), FLOOR, 2);
            }
        }

        // Interior walls dividing rooms
        // Vertical dividing wall at x=4 (between left and right rooms)
        for (int z = 0; z < depth; z++) {
            if (z == 3 || z == 10) continue; // Doorways
            BlockPos wallPos = origin.offset(4, 1, z);
            level.setBlock(wallPos, WALL, 2);
            level.setBlock(wallPos.above(), WALL, 2);
        }

        // Horizontal dividing wall at z=7 (between front and back rooms)
        for (int x = 0; x < width; x++) {
            if (x == 2 || x == 6) continue; // Doorways
            BlockPos wallPos = origin.offset(x, 1, 7);
            level.setBlock(wallPos, WALL, 2);
            level.setBlock(wallPos.above(), WALL, 2);
        }

        // Main entrance door (front, center)
        BlockPos doorPos = origin.offset(width / 2, 0, 0);
        level.setBlock(doorPos.above(), DOOR.setValue(BlockStateProperties.HORIZONTAL_FACING, Direction.SOUTH), 2);
        level.setBlock(doorPos.above(2), DOOR.setValue(BlockStateProperties.HORIZONTAL_FACING, Direction.SOUTH)
            .setValue(BlockStateProperties.DOUBLE_BLOCK_HALF, net.minecraft.world.level.block.state.properties.DoubleBlockHalf.UPPER), 2);

        // Windows
        placeWindows(level, origin, width, depth, random);

        // Furniture
        furnishTrailer(level, origin, random);
    }

    /**
     * Place windows on exterior walls
     */
    private static void placeWindows(ServerLevel level, BlockPos origin, int width, int depth, Random random) {
        // Front windows (z=0)
        placeWindow(level, origin.offset(1, 2, 0), Direction.NORTH, random);
        placeWindow(level, origin.offset(6, 2, 0), Direction.NORTH, random);

        // Back windows (z=depth-1)
        placeWindow(level, origin.offset(2, 2, depth - 1), Direction.SOUTH, random);
        placeWindow(level, origin.offset(5, 2, depth - 1), Direction.SOUTH, random);

        // Side windows
        placeWindow(level, origin.offset(0, 2, 4), Direction.WEST, random);
        placeWindow(level, origin.offset(width - 1, 2, 11), Direction.EAST, random);
    }

    /**
     * Place a window
     */
    private static void placeWindow(ServerLevel level, BlockPos pos, Direction facing, Random random) {
        level.setBlock(pos, Blocks.AIR.defaultBlockState(), 2); // Clear wall
        // 60% chance window is broken (air) instead of glass
        if (random.nextFloat() < 0.4f) {
            level.setBlock(pos, WINDOW, 2);
        }
    }

    /**
     * Add sparse furniture to trailer
     */
    private static void furnishTrailer(ServerLevel level, BlockPos origin, Random random) {
        // Living room (northwest) - sparse furniture
        level.setBlock(origin.offset(1, 1, 2), Blocks.OAK_STAIRS.defaultBlockState()
            .setValue(BlockStateProperties.HORIZONTAL_FACING, Direction.EAST), 2);

        // Kitchen (northeast) - basic appliances
        level.setBlock(origin.offset(5, 1, 2), Blocks.CRAFTING_TABLE.defaultBlockState(), 2);
        level.setBlock(origin.offset(6, 1, 3), Blocks.FURNACE.defaultBlockState(), 2);

        // Bedroom (southwest) - old bed
        level.setBlock(origin.offset(1, 1, 9), Blocks.ORANGE_BED.defaultBlockState()
            .setValue(BlockStateProperties.HORIZONTAL_FACING, Direction.NORTH), 2);
        level.setBlock(origin.offset(1, 1, 10), Blocks.ORANGE_BED.defaultBlockState()
            .setValue(BlockStateProperties.HORIZONTAL_FACING, Direction.NORTH)
            .setValue(net.minecraft.world.level.block.BedBlock.PART, net.minecraft.world.level.block.state.properties.BedPart.HEAD), 2);

        // Bathroom (southeast) - minimal
        level.setBlock(origin.offset(6, 1, 11), Blocks.CAULDRON.defaultBlockState(), 2);
    }

    /**
     * Generate roof
     */
    private static void generateRoof(ServerLevel level, BlockPos origin, int width, int depth, Random random) {
        // Flat roof with many holes (trailers are in bad shape)
        for (int x = 0; x < width; x++) {
            for (int z = 0; z < depth; z++) {
                // 25% chance for hole in roof
                if (random.nextFloat() < 0.25f) {
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

                // Heavy rust/decay (50% rusty blocks)
                BlockState wallBlock = random.nextFloat() < 0.5f ? WALL_RUSTY : WALL;

                level.setBlock(northPos, wallBlock, 2);
                level.setBlock(southPos, wallBlock, 2);
            }

            // East and West walls (along Z axis)
            for (int z = 1; z < depth - 1; z++) {
                BlockPos westPos = origin.offset(0, y, z);
                BlockPos eastPos = origin.offset(width - 1, y, z);

                BlockState wallBlock = random.nextFloat() < 0.5f ? WALL_RUSTY : WALL;

                level.setBlock(westPos, wallBlock, 2);
                level.setBlock(eastPos, wallBlock, 2);
            }
        }
    }

    /**
     * Apply overgrown aesthetics
     */
    private static void applyOvergrowth(ServerLevel level, BlockPos origin, int width, int depth, int height, Random random) {
        // Vines on exterior walls (50% coverage - trailers are heavily overgrown)
        for (int i = 0; i < (width * height + depth * height) * 0.5; i++) {
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

        // Grass growing through floor (40% of tiles)
        for (int i = 0; i < (width * depth) * 0.4; i++) {
            int x = random.nextInt(width);
            int z = random.nextInt(depth);
            BlockPos grassPos = origin.offset(x, 2, z);

            if (level.getBlockState(grassPos).isAir()) {
                level.setBlock(grassPos, Blocks.GRASS.defaultBlockState(), 2);
            }
        }

        // Many dead bushes
        for (int i = 0; i < 5; i++) {
            int x = random.nextInt(width);
            int z = random.nextInt(depth);
            BlockPos bushPos = origin.offset(x, 2, z);

            if (level.getBlockState(bushPos).isAir()) {
                level.setBlock(bushPos, Blocks.DEAD_BUSH.defaultBlockState(), 2);
            }
        }

        // Scattered around exterior
        for (int i = 0; i < 3; i++) {
            int x = -1 + random.nextInt(width + 2);
            int z = -1 + random.nextInt(depth + 2);
            BlockPos bushPos = origin.offset(x, 1, z);

            if (level.getBlockState(bushPos).isAir()) {
                level.setBlock(bushPos, Blocks.DEAD_BUSH.defaultBlockState(), 2);
            }
        }
    }

    /**
     * Apply decay to structure
     */
    private static void applyDecay(ServerLevel level, BlockPos origin, int width, int depth, int height, Random random) {
        // Heavy debris (trailers are in poor condition)
        for (int i = 0; i < 8; i++) {
            int x = random.nextInt(width);
            int z = random.nextInt(depth);
            int y = random.nextInt(height);
            BlockPos debrisPos = origin.offset(x, y, z);

            if (level.getBlockState(debrisPos).isAir()) {
                // Mix of gravel, cobble, and dirt
                BlockState debris;
                float roll = random.nextFloat();
                if (roll < 0.4f) {
                    debris = Blocks.GRAVEL.defaultBlockState();
                } else if (roll < 0.7f) {
                    debris = Blocks.COBBLESTONE.defaultBlockState();
                } else {
                    debris = Blocks.COARSE_DIRT.defaultBlockState();
                }
                level.setBlock(debrisPos, debris, 2);
            }
        }
    }

    /**
     * Place loot chest
     */
    private static void placeLoot(ServerLevel level, BlockPos origin, int width, int depth, Random random) {
        // 60% chance for single chest in bedroom closet
        if (random.nextFloat() < 0.6f) {
            BlockPos chestPos = origin.above().offset(2, 1, 12);
            level.setBlock(chestPos, Blocks.CHEST.defaultBlockState()
                .setValue(ChestBlock.FACING, Direction.NORTH), 2);
            // TODO: Fill with loot table
        }
    }

    /**
     * Spawn 1-2 enemies
     */
    private static void spawnEnemies(ServerLevel level, BlockPos origin, int width, int depth, Random random) {
        int enemyCount = 1 + random.nextInt(2); // 1-2 enemies

        for (int i = 0; i < enemyCount; i++) {
            // Random room
            int room = random.nextInt(4);
            int x, z;

            switch (room) {
                case 0: // Living room
                    x = 1 + random.nextInt(2);
                    z = 1 + random.nextInt(5);
                    break;
                case 1: // Kitchen
                    x = 5 + random.nextInt(2);
                    z = 1 + random.nextInt(5);
                    break;
                case 2: // Bedroom
                    x = 1 + random.nextInt(2);
                    z = 8 + random.nextInt(6);
                    break;
                case 3: // Bathroom
                default:
                    x = 5 + random.nextInt(2);
                    z = 8 + random.nextInt(6);
                    break;
            }

            BlockPos spawnPos = origin.offset(x, 2, z);

            // TODO: Use custom mob types when available (Goblins, Snakes)
            Zombie enemy = EntityType.ZOMBIE.create(level);
            if (enemy != null) {
                enemy.moveTo(spawnPos.getX() + 0.5, spawnPos.getY(), spawnPos.getZ() + 0.5,
                    random.nextFloat() * 360, 0);
                level.addFreshEntity(enemy);
            }
        }
    }
}
