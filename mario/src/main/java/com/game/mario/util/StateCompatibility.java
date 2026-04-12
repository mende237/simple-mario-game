package com.game.mario.util;

import java.util.*;

/**
 * Defines and validates state compatibility rules.
 * Some states are mutually exclusive and cannot occur simultaneously.
 */
public class StateCompatibility {

        private static final Map<MarioState, Set<MarioState>> INCOMPATIBLE_STATES = Map.ofEntries(
                        // Movement states are mutually exclusive
                        Map.entry(MarioState.STANDING, Set.of(
                                        MarioState.WALKING, MarioState.JUMPING, MarioState.FALLING,
                                        MarioState.MOVING_BACKWARD,
                                        MarioState.MOVING_FORWARD)),
                        Map.entry(MarioState.WALKING, Set.of(
                                        MarioState.STANDING, MarioState.JUMPING, MarioState.FALLING,
                                        MarioState.MOVING_BACKWARD,
                                        MarioState.MOVING_FORWARD)),
                        Map.entry(MarioState.JUMPING, Set.of(
                                        MarioState.STANDING, MarioState.WALKING, MarioState.FALLING)),
                        Map.entry(MarioState.FALLING, Set.of(
                                        MarioState.STANDING, MarioState.WALKING, MarioState.JUMPING)),

                        Map.entry(MarioState.WIN, Set.of(MarioState.DEAD, MarioState.HIT_BY_ANTAGONIST)));

        /**
         * Check if two states are compatible (can occur simultaneously)
         */
        public static boolean areCompatible(MarioState state1, MarioState state2) {
                if (state1 == state2)
                        return true; // Same state is always compatible

                Set<MarioState> incompatible = INCOMPATIBLE_STATES.get(state1);
                return incompatible == null || !incompatible.contains(state2);
        }

        /**
         * Check if a new state is compatible with all current states
         */
        public static boolean isCompatibleWith(MarioState newState, Set<MarioState> currentStates) {
                return currentStates.stream()
                                .allMatch(state -> areCompatible(newState, state));
        }

        /**
         * Validate state compatibility - throws exception if incompatible
         */
        public static void validate(MarioState state1, MarioState state2) {
                if (!areCompatible(state1, state2)) {
                        throw new IllegalStateException(
                                        String.format("States %s and %s are incompatible", state1, state2));
                }
        }

        /**
         * Get incompatible states for a given state
         */
        public static Set<MarioState> getIncompatibleStates(MarioState state) {
                return INCOMPATIBLE_STATES.getOrDefault(state, Collections.emptySet());
        }
}
