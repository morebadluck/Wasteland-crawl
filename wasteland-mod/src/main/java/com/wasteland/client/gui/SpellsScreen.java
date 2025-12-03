package com.wasteland.client.gui;

import com.wasteland.character.*;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * Spells screen (DCSS 'M' key - Shift+M)
 * Shows memorized spells and allows learning/forgetting
 * Click spells to memorize/forget them
 */
public class SpellsScreen extends Screen {
    private final PlayerCharacter character;
    private static final int TEXT_COLOR = 0xFFFFFF;
    private static final int HEADER_COLOR = 0xFFFF00;
    private static final int MEMORIZED_COLOR = 0x00FF00;
    private static final int KNOWN_COLOR = 0xAAAA00;
    private static final int UNKNOWN_COLOR = 0x888888;
    private static final int HOVER_COLOR = 0xFFFFAA;

    private int scrollOffset = 0;
    private static final int MAX_LINES = 35;
    private static final float FONT_SCALE = 0.75f;
    private final int LINE_HEIGHT = (int)(12 * FONT_SCALE);

    // Track clickable spell areas
    private final List<SpellButton> spellButtons = new ArrayList<>();

    public SpellsScreen(PlayerCharacter character) {
        super(Component.literal("Spells"));
        this.character = character;
    }

    private static class SpellButton {
        Spell spell;
        boolean isMemorized;
        int x, y, width, height;

        SpellButton(Spell spell, boolean isMemorized, int x, int y, int width, int height) {
            this.spell = spell;
            this.isMemorized = isMemorized;
            this.x = x;
            this.y = y;
            this.width = width;
            this.height = height;
        }

        boolean contains(int mouseX, int mouseY) {
            return mouseX >= x && mouseX < x + width && mouseY >= y && mouseY < y + height;
        }
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        this.renderBackground(graphics);
        spellButtons.clear();

        graphics.pose().pushPose();
        graphics.pose().scale(FONT_SCALE, FONT_SCALE, 1.0f);

        int scaledMouseX = (int)(mouseX / FONT_SCALE);
        int scaledMouseY = (int)(mouseY / FONT_SCALE);

        int x = (int)(20 / FONT_SCALE);
        int y = (int)(20 / FONT_SCALE);
        int lineHeight = (int)(12 / FONT_SCALE);
        int currentLine = 0;

        // Title
        if (shouldRenderLine(currentLine++)) {
            graphics.drawString(this.font, "═══════════════════════════════════════════════════════════", x, y, HEADER_COLOR);
            y += lineHeight;
        }
        if (shouldRenderLine(currentLine++)) {
            graphics.drawString(this.font, "  SPELLS (M) - Click to memorize/forget", x, y, HEADER_COLOR);
            y += lineHeight;
        }
        if (shouldRenderLine(currentLine++)) {
            graphics.drawString(this.font, "═══════════════════════════════════════════════════════════", x, y, HEADER_COLOR);
            y += lineHeight;
        }
        currentLine++; // blank line

        // Spell slots
        int memorizedCount = character.getMemorizedSpells().size();
        int maxSlots = character.getMaxSpellSlots();
        if (shouldRenderLine(currentLine++)) {
            graphics.drawString(this.font,
                    String.format("Spell Slots: %d / %d", memorizedCount, maxSlots),
                    x, y, TEXT_COLOR);
            y += lineHeight;
        }
        currentLine++; // blank line

        // Memorized spells (click to forget)
        if (shouldRenderLine(currentLine++)) {
            graphics.drawString(this.font, "─── Memorized (click to forget) ───", x, y, HEADER_COLOR);
            y += lineHeight;
        }

        if (character.getMemorizedSpells().isEmpty()) {
            if (shouldRenderLine(currentLine++)) {
                graphics.drawString(this.font, "  (No spells memorized)", x, y, UNKNOWN_COLOR);
                y += lineHeight;
            }
        } else {
            for (Spell spell : character.getMemorizedSpells()) {
                if (shouldRenderLine(currentLine++)) {
                    int mpCost = spell.getLevel() * 2;
                    boolean canCast = character.canCast(spell);

                    String line = String.format("  %s (L%d, %d MP) - %s",
                            spell.getDisplayName(),
                            spell.getLevel(),
                            mpCost,
                            spell.getDescription());

                    int color = canCast ? MEMORIZED_COLOR : KNOWN_COLOR;

                    SpellButton button = new SpellButton(spell, true, x, y, 600, lineHeight);
                    spellButtons.add(button);
                    if (button.contains(scaledMouseX, scaledMouseY)) {
                        color = HOVER_COLOR;
                    }

                    graphics.drawString(this.font, line, x, y, color);
                    y += lineHeight;
                }
            }
        }

