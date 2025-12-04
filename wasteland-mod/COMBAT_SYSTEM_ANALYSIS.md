# Turn-Based Combat System - Analysis & Fixes

## Current System Overview

### Architecture (4 main files)

1. **CombatManager.java** - Core combat logic
   - Manages combat state and turn order
   - Handles world freezing/unfreezing
   - Processes player and enemy actions
   - Tracks combatants and valid moves

2. **CombatDetection.java** - Combat triggering
   - Manual mode: Attack entity to trigger combat
   - Auto mode: Toggle with 'C' key, detects nearby hostiles
   - 5-second cooldown after combat ends
   - 15-block detection radius

3. **Combatant.java** - Wrapper for combat participants
   - Tracks entity state during combat
   - Manages action points and turn status
   - Speed stat for turn order (currently hardcoded to 10)

4. **CombatState.java** - State enum
   - EXPLORATION, PLAYER_TURN, ENEMY_TURN, ENDING

---

## Critical Problems Identified

### 1. Enemy AI Does Nothing ⚠️ CRITICAL
**Location**: `CombatManager.java:256-263`

```java
private void executeEnemyTurn(Combatant enemy) {
    // TODO: Implement proper AI
    // For now, just end turn immediately
    System.out.println("Enemy " + enemy.getEntity().getName().getString() + " passes their turn");

    // End enemy turn after a delay (so it's visible)
    endTurn(); // ← PROBLEM: No delay, no action!
}
```

**Issue**: Enemies immediately pass their turn without doing anything. No movement, no attacks, no AI decisions.

**Impact**: Combat is unplayable - enemies just stand there while player kills them.

---

### 2. World Freezing Too Aggressive
**Location**: `CombatManager.java:106-132`

**Current approach**:
- Sets `NoAI = true` on all non-combatant mobs within 50 blocks
- Sets velocity to zero
- Stores state for restoration

**Problems**:
- Might interfere with vanilla mechanics (villagers, animals, etc.)
- 50-block radius is arbitrary and might be too large/small
- NoAI completely disables entity, might cause issues with passive mechanics
- Doesn't prevent block updates, redstone, etc.

---

### 3. Hostile Detection Is Primitive
**Location**: `CombatDetection.java:172-199`

```java
private static boolean isHostile(LivingEntity entity, Player player) {
    if (!(entity instanceof Mob)) {
        return false;
    }

    Mob mob = (Mob) entity;

    // Check if mob is targeting player
    if (mob.getTarget() == player) {
        return true;
    }

    // Check mob type - hardcoded animal exclusions
    String mobType = entity.getType().toString();
    if (mobType.contains("cow") || mobType.contains("sheep") ||
        mobType.contains("chicken") || mobType.contains("pig")) {
        return false;
    }

    // Most other mobs in dungeons are hostile
    return true; // ← PROBLEM: Everything except farm animals is hostile!
}
```

**Problems**:
- String matching on mob type names is fragile
- No concept of neutral/friendly entities
- No faction system
- Villagers, iron golems, etc. would be hostile
- Doesn't account for mod-added entities

---

### 4. Combat Triggering Issues

**Manual Mode** (`CombatDetection.java:89-130`):
- Cancels attack event entirely
- Might feel unresponsive if cooldown active
- No visual feedback for why attack was canceled

**Auto Mode** (`CombatDetection.java:39-84`):
- Only checks every 20 ticks (1 second)
- Fast-moving enemies might enter/exit detection range between checks
- No hysteresis - might trigger combat repeatedly at range boundary

---

### 5. Turn Flow Problems

**No Enemy Action Delay** (`CombatManager.java:263`):
- `executeEnemyTurn()` calls `endTurn()` immediately
- Player doesn't see what enemy is doing
- No time to read combat log messages

**No Turn Indicators**:
- Combat state changes but player might not notice
- No clear "ENEMY TURN" visual
- Hard to tell when you can act again

**Immediate Turn Advance** (`CombatManager.java:224-252`):
- Turn advances instantly after action
- No pause for player to read results
- Damage numbers flash by too quickly

---

### 6. Movement System Limitations

**8-Direction Only** (`CombatManager.java:159-178`):
- Hardcoded to 8 adjacent tiles
- No support for variable movement range
- No diagonal movement cost difference
- Can't handle different unit speeds

**Grid Teleportation** (`CombatManager.java:211`):
- `player.teleportTo()` snaps to exact block center
- Might feel jarring vs. smooth movement
- No movement animation

---

### 7. Combat Victory/Defeat Flow

**Immediate End** (`CombatManager.java:491-524`):
- Combat ends instantly when last enemy dies
- No "victory animation" or loot collection pause
- Defeat message but no respawn handling

---

## Proposed Solutions

### Solution 1: Implement Real Enemy AI

**Create**: `EnemyAI.java` with DCSS-style behavior

