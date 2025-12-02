# Wasteland Crawl - Dockerfile
# Based on Dungeon Crawl Stone Soup
FROM ubuntu:22.04

# Prevent interactive prompts during build
ENV DEBIAN_FRONTEND=noninteractive

# Install build dependencies
RUN apt-get update && apt-get install -y \
    build-essential \
    libncursesw5-dev \
    bison \
    flex \
    liblua5.1-0-dev \
    libsqlite3-dev \
    libz-dev \
    pkg-config \
    python3-yaml \
    binutils-gold \
    python-is-python3 \
    git \
    # Tiles dependencies (optional but recommended)
    libsdl2-image-dev \
    libsdl2-mixer-dev \
    libsdl2-dev \
    libfreetype6-dev \
    libpng-dev \
    fonts-dejavu-core \
    advancecomp \
    pngcrush \
    # Cleanup
    && rm -rf /var/lib/apt/lists/*

# Set working directory
WORKDIR /app

# Copy source code
COPY . /app/

# Build the game (console version for better compatibility)
# Change TILES=n to TILES=y if you want graphical tiles
WORKDIR /app/crawl-ref/source
RUN make -j$(nproc) TILES=n

# Create save directory
RUN mkdir -p /root/.crawl

# Set the entrypoint to run the game
ENTRYPOINT ["./crawl"]

# Default to console mode
CMD []

# Labels
LABEL maintainer="Wasteland Crawl Team"
LABEL description="Wasteland Crawl: A post-apocalyptic roguelike based on DCSS"
LABEL version="1.0"
