package com.wasteland.player;

import com.wasteland.equipment.WeaponType;
import com.wasteland.item.ModItems;
import com.wasteland.loot.ArmorType;
import com.wasteland.loot.ItemRarity;
import com.wasteland.loot.WastelandArmor;
import com.wasteland.loot.WastelandWeapon;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

/**
 * Provides starter equipment for new players
 */
public class StarterEquipment {

    /**
     * Give starter equipment to a new player
     */
    public static void giveStarterEquipment(Player player) {
        // Give starter weapon (shiv - common, no enchantment)
        ItemStack starterWeapon = createStarterWeapon();
        player.getInventory().add(starterWeapon);

        // Give starter armor (robe - common, no enchantment)
        ItemStack starterArmor = createStarterArmor();
        player.getInventory().add(starterArmor);

        // Mark as received
        markStarterEquipmentReceived(player);
    }

    /**
     * Create a starter weapon (Shiv)
     */
    private static ItemStack createStarterWeapon() {
        ItemStack stack = new ItemStack(ModItems.SHIV.get());

        // Initialize with common rarity, no enchantment
        WastelandWeapon weapon = new WastelandWeapon(stack, WeaponType.SHIV, ItemRarity.COMMON, 0);
        weapon.identify(); // Start identified
        weapon.saveToNBT();

        return stack;
    }

    /**
     * Create starter armor (Robe)
     */
    private static ItemStack createStarterArmor() {
        ItemStack stack = new ItemStack(ModItems.ROBE.get());

        // Initialize with common rarity, no enchantment
        WastelandArmor armor = new WastelandArmor(stack, ArmorType.ROBE, ItemRarity.COMMON, 0);
        armor.identify(); // Start identified
        armor.saveToNBT();

        return stack;
    }

    /**
     * Check if player has already received starter equipment
     * Uses persistent NBT data to track this
     */
    public static boolean hasReceivedStarterEquipment(Player player) {
        var data = player.getPersistentData();
        return data.getBoolean("wasteland:received_starter_equipment");
    }

    /**
     * Mark that the player has received starter equipment
     */
    private static void markStarterEquipmentReceived(Player player) {
        var data = player.getPersistentData();
        data.putBoolean("wasteland:received_starter_equipment", true);
    }
}
