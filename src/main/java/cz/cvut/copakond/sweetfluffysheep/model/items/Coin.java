package cz.cvut.copakond.sweetfluffysheep.model.items;

import cz.cvut.copakond.sweetfluffysheep.model.utils.enums.ItemEnum;
import cz.cvut.copakond.sweetfluffysheep.model.utils.enums.SoundListEnum;
import cz.cvut.copakond.sweetfluffysheep.model.utils.files.SoundManager;

/**
 * Represents a coin item in the game.
 * The Coin class extends the Item class and provides functionality for collecting coins.
 * It keeps track of the total number of coins and the number of coins left in the level.
 */
public class Coin extends Item {
    private static int totalCoins = 0;
    protected static int coinsLeft = 0; // number of coins left in the level

    /**
     * Constructor for the Coin class.
     * Initializes the coin with a given position and duration.
     *
     * @param position The position of the coin in the game world.
     * @param duration The duration of the coin's existence.
     */
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

    /**
     * Collects the coin, reducing the number of coins left and playing a sound effect.
     */
    private void collect() {
        coinsLeft--;
        SoundManager.playSound(SoundListEnum.MONEY);
    }
}
