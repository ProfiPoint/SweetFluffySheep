package cz.cvut.copakond.pinkfluffyunicorn.model.items;

import cz.cvut.copakond.pinkfluffyunicorn.model.utils.enums.ItemEnum;
import cz.cvut.copakond.pinkfluffyunicorn.model.utils.enums.RenderPriorityEnums;

public class Coin extends Item {
    private static int totalCoins = 0;
    protected static int coinsLeft = 0;

    public Coin(double[] position, int duration) {
        super("coin", position, duration, ItemEnum.COIN);
        coinsLeft++;
        totalCoins++;
    }

    public void collect() {
        coinsLeft--;
    }

    public static void resetCoins() {
        coinsLeft = 0;
        totalCoins = 0;
    }

    public static int getCoinsLeft() {
        return coinsLeft;
    }

    public static int getTotalCoins() {
        return totalCoins;
    }

    @Override
    public boolean use() {
        coinsLeft--;
        return super.use();
    }

    // this method is here to make it easier to generalize the code to reset all items, but coins are non resetable
    // items as they don't have any effect
    public static void reset() {
        return;
    }
}
