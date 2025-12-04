# Organic World Design - Dimensional Invasion Wasteland

**Core Philosophy**: No hand-holding. The world is dangerous, unpredictable, and requires player awareness. Higher-level areas exist organically throughout the world - players must learn to recognize danger and retreat when necessary.

---

## Lore: The Dimensional Collapse

### What Happened
The world didn't end from nuclear war alone. **Something broke the barriers between dimensions.**

- **The Event**: ~50 years ago, dimensional rifts opened
- **The Invasion**: Creatures from Hell, Pandemonium, the Abyss poured through
- **The Collapse**: Human civilization fell within months
- **The Aftermath**: Survivors scattered, ruins overtaken by dimensional horrors
- **The Present**: Portals still exist, stable but dangerous

### Dimensional Influences

**Hell Portals** (Gehenna, Cocytus, Tartarus, Dis):
- Found in cursed churches, ritual sites, military bases
- Fire, ice, death, and iron themes
- Reality distortions around them

**Pandemonium Rifts**:
- Chaotic portals in random locations
- Demonic lord territories
- Unpredictable and dangerous

**Abyss Tears**:
- Shifting, unstable dimensional wounds
- Can appear and disappear
- Extreme danger

**Nuclear Elements** (still present):
- Radiation zones from pre-collapse weapons
- Mutated creatures
- Abandoned military installations
- Mix of human warfare + dimensional invasion

---

## Organic Level Distribution

### No More Rings

**Old System** (REMOVED):
- Level 1-5: 0-500 blocks
- Level 6-10: 500-1000 blocks
- etc.

**New System** (ORGANIC):
- Difficulty based on **noise map + biome + structure type**
- Low-level "safe" zones near spawn (not guaranteed safe)
- Difficulty clusters scattered naturally
- High-level areas can be close to spawn (player must avoid)
- Level range: 1-25+ (open-ended)

### Noise-Based Difficulty

```
Difficulty = BASE_BIOME_LEVEL + NOISE_FACTOR + STRUCTURE_BONUS + DISTANCE_MODIFIER
```

**Components**:
1. **Base Biome Level**:
   - Plains/Forest: 1-3
   - Taiga/Savanna: 3-6
   - Desert/Swamp: 5-10
   - Mountains: 8-15
   - Dark Forest: 10-18
   - Deep Dark: 15-25

2. **Noise Factor** (-5 to +10):
   - Perlin noise creates difficulty "hotspots"
   - Clusters of high-level areas
   - Creates danger zones

3. **Structure Bonus**:
   - Housing: +0-2
   - Grocery: +2-5
   - Mall: +5-10
   - Military Base: +8-15
   - Cursed Church: +10-18
   - Portal Dungeon: +15-25

4. **Distance Modifier** (gentle curve):
   - 0-1000 blocks: -2 to +2
   - 1000-3000: -1 to +5
   - 3000+: 0 to +10
   - NOT deterministic (creates variety)

### Visual Danger Cues

Players should be able to recognize danger **before entering**:

**Environmental Cues**:
- **Corruption**: Ground darkening, withered plants
- **Dimensional Rifts**: Purple/red particles, distortion effects
- **High-Level Creatures**: Visible from distance, intimidating models
- **Structure Decay**: More severe damage = higher level
- **Lighting**: Darker areas = more dangerous
- **Sound**: Distant monster sounds, ambient dread

**Monster Visibility**:
- Monsters are larger/more elaborate at higher levels
- Color coding: Grays → Reds → Purples → Blacks
- Unique models for high-threat creatures

---

## Aggro Range Balance

### The Rule: **Player sees threat before threat aggros**

**Player Vision**:
- Render distance: 12-16 chunks (192-256 blocks)
- Fog starts: ~128 blocks
- Clear vision: ~64-96 blocks

**Monster Aggro Ranges** (by level):
- **Level 1-5**: 16 blocks (close range)
- **Level 6-10**: 24 blocks (medium range)
- **Level 11-15**: 32 blocks (long range)
- **Level 16-20**: 40 blocks (very long range)
- **Level 21+**: 48 blocks (extreme range)
- **MAX**: Never exceed 48 blocks

