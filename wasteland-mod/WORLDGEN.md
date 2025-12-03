# Wasteland USA - World Generation System

## Overview

The Wasteland mod features a stylized USA-inspired overworld with intelligent structure placement based on geographic regions. DCSS dungeons spawn in biome-appropriate locations, and 7-11 stores serve as safe zones scattered across the wasteland.

## USA Region System

The world is divided into 11 geographic regions, each spanning specific coordinate ranges within the 10,000 x 10,000 block world (±5000 from spawn).

### Region Map

```
       NORTHWEST        GREAT     NORTHEAST
         (-4k-3k,       LAKES        (-2k2k,
          3k-5k)     (0.5k-2.5k,    2k-5k)
                       1k-3.5k)

    WEST COAST    ROCKIES    MIDWEST     ATLANTIC
    (-5k--3k,    (-3k--1k,  (-1k1.5k,     COAST
     1k-5k)       0-3k)     -1k-3k)     (1.5k-2.5k,
                                         -2k-4k)
 PACIFIC                SOUTHWEST
  COAST                (-5k--1k,
(-5k--4.5k,            -3k-1k)
  -2k-4k)
                   GULF COAST          SOUTHEAST
                   (-1k-1k,            (-2k-2k,
                    -5k--3k)            -5k-0)
```

### Region Characteristics

| Region | Biomes | Dungeon Types |
|--------|--------|---------------|
| **Northeast** | Forest, Taiga, Hills | Lair, Orc Mines, Elf Halls |
| **Southeast** | Swamp, Forest, Beach | Swamp, Crypt |
| **Midwest** | Plains, Sunflower Plains | Vaults, Any |
| **Southwest** | Desert, Badlands, Eroded Badlands | Snake Pit, Tomb |
| **Rockies** | Mountains, Gravelly Hills, Stony Peaks | Orc Mines |
| **West Coast** | Forest, Beach, Windswept Forest | Lair, Shoals |
| **Northwest** | Taiga, Pine Taiga, Windswept Forest | Lair, Spider, Elf Halls |
| **Gulf Coast** | Beach, Swamp, Mangrove Swamp | Shoals, Swamp |
| **Great Lakes** | River, Beach, Forest | Any |
| **Atlantic Coast** | Beach, Ocean, Stony Shore | Shoals |
| **Pacific Coast** | Beach, Ocean, Stony Shore | Shoals |

## Structure Placement

### Dungeon Entrances

- **Spacing**: Every 32 chunks (512 blocks)
- **Spawn Chance**: 20% per valid grid position
- **Placement**: Grid-aligned positions (multiples of 32 chunks)
- **Biome-Aware**: Dungeon type selected based on region

#### Dungeon Type Distribution

Dungeons spawn based on region compatibility:

- **Shoals** → Coastal regions only (Atlantic, Pacific, Gulf coasts)
- **Swamp** → Southeast, Gulf Coast
- **Lair** → Forest regions (Northeast, Northwest, West Coast)
- **Snake Pit** → Southwest desert
- **Spider** → Dense forests (Northwest)
- **Orc Mines** → Mountains (Rockies, Northeast)
- **Elf Halls** → Ancient forests
- **Vaults** → Any location (ruined cities)
- **Crypt** → Scattered locations
- **Tomb** → Southwest desert
- **Depths** → Rare, any location
- **Realm of Zot** → Very rare, any location

#### Rarity Weights

- Common (100): Lair, Orc Mines
- Fairly Common (80): Swamp, Shoals, Snake Pit, Spider
- Uncommon (50): Elf Halls, Vaults, Crypt
- Rare (30): Tomb
- Very Rare (15): Depths
- Extremely Rare (5): Realm of Zot

### 7-11 Stores

- **Spacing**: Every 24 chunks (384 blocks)
- **Spawn Chance**: 15% per valid grid position
- **Placement**: Offset grid (chunks + 12) to avoid dungeon conflicts
- **Features**:
  - Brick walls with quartz roof
  - Glowstone lighting
  - Chests and barrels for loot
  - Safe zone (no mob spawns - TODO)
  - Merchants (TODO)
  - Quest NPCs (TODO)

### Wasteland Decorations

- **Chance**: 3% per chunk
- **Types**:
  - Rubble piles (cracked stone bricks)
  - Dead bushes
  - Rusted barrels
  - Craters (coarse dirt depressions)
  - Abandoned campfires

## Technical Implementation

### Key Classes

**USARegion.java**
- Enum defining 11 geographic regions
- Coordinate bounds and region detection
- Biome preferences for each region
- Helper methods: `isCoastal()`, `isForest()`, `isDesert()`, etc.

**DungeonType.java**
- Enum for all DCSS dungeon types
- Biome preference flags
- Rarity weights
- `getRandomForRegion()` - selects appropriate dungeon for location

**WastelandWorldGen.java**
- Event handler for `ChunkEvent.Load`
- Grid-based structure spawning
- Region-aware dungeon placement
- 7-11 store generation
- Wasteland decoration placement

**USABiomeSource.java** (Future)
- Custom biome source for complete terrain control
- Currently uses vanilla biomes with smart structure placement

## Phase 1 Status (Current)

What's implemented:
- ✅ USA region system with 11 geographic areas
- ✅ Biome-aware dungeon type selection
- ✅ Grid-based structure placement
- ✅ 7-11 store structures
- ✅ Wasteland decorations

What uses vanilla systems:
- Terrain generation (vanilla biomes)
- Biome placement (vanilla)

## Future Enhancements (Phase 2+)

1. **Custom Biomes**
   - Replace vanilla biomes with wasteland variants
   - Custom terrain generation for each region
   - Smooth biome transitions

2. **7-11 Store Features**
   - Merchant NPCs
   - Quest givers
   - Safe zone implementation (no mob spawns)
   - Proper loot tables

3. **Dungeon Entrance Variety**
   - Unique entrance structures for each dungeon type
   - Visual theming matching dungeon content

4. **Dynamic World Events**
   - Wandering NPC caravans
   - Abandoned vehicle wrecks
   - Faction outposts

## Testing

To explore the USA wasteland:

1. Create a new world
2. Check F3 debug screen for coordinates
3. Navigate to different regions to see biome-appropriate dungeons:
   - Go west (negative X) for coastal/desert content
   - Go east (positive X) for forests/plains
   - Check logs for structure generation messages

Example coordinates:
- Southwest Desert: X=-3000, Z=0
- Northeast Forests: X=0, Z=3500
- Pacific Coast: X=-4800, Z=0
- Gulf Coast: X=0, Z=-4000
