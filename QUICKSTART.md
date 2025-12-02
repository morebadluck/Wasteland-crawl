# Wasteland Crawl - Quick Start Guide

## 🚀 Fastest Way to Play

### Using Docker (Recommended)

```bash
# Clone and run in 3 commands
git clone <your-repo-url> wasteland-crawl
cd wasteland-crawl
./run-wasteland.sh
```

That's it! The script will:
- Build the Docker image
- Create a save directory
- Launch the game

Your save games will be stored in `./saves/` and persist between sessions.

---

## 🎮 First Time Playing?

When you start the game, you'll see the main menu:

1. **Press `c`** to start a new character
2. **Choose your species** - Try Human (simple) or pick something interesting
3. **Choose your background** - Try Fighter (easy) or Wanderer (varied)
4. **Start exploring!**

### Essential Controls

| Key | Action |
|-----|--------|
| **Arrow keys** or **hjkl** | Move in 8 directions |
| **?** | Help menu (use this!) |
| **o** | Auto-explore (finds unexplored areas) |
| **G** | Auto-travel to any location |
| **TAB** | Cycle through visible enemies |
| **i** | Open inventory |
| **a** | Use/apply an item |
| **w** | Wield a weapon |
| **W** | Wear armour |
| **T** | Remove armour |
| **>** | Go down stairs |
| **<** | Go up stairs |
| **5** or **.** | Rest/wait one turn |
| **S** | Save and quit |

### Combat Tips

1. **Pick your fights** - Don't rush into groups of enemies
2. **Use the TAB key** to cycle targets and plan your attacks
3. **Run away** if overwhelmed - stairs are your friend
4. **Throw things** - Press `f` to fire missiles
5. **Rest carefully** - Press `5` repeatedly or hold Shift+5 to rest until healed

### Survival Tips

1. **Explore thoroughly** - Use `o` to auto-explore each level
2. **Don't be greedy** - Sometimes the best loot isn't worth the risk
3. **Learn from death** - Every death teaches you something
4. **Save consumables** - Potions and scrolls can save your life
5. **Read everything** - The game tells you important info in the message log

---

## 🔨 Building Locally (Without Docker)

If you prefer to build from source:

```bash
# Clone the repository
git clone <your-repo-url> wasteland-crawl
cd wasteland-crawl

# Run the build script
./build-local.sh

# After building, start the game
./start-wasteland.sh
```

### Manual Build

```bash
cd crawl-ref/source

# Console version (recommended)
make -j4 TILES=n

# Or tiles version (requires SDL2)
make -j4 TILES=y

# Run the game
./crawl
```

---

## 🎯 Game Modes

- **Normal Game** - The full experience with permadeath
- **Tutorial** (Press `t`) - Learn the basics in a safe environment
- **Sprint** (Press `s`) - Short, intense scenarios
- **Arena** (Press `a`) - Watch monsters fight each other

**New players should start with the Tutorial!**

---

## 🐛 Troubleshooting

### Docker Issues

**Problem**: Docker daemon not running
```bash
# macOS/Windows: Start Docker Desktop
# Linux:
sudo systemctl start docker
```

**Problem**: Permission denied
```bash
# Add yourself to docker group (Linux)
sudo usermod -aG docker $USER
# Then log out and back in
```

### Build Issues

**Problem**: Missing dependencies (Linux)
```bash
# Ubuntu/Debian
sudo apt install build-essential libncursesw5-dev bison flex \
  liblua5.1-0-dev libsqlite3-dev libz-dev pkg-config python3-yaml

# Fedora
sudo dnf install gcc gcc-c++ make bison flex ncurses-devel \
  compat-lua-devel sqlite-devel zlib-devel pkgconfig python3-yaml
```

**Problem**: Can't run on macOS
```bash
# Install Xcode Command Line Tools
xcode-select --install
```

---

## 📚 Learning More

- **In-game help**: Press `?` then navigate the help menus
- **Manual**: See `crawl-ref/docs/crawl_manual.rst`
- **FAQ**: Press `?Q` in-game
- **Full README**: See `WASTELAND.md`

---

## 🎲 Your First Run

Here's a recommended first character:

1. **Species**: Minotaur (high HP, good at fighting)
2. **Background**: Berserker (powerful abilities, straightforward)

This combo is:
- Forgiving for beginners
- Strong in early game
- Teaches you core combat mechanics

**Remember**: Death is permanent, but also the best teacher. Don't be discouraged - even experts die regularly!

---

## 🌟 Post-Apocalyptic Lore

You awaken in the Ruins, the collapsed remnants of what was once a great American city. The year is 2157, over a century after the Great Collapse brought civilization to its knees.

The creatures you'll encounter - called "dragons," "demons," and "goblins" by the old salvagers - are the mutated inhabitants of this new world. Whether they emerged from the radiation and chaos, or were always lurking in the dark corners of reality, no one knows anymore.

Your goal: Survive. Explore. Scavenge. And perhaps discover what really caused the apocalypse...

---

**Welcome to the Wasteland, survivor. Make it count.**
