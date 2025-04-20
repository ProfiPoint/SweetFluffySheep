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
    ROTATION_STUCK_4WALLS(true), // stuck in 4 walls -> so it can not move, so it will only rotate :'(

    // Used when multiple same characters get "compressed" into one tile, this makes them slow down to equally spread again
    SLOWDOWN(false);

    // should character start to rotate?
    private final boolean rotation;

    PhisicsEventsEnum(boolean rotation) {
        this.rotation = rotation;
    }
    
    public boolean isRotation() {
        return rotation;
    }

    public static PhisicsEventsEnum convertDirectionToPhisicsEvent(DirectionEnum direction) {
        switch (direction) {
            case RIGHT: return ROTATION_RIGHT;
            case DOWN: return ROTATION_DOWN;
            case LEFT: return ROTATION_LEFT;
            case UP: return ROTATION_UP;
            default: return NO_COLLISION;
        }
    }

    public static DirectionEnum convertPhisicsEvent(PhisicsEventsEnum phisicsEvent) {
        switch (phisicsEvent) {
            case ROTATION_RIGHT: return DirectionEnum.RIGHT;
            case ROTATION_DOWN: return DirectionEnum.DOWN;
            case ROTATION_LEFT: return DirectionEnum.LEFT;
            case ROTATION_UP: return DirectionEnum.UP;
            default: return DirectionEnum.UP; // this will never happen
        }
    }
}
