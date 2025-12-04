package com.wasteland.loot;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Global manager for player equipment.
 * Tracks equipped items for all players and handles persistence.
 */
public class EquipmentManager {
    private static final Logger LOGGER = LogManager.getLogger();
    private static EquipmentManager instance;

    private final Map<UUID, PlayerEquipment> playerEquipment;

    private EquipmentManager() {
        this.playerEquipment = new HashMap<>();
    }

    public static EquipmentManager getInstance() {
        if (instance == null) {
            instance = new EquipmentManager();
        }
        return instance;
    }

    /**
     * Get equipment for a player (creates if doesn't exist)
     */
    public PlayerEquipment getEquipment(UUID playerId) {
        return playerEquipment.computeIfAbsent(playerId, PlayerEquipment::new);
    }

    /**
     * Get equipment for a player
     */
    public PlayerEquipment getEquipment(Player player) {
        return getEquipment(player.getUUID());
    }

    /**
     * Equip an item for a player
     * @param playerId The player UUID
     * @param slot The equipment slot
     * @param itemStack The item to equip
     * @return The previously equipped item
     */
    public ItemStack equipItem(UUID playerId, ArmorSlot slot, ItemStack itemStack) {
        PlayerEquipment equipment = getEquipment(playerId);
        ItemStack previousItem = equipment.equipItem(slot, itemStack);

        // Update player character stats
        updatePlayerStats(playerId);

        return previousItem;
    }

    /**
     * Equip an item for a player
     */
    public ItemStack equipItem(Player player, ArmorSlot slot, ItemStack itemStack) {
        return equipItem(player.getUUID(), slot, itemStack);
    }

    /**
     * Unequip an item from a player
     * @param playerId The player UUID
     * @param slot The equipment slot
     * @return The unequipped item
     */
    public ItemStack unequipItem(UUID playerId, ArmorSlot slot) {
        PlayerEquipment equipment = getEquipment(playerId);
        ItemStack item = equipment.unequipItem(slot);

        // Update player character stats
        updatePlayerStats(playerId);

        return item;
    }

    /**
     * Unequip an item from a player
     */
    public ItemStack unequipItem(Player player, ArmorSlot slot) {
        return unequipItem(player.getUUID(), slot);
    }

    /**
     * Get equipped weapon for a player
     */
    public WastelandWeapon getEquippedWeapon(UUID playerId) {
        return getEquipment(playerId).getEquippedWeapon();
    }

    /**
     * Get equipped weapon for a player
     */
    public WastelandWeapon getEquippedWeapon(Player player) {
        return getEquippedWeapon(player.getUUID());
    }

    /**
     * Get equipped shield for a player
     */
    public WastelandArmor getEquippedShield(UUID playerId) {
        return getEquipment(playerId).getEquippedShield();
    }

    /**
     * Get equipped shield for a player
     */
    public WastelandArmor getEquippedShield(Player player) {
        return getEquippedShield(player.getUUID());
    }

    /**
     * Get total AC for a player
     */
    public int getTotalAC(UUID playerId) {
        com.wasteland.character.PlayerCharacter character =
            com.wasteland.character.CharacterManager.getCharacter(playerId);
        if (character == null) return 0;

        return getEquipment(playerId).getTotalAC(character);
    }

    /**
     * Get total AC for a player
     */
    public int getTotalAC(Player player) {
        return getTotalAC(player.getUUID());
    }

    /**
     * Get total EV modifier for a player
     */
    public int getTotalEVModifier(UUID playerId) {
        com.wasteland.character.PlayerCharacter character =
            com.wasteland.character.CharacterManager.getCharacter(playerId);
        if (character == null) return 0;

        return getEquipment(playerId).getTotalEVModifier(character);
    }

    /**
     * Get total EV modifier for a player
     */
    public int getTotalEVModifier(Player player) {
        return getTotalEVModifier(player.getUUID());
    }

    /**
     * Update player character stats based on equipped items
     */
    private void updatePlayerStats(UUID playerId) {
        com.wasteland.character.PlayerCharacter character =
            com.wasteland.character.CharacterManager.getCharacter(playerId);
        if (character == null) {
            LOGGER.warn("Cannot update stats for player {} - no character found", playerId);
            return;
        }

        PlayerEquipment equipment = getEquipment(playerId);
        int[] statBonuses = equipment.getTotalStatBonuses();

        // Update character with equipment bonuses
        character.updateEquipmentBonuses(statBonuses[0], statBonuses[1], statBonuses[2]);

        LOGGER.debug("Updated stats for player {}: STR+{}, DEX+{}, INT+{}",
                    playerId, statBonuses[0], statBonuses[1], statBonuses[2]);
    }