**Special Cases**:
- **Ranged enemies**: Aggro = weapon range + 8 blocks
- **Flying enemies**: +8 blocks to range
- **Boss creatures**: Double normal range (but announced visually)
- **Sentries/Guards**: Stand still, only aggro within 16 blocks

**Implementation**:
```java
// Custom entity AI goal
public class WastelandTargetGoal extends NearestAttackableTargetGoal {
    private final int aggroRange;

    @Override
    protected double getFollowDistance() {
        int level = EntityLevelManager.getLevel(mob);
        return Math.min(48.0, 16.0 + (level * 1.5));
    }
}
```

---

## Structure Placement Rules

### Housing & Malls (Civilian Zones)

**Biome**: Plains, Forest, Meadow, Taiga (grassy areas)

**Housing**:
- Level: 1-8
- Density: 1-3 per 250 block radius
- Clustered in "neighborhoods"
- Connected by overgrown roads

**Grocery Stores**:
- Level: 3-10
- Density: 1 per 500 blocks
- Near housing clusters
- Strip mall formations (2-4 stores together)

**Malls** (Normal):
- Level: 8-15
- Density: 1 per 2000 blocks
- In plains with large parking lots
- Overgrown highways leading to them

**Malls** (Cursed):
- Level: 12-20
- Density: 1 per 3000 blocks
- Dark Forest, Swamp, Badlands
- Dimensional corruption visible

---

## Portal Placement System

### Portal Discovery Progression

**Early Game** (Level 1-8):
- Housing → Grocery → Regular Mall
- Learn to fight, find gear
- Encounter first Orc patrols

**Mid Game** (Level 8-15):
- Lair Portals (Forest/Mountains)
- Orc Bases (Military installations)
- Temple discoveries
- Start collecting minor artifacts

**Late Game** (Level 15-20):
- Snake Pit (High-level Swamp/Jungle)
- Shoals (Coastal/Ice biomes)
- Elven Halls (multiple paths)
- Vaults (Heavily guarded military)

**End Game** (Level 20-25):
- Hell portals (Cursed churches, ritual sites)
- Pandemonium rifts
- Abyss tears
- Realm of Zot (ultimate goal)

---

## Portal Rules by Branch

### Lair of Beasts
- **Required Level**: 8+
- **Biomes**: Forest (60%), Mountains (30%), Taiga (10%)
- **Overworld Structure**:
  - Cave entrance with animal tracks
  - Natural rock formation
  - Overgrown, wild vegetation
- **Rune**: No (branch portal, not endgame)

---

### Snake Pit
- **Required Level**: 13+
- **Biomes**: Jungle (50%), Swamp (30%), Mangrove Swamp (20%)
- **Overworld Structure**:
  - Ruined temple with snake motifs
  - Vine-covered stone
  - Water features
- **Rune**: 20% chance if level 18+ portal
- **Guardians**: Naga champions if rune present

---

### Orcish Mines → Military Bases
- **Required Level**: 8+
- **Biomes**: Any (military bases anywhere)
- **Overworld Structure**:
  - Military compound (fences, guard towers)
  - Vehicle depot ruins
  - Weapons storage
- **Orc Ranks** (replacing standard):
  - Orc Warrior → **Private**
  - Orc Priest → **Chaplain**
  - Orc Knight → **Sergeant**
  - Orc Warlord → **Major**
  - Orc High Priest → **Colonel**
- **Rune**: No (branch portal)
- **Special**: Lowest level Orc base may have Elven Halls portal guarded by boss

---

### Shoals
- **Required Level**: 16+
- **Biomes**: Frozen Ocean (40%), Stony Shore (30%), Ice Spikes (20%), Swamp (10%)
- **Overworld Structure**:
  - Frozen shipwreck
  - Ice cave entrance
  - Ruined coastal fortress
- **Rune**: 20% chance if level 20+ portal
- **Guardians**: Merfolk champions, sea dragons

---

### Elven Halls (Multiple Paths)

**Path 1**: Through Orc Base
- Found in **lowest-level Orc military base**
- Guarded by Orc Major (boss)
- Requires clearing entire base

**Path 2**: Mountain Peaks
- **Required Level**: 18+
- **Biomes**: Jagged Peaks, Frozen Peaks
- **Structure**: Ancient elven outpost ruins

**Path 3**: Cursed Church
- **Required Level**: 18+
- **Biomes**: Dark Forest, Plains (corrupted)
- **Structure**: Cathedral with dimensional corruption

