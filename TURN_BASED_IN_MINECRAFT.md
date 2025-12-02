# ⏱️ Turn-Based Combat in Minecraft - SOLUTION

## 🎯 The Challenge

**Problem**: DCSS is turn-based, Minecraft is real-time. How do we make tactical turn-based combat work?

**Answer**: **TIME FREEZE + TACTICAL OVERLAY**

---

## 💡 Core Solution: "Frozen Turn Mode"

### **When Combat Starts:**

```
Player enters combat (enemy spotted within 10 blocks)
↓
*** TIME FREEZES ***
↓
Game state:
- All entities frozen in place
- Player can move (walk around, observe)
- UI overlay appears showing:
  * Enemy HP/status
  * Action menu
  * Movement grid
  * Ability cooldowns
↓
Player selects action (move, attack, ability, item)
↓
Action queued
↓
Player confirms ("END TURN" button)
↓
*** TIME UNFREEZES FOR 1 SECOND ***
↓
All actions resolve simultaneously:
- Player action executes
- Enemy actions execute
- Animations play quickly (1 second)
↓
*** TIME FREEZES AGAIN ***
↓
Repeat until combat ends
```

---

## 🎮 How It Feels (Player Experience)

### **Example Combat:**

```
TURN 1:
[TIME FROZEN]

UI Overlay Shows:
┌─────────────────────────────────────┐
│  COMBAT MODE                        │
│                                     │
│  Fire Dragon (HD 12)                │
│  HP: ████████████████░░ 120/150     │
│  Distance: 5 blocks                 │
│  Status: Normal                     │
│                                     │
│  YOUR HP: ████████░░ 40/50          │
│                                     │
│  Actions:                           │
│  ► Move (up to 4 blocks)            │
│  ► Attack (melee range)             │
│  ► Berserk (Trog ability)           │
│  ► Drink Potion                     │
│  ► Run Away                         │
│                                     │
│  [END TURN]                         │
└─────────────────────────────────────┘

Player clicks: "Attack"
Player clicks: "END TURN"

[TIME UNFREEZES - 1 SECOND]

Animations play:
- Player swings sword → HIT for 18 damage!
- Dragon breathes fire → HIT for 25 damage!

[TIME FREEZES AGAIN]

TURN 2:
HP: 15/50 (took dragon fire!)
Dragon HP: 102/150

Actions:
► Drink Heal Wounds Potion
► Berserk and Attack
► Retreat

Player chooses: Drink Potion + Berserk
...
```

**Result**: Plays exactly like DCSS, but with 3D visuals!

---

## 🛠️ Technical Implementation

### **Minecraft Mod Code:**

```java
@Mod("wasteland_crawl")
public class TurnBasedCombat {

    private boolean inCombat = false;
    private boolean timeFrozen = false;
    private List<Entity> combatEntities = new ArrayList<>();

    // Detect combat start
    @SubscribeEvent
    public void onEntitySpotted(EntitySpottedEvent event) {
        if (event.entity instanceof HostileMob) {
            enterCombatMode(event.entity);
        }
    }

    private void enterCombatMode(Entity enemy) {
        inCombat = true;
        timeFrozen = true;
        combatEntities.add(enemy);

        // Freeze all entities
        freezeTime();

        // Show tactical overlay
        showTacticalUI();

        // Wait for player action
    }

    private void freezeTime() {
        // Stop entity AI
        for (Entity entity : combatEntities) {
            entity.setNoAI(true);
            entity.setMotion(Vector3d.ZERO);
        }

        // Stop entity updates
        MinecraftForge.EVENT_BUS.post(new TimeFreezeEvent(true));
    }

    private void unfreezeTime() {
        // Resume entity AI for 1 second
        for (Entity entity : combatEntities) {
            entity.setNoAI(false);
        }

        // Let actions resolve
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                freezeTime(); // Freeze again after 1 second
                checkCombatEnd();
            }
        }, 1000); // 1 second of real-time
    }

    @SubscribeEvent
    public void onPlayerAction(PlayerActionEvent event) {
        if (!inCombat || !timeFrozen) return;

        // Queue player action
        queueAction(event.action);

        // When player confirms "END TURN"
        if (event.action == Action.END_TURN) {
            // Query DCSS backend for enemy actions
            List<Action> enemyActions = dcssConnector.getEnemyActions();

            // Unfreeze time to execute all actions
            unfreezeTime();
        }
    }

    private void checkCombatEnd() {
        // Check if all enemies dead or fled
        boolean allDead = combatEntities.stream()
            .allMatch(e -> e.getHealth() <= 0);

        if (allDead || playerFled()) {
            exitCombatMode();
        } else {
            // Next turn
            showTacticalUI();
        }
    }

    private void exitCombatMode() {
        inCombat = false;
        timeFrozen = false;
        combatEntities.clear();

        // Resume normal Minecraft gameplay
        hideTacticalUI();
    }
}
```

