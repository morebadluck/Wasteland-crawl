# Wasteland Crawl - Development Organization

This document organizes development into five major categories:

## 1. WORLD - Environment & Generation
World creation, terrain, biomes, and procedural generation.

### Current Status:
- ✅ Overworld integration complete
- ✅ Dungeon entrance structures (ruined buildings)
- ✅ Basic dungeon lighting system
- ⏸️ Natural entrance spawning (disabled due to threading)
- ⏸️ Wasteland biome generation
- ⏸️ Wasteland decorations (rubble, craters, debris)

### Todo:
- [ ] Fix chunk-based entrance spawning with async
- [ ] Wasteland biome with custom terrain generation
- [ ] Weather effects (dust storms, fog)
- [ ] Dynamic lighting (day/night cycle in overworld)
- [ ] More entrance variants (bunkers, shacks, caves)
- [ ] Underground biome variation by depth
- [ ] 4 major dungeon entrances (one per biome):
  - [ ] Desert biome dungeon entrance
  - [ ] Ice/Snow biome dungeon entrance
  - [ ] Jungle biome dungeon entrance
  - [ ] Nether/Volcanic biome dungeon entrance
- [ ] Final dungeon (Zot) entrance (unlocks with 4 keys)
- [ ] Hell branch entrances (optional endgame areas)

---

## 2. LORE - Story & Narrative
The underlying story, backstory, and world-building.

### Current Concept:
Biblical apocalypse caused by a divine war between angels and demons. The gods tore open a rift between their realms and Earth, unleashing monsters and devastation. Sparse human survivors cling to existence, some offering goods and safe haven, while most are too weak to traverse the dangerous wasteland.

### Completed (see LORE.md):
- ✅ The apocalypse event: Divine war between angels and demons
- ✅ History of the wasteland: 300 years after the Rift
- ✅ Purpose of the dungeons: Fragments of Heaven/Hell embedded in Earth
- ✅ Monster origins: Creatures from the rift, divine war soldiers
- ✅ Artifacts: Four Rune Keys + Orb of Zot
- ✅ Gods/religions: DCSS gods as warring deities (Lawful/Chaotic/Neutral)
- ✅ Character backstories: Each race has origin tied to divine war
- ✅ Dungeon lore by tier: Early (outer vaults) / Mid (living quarters) / Late (deep sanctums)
- ✅ Endgame structure: 4 Keys → Zot → Orb → Victory
- ✅ Hell Branches: Pandemonium, Cocytus, Gehenna, Tartarus, Abyss
- ✅ Central mystery: War was orchestrated, Orb IS the rift
- ✅ Player choice: Close/Expand/Claim/Destroy the rift

### Future Expansion:
- [ ] Specific NPC characters (merchants, quest-givers)
- [ ] Unique monster backstories and personalities
- [ ] God-specific questlines and endings
- [ ] In-game lore books and journals
- [ ] Dungeon-specific histories and themes

---

## 3. GRAPHICS - Visual Design
Look, feel, textures, models, and visual effects.

### Current Status:
- ✅ Dungeon vault rendering from DCSS vaults
- ✅ Block-based dungeon construction
- ✅ Torch lighting
- ✅ Entrance structures (stone bricks, cracked variants)
- ✅ 809 DCSS tiles extracted

### Todo:
- [ ] Custom textures for wasteland blocks
- [ ] Particle effects (dust, magic, explosions)
- [ ] Monster models/skins
- [ ] Custom item textures
- [ ] Spell visual effects
- [ ] Portal animations
- [ ] Character race skins
- [ ] HUD overlays (health, mana, status effects)
- [ ] Minimap integration

---

## 4. UI - User Interface
Menus, screens, keybinds, and user interaction.

### Current Status:
- ✅ Character sheet screen (@)
- ✅ Skills screen (m) - interactive, clickable
- ✅ Spells screen (M) - interactive, clickable
- ✅ DCSS keybinds implemented
- ✅ Mouse hover effects
- ✅ Scrolling on long lists
- ✅ 75% font scaling for better readability

### Todo:
- [ ] Character creation screen (race selection)
- [ ] Inventory management (i key)
- [ ] Equipment screen
- [ ] Abilities screen (a key)
- [ ] Spell casting UI (z key)
- [ ] Message log/combat log
- [ ] Quest/objectives UI
- [ ] Settings/options menu
- [ ] Death screen
- [ ] High scores/achievements

---

