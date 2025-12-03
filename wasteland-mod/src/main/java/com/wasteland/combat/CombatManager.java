package com.wasteland.combat;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import java.util.*;

/**
 * Central manager for turn-based combat system.
 * Handles combat state, turn order, and world freezing.
 */
public class CombatManager {
    // Singleton instance (client-side only)
    private static CombatManager instance;

    // Combat state
    private CombatState state = CombatState.EXPLORATION;
    private Player player;
    private List<Combatant> combatants = new ArrayList<>();
    private int currentTurnIndex = 0;
    private int turnCounter = 0;

    // Grid state
    private BlockPos gridCenter;
    private Set<BlockPos> validMoves = new HashSet<>();

    // Target selection
    private Combatant selectedTarget = null;

    // Frozen world state (for resuming after combat)
    private final Map<UUID, Vec3> frozenEntityVelocities = new HashMap<>();
    private final Map<UUID, Boolean> frozenEntityAI = new HashMap<>();

    // Configuration
    public static final double DETECTION_RADIUS = 15.0;
    public static final int GRID_RADIUS = 7;

    private CombatManager() {}

    public static CombatManager getInstance() {
        if (instance == null) {
            instance = new CombatManager();
        }
        return instance;
    }

    /**
     * Check if we're currently in combat mode
     */
    public boolean isInCombat() {
        return state != CombatState.EXPLORATION;
    }

    /**
     * Get current combat state
     */
    public CombatState getState() {
        return state;
    }

    /**
     * Start combat with detected enemies
     */
    public void startCombat(Player player, List<LivingEntity> enemies, Level level) {
        if (isInCombat()) {
            return; // Already in combat
        }

        System.out.println("Starting combat! Enemies detected: " + enemies.size());

        this.player = player;
        this.state = CombatState.PLAYER_TURN;
        this.turnCounter = 0;
        this.currentTurnIndex = 0;
        this.gridCenter = player.blockPosition();

        // Create combatants list
        combatants.clear();

        // Add player as first combatant
        combatants.add(new Combatant(player, true));

        // Add enemies
        for (LivingEntity enemy : enemies) {
            combatants.add(new Combatant(enemy, false));
        }

        // Sort by speed (TODO: implement proper speed stat)
        combatants.sort((a, b) -> Integer.compare(b.getSpeed(), a.getSpeed()));

        // Freeze the world
        freezeWorld(level);

        // Calculate valid moves for player
        updateValidMoves();

        System.out.println("Combat started! Turn 1, Player's turn");
    }

    /**
     * Freeze all non-combatant entities
     */
    private void freezeWorld(Level level) {
        frozenEntityVelocities.clear();
        frozenEntityAI.clear();

        // Get all entities in combat
        Set<UUID> combatantUUIDs = new HashSet<>();
        for (Combatant c : combatants) {
            combatantUUIDs.add(c.getEntity().getUUID());
        }

        // Freeze all entities except combatants
        level.getEntities((net.minecraft.world.entity.Entity) null, player.getBoundingBox().inflate(50.0)).forEach(entity -> {
            if (entity instanceof net.minecraft.world.entity.Mob && !combatantUUIDs.contains(entity.getUUID())) {
                net.minecraft.world.entity.Mob mob = (net.minecraft.world.entity.Mob) entity;

                // Store current state
                frozenEntityVelocities.put(entity.getUUID(), entity.getDeltaMovement());
                frozenEntityAI.put(entity.getUUID(), mob.isNoAi());

                // Freeze entity
                entity.setDeltaMovement(Vec3.ZERO);
                mob.setNoAi(true);
            }
        });

        System.out.println("World frozen. " + frozenEntityVelocities.size() + " entities paused.");
    }

    /**
     * Unfreeze the world and restore entity states
     */
    private void unfreezeWorld(Level level) {
        level.getEntities((net.minecraft.world.entity.Entity) null, player.getBoundingBox().inflate(50.0)).forEach(entity -> {
            UUID uuid = entity.getUUID();

            if (frozenEntityVelocities.containsKey(uuid)) {
                entity.setDeltaMovement(frozenEntityVelocities.get(uuid));
            }

            if (entity instanceof net.minecraft.world.entity.Mob && frozenEntityAI.containsKey(uuid)) {
                ((net.minecraft.world.entity.Mob)entity).setNoAi(frozenEntityAI.get(uuid));
            }
        });

        frozenEntityVelocities.clear();
        frozenEntityAI.clear();

        System.out.println("World unfrozen.");
    }

