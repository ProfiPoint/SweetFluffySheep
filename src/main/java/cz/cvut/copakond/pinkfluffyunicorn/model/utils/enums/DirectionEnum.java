package cz.cvut.copakond.pinkfluffyunicorn.model.utils.enums;

public enum DirectionEnum {
    UP(90), DOWN(270), LEFT(180), RIGHT(0);

    public DirectionEnum next() {
        DirectionEnum[] values = DirectionEnum.values();
        return values[(this.ordinal() + 1) % values.length];
    }

    private final int value;

    DirectionEnum(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}

