package cz.cvut.copakond.sweetfluffysheep.model.utils.enums;

/**
 * Enum representing all possible physics events in the game.
 * This enum is used to handle various events that can occur during the game,
 * such as collisions, rotations, and other interactions.
 */
public enum PhysicsEventsEnum {
    NO_COLLISION(false),
    SHEEP_KILLED(false),
    IN_GOAL(false),
    BEFORE_START(false),
    ROTATION_LEFT(true),
    ROTATION_RIGHT(true),
    ROTATION_UP(true),
    ROTATION_DOWN(true),
    ROTATION_OPPOSITE(true),
    ROTATION_STUCK_4WALLS(true),
    SLOWDOWN(false);

    private final boolean rotation;

    PhysicsEventsEnum(boolean rotation) {
        this.rotation = rotation;
    }
    
    public boolean isNotRotation() {
        return !rotation;
    }

    /**
     * Converts a DirectionEnum to a corresponding PhysicsEventsEnum.
     * This method is used to map the direction of movement to the corresponding physics event.
     *
     * @param direction The direction to convert.
     * @return The corresponding PhysicsEventsEnum.
     */
    public static PhysicsEventsEnum convertDirectionToPhysicsEvent(DirectionEnum direction) {
        return switch (direction) {
            case RIGHT -> ROTATION_RIGHT;
            case DOWN -> ROTATION_DOWN;
            case LEFT -> ROTATION_LEFT;
            case UP -> ROTATION_UP;
        };
    }

    /**
     * Converts a PhysicsEventsEnum to a corresponding DirectionEnum.
     * This method is used to map the physics event to the corresponding direction of movement.
     *
     * @param physicsEvent The physics event to convert.
     * @return The corresponding DirectionEnum.
     */
    public static DirectionEnum convertPhysicsEvent(PhysicsEventsEnum physicsEvent) {
        return switch (physicsEvent) {
            case ROTATION_RIGHT -> DirectionEnum.RIGHT;
            case ROTATION_DOWN -> DirectionEnum.DOWN;
            case ROTATION_LEFT -> DirectionEnum.LEFT;
            case ROTATION_UP -> DirectionEnum.UP;
            default -> throw new IllegalArgumentException("PhysicsEvent " + physicsEvent + " cannot be converted to a" +
                    " DirectionEnum.");
        };
    }
}