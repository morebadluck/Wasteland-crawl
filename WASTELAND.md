# Wasteland Crawl: Ruins of America

A post-apocalyptic roguelike based on Dungeon Crawl Stone Soup, set in the irradiated ruins of the United States.

## 🌍 Setting

The year is 2157. Over a century has passed since the Great Collapse - a devastating combination of nuclear war, biological warfare, and environmental catastrophe that brought civilization to its knees. The once-mighty United States is now a vast wasteland of crumbling cities, toxic zones, and lawless territories.

In this harsh new world, the creatures of myth and nightmare have emerged from the shadows. Whether they were always hiding in the dark corners of reality, or whether the radiation and strange energies unleashed by the apocalypse brought them into being, no one knows. What was once called a "dragon" or "demon" now stalks the ruins alongside mutated wildlife and desperate human survivors.

## 🎮 Game Features

### First-Person Perspective with Tactical Minimap
- **Immersive first-person view** - Experience the wasteland from ground level
- **Tactical ASCII minimap** - Classic roguelike top-down view for strategic planning
- **Traditional DCSS mechanics** - All the deep, tactical gameplay you know and love

### Procedurally Generated Wastelands
The game features multiple distinct wasteland zones:

- **The Ruins** (Main Dungeon) - Collapsed cities and suburbs, 15 levels deep into underground metro systems and bunkers
- **The Toxlands** (Swamp/Lair equivalent) - Irradiated wilderness where nature has mutated beyond recognition
- **The Undercroft** (Crypt) - Mass graves and underground shelter systems from the Great Collapse
- **The Bunker Complexes** (Vaults) - Pre-war military installations, heavily fortified and still partially operational
- **Corporate Towers** (Elf Halls) - The gleaming ruins of mega-corporation headquarters
- **Raider Camps** (Orc Mines) - Brutal settlements of wasteland marauders
- **The Glowing Crater** (Hell branches) - Ground zero of the nuclear strikes, where reality itself has broken down
- **The Deep Dark** (Abyss) - Spaces between realities, torn open by the apocalypse

### Adapted Factions
The old gods are dead, but new powers have risen:

- **The Remnants** - Preservers of old world technology and knowledge
- **The Radiant Host** - Worshippers of radiation as divine power
- **The Iron Covenant** - Masters of mechanical augmentation and AI
- **The Verdant Circle** - Those who commune with the mutated new nature
- **The Void Walkers** - Explorers of dimensional rifts and strange physics
- **The Warlords** - Brutal raiders who take what they need by force

### Equipment & Items
- **Scavenged Weapons** - From makeshift pipe guns to pre-war military hardware
- **Armor** - Improvised protection, tactical gear, and power armor frames
- **Tech Items** - Stimpacks, rad-away, energy cells, and mysterious pre-war devices
- **Augmentations** - Cybernetic implants and biological mutations

## 🚀 Quick Start with Docker

### Prerequisites
- Docker and Docker Compose installed on your system
- At least 2GB of free disk space

### Running the Game

1. **Clone the repository**
```bash
git clone <repository-url>
cd crawl
```

2. **Build and run with Docker Compose**
```bash
docker-compose up
```

3. **Or build and run manually**
```bash
# Build the image
docker build -t wasteland-crawl .

# Run the game
docker run -it --rm wasteland-crawl
```

4. **For persistent save games**
```bash
docker run -it --rm -v $(pwd)/saves:/root/.crawl wasteland-crawl
```

## 🛠️ Building from Source

### Linux/Mac

```bash
cd crawl-ref/source
make TILES=y
./crawl
```

### Build Options
- `make TILES=y` - Build with graphical tiles
- `make TILES=n` - Build console/ASCII version
- `make DEBUG=y` - Build with debug symbols
- `make WEBTILES=y` - Build for web-based play

## 🎯 How to Play

### Basic Controls
- **Arrow keys / hjkl** - Move in 8 directions
- **TAB** - Cycle through visible enemies
- **o** - Auto-explore
- **G** - Auto-travel to location
- **i** - Inventory
- **a** - Use/apply item
- **w** - Wield weapon
- **W** - Wear armor
- **z** - Cast spell/use ability
- **>** - Go down stairs
- **<** - Go up stairs
- **5 / .** - Rest/wait

### First-Person Mode
- **v** - Toggle between first-person and traditional overhead view
- **Look around** - In first-person mode, use direction keys to look
- **Minimap** - Always visible in corner showing tactical situation

### Survival Tips
1. **Start slow** - The early Ruins are dangerous enough
2. **Scavenge everything** - Resources are scarce in the wasteland
3. **Choose your faction** - Different factions offer different survival strategies
4. **Manage radiation** - Some areas will slowly poison you
5. **Food is scarce** - Hunt mutated creatures or find pre-war rations
6. **Save your ammo** - Bullets and energy cells are precious

## 📋 Game Modes

- **Standard Game** - Full wasteland experience with permadeath
- **Tutorial** - Learn the basics in a safe(r) environment
- **Sprint** - Short, intense scenarios in specific locations
- **Arena** - Test combat between different wasteland creatures

## 🎨 Customization

The game supports extensive customization through `.rc` files:

```bash
# Copy the default config
cp crawl-ref/source/dat/defaults/init.txt ~/.crawl/init.txt

# Edit with your preferred settings
nano ~/.crawl/init.txt
```

## 🐛 Known Issues

- First-person view is still in development (currently plays as standard DCSS)
- Some faction abilities reference old fantasy themes (will be updated)
- Tile graphics still show fantasy themes (ASCII recommended for immersion)

## 🤝 Contributing

This is a modification of Dungeon Crawl Stone Soup. Contributions welcome!

### Areas for Development
- [ ] Custom tile graphics for post-apocalyptic theme
- [ ] First-person rendering mode
- [ ] New vault designs (ruined buildings, bunkers, etc.)
- [ ] Faction-specific abilities and tech trees
- [ ] Post-apocalyptic item descriptions
- [ ] Wasteland-themed monster speech and flavor text

## 📚 More Information

- Original DCSS: https://crawl.develz.org/
- DCSS Documentation: https://github.com/crawl/crawl/tree/master/crawl-ref/docs
- Roguelike Tutorial: Press `?` in-game for help

## ⚖️ License

Based on Dungeon Crawl Stone Soup, licensed under GPLv2+. See LICENSE file for details.

## 🎮 Game Philosophy

This mod maintains DCSS's core philosophy:
- **Challenging but fair** - Every death is a learning experience
- **No grinding** - Player skill over time investment
- **Tactical depth** - Every decision matters
- **Procedural generation** - Every run is unique
- **Permadeath** - Stakes that make victory meaningful

Welcome to the Wasteland, survivor. Make it count.

---

*"In the ruins of the old world, the strong take what they need, and the clever survive to take more."*
