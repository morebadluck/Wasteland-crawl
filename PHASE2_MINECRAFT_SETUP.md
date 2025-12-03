# 🎮 Phase 2: Minecraft Mod Foundation

## 📋 Overview

**Goal**: Set up Minecraft Forge development environment and render one DCSS room in Minecraft 3D

**Timeline**: Weeks 1-8 (2 months)

**Deliverable**: Single DCSS room visible in Minecraft with basic blocks

---

## 🎯 Phase 2 Milestones

### **Milestone 1: Development Environment** (Week 1-2)
- [ ] Install Java JDK 17
- [ ] Download Minecraft Forge MDK
- [ ] Set up IntelliJ IDEA
- [ ] Verify mod loads in Minecraft
- [ ] Place first block programmatically

### **Milestone 2: Basic Communication** (Week 3-4)
- [ ] Design JSON protocol for DCSS-Minecraft
- [ ] Create DCSS room export function
- [ ] Parse JSON in Minecraft mod
- [ ] Test data transfer

### **Milestone 3: First Room Render** (Week 5-8)
- [ ] Export 10x10 room from DCSS
- [ ] Map DCSS tiles to Minecraft blocks
- [ ] Place walls and floors
- [ ] Spawn player in correct position
- [ ] **SUCCESS**: See DCSS room in 3D!

---

## 🔧 Step 1: Install Java JDK 17

### **Check Current Java Version**

```bash
java -version
```

### **Install Java 17 (Required for Minecraft 1.20+)**

**macOS (Homebrew):**
```bash
brew install openjdk@17
sudo ln -sfn /opt/homebrew/opt/openjdk@17/libexec/openjdk.jdk \
  /Library/Java/JavaVirtualMachines/openjdk-17.jdk
```

**Verify Installation:**
```bash
java -version
# Should show: openjdk version "17.x.x"
```

---

## 🔧 Step 2: Download Minecraft Forge MDK

### **Choose Minecraft Version**

**Recommendation**: Minecraft 1.20.1 (stable, well-documented)

### **Download Forge MDK**

1. Visit: https://files.minecraftforge.net/net/minecraftforge/forge/
2. Select **Minecraft Version 1.20.1**
3. Download **MDK (Mod Development Kit)**
4. Extract to: `/Users/mojo/git/crawl/wasteland-mod/`

```bash
cd /Users/mojo/git/crawl
mkdir wasteland-mod
cd wasteland-mod
# Extract downloaded Forge MDK here
unzip forge-1.20.1-*-mdk.zip -d .
```

### **Initial Gradle Setup**

```bash
cd /Users/mojo/git/crawl/wasteland-mod
./gradlew genIntellijRuns
```

This will:
- Download Minecraft dependencies (~500 MB)
- Set up development workspace
- Configure IntelliJ run configurations

**Expected Time**: 5-15 minutes (first time)

---

## 🔧 Step 3: Set Up IntelliJ IDEA

### **Install IntelliJ IDEA**

**macOS:**
```bash
brew install --cask intellij-idea-ce
```

Or download from: https://www.jetbrains.com/idea/download/

### **Open Project**

1. Launch IntelliJ IDEA
2. File → Open
3. Select `/Users/mojo/git/crawl/wasteland-mod/`
4. Wait for Gradle import to complete

### **Configure JDK**

1. File → Project Structure → Project
2. SDK: Java 17
3. Language Level: 17
4. Apply

---

## 🔧 Step 4: Create Basic Mod Structure

### **File: src/main/java/com/wasteland/WastelandMod.java**

```java
package com.wasteland;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod("wasteland")
public class WastelandMod {
    public static final String MOD_ID = "wasteland";
    private static final Logger LOGGER = LogManager.getLogger();

    public WastelandMod() {
        FMLJavaModLoadingContext.get().getModEventBus()
            .addListener(this::setup);

        LOGGER.info("Wasteland Crawl Mod Loading...");
    }

    private void setup(final FMLCommonSetupEvent event) {
        LOGGER.info("Wasteland Crawl - DCSS Integration Ready!");
    }
}
```

