# Wasteland Crawl - Gameplay Test Plan

## Overview
This document provides a comprehensive test plan for validating the recent refinements to the Wasteland Crawl mod. Focus areas: safe spawn system, road network generation, and overall gameplay flow from spawn through dungeons.

---

## Test Environment Setup

### Prerequisites
1. **Clean World**: Delete any existing test worlds
2. **Mod Version**: Refinements build (with SafeSpawnFinder + RoadGenerator)
3. **Game Mode**: Survival recommended for authentic experience
4. **Render Distance**: Set to at least 12 chunks to see roads clearly

### Creating Test World
1. Create new world with seed (optional - use same seed for reproducibility)
2. Name it "Wasteland Test Run [Date]"
3. Enable cheats for testing purposes (can use `/tp` to verify locations)
4. Start the world

---

## Phase 1: Initial Spawn Testing

### Test 1.1: Safe Spawn Location
**Objective**: Verify player spawns in a safe, accessible location

**Expected Behavior** (from `SafeSpawnFinder.java:23-56`):
- Player spawns on solid ground (not floating, not suffocating)
- No trees, leaves, or tall grass directly above player
- Clear 3x3 area with 3+ blocks of vertical clearance
- If original spawn unsafe, system searches in spiral pattern up to 32 blocks radius
- Fallback: vegetation cleared in 3x3 area if no ideal spot found

**What to Check**:
- [ ] Player standing on solid block (grass, dirt, stone, etc.)
- [ ] Can see sky directly above (no leaves blocking)
- [ ] No immediate suffocation damage
- [ ] Can move in all directions without obstruction
- [ ] Natural-looking spawn area (not obviously cleared unless fallback used)

**Logs to Review**:
```
[Server thread/INFO] [wasteland/]: Searching for safe spawn near [x, y, z]
[Server thread/INFO] [wasteland/]: Found safe spawn at [radius/center]: BlockPos{x, y, z}
[Server thread/INFO] [wasteland/]: Moved player to safe spawn: BlockPos{x, y, z}
```

**Potential Issues**:
- If spawn looks "carved out" (3x3 cleared area), fallback was used
- If player is slightly offset from natural spawn, safe spawn finder relocated them
- In rare cases (extreme terrain), might still have minor issues

---

### Test 1.2: Temple Near Spawn
**Objective**: Verify test temple generates with altars

**Expected Behavior** (from `WastelandMod.java:102-106`):
- Temple placed 20 blocks west of safe spawn
- Contains 6 altars for testing religion system
- Should be on ground level

**What to Check**:
- [ ] Walk ~20 blocks west of spawn
- [ ] Find temple structure with altars
- [ ] Altars are accessible and interactable
- [ ] Temple on solid ground (not floating/buried)

**Logs to Review**:
```
[Server thread/INFO] [wasteland/]: Placing test temple at: BlockPos{x, y, z}
```

---

## Phase 2: Road Network Testing

### Test 2.1: Road Generation
**Objective**: Verify roads generate between dungeons

**Expected Behavior** (from `RoadGenerator.java:28-52`):
- Roads connect dungeons within 1500 blocks (1.5km)
- Each dungeon connects to 1-2 nearest neighbors
- Typically 40-60 roads total across the world
- Roads appear on world surface, following terrain

**What to Check**:
- [ ] Look for gray/stone pathways in the landscape
- [ ] Roads are approximately 3 blocks wide
- [ ] Roads connect to dungeon entrances (7-11 structures)
- [ ] Multiple roads visible from high vantage points

**Logs to Review**:
```
[Server thread/INFO] [wasteland/]: Generating wasteland roads...
[Server thread/DEBUG] [wasteland/]: Creating road from BlockPos{...} to BlockPos{...}
[Server thread/INFO] [wasteland/]: Generated [N] wasteland roads
```

**Where to Look**:
- Climb to high ground (mountain, tower)
- Look for linear gray/stone paths across landscape
- Use F3 mode to see coordinates of nearby dungeons
- Roads should roughly point toward dungeon locations

---

### Test 2.2: Road Degradation & Materials
**Objective**: Verify roads have realistic wasteland appearance

**Expected Behavior** (from `RoadGenerator.java:161-196`):

