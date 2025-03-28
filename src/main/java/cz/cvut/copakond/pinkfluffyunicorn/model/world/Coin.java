package cz.cvut.copakond.pinkfluffyunicorn.model.world;

import cz.cvut.copakond.pinkfluffyunicorn.model.utils.GameObject;
import cz.cvut.copakond.pinkfluffyunicorn.model.utils.enums.RenderPriorityEnums;

public class Coin extends GameObject {
    static int coinsLeft = 0;

    public Coin(int[] position) {
        super("coin", position, RenderPriorityEnums.ITEM.getValue());
    }

    public void collect() {
        coinsLeft--;
    }

    public static void setCoinsLeft(int coins) {
        coinsLeft = coins;
    }

    public static int getCoinsLeft() {
        return coinsLeft;
    }
}
