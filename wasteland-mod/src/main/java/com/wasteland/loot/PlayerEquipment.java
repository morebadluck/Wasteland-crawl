package com.wasteland.loot;

import com.wasteland.equipment.WeaponType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.item.ItemStack;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.*;

/**
 * Manages equipped items for a single player.
 * Stores items in equipment slots and calculates total bonuses.
 */
public class PlayerEquipment {
    private static final Logger LOGGER = LogManager.getLogger();

    private final UUID playerId;
    private final Map<ArmorSlot, ItemStack> equippedItems;

    /**
     * Create new equipment set for a player
     */
    public PlayerEquipment(UUID playerId) {
        this.playerId = playerId;
        this.equippedItems = new EnumMap<>(ArmorSlot.class);

        // Initialize all slots to empty
        for (ArmorSlot slot : ArmorSlot.values()) {
            equippedItems.put(slot, ItemStack.EMPTY);
        }
    }

    /**
     * Equip an item in a specific slot
     * @param slot The equipment slot
     * @param itemStack The item to equip
     * @return The previously equipped item (or empty stack)
     */
    public ItemStack equipItem(ArmorSlot slot, ItemStack itemStack) {
        if (itemStack == null || itemStack.isEmpty()) {
            LOGGER.warn("Attempted to equip null/empty item in slot {}", slot);
            return ItemStack.EMPTY;
        }

        // Validate item type matches slot
        if (!canEquipInSlot(itemStack, slot)) {
            LOGGER.warn("Cannot equip {} in slot {}", itemStack.getHoverName().getString(), slot);
            return ItemStack.EMPTY;
        }

        ItemStack previousItem = equippedItems.get(slot);
        equippedItems.put(slot, itemStack.copy());

        LOGGER.info("Player {} equipped {} in {}", playerId, itemStack.getHoverName().getString(), slot);

        return previousItem.isEmpty() ? ItemStack.EMPTY : previousItem;
    }

    /**
     * Unequip item from a slot
     * @param slot The equipment slot
     * @return The unequipped item (or empty stack)
     */
    public ItemStack unequipItem(ArmorSlot slot) {
        ItemStack item = equippedItems.get(slot);
        equippedItems.put(slot, ItemStack.EMPTY);

        if (!item.isEmpty()) {
            LOGGER.info("Player {} unequipped {} from {}", playerId, item.getHoverName().getString(), slot);
        }

        return item.isEmpty() ? ItemStack.EMPTY : item;
    }

    /**
     * Get equipped item in a slot
     */
    public ItemStack getEquippedItem(ArmorSlot slot) {
        return equippedItems.getOrDefault(slot, ItemStack.EMPTY);
    }

    /**
     * Check if a slot is empty
     */
    public boolean isSlotEmpty(ArmorSlot slot) {
        return equippedItems.get(slot).isEmpty();
    }

    /**
     * Get equipped weapon (main hand)
     */
    public WastelandWeapon getEquippedWeapon() {
        ItemStack weapon = equippedItems.get(ArmorSlot.WEAPON);
        if (weapon.isEmpty() || !WastelandItem.isWastelandItem(weapon)) {
            return null;
        }

        // Try to determine weapon type from NBT
        CompoundTag tag = weapon.getTag();
        if (tag != null && tag.contains("WastelandWeaponType")) {
            try {
                WeaponType type = WeaponType.valueOf(tag.getString("WastelandWeaponType"));
                return new WastelandWeapon(weapon, type);
            } catch (IllegalArgumentException e) {
                LOGGER.error("Invalid weapon type in NBT", e);
            }
        }

        return null;
    }

    /**
     * Get equipped shield
     */
    public WastelandArmor getEquippedShield() {
        ItemStack shield = equippedItems.get(ArmorSlot.OFFHAND);
        if (shield.isEmpty() || !WastelandItem.isWastelandItem(shield)) {
            return null;
        }

        CompoundTag tag = shield.getTag();
        if (tag != null && tag.contains("WastelandArmorType")) {
            try {
                ArmorType type = ArmorType.valueOf(tag.getString("WastelandArmorType"));
                if (type.isShield()) {
                    return new WastelandArmor(shield, type);
                }
            } catch (IllegalArgumentException e) {
                LOGGER.error("Invalid armor type in NBT", e);
            }
        }

        return null;
    }

    /**
     * Get all equipped armor pieces (excluding weapon/shield)
     */
    public List<WastelandArmor> getEquippedArmor() {
        List<WastelandArmor> armor = new ArrayList<>();

        for (ArmorSlot slot : ArmorSlot.values()) {
            if (!slot.isArmorSlot()) continue;

            ItemStack item = equippedItems.get(slot);
            if (item.isEmpty() || !WastelandItem.isWastelandItem(item)) continue;

            CompoundTag tag = item.getTag();
            if (tag != null && tag.contains("WastelandArmorType")) {
                try {
                    ArmorType type = ArmorType.valueOf(tag.getString("WastelandArmorType"));
                    armor.add(new WastelandArmor(item, type));
                } catch (IllegalArgumentException e) {
                    LOGGER.error("Invalid armor type in NBT for slot {}", slot, e);
                }
            }
        }

        return armor;
    }

