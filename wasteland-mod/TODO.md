# Wasteland Mod - TODO List

## HIGH PRIORITY

### Robot Enemy Types
- [ ] **Create robot monster system**
  - Robot base class/interface with shared behavior
  - Armor/damage resistance mechanics (resistant to bleed/poison, vulnerable to EMP)
  - Power source mechanics (can be shut down vs destroyed)
  - Detect living/heat signature AI behavior

- [ ] **Early Game Robots (Tier 1-2)**
  - **Eyebot**: Floating surveillance drone, weak, calls for help
  - **Protectron**: Basic security robot, slow, moderate damage
  - **Mr. Handy**: Utility robot, multiple arms, fire damage
  - **Damaged Sentry**: Malfunctioning heavy robot, erratic behavior

- [ ] **Mid Game Robots (Tier 3-4)**
  - **Security Bot**: Advanced protectron, faster, better armor
  - **Sentry Bot**: Heavy combat robot, minigun/missile attacks
  - **Assaultron**: Fast melee robot with laser head weapon
  - **Gutsy (Mr. Gutsy)**: Military Mr. Handy with plasma weapons
  - **Robobrain**: Brain in robot body, psionic/energy attacks

- [ ] **Late Game Robots (Tier 5+)**
  - **Annihilator Sentry Bot**: Upgraded sentry with dual miniguns
  - **Quantum Assaultron**: Elite assaultron with stealth capabilities
  - **Overlord (Robot Boss)**: Massive security mainframe robot
  - **Experimental Bot**: Pre-war prototype with unknown weapons

- [ ] **Robot Special Mechanics**
  - EMP vulnerability (extra damage from electrical attacks)
  - Self-repair protocols (regeneration over time)
  - Alarm systems (summons other robots when damaged)
  - Targeting systems (high accuracy vs low evasion targets)
  - Overload/self-destruct on death (explosion damage)

### Artifact Weapons System
- [ ] **Implement unique artifact guns**
  - Create named legendary firearms (e.g., "The Last Ranger", "Vault-Tec Prototype")
  - Pre-war military weapons with lore
  - Experimental energy weapons
  - Each with unique backstory

- [ ] **Artifact weapon special abilities**
  - Elemental effects (fire bullets, cryo rounds, plasma burn)
  - Status effect procs (poison rounds, armor-piercing, bleeding)
  - Stat bonuses (+damage, +accuracy, +crit chance)
  - Active abilities (burst fire mode, overcharge shot)
  - Passive abilities (auto-reload, infinite ammo for certain types)

- [ ] **God-blessed artifact weapons**
  - Trog: Melee weapons with berserk-on-hit
  - TSO: Holy firearms that smite undead
  - Kikubaaqudgha: Necrotic weapons that drain life
  - Vehumet: Energy weapons with spell-like effects
  - Gozag: Gold-plated weapons with bonus loot
  - Sif Muna: Intelligent weapons that regenerate MP

- [ ] **Artifact weapon restrictions**
  - God alignment restrictions (evil gods reject holy weapons, etc.)
  - Skill requirements (high-tier artifacts need weapon skill)
  - Mutation interactions (tentacles can't use rifles, claws bonus with unarmed)
  - Curse mechanics for powerful artifacts

- [ ] **Random artifact generation (Randarts)**
  - Procedurally generated unique weapons
  - Random combination of properties
  - Balance system to prevent overpowered combinations
  - Lore generation for procedural artifacts

### God-Weapon Relations
- [ ] **God weapon preferences**
  - Trog: Bonus piety for melee kills, penalty for using guns/magic
  - Okawaru: Bonus for using martial weapons
  - Kikubaaqudgha: Bonus for necrotic/poison weapons
  - TSO: Bonus for holy weapons, penalty for cursed
  - Vehumet: Bonus for energy weapons (laser/plasma)

- [ ] **Divine weapon gifts**
  - Gods grant special weapons at piety milestones
  - Trog: Gives legendary melee weapons
  - Okawaru: Gives quality martial weapons
  - TSO: Gives holy firearms
  - Vehumet: Gives energy weapons
  - Sif Muna: Gives spell-channeling weapons

## MEDIUM PRIORITY

### Combat Integration
- [ ] Integrate wasteland weapons with combat system
- [ ] Implement gun accuracy mechanics
- [ ] Add recoil system for firearms
- [ ] Implement ammo consumption
- [ ] Add reload mechanics (action economy)
- [ ] Energy weapon overheating

### Weapon Crafting/Modification
- [ ] Weapon modification system (attachments)
  - Scopes (+accuracy)
  - Extended magazines (+ammo capacity)
  - Suppressors (+stealth)
  - Damage mods (hollow point, armor piercing)

### Monster Loot Tables
- [ ] Add appropriate weapons to monster drops
  - Raiders drop guns and ammo
  - Military robots drop energy weapons
  - Ghouls drop rusty melee weapons
  - Super mutants drop heavy weapons

### Weapon Durability
- [ ] Implement weapon degradation
- [ ] Repair mechanics
- [ ] Weapon breaking on critical failures

## LOW PRIORITY

### UI/UX
- [ ] Weapon comparison tooltips
- [ ] Ammo counter display
- [ ] Weapon quick-swap hotkeys
- [ ] Firing mode selection UI

### Balance
- [ ] Weapon damage balance pass
- [ ] Ammo scarcity tuning
- [ ] Energy weapon availability tuning
- [ ] Artifact spawn rates

### Visual/Audio
- [ ] Weapon sprites/models
- [ ] Gun firing sound effects
- [ ] Energy weapon VFX
- [ ] Reload animations

---

## COMPLETED ✅
- [x] Create wasteland weapon types (equipment/WeaponType.java)
- [x] Replace SLINGS with GUNS skill
- [x] Create ammo system (AmmoType.java)
- [x] Implement 7-tier gun progression
- [x] Update race aptitudes for GUNS
- [x] Status effect system for buffs/debuffs
- [x] Divine ability invocation system
- [x] Mutation effects integration
- [x] **Loot system integration with wasteland weapons**
  - Integrated equipment/WeaponType.java with loot system
  - Updated LootGenerator with wasteland starting weapons
  - Updated unique artifacts to use wasteland weapons
  - All 40+ weapons now spawn in loot tables
