package com.wasteland.client.gui;

import com.wasteland.character.CharacterManager;
import com.wasteland.character.PlayerCharacter;
import com.wasteland.combat.CombatManager;
import com.wasteland.combat.Combatant;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;

/**
 * Combat UI overlay - renders on top of 3D world during combat.
 * Shows HP/MP, turn counter, action buttons, and handles player input.
 */
public class CombatScreen extends Screen {
    private final CombatManager combat;
    private static final int TEXT_COLOR = 0xFFFFFF;
    private static final int HEADER_COLOR = 0xFFFF00;
    private static final int ENEMY_COLOR = 0xFF4444;
    private static final int PLAYER_COLOR = 0x44FF44;

    public CombatScreen() {
        super(Component.literal("Combat"));
        this.combat = CombatManager.getInstance();
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        // Don't render full background - keep 3D world visible
        // The grid overlay is rendered separately via CombatGridRenderer

        // Top bar - Player stats
        renderPlayerStats(graphics);

        // Bottom bar - Action buttons
        renderActionButtons(graphics);

        // Right side - Turn info and combatant list
        renderTurnInfo(graphics);

        super.render(graphics, mouseX, mouseY, partialTick);
    }

    /**
     * Render player HP/MP at top of screen
     */
    private void renderPlayerStats(GuiGraphics graphics) {
        if (minecraft == null || minecraft.player == null) return;

        PlayerCharacter character = CharacterManager.getCharacter(minecraft.player.getUUID());
        if (character == null) return;

        int x = 10;
        int y = 10;

        // HP bar
        int currentHP = character.getCurrentHP();
        int maxHP = character.getMaxHP();
        String hpText = "HP: " + currentHP + "/" + maxHP;
        graphics.drawString(this.font, hpText, x, y, currentHP < maxHP * 0.3 ? ENEMY_COLOR : TEXT_COLOR);

        // HP bar visual
        int barWidth = 200;
        int barHeight = 10;
        float hpPercent = (float) currentHP / maxHP;
        graphics.fill(x, y + 12, x + barWidth, y + 12 + barHeight, 0xFF333333); // Background
        graphics.fill(x, y + 12, x + (int)(barWidth * hpPercent), y + 12 + barHeight, 0xFF00FF00); // Green bar

        // MP bar
        int currentMP = character.getCurrentMP();
        int maxMP = character.getMaxMP();
        String mpText = "MP: " + currentMP + "/" + maxMP;
        graphics.drawString(this.font, mpText, x, y + 26, 0x4444FF);

        // MP bar visual
        float mpPercent = (float) currentMP / maxMP;
        graphics.fill(x, y + 38, x + barWidth, y + 38 + barHeight, 0xFF333333); // Background
        graphics.fill(x, y + 38, x + (int)(barWidth * mpPercent), y + 38 + barHeight, 0xFF4444FF); // Blue bar
    }

    /**
     * Render action buttons at bottom of screen
     */
    private void renderActionButtons(GuiGraphics graphics) {
        if (!combat.isInCombat()) return;

        int y = this.height - 60;
        int x = this.width / 2 - 200;

        // Action prompt with target info
        if (combat.getState() == com.wasteland.combat.CombatState.PLAYER_TURN) {
            com.wasteland.combat.Combatant target = combat.getSelectedTarget();
            if (target != null) {
                String targetInfo = "Target: " + target.getName() + " (" + (int)target.getCurrentHP() + "/" + (int)target.getMaxHP() + " HP)";
                graphics.drawString(this.font, targetInfo, x, y, HEADER_COLOR);
            } else {
                graphics.drawString(this.font, "Your Turn - [Tab] to select target", x, y, HEADER_COLOR);
            }
        } else {
            graphics.drawString(this.font, "Enemy Turn...", x, y, ENEMY_COLOR);
        }

        y += 15;

        // Action buttons
        graphics.drawString(this.font, "[Space] Attack  [Tab] Cycle Target  [Click] Move/Select  [Esc] End Combat", x, y, 0x888888);
    }

