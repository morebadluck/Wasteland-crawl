package com.wasteland.mutations;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

/**
 * Manages player mutations
 */
public class MutationManager {
    private static final Logger LOGGER = LogManager.getLogger();
    private static final Map<UUID, PlayerMutations> MUTATIONS = new HashMap<>();
    private static final Random RANDOM = new Random();

    /**
     * Get or create player mutations
     */
    public static PlayerMutations getMutations(UUID playerId) {
        return MUTATIONS.computeIfAbsent(playerId, PlayerMutations::new);
    }

    /**
     * Add a specific mutation
     */
    public static boolean addMutation(UUID playerId, Mutation mutation) {
        PlayerMutations mutations = getMutations(playerId);
        return mutations.addMutation(mutation);
    }

    /**
     * Remove a specific mutation
     */
    public static boolean removeMutation(UUID playerId, Mutation mutation) {
        PlayerMutations mutations = getMutations(playerId);
        return mutations.removeMutation(mutation);
    }

    /**
     * Add a random mutation of a specific type
     */
    public static Mutation addRandomMutation(UUID playerId, MutationType type) {
        PlayerMutations playerMutations = getMutations(playerId);

        Mutation[] candidates = switch (type) {
            case GOOD -> Mutation.getGoodMutations();
            case BAD -> Mutation.getBadMutations();
            case BODY -> Mutation.getBodyMutations();
            case MIXED -> Mutation.values(); // Any mutation
        };

        // Shuffle and try mutations until one works
        Mutation[] shuffled = candidates.clone();
        shuffleArray(shuffled);

        for (Mutation mutation : shuffled) {
            if (playerMutations.addMutation(mutation)) {
                return mutation;
            }
        }

        LOGGER.warn("Could not find valid mutation of type {} for player {}", type, playerId);
        return null;
    }

    /**
     * Add a completely random mutation (50% good, 50% bad)
     */
    public static Mutation addRandomMutation(UUID playerId) {
        boolean good = RANDOM.nextBoolean();
        return addRandomMutation(playerId, good ? MutationType.GOOD : MutationType.BAD);
    }

    /**
     * Remove a random mutation
     */
    public static Mutation removeRandomMutation(UUID playerId) {
        PlayerMutations playerMutations = getMutations(playerId);
        Map<Mutation, Integer> mutations = playerMutations.getAllMutations();

        if (mutations.isEmpty()) {
            LOGGER.warn("Player {} has no mutations to remove", playerId);
            return null;
        }

        // Pick a random mutation
        Mutation[] mutArray = mutations.keySet().toArray(new Mutation[0]);
        Mutation toRemove = mutArray[RANDOM.nextInt(mutArray.length)];

        playerMutations.removeMutation(toRemove);
        return toRemove;
    }

    /**
     * Get mutation level
     */
    public static int getMutationLevel(UUID playerId, Mutation mutation) {
        PlayerMutations mutations = getMutations(playerId);
        return mutations.getMutationLevel(mutation);
    }

    /**
     * Check if player has mutation
     */
    public static boolean hasMutation(UUID playerId, Mutation mutation) {
        PlayerMutations mutations = getMutations(playerId);
        return mutations.hasMutation(mutation);
    }

    /**
     * Get all mutations
     */
    public static Map<Mutation, Integer> getAllMutations(UUID playerId) {
        PlayerMutations mutations = getMutations(playerId);
        return mutations.getAllMutations();
    }

    /**
     * Get total mutation count
     */
    public static int getTotalMutationCount(UUID playerId) {
        PlayerMutations mutations = getMutations(playerId);
        return mutations.getTotalMutationCount();
    }

    /**
     * Clear all mutations
     */
    public static void clearAllMutations(UUID playerId) {
        PlayerMutations mutations = getMutations(playerId);
        mutations.clearAllMutations();
    }

    /**
     * Remove player mutations (cleanup)
     */
    public static void removeMutations(UUID playerId) {
        MUTATIONS.remove(playerId);
        LOGGER.debug("Removed mutation data for player {}", playerId);
    }

    /**
     * Shuffle array utility
     */
    private static void shuffleArray(Mutation[] array) {
        for (int i = array.length - 1; i > 0; i--) {
            int j = RANDOM.nextInt(i + 1);
            Mutation temp = array[i];
            array[i] = array[j];
            array[j] = temp;
        }
    }
}