        currentLine++; // blank line

        // Known but not memorized (click to memorize)
        if (shouldRenderLine(currentLine++)) {
            graphics.drawString(this.font, "─── Known Spells (click to memorize) ───", x, y, HEADER_COLOR);
            y += lineHeight;
        }

        boolean hasUnmemorized = false;
        for (Spell spell : character.getKnownSpells()) {
            if (!character.getMemorizedSpells().contains(spell)) {
                hasUnmemorized = true;
                if (shouldRenderLine(currentLine++)) {
                    String line = String.format("  %s (L%d) - %s",
                            spell.getDisplayName(),
                            spell.getLevel(),
                            spell.getDescription());

                    int color = KNOWN_COLOR;

                    SpellButton button = new SpellButton(spell, false, x, y, 600, lineHeight);
                    spellButtons.add(button);
                    if (button.contains(scaledMouseX, scaledMouseY)) {
                        color = HOVER_COLOR;
                    }

                    graphics.drawString(this.font, line, x, y, color);
                    y += lineHeight;
                }
            }
        }

        if (!hasUnmemorized) {
            if (shouldRenderLine(currentLine++)) {
                graphics.drawString(this.font, "  (No unmemorized spells)", x, y, UNKNOWN_COLOR);
                y += lineHeight;
            }
        }

        currentLine++; // blank line

        // Example spells (click to learn)
        if (shouldRenderLine(currentLine++)) {
            graphics.drawString(this.font, "─── Example Spells (click to learn) ───", x, y, HEADER_COLOR);
            y += lineHeight;
        }

        int exampleCount = 0;
        for (Spell spell : Spell.values()) {
            if (spell.getLevel() <= 2 && exampleCount < 8 && !character.getKnownSpells().contains(spell)) {
                if (shouldRenderLine(currentLine++)) {
                    String schools = spell.getPrimarySchool().getDisplayName();
                    String line = String.format("  %s (L%d, %s) - %s",
                            spell.getDisplayName(),
                            spell.getLevel(),
                            schools,
                            spell.getDescription());

                    int color = UNKNOWN_COLOR;

                    SpellButton button = new SpellButton(spell, false, x, y, 600, lineHeight);
                    spellButtons.add(button);
                    if (button.contains(scaledMouseX, scaledMouseY)) {
                        color = HOVER_COLOR;
                    }

                    graphics.drawString(this.font, line, x, y, color);
                    y += lineHeight;
                }
                exampleCount++;
            }
        }

        // Footer
        y = (int)((this.height - 40) / FONT_SCALE);
        graphics.drawString(this.font, "Click memorized spells to forget, known spells to memorize, example spells to learn", x, y, 0x888888);
        y += lineHeight;
        graphics.drawString(this.font, "Memorize spells to cast them (uses MP based on level)", x, y, 0x888888);
        y += lineHeight;
        graphics.drawString(this.font, "Press '@' for Character, 'm' for Skills, ESC to close", x, y, 0x888888);

        graphics.pose().popPose();
        super.render(graphics, mouseX, mouseY, partialTick);
    }

    private boolean shouldRenderLine(int line) {
        return line >= scrollOffset && line < scrollOffset + MAX_LINES;
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button == 0) { // Left click
            int scaledMouseX = (int)(mouseX / FONT_SCALE);
            int scaledMouseY = (int)(mouseY / FONT_SCALE);

            for (SpellButton spellButton : spellButtons) {
                if (spellButton.contains(scaledMouseX, scaledMouseY)) {
                    if (spellButton.isMemorized) {
                        // Forget this spell
                        character.forgetSpell(spellButton.spell);
                    } else if (character.getKnownSpells().contains(spellButton.spell)) {
                        // Memorize this known spell
                        character.memorizeSpell(spellButton.spell);
                    } else {
                        // Learn this new spell
                        character.learnSpell(spellButton.spell);
                    }
                    return true;
                }
            }
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double delta) {
        scrollOffset = Math.max(0, scrollOffset - (int)delta);
        return true;
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        // ESC to close
        if (keyCode == 256) { // GLFW.GLFW_KEY_ESCAPE
            this.onClose();
            return true;
        }

        // '@' for character sheet (Shift+2)
        if (keyCode == 50 && (modifiers & 1) != 0) {
            if (this.minecraft != null) {
                this.minecraft.setScreen(new CharacterSheetScreen(character));
            }
            return true;
        }

        // 'm' for skills
        if (keyCode == 77) { // M key
            if (this.minecraft != null) {
                this.minecraft.setScreen(new SkillsScreen(character));
            }
            return true;
        }

        return super.keyPressed(keyCode, scanCode, modifiers);
    }
}
