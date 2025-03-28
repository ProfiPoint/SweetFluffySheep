package cz.cvut.copakond.pinkfluffyunicorn.model.utils.enums;

import cz.cvut.copakond.pinkfluffyunicorn.model.items.FireItem;
import cz.cvut.copakond.pinkfluffyunicorn.model.items.RainbowItem;
import cz.cvut.copakond.pinkfluffyunicorn.model.items.Item;

public enum ItemEnum {
    FIRE(FireItem.class),
    RAINBOW(RainbowItem.class);

    private Class<? extends Item> itemClass;

    ItemEnum(Class<? extends Item> itemClass) {
        this.itemClass = itemClass;
    }

    // Return the class of the item based on its ordinal value
    public static Class<? extends Item> getItemClassByOrdinal(int ordinal) {
        if (ordinal >= 0 && ordinal < values().length) {
            return values()[ordinal].itemClass; // Return the class associated with this enum
        }
        return null; // Or throw an exception if you want to handle invalid ordinals
    }

    public static int getNumberOfItems() {
        return ItemEnum.values().length;
    }
}
