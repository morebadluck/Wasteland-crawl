package com.wasteland.player;

import com.wasteland.equipment.EquipmentSlot;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import java.util.EnumMap;
import java.util.Map;

/**
 * Manages equipped items for a player using persistent NBT data
 */
public class EquipmentManager {

    private static final String NBT_KEY = "wasteland:equipment";

    /**
     * Get the equipped item in a specific slot
     */
    public static ItemStack getEquipped(Player player, EquipmentSlot slot) {
        CompoundTag equipment = getEquipmentData(player);
        if (equipment.contains(slot.name(), Tag.TAG_COMPOUND)) {
            return ItemStack.of(equipment.getCompound(slot.name()));
        }
        return ItemStack.EMPTY;
    }

    /**
     * Equip an item in a specific slot
     * Returns the previously equipped item (or EMPTY if none)
     */
    public static ItemStack equip(Player player, EquipmentSlot slot, ItemStack item) {
        CompoundTag equipment = getEquipmentData(player);

        // Get previously equipped item
        ItemStack previous = ItemStack.EMPTY;
        if (equipment.contains(slot.name(), Tag.TAG_COMPOUND)) {
            previous = ItemStack.of(equipment.getCompound(slot.name()));
        }

        // Equip new item
        if (!item.isEmpty()) {
            CompoundTag itemTag = new CompoundTag();
            item.save(itemTag);
            equipment.put(slot.name(), itemTag);
        } else {
            equipment.remove(slot.name());
        }

        saveEquipmentData(player, equipment);
        return previous;
    }

    /**
     * Unequip an item from a specific slot
     * Returns the unequipped item (or EMPTY if none)
     */
    public static ItemStack unequip(Player player, EquipmentSlot slot) {
        return equip(player, slot, ItemStack.EMPTY);
    }

    /**
     * Get all equipped items as a map
     */
    public static Map<EquipmentSlot, ItemStack> getAllEquipped(Player player) {
        Map<EquipmentSlot, ItemStack> equipped = new EnumMap<>(EquipmentSlot.class);
        CompoundTag equipment = getEquipmentData(player);

        for (EquipmentSlot slot : EquipmentSlot.values()) {
            if (equipment.contains(slot.name(), Tag.TAG_COMPOUND)) {
                equipped.put(slot, ItemStack.of(equipment.getCompound(slot.name())));
            }
        }

        return equipped;
    }

    /**
     * Check if a slot has an item equipped
     */
    public static boolean isEquipped(Player player, EquipmentSlot slot) {
        return !getEquipped(player, slot).isEmpty();
    }

    /**
     * Clear all equipped items
     */
    public static void clearAll(Player player) {
        CompoundTag data = player.getPersistentData();
        data.remove(NBT_KEY);
    }

    /**
     * Get the equipment data NBT
     */
    private static CompoundTag getEquipmentData(Player player) {
        CompoundTag data = player.getPersistentData();
        if (!data.contains(NBT_KEY, Tag.TAG_COMPOUND)) {
            data.put(NBT_KEY, new CompoundTag());
        }
        return data.getCompound(NBT_KEY);
    }

    /**
     * Save the equipment data NBT
     */
    private static void saveEquipmentData(Player player, CompoundTag equipment) {
        CompoundTag data = player.getPersistentData();
        data.put(NBT_KEY, equipment);
    }
}
