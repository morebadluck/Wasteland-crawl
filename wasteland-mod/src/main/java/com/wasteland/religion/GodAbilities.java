package com.wasteland.religion;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.*;

/**
 * Registry of divine abilities for each god
 * Based on DCSS god powers
 */
public class GodAbilities {
    private static final Logger LOGGER = LogManager.getLogger();
    private static final Map<God, List<DivineAbility>> GOD_ABILITIES = new HashMap<>();

    /**
     * Register all god abilities
     */
    public static void registerAbilities() {
        LOGGER.info("Registering divine abilities...");

        registerTrogAbilities();
        registerTheShiningOneAbilities();
        registerZinAbilities();
        registerElyvilonAbilities();
        registerOkawaruAbilities();
        registerSifMunaAbilities();
        registerVehemetAbilities();
        registerKikubaaqudghaAbilities();
        registerMakhlebAbilities();
        registerYredelemnulAbilities();
        registerGozagAbilities();
        registerXomAbilities();

        int totalAbilities = GOD_ABILITIES.values().stream()
                                          .mapToInt(List::size)
                                          .sum();
        LOGGER.info("Registered {} divine abilities for {} gods",
                   totalAbilities, GOD_ABILITIES.size());
    }

    /**
     * Get abilities for a god
     */
    public static List<DivineAbility> getAbilities(God god) {
        return GOD_ABILITIES.getOrDefault(god, Collections.emptyList());
    }

    /**
     * Get available abilities at current piety
     */
    public static List<DivineAbility> getAvailableAbilities(God god, int piety) {
        List<DivineAbility> available = new ArrayList<>();
        for (DivineAbility ability : getAbilities(god)) {
            if (ability.isAvailableAtPiety(piety)) {
                available.add(ability);
            }
        }
        return available;
    }

    // ==== TROG - God of Anger ====
    private static void registerTrogAbilities() {
        List<DivineAbility> abilities = new ArrayList<>();

        // Passive: Magic resistance
        abilities.add(new DivineAbility.Builder("trog_magic_resist", "Magic Resistance")
                .description("Passive magic resistance")
                .minPiety(PlayerReligion.PIETY_RANK_1)
                .passive()
                .build());

        // * Berserk
        abilities.add(new DivineAbility.Builder("trog_berserk", "Berserk")
                .description("Enter a berserker rage (+50% damage, +50% speed, -50% defense)")
                .minPiety(PlayerReligion.PIETY_RANK_1)
                .invocationCost(0) // No piety cost, but has cooldown
                .type(DivineAbility.AbilityType.BERSERK)
                .build());

        // ** Trog's Hand
        abilities.add(new DivineAbility.Builder("trog_hand", "Trog's Hand")
                .description("Grants AC, SH, and magic resistance for a duration")
                .minPiety(PlayerReligion.PIETY_RANK_2)
                .invocationCost(2)
                .type(DivineAbility.AbilityType.BUFF)
                .build());

        // **** Brothers in Arms
        abilities.add(new DivineAbility.Builder("trog_brothers", "Brothers in Arms")
                .description("Summons powerful berserker allies")
                .minPiety(PlayerReligion.PIETY_RANK_4)
                .invocationCost(5)
                .type(DivineAbility.AbilityType.SUMMON)
                .build());

        GOD_ABILITIES.put(God.TROG, abilities);
    }

