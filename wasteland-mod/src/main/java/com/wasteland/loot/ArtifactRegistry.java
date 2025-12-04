package com.wasteland.loot;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Registry of all unique artifacts in the game.
 * Manages fixed/named artifacts with predefined properties and lore.
 */
public class ArtifactRegistry {
    private static final Logger LOGGER = LogManager.getLogger();
    private static final Map<String, UniqueArtifact> ARTIFACTS = new HashMap<>();
    private static final Random RANDOM = new Random();

    // Track spawned artifacts to prevent duplicates
    private static final Set<String> spawnedArtifacts = new HashSet<>();

    static {
        registerAllArtifacts();
    }

    /**
     * Register all unique artifacts
     */
    private static void registerAllArtifacts() {
        // === WEAPONS ===

        // The Scorching Blade - Early game fire weapon
        register(new UniqueArtifact.Builder("scorching_blade", "the Scorching Blade", UniqueArtifact.ArtifactType.WEAPON)
            .weaponType(WeaponType.LONG_SWORD)
            .enchantment(6)
            .property(ArtifactProperty.SLAYING, 4)
            .property(ArtifactProperty.RESIST_FIRE, 2)
            .property(ArtifactProperty.BRAND_FIRE, 1)
            .lore("Forged in the dying embers of the old world, this blade still burns with ancient fury.")
            .minDepth(3)
            .spawnWeight(1.0)
            .build());

        // Frostbite - Ice weapon
        register(new UniqueArtifact.Builder("frostbite", "Frostbite", UniqueArtifact.ArtifactType.WEAPON)
            .weaponType(WeaponType.GREAT_SWORD)
            .enchantment(7)
            .property(ArtifactProperty.SLAYING, 3)
            .property(ArtifactProperty.RESIST_COLD, 2)
            .property(ArtifactProperty.BRAND_COLD, 1)
            .property(ArtifactProperty.SEE_INVISIBLE, 1)
            .lore("This blade was recovered from a frozen vault deep underground. It never melts.")
            .minDepth(6)
            .spawnWeight(0.8)
            .build());

        // The Wastewalker - Survivor's weapon
        register(new UniqueArtifact.Builder("wastewalker", "the Wastewalker", UniqueArtifact.ArtifactType.WEAPON)
            .weaponType(WeaponType.SPEAR)
            .enchantment(5)
            .property(ArtifactProperty.ACCURACY, 5)
            .property(ArtifactProperty.RESIST_POISON, 1)
            .property(ArtifactProperty.REGENERATION, 1)
            .lore("A spear carried by the first survivors who walked the wastes. It has tasted the blood of countless mutants.")
            .minDepth(4)
            .spawnWeight(1.0)
            .build());

        // Thunderstrike - Lightning axe
        register(new UniqueArtifact.Builder("thunderstrike", "Thunderstrike", UniqueArtifact.ArtifactType.WEAPON)
            .weaponType(WeaponType.BATTLEAXE)
            .enchantment(8)
            .property(ArtifactProperty.SLAYING, 5)
            .property(ArtifactProperty.RESIST_ELECTRICITY, 2)
            .property(ArtifactProperty.BRAND_ELECTRICITY, 1)
            .property(ArtifactProperty.CURSE_NOISE, 1) // Makes noise when used
            .lore("This axe crackles with barely-contained electrical energy. Every strike echoes like thunder.")
            .minDepth(8)
            .spawnWeight(0.7)
            .build());

        // Shadowfang - Stealth weapon
        register(new UniqueArtifact.Builder("shadowfang", "Shadowfang", UniqueArtifact.ArtifactType.WEAPON)
            .weaponType(WeaponType.DAGGER)
            .enchantment(9)
            .property(ArtifactProperty.SLAYING, 6)
            .property(ArtifactProperty.ACCURACY, 6)
            .property(ArtifactProperty.STEALTH, 3)
            .property(ArtifactProperty.SEE_INVISIBLE, 1)
            .property(ArtifactProperty.INTELLIGENCE, 2)
            .lore("A blade forged from pure darkness. Those struck by it often don't see it coming.")
            .minDepth(10)
            .spawnWeight(0.6)
            .build());

        // The Shattering Mace - Armor-breaking weapon
        register(new UniqueArtifact.Builder("shattering_mace", "the Shattering Mace", UniqueArtifact.ArtifactType.WEAPON)
            .weaponType(WeaponType.GREAT_MACE)
            .enchantment(10)
            .property(ArtifactProperty.SLAYING, 7)
            .property(ArtifactProperty.STRENGTH, 3)
            .property(ArtifactProperty.ARMOR_CLASS, 2)
            .lore("This massive mace has crushed armor and bone for generations. It grows heavier with each kill.")
            .minDepth(12)
            .spawnWeight(0.5)
            .build());

        // Radiant Dawn - Holy weapon
        register(new UniqueArtifact.Builder("radiant_dawn", "Radiant Dawn", UniqueArtifact.ArtifactType.WEAPON)
            .weaponType(WeaponType.LONG_SWORD)
            .enchantment(12)
            .property(ArtifactProperty.SLAYING, 8)
            .property(ArtifactProperty.ACCURACY, 8)
            .property(ArtifactProperty.POSITIVE_ENERGY, 2)
            .property(ArtifactProperty.REGENERATION, 1)
            .property(ArtifactProperty.SEE_INVISIBLE, 1)
            .lore("A sword blessed by the last priests before the fall. It still shines with holy light.")
            .minDepth(15)
            .spawnWeight(0.4)
            .build());

        // === ARMOR ===

        // The Survivor's Carapace - Lightweight power armor
        register(new UniqueArtifact.Builder("survivors_carapace", "the Survivor's Carapace", UniqueArtifact.ArtifactType.ARMOR)
            .armorType(ArmorType.LEATHER_ARMOR)
            .enchantment(8)
            .property(ArtifactProperty.ARMOR_CLASS, 5)
            .property(ArtifactProperty.EVASION, 3)
            .property(ArtifactProperty.DEXTERITY, 2)
            .property(ArtifactProperty.RESIST_POISON, 1)
            .lore("Crafted from the shell of a mutated creature, this armor is surprisingly light yet incredibly durable.")
            .minDepth(5)
            .spawnWeight(1.0)
            .build());

        // Bulwark of the Last Stand - Heavy defensive armor
        register(new UniqueArtifact.Builder("bulwark_last_stand", "Bulwark of the Last Stand", UniqueArtifact.ArtifactType.ARMOR)
            .armorType(ArmorType.PLATE_ARMOR)
            .enchantment(12)
            .property(ArtifactProperty.ARMOR_CLASS, 8)
            .property(ArtifactProperty.STRENGTH, 3)
            .property(ArtifactProperty.RESIST_FIRE, 2)
            .property(ArtifactProperty.RESIST_COLD, 2)
            .property(ArtifactProperty.REGENERATION, 1)
            .lore("The last commander of the old guard wore this armor as civilization fell. It still stands defiant.")
            .minDepth(10)
            .spawnWeight(0.6)
            .build());

        // The Vault Keeper's Plate - Radiation-resistant armor
        register(new UniqueArtifact.Builder("vault_keepers_plate", "the Vault Keeper's Plate", UniqueArtifact.ArtifactType.ARMOR)
            .armorType(ArmorType.CHAIN_MAIL)
            .enchantment(10)
            .property(ArtifactProperty.ARMOR_CLASS, 6)
            .property(ArtifactProperty.RESIST_POISON, 2)
            .property(ArtifactProperty.RESIST_CORROSION, 1)
            .property(ArtifactProperty.INTELLIGENCE, 2)
            .property(ArtifactProperty.RESIST_MAGIC, 2)
            .lore("Worn by those who guarded the underground vaults. Provides excellent protection against radiation.")
            .minDepth(8)
            .spawnWeight(0.7)
            .build());

        // Cloak of Endless Night - Stealth cloak
        register(new UniqueArtifact.Builder("cloak_endless_night", "Cloak of Endless Night", UniqueArtifact.ArtifactType.ARMOR)
            .armorType(ArmorType.CLOAK)
            .enchantment(7)
            .property(ArtifactProperty.EVASION, 4)
            .property(ArtifactProperty.STEALTH, 4)
            .property(ArtifactProperty.SEE_INVISIBLE, 1)
            .property(ArtifactProperty.DEXTERITY, 2)
            .lore("This cloak seems to drink in the light around it. Wearing it makes you almost invisible.")
            .minDepth(7)
            .spawnWeight(0.8)
            .build());

        // Shield of the Faithful - Defensive shield
        register(new UniqueArtifact.Builder("shield_faithful", "Shield of the Faithful", UniqueArtifact.ArtifactType.ARMOR)
            .armorType(ArmorType.KITE_SHIELD)
            .enchantment(9)
            .property(ArtifactProperty.ARMOR_CLASS, 7)
            .property(ArtifactProperty.POSITIVE_ENERGY, 2)
            .property(ArtifactProperty.RESIST_MAGIC, 3)
            .property(ArtifactProperty.REFLECT, 1)
            .lore("Carried by a holy warrior who defended refugees until the very end. It still radiates protection.")
            .minDepth(9)
            .spawnWeight(0.7)
            .build());

        // Boots of the Wanderer - Movement boots
        register(new UniqueArtifact.Builder("boots_wanderer", "Boots of the Wanderer", UniqueArtifact.ArtifactType.ARMOR)
            .armorType(ArmorType.BOOTS)
            .enchantment(6)
            .property(ArtifactProperty.EVASION, 3)
            .property(ArtifactProperty.DEXTERITY, 2)
            .property(ArtifactProperty.REGENERATION, 1)
            .lore("These boots have walked every corner of the wasteland. They never seem to wear out.")
            .minDepth(6)
            .spawnWeight(0.9)
            .build());

        LOGGER.info("Registered {} unique artifacts", ARTIFACTS.size());
    }