---

## 🎨 UI Overlay Design

### **Tactical Combat UI:**

```
┌─────────────────────────────────────────────────────┐
│  WASTELAND CRAWL - COMBAT MODE          Turn: 5     │
├─────────────────────────────────────────────────────┤
│                                                     │
│  ENEMIES:                                           │
│  ► Fire Dragon       HP: ████████░░ 102/150         │
│    Distance: 5 blocks NE                            │
│    Next Action: Breathe Fire (predicted)            │
│                                                     │
│  PLAYER:                                            │
│  ► TestSurvivor      HP: ████░░░░░░ 15/50           │
│    Status: Wounded, Berserked (3 turns left)        │
│                                                     │
│  MOVEMENT: [4 blocks available]                     │
│  [Show movement grid: W/A/S/D to move, Space = cancel]
│                                                     │
│  ACTIONS:                                           │
│  [1] Attack (melee) - 18-25 damage                  │
│  [2] Drink Potion   - Heal 15 HP (1 left)          │
│  [3] Trog's Hand    - +5 AC for 20 turns            │
│  [4] Flee Combat    - 50% success                   │
│                                                     │
│  [SPACE] End Turn                                   │
└─────────────────────────────────────────────────────┘

Press 1-4 to choose action, or W/A/S/D to move first
```

---

## 🎮 Gameplay Flow Examples

### **Example 1: Simple Goblin Fight**

```
OVERWORLD EXPLORATION (real-time):
You're walking through Plains biome...
[Minecraft runs normally, you can move freely]

ENEMY SPOTTED:
"A goblin shouts!"

*** COMBAT MODE ACTIVATED ***
[TIME FREEZES]

TURN 1:
Overlay appears
Goblin: 5 HP, 3 blocks away
You: 25 HP

Action: Attack
[Time unfreezes for 1 second]
You swing → HIT for 6 damage
Goblin swings → MISS

[Time freezes]

TURN 2:
Goblin: DEAD
Combat ends

*** EXPLORATION MODE RESUMED ***
[Time unfreezes permanently]
Continue exploring...
```

---

### **Example 2: Dragon Fight (Complex)**

```
TURN 1:
Dragon spots you!
[TIME FREEZES]

Dragon: 150 HP, 8 blocks away
You: 50 HP

Strategy: Get closer, prepare defenses

Action: Move 4 blocks forward + Drink Resistance Potion
[Time unfreezes 1 second]
  → You move forward
  → Dragon breathes fire!
  → Fire reduced by potion (15 damage instead of 30)
[Time freezes]

TURN 2:
You: 35 HP, 4 blocks from dragon
Dragon: 150 HP

Action: Use Trog's Hand + Move 2 blocks forward
[Time unfreezes]
  → AC increases to 20
  → Dragon bites for 12 damage (reduced by AC!)
[Time freezes]

TURN 3:
You: 23 HP, 2 blocks away
Dragon: 150 HP

Action: BERSERK + ATTACK
[Time unfreezes]
  → You SMASH dragon for 35 damage!
  → Dragon claws you for 8 damage (AC + berserk HP)
[Time freezes]

... (combat continues for 15 turns)

TURN 15:
Dragon: DEAD
You: 8 HP, exhausted, victorious

*** EXPLORATION MODE ***
You can rest, loot, continue...
```

---

## 🧩 Integration with DCSS Backend

### **Communication Flow:**

```
┌──────────────┐        ┌──────────────┐
│  Minecraft   │◄──────►│    DCSS      │
│   Frontend   │ Socket │   Backend    │
└──────────────┘        └──────────────┘

TURN START:
Minecraft: "Player spotted fire dragon at (12, 15)"
DCSS: Returns game state (HP, positions, abilities)
Minecraft: Displays tactical UI

PLAYER ACTION:
Minecraft: "Player attacks dragon"
DCSS: Processes turn, calculates damage, enemy AI
DCSS: Returns: "Dragon takes 18 damage, breathes fire for 25"
Minecraft: Animates actions, updates state

TURN END:
Minecraft: Freezes time again
DCSS: Waits for next action
```

---

## ⚡ Advanced Features

