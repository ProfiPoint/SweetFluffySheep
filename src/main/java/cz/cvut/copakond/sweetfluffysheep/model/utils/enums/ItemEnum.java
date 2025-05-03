package cz.cvut.copakond.sweetfluffysheep.model.utils.enums;

/**
 * Enum representing all current items in the game.
 * This enum is used to identify items in the game.
 * It is also used to determine the number of items in the game.
 */
public enum ItemEnum {
    COIN(), // Coin, not a special item
    FREEZE(),
    RAGE();

    /**
     * Returns the name of the item.
     *
     * @return the name of the item
     */
    public static int getNumberOfItems() {
        return ItemEnum.values().length;
    }
}