### **File: src/main/resources/META-INF/mods.toml**

```toml
modLoader="javafml"
loaderVersion="[47,)"
license="GPL-3.0"

[[mods]]
modId="wasteland"
version="0.1.0"
displayName="Wasteland Crawl"
description='''
Post-apocalyptic roguelike with DCSS gameplay in Minecraft 3D.
Turn-based tactical combat, massive open world, 1000+ dungeons.
'''
authors="Wasteland Crawl Team"
displayURL="https://github.com/wasteland-crawl"

[[dependencies.wasteland]]
    modId="forge"
    mandatory=true
    versionRange="[47,)"
    ordering="NONE"
    side="BOTH"

[[dependencies.wasteland]]
    modId="minecraft"
    mandatory=true
    versionRange="[1.20.1]"
    ordering="NONE"
    side="BOTH"
```

---

## 🔧 Step 5: Test Mod Loads

### **Build the Mod**

```bash
cd /Users/mojo/git/crawl/wasteland-mod
./gradlew build
```

### **Run Minecraft with Mod**

```bash
./gradlew runClient
```

**What to Expect:**
1. Minecraft launches (1-2 minutes)
2. Check logs for: `"Wasteland Crawl Mod Loading..."`
3. Main menu appears
4. Mods button → Should see "Wasteland Crawl"

**Success Criteria**: Mod appears in mods list, no crashes

---

## 🔧 Step 6: Place First Block Programmatically

### **Goal**: Place a single block to verify we can modify the world

### **File: src/main/java/com/wasteland/WastelandMod.java** (Updated)

```java
package com.wasteland;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod("wasteland")
public class WastelandMod {
    public static final String MOD_ID = "wasteland";
    private static final Logger LOGGER = LogManager.getLogger();

    public WastelandMod() {
        LOGGER.info("Wasteland Crawl Mod Loading...");
    }

    @Mod.EventBusSubscriber(modid = MOD_ID)
    public static class Events {

        @SubscribeEvent
        public static void onPlayerJoin(PlayerEvent.PlayerLoggedInEvent event) {
            if (event.getEntity().level() instanceof ServerLevel level) {
                // Place a single gold block at player's feet
                BlockPos pos = event.getEntity().blockPosition().below();
                level.setBlock(pos, Blocks.GOLD_BLOCK.defaultBlockState(), 3);

                LOGGER.info("Wasteland Crawl: Placed test block at {}", pos);
            }
        }
    }
}
```

### **Test**

1. Run `./gradlew runClient`
2. Create new world (Creative mode)
3. Join world
4. **Look down**: You should see a gold block appear beneath you
5. Check logs for: `"Wasteland Crawl: Placed test block at..."`

**Success**: Gold block appears! We can programmatically place blocks.

---

## 📊 Phase 2 Progress Tracker

### **Week 1-2 Checklist:**
- [ ] Java 17 installed
- [ ] Forge MDK downloaded and extracted
- [ ] IntelliJ IDEA configured
- [ ] Basic mod structure created
- [ ] Mod loads without errors
- [ ] First block placed programmatically

### **Week 3-4 Checklist:**
- [ ] JSON protocol designed (see next section)
- [ ] DCSS export function created
- [ ] Minecraft JSON parser implemented
- [ ] Test data successfully transferred

### **Week 5-8 Checklist:**
- [ ] 10x10 room exported from DCSS
- [ ] Block mapping system created
- [ ] Room rendered in Minecraft
- [ ] Player spawns correctly
- [ ] Screenshots/video of success

---

## 🔗 Next Steps: JSON Protocol Design

Once the mod loads successfully, we'll need to design the communication protocol between DCSS and Minecraft.

### **JSON Room Export Format (Draft)**

