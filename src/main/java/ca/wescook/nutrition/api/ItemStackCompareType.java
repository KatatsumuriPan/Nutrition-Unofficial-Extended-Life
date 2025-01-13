package ca.wescook.nutrition.api;

/**
 * How to compare two ItemStacks.
 */
public enum ItemStackCompareType {
    DEFAULT,            // Only id sensitive
    META_SENSITIVE,     // Meta and durability sensitive, not nbt
    ONLY_NBT_SENSITIVE, // Nbt sensitive, not meta and durability
    ALL_SENSITIVE,      // All sensitive
}
