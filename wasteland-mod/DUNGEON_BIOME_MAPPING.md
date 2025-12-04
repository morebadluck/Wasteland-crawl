# Dungeon-Biome Mapping Reference

**Purpose**: Map DCSS dungeon branches to Minecraft biomes for thematic dungeon generation in the wasteland.

---

## DCSS Dungeon Branches

### Main Dungeon Path
1. **Dungeon (D:1-15)** - Standard dungeon levels, general theme
2. **Temple** - Multi-altar chamber (D:4-7)
3. **Orcish Mines (Orc:1-2)** - Orc civilization ruins
4. **Lair of Beasts (Lair:1-5)** - Natural caves, animals
5. **Swamp (Swamp:1-4)** - Poisonous, water-filled
6. **Snake Pit (Snake:1-4)** - Serpents and nagas
7. **Spider's Nest (Spider:1-4)** - Webs and arachnids
8. **Shoals (Shoals:1-4)** - Beaches and merfolk
9. **Slime Pits (Slime:1-5)** - Acidic slimes
10. **Vaults (Vaults:1-5)** - Heavily guarded treasure
11. **Crypt (Crypt:1-3)** - Undead crypts
12. **Tomb (Tomb:1-3)** - Ancient mummies
13. **Depths (Depths:1-5)** - Deep dungeon
14. **Abyss** - Chaotic dimension
15. **Pandemonium** - Demon lords
16. **Hell Branches**:
    - Gehenna (fire)
    - Cocytus (ice)
    - Tartarus (death)
    - Dis (iron)
17. **Vestibule of Hell** - Hell entrance
18. **Zot (Zot:1-5)** - Final endgame area

### Side Branches
19. **Sewers** - Early optional area
20. **Ossuary** - Undead tomb (early)
21. **Bailey** - Military fortress
22. **Ice Cave** - Frozen cavern
23. **Volcano** - Lava-filled
24. **Wizlab** - Wizard tower
25. **Trove** - Treasure vault (rare)

---

## Minecraft Biomes (1.20.1)

### Overworld Biomes

#### Temperature: Cold
- **Snowy Plains**
- **Ice Spikes**
- **Snowy Taiga**
- **Snowy Beach**
- **Frozen River**
- **Frozen Peaks**
- **Jagged Peaks**
- **Snowy Slopes**
- **Grove**

#### Temperature: Temperate
- **Plains**
- **Sunflower Plains**
- **Forest**
- **Flower Forest**
- **Birch Forest**
- **Old Growth Birch Forest**
- **Dark Forest**
- **Taiga**
- **Old Growth Spruce Taiga**
- **Old Growth Pine Taiga**
- **Meadow**
- **Cherry Grove**
- **River**
- **Beach**
- **Stony Shore**
- **Windswept Hills**
- **Windswept Gravelly Hills**
- **Windswept Forest**

#### Temperature: Warm/Hot
- **Desert**
- **Savanna**
- **Savanna Plateau**
- **Windswept Savanna**
- **Badlands**
- **Wooded Badlands**
- **Eroded Badlands**
- **Jungle**
- **Sparse Jungle**
- **Bamboo Jungle**

#### Special/Neutral
- **Swamp**
- **Mangrove Swamp**
- **Mushroom Fields**
- **Deep Dark**
- **Dripstone Caves**
- **Lush Caves**

#### Ocean Biomes
- **Ocean**
- **Deep Ocean**
- **Cold Ocean**
- **Deep Cold Ocean**
- **Frozen Ocean**
- **Deep Frozen Ocean**
- **Lukewarm Ocean**
- **Deep Lukewarm Ocean**
- **Warm Ocean**

#### Underground
- **Underground** (generic)
- **Deep Dark**

---

## Dungeon-to-Biome Mapping

**Instructions**: Fill in preferred biomes for each dungeon type. Multiple biomes can be listed with weights.

### Main Branches

