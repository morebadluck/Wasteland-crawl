# Wasteland Crawl - Docker Guide

Complete instructions for building and running Wasteland Crawl using Docker.

## 📋 Prerequisites

### 1. Install Docker

**macOS:**
```bash
# Download and install Docker Desktop
# https://www.docker.com/products/docker-desktop

# Or via Homebrew
brew install --cask docker
```

**Linux (Ubuntu/Debian):**
```bash
# Install Docker
sudo apt-get update
sudo apt-get install docker.io docker-compose

# Add your user to docker group (logout/login after)
sudo usermod -aG docker $USER
```

**Windows:**
```bash
# Download and install Docker Desktop
# https://www.docker.com/products/docker-desktop
# Ensure WSL2 is enabled
```

### 2. Verify Installation

```bash
docker --version
# Should show: Docker version 20.x.x or higher

docker-compose --version
# Should show: docker-compose version 1.x.x or higher
```

---

## 🚀 Quick Start (Easiest Method)

### Method 1: Using the Launch Script

```bash
# Navigate to the wasteland-crawl directory
cd /path/to/crawl

# Run the automated script
./run-wasteland.sh
```

This script will:
- Check Docker installation
- Create saves directory
- Build the Docker image
- Launch the game

**That's it!** Your save games are stored in `./saves/` and persist between sessions.

---

## 🔨 Manual Docker Commands

### Build the Docker Image

```bash
# From the crawl directory
docker build -t wasteland-crawl:latest .
```

**Build time:** 5-15 minutes depending on your system
**Image size:** Approximately 500MB-1GB

#### Build Progress

You'll see output like:
```
Step 1/8 : FROM ubuntu:22.04
Step 2/8 : ENV DEBIAN_FRONTEND=noninteractive
Step 3/8 : RUN apt-get update && apt-get install -y ...
...
Successfully built abc123def456
Successfully tagged wasteland-crawl:latest
```

### Run the Game

```bash
# Basic run (no save persistence)
docker run -it --rm wasteland-crawl:latest

# With persistent saves (recommended)
docker run -it --rm -v $(pwd)/saves:/root/.crawl wasteland-crawl:latest

# On Windows PowerShell, use:
docker run -it --rm -v ${PWD}/saves:/root/.crawl wasteland-crawl:latest
```

### Using Docker Compose (Recommended)

```bash
# Build and run in one command
docker-compose up

# Build only
docker-compose build

# Run (after building)
docker-compose run --rm wasteland-crawl

# Stop all containers
docker-compose down
```

---

## 📂 Save Game Management

### Where Saves Are Stored

**On Host System:**
```
./saves/              # Your local directory
├── morgue/          # Character death records
├── saves/           # Active save files
└── .crawlrc         # Configuration file
```

**Inside Container:**
```
/root/.crawl/        # Container directory (mapped to ./saves/)
```

### Backup Your Saves

```bash
# Create a backup
tar -czf wasteland-saves-$(date +%Y%m%d).tar.gz saves/

# Restore from backup
tar -xzf wasteland-saves-20231215.tar.gz
```

### View Your Save Files

```bash
# List saves
ls -la saves/saves/

# View morgue files (death records)
cat saves/morgue/*.txt
```

---

## 🎮 Advanced Usage

### Custom Build Options

#### Build Tiles Version (Graphical)

Edit the `Dockerfile` and change:
```dockerfile
# Change this line:
RUN make -j$(nproc) TILES=n

# To:
RUN make -j$(nproc) TILES=y
```

Then rebuild:
```bash
docker build -t wasteland-crawl:tiles .
```

**Note:** Running tiles in Docker requires X11 forwarding (advanced).

#### Debug Build

```bash
docker build -t wasteland-crawl:debug --build-arg BUILD_TYPE=debug .
```

### Multiple Versions

```bash
# Build different versions with tags
docker build -t wasteland-crawl:v1.0 .
docker build -t wasteland-crawl:experimental .

# Run specific version
docker run -it --rm wasteland-crawl:v1.0
```

