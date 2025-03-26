package cz.cvut.copakond.pinkfluffyunicorn.model.items;

import cz.cvut.copakond.pinkfluffyunicorn.model.utils.enums.ItemEnum;
import cz.cvut.copakond.pinkfluffyunicorn.model.utils.GameObject;
import cz.cvut.copakond.pinkfluffyunicorn.model.utils.enums.RenderPriorityEnums;

public class Item extends GameObject implements IItem {
    int duration_ticks;
    ItemEnum itemEffect;
    boolean pickable;

    public Item(String textureName, int[] position, int duration_ticks, ItemEnum itemEffect, boolean pickable) {
        super(textureName, position, RenderPriorityEnums.ITEM.getValue());
        this.duration_ticks = duration_ticks;
        this.itemEffect = itemEffect;
        this.pickable = pickable;
    }

    @Override
    public void tick() {
        duration_ticks--;
    }

    public boolean use(){
        return pickable;
    }
    public ItemEnum getItemEffect(){
        return itemEffect;
    }
}