    /**
     * Calculate total stat bonuses from all equipped items
     * @return [STR, DEX, INT] bonuses
     */
    public int[] getTotalStatBonuses() {
        int str = 0, dex = 0, intel = 0;

        // Check weapon
        WastelandWeapon weapon = getEquippedWeapon();
        if (weapon != null) {
            if (weapon.hasArtifactProperty(ArtifactProperty.STRENGTH)) {
                str += weapon.getArtifactProperty(ArtifactProperty.STRENGTH);
            }
            if (weapon.hasArtifactProperty(ArtifactProperty.DEXTERITY)) {
                dex += weapon.getArtifactProperty(ArtifactProperty.DEXTERITY);
            }
            if (weapon.hasArtifactProperty(ArtifactProperty.INTELLIGENCE)) {
                intel += weapon.getArtifactProperty(ArtifactProperty.INTELLIGENCE);
            }
        }

        // Check all armor pieces
        for (WastelandArmor armor : getEquippedArmor()) {
            int[] armorStats = armor.getStatBonuses();
            str += armorStats[0];
            dex += armorStats[1];
            intel += armorStats[2];
        }

        // Check shield
        WastelandArmor shield = getEquippedShield();
        if (shield != null) {
            int[] shieldStats = shield.getStatBonuses();
            str += shieldStats[0];
            dex += shieldStats[1];
            intel += shieldStats[2];
        }

        return new int[]{str, dex, intel};
    }

    /**
     * Calculate total AC from equipped armor
     */
    public int getTotalAC(com.wasteland.character.PlayerCharacter character) {
        int totalAC = 0;

        for (WastelandArmor armor : getEquippedArmor()) {
            totalAC += armor.calculateAC(character);
        }

        WastelandArmor shield = getEquippedShield();
        if (shield != null) {
            totalAC += shield.calculateAC(character);
        }

        return totalAC;
    }

    /**
     * Calculate total EV modifier from equipped armor
     */
    public int getTotalEVModifier(com.wasteland.character.PlayerCharacter character) {
        int totalEV = 0;

        for (WastelandArmor armor : getEquippedArmor()) {
            totalEV += armor.calculateEVModifier(character);
        }

        WastelandArmor shield = getEquippedShield();
        if (shield != null) {
            totalEV += shield.calculateEVModifier(character);
        }

        return totalEV;
    }

    /**
     * Check if an item can be equipped in a slot
     */
    private boolean canEquipInSlot(ItemStack itemStack, ArmorSlot slot) {
        if (!WastelandItem.isWastelandItem(itemStack)) {
            return false; // Not a wasteland item
        }

        CompoundTag tag = itemStack.getTag();
        if (tag == null) return false;

        // Check weapon
        if (slot == ArmorSlot.WEAPON) {
            return tag.contains("WastelandWeaponType");
        }

        // Check armor/shield
        if (slot.isArmorSlot()) {
            if (!tag.contains("WastelandArmorType")) return false;

            try {
                ArmorType type = ArmorType.valueOf(tag.getString("WastelandArmorType"));
                return type.getSlot() == slot;
            } catch (IllegalArgumentException e) {
                return false;
            }
        }

        // TODO: Add jewelry validation when implemented
        return false;
    }

    /**
     * Save equipment to NBT
     */
    public CompoundTag saveToNBT() {
        CompoundTag nbt = new CompoundTag();

        ListTag itemsList = new ListTag();
        for (Map.Entry<ArmorSlot, ItemStack> entry : equippedItems.entrySet()) {
            if (entry.getValue().isEmpty()) continue;

            CompoundTag itemTag = new CompoundTag();
            itemTag.putString("Slot", entry.getKey().name());
            itemTag.put("Item", entry.getValue().save(new CompoundTag()));
            itemsList.add(itemTag);
        }

        nbt.put("EquippedItems", itemsList);
        return nbt;
    }

    /**
     * Load equipment from NBT
     */
    public void loadFromNBT(CompoundTag nbt) {
        // Clear current equipment
        for (ArmorSlot slot : ArmorSlot.values()) {
            equippedItems.put(slot, ItemStack.EMPTY);
        }

        if (!nbt.contains("EquippedItems")) return;

        ListTag itemsList = nbt.getList("EquippedItems", Tag.TAG_COMPOUND);
        for (int i = 0; i < itemsList.size(); i++) {
            CompoundTag itemTag = itemsList.getCompound(i);

            try {
                ArmorSlot slot = ArmorSlot.valueOf(itemTag.getString("Slot"));
                ItemStack item = ItemStack.of(itemTag.getCompound("Item"));

                if (!item.isEmpty()) {
                    equippedItems.put(slot, item);
                }
            } catch (IllegalArgumentException e) {
                LOGGER.error("Invalid equipment slot in NBT", e);
            }
        }

        LOGGER.debug("Loaded equipment for player {}: {} items", playerId,
                    equippedItems.values().stream().filter(s -> !s.isEmpty()).count());
    }

    /**
     * Get all equipped items (for display/management)
     */
    public Map<ArmorSlot, ItemStack> getAllEquippedItems() {
        return new EnumMap<>(equippedItems);
    }
}