| DCSS Branch | Biome(s) | Weight | Notes |
|-------------|----------|--------|-------|
| Dungeon (D:1-15) | TBD | - | Standard dungeons, any biome |
| Temple | TBD | - | Religious site |
| Orcish Mines | TBD | - | Orc settlements |
| Lair of Beasts | TBD | - | Natural caves |
| Swamp | Swamp, Mangrove Swamp | 80%, 20% | Obvious mapping |
| Snake Pit | Jungle, Sparse Jungle, Bamboo Jungle | 60%, 20%, 20% | Tropical snakes |
| Spider's Nest | Dark Forest, Old Growth Spruce Taiga | 70%, 30% | Dark, webbed |
| Shoals | Beach, Stony Shore, Warm Ocean (coastal) | 50%, 30%, 20% | Coastal areas |
| Slime Pits | TBD | - | Acidic, polluted |
| Vaults | TBD | - | High-security storage |
| Crypt | TBD | - | Burial grounds |
| Tomb | Desert, Badlands | 70%, 30% | Ancient tombs |
| Depths | Deep Dark, Dripstone Caves | 60%, 40% | Deep underground feel |
| Abyss | N/A | - | Pocket dimension |
| Pandemonium | N/A | - | Pocket dimension |
| Hell - Gehenna | Badlands, Eroded Badlands | 60%, 40% | Fire/brimstone |
| Hell - Cocytus | Frozen Peaks, Ice Spikes | 60%, 40% | Frozen hell |
| Hell - Tartarus | Deep Dark | 100% | Death dimension |
| Hell - Dis | TBD | - | Iron city |

### Side Branches

| DCSS Branch | Biome(s) | Weight | Notes |
|-------------|----------|--------|-------|
| Sewers | TBD | - | Urban underground |
| Ossuary | Desert, Badlands | 60%, 40% | Early undead tomb |
| Bailey | TBD | - | Military fort |
| Ice Cave | Frozen Peaks, Ice Spikes, Snowy Plains | 50%, 30%, 20% | Obvious mapping |
| Volcano | Badlands, Eroded Badlands | 60%, 40% | Volcanic areas |
| Wizlab | TBD | - | Magical tower |
| Trove | TBD | - | Rare treasure |

---

## NEW: Wasteland-Specific Structures

### 1. Mall (Large Indoor Dungeon)

**Type**: Rare Portal Structure
**Size**: Large (1-3 floors)
**Overworld Structure**: Large building exterior with portal doors

#### Normal Mall
- **Inhabitants**: Orcs, Gnolls, or Humanoid types (1-2 species per mall)
- **Loot**: Consumer goods, food, electronics (wasteland items)
- **Difficulty**: Medium-Hard
- **Biome Preference**:
  - Plains (abandoned shopping districts)
  - Desert (sun-bleached malls)
  - Savanna (ruins in grasslands)

#### Cursed Mall
- **Inhabitants**: Low-level undead (zombies, skeletons)
- **Boss**: Mid-level undead, Vampire, or Mummy on top floor
- **Loot**: Cursed items, necromantic gear
- **Difficulty**: Hard
- **Biome Preference**:
  - Dark Forest (cursed grounds)
  - Swamp (decayed mall)
  - Badlands (death-touched)

**Implementation Notes**:
- Overworld structure = large concrete/brick building shell
- Doors on exterior are **portals** to instanced interior dungeon
- Interior has stores, atriums, escalators (broken), food courts
- Overgrown vegetation breaking through floor/walls
- Parking lot ruins around perimeter with overgrown roads

**Spawn Rate**: 1-2 per 2000 block radius (rare)

---

### 2. Grocery Store (Medium Dungeon)

**Type**: Standard Structure
**Size**: Medium (single floor)
**Overworld Structure**: Medium retail building

- **Inhabitants**:
  - Goblins (scavengers)
  - Rats (swarms)
  - Bandits (looters)
  - Occasional zombie (spoiled food theme)
