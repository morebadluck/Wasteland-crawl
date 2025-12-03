package com.wasteland.client.gui;

import com.wasteland.character.*;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * Skills training screen (DCSS 'm' key)
 * Shows all skills with levels and aptitudes
 * Click skills to train them with test XP
 */
public class SkillsScreen extends Screen {
    private final PlayerCharacter character;
    private static final int TEXT_COLOR = 0xFFFFFF;
    private static final int HEADER_COLOR = 0xFFFF00;
    private static final int TRAINED_COLOR = 0x00FF00;
    private static final int UNTRAINED_COLOR = 0x888888;
    private static final int HOVER_COLOR = 0xFFFFAA;

    private int scrollOffset = 0;
    private static final int MAX_LINES = 35;
    private static final float FONT_SCALE = 0.75f;
    private final int LINE_HEIGHT = (int)(12 * FONT_SCALE);

    // Track clickable skill areas
    private final List<SkillButton> skillButtons = new ArrayList<>();

    public SkillsScreen(PlayerCharacter character) {
        super(Component.literal("Skills"));
        this.character = character;
    }

    private static class SkillButton {
        Skill skill;
        int x, y, width, height;

        SkillButton(Skill skill, int x, int y, int width, int height) {
            this.skill = skill;
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
        skillButtons.clear();

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
            graphics.drawString(this.font, "  SKILLS TRAINING (m) - Click to train skills", x, y, HEADER_COLOR);
            y += lineHeight;
        }
        if (shouldRenderLine(currentLine++)) {
            graphics.drawString(this.font, "═══════════════════════════════════════════════════════════", x, y, HEADER_COLOR);
            y += lineHeight;
        }
        currentLine++; // blank line

        // Show skills by category
        for (Skill.SkillCategory category : Skill.SkillCategory.values()) {
            if (shouldRenderLine(currentLine++)) {
                graphics.drawString(this.font, "─── " + category.getDisplayName() + " ───", x, y, HEADER_COLOR);
                y += lineHeight;
            }

            for (Skill skill : Skill.getSkillsByCategory(category)) {
                if (shouldRenderLine(currentLine++)) {
                    int level = character.getSkillLevel(skill);
                    int aptitude = Aptitudes.getAptitude(character.getRace(), skill);
                    double progress = character.getSkillProgress(skill);

                    String aptStr = aptitude >= 0 ? "+" + aptitude : String.valueOf(aptitude);
                    int color = level > 0 ? TRAINED_COLOR : UNTRAINED_COLOR;

                    // Check if mouse is hovering
                    SkillButton button = new SkillButton(skill, x, y, 400, lineHeight);
                    skillButtons.add(button);
                    if (button.contains(scaledMouseX, scaledMouseY)) {
                        color = HOVER_COLOR;
                    }

                    // Format: Skill Name........... 5.2 (+2)
                    String skillName = skill.getDisplayName();
                    String levelStr = String.format("%.1f", level + progress);
                    String line = String.format("  %-30s %5s (apt: %3s)", skillName, levelStr, aptStr);

                    graphics.drawString(this.font, line, x, y, color);
                    y += lineHeight;
                }
            }

            if (shouldRenderLine(currentLine++)) {
                y += lineHeight / 2; // half-line spacing between categories
            }
        }

        // Footer
        y = (int)((this.height - 40) / FONT_SCALE);
        graphics.drawString(this.font, "Click a skill to train it (+100 XP) - Skills improve with use and XP", x, y, 0x888888);
        y += lineHeight;
        graphics.drawString(this.font, "Aptitude affects training speed: -5 (worst) to +5 (best)", x, y, 0x888888);
        y += lineHeight;
        graphics.drawString(this.font, "Press '@' for Character Sheet, 'M' for Spells, ESC to close", x, y, 0x888888);

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

            for (SkillButton skillButton : skillButtons) {
                if (skillButton.contains(scaledMouseX, scaledMouseY)) {
                    // Train this skill
                    character.trainSkill(skillButton.skill, 100);
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
        if (keyCode == 50 && (modifiers & 1) != 0) { // Shift+2
            if (this.minecraft != null) {
                this.minecraft.setScreen(new CharacterSheetScreen(character));
            }
            return true;
        }

        // 'M' (Shift+M) for spells
        if (keyCode == 77 && (modifiers & 1) != 0) {
            if (this.minecraft != null) {
                this.minecraft.setScreen(new SpellsScreen(character));
            }
            return true;
        }

        return super.keyPressed(keyCode, scanCode, modifiers);
    }
}
