package com.wasteland;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Selects random DCSS vaults for rendering
 */
public class VaultSelector {
    private static final Logger LOGGER = LogManager.getLogger();
    private static final Random RANDOM = new Random();

    // All available wasteland vaults
    private static final String[] EARLY_VAULTS = {
        "ruins_convenience_store",
        "ruins_suburban_house",
        "ruins_gas_station",
        "ruins_apartment_block",
        "ruins_collapsed_parking",
        "ruins_small_bunker",
        "ruins_playground",
        "ruins_pharmacy",
        "ruins_street_barricade",
        "ruins_school_classroom",
        "ruins_overturned_bus",
        "ruins_strip_mall",
        "ruins_subway_entrance",
        "ruins_water_tower",
        "ruins_fire_station"
    };

    private static final String[] MID_VAULTS = {
        "ruins_military_checkpoint",
        "ruins_hospital",
        "ruins_police_station",
        "ruins_office_building",
        "ruins_supermarket",
        "ruins_fortified_compound",
        "ruins_warehouse",
        "ruins_radio_station",
        "ruins_sewage_treatment",
        "ruins_power_substation",
        "ruins_apartment_complex",
        "ruins_library"
    };

    private static final String[] LATE_VAULTS = {
        "ruins_military_base",
        "ruins_corporate_headquarters",
        "ruins_underground_bunker",
        "ruins_research_facility",
        "ruins_nuclear_reactor",
        "ruins_missile_silo",
        "ruins_prison_complex",
        "ruins_data_center",
        "ruins_skyscraper_base"
    };

    /**
     * Get a random vault from all available vaults
     */
    public static String getRandomVault() {
        List<String> allVaults = new ArrayList<>();

        for (String vault : EARLY_VAULTS) allVaults.add(vault);
        for (String vault : MID_VAULTS) allVaults.add(vault);
        for (String vault : LATE_VAULTS) allVaults.add(vault);

        String vault = allVaults.get(RANDOM.nextInt(allVaults.size()));
        LOGGER.info("Selected random vault: {}", vault);
        return "rooms/" + vault + ".json";
    }

    /**
     * Get a random vault from a specific tier
     */
    public static String getRandomVault(VaultTier tier) {
        String[] vaults;

        switch (tier) {
            case EARLY:
                vaults = EARLY_VAULTS;
                break;
            case MID:
                vaults = MID_VAULTS;
                break;
            case LATE:
                vaults = LATE_VAULTS;
                break;
            default:
                return getRandomVault();
        }

        String vault = vaults[RANDOM.nextInt(vaults.length)];
        LOGGER.info("Selected random {} tier vault: {}", tier, vault);
        return "rooms/" + vault + ".json";
    }

    /**
     * Check if a vault exists
     */
    public static boolean vaultExists(String vaultPath) {
        try {
            InputStream stream = VaultSelector.class.getClassLoader().getResourceAsStream(vaultPath);
            if (stream != null) {
                stream.close();
                return true;
            }
            return false;
        } catch (IOException e) {
            return false;
        }
    }

    public enum VaultTier {
        EARLY,   // Small, simple structures
        MID,     // Medium complexity
        LATE     // Large, complex structures
    }
}
