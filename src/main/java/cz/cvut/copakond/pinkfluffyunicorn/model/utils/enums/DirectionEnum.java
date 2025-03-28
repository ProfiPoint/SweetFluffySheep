package cz.cvut.copakond.pinkfluffyunicorn.model.utils.enums;

import org.reflections.vfs.Vfs;

public enum DirectionEnum {
    RIGHT(0),
    DOWN(90),
    LEFT(180),
    UP(270);

    // general formula for [x,y] is sin(-angle), cos(-angle)

    public DirectionEnum next() {
        DirectionEnum[] values = DirectionEnum.values();
        return values[(this.ordinal() + 1) % values.length];
    }

    private final int value;

    DirectionEnum(int value) {
        this.value = value;
    }

    public DirectionEnum getOppositeDirection() {
        return values()[(this.ordinal() + 2) % values().length];
    }

    public int getValue() {
        return value;
    }
    public static DirectionEnum fromValue(int value) {
        for (DirectionEnum direction : DirectionEnum.values()) {
            if (direction.getValue() == value) {
                return direction;
            }
        }
        return null;
    }
}