## 5. DCSS - Core Game Mechanics
Core gameplay systems from Dungeon Crawl Stone Soup.

### Current Status:
- ✅ 19 playable races with stats
- ✅ 33 skills with training system
- ✅ 60+ spells (levels 1-9)
- ✅ Aptitude system (race-specific skill rates)
- ✅ PlayerCharacter class
- ✅ Skill training with XP
- ✅ Spell learning/memorization system
- ✅ HP/MP system
- ✅ Level progression (XP → Level up)
- ✅ Depth-based dungeon progression
- ✅ 36 vault templates from DCSS
- ✅ Monster spawning by tier

### GAMEPLAY DESIGN: Turn-Based Combat ✅ DECIDED

**Core Concept**: When the player encounters an enemy, the game transitions from real-time Minecraft exploration to turn-based tactical combat (DCSS-style).

**Implementation Choice: Option B - In-World Grid Combat**
- Keep 3D Minecraft view during combat
- Overlay tactical grid on floor
- Pause all entities except combatants
- Show valid moves, targets, spell areas directly in world
- Feels more "Minecraft" while preserving DCSS tactics

**Full design documented in GAMEPLAY.md**

**How it works**:
1. Player explores dungeons in real-time 3D (normal Minecraft)
2. When enemy enters detection radius (~15 blocks), combat triggers
3. World freezes, grid overlay appears
4. Player takes turn (Move, Attack, Cast, Invoke, Use Item, etc.)
5. Enemy AI takes turn
6. Repeat until victory/defeat/escape
7. Return to real-time exploration

**Player Actions Per Turn** (DCSS-based):
- **Move**: 8-directional grid movement
- **Attack**: Melee attack adjacent enemy
- **Cast Spell (z)**: Use memorized spells (costs MP)
- **Invoke Ability (a)**: God powers or racial abilities
- **Use Item (i)**: Potions, scrolls, food, wands
- **Ranged Attack (f)**: Fire bows, crossbows, throwing weapons
- **Wait (.)**: Pass turn
- **Flee (Esc)**: Attempt to exit combat

**Combat UI Elements**:
- HP/MP bars
- Turn counter
- Status effects display
- Grid overlay (green = valid move, red = enemy, blue = spell target)
- Action buttons: [M]ove [Z]Cast [A]bility [I]tem [F]ire [.]Wait [Esc]Flee
- Target info (enemy HP, distance, status)
- Spell/item selection menus (popup overlays)

### Endgame Progression (DCSS-style):
**Win Condition**: Retrieve the ultimate artifact and escape to the surface

**Structure**:
1. **4 Rune Keys** - Located in 4 major dungeons, one in each Minecraft biome:
   - Desert Dungeon → Desert Key
   - Ice/Snow Dungeon → Frozen Key
   - Jungle Dungeon → Overgrown Key
   - Nether/Volcanic Dungeon → Infernal Key

2. **Final Dungeon (Zot)** - Accessible only after collecting all 4 keys
   - Contains the ultimate artifact (Orb of Zot equivalent)
   - Most dangerous dungeon in the game
   - Retrieval triggers escape sequence

3. **Hell Branches (Optional)** - Extra challenging endgame content:
   - Pandemonium (chaotic demon realm)
   - Cocytus (frozen hell)
   - Gehenna (volcanic hell)
   - Tartarus (undead hell)
   - Contain powerful unique items and enemies
   - Not required for victory, but offer best loot

4. **Victory** - Bring the artifact back to the surface to win

### Todo - Combat System (see GAMEPLAY.md):

**Phase 1: Core Combat Framework**
- [ ] Enemy detection radius system
- [ ] Combat mode trigger (freeze world, enter turn-based)
- [ ] Grid overlay rendering (show valid moves)
- [ ] Basic player movement on grid (8 directions)
- [ ] Turn order system (player → enemy → repeat)
- [ ] Combat exit/resume exploration mode

**Phase 2: Basic Actions**
- [ ] Melee attack action (adjacent enemy)
- [ ] Damage calculation (weapon, stats, skills)
- [ ] Hit chance calculation (accuracy vs evasion)
- [ ] Enemy AI (basic: move toward player, attack if adjacent)
- [ ] Combat UI overlay (HP/MP bars, turn counter, action buttons)

**Phase 3: Advanced Actions**
- [ ] Spell casting system (z key)
  - [ ] Spell selection menu
  - [ ] Targeting system (click, area preview)
  - [ ] MP cost and spell effects
