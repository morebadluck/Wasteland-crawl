# Wasteland Crawl - Complete Build Instructions

Step-by-step guide to building and running Wasteland Crawl with Docker.

## 🎯 Choose Your Method

### ⚡ Fastest: Automated Script (Recommended)

```bash
./run-wasteland.sh
```

**Done!** The script handles everything automatically.

---

### 🔧 Manual: Step-by-Step

If you prefer to understand what's happening:

#### Step 1: Install Docker

**macOS:**
1. Download [Docker Desktop for Mac](https://www.docker.com/products/docker-desktop)
2. Install and start Docker Desktop
3. Verify: `docker --version`

**Linux (Ubuntu/Debian):**
```bash
sudo apt-get update
sudo apt-get install docker.io docker-compose
sudo usermod -aG docker $USER
# Log out and back in
docker --version
```

**Windows:**
1. Download [Docker Desktop for Windows](https://www.docker.com/products/docker-desktop)
2. Ensure WSL2 is enabled
3. Install and start Docker Desktop
4. Verify: `docker --version`

#### Step 2: Clone/Download the Game

```bash
# If you have git
git clone <repository-url> wasteland-crawl
cd wasteland-crawl

# Or extract from ZIP
unzip wasteland-crawl.zip
cd wasteland-crawl
```

#### Step 3: Build the Docker Image

```bash
# Build (takes 5-15 minutes)
docker build -t wasteland-crawl:latest .

# You'll see output like:
# Sending build context to Docker daemon...
# Step 1/8 : FROM ubuntu:22.04
# Step 2/8 : ENV DEBIAN_FRONTEND=noninteractive
# ...
# Successfully built abc123def456
# Successfully tagged wasteland-crawl:latest
```

**Build Progress:**
- ⏱️ Time: 5-15 minutes
- 💾 Size: ~500MB-1GB
- 📶 Internet required (downloads dependencies)

#### Step 4: Create Saves Directory

```bash
mkdir -p saves
```

This is where your characters and progress are saved.

#### Step 5: Run the Game

```bash
docker run -it --rm -v $(pwd)/saves:/root/.crawl wasteland-crawl:latest
```

**Command breakdown:**
- `docker run` - Run a container
- `-it` - Interactive mode with terminal
- `--rm` - Remove container when done
- `-v $(pwd)/saves:/root/.crawl` - Mount saves directory
- `wasteland-crawl:latest` - Image to run

#### Step 6: Play!

The game will start automatically. Use arrow keys or vim keys (hjkl) to move.

Press `?` for in-game help.

---

## 🧪 Testing Your Build

Use the automated test script:

```bash
./test-docker.sh
```

This will:
- ✓ Check Docker installation
- ✓ Check Docker daemon is running
- ✓ Check disk space
- ✓ Build the image
- ✓ Test the image
- ✓ Create saves directory

---

## 🐳 Using Docker Compose (Alternative)

Docker Compose makes it even easier:

```bash
# First time: Build and run
docker-compose up

# After first build: Just run
docker-compose run --rm wasteland-crawl

# Stop everything
docker-compose down
```

---

## 📊 Build Verification

After building, verify your image:

```bash
# Check image exists
docker images wasteland-crawl

# Should show:
# REPOSITORY          TAG       IMAGE ID       CREATED         SIZE
# wasteland-crawl     latest    abc123def456   2 minutes ago   850MB

# Check image details
docker inspect wasteland-crawl:latest
```

---

## 🎮 Running the Game - All Methods

### Method 1: Direct Docker Run (Most Control)
```bash
docker run -it --rm -v $(pwd)/saves:/root/.crawl wasteland-crawl:latest
```

### Method 2: Docker Compose (Simplest)
```bash
docker-compose run --rm wasteland-crawl
```

### Method 3: Launch Script (Automated)
```bash
./run-wasteland.sh
```

### Method 4: Create an Alias (Most Convenient)
```bash
# Add to ~/.bashrc or ~/.zshrc
alias wasteland='docker run -it --rm -v $(pwd)/saves:/root/.crawl wasteland-crawl:latest'

# Then just type:
wasteland
```

---

## 🔍 Build Troubleshooting

### Problem: "Cannot connect to Docker daemon"

**Cause:** Docker is not running

**Solution:**
```bash
# macOS/Windows: Start Docker Desktop from Applications

# Linux:
sudo systemctl start docker
sudo systemctl enable docker  # Start on boot

# Verify:
docker ps
```

---

### Problem: Build fails at package installation

**Example error:**
```
E: Unable to locate package libncursesw5-dev
```

**Solution:**
```bash
# Build with --no-cache to force fresh package lists
docker build --no-cache -t wasteland-crawl .
```

---

### Problem: "no space left on device"

**Solution:**
```bash
# Clean up Docker
docker system prune -a

# Remove unused images
docker image prune -a

# Check space
df -h

# Remove old containers
docker container prune
```

---

### Problem: Build is very slow

**Causes & Solutions:**

1. **Slow internet:** Package downloads take time
   - Wait it out or use a faster connection
   - Subsequent builds use cache (faster)

2. **Slow CPU:** Compilation is CPU-intensive
   - Use `-j` flag: edit Dockerfile line to `make -j2` for 2 cores

3. **Antivirus interference:** (Windows)
   - Temporarily disable antivirus during build
   - Add Docker to antivirus exclusions

---

### Problem: "error checking context: can't stat '/Users/.../node_modules'"

**Solution:**
```bash
# Create/update .dockerignore
cat >> .dockerignore << EOF
node_modules
*.log
.git
EOF
```

---

### Problem: Permission denied on saves

**Linux only:**
```bash
# Fix ownership
sudo chown -R $USER:$USER saves/

# Or run Docker as your user
docker run -it --rm --user $(id -u):$(id -g) \
  -v $(pwd)/saves:/root/.crawl wasteland-crawl:latest
```

---

## ⚙️ Build Customization

### Build Console Version (Default)
```bash
docker build -t wasteland-crawl:console .
```

### Build Tiles Version (Graphical)
```dockerfile
# Edit Dockerfile, change:
RUN make -j$(nproc) TILES=n
# To:
RUN make -j$(nproc) TILES=y
```

```bash
docker build -t wasteland-crawl:tiles .
```

**Note:** Tiles version requires X11 forwarding setup.

### Build Debug Version
```dockerfile
# Edit Dockerfile, change:
RUN make -j$(nproc) TILES=n
# To:
RUN make -j$(nproc) TILES=n DEBUG=y
```

### Optimize Build Speed
```dockerfile
# Use more CPU cores
RUN make -j8 TILES=n  # Use 8 cores

# Or automatically use all cores (default)
RUN make -j$(nproc) TILES=n
```

---

## 📦 Distributing Your Image

### Save Image to File

```bash
# Create distributable file
docker save wasteland-crawl:latest | gzip > wasteland-crawl-docker.tar.gz

# Size: ~300-500MB compressed
```

### Load Image from File

```bash
# Import the image
gunzip -c wasteland-crawl-docker.tar.gz | docker load

# Verify
docker images wasteland-crawl
```

### Share via Docker Hub

```bash
# Tag for Docker Hub
docker tag wasteland-crawl:latest yourusername/wasteland-crawl:latest

# Login to Docker Hub
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

## 🏗️ Understanding the Build Process

### What Happens During Build:

1. **Base Image** (10 seconds)
   - Downloads Ubuntu 22.04
   - ~80MB download

2. **Install Dependencies** (3-5 minutes)
   - Updates package lists
   - Installs build tools
   - Installs DCSS dependencies
   - ~200MB of packages

3. **Copy Source Code** (5 seconds)
   - Copies game files into image
   - ~50MB of source

4. **Compile Game** (5-10 minutes)
   - Compiles C++ source
   - Links libraries
   - Creates executable
   - Most time-consuming step

5. **Finalize** (5 seconds)
   - Sets entry point
   - Creates final layers
   - Tags image

**Total:** 8-15 minutes on average hardware

---

## ✅ Post-Build Checklist

After building, verify:

- [ ] Image exists: `docker images wasteland-crawl`
- [ ] Image size reasonable: ~500MB-1GB
- [ ] Saves directory exists: `ls saves/`
- [ ] Can run game: `docker run -it --rm wasteland-crawl:latest`
- [ ] Can see title screen
- [ ] Can create character
- [ ] Saves persist between runs

---

## 🎓 Build Best Practices

1. **Use .dockerignore**
   - Excludes unnecessary files
   - Speeds up builds
   - Reduces image size

2. **Tag your builds**
   ```bash
   docker build -t wasteland-crawl:v1.0 .
   docker build -t wasteland-crawl:latest .
   ```

3. **Clean up regularly**
   ```bash
   docker system prune -a
   ```

4. **Use build cache**
   - Don't use `--no-cache` unless needed
   - Order Dockerfile instructions efficiently

5. **Test before distributing**
   ```bash
   ./test-docker.sh
   ```

---

## 🚀 Next Steps

After successful build:

1. **Read the Quick Start:** `QUICKSTART.md`
2. **Learn Docker commands:** `DOCKER.md`
3. **Play the game!**

---

## 💾 Backup Strategy

After playing:

```bash
# Backup your saves
tar -czf wasteland-saves-$(date +%Y%m%d).tar.gz saves/

# Store backups somewhere safe
mv wasteland-saves-*.tar.gz ~/Backups/
```

---

## 🎉 Success!

If you've made it this far, you should have:

- ✅ Working Docker installation
- ✅ Built Wasteland Crawl image
- ✅ Saves directory created
- ✅ Ability to run the game

**Now go survive the wasteland!**

---

## 📚 Additional Resources

- [Docker Quick Reference](DOCKER_QUICK_REFERENCE.md) - Command cheat sheet
- [Complete Docker Guide](DOCKER.md) - In-depth documentation
- [Main Game Documentation](WASTELAND.md) - Game features and lore
- [Procedural Generation Guide](PROCEDURAL_GENERATION.md) - How it works

---

## ❓ Still Having Issues?

1. Check [DOCKER.md](DOCKER.md) troubleshooting section
2. Verify Docker is running: `docker ps`
3. Try the test script: `./test-docker.sh`
4. Check disk space: `df -h`
5. Try clean build: `docker build --no-cache -t wasteland-crawl .`

**Remember:** The game is based on DCSS - proven technology that works!