**Path 4**: Deep Forest
- **Required Level**: 18+
- **Biomes**: Old Growth Forest, Dark Forest
- **Structure**: Elven tree-fortress

**Rune**: 20% chance if level 22+ portal
**Guardians**: Deep elf demonologists, liches

---

### Vaults

**Location**: HIGH level military bases only
- **Required Level**: 20+
- **Biomes**: Any
- **Structure**: Maximum security military compound
- **Access**: Guarded by base commander (boss)
- **Interior**: Treasure vaults, security systems, heavy guards

**Rune**: 20% chance if level 24+ portal
**Guardians**: Vault guards, war gargoyles, ancient liches

---

### Hell Branches (Gehenna, Cocytus, Tartarus, Dis)

**Gehenna (Fire Hell)**:
- **Level**: 22+
- **Biomes**: Badlands, Eroded Badlands
- **Structure**: Burning church, lava fissure
- **Theme**: Fire demons, hellfire

**Cocytus (Ice Hell)**:
- **Level**: 22+
- **Biomes**: Ice Spikes, Frozen Peaks
- **Structure**: Frozen cathedral
- **Theme**: Ice devils, frozen torment

**Tartarus (Death Hell)**:
- **Level**: 22+
- **Biomes**: Deep Dark, Dark Forest
- **Structure**: Necropolis entrance
- **Theme**: Undead legions, reapers

**Dis (Iron Hell)**:
- **Level**: 22+
- **Biomes**: Badlands, Windswept Hills
- **Structure**: Iron fortress entrance
- **Theme**: Iron demons, tormentors

**Rune**: 20% chance if level 25 portal (only max level)
**Guardians**: Hell lords, unique demons

---

### Pandemonium Rifts

**Chaotic Portals**:
- **Level**: 23+
- **Biomes**: Random (dimensional chaos)
- **Structure**: Unstable rift in reality
- **Appearance**: Purple/red swirling vortex
- **Danger**: Extremely high
- **Rune**: Demon lords hold runes (rare, multiple possible)

---

### Abyss Tears

**Unstable Portals**:
- **Level**: 24+
- **Biomes**: Random
- **Behavior**: Can appear/disappear
- **Structure**: Reality tear, eldritch horror
- **Warning**: Visual distortion, particle effects
- **Danger**: Extreme
- **Rune**: Abyssal rune (can be found in depths)

---

### Realm of Zot

**Ultimate Goal**:
- **Level**: 25 (maximum)
- **Location**: ONE per world
- **Biomes**: Special (creates its own)
- **Structure**: Massive fortress/temple
- **Access**: Requires 3+ runes to enter
- **Contents**: Orb of Zot (victory condition)

---

## Cursed Structures (Dimensional Corruption)

### Cursed Churches
- **Level**: 12-22
- **Biomes**: Dark Forest (50%), Plains (30%), Any (20%)
- **Features**:
  - Gothic architecture
  - Broken stained glass
  - Dimensional rift inside
  - Undead/demonic inhabitants
  - Hell portal potential (high level)
- **Loot**: Necromantic items, cursed artifacts
- **Atmosphere**: Fog, darkness, eerie sounds

### Ritual Sites
- **Level**: 15-23
- **Biomes**: Any
- **Features**:
  - Stone circles
  - Summoning circles
  - Blood altars
  - Dimensional tears
- **Inhabitants**: Cultists, demons, abominations
- **Purpose**: Portal creation sites

### Dimensional Rifts (Overworld)
- **Level**: Varies (18-25)
- **Appearance**:
  - Purple/red particle effects
  - Reality distortion
  - Floating debris
  - Corrupted ground
- **Danger**: Spawns high-level demons periodically
- **Avoidance**: Clear visual warning from distance

---

## Rune of Zot System

### Rune Generation Rules

**Max Level Dungeons Only**:
- Portal must be at **maximum level for its type**
- Examples:
  - Snake Pit 18+ → 20% rune chance
  - Vaults 24+ → 20% rune chance
  - Hell branches 25 → 20% rune chance

**Rune Protection**:
- Always in the deepest floor
- Guarded by unique boss
- Additional elite guards (3-5)
- Traps and hazards
- High-value loot alongside rune