- [ ] Item usage (i key)
  - [ ] Inventory screen in combat
  - [ ] Potion/scroll effects
  - [ ] Quick-use hotkeys (1-9)
- [ ] Abilities/Invocations (a key)
  - [ ] God ability menu
  - [ ] Racial ability menu
  - [ ] Piety/cooldown tracking
- [ ] Ranged combat (f key)
  - [ ] Line of sight checking
  - [ ] Ammunition system
  - [ ] Range/damage falloff
- [ ] Wait action (.) and Flee action (Esc)

**Phase 4: Advanced Mechanics**
- [ ] Status effects system
  - [ ] Poison, Burning, Paralysis, Slow, Haste, etc.
  - [ ] Effect ticking each turn
  - [ ] Visual indicators
- [ ] Line of sight and fog of war
- [ ] Area of effect spells (fireball, poison cloud, etc.)
- [ ] Terrain hazards (lava, acid, traps)
- [ ] Tactical positioning (backstab, choke points, high ground)
- [ ] Advanced enemy AI (flee when wounded, use abilities, coordinate)

**Phase 5: Integration**
- [ ] Items and inventory system
- [ ] Equipment (weapons, armor, jewelry)
- [ ] Consumables (potions, scrolls, food)
- [ ] Gods and religion integration
- [ ] Stealth and detection
- [ ] Resistances and vulnerabilities
- [ ] Mutations
- [ ] Summoning mechanics
- [ ] Experience and leveling from combat
- [ ] Death and permadeath handling

**Endgame Content**
- [ ] Rune key system (4 keys in biome-specific dungeons)
- [ ] Final dungeon (Zot) with key requirement
- [ ] Hell branch dungeons (Pan, Cocytus, Gehenna, Tartarus)
- [ ] Ultimate artifact retrieval and escape sequence
- [ ] Victory condition handling
- [ ] Multiple endings based on choices

---

## Project Phases

### Phase 1: Foundation ✅ COMPLETE
- DCSS vault creation (50 wasteland vaults)
- DCSS compilation and tile extraction
- Minecraft mod setup

### Phase 2: Core Systems ✅ COMPLETE
- Overworld integration
- Character system (races, skills, spells)
- Dungeon rendering from JSON
- Basic progression (depth, tiers)
- Interactive UI

### Phase 3: CURRENT - Polish & Expand
Choose focus area:
- **WORLD**: Improve environment, biomes, generation
- **LORE**: Develop story and narrative
- **GRAPHICS**: Enhance visuals and effects
- **UI**: Add more screens and polish
- **DCSS**: Implement combat and items

### Phase 4: Combat & Gameplay
- Implement full combat system
- Monster AI
- Items and equipment
- Status effects

### Phase 5: Content & Balance
- More vaults and levels
- Monster variety
- Item generation
- Difficulty balancing

### Phase 6: Polish & Release
- Sound effects and music
- Optimization
- Bug fixes
- Public release

---

## Quick Reference

### DCSS Keybinds (Implemented)
- `@` (Shift+2) - Character Sheet
- `m` - Skills Training
- `M` (Shift+M) - Spell Memorization
- `z` - Cast Spell (planned)
- `a` - Abilities (planned)
- `i` - Inventory (planned)

### File Organization
```
wasteland-mod/
├── src/main/java/com/wasteland/
│   ├── character/          # DCSS - Race, Skills, Spells, Aptitudes
│   ├── client/             # UI - Keybinds, Events
│   │   └── gui/            # UI - Screen classes
│   ├── DungeonRenderer.java      # WORLD - Vault rendering
│   ├── DungeonEntrance.java      # WORLD - Structure generation
│   ├── WastelandWorldGen.java    # WORLD - World generation
│   ├── MonsterSpawner.java       # DCSS - Monster spawning
│   ├── PortalManager.java        # DCSS - Portal system
│   └── DungeonProgression.java   # DCSS - Depth tracking
└── src/main/resources/
    └── rooms/              # DCSS - Vault JSON files
```

---

## Next Steps

Pick a category to focus on:

1. **WORLD** - Improve world generation, add wasteland biome
2. **LORE** - Develop backstory, define the apocalypse
3. **GRAPHICS** - Add custom textures, particle effects
4. **UI** - Build inventory, equipment, casting screens
5. **DCSS** - Implement combat system, items

Which area would you like to explore next?
