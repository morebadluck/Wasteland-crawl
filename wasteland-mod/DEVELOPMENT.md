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

---

## 2. LORE - Story & Narrative
The underlying story, backstory, and world-building.

### Current Concept:
Post-apocalyptic wasteland with ancient dungeons containing remnants of lost civilization.

### Todo:
- [ ] Define the apocalypse event (what happened?)
- [ ] History of the wasteland
- [ ] Purpose of the dungeons
- [ ] Monster origins and factions
- [ ] Artifacts and their significance
- [ ] Gods/religions (DCSS has gods)
- [ ] Character backstories for each race
- [ ] Dungeon lore per level tier (early/mid/late)

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

### Todo:
- [ ] Combat system (melee, ranged, magic)
- [ ] Monster AI and behavior
- [ ] Items and inventory
- [ ] Equipment system (weapons, armor, jewelry)
- [ ] Consumables (potions, scrolls)
- [ ] Status effects (poison, confusion, etc.)
- [ ] Gods and religion system
- [ ] Hunger system (or simplified resource)
- [ ] Stealth and detection
- [ ] Resistances and vulnerabilities
- [ ] Mutations
- [ ] Ranged combat
- [ ] Area of effect spells
- [ ] Summoning mechanics
- [ ] Experience and character growth
- [ ] Death and permadeath handling

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