**Rune Count**:
- Typical world: 5-8 runes possible
- Required for Zot: 3 runes minimum
- More runes = easier Zot access areas

**Rune Types** (naming TBD):
- Serpentine Rune (Snake)
- Abyssal Rune (Abyss)
- Decaying Rune (Tomb)
- Barnacled Rune (Shoals)
- Silver Rune (Vaults)
- Fiery Rune (Gehenna)
- Icy Rune (Cocytus)
- Dark Rune (Tartarus)
- Iron Rune (Dis)
- Demonic Runes (Pandemonium - multiple)
- Gossamer Rune (Spider)
- Slimy Rune (Slime Pits)
- Golden Rune (Realm of Zot - victory item)

---

## Implementation Checklist

### Phase 1: Core Systems
- [ ] Remove ring-based level distribution
- [ ] Implement noise-based difficulty clusters
- [ ] Add organic level calculation (biome + noise + structure + distance)
- [ ] Create EntityLevelManager for mob levels
- [ ] Implement custom aggro range system
- [ ] Add visual danger cues (corruption, particles, lighting)

### Phase 2: Structure Generation
- [ ] Cursed church generator
- [ ] Ritual site generator
- [ ] Dimensional rift spawner
- [ ] Update mall/grocery/housing placement (organic rules)
- [ ] Military base structure
- [ ] Portal entrance structures (by type)

### Phase 3: Portal System
- [ ] Portal placement by level + biome rules
- [ ] Multi-path portal logic (Elven Halls)
- [ ] Boss-guarded portal system (Orc → Elf, Base → Vaults)
- [ ] Unstable portal behavior (Abyss)
- [ ] Rune generation logic (20% at max level)

### Phase 4: Lore Integration
- [ ] Dimensional corruption visual effects
- [ ] Hell/Pan/Abyss theming
- [ ] Orc rank name replacements
- [ ] Environmental storytelling (signs, notes)
- [ ] Ambient sound system (danger warnings)

### Phase 5: Balance & Polish
- [ ] Difficulty curve testing
- [ ] Aggro range tuning
- [ ] Rune distribution balance
- [ ] Boss encounter tuning
- [ ] Visual polish (corruption, rifts)

---

## World Generation Flow

```
1. Generate base terrain (Minecraft biomes)
2. Create noise map for difficulty clusters
3. Place major structures:
   - Spawn point (guaranteed low-level cluster)
   - Realm of Zot (hidden, far away)
   - Hell portals (scattered, high-level)
   - Cursed churches (dimensional corruption)

4. Place mid-tier structures:
   - Military bases (Orc/Vaults)
   - Portal dungeons (Lair, Snake, Shoals, Elf)
   - Dimensional rifts

5. Place common structures:
   - Housing (neighborhoods)
   - Grocery stores (strip malls)
   - Malls (rare)

6. Generate roads:
   - Connect major structures
   - Overgrown, degraded
   - Level-appropriate areas

7. Populate dungeons:
   - Mob levels = structure level ± 2
   - Loot tables by level
   - Generate runes (20% at max)

8. Add corruption/ambiance:
   - Dimensional effects near portals
   - Decay near high-level areas
   - Sound zones
```

---

## Gameplay Experience

**Early Game** (Level 1-8):
- Spawn in relatively safe area
- Explore housing, scavenge
- Encounter low-level creatures
- Learn combat, find basic gear
- **Risk**: Wandering into wrong area = death
- **Solution**: Recognize danger, retreat, teleport

**Mid Game** (Level 8-15):
- Clear grocery stores, small malls
- Find first portals (Lair, Orc)
- Begin portal exploration
- Collect artifacts
- **Challenge**: Finding safe paths through world

**Late Game** (Level 15-22):
- Clear major dungeons (Snake, Shoals, Elf)
- Raid military bases
- Face dimensional threats
- Collect first runes
- **Goal**: Build power for endgame

**End Game** (Level 22-25):
- Assault Hell branches
- Survive Pandemonium
- Collect 3+ runes
- Find Realm of Zot
- Retrieve Orb of Zot
- **Victory**: Escape with the Orb

---

*Document created: December 4, 2025*
*Status: DESIGN COMPLETE - Ready for implementation*
*Next: Begin Phase 1 implementation*
