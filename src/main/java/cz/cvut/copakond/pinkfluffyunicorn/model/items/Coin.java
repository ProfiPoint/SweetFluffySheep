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

    @Override
    public void resetLevel() {
        super.resetLevel();
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
        collect();
        return super.use();
    }

    private void collect() {
        coinsLeft--;
    }
}
