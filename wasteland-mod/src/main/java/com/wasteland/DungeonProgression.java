package com.wasteland;

import com.wasteland.player.RuneInventory;
import com.wasteland.worldgen.DungeonInstance;
import com.wasteland.worldgen.DungeonManager;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Tracks player progression through dungeon levels
 */
public class DungeonProgression {
    private static final Logger LOGGER = LogManager.getLogger();

    // Track each player's current dungeon depth
    private static final Map<UUID, Integer> PLAYER_DEPTHS = new HashMap<>();

    // Track which dungeon instance each player is currently in
    private static final Map<UUID, UUID> PLAYER_CURRENT_DUNGEON = new HashMap<>();

    // Track which floor of the current dungeon the player is on
    private static final Map<UUID, Integer> PLAYER_DUNGEON_FLOOR = new HashMap<>();

    /**
     * Get player's current dungeon depth (0 = surface, 1+ = underground)
     */
    public static int getDepth(UUID playerId) {
        return PLAYER_DEPTHS.getOrDefault(playerId, 0);
    }

    /**
     * Set player's dungeon depth
     */
    public static void setDepth(UUID playerId, int depth) {
        PLAYER_DEPTHS.put(playerId, depth);
        LOGGER.info("Player {} now at depth {}", playerId, depth);
    }

    /**
     * Move player deeper (stairs down)
     */
    public static int goDeeper(UUID playerId) {
        int currentDepth = getDepth(playerId);
        int newDepth = currentDepth + 1;
        setDepth(playerId, newDepth);
        LOGGER.info("Player descended: depth {} → {}", currentDepth, newDepth);
        return newDepth;
    }

    /**
     * Move player up (stairs up)
     */
    public static int goUp(UUID playerId) {
        int currentDepth = getDepth(playerId);
        int newDepth = Math.max(0, currentDepth - 1); // Don't go above surface (depth 0)
        setDepth(playerId, newDepth);
        LOGGER.info("Player ascended: depth {} → {}", currentDepth, newDepth);
        return newDepth;
    }

    /**
     * Get appropriate vault tier for current depth
     */
    public static VaultSelector.VaultTier getTierForDepth(int depth) {
        if (depth <= 5) {
            return VaultSelector.VaultTier.EARLY;  // Depths 1-5: easy vaults
        } else if (depth <= 10) {
            return VaultSelector.VaultTier.MID;    // Depths 6-10: medium vaults
        } else {
            return VaultSelector.VaultTier.LATE;   // Depths 11+: hard vaults
        }
    }

    /**
     * Get vault for player based on their current depth
     */
    public static String getVaultForDepth(UUID playerId) {
        int depth = getDepth(playerId);
        VaultSelector.VaultTier tier = getTierForDepth(depth);

        String vault = VaultSelector.getRandomVault(tier);

        LOGGER.info("Selected {} tier vault for depth {}: {}", tier, depth, vault);
        return vault;
    }

    /**
     * Reset player to surface (depth 0)
     */
    public static void resetToSurface(UUID playerId) {
        setDepth(playerId, 0);
        LOGGER.info("Player returned to surface (depth 0)");
    }

    /**
     * Clear all progression data (for world reload)
     */
    public static void clearAll() {
        PLAYER_DEPTHS.clear();
        PLAYER_CURRENT_DUNGEON.clear();
        PLAYER_DUNGEON_FLOOR.clear();
        LOGGER.info("Cleared all dungeon progression data");
    }

    /**
     * Enter a specific dungeon instance
     */
    public static void enterDungeon(UUID playerId, UUID dungeonId) {
        PLAYER_CURRENT_DUNGEON.put(playerId, dungeonId);
        PLAYER_DUNGEON_FLOOR.put(playerId, 1); // Start at floor 1
        LOGGER.info("Player {} entered dungeon {} (floor 1)", playerId, dungeonId);
    }

    /**
     * Get the dungeon instance the player is currently in
     */
    public static UUID getCurrentDungeon(UUID playerId) {
        return PLAYER_CURRENT_DUNGEON.get(playerId);
    }

    /**
     * Get which floor of the current dungeon the player is on
     */
    public static int getCurrentDungeonFloor(UUID playerId) {
        return PLAYER_DUNGEON_FLOOR.getOrDefault(playerId, 0);
    }

