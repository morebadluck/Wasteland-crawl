package com.wasteland.player;

import com.wasteland.equipment.WeaponType;
import com.wasteland.item.ModItems;
import com.wasteland.loot.ArmorType;
import com.wasteland.loot.ItemRarity;
import com.wasteland.loot.WastelandArmor;
import com.wasteland.loot.WastelandWeapon;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Provides starter equipment for new players
 */
public class StarterEquipment {
    private static final Logger LOGGER = LogManager.getLogger();

    /**
     * Give starter equipment to a new player
     */
    public static void giveStarterEquipment(Player player) {
        LOGGER.info("Creating starter equipment for player");

        // Give starter weapon (shiv - common, no enchantment)
        ItemStack starterWeapon = createStarterWeapon();
        LOGGER.info("Created starter weapon: {} (empty: {})", starterWeapon, starterWeapon.isEmpty());

        boolean weaponAdded = player.getInventory().add(starterWeapon);
        LOGGER.info("Weapon added to inventory: {}", weaponAdded);

        if (!weaponAdded) {
            // If inventory is full, drop it on the ground
            LOGGER.info("Dropping weapon on ground");
            player.drop(starterWeapon, false);
        }

        // Give starter armor (robe - common, no enchantment)
        ItemStack starterArmor = createStarterArmor();
        LOGGER.info("Created starter armor: {} (empty: {})", starterArmor, starterArmor.isEmpty());

        boolean armorAdded = player.getInventory().add(starterArmor);
        LOGGER.info("Armor added to inventory: {}", armorAdded);

        if (!armorAdded) {
            // If inventory is full, drop it on the ground
            LOGGER.info("Dropping armor on ground");
            player.drop(starterArmor, false);
        }

        // Mark as received
        markStarterEquipmentReceived(player);

        LOGGER.info("Inventory contents: {}", player.getInventory().items);
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
    public static void markStarterEquipmentReceived(Player player) {
        var data = player.getPersistentData();
        data.putBoolean("wasteland:received_starter_equipment", true);
    }
}
