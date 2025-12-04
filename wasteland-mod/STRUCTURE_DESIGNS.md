# Wasteland Structure Designs

Comprehensive blueprints for all overworld structure types.

---

## 1. Mall Structure (Large Portal Dungeon)

### Overworld Exterior

**Dimensions**: 60x40x20 blocks (WxDxH)
**Level Range**: 8-20

**Structure**:
```
Floor Plan (Top View):
┌────────────────────────────────────────────────────────────┐
│  PARKING LOT (overgrown, cracked concrete, grass)          │
│    ▢ ▢ ▢    ▢ ▢ ▢    ▢ ▢ ▢    ▢ ▢ ▢                       │
│    ▢ ▢ ▢    ▢ ▢ ▢    ▢ ▢ ▢    ▢ ▢ ▢                       │
│                                                              │
│  ┌──────────────────────────────────────────────────────┐  │
│  │ MAIN BUILDING                                         │  │
│  │                                                        │  │
│  │  ╔═ENTRANCE═╗         ╔═ENTRANCE═╗                   │  │
│  │  ║  PORTAL  ║         ║  PORTAL  ║                   │  │
│  │  ╚══════════╝         ╚══════════╝                   │  │
│  │                                                        │  │
│  │  [CRACKED WINDOWS]    [BROKEN SIGN]                  │  │
│  └──────────────────────────────────────────────────────┘  │
│                                                              │
│  OVERGROWN ROADS (leading to exits)                         │
└────────────────────────────────────────────────────────────┘
```

**Materials**:
- Walls: Stone Bricks (cracked 30%), Concrete (gray), Glass (broken 40%)
- Floor: Concrete, Polished Andesite
- Roof: Stone Slabs, some holes with vines
- Parking: Concrete (50%), Grass/Dirt (40%), Gravel (10%)
- Decoration: Dead bushes, vines, scattered debris

**Portal Doors**:
- Location: Main entrances (2-4 doors)
- Type: Nether portal blocks (purple)
- Activation: Right-click to enter dungeon
- Sign: Faded text "WESTFIELD MALL" or similar

---

### Interior Dungeon (Instanced)

**Normal Mall**:
- **Floors**: 1-3 (random)
- **Layout**: Central atrium, branching store hallways
- **Stores**: 8-15 per floor (clothing, electronics, food court)
- **Enemies**: Orcs OR Gnolls OR Bandits (1-2 species)
- **Loot**: Consumer goods, weapons in sporting goods store
- **Boss**: Top floor - Orc Warlord or Gnoll Chieftain

**Cursed Mall**:
- **Floors**: 1-3
- **Layout**: Same but darker, corrupted
- **Atmosphere**: Fog, dark lighting, purple particles
- **Enemies**: Zombies, Skeletons, Wights
- **Boss**: Top floor - Vampire or Mummy
- **Loot**: Cursed items, necromantic gear
- **Decoration**: Blood stains, broken mannequins, dimensional rifts

---

## 2. Grocery Store (Medium Structure)

**Dimensions**: 30x20x12 blocks
**Level Range**: 3-10

**Layout**:
```
Side View:
     ┌─────────────────────────────┐
     │   [SIGN: "SAFEWAY"]         │ ← Broken sign
     │─────────────────────────────│
     │  ╔═════╗         ╔═════╗   │ ← Entrance
     ├──╫     ╫─────────╫     ╫───┤
     │  ║DOOR ║         ║DOOR ║   │

Top View:
┌─────────────────────────────────┐
│ Produce  │ Aisles  │  Frozen   │
│   (L)    │ ══════  │   Foods   │
├──────────┼─────────┼───────────┤
│ Checkout │ Aisles  │ Storage   │
│   Area   │ ══════  │  (BOSS)   │
└─────────────────────────────────┘
```

**Features**:
- **Aisles**: 4-6 rows of shelving (cover/obstacles)
- **Checkout**: Cash registers, shopping carts
- **Storage Room**: Back area with boss encounter
- **Refrigeration**: Ice effects, potential hazards
- **Loot**: Food items, medical supplies, occasional weapon

