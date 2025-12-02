# Wasteland Crawl - Docker Quick Reference

## ⚡ Super Quick Start

```bash
./run-wasteland.sh
```

That's it! Everything else is automatic.

---

## 🔨 Manual Commands

### First Time Setup

```bash
# 1. Build the image (5-15 minutes)
docker build -t wasteland-crawl .

# 2. Create saves directory
mkdir -p saves

# 3. Run the game
docker run -it --rm -v $(pwd)/saves:/root/.crawl wasteland-crawl
```

### Every Time After

```bash
# Just run
docker run -it --rm -v $(pwd)/saves:/root/.crawl wasteland-crawl
```

---

## 🐳 Docker Compose Method

```bash
# Build and run (first time)
docker-compose up

# Run (after first build)
docker-compose run --rm wasteland-crawl
```

---

## 💾 Save Games

**Your saves are in**: `./saves/`

**Backup:**
```bash
tar -czf backup-$(date +%Y%m%d).tar.gz saves/
```

**Restore:**
```bash
tar -xzf backup-20231215.tar.gz
```

---

## 🔧 Common Tasks

### Rebuild Image
```bash
docker build -t wasteland-crawl . --no-cache
```

### Clean Up Docker
```bash
docker system prune -a
```

### View Save Files
```bash
ls -la saves/saves/
```

### Delete Everything and Start Over
```bash
docker rmi wasteland-crawl
rm -rf saves/
docker build -t wasteland-crawl .
```

---

## 🐛 Quick Fixes

### "Cannot connect to Docker daemon"
```bash
# macOS/Windows: Start Docker Desktop
# Linux:
sudo systemctl start docker
```

### "Permission denied"
```bash
chmod -R 755 saves/
```

### "No space left on device"
```bash
docker system prune -a
```

### Terminal Issues
```bash
# Use this run command instead:
docker run -it --rm -e TERM=xterm-256color \
  -v $(pwd)/saves:/root/.crawl wasteland-crawl
```

---

## 🎮 Game Controls

| Key | Action |
|-----|--------|
| **Arrow keys / hjkl** | Move |
| **?** | Help |
| **o** | Auto-explore |
| **S** | Save & quit |
| **i** | Inventory |
| **>** / **<** | Stairs |

---

## 📂 File Locations

```
crawl/
├── Dockerfile              ← Build instructions
├── docker-compose.yml      ← Easy runner
├── run-wasteland.sh        ← Automated script
└── saves/                  ← YOUR SAVE GAMES
    ├── saves/              ← Active characters
    ├── morgue/             ← Death records
    └── .crawlrc            ← Config file
```

---

## 🚨 Emergency Commands

### Game Froze
```bash
# Press Ctrl+C to exit
# Your progress is saved automatically
```

### Lost Your Saves
```bash
# If you backed up:
tar -xzf backup-20231215.tar.gz

# If you didn't... start a new character
# (This is a roguelike - death is permanent anyway!)
```

### Docker is Broken
```bash
# Complete reset
docker system prune -a -f
docker volume prune -f

# Reinstall Docker Desktop (macOS/Windows)
# or
sudo apt-get reinstall docker.io  # Linux
```

---

## 🎯 Copy-Paste Commands

### Complete Setup (One Time)
```bash
docker build -t wasteland-crawl . && mkdir -p saves
```

### Play (Every Time)
```bash
docker run -it --rm -v $(pwd)/saves:/root/.crawl wasteland-crawl
```

### With Alias (Add to ~/.bashrc)
```bash
alias wasteland='docker run -it --rm -v $(pwd)/saves:/root/.crawl wasteland-crawl'
```
Then just type: `wasteland`

---

## 🏆 Pro Tips

1. **Always use the saves volume** or you'll lose your character
2. **Backup before major updates**
3. **Use docker-compose** for easier management
4. **Read DOCKER.md** for detailed info

---

## 📞 Need More Help?

- **Full Guide**: See `DOCKER.md`
- **Game Help**: Press `?` in-game
- **Quick Start**: See `QUICKSTART.md`
- **Main Docs**: See `WASTELAND.md`

---

**Remember**: This is a roguelike. Death is permanent. Have fun!
