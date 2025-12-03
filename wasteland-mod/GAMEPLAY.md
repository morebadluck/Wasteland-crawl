# Wasteland Crawl - Gameplay Design

## Core Gameplay Loop

**Exploration Phase** (Real-time Minecraft):
- Explore dungeons in 3D first-person view
- Discover loot, traps, and secrets
- Navigate the environment freely
- Manage inventory and character

**Combat Phase** (Turn-based DCSS):
- Triggered when enemy is encountered or player initiates
- World freezes, tactical overlay appears
- Turn-by-turn tactical combat
- Returns to exploration when combat ends

---

## Turn-Based Combat System (Option B: In-World Grid)

### Combat Trigger
**How combat starts:**
1. Player enters enemy detection radius (e.g., 15 blocks)
2. OR player attacks an enemy first
3. OR player steps on enemy (melee range)

**Visual transition:**
- World freezes (entities stop moving)
- Grid overlay appears on floor (tactical squares)
- Valid movement tiles highlighted in green
- Enemy positions shown clearly
- Combat UI appears on screen

### The Combat Grid

**Grid System:**
- 1 Minecraft block = 1 grid square
- Player can see ~7x7 grid around themselves (expandable)
- Movement restricted to grid squares during combat
- Diagonal movement allowed (8 directions)
- Obstacles (walls, pits) shown clearly

**Visual Indicators:**
- **Green squares**: Valid movement positions
- **Red squares**: Enemy occupied
- **Yellow squares**: Hazards (fire, acid, etc.)
- **Blue squares**: Spell/ability targeting
- **White outline**: Current position

---

## Turn Structure

### Player Turn
**The player can take ONE action per turn:**

1. **Move** - Move to an adjacent square (8 directions)
2. **Attack** - Melee attack an adjacent enemy
3. **Cast Spell** - Use a memorized spell (costs MP)
4. **Invoke Ability** - Use a god-granted power or racial ability
5. **Use Item** - Consume potion, read scroll, use equipment
6. **Ranged Attack** - Fire ranged weapon at distant enemy
7. **Wait** - Pass turn, regain stamina/focus
8. **Flee** - Attempt to escape combat (success chance based on speed)

**After player acts:**
- Enemy takes their turn (AI controlled)
- Turn counter increments
- Status effects tick (poison, burning, etc.)
- Regeneration happens (if applicable)

### Enemy Turn
**AI determines action:**
- Move toward player
- Attack if in range
- Cast spell if applicable
- Use special ability
- Flee if heavily injured

**Turn order:**
- Based on Speed stat
- Faster entities get more turns
- Similar to DCSS speed system

---

## Combat Actions (Detailed)

### 1. MOVE
**Keybind**: Arrow keys / WASD / Numpad
**Description**: Move to an adjacent square (including diagonals)
**Mechanics**:
- Costs 1 turn
- Cannot move through walls or other entities
- Cannot move onto hazards (unless immune)
- Moving away from adjacent enemy may trigger attack of opportunity (TBD)

---

### 2. ATTACK (Melee)
**Keybind**: Left-click on enemy / Enter / Space
**Description**: Physical melee attack with equipped weapon
**Mechanics**:
- Requires adjacent enemy (1 square away, including diagonal)
- Damage based on:
  - Weapon damage
  - Strength stat
  - Fighting skill
  - Weapon skill (Maces, Axes, Short Blades, etc.)
- Hit chance based on:
  - Weapon accuracy
  - Dexterity
  - Enemy evasion
- Critical hits possible (based on skill level)
- Costs 1 turn

**UI Display**:
- Show weapon name
- Show estimated damage range
- Show hit % chance
- Preview attack before confirming

---

### 3. CAST SPELL (z key in DCSS)
**Keybind**: `z` key
**Description**: Cast a memorized spell from your spell list
**Mechanics**:
- Opens spell selection menu
- Shows all memorized spells
- Each spell costs MP (level × 2)
- Spell types:
  - **Offensive**: Magic Dart, Fireball, Lightning Bolt
  - **Defensive**: Shield, Haste, Invisibility
  - **Utility**: Blink (teleport), Apportation (pull item), Identify
  - **Conjuration**: Summon creatures, create walls
  - **Hexes**: Slow enemy, confuse, paralyze
  - **Necromancy**: Animate dead, drain life
  - **Transmutation**: Transform self or enemy

**Targeting**:
- Point-and-click for targeted spells
- Blue overlay shows area of effect
- Red if invalid target
- Show spell range and radius