    /**
     * Descend one floor in the current dungeon
     * Returns true if rune was awarded (reached bottom of max-level dungeon)
     */
    public static boolean descendDungeonFloor(Player player) {
        UUID playerId = player.getUUID();
        UUID dungeonId = getCurrentDungeon(playerId);

        if (dungeonId == null) {
            LOGGER.warn("Player {} tried to descend but is not in a dungeon!", playerId);
            return false;
        }

        DungeonInstance dungeon = DungeonManager.getDungeon(dungeonId);
        if (dungeon == null) {
            LOGGER.warn("Player {} is in unknown dungeon {}", playerId, dungeonId);
            return false;
        }

        int currentFloor = getCurrentDungeonFloor(playerId);
        int newFloor = currentFloor + 1;
        PLAYER_DUNGEON_FLOOR.put(playerId, newFloor);

        LOGGER.info("Player {} descended: floor {} → {} (dungeon has {} levels)",
            playerId, currentFloor, newFloor, dungeon.getNumLevels());

        // Check if player reached the bottom floor
        if (newFloor >= dungeon.getNumLevels()) {
            LOGGER.info("═══════════════════════════════════════════════════════");
            LOGGER.info("  Player {} reached bottom floor of {}!", playerId, dungeon.getType().getDisplayName());

            // Check if this is a max-level dungeon with a rune
            if (dungeon.hasMaxLevels() && dungeon.getRune() != null && !dungeon.isRuneCollected()) {
                // Award the rune!
                RuneInventory.addRune(playerId, dungeon.getRune());
                dungeon.collectRune();

                player.sendSystemMessage(Component.literal("═══════════════════════════════════════════")
                    .withStyle(ChatFormatting.GOLD, ChatFormatting.BOLD));
                player.sendSystemMessage(Component.literal("You found the " + dungeon.getRune().getDisplayName() + "!")
                    .withStyle(ChatFormatting.GOLD, ChatFormatting.BOLD));
                player.sendSystemMessage(Component.literal("═══════════════════════════════════════════")
                    .withStyle(ChatFormatting.GOLD, ChatFormatting.BOLD));

                int runeCount = RuneInventory.getRuneCount(playerId);
                int runesNeeded = com.wasteland.worldgen.RuneType.getRunesRequiredForZot();

                if (runeCount >= runesNeeded) {
                    player.sendSystemMessage(Component.literal("You now have " + runeCount + " runes - enough to access the final realm!")
                        .withStyle(ChatFormatting.YELLOW));
                } else {
                    player.sendSystemMessage(Component.literal("You now have " + runeCount + "/" + runesNeeded + " runes")
                        .withStyle(ChatFormatting.YELLOW));
                }

                LOGGER.info("  Awarded {} to player! ({}/{})",
                    dungeon.getRune().getDisplayName(), runeCount, runesNeeded);
                LOGGER.info("═══════════════════════════════════════════════════════");

                return true;
            } else if (dungeon.hasMaxLevels() && dungeon.isRuneCollected()) {
                player.sendSystemMessage(Component.literal("This dungeon's rune has already been collected.")
                    .withStyle(ChatFormatting.GRAY));
                LOGGER.info("  Rune already collected from this dungeon");
            } else {
                LOGGER.info("  No rune (not a max-level dungeon)");
            }

            LOGGER.info("═══════════════════════════════════════════════════════");
        }

        return false;
    }

    /**
     * Exit the current dungeon (return to surface)
     */
    public static void exitDungeon(UUID playerId) {
        UUID dungeonId = PLAYER_CURRENT_DUNGEON.remove(playerId);
        PLAYER_DUNGEON_FLOOR.remove(playerId);
        resetToSurface(playerId);

        if (dungeonId != null) {
            LOGGER.info("Player {} exited dungeon {}", playerId, dungeonId);
        }
    }

    /**
     * Save progression data to NBT
     */
    public static CompoundTag save(CompoundTag tag) {
        // Save player depths
        CompoundTag depthsTag = new CompoundTag();
        for (Map.Entry<UUID, Integer> entry : PLAYER_DEPTHS.entrySet()) {
            depthsTag.putInt(entry.getKey().toString(), entry.getValue());
        }
        tag.put("PlayerDepths", depthsTag);

        // Save current dungeons
        CompoundTag currentDungeonsTag = new CompoundTag();
        for (Map.Entry<UUID, UUID> entry : PLAYER_CURRENT_DUNGEON.entrySet()) {
            currentDungeonsTag.putUUID(entry.getKey().toString(), entry.getValue());
        }
        tag.put("CurrentDungeons", currentDungeonsTag);

        // Save dungeon floors
        CompoundTag floorsTag = new CompoundTag();
        for (Map.Entry<UUID, Integer> entry : PLAYER_DUNGEON_FLOOR.entrySet()) {
            floorsTag.putInt(entry.getKey().toString(), entry.getValue());
        }
        tag.put("DungeonFloors", floorsTag);

        LOGGER.debug("Saved progression data for {} players", PLAYER_DEPTHS.size());
        return tag;
    }

    /**
     * Load progression data from NBT
     */
    public static void load(CompoundTag tag) {
        clearAll();

        // Load player depths
        if (tag.contains("PlayerDepths")) {
            CompoundTag depthsTag = tag.getCompound("PlayerDepths");
            for (String key : depthsTag.getAllKeys()) {
                UUID playerId = UUID.fromString(key);
                int depth = depthsTag.getInt(key);
                PLAYER_DEPTHS.put(playerId, depth);
            }
        }

        // Load current dungeons
        if (tag.contains("CurrentDungeons")) {
            CompoundTag currentDungeonsTag = tag.getCompound("CurrentDungeons");
            for (String key : currentDungeonsTag.getAllKeys()) {
                UUID playerId = UUID.fromString(key);
                UUID dungeonId = currentDungeonsTag.getUUID(key);
                PLAYER_CURRENT_DUNGEON.put(playerId, dungeonId);
            }
        }

        // Load dungeon floors
        if (tag.contains("DungeonFloors")) {
            CompoundTag floorsTag = tag.getCompound("DungeonFloors");
            for (String key : floorsTag.getAllKeys()) {
                UUID playerId = UUID.fromString(key);
                int floor = floorsTag.getInt(key);
                PLAYER_DUNGEON_FLOOR.put(playerId, floor);
            }
        }

        LOGGER.info("Loaded progression data for {} players", PLAYER_DEPTHS.size());
    }
}
