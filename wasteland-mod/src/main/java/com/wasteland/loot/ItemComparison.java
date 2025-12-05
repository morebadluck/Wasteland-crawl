package com.wasteland.loot;

import com.wasteland.equipment.EquipmentSlot;
import com.wasteland.equipment.WeaponType;
import com.wasteland.item.WastelandArmorItem;
import com.wasteland.item.WastelandWeaponItem;
import com.wasteland.player.EquipmentManager;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

/**
 * Utility class for comparing items and generating comparison tooltips
 * Inspired by DCSS's equipment comparison system
 */
public class ItemComparison {

    /**
     * Generate comparison tooltip for an item vs currently equipped item
     * Returns null if no comparison needed
     */
    public static List<Component> getComparisonTooltip(Player player, ItemStack hovered) {
        if (hovered.isEmpty()) return null;

        // Determine which slot this item would go in
        EquipmentSlot targetSlot = getTargetSlot(hovered);
        if (targetSlot == null) return null;

        // Get currently equipped item in that slot
        ItemStack equipped = EquipmentManager.getEquipped(player, targetSlot);
        if (equipped.isEmpty()) {
            // No equipped item to compare to
            return getEquipMessage(hovered, targetSlot);
        }

        // Generate comparison
        return compareItems(hovered, equipped, targetSlot);
    }

    /**
     * Get "would equip" message for empty slot
     */
    private static List<Component> getEquipMessage(ItemStack item, EquipmentSlot slot) {
        List<Component> lines = new ArrayList<>();
        lines.add(Component.literal(""));
        lines.add(Component.literal("If you equip this item:")
            .withStyle(ChatFormatting.YELLOW));

        // Show stats that would be gained
        WastelandWeapon weapon = WastelandWeaponItem.createWeapon(item);
        if (weapon != null) {
            int damage = weapon.getWeaponType().getBaseDamage() + weapon.getEnchantmentLevel();
            lines.add(Component.literal("  Damage: +" + damage)
                .withStyle(ChatFormatting.GREEN));
        }

        WastelandArmor armor = WastelandArmorItem.createArmor(item);
        if (armor != null) {
            int ac = armor.getArmorType().getBaseAC() + armor.getEnchantmentLevel();
            int ev = armor.getArmorType().getBaseEVPenalty();
            lines.add(Component.literal("  AC: +" + ac)
                .withStyle(ChatFormatting.GREEN));
            if (ev != 0) {
                lines.add(Component.literal("  EV: " + (ev > 0 ? "+" : "") + ev)
                    .withStyle(ev < 0 ? ChatFormatting.RED : ChatFormatting.GREEN));
            }
        }

        return lines;
    }

    /**
     * Compare two items and generate tooltip
     */
    private static List<Component> compareItems(ItemStack newItem, ItemStack oldItem, EquipmentSlot slot) {
        List<Component> lines = new ArrayList<>();
        lines.add(Component.literal(""));
        lines.add(Component.literal("If you switch to this " + slot.getDisplayName().toLowerCase() + ":")
            .withStyle(ChatFormatting.YELLOW));

        // Compare weapons
        if (slot.canHoldWeapon()) {
            compareWeapons(lines, newItem, oldItem);
        }

        // Compare armor
        if (slot.canHoldArmor()) {
            compareArmor(lines, newItem, oldItem);
        }

        return lines;
    }