**UI Display**:
- Spell name and level
- MP cost
- Range
- Effect description
- Failure chance (based on Spellcasting skill)

---

### 4. INVOKE ABILITY (a key in DCSS)
**Keybind**: `a` key
**Description**: Use god-granted powers or innate racial abilities
**Mechanics**:
- Opens abilities menu
- Lists all available abilities (god + racial)
- Each ability has:
  - Piety cost (for god abilities)
  - Cooldown timer
  - MP/HP cost (if applicable)

**God Abilities** (examples):
- **Trog**: Berserk (massive damage boost, can't cast spells)
- **Sif Muna**: Channel Magic (restore MP)
- **The Shining One**: Summon Angel
- **Makhleb**: Minor Destruction (chaos damage)
- **Okawaru**: Heroism (+combat stats)

**Racial Abilities** (examples):
- **Naga**: Spit Poison (ranged attack)
- **Vampire**: Bat Form (fly, see invisible)
- **Demigod**: Divine Vigor (stat boost)
- **Formicid**: Dig (create tunnel)
- **Felid**: Extra life (automatic resurrection)

**UI Display**:
- Ability name
- Cost (piety/MP/HP)
- Cooldown remaining
- Effect description
- Success chance

---

### 5. USE ITEM (i key for inventory in DCSS)
**Keybind**: `i` key
**Description**: Use consumable items or equipment
**Mechanics**:
- Opens inventory screen
- Filter by item type:
  - **Potions**: Healing, Might, Brilliance, Haste, etc.
  - **Scrolls**: Teleport, Identify, Enchant Weapon, etc.
  - **Food**: Rations, chunks (if hunger system implemented)
  - **Wands**: Fireball wand, polymorph wand, etc.
  - **Miscellaneous**: Boxes, runes, quest items

**Common Items**:
- **Potion of Heal Wounds**: Restore HP instantly
- **Potion of Curing**: Remove poison/sickness
- **Scroll of Teleport**: Random teleport (escape!)
- **Scroll of Blinking**: Controlled short-range teleport
- **Potion of Haste**: Double speed for several turns
- **Potion of Might**: +5 Strength temporarily

**UI Display**:
- Item name and type
- Effect description
- Quantity remaining
- Hotkey assignment (1-9 for quick use)

---

### 6. RANGED ATTACK
**Keybind**: `f` key (fire in DCSS)
**Description**: Fire ranged weapon at distant target
**Mechanics**:
- Requires equipped ranged weapon (bow, crossbow, sling, throwing)
- Requires ammunition (arrows, bolts, stones)
- Target selection via cursor
- Line of sight required
- Damage based on:
  - Weapon damage
  - Ammunition type
  - Dexterity
  - Ranged Combat skill
  - Distance (damage falloff)

**Ranged Weapons**:
- **Bows**: Medium range, decent damage, requires arrows
- **Crossbows**: High damage, slow reload, requires bolts
- **Slings**: Low damage, fast, uses stones/bullets
- **Throwing**: Javelins, throwing nets, daggers

**UI Display**:
- Show firing line
- Show hit % chance
- Show damage estimate
- Indicate if line of sight is blocked

---

### 7. WAIT / REST
**Keybind**: `.` or `5` (numpad center)
**Description**: Pass turn without acting
**Mechanics**:
- Player does nothing
- Enemy gets turn
- Can be used to:
  - Wait for enemy to approach
  - Let status effects tick
  - Regenerate small amount of HP/MP (if resting system implemented)
  - Delay turn for tactical positioning

---

### 8. FLEE / ESCAPE
**Keybind**: `Esc` or designated flee key
**Description**: Attempt to exit combat mode
**Mechanics**:
- Success chance based on:
  - Distance from enemies
  - Speed stat
  - Enemy pursuit AI
- If successful:
  - Combat ends
  - Player returns to exploration mode
  - Enemies may chase in real-time
- If failed:
  - Enemies get free attack
  - Player loses turn

**When you can flee**:
- No enemies adjacent (or high speed)
- Entrance to current room still accessible
- Not trapped/surrounded

---

## Combat UI Layout

**Main Combat Screen**:
```
┌─────────────────────────────────────────────────────┐
│  HP: 45/60 ███████░░░    MP: 12/18 ████░░           │
│  Turn: 7    Speed: 10    Status: [POISON]          │
├─────────────────────────────────────────────────────┤
│                                                     │
│         [3D Minecraft View with Grid Overlay]       │
│                                                     │
│         Player: @        Enemy: K (Kobold)         │
│         Green squares = valid moves                │
│         Blue squares = spell target area           │
│                                                     │
├─────────────────────────────────────────────────────┤
│  ACTIONS:                                          │
│  [M] Move      [Z] Cast Spell    [A] Invoke        │
│  [Space] Attack [F] Ranged       [I] Use Item      │
│  [.] Wait      [Esc] Flee                          │
├─────────────────────────────────────────────────────┤
│  Target: Kobold (K)    HP: 8/15  Distance: 3      │
│  Status: Afraid, -2 AC                             │
└─────────────────────────────────────────────────────┘
```

**Action Menus** (popup overlays):
- Spell selection grid
- Inventory item list
- Abilities menu
- Target selection cursor

---

## Combat Flow Example

**Turn 1:**
1. Player's turn: Grid shows 8 valid movement squares
2. Player presses `z` to cast spell
3. Spell menu appears: Magic Dart, Fireball, Blink
4. Player selects "Magic Dart" (costs 2 MP)
5. Targeting cursor appears
6. Player clicks on Kobold
7. Spell fires, deals 5 damage
8. Player's turn ends

**Turn 2:**
1. Kobold's turn (AI controlled)
2. Kobold moves 2 squares closer
3. Kobold's turn ends

**Turn 3:**
1. Player's turn
2. Player presses `Space` to attack (Kobold now adjacent)
3. Attack connects: 8 damage
4. Kobold dies
5. Combat ends, returns to exploration mode
6. XP gained: +10

---

## Advanced Mechanics

### Line of Sight
- Walls block vision
- Can't target what you can't see
- Fog of war in dark areas (requires light source)

### Area of Effect (AOE)
- Fireball hits 3×3 area
- Poison Cloud lingers for multiple turns
- Ice Storm covers large radius

### Status Effects
- **Poison**: Lose HP each turn
- **Burning**: Fire damage over time
- **Paralyzed**: Can't act for X turns
- **Slowed**: Enemies get more turns
- **Hasted**: Player gets more turns
- **Confused**: Random movement
- **Blinded**: Reduced accuracy

### Terrain Hazards
- **Lava**: Damage per turn standing in it
- **Shallow Water**: Slows movement
- **Acid Pools**: Corrodes armor
- **Trap Doors**: Fall to lower level

### Positioning Matters
- Backstab bonus (DCSS style)
- Choke points (fight 1v1 even if outnumbered)
- High ground advantage
- Cover from ranged attacks

---

## DCSS Actions Reference

Based on classic DCSS controls:

| Key | Action | Description |
|-----|--------|-------------|
| Arrow/WASD | Move | Move in 8 directions |
| `,` or `g` | Pickup | Grab item from ground |
| `d` | Drop | Drop item from inventory |
| `i` | Inventory | Open inventory |
| `w` | Wield | Equip weapon |
| `W` | Wear | Equip armor |
| `T` | Take off | Remove armor |
| `P` | Put on | Equip jewelry |
| `R` | Remove | Remove jewelry |
| `e` | Eat | Consume food |
| `q` | Quaff | Drink potion |
| `r` | Read | Read scroll |
| `z` | Zap (Cast) | Cast spell |
| `Z` | Zap wand | Use wand |
| `a` | Ability | Invoke god/racial ability |
| `f` | Fire | Ranged attack |
| `v` | Evoke | Use magical item |
| `@` | Character | Character sheet |
| `m` | Memorize | Spell memorization |
| `M` | Spells | View spell list |
| `.` or `5` | Wait | Pass turn |
| `o` | Auto-explore | Auto-move (exploration mode only) |
| `G` | Travel | Autotravel to location |
| `x` | Examine | Look at something |
| `>` | Downstairs | Descend level |
| `<` | Upstairs | Ascend level |
| `s` | Search | Search for hidden doors/traps |

We'll adapt these to Minecraft keybinds with an overlay UI.

---

## Next Steps for Implementation

**Phase 1: Basic Combat**
1. Detect enemy proximity
2. Freeze world when combat triggers
3. Display grid overlay
4. Implement Move and Attack actions
5. Basic turn order system
6. Simple enemy AI (move toward player, attack if adjacent)

**Phase 2: Core Actions**
7. Spell casting system
8. Item usage
9. Ranged combat
10. Abilities/Invocations

**Phase 3: Polish**
11. Status effects
12. Advanced AI
13. Tactical positioning bonuses
14. Combat animations and visual feedback
15. Sound effects

**Phase 4: Balance**
16. Difficulty tuning
17. Monster stats
18. Spell balance
19. Item rarity and power
