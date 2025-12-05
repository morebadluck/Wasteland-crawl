package com.wasteland.client.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.wasteland.equipment.EquipmentSlot;
import com.wasteland.loot.WastelandArmor;
import com.wasteland.loot.WastelandWeapon;
import com.wasteland.player.EquipmentManager;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import java.util.Map;

/**
 * Equipment screen showing all equipped items and character stats
 */
public class EquipmentScreen extends Screen {

    private static final int SLOT_SIZE = 18;
    private static final int SLOT_SPACING = 20;
    private static final int LEFT_PANEL_X = 20;
    private static final int LEFT_PANEL_Y = 40;
    private static final int RIGHT_PANEL_X = 200;
    private static final int RIGHT_PANEL_Y = 40;

    private final Player player;
    private Map<EquipmentSlot, ItemStack> equippedItems;

    public EquipmentScreen(Player player) {
        super(Component.literal("Equipment"));
        this.player = player;
        this.equippedItems = EquipmentManager.getAllEquipped(player);
    }

    @Override
    protected void init() {
        super.init();
        // Refresh equipped items when screen opens
        this.equippedItems = EquipmentManager.getAllEquipped(player);
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        this.renderBackground(graphics);
        super.render(graphics, mouseX, mouseY, partialTick);

        // Title
        graphics.drawCenteredString(this.font, this.title, this.width / 2, 20, 0xFFFFFF);

        // Left panel: Equipment slots
        renderEquipmentSlots(graphics, mouseX, mouseY);

        // Right panel: Stats
        renderStats(graphics);
    }

    /**
     * Render all equipment slots
     */
    private void renderEquipmentSlots(GuiGraphics graphics, int mouseX, int mouseY) {
        int x = LEFT_PANEL_X;
        int y = LEFT_PANEL_Y;

        for (EquipmentSlot slot : EquipmentSlot.values()) {
            // Draw slot background
            renderSlotBackground(graphics, x, y);

            // Draw slot label
            graphics.drawString(this.font, slot.getDisplayName() + " (" + slot.getSlotKey() + ")",
                x + SLOT_SIZE + 4, y + 5, 0xFFFFFF, false);

            // Draw equipped item if present
            ItemStack equipped = equippedItems.getOrDefault(slot, ItemStack.EMPTY);
            if (!equipped.isEmpty()) {
                graphics.renderItem(equipped, x + 1, y + 1);
                graphics.renderItemDecorations(this.font, equipped, x + 1, y + 1);

                // Show tooltip on hover
                if (isMouseOver(mouseX, mouseY, x, y, SLOT_SIZE, SLOT_SIZE)) {
                    graphics.renderTooltip(this.font, equipped, mouseX, mouseY);
                }
            }

            y += SLOT_SPACING;
        }
    }

    /**
     * Render the stats panel
     */
    private void renderStats(GuiGraphics graphics) {
        int x = RIGHT_PANEL_X;
        int y = RIGHT_PANEL_Y;

        graphics.drawString(this.font, "Character Stats", x, y, 0xFFFF00, false);
        y += 15;

        // Calculate total AC and EV from equipped items
        int totalAC = 0;
        int totalEV = 0;
        int totalDamage = 0;

        // Sum up armor bonuses
        for (Map.Entry<EquipmentSlot, ItemStack> entry : equippedItems.entrySet()) {
            ItemStack stack = entry.getValue();
            if (stack.isEmpty()) continue;

            // Check if it's armor
            if (entry.getKey().canHoldArmor()) {
                WastelandArmor armor = com.wasteland.item.WastelandArmorItem.createArmor(stack);
                if (armor != null) {
                    // AC = base + enchantment
                    totalAC += armor.getArmorType().getBaseAC() + armor.getEnchantmentLevel();
                    // EV penalty from armor type
                    totalEV += armor.getArmorType().getBaseEVPenalty();
                }
            }

            // Check if it's a weapon
            if (entry.getKey().canHoldWeapon()) {
                WastelandWeapon weapon = com.wasteland.item.WastelandWeaponItem.createWeapon(stack);
                if (weapon != null) {
                    // Damage = base + enchantment
                    totalDamage = weapon.getWeaponType().getBaseDamage() + weapon.getEnchantmentLevel();
                }
            }
        }

        // Display stats
        graphics.drawString(this.font, "HP: " + (int)player.getHealth() + " / " + (int)player.getMaxHealth(),
            x, y, 0xFFFFFF, false);
        y += 12;

        graphics.drawString(this.font, "AC: " + totalAC, x, y, 0xFFFFFF, false);
        y += 12;

        graphics.drawString(this.font, "EV: " + (10 + totalEV), x, y, 0xFFFFFF, false);
        y += 12;

        if (totalDamage > 0) {
            graphics.drawString(this.font, "Damage: " + totalDamage, x, y, 0xFFFFFF, false);
            y += 12;
        }

        y += 10;
        graphics.drawString(this.font, "Press 'E' to close", x, y, 0x888888, false);
    }

    /**
     * Render a slot background (simple gray rectangle)
     */
    private void renderSlotBackground(GuiGraphics graphics, int x, int y) {
        // Dark gray background
        graphics.fill(x, y, x + SLOT_SIZE, y + SLOT_SIZE, 0xFF8B8B8B);
        // Light gray border (top/left)
        graphics.fill(x, y, x + SLOT_SIZE, y + 1, 0xFFFFFFFF);
        graphics.fill(x, y, x + 1, y + SLOT_SIZE, 0xFFFFFFFF);
        // Dark gray border (bottom/right)
        graphics.fill(x, y + SLOT_SIZE - 1, x + SLOT_SIZE, y + SLOT_SIZE, 0xFF373737);
        graphics.fill(x + SLOT_SIZE - 1, y, x + SLOT_SIZE, y + SLOT_SIZE, 0xFF373737);
    }

    /**
     * Check if mouse is over a rectangular area
     */
    private boolean isMouseOver(int mouseX, int mouseY, int x, int y, int width, int height) {
        return mouseX >= x && mouseX < x + width && mouseY >= y && mouseY < y + height;
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        // Close on 'E' key or ESC
        if (keyCode == 69 || keyCode == 256) { // E or ESC
            this.onClose();
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean isPauseScreen() {
        return false; // Don't pause the game when this screen is open
    }
}