### Interactive Shell (for debugging)

```bash
# Enter container shell without starting game
docker run -it --rm wasteland-crawl:latest /bin/bash

# Inside container, you can:
cd /app/crawl-ref/source
./crawl
# or explore the filesystem
```

---

## 🐛 Troubleshooting

### Problem: "Cannot connect to Docker daemon"

**Solution:**
```bash
# Start Docker service (Linux)
sudo systemctl start docker

# Or start Docker Desktop (macOS/Windows)
# Check system tray for Docker icon
```

### Problem: "Permission denied" on saves directory

**Solution:**
```bash
# Fix permissions
chmod -R 755 saves/

# Or on Linux, match container user
sudo chown -R $USER:$USER saves/
```

### Problem: Build fails with "no space left on device"

**Solution:**
```bash
# Clean up Docker
docker system prune -a

# Remove old images
docker image prune -a

# Check disk space
df -h
```

### Problem: Terminal issues (garbled display)

**Solution:**
```bash
# Set terminal type
docker run -it --rm -e TERM=xterm-256color wasteland-crawl:latest

# Or add to docker-compose.yml:
environment:
  - TERM=xterm-256color
```

### Problem: Game runs but controls don't work

**Solution:**
- Ensure your terminal supports interactive mode
- Use a proper terminal (not a basic text editor terminal)
- Try different terminal emulators (iTerm2, Windows Terminal, etc.)

### Problem: Cannot see save files

**Solution:**
```bash
# Check if volume is mounted correctly
docker run -it --rm -v $(pwd)/saves:/root/.crawl wasteland-crawl:latest ls -la /root/.crawl

# Verify saves directory exists
mkdir -p saves
```

---

## 🔧 Configuration

### Create Custom Configuration

```bash
# Create saves directory
mkdir -p saves

# Create custom config
cat > saves/.crawlrc << 'EOF'
# Wasteland Crawl Configuration

# Auto-pickup
autopickup = $?!+"/%

# Travel settings
travel_delay = -1
explore_delay = -1

# Display
tile_full_screen = false
tile_window_width = 1920
tile_window_height = 1080

# Add your custom settings here
EOF
```

### Environment Variables

```bash
# Run with custom settings
docker run -it --rm \
  -e TERM=xterm-256color \
  -e CRAWL_NAME="YourName" \
  -v $(pwd)/saves:/root/.crawl \
  wasteland-crawl:latest
```

---

## 📊 Docker Image Details

### Image Information

```bash
# View image details
docker images wasteland-crawl

# View image history
docker history wasteland-crawl:latest

# Inspect image
docker inspect wasteland-crawl:latest
```

### Image Size Optimization

The image is optimized but can be further reduced:

```bash
# Multi-stage build (advanced)
# Edit Dockerfile to use multi-stage build
# Then rebuild:
docker build -t wasteland-crawl:slim -f Dockerfile.slim .
```

---

## 🌐 Sharing Your Image

### Save Image to File

```bash
# Export image
docker save wasteland-crawl:latest | gzip > wasteland-crawl.tar.gz

# Share the file
# Upload to file sharing service
```

### Load Image from File

```bash
# Import image
gunzip -c wasteland-crawl.tar.gz | docker load

# Verify
docker images wasteland-crawl
```

### Push to Docker Hub (Optional)

```bash
# Tag for Docker Hub
docker tag wasteland-crawl:latest yourusername/wasteland-crawl:latest

# Login
docker login

# Push
docker push yourusername/wasteland-crawl:latest
```

Then others can use:
```bash
docker pull yourusername/wasteland-crawl:latest
docker run -it --rm yourusername/wasteland-crawl:latest
```

---

## 🎯 Usage Scenarios

### Scenario 1: Quick Play Session

```bash
# No saves needed, just play
docker run -it --rm wasteland-crawl:latest
```

### Scenario 2: Persistent Character

```bash
# Save games persist
docker run -it --rm -v $(pwd)/saves:/root/.crawl wasteland-crawl:latest
```

### Scenario 3: Multiple Players on Same Machine

