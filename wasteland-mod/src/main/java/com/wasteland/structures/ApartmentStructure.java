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
 * Generates apartment buildings for the wasteland.
 * Multi-floor structures (3-5 floors, 20x20x15) with 4 units per floor.
 */
public class ApartmentStructure {

    // Block palette for apartments
    private static final BlockState WALL = Blocks.STONE_BRICKS.defaultBlockState();
    private static final BlockState WALL_CRACKED = Blocks.CRACKED_STONE_BRICKS.defaultBlockState();
    private static final BlockState FLOOR = Blocks.POLISHED_ANDESITE.defaultBlockState();
    private static final BlockState CEILING = Blocks.SMOOTH_STONE.defaultBlockState();
    private static final BlockState WINDOW = Blocks.GLASS_PANE.defaultBlockState();
    private static final BlockState DOOR = Blocks.IRON_DOOR.defaultBlockState();
    private static final BlockState STAIRS = Blocks.STONE_STAIRS.defaultBlockState();
    private static final BlockState FOUNDATION = Blocks.STONE_BRICKS.defaultBlockState();

    /**
     * Generate an apartment building at the given position
     */
    public static void generate(ServerLevel level, BlockPos origin, Random random) {
        // Dimensions: 20 wide (X), 20 deep (Z), variable height
        int width = 20;
        int depth = 20;
        int floors = 3 + random.nextInt(3); // 3-5 floors
        int floorHeight = 4; // Height of each floor
        int totalHeight = floors * floorHeight;

        // Clear area first
        clearArea(level, origin, width, depth, totalHeight + 2);

        // Generate foundation
        generateFoundation(level, origin, width, depth, random);

        // Generate each floor
        for (int floor = 0; floor < floors; floor++) {
            BlockPos floorOrigin = origin.above(floor * floorHeight + 1);
            generateFloor(level, floorOrigin, width, depth, floor, floors, random);
        }

        // Generate roof
        generateRoof(level, origin.above(totalHeight), width, depth, random);

        // Generate stairwell connecting all floors
        generateStairwell(level, origin, floors, floorHeight, random);

        // Apply overgrowth aesthetics
        applyOvergrowth(level, origin, width, depth, totalHeight, random);

        // Apply decay
        applyDecay(level, origin, width, depth, totalHeight, random);

        // Place loot across apartments
        placeLoot(level, origin, width, depth, floors, floorHeight, random);

        // Spawn enemies
        spawnEnemies(level, origin, width, depth, floors, floorHeight, random);
    }

