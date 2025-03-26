package cz.cvut.copakond.pinkfluffyunicorn.model.utils.enums;

public enum RenderPriorityEnums {
    BACKGROUND(0),
    DECORATION(1),
    TILE(2),
    ARROW(3),
    ITEM(4),
    CHARACTER(5);

    private final int value;

    RenderPriorityEnums(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
