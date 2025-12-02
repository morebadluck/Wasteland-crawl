# Wasteland Crawl - Procedural Generation System

## Overview

Wasteland Crawl uses a sophisticated procedural generation system that creates unique dungeons for every character while maintaining proper difficulty scaling. Every playthrough is different, but balanced.

## How It Works

### 1. Depth-Based Difficulty Scaling

The game uses **depth tags** to ensure monsters and vaults are appropriate for your character's power level:

```
DEPTH: D:1-5   → Early game (weak enemies)
DEPTH: D:6-12  → Mid game (moderate enemies)
DEPTH: D:13-15 → Late game (strong enemies)
```

### 2. Procedural Vault Selection

Each level randomly selects from available vaults appropriate for that depth:

**Levels 1-5 (Early Ruins)**
- Suburban houses
- Convenience stores
- Gas stations
- Small shelters
- Playgrounds
- Overturned buses

**Levels 6-12 (Dangerous Zones)**
- Military checkpoints
- Hospitals
- Police stations
- Office buildings
- Warehouses
- Fortified compounds

**Levels 13-15 (High Risk Areas)**
- Military bases
- Corporate headquarters
- Underground bunkers
- Research facilities
- Nuclear reactors
- Prison complexes

### 3. Monster Scaling

Monsters are carefully balanced by depth:

#### Early Game (D:1-5)
- **Tier 1**: Giant cockroaches, rats, bats (HD 1)
- **Tier 2**: Goblins, kobolds, jackals (HD 1-2)
- **Tier 3**: Hobgoblins, gnolls, orcs (HD 3-5)

These enemies can be handled by new characters with basic equipment.

#### Mid Game (D:6-12)
- **Tier 1**: Orc warriors, gnoll sergeants, ogres (HD 5-7)
- **Tier 2**: Wargs, trolls, centaurs (HD 6-9)
- **Tier 3**: Two-headed ogres, stone giants (HD 8-12)

Characters need good weapons, armor, and tactics.

#### Late Game (D:13-15)
- **Tier 1**: Stone giants, fire giants, dragons (HD 12-15)
- **Tier 2**: Titans, juggernauts, elder dragons (HD 15-18)
- **Tier 3**: Ancient liches, shadow dragons (HD 18+)

Only well-equipped, experienced characters should venture here.

### 4. Weight System

Vaults have **WEIGHT** values that control spawn probability:

```
WEIGHT: 8  → Very common (suburban houses)
WEIGHT: 5  → Common (gas stations, checkpoints)
WEIGHT: 3  → Uncommon (military bases, hospitals)
WEIGHT: 2  → Rare (nuclear reactors, special locations)
```

This ensures variety while keeping iconic locations special.

### 5. Monster Placement Within Vaults

Monsters within vaults use numbered slots (1-5) that scale with the vault:

```lua
-- Early game function
e.mons("rat / bat / giant cockroach")  -- Slot 1: Very weak
e.mons("goblin / kobold")              -- Slot 2: Weak
e.mons("hobgoblin / gnoll")            -- Slot 3: Moderate
e.mons("orc / ogre")                   -- Slot 4: Dangerous

-- Late game function
e.mons("stone giant / fire giant")     -- Slot 1: Strong
e.mons("dragon / ice dragon")          -- Slot 2: Very strong
e.mons("titan / ancient lich")         -- Slot 3: Deadly
```

## Procedural Layouts

### Base Layout Types

The game uses various layout algorithms for level structure:

1. **Rooms** - Classic connected rooms
2. **Caves** - Natural cave systems
3. **Corridors** - Maze-like passages
4. **City** - Grid-like urban layouts
5. **Loops** - Circular connected paths

### Overlay System

Vaults are placed ON TOP of the base layout:

```
1. Generate base layout (caves, rooms, etc.)
2. Place special vaults (military base, hospital)
3. Add decorative elements (rubble, debris)
4. Populate with monsters (appropriate to depth)
5. Place items and loot (scaled to depth)
6. Add stairs and exits
```

## Loot Scaling

Items also scale with depth:

### Early Levels (D:1-5)
- Basic weapons (+0 to +2)
- Light armor (leather, ring mail)
- Healing potions, bread rations
- Basic scrolls (identify, teleport)

