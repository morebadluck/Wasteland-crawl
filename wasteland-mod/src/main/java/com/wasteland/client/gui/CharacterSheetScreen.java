package com.wasteland.client.gui;

import com.wasteland.character.*;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

/**
 * Character sheet screen (DCSS '@' key)
 * Shows character stats, race, skills overview
 */
public class CharacterSheetScreen extends Screen {
    private final PlayerCharacter character;
    private static final int TEXT_COLOR = 0xFFFFFF;
    private static final int HEADER_COLOR = 0xFFFF00;
    private static final int SKILL_COLOR = 0x00FF00;
    private static final float FONT_SCALE = 0.75f;

    public CharacterSheetScreen(PlayerCharacter character) {
        super(Component.literal("Character Sheet"));
        this.character = character;
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        this.renderBackground(graphics);

        graphics.pose().pushPose();
        graphics.pose().scale(FONT_SCALE, FONT_SCALE, 1.0f);

        int x = (int)(20 / FONT_SCALE);
        int y = (int)(20 / FONT_SCALE);
        int lineHeight = (int)(12 / FONT_SCALE);

        // Title
        graphics.drawString(this.font, "═══════════════════════════════════════", x, y, HEADER_COLOR);
        y += lineHeight;
        graphics.drawString(this.font, "  CHARACTER SHEET (@)", x, y, HEADER_COLOR);
        y += lineHeight;
        graphics.drawString(this.font, "═══════════════════════════════════════", x, y, HEADER_COLOR);
        y += lineHeight * 2;

        // Basic info
        graphics.drawString(this.font, "Name: " + character.getCharacterName(), x, y, TEXT_COLOR);
        y += lineHeight;
        graphics.drawString(this.font, "Race: " + character.getRace().getDisplayName(), x, y, TEXT_COLOR);
        y += lineHeight;
        graphics.drawString(this.font, "  " + character.getRace().getDescription(), x, y, 0xAAAAAA);
        y += lineHeight * 2;

        // Stats
        graphics.drawString(this.font, "Level: " + character.getExperienceLevel(), x, y, SKILL_COLOR);
        y += lineHeight;
        graphics.drawString(this.font, "Experience: " + character.getTotalXP(), x, y, TEXT_COLOR);
        y += lineHeight * 2;

        graphics.drawString(this.font, "HP: " + character.getCurrentHP() + "/" + character.getMaxHP(), x, y, TEXT_COLOR);
        y += lineHeight;
        graphics.drawString(this.font, "MP: " + character.getCurrentMP() + "/" + character.getMaxMP(), x, y, TEXT_COLOR);
        y += lineHeight * 2;

        // Skills summary
        graphics.drawString(this.font, "═══ SKILLS ═══", x, y, HEADER_COLOR);
        y += lineHeight;

        // Show top 5 trained skills
        int skillsShown = 0;
        for (Skill.SkillCategory category : Skill.SkillCategory.values()) {
            for (Skill skill : Skill.getSkillsByCategory(category)) {
                int level = character.getSkillLevel(skill);
                if (level > 0) {
                    int aptitude = Aptitudes.getAptitude(character.getRace(), skill);
                    String aptStr = aptitude >= 0 ? "+" + aptitude : String.valueOf(aptitude);

                    graphics.drawString(this.font,
                            String.format("  %s: %d (apt: %s)", skill.getDisplayName(), level, aptStr),
                            x, y, SKILL_COLOR);
                    y += lineHeight;
                    skillsShown++;

                    if (skillsShown >= 10) break;
                }
            }
            if (skillsShown >= 10) break;
        }

        if (skillsShown == 0) {
            graphics.drawString(this.font, "  (No skills trained yet)", x, y, 0xAAAAAA);
            y += lineHeight;
        }

        y += lineHeight;

        // Spells summary
        graphics.drawString(this.font, "═══ SPELLS ═══", x, y, HEADER_COLOR);
        y += lineHeight;

        int spellCount = character.getMemorizedSpells().size();
        int maxSpells = character.getMaxSpellSlots();
        graphics.drawString(this.font,
                String.format("  Memorized: %d / %d", spellCount, maxSpells),
                x, y, TEXT_COLOR);
        y += lineHeight;

        for (Spell spell : character.getMemorizedSpells()) {
            graphics.drawString(this.font,
                    "  " + spell.getDisplayName() + " (L" + spell.getLevel() + ")",
                    x, y, SKILL_COLOR);
            y += lineHeight;
        }

        if (spellCount == 0) {
            graphics.drawString(this.font, "  (No spells memorized)", x, y, 0xAAAAAA);
            y += lineHeight;
        }

        y += lineHeight * 2;

        // Controls
        graphics.drawString(this.font, "Press 'm' for Skills, 'M' for Spells, ESC to close", x, y, 0x888888);

        graphics.pose().popPose();
        super.render(graphics, mouseX, mouseY, partialTick);
    }

    @Override
    public boolean isPauseScreen() {
        return false; // Don't pause the game
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        // ESC to close
        if (keyCode == 256) { // GLFW.GLFW_KEY_ESCAPE
            this.onClose();
            return true;
        }

        // 'm' for skills screen
        if (keyCode == 77) { // GLFW.GLFW_KEY_M
            if (this.minecraft != null) {
                this.minecraft.setScreen(new SkillsScreen(character));
            }
            return true;
        }

        // 'M' (Shift+M) for spells
        if (keyCode == 77 && (modifiers & 1) != 0) { // Shift modifier
            if (this.minecraft != null) {
                this.minecraft.setScreen(new SpellsScreen(character));
            }
            return true;
        }

        return super.keyPressed(keyCode, scanCode, modifiers);
    }
}
