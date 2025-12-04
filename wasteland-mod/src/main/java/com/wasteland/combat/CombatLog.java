package com.wasteland.combat;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Combat message log - DCSS style combat messages
 * Stores recent combat events for display in UI
 */
public class CombatLog {
    private static final List<String> messages = new ArrayList<>();
    private static final int MAX_MESSAGES = 20; // Keep last 20 messages

    /**
     * Add a new combat message to the log
     * Most recent messages appear first
     */
    public static void addMessage(String message) {
        messages.add(0, message); // Add to beginning
        if (messages.size() > MAX_MESSAGES) {
            messages.remove(MAX_MESSAGES); // Remove oldest
        }
    }

    /**
     * Get all messages (most recent first)
     */
    public static List<String> getMessages() {
        return new ArrayList<>(messages); // Return copy
    }

    /**
     * Get last N messages
     */
    public static List<String> getRecentMessages(int count) {
        int size = Math.min(count, messages.size());
        return new ArrayList<>(messages.subList(0, size));
    }

    /**
     * Clear all messages (when combat ends)
     */
    public static void clear() {
        messages.clear();
    }

    /**
     * Get message count
     */
    public static int size() {
        return messages.size();
    }
}
