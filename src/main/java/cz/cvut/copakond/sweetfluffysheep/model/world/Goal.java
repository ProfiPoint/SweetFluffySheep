package cz.cvut.copakond.sweetfluffysheep.model.world;

import cz.cvut.copakond.sweetfluffysheep.model.items.Coin;
import cz.cvut.copakond.sweetfluffysheep.model.utils.enums.SoundListEnum;
import cz.cvut.copakond.sweetfluffysheep.model.utils.files.SoundManager;
import cz.cvut.copakond.sweetfluffysheep.model.utils.game.GameObject;
import cz.cvut.copakond.sweetfluffysheep.model.utils.enums.DirectionEnum;
import cz.cvut.copakond.sweetfluffysheep.model.utils.enums.RenderPriorityEnums;
import javafx.scene.image.Image;

import java.util.logging.Logger;

public class Goal extends GameObject {
    private static final Logger logger = Logger.getLogger(Goal.class.getName());
    
    // 10x per second it will update the anim texture
    private static final int textureChangeFrameCoefficient = (int) Math.ceil((double) GameObject.getFPS() / 10);
    private boolean locked = true;
    private boolean lockedTexture = true;
    private DirectionEnum direction;

    public Goal(double[] position, DirectionEnum orientation) {
        super("goal", position, RenderPriorityEnums.ARROW.getValue());
        this.direction = orientation;
    }

    public DirectionEnum getDirection() {
        return this.direction;
    }

    public boolean isLocked() {
        return locked;
    }

    public void rotateCharacterLE(){
        this.direction = this.direction.next();
    }

    // to show the unlocked goal texture in level editor
    public void unlockForLevelEditor() {
        this.lockedTexture = false;
    }

    @Override
    public void tick(boolean doesTimeFlow) {
        super.tick(doesTimeFlow);
        if (this.locked && Coin.getCoinsLeft() <= 0) {
            this.locked = false;
            this.lockedTexture = false;
            SoundManager.playSound(SoundListEnum.GOAL_UNLOCKED);
            logger.info("Goal unlocked!");
        }
    }

    @Override
    public Image getTexture() {
        int orientation = (this.direction.getValue() / 90);
        this.textureIdNow = (32 * ((orientation+2) % 4)) + (int)((Level.getCurrentCalculatedFrame()/textureChangeFrameCoefficient) % 32);
        if (this.locked && this.lockedTexture) {
            this.textureIdNow += 32*4;

        }
        return this.textures.get(this.textureIdNow);
    }
}
