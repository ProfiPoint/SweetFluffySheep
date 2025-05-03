package cz.cvut.copakond.sweetfluffysheep.model.utils.enums;

/**
 * Enum representing the render priorities for different game elements.
 * <p>
 * This enum is used to determine the order in which game elements are rendered
 * on the screen. The lower the value, the higher the priority.
 */
public enum RenderPriorityEnums {
    TILE(0),
    ARROW(1),
    ITEM(2),
    CHARACTER(3);

    private final int value;

    /**
     * Constructor for RenderPriorityEnums.
     *
     * @param value the integer value representing the render priority
     */
    RenderPriorityEnums(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