    /**
     * Clear the area for the building
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
     * Generate a single floor with 4 apartments
     * Layout:
     * ┌─────┬─────┬─────┬─────┐
     * │ APT │ APT │ APT │ APT │
     * │  1  │  2  │  3  │  4  │
     * ├─────┴──┬──┴─────┴─────┤
     * │        │               │
     * │  HALL  │  STAIRWELL    │
     * │        │               │
     * └────────┴───────────────┘
     */
    private static void generateFloor(ServerLevel level, BlockPos origin, int width, int depth,
                                     int floorNumber, int totalFloors, Random random) {
        // Build exterior walls
        buildWalls(level, origin, width, depth, 3, random);

        // Floor
        for (int x = 0; x < width; x++) {
            for (int z = 0; z < depth; z++) {
                level.setBlock(origin.offset(x, 0, z), FLOOR, 2);
            }
        }

        // Ceiling
        for (int x = 0; x < width; x++) {
            for (int z = 0; z < depth; z++) {
                level.setBlock(origin.offset(x, 3, z), CEILING, 2);
            }
        }

        // Central hallway walls (vertical, at x=10)
        for (int z = 0; z < depth; z++) {
            if (z == 5 || z == 10 || z == 15) continue; // Doorways to apartments
            level.setBlock(origin.offset(9, 1, z), WALL, 2);
            level.setBlock(origin.offset(9, 2, z), WALL, 2);
        }

        // Horizontal dividing walls for apartments
        // Top row apartments (z=0 to z=8)
        for (int x = 0; x < 9; x++) {
            if (x == 2) continue; // Doorway to APT 1
            level.setBlock(origin.offset(x, 1, 4), WALL, 2);
            level.setBlock(origin.offset(x, 2, 4), WALL, 2);
        }

        // Middle row apartments (z=11 to z=19)
        for (int x = 0; x < 9; x++) {
            if (x == 2) continue; // Doorway to APT 2
            level.setBlock(origin.offset(x, 1, 11), WALL, 2);
            level.setBlock(origin.offset(x, 2, 11), WALL, 2);
        }

        // Apartments on right side
        for (int z = 0; z < depth; z++) {
            if (z == 5 || z == 15) continue; // Doorways
            level.setBlock(origin.offset(14, 1, z), WALL, 2);
            level.setBlock(origin.offset(14, 2, z), WALL, 2);
        }

        // APT 3 and APT 4 dividing wall
        for (int x = 15; x < width; x++) {
            if (x == 17) continue; // Doorway to APT 4
            level.setBlock(origin.offset(x, 1, 10), WALL, 2);
            level.setBlock(origin.offset(x, 2, 10), WALL, 2);
        }

        // Place windows on exterior walls
        placeWindows(level, origin, width, depth, random);

        // Add furniture to apartments
        furnishApartments(level, origin, random);

        // Main entrance door on ground floor only
        if (floorNumber == 0) {
            BlockPos doorPos = origin.offset(width / 2, 0, 0);
            level.setBlock(doorPos.above(), DOOR.setValue(BlockStateProperties.HORIZONTAL_FACING, Direction.SOUTH), 2);
            level.setBlock(doorPos.above(2), DOOR.setValue(BlockStateProperties.HORIZONTAL_FACING, Direction.SOUTH)
                .setValue(BlockStateProperties.DOUBLE_BLOCK_HALF, net.minecraft.world.level.block.state.properties.DoubleBlockHalf.UPPER), 2);
        }
    }

    /**
     * Place windows on exterior walls
     */
    private static void placeWindows(ServerLevel level, BlockPos origin, int width, int depth, Random random) {
        // North wall (z=0)
        placeWindow(level, origin.offset(2, 2, 0), Direction.NORTH, random);
        placeWindow(level, origin.offset(7, 2, 0), Direction.NORTH, random);
        placeWindow(level, origin.offset(17, 2, 0), Direction.NORTH, random);

        // South wall (z=depth-1)
        placeWindow(level, origin.offset(2, 2, depth - 1), Direction.SOUTH, random);
        placeWindow(level, origin.offset(7, 2, depth - 1), Direction.SOUTH, random);
        placeWindow(level, origin.offset(17, 2, depth - 1), Direction.SOUTH, random);

        // West wall (x=0)
        placeWindow(level, origin.offset(0, 2, 2), Direction.WEST, random);
        placeWindow(level, origin.offset(0, 2, 7), Direction.WEST, random);
        placeWindow(level, origin.offset(0, 2, 17), Direction.WEST, random);

        // East wall (x=width-1)
        placeWindow(level, origin.offset(width - 1, 2, 2), Direction.EAST, random);
        placeWindow(level, origin.offset(width - 1, 2, 7), Direction.EAST, random);
        placeWindow(level, origin.offset(width - 1, 2, 17), Direction.EAST, random);
    }

    /**
     * Place a window
     */
    private static void placeWindow(ServerLevel level, BlockPos pos, Direction facing, Random random) {
        level.setBlock(pos, Blocks.AIR.defaultBlockState(), 2); // Clear wall
        // 50% chance window is broken (air) instead of glass
        if (random.nextFloat() < 0.5f) {
            level.setBlock(pos, WINDOW, 2);
        }
    }