    /**
     * Register a unique artifact
     */
    private static void register(UniqueArtifact artifact) {
        ARTIFACTS.put(artifact.getId(), artifact);
    }

    /**
     * Get artifact by ID
     */
    public static UniqueArtifact getArtifact(String id) {
        return ARTIFACTS.get(id);
    }

    /**
     * Get all artifacts
     */
    public static Collection<UniqueArtifact> getAllArtifacts() {
        return new ArrayList<>(ARTIFACTS.values());
    }

    /**
     * Get artifacts available at a given depth
     */
    public static List<UniqueArtifact> getArtifactsForDepth(int depth) {
        return ARTIFACTS.values().stream()
            .filter(a -> a.getMinDepth() <= depth)
            .filter(a -> !spawnedArtifacts.contains(a.getId())) // Not already spawned
            .collect(Collectors.toList());
    }

    /**
     * Get weapon artifacts for a depth
     */
    public static List<UniqueArtifact> getWeaponArtifactsForDepth(int depth) {
        return getArtifactsForDepth(depth).stream()
            .filter(a -> a.getType() == UniqueArtifact.ArtifactType.WEAPON)
            .collect(Collectors.toList());
    }

    /**
     * Get armor artifacts for a depth
     */
    public static List<UniqueArtifact> getArmorArtifactsForDepth(int depth) {
        return getArtifactsForDepth(depth).stream()
            .filter(a -> a.getType() == UniqueArtifact.ArtifactType.ARMOR)
            .collect(Collectors.toList());
    }