    // ==== THE SHINING ONE - God of Holy Light ====
    private static void registerTheShiningOneAbilities() {
        List<DivineAbility> abilities = new ArrayList<>();

        // Passive: Halo
        abilities.add(new DivineAbility.Builder("tso_halo", "Halo")
                .description("Emit light, preventing enemy invisibility")
                .minPiety(PlayerReligion.PIETY_RANK_1)
                .passive()
                .build());

        // * Divine Shield
        abilities.add(new DivineAbility.Builder("tso_shield", "Divine Shield")
                .description("Grants temporary divine protection")
                .minPiety(PlayerReligion.PIETY_RANK_1)
                .invocationCost(2)
                .type(DivineAbility.AbilityType.BUFF)
                .build());

        // *** Cleansing Flame
        abilities.add(new DivineAbility.Builder("tso_flame", "Cleansing Flame")
                .description("Blast nearby enemies with holy fire")
                .minPiety(PlayerReligion.PIETY_RANK_3)
                .invocationCost(3)
                .type(DivineAbility.AbilityType.ATTACK)
                .build());

        // ***** Summon Divine Warrior
        abilities.add(new DivineAbility.Builder("tso_angel", "Summon Divine Warrior")
                .description("Summons a powerful angel to fight for you")
                .minPiety(PlayerReligion.PIETY_RANK_5)
                .invocationCost(6)
                .type(DivineAbility.AbilityType.SUMMON)
                .build());

        GOD_ABILITIES.put(God.THE_SHINING_ONE, abilities);
    }

    // ==== ZIN - God of Law ====
    private static void registerZinAbilities() {
        List<DivineAbility> abilities = new ArrayList<>();

        // * Recite
        abilities.add(new DivineAbility.Builder("zin_recite", "Recite")
                .description("Recite Zin's laws to weaken nearby enemies")
                .minPiety(PlayerReligion.PIETY_RANK_1)
                .invocationCost(1)
                .type(DivineAbility.AbilityType.ATTACK)
                .build());

        // ** Vitalisation
        abilities.add(new DivineAbility.Builder("zin_vital", "Vitalisation")
                .description("Restore health and clear status effects")
                .minPiety(PlayerReligion.PIETY_RANK_2)
                .invocationCost(2)
                .type(DivineAbility.AbilityType.HEAL)
                .build());

        // **** Imprison
        abilities.add(new DivineAbility.Builder("zin_imprison", "Imprison")
                .description("Trap a monster in a cage of silver")
                .minPiety(PlayerReligion.PIETY_RANK_4)
                .invocationCost(4)
                .type(DivineAbility.AbilityType.UTILITY)
                .build());

        // ****** Sanctuary
        abilities.add(new DivineAbility.Builder("zin_sanctuary", "Sanctuary")
                .description("Create a holy sanctuary where no violence is possible")
                .minPiety(PlayerReligion.PIETY_RANK_6)
                .invocationCost(7)
                .type(DivineAbility.AbilityType.UTILITY)
                .build());

        GOD_ABILITIES.put(God.ZIN, abilities);
    }

    // ==== ELYVILON - God of Healing ====
    private static void registerElyvilonAbilities() {
        List<DivineAbility> abilities = new ArrayList<>();

        // * Lesser Healing
        abilities.add(new DivineAbility.Builder("ely_lesser_heal", "Lesser Healing")
                .description("Heal yourself for a small amount")
                .minPiety(PlayerReligion.PIETY_RANK_1)
                .invocationCost(1)
                .type(DivineAbility.AbilityType.HEAL)
                .build());

        // ** Purification
        abilities.add(new DivineAbility.Builder("ely_purify", "Purification")
                .description("Remove negative status effects")
                .minPiety(PlayerReligion.PIETY_RANK_2)
                .invocationCost(2)
                .type(DivineAbility.AbilityType.HEAL)
                .build());

        // *** Greater Healing
        abilities.add(new DivineAbility.Builder("ely_greater_heal", "Greater Healing")
                .description("Heal yourself for a large amount")
                .minPiety(PlayerReligion.PIETY_RANK_3)
                .invocationCost(3)
                .type(DivineAbility.AbilityType.HEAL)
                .build());

        // ***** Divine Vigour
        abilities.add(new DivineAbility.Builder("ely_vigour", "Divine Vigour")
                .description("Increase max HP and MP temporarily")
                .minPiety(PlayerReligion.PIETY_RANK_5)
                .invocationCost(5)
                .type(DivineAbility.AbilityType.BUFF)
                .build());

        GOD_ABILITIES.put(God.ELYVILON, abilities);
    }