    /**
     * Render turn counter and combatant list on right side
     */
    private void renderTurnInfo(GuiGraphics graphics) {
        if (!combat.isInCombat()) return;

        int x = this.width - 220;
        int y = 60;

        // Turn counter
        graphics.drawString(this.font, "═══ TURN " + (combat.getTurnCounter() + 1) + " ═══", x, y, HEADER_COLOR);
        y += 15;

        // Current combatant
        Combatant current = combat.getCurrentCombatant();
        if (current != null) {
            String turnText = current.isPlayer() ? "► Your Turn" : "► " + current.getName();
            int color = current.isPlayer() ? PLAYER_COLOR : ENEMY_COLOR;
            graphics.drawString(this.font, turnText, x, y, color);
            y += 15;
        }

        y += 10;
        graphics.drawString(this.font, "─── Combatants ───", x, y, HEADER_COLOR);
        y += 12;

        // List all combatants
        Combatant selectedTarget = combat.getSelectedTarget();
        for (Combatant combatant : combat.getCombatants()) {
            if (!combatant.isAlive()) continue;

            String name = combatant.isPlayer() ? "You" : combatant.getName();
            int hp = (int) combatant.getCurrentHP();
            int maxHp = (int) combatant.getMaxHP();

            // Add targeting arrow for selected target
            String prefix = (combatant == selectedTarget && !combatant.isPlayer()) ? "► " : "  ";
            String text = prefix + name + ": " + hp + "/" + maxHp;

            int color = combatant.isPlayer() ? PLAYER_COLOR : TEXT_COLOR;
            if (combatant == selectedTarget && !combatant.isPlayer()) {
                color = HEADER_COLOR; // Highlight selected target
            }

            graphics.drawString(this.font, text, x, y, color);
            y += 12;
        }
    }

    @Override
    public boolean isPauseScreen() {
        return false; // Don't pause game - combat manager handles that
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button != 0) return super.mouseClicked(mouseX, mouseY, button); // Only left click

        if (!combat.isInCombat()) return super.mouseClicked(mouseX, mouseY, button);

        // Check if it's player's turn
        if (combat.getState() != com.wasteland.combat.CombatState.PLAYER_TURN) {
            return super.mouseClicked(mouseX, mouseY, button);
        }

        Minecraft mc = Minecraft.getInstance();

        // First check if we clicked on an entity (for targeting)
        if (mc.hitResult != null && mc.hitResult.getType() == HitResult.Type.ENTITY) {
            net.minecraft.world.phys.EntityHitResult entityHit = (net.minecraft.world.phys.EntityHitResult) mc.hitResult;
            if (entityHit.getEntity() instanceof net.minecraft.world.entity.LivingEntity) {
                combat.setTarget((net.minecraft.world.entity.LivingEntity) entityHit.getEntity());
                return true;
            }
        }

        // Otherwise check for block (for movement)
        if (mc.hitResult != null && mc.hitResult.getType() == HitResult.Type.BLOCK) {
            BlockHitResult blockHit = (BlockHitResult) mc.hitResult;
            BlockPos targetPos = blockHit.getBlockPos().above(); // Position above the block (where player stands)

            // Try to move player to this position
            if (combat.isValidMove(targetPos)) {
                combat.movePlayer(targetPos);
                return true;
            } else {
                System.out.println("Invalid move target: " + targetPos);
            }
        }

        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        // ESC to end combat (for testing)
        if (keyCode == 256) { // GLFW.GLFW_KEY_ESCAPE
            if (combat.isInCombat() && minecraft != null && minecraft.level != null) {
                combat.endCombat(minecraft.level);
                this.onClose();
                return true;
            }
        }

        // Tab to cycle targets
        if (keyCode == 258) { // GLFW.GLFW_KEY_TAB
            combat.cycleTarget();
            return true;
        }

        // Space to attack
        if (keyCode == 32) { // GLFW.GLFW_KEY_SPACE
            if (combat.getState() == com.wasteland.combat.CombatState.PLAYER_TURN) {
                combat.attackTarget();
                return true;
            }
        }

        // TODO: Add other action keys (Z for spell, A for ability, etc.)

        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean shouldCloseOnEsc() {
        return false; // Handle ESC manually for combat exit
    }
}