- **Loot**:
  - Food items
  - Medical supplies
  - Basic weapons (pipes, knives)
  - Occasional ranged weapon (crossbow in sporting goods)
- **Difficulty**: Easy-Medium
- **Biome Preference**:
  - Any biome near housing
  - Plains, Forest, Taiga most common

**Layout**:
- Aisles with shelving (cover/obstacles)
- Checkout area
- Back storage room (boss encounter)
- Refrigeration section (potential ice hazards)

**Spawn Rate**: 1 per 500 block radius (common)

---

### 3. Housing (Small Dungeon)

**Type**: Common Structure
**Size**: Small (single floor or 2-floor house)

#### Variants:
1. **Suburban House**
   - 2 floors, basement optional
   - 2-4 rooms
   - Family home aesthetic

2. **Apartment Building**
   - 3-5 floors
   - Multiple units
   - Shared hallways

3. **Trailer/Mobile Home**
   - Single floor
   - Compact layout
   - Rural/wasteland aesthetic

**Inhabitants**:
- 1-3 creatures depending on size
- Goblins, bandits, rats, snakes
- Occasional ghost (haunted variant)
- Feral dogs/cats

**Loot**:
- Minor: clothing, basic supplies
- Occasional: hidden stash (under bed, closet)
- Rare: family heirloom (artifact potential)

**Difficulty**: Easy

**Biome Preference**:
- Houses: Plains, Forest, Meadow, Taiga
- Trailers: Desert, Savanna, Badlands
- Apartments: Plains (urban ruins)

**Spawn Rate**: 1-3 per 250 block radius (very common)

---

## Overgrown Aesthetics

### Road Decay Patterns

**Leading to Malls/Stores/Housing**:
- **Stage 1** (nearby roads): 30-50% intact, grass breaking through
- **Stage 2** (approach): 50-70% damaged, shrubs, small trees
- **Stage 3** (parking lots): 70-90% overgrown, full vegetation

### Vegetation by Biome

| Biome | Overgrowth Type |
|-------|-----------------|
| Plains | Tall grass, flowers, saplings |
| Forest | Roots, vines, tree growth through pavement |
| Taiga | Moss, pine saplings, snow cover |
| Desert | Cacti breaking through, sand drifts, tumbleweed |
| Jungle | Dense vines, massive root systems, leaf litter |
| Swamp | Water intrusion, lily pads, mangrove roots |
| Savanna | Dry grass, acacia saplings, dead bushes |
| Dark Forest | Mushrooms, dark oak growth, spooky atmosphere |

### Building Decay

- **Walls**: Cracks (cobblestone mixed with concrete)
- **Windows**: Broken, boarded, or shattered
- **Roofs**: Holes, collapsed sections, vegetation growing through
- **Floors**: Cracked, grass/roots breaking through
- **Furniture**: Damaged, looted, scattered debris

---

## Implementation Priority

### Phase 1 (This Session)
- [ ] Finalize biome mappings for main branches
- [ ] Design mall exterior structure
- [ ] Create grocery store layout template
- [ ] Design 3 house variants

### Phase 2
- [ ] Implement mall portal system
- [ ] Add overgrown road generation
- [ ] Create loot tables for new structures
- [ ] Add inhabitant spawn logic

### Phase 3
- [ ] Cursed mall variant
- [ ] Apartment building structure
- [ ] Parking lot ruins
- [ ] Advanced decay effects

---

## Notes

- All structures should feel **post-apocalyptic** (20-50 years abandoned)
- Biome-specific decay (desert sun bleaching, jungle overgrowth, etc.)
- Loot should reflect pre-war civilization (consumer goods, tech)
- Difficulty scales with distance from spawn
- Portal structures (malls) prevent griefing of rare dungeons
- Roads connect major structures (malls, stores) to create navigation paths

---

*Document created: December 4, 2025*
*Status: PLANNING - Awaiting biome mapping decisions*
