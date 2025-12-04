package com.wasteland.client.gui;

import com.mojang.blaze3d.vertex.PoseStack;
import com.wasteland.character.Aptitudes;
import com.wasteland.character.CharacterManager;
import com.wasteland.character.Race;
import com.wasteland.character.Skill;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

/**
 * Race selection screen shown to new players
 */
public class RaceSelectionScreen extends Screen {
    private Race selectedRace = Race.HUMAN;
    private boolean showAllRaces = false;
    private static final int RACE_BUTTON_WIDTH = 120;
    private static final int RACE_BUTTON_HEIGHT = 20;

    public RaceSelectionScreen() {
        super(Component.literal("Choose Your Race"));
    }

    @Override
    protected void init() {
        super.init();

        // Toggle between beginner and all races
        this.addRenderableWidget(Button.builder(
            Component.literal(showAllRaces ? "Show Beginner Races" : "Show All Races"),
            button -> {
                showAllRaces = !showAllRaces;
                this.clearWidgets();
                this.init();
            })
            .bounds(this.width - 160, 10, 150, 20)
            .build());

        // Race selection buttons
        Race[] races = showAllRaces ? Race.getAllRaces() : Race.getBeginnerRaces();
        int startY = 40;
        int x = 10;
        int y = startY;

        for (Race race : races) {
            Button raceButton = Button.builder(
                Component.literal(race.getDisplayName()),
                button -> {
                    selectedRace = race;
                    this.clearWidgets();
                    this.init();
                })
                .bounds(x, y, RACE_BUTTON_WIDTH, RACE_BUTTON_HEIGHT)
                .build();

            this.addRenderableWidget(raceButton);

            y += RACE_BUTTON_HEIGHT + 2;
            if (y > this.height - 60) {
                y = startY;
                x += RACE_BUTTON_WIDTH + 5;
            }
        }

        // Confirm button
        this.addRenderableWidget(Button.builder(
            Component.literal("§aConfirm Selection"),
            button -> confirmSelection())
            .bounds(this.width / 2 - 75, this.height - 30, 150, 20)
            .build());
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        renderBackground(graphics);

        // Title
        graphics.drawCenteredString(this.font, "§e§lChoose Your Race", this.width / 2, 10, 0xFFFFFF);
        graphics.drawCenteredString(this.font, "§7This choice is permanent!", this.width / 2, 22, 0xAAAAAA);

        // Race info panel (right side)
        int panelX = this.width / 2 + 20;
        int panelY = 40;
        int panelWidth = this.width / 2 - 40;

        // Draw panel background
        graphics.fill(panelX - 5, panelY - 5, panelX + panelWidth + 5, this.height - 50,
                     0x80000000);

        // Race name
        graphics.drawString(this.font, "§e§l" + selectedRace.getDisplayName(),
                          panelX, panelY, 0xFFFFFF);
        panelY += 15;

        // Description
        graphics.drawString(this.font, "§7" + selectedRace.getDescription(),
                          panelX, panelY, 0xAAAAAA);
        panelY += 20;

        // Base modifiers
        graphics.drawString(this.font, "§6Base Modifiers:", panelX, panelY, 0xFFAA00);
        panelY += 12;

        graphics.drawString(this.font,
            String.format("  HP: %s%+d  MP: %s%+d  Speed: %s%+d",
                getModifierColor(selectedRace.getHpModifier()), selectedRace.getHpModifier(),
                getModifierColor(selectedRace.getMpModifier()), selectedRace.getMpModifier(),
                getModifierColor(selectedRace.getSpeedModifier()), selectedRace.getSpeedModifier()),
            panelX, panelY, 0xFFFFFF);
        panelY += 12;

        graphics.drawString(this.font,
            String.format("  STR: %s%+d  DEX: %s%+d  INT: %s%+d",
                getModifierColor(selectedRace.getStrengthModifier()), selectedRace.getStrengthModifier(),
                getModifierColor(selectedRace.getDexterityModifier()), selectedRace.getDexterityModifier(),
                getModifierColor(selectedRace.getIntelligenceModifier()), selectedRace.getIntelligenceModifier()),
            panelX, panelY, 0xFFFFFF);
        panelY += 20;

        // Skill aptitudes (notable ones)
        graphics.drawString(this.font, "§6Notable Skill Aptitudes:", panelX, panelY, 0xFFAA00);
        panelY += 12;

        // Show top 5 positive and bottom 3 negative aptitudes
        int[] bestAptitudes = new int[5];
        Skill[] bestSkills = new Skill[5];
        int[] worstAptitudes = new int[3];
        Skill[] worstSkills = new Skill[3];

        for (int i = 0; i < 5; i++) {
            bestAptitudes[i] = -99;
            worstAptitudes[i < 3 ? i : 0] = 99;
        }

        for (Skill skill : Skill.values()) {
            int apt = Aptitudes.getAptitude(selectedRace, skill);

            // Check for best aptitudes
            for (int i = 0; i < 5; i++) {
                if (apt > bestAptitudes[i]) {
                    // Shift down
                    for (int j = 4; j > i; j--) {
                        bestAptitudes[j] = bestAptitudes[j-1];
                        bestSkills[j] = bestSkills[j-1];
                    }
                    bestAptitudes[i] = apt;
                    bestSkills[i] = skill;
                    break;
                }
            }

            // Check for worst aptitudes
            for (int i = 0; i < 3; i++) {
                if (apt < worstAptitudes[i] && apt < 0) {
                    // Shift down
                    for (int j = 2; j > i; j--) {
                        worstAptitudes[j] = worstAptitudes[j-1];
                        worstSkills[j] = worstSkills[j-1];
                    }
                    worstAptitudes[i] = apt;
                    worstSkills[i] = skill;
                    break;
                }
            }
        }

        // Display best aptitudes
        graphics.drawString(this.font, "  §aStrengths:", panelX, panelY, 0x00FF00);
        panelY += 10;
        for (int i = 0; i < 5 && bestSkills[i] != null && bestAptitudes[i] > 0; i++) {
            String aptColor = bestAptitudes[i] >= 3 ? "§a" : "§2";
            graphics.drawString(this.font,
                String.format("    %s (%s%+d)",
                    bestSkills[i].getDisplayName(),
                    aptColor,
                    bestAptitudes[i]),
                panelX, panelY, 0xFFFFFF);
            panelY += 10;
        }

        panelY += 5;

        // Display worst aptitudes
        if (worstSkills[0] != null && worstAptitudes[0] < 99) {
            graphics.drawString(this.font, "  §cWeaknesses:", panelX, panelY, 0xFF0000);
            panelY += 10;
            for (int i = 0; i < 3 && worstSkills[i] != null && worstAptitudes[i] < 99; i++) {
                String aptColor = worstAptitudes[i] <= -3 ? "§c" : "§4";
                graphics.drawString(this.font,
                    String.format("    %s (%s%+d)",
                        worstSkills[i].getDisplayName(),
                        aptColor,
                        worstAptitudes[i]),
                    panelX, panelY, 0xFFFFFF);
                panelY += 10;
            }
        }

        super.render(graphics, mouseX, mouseY, partialTick);
    }

    /**
     * Get color code for modifier display
     */
    private String getModifierColor(int modifier) {
        if (modifier > 0) return "§a";
        if (modifier < 0) return "§c";
        return "§7";
    }

    /**
     * Confirm race selection and create character
     */
    private void confirmSelection() {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null) return;

        // Create character with selected race
        CharacterManager.createCharacter(mc.player.getUUID(), selectedRace, mc.player.getName().getString());

        // Close screen
        this.onClose();

        // Show welcome message
        mc.player.displayClientMessage(
            Component.literal(String.format("§aWelcome, %s %s!",
                selectedRace.getDisplayName(),
                mc.player.getName().getString())),
            false
        );
        mc.player.displayClientMessage(
            Component.literal("§7Your adventure in the wasteland begins..."),
            false
        );
    }

    @Override
    public boolean isPauseScreen() {
        return true; // Pause the game during race selection
    }

    @Override
    public boolean shouldCloseOnEsc() {
        return false; // Don't allow escape without selecting
    }
}