    /**
     * Compare weapon stats
     */
    private static void compareWeapons(List<Component> lines, ItemStack newItem, ItemStack oldItem) {
        WastelandWeapon newWeapon = WastelandWeaponItem.createWeapon(newItem);
        WastelandWeapon oldWeapon = WastelandWeaponItem.createWeapon(oldItem);

        if (newWeapon == null || oldWeapon == null) return;

        // Compare damage
        int newDamage = newWeapon.getWeaponType().getBaseDamage() + newWeapon.getEnchantmentLevel();
        int oldDamage = oldWeapon.getWeaponType().getBaseDamage() + oldWeapon.getEnchantmentLevel();
        addStatComparison(lines, "Damage", oldDamage, newDamage);

        // Compare attack delay (lower is better)
        int newDelay = newWeapon.getWeaponType().getBaseDelay();
        int oldDelay = oldWeapon.getWeaponType().getBaseDelay();
        if (newDelay != oldDelay) {
            addStatComparison(lines, "Attack Speed", oldDelay, newDelay, true);
        }

        // Calculate DPS comparison
        double newDPS = (double) newDamage / newDelay;
        double oldDPS = (double) oldDamage / oldDelay;
        double dpsDiff = newDPS - oldDPS;
        if (Math.abs(dpsDiff) > 0.01) {
            String dpsText = String.format("  DPS: %.1f -> %.1f (%s%.1f)",
                oldDPS, newDPS,
                dpsDiff > 0 ? "+" : "",
                dpsDiff);
            lines.add(Component.literal(dpsText)
                .withStyle(dpsDiff > 0 ? ChatFormatting.GREEN : ChatFormatting.RED));
        }
    }

    /**
     * Compare armor stats
     */
    private static void compareArmor(List<Component> lines, ItemStack newItem, ItemStack oldItem) {
        WastelandArmor newArmor = WastelandArmorItem.createArmor(newItem);
        WastelandArmor oldArmor = WastelandArmorItem.createArmor(oldItem);

        if (newArmor == null || oldArmor == null) return;

        // Compare AC
        int newAC = newArmor.getArmorType().getBaseAC() + newArmor.getEnchantmentLevel();
        int oldAC = oldArmor.getArmorType().getBaseAC() + oldArmor.getEnchantmentLevel();
        addStatComparison(lines, "AC", oldAC, newAC);

        // Compare EV penalty
        int newEV = newArmor.getArmorType().getBaseEVPenalty();
        int oldEV = oldArmor.getArmorType().getBaseEVPenalty();
        if (newEV != oldEV) {
            addStatComparison(lines, "EV", oldEV, newEV);
        }
    }

    /**
     * Add a stat comparison line (e.g., "AC: 5 -> 8 (+3)")
     * @param invert If true, lower values are better (e.g., delay)
     */
    private static void addStatComparison(List<Component> lines, String statName, int oldValue, int newValue, boolean invert) {
        if (oldValue == newValue) {
            lines.add(Component.literal("  " + statName + ": unchanged")
                .withStyle(ChatFormatting.GRAY));
            return;
        }

        int diff = newValue - oldValue;
        boolean better = invert ? (diff < 0) : (diff > 0);

        String diffText = (diff > 0 ? "+" : "") + diff;
        String fullText = String.format("  %s: %d -> %d (%s)",
            statName, oldValue, newValue, diffText);

        lines.add(Component.literal(fullText)
            .withStyle(better ? ChatFormatting.GREEN : ChatFormatting.RED));
    }

    /**
     * Overload for normal stats where higher is better
     */
    private static void addStatComparison(List<Component> lines, String statName, int oldValue, int newValue) {
        addStatComparison(lines, statName, oldValue, newValue, false);
    }

    /**
     * Determine which equipment slot an item would go in
     */
    private static EquipmentSlot getTargetSlot(ItemStack stack) {
        // Check if it's a weapon
        if (stack.getItem() instanceof WastelandWeaponItem) {
            return EquipmentSlot.WEAPON;
        }

        // Check if it's armor
        if (stack.getItem() instanceof WastelandArmorItem armorItem) {
            ArmorType type = armorItem.getArmorType();
            return switch (type.getSlot()) {
                case BODY -> EquipmentSlot.BODY;
                case HELMET -> EquipmentSlot.HELMET;
                case CLOAK -> EquipmentSlot.CLOAK;
                case GLOVES -> EquipmentSlot.GLOVES;
                case BOOTS -> EquipmentSlot.BOOTS;
                default -> null; // Shouldn't happen for armor, but cover all cases
            };
        }

        return null;
    }
}