    /**
     * Calculate valid movement positions (8-directional)
     */
    private void updateValidMoves() {
        validMoves.clear();

        BlockPos playerPos = player.blockPosition();

        // 8 directions: N, NE, E, SE, S, SW, W, NW
        int[] dx = {0, 1, 1, 1, 0, -1, -1, -1};
        int[] dz = {-1, -1, 0, 1, 1, 1, 0, -1};

        for (int i = 0; i < 8; i++) {
            BlockPos targetPos = playerPos.offset(dx[i], 0, dz[i]);

            // Check if position is valid (not occupied, not solid block)
            if (isValidMovePosition(targetPos)) {
                validMoves.add(targetPos);
            }
        }

        System.out.println("Valid moves updated: " + validMoves.size() + " positions");
    }

    /**
     * Check if a position is valid for movement
     */
    private boolean isValidMovePosition(BlockPos pos) {
        Level level = player.level();

        // Check if block is solid
        if (level.getBlockState(pos).isSolidRender(level, pos)) {
            return false;
        }

        // Check if any combatant is at this position
        for (Combatant c : combatants) {
            if (c.getEntity().blockPosition().equals(pos)) {
                return false;
            }
        }

        return true;
    }

    /**
     * Execute player movement action
     */
    public boolean movePlayer(BlockPos targetPos) {
        if (!validMoves.contains(targetPos)) {
            System.out.println("Invalid move position!");
            return false;
        }

        // Move player to target position (centered on block)
        player.teleportTo(targetPos.getX() + 0.5, targetPos.getY(), targetPos.getZ() + 0.5);

        System.out.println("Player moved to " + targetPos);

        // End player turn
        endTurn();

        return true;
    }

    /**
     * End current turn and advance to next combatant
     */
    public void endTurn() {
        currentTurnIndex++;

        // Check if round is complete
        if (currentTurnIndex >= combatants.size()) {
            currentTurnIndex = 0;
            turnCounter++;
            System.out.println("=== Turn " + (turnCounter + 1) + " ===");
        }

        // Check for victory or defeat
        if (checkCombatEnd()) {
            return; // Combat has ended
        }

        Combatant current = combatants.get(currentTurnIndex);

        if (current.isPlayer()) {
            state = CombatState.PLAYER_TURN;
            updateValidMoves();
            System.out.println("Player's turn");
        } else {
            state = CombatState.ENEMY_TURN;
            System.out.println("Enemy turn: " + current.getEntity().getName().getString());

            // Execute enemy AI (simple for now - just end turn)
            executeEnemyTurn(current);
        }
    }

    /**
     * Execute enemy AI for their turn
     */
    private void executeEnemyTurn(Combatant enemy) {
        // TODO: Implement proper AI
        // For now, just end turn immediately
        System.out.println("Enemy " + enemy.getEntity().getName().getString() + " passes their turn");

        // End enemy turn after a delay (so it's visible)
        endTurn();
    }

    /**
     * End combat and return to exploration mode
     */
    public void endCombat(Level level) {
        if (!isInCombat()) {
            return;
        }

        System.out.println("Combat ended!");

        unfreezeWorld(level);

        state = CombatState.EXPLORATION;
        combatants.clear();
        validMoves.clear();
        currentTurnIndex = 0;
        turnCounter = 0;
        player = null;

        // Set cooldown to prevent immediate re-trigger
        com.wasteland.combat.CombatDetection.setCombatCooldown();
    }

    /**
     * Get the player in combat
     */
    public Player getPlayer() {
        return player;
    }

    /**
     * Get all combatants
     */
    public List<Combatant> getCombatants() {
        return new ArrayList<>(combatants);
    }

    /**
     * Get current turn combatant
     */
    public Combatant getCurrentCombatant() {
        if (combatants.isEmpty()) {
            return null;
        }
        return combatants.get(currentTurnIndex);
    }

    /**
     * Get turn counter
     */
    public int getTurnCounter() {
        return turnCounter;
    }

    /**
     * Get grid center position
     */
    public BlockPos getGridCenter() {
        return gridCenter;
    }

    /**
     * Get valid move positions
     */
    public Set<BlockPos> getValidMoves() {
        return new HashSet<>(validMoves);
    }

