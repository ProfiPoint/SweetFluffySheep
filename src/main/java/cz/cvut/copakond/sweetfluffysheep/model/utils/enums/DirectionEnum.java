package cz.cvut.copakond.sweetfluffysheep.model.utils.enums;

/**
 * Enum representing the four directions (RIGHT, DOWN, LEFT, UP).
 * Each direction has an associated integer value (0 - right, 90 - down,
 * 180 - left, 270 - up).
 * Provides methods to get the next direction, the opposite direction,
 * and to create a DirectionEnum from an integer value.
 */
public enum DirectionEnum {
    RIGHT(0), //
    DOWN(90),
    LEFT(180),
    UP(270);

    private final int value;

    DirectionEnum(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    /**
     * Returns the next direction in the clockwise order.
     * For example, if the current direction is RIGHT, the next direction will be DOWN.
     *
     * @return the next direction
     */
    public DirectionEnum next() {
        DirectionEnum[] values = DirectionEnum.values();
        return values[(this.ordinal() + 1) % values.length];
    }

    /**
     * Returns the opposite direction.
     * For example, if the current direction is RIGHT, the opposite direction will be LEFT.
     *
     * @return the opposite direction
     */
    public DirectionEnum getOppositeDirection() {
        return values()[(this.ordinal() + 2) % values().length];
    }

    /**
     * Converts an integer value to a DirectionEnum.
     * The integer value should be one of the values defined in this enum (0, 90, 180, 270).
     *
     * @param value the integer value representing a direction
     * @return the corresponding DirectionEnum, or null if the value is not valid
     */
    public static DirectionEnum fromValue(int value) {
        for (DirectionEnum direction : DirectionEnum.values()) {
            if (direction.getValue() == value) {
                return direction;
            }
        }
        return null;
    }
}