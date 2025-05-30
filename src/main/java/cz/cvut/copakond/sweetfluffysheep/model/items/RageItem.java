package cz.cvut.copakond.sweetfluffysheep.model.items;

import cz.cvut.copakond.sweetfluffysheep.model.utils.enums.ItemEnum;
import cz.cvut.copakond.sweetfluffysheep.model.utils.enums.SoundListEnum;
import cz.cvut.copakond.sweetfluffysheep.model.utils.files.SoundManager;

import java.util.logging.Logger;

/**
 * Represents a freeze item that can be used to freeze enemies.
 * Only one freeze item can be active at a time.
 */
public class RageItem extends Item {
    private static final Logger logger = Logger.getLogger(RageItem.class.getName());
    
    private static RageItem active = null; // only one freeze item can be active at a time

    /**
     * Constructor for the RageItem class.
     *
     * @param position The position of the item in the game world.
     * @param duration The duration of the item effect.
     */
    public RageItem(double[] position, int duration) {
        super("rage", position, duration, ItemEnum.RAGE);
    }

    public static boolean isActive() {
        return active != null;
    }

    @Override
    public boolean use() {
        if (active != null) {
            return false; // freeze item is already active
        }
        active = this;
        SoundManager.playSound(SoundListEnum.IMMORTAL);
        return super.use();
    }

    @Override
    public void tick(boolean doesTimeFlow) {
        super.tick(doesTimeFlow);
        if (durationTicks > 0 && active == this && doesTimeFlow) {
            durationTicks--;
            if (durationTicks == 0) {
                active = null;
                logger.info("Rage item expired");
                SoundManager.stopSfx(SoundListEnum.IMMORTAL);
            }
        }
    }

    @Override
    public void resetLevel() {
        super.resetLevel();
        active = null;
    }
}
