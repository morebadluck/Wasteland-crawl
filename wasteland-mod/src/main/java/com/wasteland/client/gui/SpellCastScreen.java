package com.wasteland.client.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.wasteland.character.PlayerCharacter;
import com.wasteland.magic.Spell;
import com.wasteland.magic.SpellManager;
import com.wasteland.magic.SpellTargeting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * Screen for casting spells - shows memorized spells and allows selection
 */
public class SpellCastScreen extends Screen {
    private final PlayerCharacter character;
    private final SpellManager spellManager;
    private final List<Spell> memorizedSpells;

    private int selectedIndex = 0;
    private static final int SPELL_ENTRY_HEIGHT = 20;
    private static final int TOP_MARGIN = 40;

    public SpellCastScreen(PlayerCharacter character) {
        super(Component.literal("Cast Spell"));
        this.character = character;
        this.spellManager = SpellManager.getInstance(character.getPlayerId());
        this.memorizedSpells = spellManager.getMemorizedSpells();
    }

    @Override
    public boolean isPauseScreen() {
        return false; // Don't pause the game
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        renderBackground(graphics);

        PoseStack poseStack = graphics.pose();

        // Title
        graphics.drawCenteredString(this.font, "§e§lCast Spell (z)", this.width / 2, 10, 0xFFFFFF);

        // Instructions
        graphics.drawCenteredString(this.font, "§7Select a spell to cast", this.width / 2, 25, 0xAAAAAA);

        if (memorizedSpells.isEmpty()) {
            graphics.drawCenteredString(this.font,
                "§cNo spells memorized!",
                this.width / 2,
                this.height / 2,
                0xFF0000);
            graphics.drawCenteredString(this.font,
                "§7Press M to memorize spells",
                this.width / 2,
                this.height / 2 + 15,
                0xAAAAAA);
            return;
        }

        // Spell list
        int y = TOP_MARGIN;
        for (int i = 0; i < memorizedSpells.size(); i++) {
            Spell spell = memorizedSpells.get(i);
            boolean selected = (i == selectedIndex);

            // Background for selected spell
            if (selected) {
                graphics.fill(20, y - 2, this.width - 20, y + SPELL_ENTRY_HEIGHT - 2, 0x80FFAA00);
            }

            // Spell slot letter (a-z)
            char slotLetter = (char) ('a' + i);
            String slotText = "§e" + slotLetter + ")§r ";

            // Spell name and info
            int mpCost = spell.calculateMPCost(character);
            int spellPower = spell.calculateSpellPower(character);

            String spellText = String.format("%s - %s §7(MP: %d, Pow: %d)§r",
                spell.getDisplayName(),
                spell.getTargetType().getDisplayName(),
                mpCost,
                spellPower
            );

            // Check if player has enough MP
            String color = character.getCurrentMP() >= mpCost ? "§f" : "§8";
            graphics.drawString(this.font, slotText + color + spellText, 25, y, 0xFFFFFF);

            y += SPELL_ENTRY_HEIGHT;
        }

        // Footer instructions
        graphics.drawCenteredString(this.font,
            "§7[a-z] Select | [Enter/Click] Cast | [Esc] Cancel",
            this.width / 2,
            this.height - 20,
            0xAAAAAA);

        super.render(graphics, mouseX, mouseY, partialTick);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (memorizedSpells.isEmpty()) {
            this.onClose();
            return true;
        }

        // Up arrow - previous spell
        if (keyCode == 265) { // GLFW.GLFW_KEY_UP
            selectedIndex = (selectedIndex - 1 + memorizedSpells.size()) % memorizedSpells.size();
            return true;
        }

        // Down arrow - next spell
        if (keyCode == 264) { // GLFW.GLFW_KEY_DOWN
            selectedIndex = (selectedIndex + 1) % memorizedSpells.size();
            return true;
        }

        // Enter - cast selected spell
        if (keyCode == 257) { // GLFW.GLFW_KEY_ENTER
            castSelectedSpell();
            return true;
        }

        // Escape - close screen
        if (keyCode == 256) { // GLFW.GLFW_KEY_ESCAPE
            this.onClose();
            return true;
        }

        // Letter keys (a-z) for direct selection
        if (keyCode >= 65 && keyCode <= 90) { // A-Z
            int index = keyCode - 65; // Convert to 0-based index
            if (index < memorizedSpells.size()) {
                selectedIndex = index;
                castSelectedSpell();
                return true;
            }
        }

        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (memorizedSpells.isEmpty()) {
            return super.mouseClicked(mouseX, mouseY, button);
        }

        // Check if clicked on a spell entry
        int y = TOP_MARGIN;
        for (int i = 0; i < memorizedSpells.size(); i++) {
            if (mouseY >= y - 2 && mouseY < y + SPELL_ENTRY_HEIGHT - 2) {
                selectedIndex = i;
                castSelectedSpell();
                return true;
            }
            y += SPELL_ENTRY_HEIGHT;
        }

        return super.mouseClicked(mouseX, mouseY, button);
    }

    /**
     * Cast the selected spell
     */
    private void castSelectedSpell() {
        if (selectedIndex < 0 || selectedIndex >= memorizedSpells.size()) {
            return;
        }

        Spell spell = memorizedSpells.get(selectedIndex);
        Minecraft mc = Minecraft.getInstance();

        // Check MP cost
        int mpCost = spell.calculateMPCost(character);
        if (character.getCurrentMP() < mpCost) {
            mc.player.displayClientMessage(
                Component.literal("§cNot enough MP! Need " + mpCost + " MP."),
                true
            );
            this.onClose();
            return;
        }

        // Close screen and start targeting
        this.onClose();

        // Start targeting mode
        SpellTargeting.getInstance().startTargeting(spell, mc.player);
    }
}