**Material Mix**:
- 40%: Gray Concrete (primary pavement)
- 30%: Stone (weathered)
- 15%: Cobblestone (more degraded)
- 15%: Andesite (broken up)
- 10%: Gravel/Coarse Dirt (debris overlay)

**Degradation Levels**:
- 20% of roads: Well-preserved (20-40% missing blocks)
- 65% of roads: Normal degradation (40-70% missing blocks)
- 15% of roads: Heavy degradation (70-90% missing blocks)

**What to Check**:
- [ ] Roads are NOT solid gray concrete (should have variety)
- [ ] Gaps and missing sections are visible (not continuous lines)
- [ ] Different road segments have different conditions
- [ ] Some sections clearly more intact than others
- [ ] Corners often missing (70% chance per corner)
- [ ] Mix of gray, stone, cobblestone, andesite blocks visible

**Visual Inspection**:
Walk along several roads and note:
- Material variety (should see multiple block types)
- Gap patterns (random, not uniform)
- Width consistency (mostly 3 blocks, corners may be narrower)
- Overall "wasteland" aesthetic (broken, weathered, not pristine)

---

### Test 2.3: Road Pathfinding
**Objective**: Use roads to navigate to dungeons

**Expected Behavior**:
- Roads lead directly from dungeon to dungeon
- May be interrupted but general direction is clear
- Roads follow ground level (no elevation changes)

**What to Do**:
1. Find a road near spawn
2. Follow it in one direction
3. Should eventually reach a dungeon entrance (7-11 structure)
4. Note distance traveled (should be within 1500 blocks)
5. Find another road at that dungeon
6. Follow it to the next dungeon

**What to Check**:
- [ ] Can visually follow road path despite gaps
- [ ] Road leads to a dungeon entrance
- [ ] Distance traveled is reasonable (not excessively long)
- [ ] Roads don't lead to empty areas (should always connect dungeons)

**Potential Issues**:
- Roads may cross water (currently no bridge generation)
- Roads may intersect natural structures (minimal impact expected)
- Heavy degradation sections might be hard to follow

---

## Phase 3: Dungeon Progression Testing

### Test 3.1: Find First Dungeon
**Objective**: Locate and identify a dungeon entrance

**What to Look For**:
- 7-11 style convenience store structure
- Entrance portal (colored wool marker)
- Structure should be intact and recognizable

**What to Do**:
1. Follow roads from spawn
2. Locate first dungeon entrance
3. Note the dungeon type (visible in structure design)
4. Check entrance portal is present and accessible

**What to Check**:
- [ ] Dungeon structure generated correctly
- [ ] Entrance portal exists (wool block marker)
- [ ] Can approach and stand on portal
- [ ] Structure connects to road network

---

### Test 3.2: Enter Dungeon
**Objective**: Test portal entry and progression tracking

**Expected Behavior** (from `DungeonProgression.java:115-120`):
- Standing on entrance portal triggers teleport
- Player enters dungeon at floor 1
- Depth tracking initialized
- System logs entry

**What to Do**:
1. Stand on entrance portal (colored wool block)
2. Wait for teleport (may take 1-2 seconds)
3. Observe new environment (should be in dungeon interior)

**What to Check**:
- [ ] Teleport occurs successfully
- [ ] Inside dungeon vault (DCSS-style layout)
- [ ] Chat message confirms entry
- [ ] Can see dungeon interior (not void/error)

**Logs to Review**:
```
[Server thread/INFO] [wasteland/]: Player [UUID] entered dungeon [UUID] (floor 1)
```

---

### Test 3.3: Dungeon Interior
**Objective**: Validate dungeon generation and content

**Expected Elements**:
- Vault layout from DCSS .des files
- Enemies/mobs (if implemented)
- Loot chests
- Stairs down (portal to next level)
- Stairs up (portal to exit)

**What to Check**:
- [ ] Vault layout matches DCSS aesthetic (rooms, corridors)
- [ ] Loot is present and accessible
- [ ] Enemies spawn (if applicable)
- [ ] Can navigate the space
- [ ] Stairs down visible (usually at far end)

**Exploration**:
- Take note of vault type/layout
- Check difficulty appropriate for depth (should be EARLY tier for depths 1-5)
- Collect any loot
- Look for descent stairs

---

### Test 3.4: Descend Through Floors
**Objective**: Test multi-floor progression