```java
public class EnemyAI {
    /**
     * Decide and execute enemy action for their turn
     * Returns time delay before next turn (in ticks)
     */
    public static int executeTurn(Combatant enemy, Player player, List<Combatant> allCombatants) {
        LivingEntity entity = enemy.getEntity();

        // 1. Check if in melee range of player
        double distanceToPlayer = entity.position().distanceTo(player.position());

        if (distanceToPlayer <= 2.0) {
            // Attack player
            attackTarget(enemy, player);
            return 20; // 1 second delay to show attack
        }

        // 2. Move toward player
        moveTowardTarget(enemy, player);
        return 10; // 0.5 second delay for movement
    }

    private static void attackTarget(Combatant attacker, Player target) {
        // Calculate damage
        // Apply damage
        // Show combat message
    }

    private static void moveTowardTarget(Combatant mover, Player target) {
        // Find best adjacent tile toward target
        // Move entity to that tile
        // Update valid moves
    }
}
```

**Integration**: `CombatManager.executeEnemyTurn()`:
```java
private void executeEnemyTurn(Combatant enemy) {
    System.out.println("Enemy turn: " + enemy.getName());

    // Execute AI and get delay
    int delayTicks = EnemyAI.executeTurn(enemy, player, combatants);

    // Schedule next turn after delay
    CombatScheduler.scheduleAfterDelay(() -> {
        endTurn();
    }, delayTicks);
}
```

---

### Solution 2: Better World Pausing

**Option A: Tick Event Interception** (Recommended)
```java
@SubscribeEvent
public static void onLivingTick(LivingEvent.LivingTickEvent event) {
    CombatManager combat = CombatManager.getInstance();

    if (!combat.isInCombat()) {
        return; // Not in combat, allow normal ticking
    }

    LivingEntity entity = event.getEntity();

    // Check if entity is a combatant
    if (combat.isCombatant(entity)) {
        // Allow combatants to tick
        return;
    }

    // Freeze all non-combatants by canceling their tick
    event.setCanceled(true);
}
```

**Advantages**:
- Fine-grained control
- No state manipulation (NoAI)
- Reversible instantly
- Doesn't break vanilla mechanics

**Option B: Time Dilation**
- Reduce game speed to 0 for non-combatants
- Similar to old VATS in Fallout
- More cinematic but technically harder

---

### Solution 3: Improved Hostile Detection

**Create**: Faction/hostility system

```java
public enum EntityFaction {
    PLAYER,
    HOSTILE_DUNGEON,  // Dungeon enemies
    HOSTILE_WASTELAND, // Overworld enemies
    NEUTRAL,          // Villagers, merchants
    PASSIVE,          // Farm animals
    FRIENDLY          // Allied NPCs
}

public class HostilityManager {
    // Determine faction of entity
    public static EntityFaction getFaction(LivingEntity entity) {
        // Check entity type
        // Check NBT tags (custom faction data)
        // Check team
        // Return appropriate faction
    }

    // Check if two factions are hostile
    public static boolean areHostile(EntityFaction f1, EntityFaction f2) {
        // Faction relationship matrix
    }

    // Check if entity is hostile to player
    public static boolean isHostileToPlayer(LivingEntity entity) {
        EntityFaction faction = getFaction(entity);
        return areHostile(faction, EntityFaction.PLAYER);
    }
}
```

**Integration**: Replace `CombatDetection.isHostile()`:
```java
private static boolean isHostile(LivingEntity entity, Player player) {
    return HostilityManager.isHostileToPlayer(entity);
}
```

---

### Solution 4: Combat Trigger Improvements

**Auto Mode Improvements**:
```java
// Increase check frequency for better responsiveness
private static final int CHECK_INTERVAL = 10; // 0.5 seconds instead of 1

// Add hysteresis to prevent trigger-spam
private static BlockPos lastTriggerPosition = null;
private static final double HYSTERESIS_DISTANCE = 5.0;

// Only trigger if:
// 1. Enemies nearby AND
// 2. Either (first trigger OR player moved significantly since last trigger)
```

**Manual Mode Improvements**:
```java
@SubscribeEvent
public static void onPlayerAttack(AttackEntityEvent event) {
    // Don't cancel event immediately
    // Just trigger combat
    // Let first attack go through (feels more responsive)
    // Cancel subsequent attacks until combat resolves
}
```

---

### Solution 5: Turn Flow Improvements

**Add Turn Scheduler**:
```java
public class CombatScheduler {
    private static ScheduledFuture<?> currentTask = null;

    public static void scheduleAfterDelay(Runnable task, int ticks) {
        // Schedule task to run after delay
        // Cancel previous task if any
    }

    public static void cancelAll() {
        if (currentTask != null) {
            currentTask.cancel(false);
        }
    }
}
```

**Add Turn Indicators**:
```java
// In CombatScreen
@Override
public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
    // Show large "ENEMY TURN" banner
    if (combat.getState() == CombatState.ENEMY_TURN) {
        renderEnemyTurnBanner(graphics);
    }

    // Highlight current combatant
    Combatant current = combat.getCurrentCombatant();
    if (current != null) {
        highlightCombatant(graphics, current);
    }
}
```