**Enemies**:
- Goblins (scavengers) - 3-5
- Rat swarms - 2-3
- Bandits (looters) - 1-2
- Boss: Bandit Leader or Giant Rat (storage room)

**Decay**:
- Shelves: 60% knocked over or empty
- Floor: Cracked tiles, scattered products
- Ceiling: Holes, exposed wiring
- Windows: All broken
- Vegetation: Vines through roof holes

---

## 3. Housing Structures (Small Dungeons)

### A. Suburban House

**Dimensions**: 12x10x8 blocks
**Level Range**: 1-5

```
Floor 1:
┌───────────┐
│ Living Rm │
│     ▢     │ ← Furniture
├─────┬─────┤
│ Kit │ Din │
└─────┴─────┘

Floor 2:
┌─────┬─────┐
│ Bed │ Bed │
├─────┴─────┤
│  Bathroom │
└───────────┘

Basement (optional):
┌───────────┐
│  Storage  │
│   (LOOT)  │
└───────────┘
```

**Features**:
- 2 floors + optional basement
- 4-6 rooms total
- Furniture: Beds, tables, bookshelves
- Loot: Closets, under beds, basement stash

**Enemies**: 1-3 creatures
- Goblins
- Bandits
- Rats
- Ghost (haunted variant - 10% chance)

---

### B. Apartment Building

**Dimensions**: 20x20x15 blocks (3-5 floors)
**Level Range**: 3-8

```
Each Floor:
┌─────┬─────┬─────┬─────┐
│ APT │ APT │ APT │ APT │
│  1  │  2  │  3  │  4  │
├─────┴──┬──┴─────┴─────┤
│        │               │
│  HALL  │  STAIRWELL    │
│        │               │
└────────┴───────────────┘
```

**Features**:
- 3-5 floors
- 4 units per floor
- Shared hallway
- Stairwell connects floors
- Roof access

**Enemies**: 5-10 creatures
- Spread across units
- Boss on top floor or roof

---

### C. Trailer/Mobile Home

**Dimensions**: 8x15x6 blocks
**Level Range**: 1-4

```
┌─────────────┐
│ LR  │ Kit   │
├─────┼───────┤
│ Bed │ Bath  │
└─────────────┘
```

**Features**:
- Single floor, compact
- 3-4 small rooms
- Rusty exterior
- Sparse furniture

**Enemies**: 1-2 creatures
- Goblins
- Snakes
- Bandits

---

## 4. Cursed Church (Medium-Large)

**Dimensions**: 25x40x20 blocks
**Level Range**: 12-22

```
Top View:
      ┌─┐
      │↑│ ← Steeple
      └┬┘
    ┌──┴──┐
    │     │
┌───┴─────┴───┐
│   ALTAR     │
│      ╔╗     │ ← Dimensional Rift
│      ╚╝     │
├─────────────┤
│   PEWS      │
│   ═══       │
│   ═══       │
│   ═══       │
├─────────────┤
│  ENTRANCE   │
│   ╔═══╗     │
└───╨───╨─────┘
```

**Features**:
- Gothic architecture
- Broken stained glass
- Dimensional rift at altar
- Crypts below (optional dungeon)
- Pews (cover/obstacles)

**Atmosphere**:
- Fog effect
- Dark lighting
- Purple/red particles
- Ominous ambient sounds

**Enemies**:
- Undead (skeletons, zombies)
- Cultists
- Demons (if high level)
- Boss: Demon or Lich at altar

**Loot**:
- Necromantic items
- Cursed artifacts
- Religious relics
- Potential Hell portal

---

## 5. Military Base (Large Complex)

**Dimensions**: 80x80x15 blocks
**Level Range**: 8-24

```
Top View:
┌────────────────────────────────┐
│ ┌──────┐  FENCE PERIMETER     │
│ │TOWER │        ═══           │
│ └──────┘                       │
│                                 │
│  ┌─────────┐  ┌──────────┐    │
│  │ BARRACKS│  │ ARMORY   │    │
│  │         │  │ (WEAPONS)│    │
│  └─────────┘  └──────────┘    │
│                                 │
│  ┌──────────┐  ┌──────────┐   │
│  │ VEHICLES │  │ COMMAND  │   │
│  │  DEPOT   │  │  CENTER  │   │
│  └──────────┘  └──────────┘   │
│                                 │
│       ┌──────────────┐         │
│       │ UNDERGROUND  │         │
│       │   BUNKER     │         │
│       └──────────────┘         │
└────────────────────────────────┘
```

