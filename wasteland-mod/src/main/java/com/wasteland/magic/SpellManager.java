package com.wasteland.magic;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.*;

/**
 * Manages known spells for a player.
 * Each player has a spell library of memorized spells.
 */
public class SpellManager {
    private static final Logger LOGGER = LogManager.getLogger();
    private static final Map<UUID, SpellManager> INSTANCES = new HashMap<>();

    private final UUID playerId;
    private final Set<String> knownSpells; // Spell IDs
    private final Map<Integer, String> spellSlots; // Slot number -> Spell ID

    // Max spell slots based on XL (experience level)
    private static final int BASE_SPELL_SLOTS = 5;
    private static final int SLOTS_PER_XL = 1; // +1 slot every 2 levels

    private SpellManager(UUID playerId) {
        this.playerId = playerId;
        this.knownSpells = new HashSet<>();
        this.spellSlots = new HashMap<>();
    }

    /**
     * Get spell manager for a player
     */
    public static SpellManager getInstance(UUID playerId) {
        return INSTANCES.computeIfAbsent(playerId, SpellManager::new);
    }

    /**
     * Get spell manager for a player
     */
    public static SpellManager getInstance(net.minecraft.world.entity.player.Player player) {
        return getInstance(player.getUUID());
    }

    /**
     * Learn a spell (add to known spells library)
     */
    public boolean learnSpell(String spellId) {
        if (knownSpells.contains(spellId)) {
            LOGGER.debug("Player {} already knows spell {}", playerId, spellId);
            return false;
        }

        knownSpells.add(spellId);
        LOGGER.info("Player {} learned spell {}", playerId, spellId);
        return true;
    }

    /**
     * Forget a spell (remove from known spells)
     */
    public boolean forgetSpell(String spellId) {
        if (!knownSpells.contains(spellId)) {
            return false;
        }

        knownSpells.remove(spellId);

        // Remove from slots if memorized
        spellSlots.entrySet().removeIf(entry -> entry.getValue().equals(spellId));

        LOGGER.info("Player {} forgot spell {}", playerId, spellId);
        return true;
    }

    /**
     * Memorize a spell in a slot
     * Requires spell to be known
     */
    public boolean memorizeSpell(String spellId, int slotNumber) {
        if (!knownSpells.contains(spellId)) {
            LOGGER.warn("Cannot memorize unknown spell: {}", spellId);
            return false;
        }

        if (slotNumber < 0 || slotNumber >= getMaxSpellSlots()) {
            LOGGER.warn("Invalid spell slot: {}", slotNumber);
            return false;
        }

        // Check if spell is already memorized
        if (spellSlots.containsValue(spellId)) {
            LOGGER.warn("Spell {} already memorized", spellId);
            return false;
        }

        spellSlots.put(slotNumber, spellId);
        LOGGER.info("Player {} memorized {} in slot {}", playerId, spellId, slotNumber);
        return true;
    }

    /**
     * Unmemorize a spell from a slot
     */
    public boolean unmemorizeSpell(int slotNumber) {
        if (!spellSlots.containsKey(slotNumber)) {
            return false;
        }

        String spellId = spellSlots.remove(slotNumber);
        LOGGER.info("Player {} unmemorized {} from slot {}", playerId, spellId, slotNumber);
        return true;
    }

    /**
     * Get spell in a specific slot
     */
    public Spell getMemorizedSpell(int slotNumber) {
        String spellId = spellSlots.get(slotNumber);
        if (spellId == null) {
            return null;
        }
        return SpellRegistry.getSpell(spellId);
    }

    /**
     * Get all memorized spells
     */
    public List<Spell> getMemorizedSpells() {
        List<Spell> spells = new ArrayList<>();
        for (int i = 0; i < getMaxSpellSlots(); i++) {
            Spell spell = getMemorizedSpell(i);
            if (spell != null) {
                spells.add(spell);
            }
        }
        return spells;
    }

    /**
     * Get all known spells
     */
    public List<Spell> getKnownSpells() {
        List<Spell> spells = new ArrayList<>();
        for (String spellId : knownSpells) {
            Spell spell = SpellRegistry.getSpell(spellId);
            if (spell != null) {
                spells.add(spell);
            }
        }
        return spells;
    }

    /**
     * Check if player knows a spell
     */
    public boolean knowsSpell(String spellId) {
        return knownSpells.contains(spellId);
    }

    /**
     * Check if spell is memorized
     */
    public boolean isMemorized(String spellId) {
        return spellSlots.containsValue(spellId);
    }

    /**
     * Get slot number for a spell (-1 if not memorized)
     */
    public int getSlotNumber(String spellId) {
        for (Map.Entry<Integer, String> entry : spellSlots.entrySet()) {
            if (entry.getValue().equals(spellId)) {
                return entry.getKey();
            }
        }
        return -1;
    }

    /**
     * Get maximum spell slots for this player
     */
    public int getMaxSpellSlots() {
        // Get player character to check XL
        com.wasteland.character.PlayerCharacter character =
            com.wasteland.character.CharacterManager.getCharacter(playerId);

        if (character == null) {
            return BASE_SPELL_SLOTS;
        }

        int xl = character.getExperienceLevel();
        return BASE_SPELL_SLOTS + (xl / 2) * SLOTS_PER_XL;
    }

    /**
     * Get number of used spell slots
     */
    public int getUsedSlots() {
        return spellSlots.size();
    }

    /**
     * Get number of free spell slots
     */
    public int getFreeSlots() {
        return getMaxSpellSlots() - getUsedSlots();
    }

    /**
     * Save to NBT
     */
    public CompoundTag saveToNBT() {
        CompoundTag nbt = new CompoundTag();

        // Save known spells
        ListTag knownList = new ListTag();
        for (String spellId : knownSpells) {
            knownList.add(StringTag.valueOf(spellId));
        }
        nbt.put("KnownSpells", knownList);

        // Save memorized spells
        CompoundTag slotsTag = new CompoundTag();
        for (Map.Entry<Integer, String> entry : spellSlots.entrySet()) {
            slotsTag.putString(String.valueOf(entry.getKey()), entry.getValue());
        }
        nbt.put("SpellSlots", slotsTag);

        return nbt;
    }

    /**
     * Load from NBT
     */
    public void loadFromNBT(CompoundTag nbt) {
        knownSpells.clear();
        spellSlots.clear();

        // Load known spells
        if (nbt.contains("KnownSpells")) {
            ListTag knownList = nbt.getList("KnownSpells", Tag.TAG_STRING);
            for (int i = 0; i < knownList.size(); i++) {
                knownSpells.add(knownList.getString(i));
            }
        }

        // Load memorized spells
        if (nbt.contains("SpellSlots")) {
            CompoundTag slotsTag = nbt.getCompound("SpellSlots");
            for (String key : slotsTag.getAllKeys()) {
                try {
                    int slot = Integer.parseInt(key);
                    String spellId = slotsTag.getString(key);
                    spellSlots.put(slot, spellId);
                } catch (NumberFormatException e) {
                    LOGGER.error("Invalid spell slot key: {}", key);
                }
            }
        }

        LOGGER.info("Loaded spell data for player {}: {} known, {} memorized",
                   playerId, knownSpells.size(), spellSlots.size());
    }

    /**
     * Remove player data
     */
    public static void removePlayer(UUID playerId) {
        INSTANCES.remove(playerId);
    }

    /**
     * Clear all data
     */
    public static void clearAll() {
        INSTANCES.clear();
    }
}
