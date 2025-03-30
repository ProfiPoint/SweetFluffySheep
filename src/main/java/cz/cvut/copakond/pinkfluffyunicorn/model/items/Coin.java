package cz.cvut.copakond.pinkfluffyunicorn.model.items;

import cz.cvut.copakond.pinkfluffyunicorn.model.utils.enums.ItemEnum;
import cz.cvut.copakond.pinkfluffyunicorn.model.utils.enums.RenderPriorityEnums;

public class Coin extends Item {
    static int coinsLeft = 0;

    public Coin(double[] position, int duration) {
        super("coin", position, duration, ItemEnum.COIN);
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
