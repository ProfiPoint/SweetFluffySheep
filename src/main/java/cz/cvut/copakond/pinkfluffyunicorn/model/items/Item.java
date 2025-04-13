package cz.cvut.copakond.pinkfluffyunicorn.model.items;

import cz.cvut.copakond.pinkfluffyunicorn.model.utils.enums.ItemEnum;
import cz.cvut.copakond.pinkfluffyunicorn.model.utils.GameObject;
import cz.cvut.copakond.pinkfluffyunicorn.model.utils.enums.RenderPriorityEnums;

public class Item extends GameObject implements IItem {
    protected int duration_ticks;
    private ItemEnum itemEffect;
    private boolean pickable;

    public Item(String textureName, double[] position, int duration, ItemEnum itemEffect) {
        super(textureName, position, RenderPriorityEnums.ITEM.getValue());
        this.duration_ticks = duration * 60;
        this.itemEffect = itemEffect;
        this.pickable = true;
    }

    @Override
    public void tick(boolean doesTimeFlow) {
        super.tick(doesTimeFlow);
    }

    @Override
    public boolean use() {
        if (pickable){
            pickable = false;
            super.visible = false;
            return true;
        }
        return false;
    }

    @Override
    public ItemEnum getItemEffect() {
        return itemEffect;
    }
}