    // ==== OKAWARU - God of War ====
    private static void registerOkawaruAbilities() {
        List<DivineAbility> abilities = new ArrayList<>();

        // * Heroism
        abilities.add(new DivineAbility.Builder("oka_heroism", "Heroism")
                .description("Temporarily increase combat skills")
                .minPiety(PlayerReligion.PIETY_RANK_1)
                .invocationCost(2)
                .type(DivineAbility.AbilityType.BUFF)
                .build());

        // *** Finesse
        abilities.add(new DivineAbility.Builder("oka_finesse", "Finesse")
                .description("Move and attack at double speed")
                .minPiety(PlayerReligion.PIETY_RANK_3)
                .invocationCost(3)
                .type(DivineAbility.AbilityType.BUFF)
                .build());

        // ***** Duel
        abilities.add(new DivineAbility.Builder("oka_duel", "Duel")
                .description("Challenge an enemy to single combat")
                .minPiety(PlayerReligion.PIETY_RANK_5)
                .invocationCost(5)
                .type(DivineAbility.AbilityType.UTILITY)
                .build());

        GOD_ABILITIES.put(God.OKAWARU, abilities);
    }

    // ==== SIF MUNA - God of Magic ====
    private static void registerSifMunaAbilities() {
        List<DivineAbility> abilities = new ArrayList<>();

        // Passive: Channel Magic
        abilities.add(new DivineAbility.Builder("sif_channel", "Channel Magic")
                .description("Passively regenerate MP faster")
                .minPiety(PlayerReligion.PIETY_RANK_1)
                .passive()
                .build());

        // ** Forget Spell
        abilities.add(new DivineAbility.Builder("sif_forget", "Forget Spell")
                .description("Forget a memorized spell to make room")
                .minPiety(PlayerReligion.PIETY_RANK_2)
                .invocationCost(0)
                .type(DivineAbility.AbilityType.UTILITY)
                .build());

        // **** Divine Energy
        abilities.add(new DivineAbility.Builder("sif_energy", "Divine Energy")
                .description("Restore a large amount of MP")
                .minPiety(PlayerReligion.PIETY_RANK_4)
                .invocationCost(3)
                .type(DivineAbility.AbilityType.UTILITY)
                .build());

        GOD_ABILITIES.put(God.SIF_MUNA, abilities);
    }

    // ==== VEHUMET - God of Destructive Magic ====
    private static void registerVehemetAbilities() {
        List<DivineAbility> abilities = new ArrayList<>();

        // Passive: Spell Power
        abilities.add(new DivineAbility.Builder("veh_power", "Destructive Power")
                .description("Increases damage of offensive spells")
                .minPiety(PlayerReligion.PIETY_RANK_1)
                .passive()
                .build());

        // Passive: Spell Library (gift spells)
        abilities.add(new DivineAbility.Builder("veh_library", "Vehumet's Library")
                .description("Vehumet gifts you destructive spells")
                .minPiety(PlayerReligion.PIETY_RANK_2)
                .passive()
                .build());

        GOD_ABILITIES.put(God.VEHUMET, abilities);
    }

    // ==== KIKUBAAQUDGHA - God of Necromancy ====
    private static void registerKikubaaqudghaAbilities() {
        List<DivineAbility> abilities = new ArrayList<>();

        // * Receive Corpses
        abilities.add(new DivineAbility.Builder("kiku_corpses", "Receive Corpses")
                .description("Kikubaaqudgha sends you corpses")
                .minPiety(PlayerReligion.PIETY_RANK_1)
                .invocationCost(2)
                .type(DivineAbility.AbilityType.UTILITY)
                .build());

        // *** Torment
        abilities.add(new DivineAbility.Builder("kiku_torment", "Torment")
                .description("Inflict torment on all nearby living creatures")
                .minPiety(PlayerReligion.PIETY_RANK_3)
                .invocationCost(4)
                .type(DivineAbility.AbilityType.ATTACK)
                .build());

        GOD_ABILITIES.put(God.KIKUBAAQUDGHA, abilities);
    }

