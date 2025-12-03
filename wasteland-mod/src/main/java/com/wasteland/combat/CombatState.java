package com.wasteland.combat;

/**
 * Enum representing the current state of combat
 */
public enum CombatState {
    /**
     * Not in combat - normal exploration mode
     */
    EXPLORATION,

    /**
     * Player's turn to act
     */
    PLAYER_TURN,

    /**
     * Enemy's turn (AI controlled)
     */
    ENEMY_TURN,

    /**
     * Combat ending (cleanup phase)
     */
    ENDING
}