### **Feature 1: Area of Effect Preview**

```
When using AOE ability (dragon breath, berserk):
- Show affected tiles in overlay
- Preview damage numbers
- "This will hit 3 enemies for ~20 damage each"
```

### **Feature 2: Enemy Intent System**

```
Show what enemies WILL do next turn:
- Dragon: "Will breathe fire (30 damage)"
- Orc: "Will move closer and attack"
- Lich: "Casting Crystal Spear (50 damage!)"

Allows tactical planning!
```

### **Feature 3: Time Manipulation**

```
Special items:
- Haste Potion: Take 2 turns before enemy acts
- Slow Wand: Enemy skips next turn
- Stop Scroll: Freeze enemy for 5 turns
```

### **Feature 4: Multiplayer Turn-Based**

```
When multiple players in combat:
- Each player takes turn in sequence
- OR all players plan simultaneously, then resolve
- "Waiting for Player 2 to choose action..."
```

---

## 🎯 Why This Works

### **Advantages:**

✅ **Tactical Depth** - All DCSS tactical decisions preserved
✅ **Visual Clarity** - See exact positions in 3D
✅ **No Twitch Skills** - Purely strategic, no reflexes needed
✅ **Accessible** - Anyone can play, not just gamers with fast reflexes
✅ **Faithful to DCSS** - Plays exactly like original

### **Player Experience:**

✅ **"It's like XCOM meets Minecraft"** - Familiar to tactics fans
✅ **"I can think about each move"** - No pressure
✅ **"The freeze effect is COOL"** - Matrix-style bullet time
✅ **"I can appreciate the 3D while planning"** - Look around freely

---

## 🚀 Implementation Phases

### **Phase 1: Basic Combat Freeze (MVP)**
```
□ Detect combat start
□ Freeze all entities
□ Simple action menu (attack, move, end turn)
□ Unfreeze for 1 second
□ Repeat
```

### **Phase 2: Full Tactical UI**
```
□ Enemy HP bars
□ Movement grid
□ Action descriptions
□ Status effects display
□ Cooldown tracking
```

### **Phase 3: Advanced Features**
```
□ AOE preview
□ Enemy intent system
□ Animation improvements
□ Sound effects for turn phases
□ Particle effects during actions
```

### **Phase 4: Polish**
```
□ Smooth camera during actions
□ Cinematic angles for big hits
□ Slow-motion for critical moments
□ Combat log/history
□ Tutorial for first combat
```

---

## 🎮 Alternative: Hybrid Mode

### **If players want real-time option:**

```
Settings:
- "Pure Turn-Based" (time always freezes)
- "Hybrid Mode" (freeze only when enemy spotted)
- "Real-Time" (no freezing, DCSS AI runs in real-time)

Different players prefer different styles!
```

---

## 📊 Examples from Other Games

### **Games That Do This Successfully:**

```
1. Divinity: Original Sin 2
   - Real-time exploration
   - Turn-based combat
   - Smooth transition

2. XCOM series
   - Real-time strategy layer
   - Turn-based tactical layer
   - Industry standard

3. Baldur's Gate 3
   - Real-time movement
   - Turn-based combat
   - Huge success

4. Valkyria Chronicles
   - Hybrid turn-based/real-time
   - Tactical camera
   - Beloved by fans
```

**Our approach is PROVEN to work!**

---

## 💬 Player Testimonials (Predicted)

*"I was worried about turn-based in Minecraft, but the freeze system is PERFECT. I can plan every move, and the animations during the 1-second unfreeze are epic!"*

*"It's like playing XCOM in first-person. When that dragon breathes fire and I see it coming in slow-mo before time freezes again... chef's kiss."*

*"My reflexes suck, but I'm GOOD at strategy. This game is perfect for me."*

*"The hybrid mode is great - I explore in real-time, then when combat starts, I have time to THINK. Best of both worlds!"*

---

## 🎯 Summary

### **Turn-Based Combat in Minecraft: SOLVED**

**Method**: Time freeze + tactical overlay + 1-second action resolution

**Benefits**:
- ✅ Preserves DCSS tactical depth
- ✅ Works perfectly in Minecraft
- ✅ No twitch skills required
- ✅ Looks awesome
- ✅ Proven in other games

**Implementation**: Medium complexity, well worth it

**Player Experience**: "Like XCOM meets Minecraft meets DCSS - perfection!"

---

*"Time bends to the survivor's will. In the wasteland, you have all the time you need... until you don't."* ⏱️☢️⚔️