```bash
# Player 1
docker run -it --rm -v $(pwd)/saves-player1:/root/.crawl wasteland-crawl:latest

# Player 2
docker run -it --rm -v $(pwd)/saves-player2:/root/.crawl wasteland-crawl:latest
```

### Scenario 4: Testing/Development

```bash
# Mount source code for live editing
docker run -it --rm \
  -v $(pwd)/crawl-ref/source/dat:/app/crawl-ref/source/dat \
  wasteland-crawl:latest
```

---

## 📖 Command Reference

### Essential Commands

```bash
# Build image
docker build -t wasteland-crawl .

# Run game (ephemeral)
docker run -it --rm wasteland-crawl

# Run game (persistent saves)
docker run -it --rm -v $(pwd)/saves:/root/.crawl wasteland-crawl

# Use docker-compose
docker-compose up

# Stop containers
docker-compose down

# Rebuild
docker-compose build --no-cache

# View logs
docker-compose logs

# Remove image
docker rmi wasteland-crawl

# Clean everything
docker system prune -a
```

### Useful Aliases

Add to your `~/.bashrc` or `~/.zshrc`:

```bash
# Quick launch
alias wasteland='docker run -it --rm -v $(pwd)/saves:/root/.crawl wasteland-crawl:latest'

# Build
alias wasteland-build='docker build -t wasteland-crawl:latest .'

# Clean
alias wasteland-clean='docker system prune -a'
```

Then use:
```bash
wasteland          # Launch game
wasteland-build    # Rebuild image
wasteland-clean    # Clean Docker
```

---

## 🎓 Understanding the Dockerfile

Our Dockerfile does the following:

1. **Base Image**: Ubuntu 22.04 (stable, well-supported)
2. **Install Dependencies**: All DCSS build requirements
3. **Copy Source**: Game source code into container
4. **Build Game**: Compile the game (console version)
5. **Set Entry Point**: Automatically run the game on container start

```dockerfile
FROM ubuntu:22.04                    # Base OS
RUN apt-get install ...              # Install tools
COPY . /app/                         # Copy code
RUN make TILES=n                     # Build game
ENTRYPOINT ["./crawl"]               # Run game
```

---

## 🔒 Security Notes

- Container runs as root (standard for Docker)
- No network access required (game is offline)
- Save files are yours (stored on host)
- No telemetry or external connections
- Open source - audit the Dockerfile

---

## ✅ Best Practices

1. **Always use volumes for saves**
   ```bash
   -v $(pwd)/saves:/root/.crawl
   ```

2. **Use `--rm` flag** to auto-cleanup containers
   ```bash
   docker run -it --rm ...
   ```

3. **Tag your builds** for version control
   ```bash
   docker build -t wasteland-crawl:v1.0 .
   ```

4. **Back up saves regularly**
   ```bash
   tar -czf backup.tar.gz saves/
   ```

5. **Use docker-compose** for easier management
   ```bash
   docker-compose up
   ```

---

## 🎊 You're Ready!

The simplest way to play:

```bash
./run-wasteland.sh
```

Or manually:

```bash
docker build -t wasteland-crawl .
docker run -it --rm -v $(pwd)/saves:/root/.crawl wasteland-crawl
```

**Happy surviving in the wasteland!**

---

## 📚 Additional Resources

- [Docker Documentation](https://docs.docker.com/)
- [Docker Compose Documentation](https://docs.docker.com/compose/)
- [DCSS Build Instructions](https://github.com/crawl/crawl/blob/master/crawl-ref/INSTALL.md)
- [Wasteland Crawl Main README](WASTELAND.md)
- [Quick Start Guide](QUICKSTART.md)

---

## 💬 Need Help?

If you encounter issues:

1. Check the [Troubleshooting](#-troubleshooting) section
2. Verify Docker is running: `docker ps`
3. Check Docker logs: `docker-compose logs`
4. Try rebuilding: `docker-compose build --no-cache`
5. Check disk space: `df -h`

Still stuck? The game is based on DCSS - check their community resources in the main README.
