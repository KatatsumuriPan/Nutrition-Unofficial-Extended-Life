package ca.wescook.nutrition.api;

/**
 * Phase in which food nutrients will be applied to the player.
 */
public enum NutrientApplicationPhase {
    FINISH_USING, // Default
    ON_RIGHT_CLICK, // For foods that are eaten immediately
}
