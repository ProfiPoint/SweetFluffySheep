package cz.cvut.copakond.pinkfluffyunicorn.model.items;

import cz.cvut.copakond.pinkfluffyunicorn.model.utils.enums.ItemEnum;
import cz.cvut.copakond.pinkfluffyunicorn.model.utils.enums.SoundListEnum;
import cz.cvut.copakond.pinkfluffyunicorn.model.utils.files.SoundManager;

import java.util.logging.Logger;

public class FireItem extends Item {
    private static final Logger logger = Logger.getLogger(FireItem.class.getName());

    private static FireItem active = null; // only one fire item can be active at a time

    public FireItem(double[] position, int duration) {
        super("fire", position, duration, ItemEnum.FIRE);
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
        SoundManager.playSound(SoundListEnum.HOLD);
        return super.use();
    }

    @Override
    public void tick(boolean doesTimeFlow) {
        super.tick(doesTimeFlow);
        if (duration_ticks > 0 && active == this && doesTimeFlow) {
            duration_ticks--;
            if (duration_ticks == 0) {
                active = null;
                logger.info("Fire item expired");
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
