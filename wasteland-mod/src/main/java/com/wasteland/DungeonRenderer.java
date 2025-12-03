package com.wasteland;

import com.google.gson.Gson;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

/**
 * Renders DCSS-style dungeon rooms in Minecraft
 */
public class DungeonRenderer {
    private static final Logger LOGGER = LogManager.getLogger();

    /**
     * Renders a basic 13x13 DCSS-style room
     *
     * Room layout:
     * xxxxxxxxxxxxx
     * x...........x
     * x...........x
     * x.....@.....x  (@ = player spawn)
     * x...........x
     * x...........x
     * xxxxxxxxxxxxx
     *
     * x = Stone brick walls (3 blocks tall)
     * . = Gray concrete floor
     * @ = Air (where player stands)
     */
    public static BlockPos renderBasicRoom(ServerLevel level, BlockPos playerPos) {
        // Calculate room origin (bottom-left corner)
        // Build room BELOW player, then we'll teleport them into it
        BlockPos origin = playerPos.offset(-6, -2, -6);

        LOGGER.info("═══════════════════════════════════════════════════════");
        LOGGER.info("  Wasteland Crawl - Rendering DCSS Room!");
        LOGGER.info("  Origin: {}", origin);
        LOGGER.info("  Size: 13x13x3 blocks");
        LOGGER.info("═══════════════════════════════════════════════════════");

        // Room dimensions
        int width = 13;
        int depth = 13;
        int height = 3;

        // Build the room
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                for (int z = 0; z < depth; z++) {
                    BlockPos pos = origin.offset(x, y, z);

                    // Determine what block to place
                    boolean isWall = (x == 0 || x == width - 1 || z == 0 || z == depth - 1);
                    boolean isFloor = (y == 0);
                    boolean isCeiling = (y == height - 1);

                    if (isFloor) {
                        if (isWall) {
                            // Wall base - stone bricks
                            level.setBlock(pos, Blocks.STONE_BRICKS.defaultBlockState(), 3);
                        } else {
                            // Floor - gray concrete (wasteland aesthetic)
                            level.setBlock(pos, Blocks.GRAY_CONCRETE.defaultBlockState(), 3);
                        }
                    } else if (isCeiling) {
                        if (isWall) {
                            // Wall top - stone bricks
                            level.setBlock(pos, Blocks.STONE_BRICKS.defaultBlockState(), 3);
                        } else {
                            // Open ceiling - air (so we can see)
                            level.setBlock(pos, Blocks.AIR.defaultBlockState(), 3);
                        }
                    } else {
                        // Middle layer
                        if (isWall) {
                            // Wall middle - stone bricks
                            level.setBlock(pos, Blocks.STONE_BRICKS.defaultBlockState(), 3);
                        } else {
                            // Interior - air (walkable space)
                            level.setBlock(pos, Blocks.AIR.defaultBlockState(), 3);
                        }
                    }
                }
            }
        }

        // Add torches for lighting (like DCSS uses light sources)
        // Place torches on the walls
        BlockPos torch1 = origin.offset(2, 1, 1);
        BlockPos torch2 = origin.offset(width - 3, 1, 1);
        BlockPos torch3 = origin.offset(2, 1, depth - 2);
        BlockPos torch4 = origin.offset(width - 3, 1, depth - 2);

        level.setBlock(torch1, Blocks.TORCH.defaultBlockState(), 3);
        level.setBlock(torch2, Blocks.TORCH.defaultBlockState(), 3);
        level.setBlock(torch3, Blocks.TORCH.defaultBlockState(), 3);
        level.setBlock(torch4, Blocks.TORCH.defaultBlockState(), 3);

        LOGGER.info("  Room rendered successfully!");
        LOGGER.info("  Walls: Stone Bricks (DCSS dungeon aesthetic)");
        LOGGER.info("  Floor: Gray Concrete (wasteland theme)");
        LOGGER.info("  Torches: 4 placed for lighting");
        LOGGER.info("═══════════════════════════════════════════════════════");

        // Return the safe spawn position (center of room, standing on floor)
        return origin.offset(6, 1, 6);
    }

    /**
     * Renders a room from a JSON file
     *
     * @param level Server level to render in
     * @param playerPos Player's current position
     * @param jsonPath Path to JSON file (e.g., "rooms/basic_room.json")
     * @return Safe spawn position in the center of the room
     */
    public static BlockPos renderRoomFromJson(ServerLevel level, BlockPos playerPos, String jsonPath) {
        LOGGER.info("═══════════════════════════════════════════════════════");
        LOGGER.info("  Wasteland Crawl - Loading Room from JSON!");
        LOGGER.info("  JSON Path: {}", jsonPath);
        LOGGER.info("═══════════════════════════════════════════════════════");

        try {
            // Load JSON file from resources
            InputStream inputStream = DungeonRenderer.class.getClassLoader()
                    .getResourceAsStream(jsonPath);

            if (inputStream == null) {
                LOGGER.error("Failed to load JSON file: {}", jsonPath);
                LOGGER.error("Falling back to hardcoded room...");
                return renderBasicRoom(level, playerPos);
            }

            // Parse JSON using Gson
            Gson gson = new Gson();
            InputStreamReader reader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);
            RoomData roomData = gson.fromJson(reader, RoomData.class);
            reader.close();

            LOGGER.info("  Room Name: {}", roomData.name);
            LOGGER.info("  Description: {}", roomData.description);
            LOGGER.info("  Size: {}x{} (height: {})", roomData.width, roomData.depth, roomData.height);

            // Enforce minimum height of 5 (floor + 3 air blocks + ceiling)
            int minHeight = 5;
            if (roomData.height < minHeight) {
                LOGGER.info("  Adjusting height from {} to {} (minimum 3 blocks of walkable space)",
                           roomData.height, minHeight);
                roomData.height = minHeight;
            }

            // Calculate room origin (build below player)
            int halfWidth = roomData.width / 2;
            int halfDepth = roomData.depth / 2;
            BlockPos origin = playerPos.offset(-halfWidth, -2, -halfDepth);

            // Render floor and ceiling first
            for (int y = 0; y < roomData.height; y++) {
                for (int x = 0; x < roomData.width; x++) {
                    for (int z = 0; z < roomData.depth; z++) {
                        BlockPos pos = origin.offset(x, y, z);

                        if (y == 0) {
                            // Floor level - will be set by map
                        } else if (y == roomData.height - 1) {
                            // Ceiling - solid stone bricks for underground dungeons
                            level.setBlock(pos, Blocks.STONE_BRICKS.defaultBlockState(), 3);
                        } else {
                            // Middle layers - air by default
                            level.setBlock(pos, Blocks.AIR.defaultBlockState(), 3);
                        }
                    }
                }
            }

            // Render map data
            for (int z = 0; z < roomData.map.size(); z++) {
                String row = roomData.map.get(z);
                for (int x = 0; x < row.length(); x++) {
                    char tile = row.charAt(x);
                    String tileKey = String.valueOf(tile);

                    if (roomData.legend.containsKey(tileKey)) {
                        RoomData.TileType tileType = roomData.legend.get(tileKey);
                        Block block = getBlockFromString(tileType.block);

                        // Place block on floor
                        BlockPos floorPos = origin.offset(x, 0, z);
                        level.setBlock(floorPos, block.defaultBlockState(), 3);

                        // If it's a wall, build it up to full height
                        if (tileType.type.equals("wall")) {
                            for (int y = 1; y < roomData.height; y++) {
                                BlockPos wallPos = origin.offset(x, y, z);
                                level.setBlock(wallPos, block.defaultBlockState(), 3);
                            }
                        }
                        // If it's a door, place floor and leave opening for door
                        else if (tileType.type.equals("door")) {
                            // Place floor at y=0, air at y=1 and y=2 for door opening
                            level.setBlock(origin.offset(x, 1, z), Blocks.AIR.defaultBlockState(), 3);
                            level.setBlock(origin.offset(x, 2, z), Blocks.AIR.defaultBlockState(), 3);
                        }
                    }
                }
            }

            // Render features (torches, chests, portals, monsters, etc.)
            if (roomData.features != null) {
                for (RoomData.Feature feature : roomData.features) {
                    BlockPos featurePos = origin.offset(feature.x, feature.y, feature.z);

                    // Handle monster spawns (spawn mob, don't place marker block)
                    if (feature.type.equals("monster_spawn")) {
                        // Spawn monster at this position
                        if (feature.monster_tier != null) {
                            MonsterSpawner.spawnMonster(level, featurePos, feature.monster_tier);
                            LOGGER.debug("Spawned tier {} monster at {}", feature.monster_tier, featurePos);
                        }
                        // Don't place the red wool marker block in-game
                        continue;
                    }

                    // Place block for all other features
                    Block block = getBlockFromString(feature.block);
                    level.setBlock(featurePos, block.defaultBlockState(), 3);

                    // Register portals
                    if (feature.type.startsWith("portal_")) {
                        registerPortalFeature(featurePos, feature);
                    }
                }
            }

            // Add automatic lighting throughout the dungeon
            addDungeonLighting(level, origin, roomData.width, roomData.depth, roomData.height);

            LOGGER.info("  Room rendered successfully from JSON!");
            LOGGER.info("  Total tiles in map: {}", roomData.map.size() * roomData.width);
            LOGGER.info("  Features placed: {}", roomData.features != null ? roomData.features.size() : 0);
            LOGGER.info("═══════════════════════════════════════════════════════");

            // Return spawn position from JSON
            if (roomData.spawn_point != null) {
                return origin.offset(roomData.spawn_point.x, roomData.spawn_point.y, roomData.spawn_point.z);
            } else {
                // Default to center of room
                return origin.offset(halfWidth, 1, halfDepth);
            }

        } catch (IOException e) {
            LOGGER.error("Error loading JSON file: {}", e.getMessage());
            LOGGER.error("Falling back to hardcoded room...");
            return renderBasicRoom(level, playerPos);
        } catch (Exception e) {
            LOGGER.error("Error parsing/rendering room: {}", e.getMessage());
            e.printStackTrace();
            LOGGER.error("Falling back to hardcoded room...");
            return renderBasicRoom(level, playerPos);
        }
    }

    /**
     * Converts a Minecraft block string (e.g., "minecraft:stone_bricks") to a Block object
     */
    private static Block getBlockFromString(String blockString) {
        try {
            ResourceLocation resourceLocation = new ResourceLocation(blockString);
            return BuiltInRegistries.BLOCK.get(resourceLocation);
        } catch (Exception e) {
            LOGGER.warn("Failed to parse block: {}, using stone as fallback", blockString);
            return Blocks.STONE;
        }
    }

    /**
     * Add automatic lighting throughout the dungeon
     * Places torches on floors at regular intervals to ensure good visibility
     */
    private static void addDungeonLighting(ServerLevel level, BlockPos origin, int width, int depth, int height) {
        int lightsPlaced = 0;
        int spacing = 5; // Place lights every 5 blocks

        LOGGER.debug("Adding automatic dungeon lighting (spacing: {} blocks)", spacing);

        // Place torches on the floor in a grid pattern
        for (int x = 2; x < width - 2; x += spacing) {
            for (int z = 2; z < depth - 2; z += spacing) {
                BlockPos floorPos = origin.offset(x, 0, z);
                BlockPos torchPos = origin.offset(x, 1, z);

                // Check if floor is solid and torch position is air
                if (level.getBlockState(floorPos).isSolidRender(level, floorPos) &&
                    level.getBlockState(torchPos).isAir()) {

                    // Place torch on the floor
                    level.setBlock(torchPos, Blocks.TORCH.defaultBlockState(), 3);
                    lightsPlaced++;
                }
            }
        }

        // Add corner lighting for better coverage
        placeCornerLight(level, origin, 2, 2, lightsPlaced);
        placeCornerLight(level, origin, width - 3, 2, lightsPlaced);
        placeCornerLight(level, origin, 2, depth - 3, lightsPlaced);
        placeCornerLight(level, origin, width - 3, depth - 3, lightsPlaced);
        lightsPlaced += 4;

        LOGGER.debug("Placed {} automatic light sources in dungeon", lightsPlaced);
    }

    /**
     * Place a light in a corner if possible
     */
    private static void placeCornerLight(ServerLevel level, BlockPos origin, int x, int z, int lightsPlaced) {
        BlockPos floorPos = origin.offset(x, 0, z);
        BlockPos torchPos = origin.offset(x, 1, z);

        if (level.getBlockState(floorPos).isSolidRender(level, floorPos) &&
            level.getBlockState(torchPos).isAir()) {
            level.setBlock(torchPos, Blocks.TORCH.defaultBlockState(), 3);
        }
    }

    /**
     * Register a portal feature with the PortalManager
     * Note: Destination vault is determined dynamically when portal is used
     */
    private static void registerPortalFeature(BlockPos pos, RoomData.Feature feature) {
        PortalManager.PortalType portalType;

        switch (feature.type) {
            case "portal_up":
                portalType = PortalManager.PortalType.STAIRS_UP;
                break;
            case "portal_down":
                portalType = PortalManager.PortalType.STAIRS_DOWN;
                break;
            case "portal_entrance":
                portalType = PortalManager.PortalType.DUNGEON_ENTRANCE;
                break;
            case "portal_exit":
                portalType = PortalManager.PortalType.DUNGEON_EXIT;
                break;
            default:
                LOGGER.warn("Unknown portal type: {}", feature.type);
                return;
        }

        // Register portal (destination determined dynamically based on player depth)
        PortalManager.registerPortal(pos, portalType, null);
        LOGGER.debug("Registered {} portal at {}", portalType, pos);
    }
}
