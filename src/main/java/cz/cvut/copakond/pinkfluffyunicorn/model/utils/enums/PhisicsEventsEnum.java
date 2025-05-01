package cz.cvut.copakond.pinkfluffyunicorn.model.utils.enums;

public enum PhisicsEventsEnum {
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

    PhisicsEventsEnum(boolean rotation) {
        this.rotation = rotation;
    }
    
    public boolean isNotRotation() {
        return !rotation;
    }

    public static PhisicsEventsEnum convertDirectionToPhysicsEvent(DirectionEnum direction) {
        return switch (direction) {
            case RIGHT -> ROTATION_RIGHT;
            case DOWN -> ROTATION_DOWN;
            case LEFT -> ROTATION_LEFT;
            case UP -> ROTATION_UP;
        };
    }

    public static DirectionEnum convertPhysicsEvent(PhisicsEventsEnum physicsEvent) {
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