    /**
     * Randomly select an artifact for a depth using weighted random
     * @return Selected artifact, or null if none available
     */
    public static UniqueArtifact selectRandomArtifact(int depth) {
        List<UniqueArtifact> available = getArtifactsForDepth(depth);
        if (available.isEmpty()) {
            return null;
        }

        // Calculate total weight
        double totalWeight = available.stream()
            .mapToDouble(UniqueArtifact::getSpawnWeight)
            .sum();

        // Weighted random selection
        double roll = RANDOM.nextDouble() * totalWeight;
        double cumulative = 0.0;

        for (UniqueArtifact artifact : available) {
            cumulative += artifact.getSpawnWeight();
            if (roll < cumulative) {
                return artifact;
            }
        }

        return available.get(available.size() - 1); // Fallback
    }

    /**
     * Mark an artifact as spawned (prevents duplicates)
     */
    public static void markAsSpawned(String artifactId) {
        spawnedArtifacts.add(artifactId);
        LOGGER.info("Unique artifact spawned: {}", artifactId);
    }

    /**
     * Check if an artifact has been spawned
     */
    public static boolean hasBeenSpawned(String artifactId) {
        return spawnedArtifacts.contains(artifactId);
    }

    /**
     * Reset spawned artifacts (for new game)
     */
    public static void resetSpawnedArtifacts() {
        spawnedArtifacts.clear();
        LOGGER.info("Reset spawned artifacts");
    }

    /**
     * Get count of spawned artifacts
     */
    public static int getSpawnedCount() {
        return spawnedArtifacts.size();
    }

    /**
     * Get count of total artifacts
     */
    public static int getTotalCount() {
        return ARTIFACTS.size();
    }
}
