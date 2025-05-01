package cz.cvut.copakond.sweetfluffysheep.model.utils.enums;

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

    public DirectionEnum next() {
        DirectionEnum[] values = DirectionEnum.values();
        return values[(this.ordinal() + 1) % values.length];
    }

    public DirectionEnum getOppositeDirection() {
        return values()[(this.ordinal() + 2) % values().length];
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