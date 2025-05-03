package cz.cvut.copakond.sweetfluffysheep.model.items;

import cz.cvut.copakond.sweetfluffysheep.model.utils.enums.ItemEnum;
import cz.cvut.copakond.sweetfluffysheep.model.utils.enums.SoundListEnum;
import cz.cvut.copakond.sweetfluffysheep.model.utils.files.SoundManager;

import java.util.logging.Logger;

/**
 * Represents a freeze item that can be used to freeze enemies.
 * Only one freeze item can be active at a time.
 */
public class FreezeItem extends Item {
    private static final Logger logger = Logger.getLogger(FreezeItem.class.getName());

    private static FreezeItem active = null; // only one freeze item can be active at a time

    /**
     * Constructor for FreezeItem.
     *
     * @param position the position of the item
     * @param duration the duration of the freeze effect in ticks
     */
    public FreezeItem(double[] position, int duration) {
        super("freeze", position, duration, ItemEnum.FREEZE);
    }

    /**
     * Checks if a freeze item is currently active.
     *
     * @return true if a freeze item is active, false otherwise
     */
    public static boolean isActive() {
        return active != null;
    }

    @Override
    public boolean use() {
        if (active != null) {
            return false; // freeze item is already active
        }
        active = this;
        SoundManager.playSound(SoundListEnum.HOLD);
        return super.use();
    }

    @Override
    public void tick(boolean doesTimeFlow) {
        super.tick(doesTimeFlow);
        if (durationTicks > 0 && active == this && doesTimeFlow) {
            durationTicks--;
            if (durationTicks == 0) {
                active = null;
                logger.info("Freeze item expired");
                SoundManager.stopSfx(SoundListEnum.HOLD);
            }
        }
    }

    @Override
    public void resetLevel() {
        super.resetLevel();
        active = null;
    }
}