**Add Combat Log**:
```java
public class CombatLog {
    private static final List<String> messages = new ArrayList<>();
    private static final int MAX_MESSAGES = 10;

    public static void addMessage(String message) {
        messages.add(0, message); // Add to top
        if (messages.size() > MAX_MESSAGES) {
            messages.remove(MAX_MESSAGES); // Remove oldest
        }
    }

    public static List<String> getMessages() {
        return new ArrayList<>(messages);
    }

    public static void clear() {
        messages.clear();
    }
}
```

---

### Solution 6: Movement Improvements

**Add Movement Range**:
```java
public class CombatMovement {
    /**
     * Calculate all valid move positions within range
     */
    public static Set<BlockPos> calculateMoveRange(LivingEntity entity, int range, Level level) {
        Set<BlockPos> valid = new HashSet<>();
        BlockPos start = entity.blockPosition();

        // Flood-fill algorithm
        Queue<MoveNode> queue = new LinkedList<>();
        queue.add(new MoveNode(start, 0));
        Set<BlockPos> visited = new HashSet<>();

        while (!queue.isEmpty()) {
            MoveNode node = queue.poll();

            if (node.cost > range) continue;
            if (visited.contains(node.pos)) continue;
            visited.add(node.pos);

            // Check if valid
            if (isValidPosition(node.pos, level)) {
                valid.add(node.pos);

                // Add adjacent positions
                for (BlockPos adj : getAdjacent(node.pos)) {
                    queue.add(new MoveNode(adj, node.cost + 1));
                }
            }
        }

        return valid;
    }
}
```

**Add Move Animation**:
```java
public class CombatAnimation {
    public static void smoothMove(LivingEntity entity, BlockPos target, int durationTicks) {
        Vec3 start = entity.position();
        Vec3 end = Vec3.atCenterOf(target);

        // Interpolate position over time
        AnimationTask task = new AnimationTask(entity, start, end, durationTicks);
        CombatScheduler.scheduleAnimation(task);
    }
}
```

---

## Implementation Priority

### Phase 1: Core Fixes (Must Have)
1. ✅ **Implement basic enemy AI**
   - Movement toward player
   - Melee attacks
   - Turn delays

2. ✅ **Fix world pausing**
   - Use tick event interception
   - Remove NoAI approach

3. ✅ **Add turn flow delays**
   - Combat scheduler
   - Turn indicators
   - Combat log

### Phase 2: Polish (Should Have)
4. **Improve hostile detection**
   - Basic faction system
   - Better type checking

5. **Combat trigger improvements**
   - Faster auto-detection
   - Better manual feedback

6. **Movement improvements**
   - Movement range calculation
   - Better visuals

### Phase 3: Advanced (Nice to Have)
7. **Enemy AI improvements**
   - Pathfinding
   - Different behaviors per enemy type
   - Special abilities

8. **Animation system**
   - Smooth movement
   - Attack animations
   - Damage numbers

9. **Advanced mechanics**
   - Status effects during combat
   - Environmental hazards
   - Multi-target abilities

---

## Testing Plan

### Test 1: Basic Combat Flow
1. Start game
2. Find a single hostile mob
3. Attack it to trigger combat
4. Verify: Combat screen opens, world freezes, turn-based mode active
5. Player turn: Move or attack
6. Verify: Enemy turn happens with visible action
7. Continue until victory
8. Verify: Combat ends, world unfreezes

### Test 2: Multiple Enemies
1. Find 3-5 enemies
2. Trigger combat
3. Verify: All enemies added as combatants
4. Verify: Turn order correct
5. Verify: Each enemy takes action
6. Defeat all enemies
7. Verify: Victory condition triggers

### Test 3: Auto-Combat Mode
1. Press 'C' to enable auto-combat
2. Walk near hostile mob (within 15 blocks)
3. Verify: Combat triggers automatically
4. Complete combat
5. Verify: 5-second cooldown prevents immediate re-trigger
6. Walk away and approach again
7. Verify: Combat triggers again after cooldown

### Test 4: Player Death
1. Start combat with strong enemy
2. Let enemy kill player
3. Verify: Defeat message shows
4. Verify: Combat ends properly
5. Verify: Respawn works correctly

---

## Known Limitations

1. **No Pathfinding**: Enemies use simple "move toward player" logic
2. **No Abilities**: Enemies only do basic melee attacks
3. **Grid-Based Only**: Movement locked to block grid
4. **Single-Player Only**: No network sync for multiplayer
5. **Performance**: World pausing might lag with many entities

---

## API for Other Systems

```java
// Check if currently in combat
boolean inCombat = CombatManager.getInstance().isInCombat();

// Trigger combat programmatically
CombatDetection.triggerCombat(player, targetEntity);

// Check if entity is hostile
boolean hostile = HostilityManager.isHostileToPlayer(entity);

// Add message to combat log
CombatLog.addMessage("You hit the goblin for 5 damage!");
```

---

*Document created: December 4, 2025*
*For: Wasteland Crawl Turn-Based Combat System*
*Version: 0.1.0 - Analysis & Redesign*