```json
{
  "room_id": "wasteland_arrival_ruined_house",
  "width": 10,
  "height": 10,
  "tiles": [
    ["wall", "wall", "wall", "wall", "wall", "wall", "wall", "wall", "wall", "wall"],
    ["wall", "floor", "floor", "floor", "floor", "floor", "floor", "floor", "floor", "wall"],
    ["wall", "floor", "floor", "floor", "floor", "floor", "floor", "floor", "floor", "wall"],
    ["wall", "floor", "floor", "floor", "player", "floor", "floor", "floor", "floor", "wall"],
    ["wall", "wall", "wall", "wall", "door", "wall", "wall", "wall", "wall", "wall"]
  ],
  "monsters": [
    {"type": "rat", "x": 3, "y": 2}
  ],
  "items": [
    {"type": "potion", "x": 5, "y": 3}
  ]
}
```

### **Minecraft Block Mapping**

```
DCSS Tile          → Minecraft Block
-----------------------------------------
floor (gray)       → Gray Concrete
wall (stone)       → Stone Bricks
door (closed)      → Oak Door
door (open)        → Air + Door (open state)
stairs_down        → Ladder
stairs_up          → Ladder (going up)
altar              → Enchanting Table
fountain           → Cauldron (with water)
```

---

## 🎯 Phase 2 Success Criteria

### **Minimum Viable Deliverable:**
✅ Minecraft mod loads without errors
✅ Can place blocks programmatically
✅ Can read JSON room data
✅ Can render 10x10 room with walls and floors
✅ Player spawns in correct position

### **Stretch Goals:**
⭐ Render doors with correct open/closed state
⭐ Place monster entities (even if non-functional)
⭐ Use extracted DCSS tiles as textures

---

## 📚 Resources

### **Forge Documentation:**
- Official Docs: https://docs.minecraftforge.net/
- Community Wiki: https://forge.gemwire.uk/wiki/Main_Page
- Tutorials: https://tutorials.darkhax.net/

### **Minecraft Modding:**
- Forge Forums: https://forums.minecraftforge.net/
- Modding Discord: https://discord.gg/UvedJ9m

### **Java/IntelliJ:**
- IntelliJ Shortcuts: https://www.jetbrains.com/help/idea/
- Java 17 Features: https://openjdk.org/projects/jdk/17/

---

## 🐛 Common Issues

### **Issue: "java: error: invalid target release: 17"**
**Fix**: Set Java version in `build.gradle`:
```gradle
java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(17)
    }
}
```

### **Issue: Minecraft crashes on launch**
**Fix**: Check logs in `run/logs/latest.log`, usually missing dependency

### **Issue: Mod doesn't appear in mods list**
**Fix**: Verify `mods.toml` syntax, check MOD_ID matches

---

## 📅 Timeline Estimate

```
Week 1:  Java + Forge setup                    (4-8 hours)
Week 2:  IntelliJ + basic mod structure        (4-6 hours)
Week 3:  JSON protocol + DCSS export           (6-10 hours)
Week 4:  Minecraft JSON parser                 (4-6 hours)
Week 5:  Block mapping system                  (6-8 hours)
Week 6:  Room rendering implementation         (8-12 hours)
Week 7:  Testing and debugging                 (4-8 hours)
Week 8:  Polish and documentation              (2-4 hours)

TOTAL: 38-62 hours over 8 weeks
```

---

## 🎉 When Phase 2 is Complete

You will have:
- ✅ Working Minecraft mod environment
- ✅ DCSS-to-Minecraft communication pipeline
- ✅ Proof of concept: DCSS room in 3D
- ✅ Foundation for Phase 3 (full dungeon support)

**Screenshot Goal**: Standing in a Minecraft-rendered DCSS room with stone walls, gray floors, and a door visible.

---

## 🚀 Ready to Begin?

### **First Command to Run:**

```bash
java -version
```

If you see Java 17, you're ready to download Forge MDK!

If not:
```bash
brew install openjdk@17
```

---

*Phase 2 Start Date: December 2, 2025*
*Target Completion: February 2026*
*Status: READY TO BEGIN*

**Let's render that first room!** 🌍☢️⚔️
