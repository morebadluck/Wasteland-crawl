package com.wasteland.worldgen;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeSource;
import net.minecraft.world.level.biome.Climate;

import java.util.stream.Stream;

/**
 * Custom BiomeSource that creates a stylized USA map layout.
 * Maps world coordinates to appropriate biomes based on USA geography.
 */
public class USABiomeSource extends BiomeSource {
    public static final Codec<USABiomeSource> CODEC = RecordCodecBuilder.create(
            (instance) -> instance.group(
                    Codec.LONG.fieldOf("seed").forGetter((source) -> source.seed)
            ).apply(instance, USABiomeSource::new)
    );

    private final long seed;
    private final SimplexNoise noise;

    public USABiomeSource(long seed) {
        this.seed = seed;
        this.noise = new SimplexNoise(seed);
    }

    @Override
    protected Codec<? extends BiomeSource> codec() {
        return CODEC;
    }

    @Override
    protected Stream<Holder<Biome>> collectPossibleBiomes() {
        // Return all biomes that might be used
        // This is required for Minecraft to properly initialize the world
        return Stream.of(
                // All possible biomes from USARegion
                net.minecraft.world.level.biome.Biomes.FOREST,
                net.minecraft.world.level.biome.Biomes.TAIGA,
                net.minecraft.world.level.biome.Biomes.WINDSWEPT_HILLS,
                net.minecraft.world.level.biome.Biomes.SWAMP,
                net.minecraft.world.level.biome.Biomes.BEACH,
                net.minecraft.world.level.biome.Biomes.PLAINS,
                net.minecraft.world.level.biome.Biomes.SUNFLOWER_PLAINS,
                net.minecraft.world.level.biome.Biomes.RIVER,
                net.minecraft.world.level.biome.Biomes.DESERT,
                net.minecraft.world.level.biome.Biomes.BADLANDS,
                net.minecraft.world.level.biome.Biomes.ERODED_BADLANDS,
                net.minecraft.world.level.biome.Biomes.WINDSWEPT_GRAVELLY_HILLS,
                net.minecraft.world.level.biome.Biomes.STONY_PEAKS,
                net.minecraft.world.level.biome.Biomes.WINDSWEPT_FOREST,
                net.minecraft.world.level.biome.Biomes.OLD_GROWTH_PINE_TAIGA,
                net.minecraft.world.level.biome.Biomes.MANGROVE_SWAMP,
                net.minecraft.world.level.biome.Biomes.OCEAN,
                net.minecraft.world.level.biome.Biomes.STONY_SHORE
        ).map(this::getBiomeHolder);
    }

    @Override
    public Holder<Biome> getNoiseBiome(int quartX, int quartY, int quartZ, Climate.Sampler sampler) {
        // Convert quarter-block coordinates to block coordinates
        // Minecraft uses quarter-block coordinates (divide by 4 to get actual blocks)
        int x = quartX << 2;
        int z = quartZ << 2;

        // Get the region for this coordinate
        USARegion region = USARegion.getRegion(x, z);

        // Use noise to select which biome within the region
        double noiseValue = (noise.noise(x * 0.001, z * 0.001) + 1.0) / 2.0; // Normalize to 0-1

        // Get biome from region
        ResourceKey<Biome> biomeKey = region.getBiome(noiseValue);

        return getBiomeHolder(biomeKey);
    }

    /**
     * Get a biome holder for a biome key
     * In 1.20.1, biomes are accessed through the registry
     */
    private Holder<Biome> getBiomeHolder(ResourceKey<Biome> biomeKey) {
        // This is a simplified version - in practice, you'd get this from the level's biome registry
        // For now, we'll use direct holders
        return Holder.direct(null); // TODO: Fix this to properly resolve biomes
    }

    /**
     * Simple Simplex noise generator for biome variation
     */
    private static class SimplexNoise {
        private final long seed;

        public SimplexNoise(long seed) {
            this.seed = seed;
        }

        public double noise(double x, double z) {
            // Very simple noise implementation
            // This should be replaced with proper simplex noise for production
            double value = Math.sin(x + seed) * Math.cos(z + seed);
            return Math.max(-1.0, Math.min(1.0, value));
        }
    }
}
