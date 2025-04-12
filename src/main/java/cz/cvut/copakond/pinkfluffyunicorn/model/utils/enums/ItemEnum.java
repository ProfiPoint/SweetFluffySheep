package cz.cvut.copakond.pinkfluffyunicorn.model.utils.enums;

import cz.cvut.copakond.pinkfluffyunicorn.model.items.FireItem;
import cz.cvut.copakond.pinkfluffyunicorn.model.items.ItemFactory;
import cz.cvut.copakond.pinkfluffyunicorn.model.items.RainbowItem;
import cz.cvut.copakond.pinkfluffyunicorn.model.items.Item;

public enum ItemEnum {
    COIN(), // Coin, not a special item
    FIRE(),
    RAINBOW();

    public static int getNumberOfItems() {
        return ItemEnum.values().length;
    }
}