    /**
     * Add furniture to apartments
     */
    private static void furnishApartments(ServerLevel level, BlockPos origin, Random random) {
        // APT 1 (northwest) - furniture
        level.setBlock(origin.offset(2, 1, 2), Blocks.RED_BED.defaultBlockState()
            .setValue(BlockStateProperties.HORIZONTAL_FACING, Direction.EAST), 2);
        level.setBlock(origin.offset(3, 1, 2), Blocks.RED_BED.defaultBlockState()
            .setValue(BlockStateProperties.HORIZONTAL_FACING, Direction.EAST)
            .setValue(net.minecraft.world.level.block.BedBlock.PART, net.minecraft.world.level.block.state.properties.BedPart.HEAD), 2);

        // APT 2 (southwest) - furniture
        level.setBlock(origin.offset(2, 1, 13), Blocks.BLUE_BED.defaultBlockState()
            .setValue(BlockStateProperties.HORIZONTAL_FACING, Direction.EAST), 2);
        level.setBlock(origin.offset(3, 1, 13), Blocks.BLUE_BED.defaultBlockState()
            .setValue(BlockStateProperties.HORIZONTAL_FACING, Direction.EAST)
            .setValue(net.minecraft.world.level.block.BedBlock.PART, net.minecraft.world.level.block.state.properties.BedPart.HEAD), 2);

        // APT 3 (northeast) - furniture
        level.setBlock(origin.offset(16, 1, 3), Blocks.WHITE_BED.defaultBlockState()
            .setValue(BlockStateProperties.HORIZONTAL_FACING, Direction.NORTH), 2);
        level.setBlock(origin.offset(16, 1, 4), Blocks.WHITE_BED.defaultBlockState()
            .setValue(BlockStateProperties.HORIZONTAL_FACING, Direction.NORTH)
            .setValue(net.minecraft.world.level.block.BedBlock.PART, net.minecraft.world.level.block.state.properties.BedPart.HEAD), 2);

        // APT 4 (southeast) - furniture
        level.setBlock(origin.offset(16, 1, 13), Blocks.GREEN_BED.defaultBlockState()
            .setValue(BlockStateProperties.HORIZONTAL_FACING, Direction.NORTH), 2);
        level.setBlock(origin.offset(16, 1, 14), Blocks.GREEN_BED.defaultBlockState()
            .setValue(BlockStateProperties.HORIZONTAL_FACING, Direction.NORTH)
            .setValue(net.minecraft.world.level.block.BedBlock.PART, net.minecraft.world.level.block.state.properties.BedPart.HEAD), 2);

        // Some random furniture in hallway
        if (random.nextBoolean()) {
            level.setBlock(origin.offset(10, 1, 5), Blocks.CRAFTING_TABLE.defaultBlockState(), 2);
        }
    }

    /**
     * Generate stairwell connecting all floors
     */
    private static void generateStairwell(ServerLevel level, BlockPos origin, int floors, int floorHeight, Random random) {
        // Stairwell location: x=15-18, z=5-9
        int stairX = 16;
        int stairZ = 7;

        for (int floor = 0; floor < floors - 1; floor++) {
            int y = origin.getY() + floor * floorHeight + 1;

            // Create stairs going up
            for (int i = 0; i < floorHeight; i++) {
                BlockPos stairPos = new BlockPos(origin.getX() + stairX, y + i, origin.getZ() + stairZ + (i % 2));
                level.setBlock(stairPos, STAIRS.setValue(BlockStateProperties.HORIZONTAL_FACING,
                    i % 2 == 0 ? Direction.SOUTH : Direction.NORTH), 2);
            }
        }
    }

