package com.wasteland.combat;

import net.minecraft.core.BlockPos;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

import java.util.ArrayList;
import java.util.List;

/**
 * DCSS-style enemy AI for turn-based combat.
 * Enemies will move toward player and attack when in range.
 */
public class EnemyAI {

    /**
     * Execute enemy turn and return delay in ticks before next action
     * @return number of ticks to delay before ending turn
     */
    public static int executeTurn(Combatant enemy, Player player, List<Combatant> allCombatants, Level level) {
        LivingEntity entity = enemy.getEntity();
        BlockPos enemyPos = entity.blockPosition();
        BlockPos playerPos = player.blockPosition();

        // Calculate distance to player
        double distance = Math.sqrt(enemyPos.distSqr(playerPos));

        System.out.println("[AI] " + enemy.getName() + " turn - distance to player: " + distance);

        // If in melee range (adjacent), attack
        if (distance <= 1.5) {
            return attackPlayer(enemy, player);
        }

        // Otherwise, move toward player
        return moveTowardPlayer(enemy, player, allCombatants, level);
    }

    /**
     * Enemy attacks the player
     * @return delay in ticks (20 = 1 second)
     */
    private static int attackPlayer(Combatant attacker, Player target) {
        LivingEntity entity = attacker.getEntity();

        // Calculate damage (simple for now, can be enhanced later)
        float baseDamage = (float) entity.getAttributeValue(net.minecraft.world.entity.ai.attributes.Attributes.ATTACK_DAMAGE);

        // Add some variance (80-120%)
        double variance = 0.8 + (Math.random() * 0.4);
        float finalDamage = Math.max(1.0f, baseDamage * (float)variance);

        // Apply damage
        float oldHP = target.getHealth();
        DamageSource damageSource = target.level().damageSources().mobAttack(entity);
        target.hurt(damageSource, finalDamage);
        float newHP = target.getHealth();

        // Log attack
        String message = String.format("%s attacks you for %.1f damage!",
            attacker.getName(), finalDamage);
        System.out.println("[Combat] " + message);
        CombatLog.addMessage(message);

        // Show HP change
        if (newHP > 0) {
            String hpMessage = String.format("Your HP: %.1f -> %.1f", oldHP, newHP);
            System.out.println("[Combat] " + hpMessage);
            CombatLog.addMessage(hpMessage);
        } else {
            CombatLog.addMessage("You have been defeated!");
        }

        // Return 20 ticks (1 second) delay to show the attack
        return 20;
    }

    /**
     * Move enemy one step toward player
     * @return delay in ticks (10 = 0.5 seconds)
     */
    private static int moveTowardPlayer(Combatant mover, Player target, List<Combatant> allCombatants, Level level) {
        BlockPos currentPos = mover.getEntity().blockPosition();
        BlockPos targetPos = target.blockPosition();

        // Find best adjacent position
        BlockPos bestMove = findBestMovePosition(currentPos, targetPos, allCombatants, level);

        if (bestMove != null && !bestMove.equals(currentPos)) {
            // Move entity to new position
            LivingEntity entity = mover.getEntity();
            entity.teleportTo(bestMove.getX() + 0.5, bestMove.getY(), bestMove.getZ() + 0.5);

            String message = mover.getName() + " moves closer";
            System.out.println("[Combat] " + message);
            CombatLog.addMessage(message);

            // Return 10 ticks (0.5 seconds) delay for movement
            return 10;
        } else {
            // Can't move, just pass turn
            String message = mover.getName() + " cannot move";
            System.out.println("[Combat] " + message);
            CombatLog.addMessage(message);
            return 10;
        }
    }

    /**
     * Find the best adjacent position to move toward target
     * Uses simple greedy algorithm - picks adjacent tile closest to target
     */
    private static BlockPos findBestMovePosition(BlockPos current, BlockPos target,
                                                  List<Combatant> allCombatants, Level level) {
        BlockPos bestPos = null;
        double bestDistance = Double.MAX_VALUE;

        // Check all 8 adjacent positions
        int[] dx = {-1, 0, 1, -1, 1, -1, 0, 1};
        int[] dz = {-1, -1, -1, 0, 0, 1, 1, 1};

        for (int i = 0; i < 8; i++) {
            BlockPos candidate = current.offset(dx[i], 0, dz[i]);

            // Check if position is valid
            if (!isValidPosition(candidate, allCombatants, level)) {
                continue;
            }

            // Calculate distance to target
            double distance = Math.sqrt(candidate.distSqr(target));

            // Pick closest to target
            if (distance < bestDistance) {
                bestDistance = distance;
                bestPos = candidate;
            }
        }

        return bestPos;
    }

    /**
     * Check if a position is valid for movement
     */
    private static boolean isValidPosition(BlockPos pos, List<Combatant> allCombatants, Level level) {
        // Check if block is solid
        if (level.getBlockState(pos).isSolidRender(level, pos)) {
            return false;
        }

        // Check if block below is solid (don't walk into void)
        if (!level.getBlockState(pos.below()).isSolidRender(level, pos.below())) {
            return false;
        }

        // Check if any combatant is at this position
        for (Combatant c : allCombatants) {
            if (c.getEntity().blockPosition().equals(pos)) {
                return false; // Position occupied
            }
        }

        return true;
    }

    /**
     * Advanced pathfinding (for future enhancement)
     * Could implement A* or similar for smarter movement
     */
    // TODO: Implement proper pathfinding algorithm
}
