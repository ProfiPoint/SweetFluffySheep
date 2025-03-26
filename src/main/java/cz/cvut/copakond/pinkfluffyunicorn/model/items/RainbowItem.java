package cz.cvut.copakond.pinkfluffyunicorn.model.items;

import cz.cvut.copakond.pinkfluffyunicorn.model.utils.enums.ItemEnum;

public class RainbowItem extends Item {
    private static RainbowItem active = null; // only one fire item can be active at a time

    public RainbowItem(int[] position, int duration_ticks, ItemEnum itemEffect, boolean pickable) {
        super("rainbow", position, duration_ticks, itemEffect, pickable);
    }
}