**Expected Behavior** (from `DungeonProgression.java:140-208`):
- Each descent increases floor counter
- New vault generates for each floor
- Depth tracking updates
- Bottom floor reached = rune awarded (if max-level dungeon)

**What to Do**:
1. Find stairs down (exit portal for next level)
2. Stand on portal to descend
3. Repeat for multiple floors
4. Note changes in difficulty/vault types
5. Continue until reaching bottom floor

**What to Check**:
- [ ] Each floor has different layout
- [ ] Floor counter increments (visible in logs)
- [ ] Difficulty increases with depth (more enemies, harder layouts)
- [ ] Can descend multiple times without errors
- [ ] Bottom floor triggers rune award (if applicable)

**Logs to Review**:
```
[Server thread/INFO] [wasteland/]: Player [UUID] descended: floor [N] → [N+1] (dungeon has [MAX] levels)
[Server thread/INFO] [wasteland/]: Player [UUID] reached bottom floor of [Dungeon Type]!
[Server thread/INFO] [wasteland/]: Awarded [Rune Name] to player! ([X]/[Y])
```

**Rune Collection** (Max-Level Dungeons):
- [ ] Gold message displayed: "You found the [Rune Name]!"
- [ ] Rune count shown: "[X]/[Y] runes"
- [ ] Can only collect each rune once
- [ ] Second visit shows "already collected" message

---

### Test 3.5: Exit Dungeon
**Objective**: Return to overworld safely

**Expected Behavior** (from `DungeonProgression.java:213-221`):
- Stairs up portal returns to overworld
- Player exits at or near dungeon entrance
- Depth reset to 0 (surface)
- Dungeon state cleared

**What to Do**:
1. Find stairs up (usually near entrance)
2. Stand on up portal
3. Teleport to surface
4. Verify location

**What to Check**:
- [ ] Return to overworld (not void/error)
- [ ] Near dungeon entrance (should be at/near 7-11)
- [ ] Can move and interact normally
- [ ] Ready to enter another dungeon

**Logs to Review**:
```
[Server thread/INFO] [wasteland/]: Player [UUID] exited dungeon [UUID]
[Server thread/INFO] [wasteland/]: Player returned to surface (depth 0)
```

---

## Phase 4: Multi-Dungeon Run

### Test 4.1: Second Dungeon
**Objective**: Repeat dungeon experience with different location

**What to Do**:
1. From first dungeon exit, find a road
2. Follow to second dungeon
3. Enter and explore
4. Note differences from first dungeon
5. Exit safely

**What to Check**:
- [ ] Different dungeon type/theme
- [ ] Different vault layouts
- [ ] Progression tracking works for multiple dungeons
- [ ] No conflicts or errors
- [ ] Roads connect multiple dungeons as expected

---

### Test 4.2: Rune Collection Progress
**Objective**: Track rune collection across multiple dungeons

**Expected Behavior**:
- Max-level dungeons (full depth) award runes
- Shorter dungeons don't award runes
- Each rune collectable only once
- Progress tracked toward final realm access

**What to Do**:
1. Complete 3-5 dungeons
2. Note which ones award runes
3. Track rune count progression
4. Verify each dungeon awards unique rune (or none)

**What to Check**:
- [ ] Rune count increases appropriately
- [ ] Max-level dungeons award runes
- [ ] Partial dungeons don't award runes
- [ ] Chat messages show rune count after each collection
- [ ] System logs confirm rune awards

**Expected Runes** (from code - need 3 for final realm):
Check `RuneType.java` for complete list, but typical examples:
- Serpentine Rune (Swamp)
- Abyssal Rune (Abyss)
- Demonic Rune (Hell)
- Slimy Rune (Slime Pits)
- Icy Rune (Cocytus)

---

### Test 4.3: Depth Progression
**Objective**: Verify depth tracking across multiple dungeons

**Expected Behavior**:
- Each dungeon entrance resets depth
- Within dungeon, depth increases per floor
- Vault difficulty scales with depth
- Tiers: EARLY (1-5), MID (6-10), LATE (11+)

**What to Check**:
- [ ] First few dungeons use EARLY tier vaults
- [ ] Deeper floors use harder vaults
- [ ] Depth doesn't persist between different dungeons
- [ ] Each dungeon starts at floor 1