    /**
     * Check if a position is a valid move
     */
    public boolean isValidMove(BlockPos pos) {
        return validMoves.contains(pos);
    }

    /**
     * Cycle to next enemy target (Tab key)
     */
    public void cycleTarget() {
        List<Combatant> enemies = getAliveEnemies();
        if (enemies.isEmpty()) {
            selectedTarget = null;
            return;
        }

        // Find current target index
        int currentIndex = -1;
        if (selectedTarget != null) {
            currentIndex = enemies.indexOf(selectedTarget);
        }

        // Cycle to next
        int nextIndex = (currentIndex + 1) % enemies.size();
        selectedTarget = enemies.get(nextIndex);

        System.out.println("Target: " + selectedTarget.getName());
    }

    /**
     * Set target by clicking on enemy
     */
    public void setTarget(LivingEntity entity) {
        for (Combatant c : combatants) {
            if (c.getEntity() == entity && !c.isPlayer() && c.isAlive()) {
                selectedTarget = c;
                System.out.println("Target selected: " + selectedTarget.getName());
                return;
            }
        }
    }

    /**
     * Get currently selected target
     */
    public Combatant getSelectedTarget() {
        // Auto-select first enemy if none selected
        if (selectedTarget == null || !selectedTarget.isAlive()) {
            List<Combatant> enemies = getAliveEnemies();
            if (!enemies.isEmpty()) {
                selectedTarget = enemies.get(0);
            }
        }
        return selectedTarget;
    }

    /**
     * Get list of alive enemies
     */
    private List<Combatant> getAliveEnemies() {
        List<Combatant> enemies = new ArrayList<>();
        for (Combatant c : combatants) {
            if (!c.isPlayer() && c.isAlive()) {
                enemies.add(c);
            }
        }
        return enemies;
    }

    /**
     * Player attacks the currently selected target
     */
    public boolean attackTarget() {
        if (state != CombatState.PLAYER_TURN) {
            System.out.println("Not player's turn!");
            return false;
        }

        Combatant target = getSelectedTarget();
        if (target == null) {
            System.out.println("No target selected!");
            return false;
        }

        // Calculate damage (basic formula for now)
        // TODO: Get from equipped weapon and skill level
        int baseDamage = 5; // Base unarmed/basic weapon damage

        // Add some randomness (80-120% of base damage)
        double variance = 0.8 + (Math.random() * 0.4);
        int finalDamage = Math.max(1, (int)(baseDamage * variance)); // At least 1 damage

        // Apply damage
        LivingEntity targetEntity = target.getEntity();
        float oldHP = targetEntity.getHealth();
        targetEntity.hurt(player.level().damageSources().playerAttack(player), finalDamage);
        float newHP = targetEntity.getHealth();

        System.out.println("You attack " + target.getName() + " for " + finalDamage + " damage!");
        System.out.println(target.getName() + ": " + oldHP + " -> " + newHP + " HP");

        // Check if target died
        if (!target.isAlive()) {
            System.out.println(target.getName() + " has been defeated!");
            selectedTarget = null; // Clear dead target

            // Check for victory immediately
            if (checkCombatEnd()) {
                return true; // Combat ended in victory
            }
        }

        // End player's turn
        endTurn();

        return true;
    }

    /**
     * Check if combat should end due to victory or defeat
     * @return true if combat ended, false if it continues
     */
    private boolean checkCombatEnd() {
        if (!isInCombat()) {
            return false; // Already ended
        }

        // Check defeat - player is dead
        if (player == null || !player.isAlive()) {
            System.out.println("╔════════════════════╗");
            System.out.println("║   === DEFEAT ===   ║");
            System.out.println("╚════════════════════╝");
            player.displayClientMessage(
                net.minecraft.network.chat.Component.literal("§4☠ You have been defeated! ☠"),
                false
            );
            endCombat(player.level());
            return true;
        }

        // Check victory - all enemies are dead
        List<Combatant> aliveEnemies = getAliveEnemies();
        if (aliveEnemies.isEmpty()) {
            System.out.println("╔════════════════════╗");
            System.out.println("║   === VICTORY ===  ║");
            System.out.println("╚════════════════════╝");
            player.displayClientMessage(
                net.minecraft.network.chat.Component.literal("§6★ Victory! All enemies defeated! ★"),
                false
            );
            endCombat(player.level());
            return true;
        }

        return false; // Combat continues
    }
}
