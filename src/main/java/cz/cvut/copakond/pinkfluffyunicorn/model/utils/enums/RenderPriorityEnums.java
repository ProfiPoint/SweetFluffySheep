package cz.cvut.copakond.pinkfluffyunicorn.model.utils.enums;

public enum RenderPriorityEnums {
    TILE(0),
    ARROW(1),
    ITEM(2),
    CHARACTER(3);

    private final int value;

    RenderPriorityEnums(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