**Logs to Review**:
```
[Server thread/INFO] [wasteland/]: Selected [TIER] tier vault for depth [N]: [vault_name]
```

---

## Phase 5: System Integration Testing

### Test 5.1: Save/Load Persistence
**Objective**: Verify world state persists across sessions

**What to Do**:
1. Complete 1-2 dungeons
2. Collect at least one rune
3. Exit game completely
4. Reload world
5. Check state

**What to Check**:
- [ ] Roads still present (not regenerated)
- [ ] Dungeons in same locations
- [ ] Rune count preserved
- [ ] No duplicate generation
- [ ] Can continue progression

**Logs to Review**:
```
[Server thread/INFO] [wasteland/]: Loaded progression data for [N] players
```

---

### Test 5.2: Respawn Handling
**Objective**: Test death and respawn mechanics

**What to Do**:
1. Note current location
2. Die (fall damage, mobs, etc.)
3. Respawn
4. Check spawn location

**What to Check**:
- [ ] Respawn at safe spawn (same as initial spawn)
- [ ] No suffocation on respawn
- [ ] Progression preserved (don't lose rune count)
- [ ] Can continue playing normally

---

### Test 5.3: Multiple Visits to Same Dungeon
**Objective**: Test dungeon revisit mechanics

**What to Do**:
1. Enter a dungeon
2. Exit without completing
3. Re-enter same dungeon
4. Complete dungeon
5. Exit and re-enter again

**What to Check**:
- [ ] Can enter same dungeon multiple times
- [ ] Rune only awarded once (first completion)
- [ ] Subsequent visits show "already collected" message
- [ ] Dungeon state persists or resets appropriately

---

## Phase 6: Edge Cases & Stress Testing

### Test 6.1: Extreme Terrain Spawn
**Objective**: Test safe spawn in difficult terrain

**What to Try**:
- Create worlds with different biomes at spawn
- Test in forest (trees), mountains (cliffs), ocean (water)
- Note how system handles each

**What to Check**:
- [ ] Spawn always safe regardless of biome
- [ ] System adapts to terrain challenges
- [ ] Fallback clearing works if needed
- [ ] No fatal spawn conditions

---

### Test 6.2: Road Coverage
**Objective**: Verify road network completeness

**What to Do**:
1. Use F3 debug to find dungeon coordinates
2. Visit 5-10 dungeon locations
3. Check if each has at least one road connection

**What to Check**:
- [ ] Most dungeons have 1-2 road connections
- [ ] Roads don't exceed 1500 block max distance
- [ ] No isolated dungeons (all should be reachable)
- [ ] Road count reasonable (40-60 typical)

---

### Test 6.3: Performance
**Objective**: Monitor performance during generation

**What to Check**:
- [ ] World creation time reasonable (5-10 seconds for generation)
- [ ] No lag spikes during play
- [ ] Road generation doesn't freeze game
- [ ] Dungeon teleports are smooth

**Logs to Review**:
```
[Server thread/INFO] [wasteland/]: Generating dungeons across the wasteland...
[Server thread/INFO] [wasteland/]: Generating wasteland roads...
[Server thread/INFO] [wasteland/]: Generated [N] wasteland roads
```

Timing between these log lines indicates generation time.

---

## Phase 7: Guns & Combat Testing (Focused Run)

### Test 7.1: Gun Loot Discovery
**Objective**: Verify guns spawn as loot in dungeons and on robots

**Expected Behavior**:
- Guns found in dungeon loot chests
- Robots drop gun loot on death
- Gun tier scales with dungeon depth/difficulty
- Ammunition spawns alongside guns

**Gun Tier System** (from `WeaponType.java:162-175`):
- **Tier 1**: Pipe Pistol, .22 Pistol (early game, low damage)
- **Tier 2**: 9mm Pistol, .45 Pistol (standard sidearms)
- **Tier 3**: .44 Magnum, Desert Eagle (powerful pistols)
- **Tier 4**: Hunting Rifle, Varmint Rifle (civilian rifles)
- **Tier 5**: Assault Rifle, Battle Rifle, Sniper Rifle (military)
- **Tier 6**: Sawed-Off Shotgun, Combat Shotgun (close combat)
- **Tier 7**: Laser Pistol, Laser Rifle, Plasma Rifle, Gauss Rifle (energy weapons)

**What to Check**:
- [ ] Find at least one gun in early dungeons (Tier 1-2)
- [ ] Gun has readable stats (damage, delay, ammo type)
- [ ] Ammunition spawns in loot tables
- [ ] Higher tier guns appear in deeper dungeons
- [ ] Robot enemies drop gun loot when killed

**Loot Sources**:
1. Dungeon loot chests (random generation)
2. Robot entity drops (SentryBot, MrGusty, Annihilator)
3. Unique artifact weapons (god-tier guns)

---

### Test 7.2: Ammunition System
**Objective**: Verify ammunition types work correctly

**Ammunition Types** (from `AmmoType.java:7-24`):
- **.22LR**: Light pistol ammo (Tier 1 guns)
- **9mm**: Standard pistol ammo (Tier 2 guns)
- **.45 ACP**: Heavy pistol ammo (Tier 2-3 guns)
- **5.56mm**: Rifle ammo (Tier 4-5 rifles)
- **7.62mm**: Heavy rifle/sniper ammo (Tier 5 rifles)
- **Shotgun Shell**: 12 gauge (Tier 6 shotguns)
- **Energy Cell**: Power cells (Tier 7 energy weapons)

**What to Check**:
- [ ] Ammunition appears as separate loot items
- [ ] Each gun requires specific ammo type
- [ ] Cannot use gun without matching ammo
- [ ] Ammo count tracked in inventory
- [ ] Different ammo types have different damage values

**Testing Process**:
1. Find a gun (note its ammo type)
2. Find matching ammunition
3. Equip gun and verify it requires ammo
4. Use gun in combat (ammo count should decrease)
5. Run out of ammo (gun should be unusable)
6. Find different gun requiring different ammo type
7. Verify ammo types don't cross-work

---

### Test 7.3: Gun Progression Arc
**Objective**: Experience full gun progression from early to late game

**Recommended Test Path**:

**Early Game (Depths 1-3)**:
1. Start new character
2. Enter first dungeon
3. Look for Tier 1 guns:
   - Pipe Pistol (8 damage, .22 ammo)
   - .22 Pistol (10 damage, .22 ammo)
4. Collect .22 ammo
5. Test combat with early pistol
6. Note: Low damage, common ammo

**Mid Game (Depths 4-8)**:
1. Progress through dungeons
2. Look for Tier 2-3 guns:
   - 9mm Pistol (12 damage, 9mm ammo)
   - .45 Pistol (15 damage, .45 ammo)
   - .44 Magnum (18 damage, .45 ammo)
3. Collect 9mm/.45 ammo
4. Compare damage output to early guns
5. Note: Better damage, still manageable ammo

**Late Game (Depths 9-12)**:
1. Reach deeper dungeons
2. Look for Tier 4-5 guns:
   - Hunting Rifle (16 damage, 5.56mm)
   - Assault Rifle (20 damage, 5.56mm)
   - Battle Rifle (24 damage, 7.62mm)
   - Sniper Rifle (28 damage, 7.62mm)
3. Collect rifle ammunition
4. Test two-handed rifle combat
5. Note: High damage, heavier weapons

**End Game (Depths 13+)**:
1. Reach final dungeons
2. Look for Tier 6-7 guns:
   - Combat Shotgun (26 damage, shotgun shells)
   - Laser Rifle (26 damage, energy cells)
   - Plasma Rifle (32 damage, energy cells)
   - Gauss Rifle (35 damage, energy cells)
3. Collect energy cells
4. Test energy weapons
5. Note: Maximum damage, rare ammo

**What to Document**:
- [ ] Damage progression feels balanced (gradual increase)
- [ ] Ammunition scarcity increases with gun tier
- [ ] Two-handed rifles require different tactics than pistols
- [ ] Energy weapons feel rare/powerful
- [ ] Gun skill progression impacts effectiveness

---

### Test 7.4: Gun vs. Melee Comparison
**Objective**: Compare gun combat to melee combat effectiveness

**Test Setup**:
Create two test runs in parallel (or sequential):

**Run A: Pure Guns**:
1. Focus on finding and using guns only
2. Avoid melee weapons
3. Prioritize ammunition collection
4. Note combat effectiveness

**Run B: Pure Melee**:
1. Focus on melee weapons (swords, axes, etc.)
2. Avoid guns completely
3. Note combat effectiveness

**Comparison Metrics**:
- [ ] **Damage Output**: Which deals more damage per hit?
- [ ] **Attack Speed**: Which attacks faster?
- [ ] **Resource Management**: Ammo vs. durability
- [ ] **Range**: Guns can attack from distance
- [ ] **Difficulty**: Which is easier to play?
- [ ] **Fun Factor**: Which is more enjoyable?

**Expected Tradeoffs**:
- **Guns**: Higher damage at range, limited by ammo, safer
- **Melee**: Unlimited use, close combat risk, no ammo needed

---

### Test 7.5: Robot Combat & Gun Drops
**Objective**: Fight robot enemies and collect gun loot

**Robot Types** (from entity files):
1. **SentryBot**: Standard robot guard
2. **MrGusty**: Handy robot variant
3. **AnnihilatorSentryBot**: Heavy combat robot

**Expected Behavior** (from `RobotLoot.java`):
- Robots have gun-focused loot tables
- Higher tier robots drop better guns
- Ammunition drops common from robots
- Energy weapons more likely from advanced robots

**What to Do**:
1. Find and engage robot enemies in dungeons
2. Defeat at least 5 robots
3. Collect dropped loot
4. Analyze gun drops

**What to Check**:
- [ ] Robots actually drop loot on death
- [ ] Gun drops are appropriate for robot type
- [ ] Ammunition drops alongside guns
- [ ] Higher tier robots drop better loot
- [ ] Robot loot tables feel balanced

**Combat Notes**:
- Robots may use ranged attacks (be prepared)
- Cover/positioning important against guns
- Energy weapons particularly effective against robots
- Robot difficulty should scale with dungeon depth

---

### Test 7.6: God-Weapons with Guns
**Objective**: Test piety system with gun weapons

**God-Weapon Piety System** (from `GodWeaponPiety.java`):
- Using god-favored weapons grants piety
- Each god has weapon preferences
- Killing with favored weapon = bonus piety
- Guns may be favored by certain gods

**What to Do**:
1. Worship a god at temple altar
2. Identify god's favored weapon types
3. Find a gun (if favored) or other weapon
4. Use weapon in combat
5. Observe piety changes

**What to Check**:
- [ ] God preferences include guns (if applicable)
- [ ] Using favored gun grants piety
- [ ] Killing enemies with favored gun gives bonus
- [ ] Piety gain visible in UI/logs
- [ ] Divine abilities unlock with piety

**Gods & Weapon Preferences** (check `God.java` for specifics):
- Different gods favor different weapon types
- Some gods may favor ranged (guns, bows)
- Some gods may favor melee
- Some gods may be weapon-agnostic

---

### Test 7.7: Unique Gun Artifacts
**Objective**: Find and test unique/artifact guns

**Expected Features** (from `UniqueArtifact.java`):
- Pre-defined unique weapons with names
- Special properties/enchantments
- Higher stats than normal guns
- Rare loot drops

**What to Look For**:
- Named weapons (e.g., "The Peacemaker", "Annihilator")
- Guns with special effects (fire damage, poison, etc.)
- Unusually high damage values
- Unique visual descriptions

**What to Check**:
- [ ] At least one unique gun found (after multiple dungeons)
- [ ] Unique gun has special name/description
- [ ] Stats noticeably better than normal guns
- [ ] Special properties function correctly
- [ ] Feels rewarding to find

**Artifact Properties to Test**:
- Damage bonuses
- Speed bonuses
- Special elemental effects
- Slaying bonuses against enemy types
- Any unique mechanics

---

### Test 7.8: Ammunition Economy
**Objective**: Test ammunition scarcity and management

**Research Questions**:
1. How much ammo spawns per dungeon?
2. Can you sustain gun-only playstyle?
3. Do you run out of ammo frequently?
4. Is ammo management fun or frustrating?

**Test Method**:
1. Start with a gun (Tier 2-3 recommended)
2. Count starting ammunition
3. Complete one full dungeon using only guns
4. Count remaining ammunition
5. Note whether you found more ammo in dungeon
6. Calculate ammo net gain/loss

**What to Document**:
- Starting ammo count: _____
- Ammo used in dungeon: _____
- Ammo found in dungeon: _____
- Net change: _____
- Sustainability: [ ] Sustainable [ ] Running out [ ] Excess

**Balance Considerations**:
- **Too much ammo**: Guns become overpowered, no tradeoff
- **Too little ammo**: Frustrating, forced to use melee
- **Just right**: Meaningful choice between gun and melee

**Recommended Balance**:
- Early game: Abundant ammo for Tier 1-2 guns
- Mid game: Moderate ammo, need to conserve
- Late game: Scarce ammo, precious resource
- Energy weapons: Very rare ammo, powerful payoff

---

### Test 7.9: Gun Combat Feel
**Objective**: Evaluate subjective combat experience with guns

**Evaluation Criteria**:

**1. Sound & Feedback**:
- [ ] Gun firing has appropriate sound
- [ ] Hit registration feels responsive
- [ ] Damage numbers/feedback clear

**2. Animation & Visuals**:
- [ ] Gun attack animation exists
- [ ] Projectile visible (if applicable)
- [ ] Impact effects visible

**3. Tactical Depth**:
- [ ] Range advantage meaningful
- [ ] Cover/positioning matters
- [ ] Ammo management adds strategy
- [ ] Different guns require different tactics

**4. Balance vs. Melee**:
- [ ] Guns feel viable (not underpowered)
- [ ] Guns not overpowered (melee still useful)
- [ ] Both playstyles fun
- [ ] Hybrid builds viable (using both)

**5. Progression Satisfaction**:
- [ ] Upgrading from pistol to rifle feels good
- [ ] Finding rare gun is exciting
- [ ] Energy weapons feel like rewards
- [ ] Tier progression clear and rewarding

---

### Gun Testing Summary Template

**Test Run Focus**: Guns & Ranged Combat

**Character Build**:
- Primary weapon category: Guns
- Secondary weapon (if any): ____________
- God worshipped: ____________
- Playstyle: [ ] Pure ranged [ ] Hybrid [ ] Situational

**Guns Found** (list all):
1. ____________ (Tier __, Depth __)
2. ____________ (Tier __, Depth __)
3. ____________ (Tier __, Depth __)
4. ____________ (Tier __, Depth __)
5. ____________ (Tier __, Depth __)

**Ammunition Collected**:
- .22LR: _____ rounds
- 9mm: _____ rounds
- .45 ACP: _____ rounds
- 5.56mm: _____ rounds
- 7.62mm: _____ rounds
- Shotgun: _____ shells
- Energy: _____ cells

**Combat Statistics**:
- Total enemies killed: _____
- Kills with guns: _____
- Kills with melee: _____
- Ammunition expended: _____
- Times ran out of ammo: _____

**Progression Arc**:
- Started with: ____________
- Best gun found: ____________
- Final loadout: ____________

**Gun System Evaluation**:

**What Worked Well**:
-
-
-

**What Felt Unbalanced**:
-
-
-

**Ammunition Economy**:
- [ ] Too scarce (constantly empty)
- [ ] Well balanced (meaningful choices)
- [ ] Too abundant (no resource tension)

**Gun vs. Melee**:
- [ ] Guns stronger (imbalanced toward ranged)
- [ ] Balanced (both viable)
- [ ] Melee stronger (guns underpowered)

**Overall Gun Experience**:
Rating: ___ / 10

Comments:



---

**Recommended Test Sequence**:
1. Complete Phase 1-6 (general gameplay) first
2. Create NEW character for dedicated gun-focused run
3. Prioritize gun loot over melee weapons
4. Focus on ammunition management
5. Fight robots specifically for gun drops
6. Test full progression from Tier 1 to Tier 7
7. Document balance and feel

---

## Test Results Template

### Session Information
- **Date**: ___________
- **World Seed**: ___________
- **Playtime**: ___________
- **Dungeons Visited**: ___________
- **Runes Collected**: ___________

### Safe Spawn System
- Initial spawn location: [ ] Safe [ ] Issues
- Spawn quality: [ ] Natural [ ] Fallback Cleared
- Issues encountered: ___________

### Road Network
- Roads visible: [ ] Yes [ ] No
- Road quality: [ ] Good variety [ ] Too uniform [ ] Too degraded
- Navigation: [ ] Easy to follow [ ] Difficult
- Issues encountered: ___________

### Dungeon Progression
- Entry/exit: [ ] Smooth [ ] Issues
- Floor progression: [ ] Working [ ] Broken
- Rune awards: [ ] Correct [ ] Missing [ ] Duplicate
- Issues encountered: ___________

### Overall Experience
**What Worked Well**:
-
-
-

**What Needs Improvement**:
-
-
-

**Bugs/Errors Found**:
-
-
-

**Gameplay Feel**:
(Describe the experience playing from spawn through dungeons)

---

## Known Limitations (Reference)

### Safe Spawn System
- May teleport player slightly from natural spawn
- Clearing vegetation is visible if fallback used
- Search might fail in extremely hostile terrain (rare)

### Road Network
- Don't respect water bodies (roads may go into lakes)
- No elevation changes (ground-level only)
- May intersect with natural structures
- Generation time increases with dungeon count

### Dungeons
- Vault selection random within tier (not curated)
- Difficulty scaling is rough approximation
- No boss encounters yet (future feature)

---

## Critical Issues Checklist

If any of these occur, report immediately:

**Game-Breaking**:
- [ ] Cannot spawn (stuck in void/fatal error)
- [ ] Crash on world creation
- [ ] Cannot enter dungeons (portal non-functional)
- [ ] Cannot exit dungeons (trapped inside)
- [ ] Rune collection broken (not tracking)

**Major Issues**:
- [ ] Spawn in unsafe location (suffocating, falling)
- [ ] Roads not generating at all
- [ ] Dungeons not generating
- [ ] Progression not saving
- [ ] Massive performance issues

**Minor Issues**:
- [ ] Some roads missing
- [ ] Spawn location suboptimal but safe
- [ ] Visual glitches
- [ ] Minor performance hiccups

---

## Next Steps After Testing

Based on test results, potential refinements:
1. Adjust spawn search radius if needed
2. Tune road degradation percentages
3. Fix any game-breaking bugs
4. Add quality-of-life improvements
5. Balance difficulty progression
6. Add missing features (bridges over water, etc.)

---

## Appendix: Expected Log Output

### Normal World Creation Sequence
```
[Server thread/INFO] [wasteland/]: ═══════════════════════════════════════════════════════
[Server thread/INFO] [wasteland/]: Wasteland Crawl - Player Spawned in Overworld
[Server thread/INFO] [wasteland/]: Find a dungeon entrance to begin your adventure!
[Server thread/INFO] [wasteland/]: ═══════════════════════════════════════════════════════
[Server thread/INFO] [wasteland/]: Generating dungeons across the wasteland...
[Server thread/INFO] [wasteland/]: Generating wasteland roads...
[Server thread/DEBUG] [wasteland/]: Creating road from BlockPos{...} to BlockPos{...}
[Server thread/INFO] [wasteland/]: Generated 47 wasteland roads
[Server thread/INFO] [wasteland/]: Searching for safe spawn near BlockPos{...}
[Server thread/INFO] [wasteland/]: Found safe spawn at center: BlockPos{...}
[Server thread/INFO] [wasteland/]: Placing test temple at: BlockPos{...}
```

### Dungeon Entry/Exit Sequence
```
[Server thread/INFO] [wasteland/]: Player [UUID] entered dungeon [UUID] (floor 1)
[Server thread/INFO] [wasteland/]: Selected EARLY tier vault for depth 1: [vault_name]
[Server thread/INFO] [wasteland/]: Player [UUID] descended: floor 1 → 2 (dungeon has 5 levels)
...
[Server thread/INFO] [wasteland/]: ═══════════════════════════════════════════════════════
[Server thread/INFO] [wasteland/]: Player [UUID] reached bottom floor of Swamp!
[Server thread/INFO] [wasteland/]: Awarded Serpentine Rune to player! (1/3)
[Server thread/INFO] [wasteland/]: ═══════════════════════════════════════════════════════
[Server thread/INFO] [wasteland/]: Player [UUID] exited dungeon [UUID]
[Server thread/INFO] [wasteland/]: Player returned to surface (depth 0)
```

---

**End of Test Plan**

*Document created: December 4, 2025*
*For: Wasteland Crawl Refinements Testing*
*Version: 0.1.0 - Safe Spawn & Road Network Update*
