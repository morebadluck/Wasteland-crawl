package com.wasteland.player;

import com.wasteland.worldgen.RuneType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.*;

/**
 * Tracks which runes a player has collected.
 * This is separate from inventory - once collected, runes are permanently tracked.
 */
public class RuneInventory {
    private static final Logger LOGGER = LogManager.getLogger();
    private static final Map<UUID, Set<RuneType>> PLAYER_RUNES = new HashMap<>();

    /**
     * Add a rune to player's collection
     */
    public static void addRune(UUID playerId, RuneType rune) {
        Set<RuneType> runes = PLAYER_RUNES.computeIfAbsent(playerId, k -> new HashSet<>());

        if (runes.add(rune)) {
            LOGGER.info("Player {} collected rune: {} ({}/{})",
                playerId, rune.getDisplayName(), runes.size(), RuneType.getRunesRequiredForZot());

            // Check if player now has enough runes for Zot
            if (RuneType.hasEnoughRunesForZot(runes)) {
                LOGGER.info("Player {} has collected enough runes to access Zot!", playerId);
            }
        } else {
            LOGGER.debug("Player {} already has rune: {}", playerId, rune.getDisplayName());
        }
    }

    /**
     * Check if player has a specific rune
     */
    public static boolean hasRune(UUID playerId, RuneType rune) {
        Set<RuneType> runes = PLAYER_RUNES.get(playerId);
        return runes != null && runes.contains(rune);
    }

    /**
     * Get all runes collected by player
     */
    public static Set<RuneType> getRunes(UUID playerId) {
        return new HashSet<>(PLAYER_RUNES.getOrDefault(playerId, new HashSet<>()));
    }

    /**
     * Get number of runes collected
     */
    public static int getRuneCount(UUID playerId) {
        Set<RuneType> runes = PLAYER_RUNES.get(playerId);
        return runes != null ? runes.size() : 0;
    }

    /**
     * Check if player has enough runes to access Zot
     */
    public static boolean canAccessZot(UUID playerId) {
        Set<RuneType> runes = PLAYER_RUNES.get(playerId);
        return runes != null && RuneType.hasEnoughRunesForZot(runes);
    }

    /**
     * Get a formatted list of collected runes
     */
    public static List<String> getRuneList(UUID playerId) {
        Set<RuneType> runes = PLAYER_RUNES.get(playerId);
        if (runes == null || runes.isEmpty()) {
            return Collections.singletonList("No runes collected");
        }

        List<String> list = new ArrayList<>();
        for (RuneType rune : runes) {
            list.add("§6✦ §e" + rune.getDisplayName());
        }
        return list;
    }

    /**
     * Remove a rune (for testing/admin commands)
     */
    public static void removeRune(UUID playerId, RuneType rune) {
        Set<RuneType> runes = PLAYER_RUNES.get(playerId);
        if (runes != null && runes.remove(rune)) {
            LOGGER.info("Removed rune {} from player {}", rune.getDisplayName(), playerId);
        }
    }

    /**
     * Clear all runes for a player
     */
    public static void clearRunes(UUID playerId) {
        PLAYER_RUNES.remove(playerId);
        LOGGER.info("Cleared all runes for player {}", playerId);
    }

    /**
     * Save to NBT
     */
    public static CompoundTag save(UUID playerId, CompoundTag tag) {
        Set<RuneType> runes = PLAYER_RUNES.get(playerId);
        if (runes != null && !runes.isEmpty()) {
            ListTag runesList = new ListTag();
            for (RuneType rune : runes) {
                runesList.add(StringTag.valueOf(rune.name()));
            }
            tag.put("CollectedRunes", runesList);
        }
        return tag;
    }

    /**
     * Load from NBT
     */
    public static void load(UUID playerId, CompoundTag tag) {
        if (tag.contains("CollectedRunes")) {
            Set<RuneType> runes = new HashSet<>();
            ListTag runesList = tag.getList("CollectedRunes", Tag.TAG_STRING);

            for (int i = 0; i < runesList.size(); i++) {
                try {
                    RuneType rune = RuneType.valueOf(runesList.getString(i));
                    runes.add(rune);
                } catch (IllegalArgumentException e) {
                    LOGGER.warn("Invalid rune type in save data: {}", runesList.getString(i));
                }
            }

            if (!runes.isEmpty()) {
                PLAYER_RUNES.put(playerId, runes);
                LOGGER.info("Loaded {} runes for player {}", runes.size(), playerId);
            }
        }
    }

    /**
     * Clear all data (for world reload)
     */
    public static void clearAll() {
        PLAYER_RUNES.clear();
        LOGGER.info("Cleared all rune inventory data");
    }

    /**
     * Save all players' runes to NBT
     */
    public static CompoundTag save(CompoundTag tag) {
        CompoundTag playersTag = new CompoundTag();

        for (Map.Entry<UUID, Set<RuneType>> entry : PLAYER_RUNES.entrySet()) {
            if (!entry.getValue().isEmpty()) {
                CompoundTag playerTag = new CompoundTag();
                save(entry.getKey(), playerTag);
                playersTag.put(entry.getKey().toString(), playerTag);
            }
        }

        tag.put("Players", playersTag);
        tag.putInt("PlayerCount", playersTag.size());
        LOGGER.debug("Saved rune data for {} players", playersTag.size());
        return tag;
    }

    /**
     * Load all players' runes from NBT
     */
    public static void load(CompoundTag tag) {
        clearAll();

        if (tag.contains("Players")) {
            CompoundTag playersTag = tag.getCompound("Players");

            for (String key : playersTag.getAllKeys()) {
                try {
                    UUID playerId = UUID.fromString(key);
                    CompoundTag playerTag = playersTag.getCompound(key);
                    load(playerId, playerTag);
                } catch (IllegalArgumentException e) {
                    LOGGER.warn("Invalid player UUID in rune save data: {}", key);
                }
            }

            LOGGER.info("Loaded rune data for {} players", PLAYER_RUNES.size());
        }
    }
}