    // ==== MAKHLEB - God of Chaos ====
    private static void registerMakhlebAbilities() {
        List<DivineAbility> abilities = new ArrayList<>();

        // ** Lesser Servant
        abilities.add(new DivineAbility.Builder("mak_lesser", "Lesser Servant of Makhleb")
                .description("Summon a minor demon")
                .minPiety(PlayerReligion.PIETY_RANK_2)
                .invocationCost(2)
                .type(DivineAbility.AbilityType.SUMMON)
                .build());

        // **** Greater Servant
        abilities.add(new DivineAbility.Builder("mak_greater", "Greater Servant of Makhleb")
                .description("Summon a powerful demon")
                .minPiety(PlayerReligion.PIETY_RANK_4)
                .invocationCost(4)
                .type(DivineAbility.AbilityType.SUMMON)
                .build());

        GOD_ABILITIES.put(God.MAKHLEB, abilities);
    }

    // ==== YREDELEMNUL - God of Death ====
    private static void registerYredelemnulAbilities() {
        List<DivineAbility> abilities = new ArrayList<>();

        // * Animate Remains
        abilities.add(new DivineAbility.Builder("yred_animate", "Animate Remains")
                .description("Raise a corpse as an undead servant")
                .minPiety(PlayerReligion.PIETY_RANK_1)
                .invocationCost(2)
                .type(DivineAbility.AbilityType.SUMMON)
                .build());

        // *** Drain Life
        abilities.add(new DivineAbility.Builder("yred_drain", "Drain Life")
                .description("Drain life from nearby living creatures")
                .minPiety(PlayerReligion.PIETY_RANK_3)
                .invocationCost(3)
                .type(DivineAbility.AbilityType.ATTACK)
                .build());

        // ***** Enslave Soul
        abilities.add(new DivineAbility.Builder("yred_enslave", "Enslave Soul")
                .description("Permanently enslave the soul of a fallen enemy")
                .minPiety(PlayerReligion.PIETY_RANK_5)
                .invocationCost(5)
                .type(DivineAbility.AbilityType.SUMMON)
                .build());

        GOD_ABILITIES.put(God.YREDELEMNUL, abilities);
    }

    // ==== GOZAG - God of Gold ====
    private static void registerGozagAbilities() {
        List<DivineAbility> abilities = new ArrayList<>();

        // Passive: Gold distraction
        abilities.add(new DivineAbility.Builder("gozag_distract", "Gold Distraction")
                .description("Gold distracts your enemies")
                .minPiety(PlayerReligion.PIETY_RANK_1)
                .passive()
                .build());

        // * Potion Petition (uses gold not piety)
        abilities.add(new DivineAbility.Builder("gozag_potion", "Potion Petition")
                .description("Buy a set of potions")
                .minPiety(PlayerReligion.PIETY_RANK_1)
                .invocationCost(0) // Uses gold
                .type(DivineAbility.AbilityType.UTILITY)
                .build());

        // * Call Merchant (uses gold not piety)
        abilities.add(new DivineAbility.Builder("gozag_merchant", "Call Merchant")
                .description("Summon a shop")
                .minPiety(PlayerReligion.PIETY_RANK_1)
                .invocationCost(0) // Uses gold
                .type(DivineAbility.AbilityType.UTILITY)
                .build());

        GOD_ABILITIES.put(God.GOZAG, abilities);
    }

    // ==== XOM - God of Chaos ====
    private static void registerXomAbilities() {
        List<DivineAbility> abilities = new ArrayList<>();

        // Xom has no active abilities - acts randomly!
        abilities.add(new DivineAbility.Builder("xom_chaos", "Xom's Whim")
                .description("Xom will act randomly - good or bad!")
                .minPiety(0)
                .passive()
                .build());

        GOD_ABILITIES.put(God.XOM, abilities);
    }
}