**Features**:
- Fence perimeter with guard towers
- Multiple buildings:
  - Barracks (soldiers)
  - Armory (weapons/loot)
  - Vehicle depot (cover)
  - Command center (boss)
  - Underground bunker (portal access)

**Enemies** (Orc-themed):
- Orc Private (warriors)
- Orc Sergeant (knights)
- Orc Chaplain (priests)
- Orc Major (warlords)
- Boss: Orc Colonel

**Special**:
- Lowest level base: Portal to Elven Halls (guarded by boss)
- Highest level base (20+): Portal to Vaults (heavily guarded)

**Loot**:
- Military weapons
- Armor
- Ammunition
- Tactical gear

---

## 6. Ritual Site (Small-Medium)

**Dimensions**: 15x15x5 blocks
**Level Range**: 15-23

```
Top View:
    ╔═══╗
    ║ ◉ ║ ← Blood Altar
    ╚═══╝
      │
    ──┼──
      │
   ▄▀ │ ▀▄
  ▐   │   ▌ ← Stone Circle
   ▀▄ │ ▄▀
      │
    ──┼──
      │
    ╔═══╗
    ║ 🜏 ║ ← Summoning Circle
    ╚═══╝
```

**Features**:
- Stone circle formation
- Blood altar (center)
- Summoning circles
- Dimensional tear
- Scattered bones/skulls

**Atmosphere**:
- Heavy corruption
- Reality distortion
- Purple particles
- Lightning effects

**Enemies**:
- Cultists
- Summoned demons
- Abominations
- Boss: Demon Lord or Eldritch Horror

**Purpose**:
- Portal creation site
- Can lead to Hell branches
- Occasionally Pandemonium rift

---

## Implementation Priority

### Phase 1 (Immediate):
1. Housing (easiest, most common)
2. Grocery Store (medium complexity)

### Phase 2 (Next session):
3. Mall Exterior (complex but cool)
4. Cursed Church (atmospheric)

### Phase 3 (Later):
5. Military Base (largest, most complex)
6. Ritual Sites (special encounter)

---

## Structure Placement Rules

**Housing**:
- Biomes: Plains, Forest, Meadow, Taiga
- Density: 1-3 per 250 blocks
- Clustered in "neighborhoods"

**Grocery**:
- Biomes: Near housing clusters
- Density: 1 per 500 blocks
- Strip mall formations

**Mall**:
- Biomes: Plains with large clearings
- Density: 1 per 2000 blocks (rare)
- Requires flat area

**Cursed Church**:
- Biomes: Dark Forest (70%), Plains (30%)
- Density: 1 per 1500 blocks
- High-level areas only

**Military Base**:
- Biomes: Any
- Density: 1 per 3000 blocks (rare)
- Level 8+ areas

**Ritual Site**:
- Biomes: Random
- Density: 1 per 2500 blocks
- Level 15+ areas only
- Can appear in corrupted zones

---

## Common Structure Elements

### Overgrown Aesthetics
- Vines on walls: 40% coverage
- Grass through floor: 30% of tiles
- Dead bushes: Scattered
- Trees: Breaking through roofs (10% chance)

### Decay Patterns
- Cracked blocks: Mix with smooth variants
- Broken windows: Air blocks instead of glass
- Collapsed sections: Missing chunks (5-10%)
- Debris: Scattered gravel, cobblestone

### Loot Containers
- Chests: Hidden in logical places
  - Housing: Closets, basements
  - Grocery: Storage room, locked office
  - Mall: Store backrooms, manager offices
  - Church: Crypts, altar
  - Military: Armory, command center

### Lighting
- No torches (post-apocalyptic)
- Natural light from holes
- Glowstone in cursed areas (corruption)
- Sea lanterns in high-tech areas (rare)

---

*Document created: December 4, 2025*
*Status: DESIGN COMPLETE - Ready for incremental implementation*