    /**
     * Generate roof
     */
    private static void generateRoof(ServerLevel level, BlockPos origin, int width, int depth, Random random) {
        // Flat roof with some holes for decay
        for (int x = 0; x < width; x++) {
            for (int z = 0; z < depth; z++) {
                // 15% chance for hole in roof
                if (random.nextFloat() < 0.15f) {
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

                // Mix in cracked walls
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
     * Apply overgrown aesthetics
     */
    private static void applyOvergrowth(ServerLevel level, BlockPos origin, int width, int depth, int height, Random random) {
        // Vines on exterior walls (30% coverage)
        for (int i = 0; i < (width * height + depth * height) * 0.3; i++) {
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

        // Grass growing through floor (20% of tiles on ground floor)
        for (int i = 0; i < (width * depth) * 0.2; i++) {
            int x = random.nextInt(width);
            int z = random.nextInt(depth);
            BlockPos grassPos = origin.offset(x, 2, z);

            if (level.getBlockState(grassPos).isAir()) {
                level.setBlock(grassPos, Blocks.GRASS.defaultBlockState(), 2);
            }
        }

        // Dead bushes scattered
        for (int i = 0; i < 8; i++) {
            int x = random.nextInt(width);
            int z = random.nextInt(depth);
            BlockPos bushPos = origin.offset(x, 2, z);

            if (level.getBlockState(bushPos).isAir()) {
                level.setBlock(bushPos, Blocks.DEAD_BUSH.defaultBlockState(), 2);
            }
        }
    }

    /**
     * Apply decay to structure
     */
    private static void applyDecay(ServerLevel level, BlockPos origin, int width, int depth, int height, Random random) {
        // Add debris (cobblestone, gravel)
        for (int i = 0; i < 10; i++) {
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
     * Place loot chests in apartments
     */
    private static void placeLoot(ServerLevel level, BlockPos origin, int width, int depth,
                                  int floors, int floorHeight, Random random) {
        // 1-2 chests per floor
        for (int floor = 0; floor < floors; floor++) {
            int chestsThisFloor = 1 + random.nextInt(2);

            for (int i = 0; i < chestsThisFloor; i++) {
                int apartment = random.nextInt(4);
                BlockPos floorOrigin = origin.above(floor * floorHeight + 1);
                BlockPos chestPos;

                switch (apartment) {
                    case 0: // APT 1
                        chestPos = floorOrigin.offset(1, 1, 1);
                        break;
                    case 1: // APT 2
                        chestPos = floorOrigin.offset(1, 1, 12);
                        break;
                    case 2: // APT 3
                        chestPos = floorOrigin.offset(15, 1, 2);
                        break;
                    case 3: // APT 4
                    default:
                        chestPos = floorOrigin.offset(15, 1, 12);
                        break;
                }

                level.setBlock(chestPos, Blocks.CHEST.defaultBlockState()
                    .setValue(ChestBlock.FACING, Direction.NORTH), 2);
                // TODO: Fill with loot table
            }
        }
    }

    /**
     * Spawn 5-10 enemies across apartments, with boss on top floor
     */
    private static void spawnEnemies(ServerLevel level, BlockPos origin, int width, int depth,
                                    int floors, int floorHeight, Random random) {
        int enemyCount = 5 + random.nextInt(6); // 5-10 enemies

        for (int i = 0; i < enemyCount; i++) {
            // Random floor
            int floor = random.nextInt(floors);
            int floorY = origin.getY() + floor * floorHeight + 2;

            // Random apartment (0-3)
            int apartment = random.nextInt(4);
            int x, z;

            // Determine spawn position based on apartment
            switch (apartment) {
                case 0: // APT 1 (northwest)
                    x = 2 + random.nextInt(6);
                    z = 1 + random.nextInt(3);
                    break;
                case 1: // APT 2 (southwest)
                    x = 2 + random.nextInt(6);
                    z = 12 + random.nextInt(6);
                    break;
                case 2: // APT 3 (northeast)
                    x = 15 + random.nextInt(4);
                    z = 1 + random.nextInt(8);
                    break;
                case 3: // APT 4 (southeast)
                default:
                    x = 15 + random.nextInt(4);
                    z = 11 + random.nextInt(8);
                    break;
            }

            BlockPos spawnPos = new BlockPos(origin.getX() + x, floorY, origin.getZ() + z);

            // Last enemy is the boss, spawn on top floor
            if (i == enemyCount - 1) {
                int topFloorY = origin.getY() + (floors - 1) * floorHeight + 2;
                spawnPos = new BlockPos(origin.getX() + 16, topFloorY, origin.getZ() + 13);
            }

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
