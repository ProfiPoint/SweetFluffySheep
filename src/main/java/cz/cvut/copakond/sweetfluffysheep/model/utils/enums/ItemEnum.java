package cz.cvut.copakond.sweetfluffysheep.model.utils.enums;

public enum ItemEnum {
    COIN(), // Coin, not a special item
    FIRE(),
    RAINBOW();

    public static int getNumberOfItems() {
        return ItemEnum.values().length;
    }
}
