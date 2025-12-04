# Wasteland Mod - TODO List

## HIGH PRIORITY

### Loot System Integration
- [ ] **Update loot system to use new wasteland weapons**
  - Current: `loot/WeaponType.java` uses old DCSS weapons (daggers, swords)
  - Needed: Integrate `equipment/WeaponType.java` (40+ wasteland weapons + guns)
  - Replace or merge the two WeaponType classes
  - Update `LootGenerator.java` to spawn guns and wasteland weapons
  - Add gun ammo to loot drops

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