    /**
     * Save player equipment to NBT
     * Called when player data is saved
     */
    public CompoundTag savePlayerEquipment(UUID playerId) {
        PlayerEquipment equipment = playerEquipment.get(playerId);
        if (equipment == null) {
            return new CompoundTag();
        }

        return equipment.saveToNBT();
    }

    /**
     * Load player equipment from NBT
     * Called when player data is loaded
     */
    public void loadPlayerEquipment(UUID playerId, CompoundTag nbt) {
        if (nbt == null || nbt.isEmpty()) {
            return;
        }

        PlayerEquipment equipment = getEquipment(playerId);
        equipment.loadFromNBT(nbt);

        // Update character stats after loading equipment
        updatePlayerStats(playerId);

        LOGGER.info("Loaded equipment for player {}", playerId);
    }

    /**
     * Remove player equipment data (when player leaves server)
     */
    public void removePlayer(UUID playerId) {
        playerEquipment.remove(playerId);
        LOGGER.debug("Removed equipment data for player {}", playerId);
    }

    /**
     * Clear all equipment data (for server shutdown)
     */
    public void clearAll() {
        playerEquipment.clear();
        LOGGER.info("Cleared all equipment data");
    }

    /**
     * Auto-equip an item if the slot is empty
     * Used for loot pickup
     * @return true if item was equipped, false otherwise
     */
    public boolean autoEquipIfEmpty(Player player, ItemStack itemStack) {
        if (!WastelandItem.isWastelandItem(itemStack)) {
            return false;
        }

        CompoundTag tag = itemStack.getTag();
        if (tag == null) return false;

        ArmorSlot targetSlot = null;

        // Determine target slot
        if (tag.contains("WastelandWeaponType")) {
            targetSlot = ArmorSlot.WEAPON;
        } else if (tag.contains("WastelandArmorType")) {
            try {
                ArmorType armorType = ArmorType.valueOf(tag.getString("WastelandArmorType"));
                targetSlot = armorType.getSlot();
            } catch (IllegalArgumentException e) {
                return false;
            }
        }

        if (targetSlot == null) {
            return false;
        }

        // Check if slot is empty
        PlayerEquipment equipment = getEquipment(player);
        if (!equipment.isSlotEmpty(targetSlot)) {
            return false; // Slot occupied
        }

        // Auto-equip
        equipItem(player, targetSlot, itemStack);
        LOGGER.info("Auto-equipped {} in {} for player {}",
                   itemStack.getHoverName().getString(), targetSlot, player.getName().getString());

        return true;
    }

    /**
     * Get a summary of equipped items for a player (for display)
     */
    public String getEquipmentSummary(UUID playerId) {
        PlayerEquipment equipment = playerEquipment.get(playerId);
        if (equipment == null) {
            return "No equipment";
        }

        StringBuilder sb = new StringBuilder();
        sb.append("=== Equipment ===\n");

        for (Map.Entry<ArmorSlot, ItemStack> entry : equipment.getAllEquippedItems().entrySet()) {
            if (!entry.getValue().isEmpty()) {
                sb.append(entry.getKey().getDisplayName())
                  .append(": ")
                  .append(entry.getValue().getHoverName().getString())
                  .append("\n");
            }
        }

        // Show total stats
        int[] stats = equipment.getTotalStatBonuses();
        if (stats[0] != 0 || stats[1] != 0 || stats[2] != 0) {
            sb.append("\nTotal Bonuses:\n");
            if (stats[0] != 0) sb.append("  STR: ").append(stats[0] > 0 ? "+" : "").append(stats[0]).append("\n");
            if (stats[1] != 0) sb.append("  DEX: ").append(stats[1] > 0 ? "+" : "").append(stats[1]).append("\n");
            if (stats[2] != 0) sb.append("  INT: ").append(stats[2] > 0 ? "+" : "").append(stats[2]).append("\n");
        }

        return sb.toString();
    }
}
