package cz.cvut.copakond.pinkfluffyunicorn.model.items;

import cz.cvut.copakond.pinkfluffyunicorn.model.utils.enums.ItemEnum;

public class FireItem extends Item {
    private static FireItem active = null; // only one fire item can be active at a time

    public FireItem(int[] position, int duration) {
        super("fire", position, duration, ItemEnum.FIRE);
    }
}