### Mid Levels (D:6-12)
- Good weapons (+3 to +5)
- Medium armor (chain mail, scale mail)
- Enhanced potions (might, brilliance)
- Useful scrolls (blinking, fog)
- Occasional wands

### Late Levels (D:13-15)
- Excellent weapons (+6 to +9, artifacts)
- Heavy armor (plate mail, enchanted)
- Rare potions (experience, resistance)
- Powerful scrolls (acquirement)
- Good wands and jewellery

## Randomization Elements

### Environmental Hazards

Certain vaults include randomized environmental features:

```
w = shallow_water (toxic pools, sewage)
W = deep_water (flooding, dangerous areas)
l = lava (reactor cores, industrial accidents)
```

### Variable Encounters

Monster groups can vary:

```
e.mons("wolf / warg / hound")  -- One of these three
```

This means the same vault can play differently each time.

## Special Mechanics

### 1. No Monster Generation Zones

Some vaults have `no_monster_gen` tag:
- Monsters only appear where placed
- Prevents random spawns
- Makes the area more controlled and predictable

### 2. Transparent Vaults

Vaults with `transparent` tag:
- Don't block vision through walls
- More open, dangerous encounters
- Can't use terrain as safely

### 3. Branch Entrances

Special vaults contain entrances to themed branches:
- **Bunker Complexes** (military installations)
- **Corporate Towers** (mega-corp headquarters)
- **Toxic Zones** (irradiated wilderness)
- **The Glowing Crater** (ground zero)

## Difficulty Curve

The game is designed so that:

1. **Levels 1-3**: Learning phase
   - Weak enemies
   - Plentiful healing
   - Small vaults
   - Escape is easy

2. **Levels 4-8**: Growth phase
   - Moderate challenges
   - Need to manage resources
   - Larger vaults with multiple enemies
   - Strategy becomes important

3. **Levels 9-12**: Challenge phase
   - Dangerous encounters
   - Resource scarcity
   - Complex vaults
   - Tactics are critical

4. **Levels 13-15**: Mastery phase
   - Deadly enemies
   - High-value loot
   - Epic vaults
   - Only the prepared survive

## Player Strategy

### Early Game
- Explore thoroughly using auto-explore (o key)
- Fight enemies one-on-one
- Use stairs to escape
- Hoard consumables for emergencies

### Mid Game
- Choose engagements carefully
- Use terrain and tactics
- Start burning consumables when needed
- Build your character's strengths

### Late Game
- Prepare extensively before encounters
- Use all available tools
- Control engagement range
- Know when to retreat

## Generation Statistics

For a typical 15-level dungeon:

- **~40-60 vaults** placed per full run
- **~200-400 monsters** encountered total
- **~100-200 items** found
- **Every run is unique** - millions of possible combinations

## Technical Details

### Vault Tags Reference

```
DEPTH: D:X-Y    → Which levels this can appear on
WEIGHT: N       → Spawn probability (higher = more common)
ORIENT: float   → Can appear anywhere on the level
no_monster_gen  → Only placed monsters, no random spawns
transparent     → Doesn't block line of sight
no_item_gen     → No random item spawns
```

### Monster Slot System

```
MONS: definition  → Creates numbered monster slots
{1} in map        → Places monster from slot 1
{2} in map        → Places monster from slot 2
```

## Balancing Philosophy

Wasteland Crawl follows these principles:

1. **Fair but Deadly**: You can always win, but mistakes are punished
2. **No Grinding**: Can't farm weaker areas for safety
3. **Tactical Depth**: Smart play beats raw stats
4. **Procedural Variety**: No two runs are identical
5. **Progressive Challenge**: Difficulty increases smoothly

## Tips for New Players

1. **Don't rush deeper** - Explore thoroughly
2. **Expect variation** - Same depth can vary in difficulty
3. **Learn enemy patterns** - Some are more dangerous than ratings suggest
4. **Use the environment** - Doorways, corridors create tactical advantages
5. **Save consumables** - But don't die with a full inventory
6. **Death is learning** - Every death teaches you something

---

## Future Enhancements

Planned additions to the procedural system:

- [ ] Dynamic weather/environmental effects
- [ ] Faction-controlled zones with unique generation
- [ ] Random events and encounters
- [ ] Rare super-vaults with unique challenges
- [ ] Adaptive difficulty based on player performance

---

*Every wasteland is different. Adapt or perish.*
