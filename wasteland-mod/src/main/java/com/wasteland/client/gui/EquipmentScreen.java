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
    private static final int EQUIPMENT_COL1_X = 20;   // First column of equipment (Weapon, Body, Helmet, Cloak, Boots)
    private static final int EQUIPMENT_COL2_X = 160;  // Second column of equipment (Offhand, Gloves, Amulet, Rings)
    private static final int EQUIPMENT_Y = 40;
    private static final int STATS_PANEL_X = 300;
    private static final int STATS_PANEL_Y = 40;
    private static final int INVENTORY_PANEL_X = 440;
    private static final int INVENTORY_PANEL_Y = 40;
    private static final int INVENTORY_COLS = 9;
    private static final int INVENTORY_ROWS = 4; // Player inventory (not hotbar)

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

        // Middle panel: Stats
        renderStats(graphics);

        // Right panel: Inventory
        renderInventory(graphics, mouseX, mouseY);
    }

    /**
     * Render all equipment slots in two columns
     */
    private void renderEquipmentSlots(GuiGraphics graphics, int mouseX, int mouseY) {
        // Column 1: Weapon, Body, Helmet, Cloak, Boots
        EquipmentSlot[] column1 = {
            EquipmentSlot.WEAPON,
            EquipmentSlot.BODY,
            EquipmentSlot.HELMET,
            EquipmentSlot.CLOAK,
            EquipmentSlot.BOOTS
        };

        // Column 2: Offhand, Gloves, Amulet, Left Ring, Right Ring
        EquipmentSlot[] column2 = {
            EquipmentSlot.OFFHAND,
            EquipmentSlot.GLOVES,
            EquipmentSlot.AMULET,
            EquipmentSlot.LEFT_RING,
            EquipmentSlot.RIGHT_RING
        };

        // Render column 1
        int x = EQUIPMENT_COL1_X;
        int y = EQUIPMENT_Y;
        for (EquipmentSlot slot : column1) {
            renderEquipmentSlot(graphics, slot, x, y, mouseX, mouseY);
            y += SLOT_SPACING;
        }

        // Render column 2
        x = EQUIPMENT_COL2_X;
        y = EQUIPMENT_Y;
        for (EquipmentSlot slot : column2) {
            renderEquipmentSlot(graphics, slot, x, y, mouseX, mouseY);
            y += SLOT_SPACING;
        }
    }

    /**
     * Render a single equipment slot
     */
    private void renderEquipmentSlot(GuiGraphics graphics, EquipmentSlot slot, int x, int y, int mouseX, int mouseY) {
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
    }

    /**
     * Render the stats panel
     */
    private void renderStats(GuiGraphics graphics) {
        int x = STATS_PANEL_X;
        int y = STATS_PANEL_Y;

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
     * Render the player inventory
     */
    private void renderInventory(GuiGraphics graphics, int mouseX, int mouseY) {
        int x = INVENTORY_PANEL_X;
        int y = INVENTORY_PANEL_Y;

        // Title
        graphics.drawString(this.font, "Inventory", x, y - 12, 0xFFFF00, false);

        // Render inventory slots (skip hotbar, show main inventory)
        for (int row = 0; row < INVENTORY_ROWS; row++) {
            for (int col = 0; col < INVENTORY_COLS; col++) {
                int slotX = x + col * SLOT_SPACING;
                int slotY = y + row * SLOT_SPACING;

                // Draw slot background
                renderSlotBackground(graphics, slotX, slotY);

                // Get item in this slot (slots 9-35 are main inventory, skip 0-8 hotbar)
                int slotIndex = 9 + (row * INVENTORY_COLS + col);
                if (slotIndex < player.getInventory().getContainerSize()) {
                    ItemStack stack = player.getInventory().getItem(slotIndex);
                    if (!stack.isEmpty()) {
                        graphics.renderItem(stack, slotX + 1, slotY + 1);
                        graphics.renderItemDecorations(this.font, stack, slotX + 1, slotY + 1);

                        // Show tooltip on hover
                        if (isMouseOver(mouseX, mouseY, slotX, slotY, SLOT_SIZE, SLOT_SIZE)) {
                            graphics.renderTooltip(this.font, stack, mouseX, mouseY);
                        }
                    }
                }
            }
        }
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

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        // Only handle left click
        if (button != 0) {
            return super.mouseClicked(mouseX, mouseY, button);
        }

        // Check equipment slots - Column 1
        EquipmentSlot[] column1 = {
            EquipmentSlot.WEAPON,
            EquipmentSlot.BODY,
            EquipmentSlot.HELMET,
            EquipmentSlot.CLOAK,
            EquipmentSlot.BOOTS
        };
        int x = EQUIPMENT_COL1_X;
        int y = EQUIPMENT_Y;
        for (EquipmentSlot slot : column1) {
            if (isMouseOver((int)mouseX, (int)mouseY, x, y, SLOT_SIZE, SLOT_SIZE)) {
                handleSlotClick(slot);
                return true;
            }
            y += SLOT_SPACING;
        }

        // Check equipment slots - Column 2
        EquipmentSlot[] column2 = {
            EquipmentSlot.OFFHAND,
            EquipmentSlot.GLOVES,
            EquipmentSlot.AMULET,
            EquipmentSlot.LEFT_RING,
            EquipmentSlot.RIGHT_RING
        };
        x = EQUIPMENT_COL2_X;
        y = EQUIPMENT_Y;
        for (EquipmentSlot slot : column2) {
            if (isMouseOver((int)mouseX, (int)mouseY, x, y, SLOT_SIZE, SLOT_SIZE)) {
                handleSlotClick(slot);
                return true;
            }
            y += SLOT_SPACING;
        }

        // Check inventory slots
        for (int row = 0; row < INVENTORY_ROWS; row++) {
            for (int col = 0; col < INVENTORY_COLS; col++) {
                int slotX = INVENTORY_PANEL_X + col * SLOT_SPACING;
                int slotY = INVENTORY_PANEL_Y + row * SLOT_SPACING;

                if (isMouseOver((int)mouseX, (int)mouseY, slotX, slotY, SLOT_SIZE, SLOT_SIZE)) {
                    int slotIndex = 9 + (row * INVENTORY_COLS + col);
                    handleInventoryClick(slotIndex);
                    return true;
                }
            }
        }

        return super.mouseClicked(mouseX, mouseY, button);
    }

    /**
     * Handle clicking on an equipment slot
     */
    private void handleSlotClick(EquipmentSlot slot) {
        ItemStack equipped = equippedItems.getOrDefault(slot, ItemStack.EMPTY);
        ItemStack carried = this.minecraft.player.containerMenu.getCarried();

        if (!carried.isEmpty()) {
            // Player is holding an item - try to equip it
            if (canEquipToSlot(carried, slot)) {
                // Swap: put carried item in slot, previous item goes to cursor
                ItemStack previouslyEquipped = EquipmentManager.equip(player, slot, carried.copy());
                this.minecraft.player.containerMenu.setCarried(previouslyEquipped);
                // Refresh display
                this.equippedItems = EquipmentManager.getAllEquipped(player);
            }
        } else if (!equipped.isEmpty()) {
            // Player clicked on equipped item with empty hand - unequip it
            ItemStack unequipped = EquipmentManager.unequip(player, slot);
            this.minecraft.player.containerMenu.setCarried(unequipped);
            // Refresh display
            this.equippedItems = EquipmentManager.getAllEquipped(player);
        }
    }

    /**
     * Handle clicking on an inventory slot
     */
    private void handleInventoryClick(int slotIndex) {
        ItemStack clickedStack = player.getInventory().getItem(slotIndex);
        ItemStack carried = this.minecraft.player.containerMenu.getCarried();

        if (!carried.isEmpty()) {
            // Player is holding an item - put it in the inventory slot
            player.getInventory().setItem(slotIndex, carried.copy());
            this.minecraft.player.containerMenu.setCarried(ItemStack.EMPTY);
        } else if (!clickedStack.isEmpty()) {
            // Player clicked on an item - pick it up
            player.getInventory().setItem(slotIndex, ItemStack.EMPTY);
            this.minecraft.player.containerMenu.setCarried(clickedStack.copy());
        }
    }

    /**
     * Check if an item can be equipped to a specific slot
     */
    private boolean canEquipToSlot(ItemStack stack, EquipmentSlot slot) {
        // Check if the item is a weapon or armor
        boolean isWeapon = stack.getItem() instanceof com.wasteland.item.WastelandWeaponItem;
        boolean isArmor = stack.getItem() instanceof com.wasteland.item.WastelandArmorItem;

        // Weapon slots can only hold weapons
        if (slot == EquipmentSlot.WEAPON || slot == EquipmentSlot.OFFHAND) {
            return isWeapon;
        }

        // Armor slots can only hold armor, and must match the slot type
        if (slot.canHoldArmor()) {
            if (!isArmor) return false;

            // Check armor matches the slot
            com.wasteland.loot.WastelandArmor armor = com.wasteland.item.WastelandArmorItem.createArmor(stack);
            if (armor == null) return false;

            // Match armor type to slot
            com.wasteland.loot.ArmorType armorType = armor.getArmorType();
            return switch (slot) {
                case BODY -> armorType == com.wasteland.loot.ArmorType.ROBE ||
                            armorType == com.wasteland.loot.ArmorType.LEATHER_ARMOR ||
                            armorType == com.wasteland.loot.ArmorType.RING_MAIL ||
                            armorType == com.wasteland.loot.ArmorType.SCALE_MAIL ||
                            armorType == com.wasteland.loot.ArmorType.CHAIN_MAIL ||
                            armorType == com.wasteland.loot.ArmorType.PLATE_ARMOR;
                case HELMET -> armorType == com.wasteland.loot.ArmorType.HELMET;
                case CLOAK -> armorType == com.wasteland.loot.ArmorType.CLOAK;
                case GLOVES -> armorType == com.wasteland.loot.ArmorType.GLOVES;
                case BOOTS -> armorType == com.wasteland.loot.ArmorType.BOOTS;
                default -> false;
            };
        }

        // For jewelry slots (ring, amulet), allow any item for now
        // TODO: Add jewelry items
        return slot.canHoldJewelry();
    }
}
