package cz.cvut.copakond.sweetfluffysheep.model.items;

import cz.cvut.copakond.sweetfluffysheep.model.utils.enums.ItemEnum;
import cz.cvut.copakond.sweetfluffysheep.model.utils.enums.SoundListEnum;
import cz.cvut.copakond.sweetfluffysheep.model.utils.files.SoundManager;

import java.util.logging.Logger;

public class RainbowItem extends Item {
    private static final Logger logger = Logger.getLogger(RainbowItem.class.getName());
    
    private static RainbowItem active = null; // only one fire item can be active at a time

    public RainbowItem(double[] position, int duration) {
        super("rainbow", position, duration, ItemEnum.RAINBOW);
    }

    public static boolean isActive() {
        return active != null;
    }

    @Override
    public boolean use() {
        if (active != null) {
            return false; // fire item is already active
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
                logger.info("Rainbow item expired");
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
